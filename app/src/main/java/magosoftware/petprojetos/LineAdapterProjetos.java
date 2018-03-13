package magosoftware.petprojetos;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 09/03/18.
 */

public class LineAdapterProjetos extends RecyclerView.Adapter<LineHolderProjetos>{

    private LineAdapterProjetos.OnItemClicked onClick;

    List<Projeto> mProjeto;

    private Drawable backgroundEntrar;
    private Drawable backgroundAguardando;
    private Drawable backgroundMembro;

    public LineAdapterProjetos(Drawable backgroundEntrar, Drawable backgroundAguardando, Drawable backgroundMembro) {
        mProjeto = new ArrayList<>();
        this.backgroundEntrar = backgroundEntrar;
        this.backgroundAguardando = backgroundAguardando;
        this.backgroundMembro = backgroundMembro;
    }

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position, String nome, String situacao);
    }

    @Override
    public LineHolderProjetos onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderProjetos(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.projeto_card, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderProjetos holder, final int position) {
        holder.nomeProjeto.setText(mProjeto.get(position).getNome());
        holder.imagemProjeto.setImageDrawable(mProjeto.get(position).getImagemProjeto());
        if(mProjeto.get(position).getSituacao().equals("fora")) {
            holder.opcao.setText("ENTRAR");
            holder.opcao.setTextColor(Color.parseColor("#03A9F4"));
            holder.opcao.setBackgroundDrawable(backgroundEntrar);
        }
        else if(mProjeto.get(position).getSituacao().equals("aguardando")) {
            holder.opcao.setText("AGUARDANDO");
            holder.opcao.setTextColor(Color.parseColor("#FFFF00"));
            holder.opcao.setBackgroundDrawable(backgroundAguardando);
        }
        else if(mProjeto.get(position).getSituacao().equals("membro")) {
            holder.opcao.setText("MEMBRO");
            holder.opcao.setTextColor(Color.parseColor("#00E676"));
            holder.opcao.setBackgroundDrawable(backgroundMembro);
        }
        holder.cardProjeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, mProjeto.get(position).getNome(), mProjeto.get(position).getSituacao());
            }
        });
    }

    public void add(Projeto model) {
        mProjeto.add(model);
    }

    public void remove(Projeto model) {
        mProjeto.remove(model);
    }

    public void add(List<Projeto> models) {
        mProjeto.addAll(models);
    }

    public void remove(List<Projeto> models) {
        for (Projeto model : models) {
            mProjeto.remove(model);
        }
    }

    public void replaceAll(List<Projeto> models) {
        for (int i = mProjeto.size() - 1; i >= 0; i--) {
            final Projeto model = mProjeto.get(i);
            if (!models.contains(model)) {
                mProjeto.remove(model);
            }
        }
        mProjeto.addAll(models);
    }

    @Override
    public int getItemCount() {
        return mProjeto.size();
    }

    public void setOnClick(LineAdapterProjetos.OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}