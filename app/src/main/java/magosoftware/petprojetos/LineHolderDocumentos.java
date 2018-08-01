package magosoftware.petprojetos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LineHolderDocumentos extends RecyclerView.ViewHolder{

    public CardView card;
    public TextView tipo;
    public TextView nome;

    public LineHolderDocumentos(View itemView) {
        super(itemView);
        card = itemView.findViewById(R.id.card);
        tipo = itemView.findViewById(R.id.tipo);
        nome = itemView.findViewById(R.id.nome);
    }
}