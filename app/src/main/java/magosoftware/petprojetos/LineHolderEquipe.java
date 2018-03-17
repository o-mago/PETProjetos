package magosoftware.petprojetos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by root on 14/03/18.
 */

public class LineHolderEquipe extends RecyclerView.ViewHolder {
    //private CardView card;
    public TextView nomeEquipe;
    public CardView cardEquipe;
    //public TextView nomeUniversidade;


    public LineHolderEquipe(View itemView) {
        super(itemView);
        //card = itemView.findViewById(R.id.card);
        nomeEquipe = itemView.findViewById(R.id.nome_projeto);
        cardEquipe = itemView.findViewById(R.id.card);
        //nomeUniversidade = itemView.findViewById(R.id.nome_universidade);
    }
}
