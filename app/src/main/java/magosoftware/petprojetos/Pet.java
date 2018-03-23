package magosoftware.petprojetos;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by root on 27/02/18.
 */

public class Pet {

    private String nome;
    private String site;
    private String email;
    private String cidade;
    private String estado;
    private String universidade;
    private String curso;
    private String ano;
    private String nPetianos;
    private Drawable logo;

    public Pet(String nome, String nPetianos, String email, String universidade,
               String ano, String site, String cidade, String estado) {
        this.nome = nome;
        this.nPetianos = nPetianos;
        this.email = email;
        this.universidade = universidade;
        this.ano = ano;
        this.site = site;
        this.cidade = cidade;
        this.estado = estado;
    }

    public Pet(String nome, Drawable logo) {
        this.nome = nome;
        this.logo = logo;
    }

    public String getNome() {
        return nome;
    }

    public String getNPetianos() { return nPetianos; }

    public String getEmail() {
        return email;
    }

    public String getUniversidade() { return universidade; }

    public String getCurso() {
        return curso;
    }

    public String getAno() {
        return ano;
    }

    public String getSite() { return site; }

    public String getCidade() { return cidade; }

    public String getEstado() { return estado; }

    public Drawable getLogo() { return logo; }
}