package magosoftware.petprojetos;

import java.io.Serializable;

public class Horario implements Serializable{
    private String hora;
    public boolean disponivel;
    private String quantidade;

    public Horario(String hora, boolean disponivel) {
        this.hora = hora;
        this.disponivel = disponivel;
    }

    public Horario(String hora, boolean disponivel, String quantidade) {
        this.hora = hora;
        this.disponivel = disponivel;
        this.quantidade = quantidade;
    }

    public String getHora() {
        return hora;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public String getQuantidade() {
        return quantidade;
    }
}
