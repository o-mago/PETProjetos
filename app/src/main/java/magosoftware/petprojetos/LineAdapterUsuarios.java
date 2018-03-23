package magosoftware.petprojetos;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by root on 03/03/18.
 */

public class LineAdapterUsuarios extends RecyclerView.Adapter<LineHolderUsuarios>{

    private LineAdapterUsuarios.OnItemClicked onClick;
    
    List<Usuario> mUsuario;

    public LineAdapterUsuarios() {

    }

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position, String nome);
    }

    @Override
    public LineHolderUsuarios onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderUsuarios(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pet_card, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderUsuarios holder, final int position) {
        holder.nomeUsuario.setText(mUsuario.get(position).getNome());
        //holder.nomeUniversidade.setText(mUsuario.get(position).getUniversidade());
        holder.imagemUsuario.setImageDrawable(mUsuario.get(position).getFoto());
        holder.cardUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, mUsuario.get(position).getNome());
            }
        });
    }

    public void add(Usuario model) {
        mUsuario.add(model);
    }

    public void remove(Usuario model) {
        mUsuario.remove(model);
    }

    public void add(List<Usuario> models) {
        mUsuario.addAll(models);
    }

    public void remove(List<Usuario> models) {
        for (Usuario model : models) {
            mUsuario.remove(model);
        }
    }

    public void replaceAll(List<Usuario> models) {
        for (int i = mUsuario.size() - 1; i >= 0; i--) {
            final Usuario model = mUsuario.get(i);
            if (!models.contains(model)) {
                mUsuario.remove(model);
            }
        }
        mUsuario.addAll(models);
    }

    @Override
    public int getItemCount() {
        return mUsuario.size();
    }

    public void setOnClick(LineAdapterUsuarios.OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}
