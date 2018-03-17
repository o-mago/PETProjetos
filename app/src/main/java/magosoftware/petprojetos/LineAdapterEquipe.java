package magosoftware.petprojetos;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import magosoftware.petprojetos.Equipe;
import magosoftware.petprojetos.LineHolderEquipe;
import magosoftware.petprojetos.R;

/**
 * Created by root on 14/03/18.
 */

public class LineAdapterEquipe extends RecyclerView.Adapter<LineHolderEquipe> {
    private LineAdapterEquipe.OnItemClicked onClick;

    List<Equipe> mEquipe;

    public LineAdapterEquipe() {
        mEquipe = new ArrayList<>();
    }

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position, String nome);
    }

    @Override
    public LineHolderEquipe onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderEquipe(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.equipe_card, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderEquipe holder, final int position) {
        holder.nomeEquipe.setText(mEquipe.get(position).getNome());
        holder.cardEquipe.setCardBackgroundColor(mEquipe.get(position).getCorEquipe());
        holder.cardEquipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, mEquipe.get(position).getNome());
            }
        });
    }

    public void add(Equipe model) {
        mEquipe.add(model);
    }

    public void remove(Equipe model) {
        mEquipe.remove(model);
    }

    public void add(List<Equipe> models) {
        mEquipe.addAll(models);
    }

    public void removeAll() {
        for (int i = mEquipe.size() - 1; i >= 0; i--) {
            final Equipe model = mEquipe.get(i);
            mEquipe.remove(model);
        }
    }

    public void replaceAll(List<Equipe> models) {
        for (int i = mEquipe.size() - 1; i >= 0; i--) {
            final Equipe model = mEquipe.get(i);
            if (!models.contains(model)) {
                mEquipe.remove(model);
            }
        }
        mEquipe.addAll(models);
    }

    @Override
    public int getItemCount() {
        return mEquipe.size();
    }

    public void setOnClick(LineAdapterEquipe.OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}
