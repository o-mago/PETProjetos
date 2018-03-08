package magosoftware.petprojetos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by root on 07/03/18.
 */

public class LineHolderPesquisarPetiano extends RecyclerView.ViewHolder{
    //private CardView card;
    public ImageView imagemUsuario;
    public TextView nomeUsuario;
    public CardView cardUsuario;
    //public TextView nomeUniversidade;


    public LineHolderPesquisarPetiano(View itemView) {
        super(itemView);
        //card = itemView.findViewById(R.id.card);
        imagemUsuario = itemView.findViewById(R.id.imagem_pet);
        nomeUsuario = itemView.findViewById(R.id.nome_pet);
        cardUsuario = itemView.findViewById(R.id.card);
        //nomeUniversidade = itemView.findViewById(R.id.nome_universidade);
    }
}

