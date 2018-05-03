package magosoftware.petprojetos;

/**
 * Created by root on 28/03/18.
 */

public class Reuniao {
    private String tituloReuniao;
    private String dataReuniao;
    private String node;

    public Reuniao (String tituloReuniao, String dataReuniao, String node) {
        this.tituloReuniao = tituloReuniao;
        this.dataReuniao = dataReuniao;
        this.node = node;
    }

    public String getTituloReuniao() { return tituloReuniao; }

    public String getDataReuniao() { return dataReuniao; }

    public String getNode() {
        return node;
    }
}
