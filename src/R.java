import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class R {
    private static final int PORTA = 12345;
    private static final int NUM_PROCESSADORES = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        System.out.println("[R] Servidor Receptor iniciado.");
        System.out.println("[R] Porta: " + PORTA);
        System.out.println("[R] Processadores disponíveis: " + NUM_PROCESSADORES);
        
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("[R] Aguardando conexões...");
            
            while (true) {
                try {
                    Socket conexao = serverSocket.accept();
                    String clienteIP = conexao.getInetAddress().getHostAddress();
                    System.out.println("[R] Conexão aceita do cliente: " + clienteIP);
                    
                    atenderCliente(conexao, clienteIP);
                    
                } catch (IOException e) {
                    System.err.println("[R] Erro ao aceitar conexão: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[R] Erro ao criar ServerSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void atenderCliente(Socket conexao, String clienteIP) {
        try (ObjectOutputStream transmissor = new ObjectOutputStream(conexao.getOutputStream());
             ObjectInputStream receptor = new ObjectInputStream(conexao.getInputStream())) {
            
            transmissor.flush();
            
            while (true) {
                try {
                    Object comunicado = receptor.readObject();
                    
                    if (comunicado instanceof ComunicadoEncerramento) {
                        System.out.println("[R] ComunicadoEncerramento recebido de " + clienteIP);
                        System.out.println("[R] Encerrando conexão com " + clienteIP);
                        break;
                        
                    } else if (comunicado instanceof Pedido) {
                        Pedido pedido = (Pedido) comunicado;
                        System.out.println("[R] Pedido recebido do cliente " + clienteIP);
                        System.out.println("[R] Tamanho do vetor: " + pedido.getNumeros().length);
                        System.out.println("[R] Número procurado: " + pedido.getProcurado());
                        
                        long inicio = System.currentTimeMillis();
                        int contagem = contarParalelo(pedido);
                        long fim = System.currentTimeMillis();
                        
                        System.out.println("[R] Contagem realizada: " + contagem);
                        System.out.println("[R] Tempo de processamento: " + (fim - inicio) + "ms");
                        
                        Resposta resposta = new Resposta(contagem);
                        transmissor.writeObject(resposta);
                        transmissor.flush();
                        System.out.println("[R] Resposta enviada para " + clienteIP);
                        
                    } else {
                        System.err.println("[R] Comunicado desconhecido recebido: " + 
                                         comunicado.getClass().getName());
                    }
                    
                } catch (EOFException e) {
                    System.out.println("[R] Conexão encerrada pelo cliente " + clienteIP);
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("[R] Classe não encontrada: " + e.getMessage());
                    break;
                }
            }
            
        } catch (IOException e) {
            System.err.println("[R] Erro na comunicação com cliente " + clienteIP + ": " + 
                             e.getMessage());
        } finally {
            try {
                conexao.close();
                System.out.println("[R] Conexão fechada com " + clienteIP);
            } catch (IOException e) {
                System.err.println("[R] Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    private static int contarParalelo(Pedido pedido) {
        int[] numeros = pedido.getNumeros();
        int procurado = pedido.getProcurado();
        int tamanho = numeros.length;
        
        // Determina o número de threads a usar
        int numThreads = Math.min(NUM_PROCESSADORES, tamanho);
        
        // Se o vetor for muito pequeno, executa sequencialmente
        if (tamanho < 1000 || numThreads == 1) {
            return pedido.contar();
        }
        
        System.out.println("[R] Processando com " + numThreads + " threads");
        
        // Cria um pool de threads
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        AtomicInteger contagemTotal = new AtomicInteger(0);
        
        // Divide o trabalho entre as threads
        int tamanhoBloco = tamanho / numThreads;
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int inicio = i * tamanhoBloco;
            final int fim = (i == numThreads - 1) ? tamanho : (i + 1) * tamanhoBloco;
            
            executor.submit(() -> {
                try {
                    int contagemLocal = 0;
                    for (int j = inicio; j < fim; j++) {
                        if (numeros[j] == procurado) {
                            contagemLocal++;
                        }
                    }
                    contagemTotal.addAndGet(contagemLocal);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            // Aguarda todas as threads terminarem
            latch.await();
        } catch (InterruptedException e) {
            System.err.println("[R] Erro ao aguardar threads: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
        
        return contagemTotal.get();
    }
}
