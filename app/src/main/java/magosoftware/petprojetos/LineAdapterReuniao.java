package magosoftware.petprojetos;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 28/03/18.
 */

public class LineAdapterReuniao extends RecyclerView.Adapter<LineHolderReuniao> {
    private LineAdapterReuniao.OnItemClicked onClick;

    List<Reuniao> mReuniao;

    public LineAdapterReuniao() {
        mReuniao = new ArrayList<>();
    }

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position, int id, String nome, String node);
    }

    @Override
    public LineHolderReuniao onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderReuniao(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reuniao_card, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderReuniao holder, final int position) {
        holder.tituloReuniao.setText(mReuniao.get(position).getTituloReuniao());
        holder.dataReuniao.setText(mReuniao.get(position).getDataReuniao());
        holder.cardReuniao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, v.getId(), mReuniao.get(position).getTituloReuniao(), mReuniao.get(position).getNode());
            }
        });
    }

    public void add(Reuniao model) {
        mReuniao.add(model);
    }

    public void remove(Reuniao model) {
        mReuniao.remove(model);
    }

    public void add(List<Reuniao> models) {
        mReuniao.addAll(models);
    }

    public void removeAll() {
        for (int i = mReuniao.size() - 1; i >= 0; i--) {
            final Reuniao model = mReuniao.get(i);
            mReuniao.remove(model);
        }
    }

    public void replaceAll(List<Reuniao> models) {
        for (int i = mReuniao.size() - 1; i >= 0; i--) {
            final Reuniao model = mReuniao.get(i);
            if (!models.contains(model)) {
                mReuniao.remove(model);
            }
        }
        mReuniao.addAll(models);
    }

    @Override
    public int getItemCount() {
        return mReuniao.size();
    }

    public void setOnClick(LineAdapterReuniao.OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}
