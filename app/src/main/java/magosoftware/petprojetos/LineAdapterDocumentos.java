package magosoftware.petprojetos;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class LineAdapterDocumentos extends RecyclerView.Adapter<LineHolderDocumentos>{

    private LineAdapterDocumentos.OnItemClicked onClick;

    List<Documento> mDocumento;

    public LineAdapterDocumentos() {
        mDocumento = new ArrayList<>();
    }

    //make interface like this
    public interface OnItemClicked {
        void onItemClick(int position, String path);
    }

    @Override
    public LineHolderDocumentos onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolderDocumentos(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_documento, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolderDocumentos holder, final int position) {
        holder.nome.setText(mDocumento.get(position).getNome());
        holder.tipo.setText(mDocumento.get(position).getTipo());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClick(position, mDocumento.get(position).getPath());
            }
        });
    }

    public void add(Documento model) {
        mDocumento.add(model);
    }

    public void remove(Documento model) {
        mDocumento.remove(model);
    }

    public void add(List<Documento> models) {
        mDocumento.addAll(models);
    }

    public void removeAll() {
        for (int i = mDocumento.size() - 1; i >= 0; i--) {
            final Documento model = mDocumento.get(i);
            mDocumento.remove(model);
        }
    }

    public void replaceAll(List<Documento> models) {
        for (int i = mDocumento.size() - 1; i >= 0; i--) {
            final Documento model = mDocumento.get(i);
            if (!models.contains(model)) {
                mDocumento.remove(model);
            }
        }
        mDocumento.addAll(models);
    }

    @Override
    public int getItemCount() {
        return mDocumento.size();
    }

    public void setOnClick(LineAdapterDocumentos.OnItemClicked onClick)
    {
        this.onClick=onClick;
    }
}