package magosoftware.petprojetos;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class LineHolderPresenca extends RecyclerView.ViewHolder{

    public TextView nome;
    public CheckBox checkBox;

    public LineHolderPresenca(View itemView) {
        super(itemView);
        nome = itemView.findViewById(R.id.nome);
        checkBox = itemView.findViewById(R.id.checkBox);
    }
}