public class Pedido extends Comunicado {
    private int[] numeros;
    private int procurado;

    public Pedido(int[] numeros, int procurado) {
        this.numeros = numeros;
        this.procurado = procurado;
    }

    public int[] getNumeros() {
        return numeros;
    }

    public int getProcurado() {
        return procurado;
    }

    public int contar() {
        int contagem = 0;
        for (int numero : numeros) {
            if (numero == procurado) {
                contagem++;
            }
        }
        return contagem;
    }
}
