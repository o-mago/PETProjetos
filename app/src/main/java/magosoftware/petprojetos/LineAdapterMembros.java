package magosoftware.petprojetos;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class LineAdapterMembros extends RecyclerView.Adapter<LineHolderMembros>{

    private LineAdapterMembros.OnItemClicked onClick;

    List<Usuario> mUsuario;

    private Drawable backgroundCoordenador;
    private Drawable backgroundMembro;
    private Drawable backgroundBolsistas;
    private Drawable backgroundOficiais;
    private Drawable backgroundVoluntarios;

    public LineAdapterMembros(Drawable backgroundCoordenador, Drawable backgroundMembro) {
        mUsuario = new ArrayList<>();
        this.backgroundCoordenador = backgroundCoordenador;
        this.backgroundMembro = backgroundMembro;
    }

    public LineAdapterMembros(Drawable backgroundBolsistas, Drawable backgroundOficiais, Drawable backgroundVoluntarios) {
        mUsuario = new ArrayList<>();
        this.backgroundBolsistas = backgroundBolsistas;
        this.backgroundOficiais = backgroundOficiais;
        this.backgroundVoluntarios = backgroundVoluntarios;
    }

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position, String codigo, String situacao, String tipo);
    }

    @Override
    public LineHolderMembros onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderMembros(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.projeto_card, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderMembros holder, final int position) {
        holder.nomeUsuario.setText(mUsuario.get(position).getNome());
        holder.imagemUsuario.setImageDrawable(mUsuario.get(position).getFoto());
        if(mUsuario.get(position).getSituacao().equals("coordenador")) {
            holder.opcao.setText("COORD");
            holder.opcao.setTextColor(Color.parseColor("#03A9F4"));
            holder.opcao.setBackgroundDrawable(backgroundCoordenador);
        }
        else if(mUsuario.get(position).getSituacao().equals("membro")) {
            holder.opcao.setText("MEMBRO");
            holder.opcao.setTextColor(Color.parseColor("#00E676"));
            holder.opcao.setBackgroundDrawable(backgroundMembro);
        }
        else if(mUsuario.get(position).getSituacao().equals("bolsistas")) {
            holder.opcao.setText("BOLSISTA");
            holder.opcao.setTextColor(Color.parseColor("#03A9F4"));
            holder.opcao.setBackgroundDrawable(backgroundBolsistas);
        }
        else if(mUsuario.get(position).getSituacao().equals("oficiais")) {
            holder.opcao.setText("OFICIAL");
            holder.opcao.setTextColor(Color.parseColor("#00E676"));
            holder.opcao.setBackgroundDrawable(backgroundOficiais);
        }
        else if(mUsuario.get(position).getSituacao().equals("voluntarios")) {
            holder.opcao.setText("VOLUNT");
            holder.opcao.setTextColor(Color.parseColor("#6A1B9A"));
            holder.opcao.setBackgroundDrawable(backgroundVoluntarios);
        }

        holder.opcao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, mUsuario.get(position).getCodigo(), mUsuario.get(position).getSituacao(), "opcao");
            }
        });
        holder.cardUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, mUsuario.get(position).getCodigo(), mUsuario.get(position).getSituacao(), "card");
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

    public void setOnClick(LineAdapterMembros.OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}