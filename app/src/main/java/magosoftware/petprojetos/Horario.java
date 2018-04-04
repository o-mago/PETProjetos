package magosoftware.petprojetos;

public class Horario {
    private String hora;
    public boolean disponivel;

    public Horario(String hora, boolean disponivel) {
        this.hora = hora;
        this.disponivel = disponivel;
    }

    public String getHora() {
        return hora;
    }

    public boolean isDisponivel() {
        return disponivel;
    }
}
