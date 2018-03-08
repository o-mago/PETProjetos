package magosoftware.petprojetos;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Comparator;
import java.util.List;

/**
 * Created by root on 07/03/18.
 */

public class LineAdapterPesquisarPetiano extends RecyclerView.Adapter<LineHolderPesquisarPetiano> {

    private final Comparator<Usuario> mComparator;

    public LineAdapterPesquisarPetiano(Comparator comparator) {
        mComparator = comparator;
    }

    private OnItemClicked onClick;

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position, String nome);
    }

    @Override
    public LineHolderPesquisarPetiano onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderPesquisarPetiano(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.usuarios_card, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderPesquisarPetiano holder, final int position) {
        holder.nomeUsuario.setText(mSortedList.get(position).getNome());
        //holder.nomeUniversidade.setText(mSortedList.get(position).getUniversidade());
        holder.imagemUsuario.setImageDrawable(mSortedList.get(position).getFoto());
        holder.cardUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, mSortedList.get(position).getCodigo());
            }
        });
    }

    private final SortedList<Usuario> mSortedList = new SortedList<>(Usuario.class, new SortedList.Callback<Usuario>() {
        @Override
        public int compare(Usuario a, Usuario b) {
            return mComparator.compare(a, b);
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(Usuario oldItem, Usuario newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Usuario item1, Usuario item2) {
            return item1 == item2;
        }
    });

    public void add(Usuario model) {
        mSortedList.add(model);
    }

    public void remove(Usuario model) {
        mSortedList.remove(model);
    }

    public void add(List<Usuario> models) {
        mSortedList.addAll(models);
    }

    public void remove(List<Usuario> models) {
        mSortedList.beginBatchedUpdates();
        for (Usuario model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    public void replaceAll(List<Usuario> models) {
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final Usuario model = mSortedList.get(i);
            if (!models.contains(model)) {
                mSortedList.remove(model);
            }
        }
        mSortedList.addAll(models);
        mSortedList.endBatchedUpdates();
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
    }

}
