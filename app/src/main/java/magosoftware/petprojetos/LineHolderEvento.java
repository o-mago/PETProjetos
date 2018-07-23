package magosoftware.petprojetos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LineHolderEvento extends RecyclerView.ViewHolder{

    public CardView cardDia;
    public LinearLayout fundoCard;
    public TextView numeroDia;
    public FrameLayout marcacaoTarefa;
    public FrameLayout marcacaoReuniao;
    public FrameLayout marcacaoAniversario;
    public FrameLayout marcacaoEvento;

    public LineHolderEvento(View itemView) {
        super(itemView);
        cardDia = itemView.findViewById(R.id.card_dia);
        fundoCard = itemView.findViewById(R.id.fundo_card);
        numeroDia = itemView.findViewById(R.id.numero_dia);
        marcacaoTarefa = itemView.findViewById(R.id.marcacao_tarefa);
        marcacaoReuniao = itemView.findViewById(R.id.marcacao_reuniao);
        marcacaoAniversario = itemView.findViewById(R.id.marcacao_aniversario);
        marcacaoEvento = itemView.findViewById(R.id.marcacao_evento);
    }
}