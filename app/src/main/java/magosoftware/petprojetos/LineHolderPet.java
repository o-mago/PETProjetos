package magosoftware.petprojetos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by root on 01/03/18.
 */

public class LineHolderPet extends RecyclerView.ViewHolder{
    //private CardView card;
    public ImageView logoPET;
    public TextView nomePET;
    public CardView cardPET;
    //public TextView nomeUniversidade;


    public LineHolderPet(View itemView) {
        super(itemView);
        //card = itemView.findViewById(R.id.card);
        logoPET = itemView.findViewById(R.id.imagem_pet);
        nomePET = itemView.findViewById(R.id.nome_pet);
        cardPET = itemView.findViewById(R.id.card);
        //nomeUniversidade = itemView.findViewById(R.id.nome_universidade);
    }
}
