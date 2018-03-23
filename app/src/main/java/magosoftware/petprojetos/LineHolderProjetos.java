package magosoftware.petprojetos;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by root on 09/03/18.
 */

public class LineHolderProjetos extends RecyclerView.ViewHolder{
    //private CardView card;
    public CircularImageView imagemProjeto;
    public TextView nomeProjeto;
    public Button opcao;
    public CardView cardProjeto;
    //public TextView nomeUniversidade;


    public LineHolderProjetos(View itemView) {
        super(itemView);
        //card = itemView.findViewById(R.id.card);
        imagemProjeto = itemView.findViewById(R.id.imagem_projeto);
        nomeProjeto = itemView.findViewById(R.id.nome_projeto);
        cardProjeto = itemView.findViewById(R.id.card);
        opcao = itemView.findViewById(R.id.opcao);
        //nomeUniversidade = itemView.findViewById(R.id.nome_universidade);
    }
}
