package magosoftware.petprojetos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 09/03/18.
 */

public class TarefasFragment extends BaseFragment implements LineAdapterTarefa.OnItemClicked, View.OnClickListener{

    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    private LineAdapterTarefa mAdapter;
    private List<Tarefa> mModels;
    StorageReference storageRef;
    RecyclerView mRecyclerView;
    DatabaseReference dbEquipe;
    String equipePath;

    public static TarefasFragment newInstance() {
        TarefasFragment tarefasFragment = new TarefasFragment();
        return tarefasFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        equipePath = getArguments().getString("equipe_path");
        dbEquipe = mDatabase.child(equipePath);
        Log.d("nomePETWOW", equipePath);

        return inflater.inflate(R.layout.tarefas_page, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);

        getView().findViewById(R.id.add_tarefa).setOnClickListener(this);
        setupRecycler();
        setupLista();
        Log.d("ENTROU3", "PASSOU");
        mAdapter.setOnClick(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.add_tarefa) {
            Intent intent = new Intent(getActivity(), TarefasEditActivity.class);
            intent.putExtra("equipe_path", equipePath);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(int position, String nome) {
            Intent intent = new Intent(getActivity(), TarefasEditActivity.class);
            intent.putExtra("equipe_path", equipePath);
            startActivity(intent);
    }

    private void setupLista() {
        mModels = new ArrayList<>();
        dbEquipe.child("tarefas").child("completas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                    String titulo = listSnapshot.child("titulo").getValue(String.class);
                    String descricao = listSnapshot.child("descricao").getValue(String.class);
                    mModels.add(new Tarefa(titulo, descricao, false));
                }
                mAdapter.add(mModels);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupRecycler() {

        // Configurando o gerenciador de layout para ser uma lista.
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView = getView().findViewById(R.id.lista_tarefas);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new LineAdapterTarefa();
        mRecyclerView.setAdapter(mAdapter);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                layoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }
}
