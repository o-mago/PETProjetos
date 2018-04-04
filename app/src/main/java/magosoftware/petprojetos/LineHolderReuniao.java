package magosoftware.petprojetos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by root on 28/03/18.
 */

public class LineHolderReuniao extends RecyclerView.ViewHolder{

    public TextView tituloReuniao;
    public TextView dataReuniao;
    public ImageView deletar;
    public CardView cardReuniao;

    public LineHolderReuniao(View itemView) {
        super(itemView);
        tituloReuniao = itemView.findViewById(R.id.titulo_reuniao);
        dataReuniao = itemView.findViewById(R.id.data_reuniao);
        deletar = itemView.findViewById(R.id.deletar);
        cardReuniao = itemView.findViewById(R.id.card);
    }
}
