package magosoftware.petprojetos;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

public class LineAdapterPresenca extends RecyclerView.Adapter<LineHolderPresenca> {
    private LineAdapterPresenca.OnItemClicked onClick;

    List<Presenca> mPresenca;

    public LineAdapterPresenca() {
        mPresenca = new ArrayList<>();
    }

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position, String nome);
    }

    @Override
    public LineHolderPresenca onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderPresenca(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lista_presenca, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderPresenca holder, final int position) {
        holder.nome.setText(mPresenca.get(position).getNome());
        if(mPresenca.get(position).getSituacao().equals("presente")) {
            holder.checkBox.setChecked(true);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mPresenca.get(position).situacao = "presente";
                }
                else {
                    mPresenca.get(position).situacao = "ausente";
                }
            }
        });
//        holder.dataReuniao.setText(mReuniao.get(position).getDataReuniao());
//        holder.cardReuniao.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClick.onItemClick(position, v.getId(),mReuniao.get(position).getTituloReuniao());
//            }
//        });
    }

    public List<Presenca> getPresenca() {
        return mPresenca;
    }

    public void add(Presenca model) {
        mPresenca.add(model);
    }

    public void remove(Presenca model) {
        mPresenca.remove(model);
    }

    public void add(List<Presenca> models) {
        mPresenca.addAll(models);
    }

    public void removeAll() {
        for (int i = mPresenca.size() - 1; i >= 0; i--) {
            final Presenca model = mPresenca.get(i);
            mPresenca.remove(model);
        }
    }

    public void replaceAll(List<Presenca> models) {
        for (int i = mPresenca.size() - 1; i >= 0; i--) {
            final Presenca model = mPresenca.get(i);
            if (!models.contains(model)) {
                mPresenca.remove(model);
            }
        }
        mPresenca.addAll(models);
    }

    @Override
    public int getItemCount() {
        return mPresenca.size();
    }

    public void setOnClick(LineAdapterPresenca.OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}
