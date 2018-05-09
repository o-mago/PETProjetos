package magosoftware.petprojetos;

public class Presenca {
    private String codigo;
    private String nome;
    public String situacao;

    public Presenca (String nome, String situacao) {
        this.nome = nome;
        this.situacao = situacao;
    }

    public Presenca (String codigo, String nome, String situacao) {
        this.codigo = codigo;
        this.nome = nome;
        this.situacao = situacao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getSituacao() {
        return situacao;
    }
}
