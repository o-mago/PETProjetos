package magosoftware.petprojetos;

import android.graphics.drawable.Drawable;

public class ItemMenu {
    private String opcao;
    private Drawable icone;

    public ItemMenu(String opcao, Drawable icone) {
        this.opcao = opcao;
        this.icone = icone;
    }

    public String getOpcao() {
        return opcao;
    }

    public Drawable getIcone() {
        return icone;
    }
}
