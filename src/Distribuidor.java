import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Distribuidor {
    
    // Define a constante do limite máximo
    private static final int LIMITE_MAXIMO = 1_400_000_000;
    
    // Porta que os receptores (R) estão escutando
    private static final int PORTA_SERVIDORES = 12345;
    
    // IPs dos servidores R
    // ATENÇÃO: Para teste local, use "127.0.0.1" repetido ou IPs reais se rodar em máquinas diferentes.
    private static final String[] IPS_SERVIDORES = {
        "127.0.0.1", 
        "127.0.0.1", // Repetido para teste de paralelismo interno em um só R local
        "127.0.0.1"  // (Ajuste para IPs reais para o teste de distribuição real!)
    };
    
    // Classe interna para guardar os resultados parciais
    private static class ResultadoParcial {
        public int contagem = 0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long inicioTotal = System.currentTimeMillis();

        try {
            //Parte 1 - Input do Usuário e Validação
            
            System.out.println(".-.-. Programa Distribuidor .-.-.");
            System.out.println("ATENÇÃO: O limite máximo para o vetor é de " + String.format("%,d", LIMITE_MAXIMO) + " elementos.");

            int tamanhoVetor = 0;
            while (tamanhoVetor <= 0 || tamanhoVetor > LIMITE_MAXIMO) {
                System.out.print("Quantos elementos você deseja no vetor?: ");
                
                if (scanner.hasNextInt()) {
                    tamanhoVetor = scanner.nextInt();
                    
                    if (tamanhoVetor <= 0) {
                        System.out.println("O tamanho deve ser um número positivo maior que zero. Tente novamente.");
                    } else if (tamanhoVetor > LIMITE_MAXIMO) {
                        System.out.printf("O tamanho máximo permitido é de %,d elementos. Tente um valor menor.%n", LIMITE_MAXIMO);
                    }
                } else {
                    System.out.println("Input inválido. Por favor, digite um número inteiro.");
                    scanner.next(); // Limpa o buffer
                }
            }
            
            System.out.print("Deseja ver o vetor grande na tela? (s/n): ");
            boolean mostrarVetor = scanner.next().trim().equalsIgnoreCase("s");
            
            System.out.print("Deseja testar um número que não existe (111)? (s/n): ");
            boolean testarNaoExistente = scanner.next().trim().equalsIgnoreCase("s");
            
            // Parte 2 - Geração de Dados
            
            byte[] vetorGrande = gerarVetorAleatorio(tamanhoVetor, (byte)-100, (byte)100);
            
            if (mostrarVetor) {
                System.out.println("\nVetor gerado (início): " + Arrays.toString(Arrays.copyOfRange(vetorGrande, 0, Math.min(100, tamanhoVetor))));
            }

            byte numeroProcurado = (byte) vetorGrande[(int)(Math.random() * vetorGrande.length)];
            
            if (testarNaoExistente) {
                numeroProcurado = 111; 
                System.out.println("\n[D] MODO DE TESTE: Procurando o número 111 (deve dar 0).");
            }

            System.out.println("\n[D] O número escolhido para procurar é: " + numeroProcurado);
            System.out.println("[D] O vetor será dividido entre " + IPS_SERVIDORES.length + " servidores R.");

            final byte numeroProcuradoThread = numeroProcurado;

            // Parte 3 - Divisão de Trabalho, Criação de Threads e Execução
            
            List<Thread> threads = new ArrayList<>();
            List<ResultadoParcial> resultados = new ArrayList<>();

            int numServidores = IPS_SERVIDORES.length;
            int tamanhoParte = tamanhoVetor / numServidores;
            
            int inicio = 0;
            for (int i = 0; i < numServidores; i++) {
                int fim = (i == numServidores - 1) ? tamanhoVetor : inicio + tamanhoParte;
                
                // Cria a parte do vetor
                byte[] subVetor = Arrays.copyOfRange(vetorGrande, inicio, fim);
                String ipAtual = IPS_SERVIDORES[i];
                ResultadoParcial resultado = new ResultadoParcial();
                
                resultados.add(resultado);
                
                // Cria uma Thread anônima para cada servidor
                Thread thread = new Thread(() -> {
                    Socket conexao = null;
                    ObjectOutputStream transmissor = null;
                    ObjectInputStream receptor = null;

                    try {
                        System.out.println("[D] Thread para " + ipAtual + " iniciando conexão...");
                        conexao = new Socket(ipAtual, PORTA_SERVIDORES);
                        
                        // OOS antes do OIS para evitar deadlock
                        transmissor = new ObjectOutputStream(conexao.getOutputStream());
                        receptor = new ObjectInputStream(conexao.getInputStream());

                        // Envia Pedido
                        Pedido pedido = new Pedido(subVetor, numeroProcuradoThread);
                        transmissor.writeObject(pedido);
                        System.out.println("[D] Enviando Pedido para " + ipAtual + "...");

                        // Recebe Resposta
                        Comunicado respostaRecebida = (Comunicado) receptor.readObject();

                        if (respostaRecebida instanceof Resposta) {
                            Resposta resposta = (Resposta) respostaRecebida;
                            resultado.contagem = resposta.getContagem();
                            System.out.println("[D] Resposta recebida de " + ipAtual + ": " + resultado.contagem);
                        } else {
                            System.err.println("[D] Recebido comunicado inesperado de " + ipAtual);
                        }

                    } catch (Exception e) {
                        System.err.println("[D] ERRO de comunicação com " + ipAtual + ": " + e.getMessage());
                    } finally {
                        // Não fechamos a conexão aqui, ela fecha após o encerramento do ciclo.
                    }
                });
                
                threads.add(thread);
                thread.start(); // Inicia a thread
                
                inicio = fim;
            }

            // Sincronização e Compilação

            int contagemFinal = 0;
            for (Thread thread : threads) {
                thread.join(); // Espera cada thread terminar
            }

            // Soma os resultados parciais
            for (ResultadoParcial resultado : resultados) {
                contagemFinal += resultado.contagem;
            }

            // Medição e Exibição
            
            long fimTotal = System.currentTimeMillis();
            
            System.out.println("\n.-.-. RESULTADO FINAL .-.-.");
            System.out.printf("[D] Contagem Paralela e Distribuída Final: %d ocorrências do número %d%n", contagemFinal, numeroProcurado);
            System.out.printf("[D] Tempo total de execução: %.3f segundos%n", (fimTotal - inicioTotal) / 1000.0);
            
        } catch (Exception e) {
            System.err.println("[D] ERRO GERAL NO DISTRIBUIDOR: " + e.getMessage());
        } finally {
            // Encerramento
            System.out.println("\n[D] Iniciando o envio de ComunicadoEncerramento para todos os servidores...");
             for(String ip : IPS_SERVIDORES) {
                 enviarEncerramento(ip);
             }
             scanner.close();
        }
    }
    

    private static void enviarEncerramento(String ip) {
         Socket conexao = null;
         ObjectOutputStream transmissor = null;
         try {
             System.out.println("[D] Enviando ComunicadoEncerramento para " + ip + "...");
             conexao = new Socket(ip, PORTA_SERVIDORES); 
             transmissor = new ObjectOutputStream(conexao.getOutputStream());
             
             transmissor.writeObject(new ComunicadoEncerramento());
             System.out.println("[D] Encerramento enviado com sucesso para " + ip);
             
         } catch (Exception e) {
             System.err.println("[D] Falha ao enviar encerramento para " + ip + ": " + e.getMessage());
         } finally {
             // Fecha os recursos
             try {
                 if (transmissor != null) transmissor.close();
                 if (conexao != null) conexao.close();
             } catch (IOException io) {}
         }
    }
    
    //Gera um vetor de bytes com números aleatórios no range.

    private static byte[] gerarVetorAleatorio(int tamanho, byte min, byte max) {
        byte[] vetor = new byte[tamanho];
        for (int i = 0; i < tamanho; i++) {
            int aleatorio = ((int)(Math.random() * (max - min + 1))) + min;
            vetor[i] = (byte) aleatorio;
        }
        return vetor;
    }
}