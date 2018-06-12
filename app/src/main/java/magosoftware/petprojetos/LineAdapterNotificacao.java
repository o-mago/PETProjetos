package magosoftware.petprojetos;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.internal.ListenerClass;

public class LineAdapterNotificacao extends RecyclerView.Adapter<LineHolderNotificacao> {
    private LineAdapterNotificacao.OnItemClicked onClick;

    List<Notificacao> mNotificacao;

    public LineAdapterNotificacao() {
        mNotificacao = new ArrayList<>();
    }

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position, String nome, String node, String tipo);
    }

    @Override
    public LineHolderNotificacao onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderNotificacao(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderNotificacao holder, final int position) {
        holder.tituloNotificacao.setText(mNotificacao.get(position).getTitulo());
        holder.dataNotificacao.setText(mNotificacao.get(position).getData());
        holder.mensagemNotificacao.setText(mNotificacao.get(position).getMensagem());
        if(mNotificacao.get(position).getTipo().equals("tarefa")) {
            holder.imagemNotificacao.setImageResource(R.mipmap.tarefa_card);
        }
        if(mNotificacao.get(position).getTipo().equals("reuniao")) {
            holder.imagemNotificacao.setImageResource(R.mipmap.evento_notificacao_round);
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, mNotificacao.get(position).getTitulo(), mNotificacao.get(position).getNode(), mNotificacao.get(position).getTipo());
            }
        });
    }

    public void add(Notificacao model) {
        mNotificacao.add(model);
    }

    public void remove(Notificacao model) {
        mNotificacao.remove(model);
    }

    public void add(List<Notificacao> models) {
        mNotificacao.addAll(models);
    }

    public void removeAll() {
        for (int i = mNotificacao.size() - 1; i >= 0; i--) {
            final Notificacao model = mNotificacao.get(i);
            mNotificacao.remove(model);
        }
    }

    public void replaceAll(List<Notificacao> models) {
        for (int i = mNotificacao.size() - 1; i >= 0; i--) {
            final Notificacao model = mNotificacao.get(i);
            if (!models.contains(model)) {
                mNotificacao.remove(model);
            }
        }
        mNotificacao.addAll(models);
    }

//    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    @Override
    public int getItemCount() {
        return mNotificacao.size();
    }

    public void setOnClick(LineAdapterNotificacao.OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}