package magosoftware.petprojetos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class LineHolderNotificacao extends RecyclerView.ViewHolder{

    public TextView tituloNotificacao;
    public ImageView imagemNotificacao;
    public TextView dataNotificacao;
    public TextView mensagemNotificacao;
    public CardView card;

    public LineHolderNotificacao(View itemView) {
        super(itemView);
        //card = itemView.findViewById(R.id.card);
        tituloNotificacao = itemView.findViewById(R.id.titulo_notificacao);
        imagemNotificacao = itemView.findViewById(R.id.imagem_notificacao);
        dataNotificacao = itemView.findViewById(R.id.data_notificacao);
        mensagemNotificacao = itemView.findViewById(R.id.mensagem);
        card = itemView.findViewById(R.id.card);
    }
}