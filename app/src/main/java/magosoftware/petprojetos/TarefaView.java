package magosoftware.petprojetos;

public class TarefaView {

    private int layoutId;
    private int recyclerViewId;
    private int botaoId;

    public TarefaView( int layoutId, int recyclerViewId, int botaoId) {
        this.layoutId = layoutId;
        this.recyclerViewId = recyclerViewId;
        this.botaoId = botaoId;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public int getRecyclerViewId() {
        return recyclerViewId;
    }

    public int getBotaoId() {
        return botaoId;
    }
}
