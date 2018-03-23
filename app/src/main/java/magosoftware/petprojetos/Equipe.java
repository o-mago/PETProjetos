package magosoftware.petprojetos;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by root on 14/03/18.
 */

public class Equipe {
    String nome;
//    String coordenador;
//    ArrayList<String> petianos;
    int corEquipe;
//    String situacao;

    public Equipe (String nome, int corEquipe) {
        this.nome = nome;
//        this.coordenador = coordenador;
//        this.petianos = petianos;
        this.corEquipe = corEquipe;
    }

    public String getNome() { return nome; }

//    public String getCoordenador() { return coordenador; }

//    public ArrayList<String> getPetianos() { return petianos; }

    public int getCorEquipe() { return corEquipe; }

//    public String getSituacao() { return situacao; }
}
