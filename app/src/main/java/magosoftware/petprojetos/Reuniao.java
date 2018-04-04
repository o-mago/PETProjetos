package magosoftware.petprojetos;

/**
 * Created by root on 28/03/18.
 */

public class Reuniao {
    private String tituloReuniao;
    private String dataReuniao;

    public Reuniao (String tituloReuniao, String dataReuniao) {
        this.tituloReuniao = tituloReuniao;
        this.dataReuniao = dataReuniao;
    }

    public String getTituloReuniao() { return tituloReuniao; }

    public String getDataReuniao() { return dataReuniao; }
}
