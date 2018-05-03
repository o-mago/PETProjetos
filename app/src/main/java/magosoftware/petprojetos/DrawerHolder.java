package magosoftware.petprojetos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DrawerHolder extends RecyclerView.ViewHolder {
    public TextView opcao;
    public ImageView icone;
    public LinearLayout menuClick;

    public DrawerHolder(View itemView) {
        super(itemView);
        opcao = itemView.findViewById(R.id.texto);
        icone = itemView.findViewById(R.id.icone);
        menuClick = itemView.findViewById(R.id.menu_item_click);
    }
}
