package magosoftware.petprojetos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

public class LineHolderRequisicao extends RecyclerView.ViewHolder {
    public CircularImageView imagemUsuario;
    public TextView nomeUsuario;
    public ImageButton botaoOk;
    public ImageButton botaoCancel;
    public CardView cardUsuario;


    public LineHolderRequisicao(View itemView) {
        super(itemView);
        imagemUsuario = itemView.findViewById(R.id.imagem_projeto);
        nomeUsuario = itemView.findViewById(R.id.nome_projeto);
        cardUsuario = itemView.findViewById(R.id.card);
        botaoOk = itemView.findViewById(R.id.ok);
        botaoCancel = itemView.findViewById(R.id.cancel);
    }
}
