package magosoftware.petprojetos;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by root on 01/03/18.
 */

public class LineAdapterPet extends RecyclerView.Adapter<LineHolderPet> {

    private final Comparator<Pet> mComparator;

    public LineAdapterPet(Comparator comparator) {
        mComparator = comparator;
    }

    private OnItemClicked onClick;

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position, String nome, String codigo);
    }

    @Override
    public LineHolderPet onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderPet(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.usuarios_card, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderPet holder, final int position) {
        holder.nomePET.setText(mSortedList.get(position).getNome());
        //holder.nomeUniversidade.setText(mSortedList.get(position).getUniversidade());
        holder.logoPET.setImageDrawable(mSortedList.get(position).getLogo());
        holder.cardPET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, mSortedList.get(position).getNome(), mSortedList.get(position).getCodigo());
            }
        });
    }

private final SortedList<Pet> mSortedList = new SortedList<>(Pet.class, new SortedList.Callback<Pet>() {
    @Override
    public int compare(Pet a, Pet b) {
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
    public boolean areContentsTheSame(Pet oldItem, Pet newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areItemsTheSame(Pet item1, Pet item2) {
        return item1 == item2;
    }
});

    public void add(Pet model) {
        mSortedList.add(model);
    }

    public void remove(Pet model) {
        mSortedList.remove(model);
    }

    public void add(List<Pet> models) {
        mSortedList.addAll(models);
    }

    public void remove(List<Pet> models) {
        mSortedList.beginBatchedUpdates();
        for (Pet model : models) {
            mSortedList.remove(model);
        }
        mSortedList.endBatchedUpdates();
    }

    public void replaceAll(List<Pet> models, Drawable padrao) {
        mSortedList.beginBatchedUpdates();
        for (int i = mSortedList.size() - 1; i >= 0; i--) {
            final Pet model = mSortedList.get(i);
            if (!models.contains(model)) {
                mSortedList.remove(model);
            }
        }
        mSortedList.addAll(models);
        if(mSortedList.size() == 0) {
            Pet naoEncontrou = new Pet("","Não encontrou seu PET?", padrao);
            mSortedList.add(naoEncontrou);
        }
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
