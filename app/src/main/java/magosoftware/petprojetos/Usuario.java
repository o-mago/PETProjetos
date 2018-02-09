package magosoftware.petprojetos;

/**
 * Created by root on 09/02/18.
 */

public class Usuario {

    public String nome;
    public String nick;
    public String email;
    public String universidade;
    public String curso;
    public String nascimento;

    public Usuario(String nome, String apelido, String email, String universidade, String curso, String nascimento) {
        this.nome = nome;
        this.nick = apelido;
        this.email = email;
        this.universidade = universidade;
        this.curso = curso;
        this.nascimento = nascimento;
    }

    public String getNome() {
        return nome;
    }

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

    public String getnick() {
        return nick;
    }
}
