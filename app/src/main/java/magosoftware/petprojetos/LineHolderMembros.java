package magosoftware.petprojetos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

public class LineHolderMembros extends RecyclerView.ViewHolder {
    public CircularImageView imagemUsuario;
    public TextView nomeUsuario;
    public Button opcao;
    public CardView cardUsuario;


    public LineHolderMembros(View itemView) {
        super(itemView);
        imagemUsuario = itemView.findViewById(R.id.imagem_projeto);
        nomeUsuario = itemView.findViewById(R.id.nome_projeto);
        cardUsuario = itemView.findViewById(R.id.card);
        opcao = itemView.findViewById(R.id.opcao);
    }
}
