package magosoftware.petprojetos;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerHolder>{

    private DrawerAdapter.OnItemClicked onClick;

    List<ItemMenu> mItem;

    public DrawerAdapter() {
        mItem = new ArrayList<>();
    }

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position, View itemView, String opcaoEscolhida);
    }

    @Override
    public DrawerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DrawerHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider_drawer, parent, false));
    }

    @Override
    public void onBindViewHolder(final DrawerHolder holder, final int position) {
        holder.opcao.setText(mItem.get(position).getOpcao());
        holder.icone.setImageDrawable(mItem.get(position).getIcone());
        
        holder.menuClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, v, mItem.get(position).getOpcao());
            }
        });
    }

    public void add(ItemMenu model) {
        mItem.add(model);
    }

    public void remove(ItemMenu model) {
        mItem.remove(model);
    }

    public void add(List<ItemMenu> models) {
        mItem.addAll(models);
    }

    public void removeAll() {
        for (int i = mItem.size() - 1; i >= 0; i--) {
            final ItemMenu model = mItem.get(i);
            mItem.remove(model);
        }
    }

    public void replaceAll(List<ItemMenu> models) {
        for (int i = mItem.size() - 1; i >= 0; i--) {
            final ItemMenu model = mItem.get(i);
            if (!models.contains(model)) {
                mItem.remove(model);
            }
        }
        mItem.addAll(models);
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }

    public void setOnClick(DrawerAdapter.OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}