public class Resposta extends Comunicado {
    private Integer contagem;

    public Resposta(int contagem) {
        this.contagem = contagem;
    }

    public Integer getContagem() {
        return contagem;
    }
}
