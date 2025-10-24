import java.util.Arrays;
import java.util.Scanner;

//faz a mesma coisa que o distribuidor, mas sem paralelismo para comparação

public class ContagemSequencial {
    
    private static final int LIMITE_MAXIMO = 1_400_000_000;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println(".-.-. Programa de Contagem SEQUENCIAL .-.-.");

            int tamanhoVetor = 0;
            while (tamanhoVetor <= 0 || tamanhoVetor > LIMITE_MAXIMO) {
                System.out.print("Quantos elementos você deseja no vetor? (Máx: " + String.format("%,d", LIMITE_MAXIMO) + "): ");
                if (scanner.hasNextInt()) {
                    tamanhoVetor = scanner.nextInt();
                    if (tamanhoVetor <= 0 || tamanhoVetor > LIMITE_MAXIMO) {
                        System.out.println("Valor fora do limite. Tente novamente.");
                    }
                } else {
                    System.out.println("Input inválido. Digite um número inteiro.");
                    scanner.next();
                }
            }
            
            // Geração e escolha do número
            byte[] vetorGrande = gerarVetorAleatorio(tamanhoVetor, (byte)-100, (byte)100);
            byte numeroProcurado = (byte) vetorGrande[(int)(Math.random() * vetorGrande.length)];
            
            System.out.println("[S] O número escolhido para procurar é: " + numeroProcurado);
            System.out.println("[S] Iniciando contagem sequencial em um único bloco...");


            long inicio = System.currentTimeMillis(); // log Inicio da contagem
            
            Pedido pedidoSequencial = new Pedido(vetorGrande, numeroProcurado);
            
            int contagemFinal = pedidoSequencial.contar();
            
            long fim = System.currentTimeMillis(); // log Fim da contagem

            
            System.out.println("\n--- RESULTADO SEQUENCIAL ---");
            System.out.printf("[S] Contagem Final: %d ocorrências do número %d%n", contagemFinal, numeroProcurado);
            System.out.printf("[S] Tempo total de execução (Sequencial): %.3f segundos%n", (fim - inicio) / 1000.0);
            
        } catch (Exception e) {
            System.err.println("[S] ERRO GERAL NO PROGRAMA SEQUENCIAL: " + e.getMessage());
        } finally {
             scanner.close();
        }
    }
    
    private static byte[] gerarVetorAleatorio(int tamanho, byte min, byte max) {
        byte[] vetor = new byte[tamanho];
        for (int i = 0; i < tamanho; i++) {
            int aleatorio = ((int)(Math.random() * (max - min + 1))) + min;
            vetor[i] = (byte) aleatorio;
        }
        return vetor;
    }
}