package magosoftware.petprojetos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by root on 16/03/18.
 */

public class LineHolderTarefa extends RecyclerView.ViewHolder{

    public TextView tituloTarefa;
    public TextView descricaoTarefa;
    public ImageView concluido;
    public Button deletar;
    public FrameLayout frameConcluido;
    public FrameLayout frameDeletar;
    public FrameLayout frameTime;
    public ImageView time;
    public CardView cardTarefa;

    public LineHolderTarefa(View itemView) {
        super(itemView);
        //card = itemView.findViewById(R.id.card);
        tituloTarefa = itemView.findViewById(R.id.titulo_card);
        descricaoTarefa = itemView.findViewById(R.id.descricao_card);
        frameConcluido = itemView.findViewById(R.id.frame_concluido);
        frameDeletar = itemView.findViewById(R.id.frame_deletar);
        frameTime = itemView.findViewById(R.id.frame_time);
        concluido = itemView.findViewById(R.id.concluido);
        deletar = itemView.findViewById(R.id.deletar);
        time = itemView.findViewById(R.id.time);
        cardTarefa = itemView.findViewById(R.id.card);
    }
}
