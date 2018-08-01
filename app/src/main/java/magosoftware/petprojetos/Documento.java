package magosoftware.petprojetos;

public class Documento {

    public String tipo;
    public String nome;
    public String path;

    public Documento(String tipo, String nome, String path) {
        this.tipo = tipo;
        this.nome = nome;
        this.path = path;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getPath() {
        return path;
    }
}
