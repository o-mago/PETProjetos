package magosoftware.petprojetos;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by root on 19/02/18.
 */

public class LineAdapter extends RecyclerView.Adapter<LineHolder> {

    private final List<String> mDados;

    public LineAdapter(List<String> dados) {
        mDados = dados;
    }

    @Override
    public LineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LineHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linhas_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(LineHolder holder, int position) {
//        holder.title.setText(String.format(Locale.getDefault(), "%s, %d - %s",
//                mUsers.get(position).getNome(),
//                mUsers.get(position).getCurso(),
//                mUsers.get(position).getUniversidade(),
//                mUsers.get(position).getEmail(),
//                mUsers.get(position).getNascimento()
//        ));
        holder.title.setText(mDados.get(position));
    }

    @Override
    public int getItemCount() {
        return mDados != null ? mDados.size() : 0;
    }

//    public void updateList(Usuario user) {
//        insertItem(user);
//    }
//
//    // Método responsável por inserir um novo usuário na lista
//    //e notificar que há novos itens.
//    private void insertItem(Usuario user) {
//        mUsers.add(user);
//        notifyItemInserted(getItemCount());
//    }

}
