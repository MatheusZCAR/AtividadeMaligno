import java.io.Serializable;

public class Resposta extends Comunicado {
    private Integer contagem;

    public Resposta(int contagem){
        this.contagem = contagem;
    }

    public Integer getContagem(){
        return this.contagem;
    }

    @Override
    public String toString(){
        return "Resposta [Contagem: " + contagem + "]";
    }
}
