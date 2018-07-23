package magosoftware.petprojetos;

public class Evento {

    int dia;
    int mes;
    int ano;
    int diaSemana = 0;
    boolean tarefa = false;
    boolean reuniao = false;
    boolean aniversario = false;
    boolean evento = false;
    boolean vazio = false;
    boolean hoje = false;
    String caminhoTarefa = "";
    String caminhoReuniao = "";
    String caminhoAniversario = "";
    String caminhoEvento = "";

    public Evento (int dia, int mes, int ano,
                   boolean tarefa, boolean reuniao, boolean aniversario, boolean evento,
                   String caminhoTarefa, String caminhoReuniao, String caminhoAniversario, String caminhoEvento) {

        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
        this.tarefa = tarefa;
        this.reuniao = reuniao;
        this.aniversario = aniversario;
        this.evento = evento;
        this.caminhoTarefa = caminhoTarefa;
        this.caminhoReuniao = caminhoReuniao;
        this.caminhoAniversario = caminhoAniversario;
        this.caminhoEvento = caminhoEvento;
    }

    public Evento (int diaSemana,
                   boolean tarefa, boolean reuniao, boolean aniversario, boolean evento,
                   String caminhoTarefa, String caminhoReuniao, String caminhoAniversario, String caminhoEvento) {
        this.diaSemana = diaSemana;
        this.tarefa = tarefa;
        this.reuniao = reuniao;
        this.aniversario = aniversario;
        this.evento = evento;
        this.caminhoTarefa = caminhoTarefa;
        this.caminhoReuniao = caminhoReuniao;
        this.caminhoAniversario = caminhoAniversario;
        this.caminhoEvento = caminhoEvento;
    }

    public Evento (int dia, int mes, int ano, int diaSemana) {

        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
        this.diaSemana = diaSemana;
    }

    public Evento (boolean vazio) {

        this.vazio = vazio;
    }

    public Evento () {
    }

    public int getDia() {
        return dia;
    }

    public int getMes() {
        return mes;
    }

    public int getAno() {
        return ano;
    }

    public int getDiaSemana() {
        return diaSemana;
    }

    public boolean isTarefa() {
        return tarefa;
    }

    public boolean isReuniao() {
        return reuniao;
    }

    public boolean isAniversario() {
        return aniversario;
    }

    public boolean isEvento() {
        return evento;
    }

    public String getCaminhoTarefa() {
        return caminhoTarefa;
    }

    public String getCaminhoReuniao() {
        return caminhoReuniao;
    }

    public String getCaminhoAniversario() {
        return caminhoAniversario;
    }

    public String getCaminhoEvento() {
        return caminhoEvento;
    }

    public boolean isVazio() {
        return vazio;
    }
}
