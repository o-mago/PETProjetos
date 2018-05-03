package magosoftware.petprojetos;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class LineAdapterRequisicao extends RecyclerView.Adapter<LineHolderRequisicao>{

    private LineAdapterRequisicao.OnItemClicked onClick;

    List<Usuario> mUsuario;

    public LineAdapterRequisicao() {
        mUsuario = new ArrayList<>();
    }

    public interface OnItemClicked {
        void onItemClick(String nome, String codigo, String tipo, String situacao);
    }

    @Override
    public LineHolderRequisicao onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderRequisicao(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.requisicao_card, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderRequisicao holder, final int position) {
        holder.nomeUsuario.setText(mUsuario.get(position).getNome());
        holder.imagemUsuario.setImageDrawable(mUsuario.get(position).getFoto());
        holder.botaoOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(mUsuario.get(position).getNome(),
                        mUsuario.get(position).getCodigo(),
                        "ok",
                        mUsuario.get(position).getSituacao());
            }
        });
        holder.botaoCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(mUsuario.get(position).getNome(),
                        mUsuario.get(position).getCodigo(),
                        "cancel",
                        mUsuario.get(position).getSituacao());
            }
        });
        holder.cardUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(mUsuario.get(position).getNome(),
                        mUsuario.get(position).getCodigo(),
                        "card",
                        mUsuario.get(position).getSituacao());
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

    public void removeAll() {
        for (int i = mUsuario.size() - 1; i >= 0; i--) {
            final Usuario model = mUsuario.get(i);
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

    public void setOnClick(LineAdapterRequisicao.OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}