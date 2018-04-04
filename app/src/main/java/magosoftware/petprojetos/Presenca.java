package magosoftware.petprojetos;

public class Presenca {
    private String nome;
    public String situacao;

    public Presenca (String nome, String situacao) {
        this.nome = nome;
        this.situacao = situacao;
    }

    public String getNome() {
        return nome;
    }

    public String getSituacao() {
        return situacao;
    }
}
