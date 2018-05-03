package magosoftware.petprojetos;

import android.graphics.drawable.Drawable;

/**
 * Created by root on 09/02/18.
 */

public class Usuario {

    public String nome;
    public String pet;
    public String nick;
    public String email;
    public String universidade;
    public String curso;
    public String nascimento;
    public Drawable foto;
    public String codigo;
    public String situacao;

    public Usuario(String nome, String apelido, String email, String universidade, String curso, String nascimento) {
        this.nome = nome;
        this.nick = apelido;
        this.email = email;
        this.universidade = universidade;
        this.curso = curso;
        this.nascimento = nascimento;
    }

    public Usuario(String nome, Drawable foto, String codigo) {
        this.nome = nome;
        this.foto = foto;
        this.codigo = codigo;
    }

    public Usuario(String nome, Drawable foto, String situacao, String codigo) {
        this.nome = nome;
        this.foto = foto;
        this.situacao = situacao;
        this.codigo = codigo;
    }

    public Usuario(String nome, Drawable foto) {
        this.nome = nome;
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }

    public String getNick() { return nick; }

    public String getEmail() {
        return email;
    }

    public String getUniversidade() {
        return universidade;
    }

    public String getCurso() {
        return curso;
    }

    public String getNascimento() {
        return nascimento;
    }

    public String getPet() { return pet; }

    public Drawable getFoto() { return foto; }

    public String getCodigo() {
        return codigo;
    }

    public String getSituacao() {
        return situacao;
    }
}
