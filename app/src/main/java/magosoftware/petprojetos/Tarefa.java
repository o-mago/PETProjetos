package magosoftware.petprojetos;

import java.util.ArrayList;

/**
 * Created by root on 16/03/18.
 */

public class Tarefa {

    private String titulo;
    private String descricao;
    private String prazo;
    private ArrayList<String> petianos;
    private boolean concluido;
    private String situacaoPrazo;
    private String node;

    public Tarefa(String titulo, String descricao, String prazo, ArrayList<String> petianos, boolean concluido) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.prazo = prazo;
        this.petianos = petianos;
        this.concluido = concluido;
    }

    public Tarefa(String titulo, String descricao, boolean concluido, String situacaoPrazo, String node) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.concluido = concluido;
        this.situacaoPrazo = situacaoPrazo;
        this.node = node;
    }

    public String getTitulo() { return titulo; }

    public String getDescricao() { return descricao; }

    public String getPrazo() { return  prazo; }

    public ArrayList<String> getPetianos() { return petianos; }

    public Boolean getConcluido() { return concluido; }

    public String getSituacaoPrazo() { return situacaoPrazo; }

    public String getNode() {
        return node;
    }
}
