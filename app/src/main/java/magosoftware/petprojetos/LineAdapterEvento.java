package magosoftware.petprojetos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

public class LineAdapterEvento extends RecyclerView.Adapter<LineHolderEvento> {
    private LineAdapterEvento.OnItemClicked onClick;

    List<Evento> mEvento;
    Context mContext;
    int ultimaPosicao = -1;

    public LineAdapterEvento(Context context) {
        mEvento = new ArrayList<>();
        mContext = context;
    }

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position, int id, int dia, int mes, int ano, int diaSemana,
                         String caminhoReuniao, String caminhoTarefa, String caminhoAniversario, String caminhoEvento);
    }

    @Override
    public LineHolderEvento onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderEvento(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dia_evento, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderEvento holder, final int position) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int devicewidth = displaymetrics.widthPixels;
        int deviceheight = displaymetrics.heightPixels;
        holder.cardDia.getLayoutParams().width = devicewidth / 7;
        holder.cardDia.getLayoutParams().height = devicewidth / 7;
        holder.numeroDia.setTextSize(devicewidth / 42);
        if(mEvento.get(position).diaSemana == 1 || mEvento.get(position).diaSemana == 7) {
            holder.fundoCard.setBackgroundColor(Color.parseColor("#D7D7D7"));
        }
        if(mEvento.get(position).hoje) {
            holder.fundoCard.setBackgroundColor(Color.parseColor("#f9ffb7"));
        }
        if(mEvento.get(position).isVazio()) {
            holder.cardDia.setVisibility(View.GONE);
        }
        else {
            String diaString = Integer.toString(mEvento.get(position).getDia());
            holder.numeroDia.setText(diaString);
//        holder.marcacaoTarefa.getLayoutParams().width = holder.cardDia.getWidth()/4;
//        holder.marcacaoTarefa.getLayoutParams().height = holder.cardDia.getWidth()/4;
//        holder.marcacaoReuniao.getLayoutParams().width = holder.cardDia.getWidth()/4;
//        holder.marcacaoReuniao.getLayoutParams().height = holder.cardDia.getWidth()/4;
//        holder.marcacaoAniversario.getLayoutParams().width = holder.cardDia.getWidth()/4;
//        holder.marcacaoAniversario.getLayoutParams().height = holder.cardDia.getWidth()/4;
//        holder.marcacaoEvento.getLayoutParams().width = holder.cardDia.getWidth()/4;
//        holder.marcacaoEvento.getLayoutParams().height = holder.cardDia.getWidth()/4;
            if (mEvento.get(position).isTarefa()) {
                holder.marcacaoTarefa.setVisibility(View.VISIBLE);
            } else {
                holder.marcacaoTarefa.setVisibility(View.GONE);
            }
            if (mEvento.get(position).isReuniao()) {
                holder.marcacaoReuniao.setVisibility(View.VISIBLE);
            } else {
                holder.marcacaoReuniao.setVisibility(View.GONE);
            }
            if (mEvento.get(position).isAniversario()) {
                holder.marcacaoAniversario.setVisibility(View.VISIBLE);
            } else {
                holder.marcacaoAniversario.setVisibility(View.GONE);
            }
            if (mEvento.get(position).isEvento()) {
                holder.marcacaoEvento.setVisibility(View.VISIBLE);
            } else {
                holder.marcacaoEvento.setVisibility(View.GONE);
            }

            setAnimation(holder.itemView, position);

            holder.cardDia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.onItemClick(position, v.getId(), mEvento.get(position).getDia(),
                            mEvento.get(position).getMes(), mEvento.get(position).getAno(), mEvento.get(position).getDiaSemana(),
                            mEvento.get(position).getCaminhoReuniao(), mEvento.get(position).getCaminhoTarefa(),
                            mEvento.get(position).getCaminhoAniversario(), mEvento.get(position).getCaminhoEvento());
                }
            });
        }
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > ultimaPosicao)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
            animation.setDuration(1000);
            viewToAnimate.startAnimation(animation);
            ultimaPosicao = position;
        }
    }

    public void add(Evento model) {
        mEvento.add(model);
    }

    public void remove(Evento model) {
        mEvento.remove(model);
    }

    public void add(List<Evento> models) {
        mEvento.addAll(models);
    }

    public void removeAll() {
        for (int i = mEvento.size() - 1; i >= 0; i--) {
            final Evento model = mEvento.get(i);
            mEvento.remove(model);
        }
    }

    public void replaceAll(List<Evento> models) {
        for (int i = mEvento.size() - 1; i >= 0; i--) {
            final Evento model = mEvento.get(i);
            if (!models.contains(model)) {
                mEvento.remove(model);
            }
        }
        mEvento.addAll(models);
    }

    @Override
    public int getItemCount() {
        return mEvento.size();
    }

    public void setOnClick(LineAdapterEvento.OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}