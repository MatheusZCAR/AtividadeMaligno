public class Pedido extends Comunicado {
    private byte[] numeros;
    private int procurado;

    public Pedido(byte[] subVetor, int procurado) {
        this.numeros = subVetor;
        this.procurado = procurado;
    }

    public byte[] getNumeros() {
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
