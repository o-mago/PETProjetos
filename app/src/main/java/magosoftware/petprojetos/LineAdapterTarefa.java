package magosoftware.petprojetos;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 16/03/18.
 */

public class LineAdapterTarefa extends RecyclerView.Adapter<LineHolderTarefa> {
    private LineAdapterTarefa.OnItemClicked onClick;

    List<Tarefa> mTarefa;

    public LineAdapterTarefa() {
        mTarefa = new ArrayList<>();
    }

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position, int id, String nome, String node);
    }

    @Override
    public LineHolderTarefa onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderTarefa(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarefa_card, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderTarefa holder, final int position) {
        holder.tituloTarefa.setText(mTarefa.get(position).getTitulo());
        holder.descricaoTarefa.setText(mTarefa.get(position).getDescricao());
        if(mTarefa.get(position).getSituacaoPrazo().equals("proximo")) {
            holder.time.setColorFilter(Color.parseColor("#FF4081"));
        }
        if(mTarefa.get(position).getSituacaoPrazo().equals("concluido")) {
            holder.concluido.setColorFilter(Color.parseColor("#1976D2"));
        }
        if(mTarefa.get(position).getConcluido()) {
            holder.concluido.setBackgroundColor(Color.parseColor("#2962FF"));
        }
        holder.frameDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, v.getId(), mTarefa.get(position).getTitulo(), mTarefa.get(position).getNode());
            }
        });
        holder.frameConcluido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, v.getId(), mTarefa.get(position).getTitulo(), mTarefa.get(position).getNode());
            }
        });
        holder.cardTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, v.getId(), mTarefa.get(position).getTitulo(), mTarefa.get(position).getNode());
            }
        });
    }

    public void add(Tarefa model) {
        mTarefa.add(model);
    }

    public void remove(Tarefa model) {
        mTarefa.remove(model);
    }

    public void add(List<Tarefa> models) {
        mTarefa.addAll(models);
    }

    public void removeAll() {
        for (int i = mTarefa.size() - 1; i >= 0; i--) {
            final Tarefa model = mTarefa.get(i);
            mTarefa.remove(model);
        }
    }

    public void replaceAll(List<Tarefa> models) {
        for (int i = mTarefa.size() - 1; i >= 0; i--) {
            final Tarefa model = mTarefa.get(i);
            if (!models.contains(model)) {
                mTarefa.remove(model);
            }
        }
        mTarefa.addAll(models);
    }

    @Override
    public int getItemCount() {
        return mTarefa.size();
    }

    public void setOnClick(LineAdapterTarefa.OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}
