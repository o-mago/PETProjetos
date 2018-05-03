package magosoftware.petprojetos;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by root on 09/03/18.
 */

public class Projeto {

    String nome;
    String coordenador;
    ArrayList<String> petianos;
    Drawable imagemProjeto;
    String situacao;
    String node;

    public Projeto (String nome, String coordenador, ArrayList<String> petianos, Drawable imagemProjeto) {
        this.nome = nome;
        this.coordenador = coordenador;
        this.petianos = petianos;
        this.imagemProjeto = imagemProjeto;
    }

    public Projeto (String nome, Drawable imagemProjeto, String situacao, String node) {
        this.nome = nome;
        this.imagemProjeto = imagemProjeto;
        this.situacao = situacao;
        this.node = node;
    }

    public String getNome() { return nome; }

    public String getCoordenador() { return coordenador; }

    public ArrayList<String> getPetianos() { return petianos; }

    public Drawable getImagemProjeto() { return imagemProjeto; }

    public String getSituacao() { return situacao; }

    public String getNode() {
        return node;
    }
}
