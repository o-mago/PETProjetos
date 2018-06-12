package magosoftware.petprojetos;

public class Notificacao {

    public String tipo;
    public String titulo;
    public String data;
    public String node;
    public String mensagem;

    public Notificacao(String tipo, String titulo, String data, String node, String mensagem) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.data = data;
        this.node = node;
        this.mensagem = mensagem;
    }

    public String getData() {
        return data;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getNode() {
        return node;
    }

    public String getMensagem() {
        return mensagem;
    }
}
