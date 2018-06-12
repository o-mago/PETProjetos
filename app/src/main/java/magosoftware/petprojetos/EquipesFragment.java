package magosoftware.petprojetos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
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
 * Created by root on 14/03/18.
 */

public class EquipesFragment extends BaseFragment implements LineAdapterEquipe.OnItemClicked, View.OnClickListener{
    FirebaseUser user;
    FirebaseAuth mAuth;
    private LineAdapterEquipe mAdapter;
    private List<Equipe> mModels;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference dbEquipes;
    RecyclerView mRecyclerView;
    public SharedPreferences sharedPref;
    String nomePET;
    FragmentTransaction ft;
    private String nodeProjeto;
    ValueEventListener valueEventListener;
    private ProgressBar progressBar;
    private String nodePET;
    private TextView aviso;
    private CoordinatorLayout coordinator;
    private FloatingActionButton addEquipe;
    private String nomeProjeto;

    public static EquipesFragment newInstance() {
        EquipesFragment equipesFragment = new EquipesFragment();
        return equipesFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        sharedPref = getActivity().getSharedPreferences("todoApp", 0);
        nomePET = sharedPref.getString("nome_meu_pet", "nada");
        nodePET = sharedPref.getString("node_meu_pet", "nada");
        Log.d("DEV/EQUIPESFRAG", "nomePET: "+nomePET);
        nodeProjeto = getArguments().getString("node_projeto");
        nomeProjeto = getArguments().getString("nome_projeto");
        Log.d("DEV/EQUIPESFRAG", "nodeProjeto: "+nodeProjeto);
        dbEquipes = mDatabase.child("PETs").child(nodePET).child("projetos").child(nodeProjeto).child("equipes");
        Log.d("nomePETWOW", nomePET);

        return inflater.inflate(R.layout.equipes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);

        getView().findViewById(R.id.add_equipe).setOnClickListener(this);
        progressBar = getView().findViewById(R.id.progress_bar);
        coordinator = (CoordinatorLayout) getView().findViewById(R.id.coordinator);
        addEquipe = getView().findViewById(R.id.add_equipe);
        addEquipe.setVisibility(View.GONE);
        aviso = getView().findViewById(R.id.aviso);
        setupRecycler();
        setupLista();
        Log.d("ENTROU3", "PASSOU");
        mAdapter.setOnClick(this);
//        mAdapter.add(mModels);
//        mAdapter.setOnClick(ProjetosFragment.this);
    }

    public void setupLista() {
        mModels = new ArrayList<>();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                        String nodeEquipe = listSnapshot.getKey();
                        try {
                            String nome = listSnapshot.child("nome").getValue(String.class);
                            int cor = listSnapshot.child("cor").getValue(Integer.class);
                            mModels.add(new Equipe(nome, cor, nodeEquipe));
                        } catch (NullPointerException e) {
                            dbEquipes.removeEventListener(valueEventListener);
                            setupLista();
                        }
                    }
                    aviso.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    addEquipe.setVisibility(View.VISIBLE);
                    mAdapter.replaceAll(mModels);
                    mAdapter.notifyDataSetChanged();
                    mModels.clear();
                }
                else {
                    aviso.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    addEquipe.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        };
        dbEquipes.addValueEventListener(valueEventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        dbEquipes.removeEventListener(valueEventListener);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.add_equipe) {
            Intent intent = new Intent(getActivity(), AdicioneEquipe.class);
            intent.putExtra("nome_pet", nomePET);
            intent.putExtra("node_pet", nodePET);
            intent.putExtra("node_projeto", nodeProjeto);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(int position, String nome, String node) {
        Bundle bundle = new Bundle();
        bundle.putString("node_equipe", node);
        bundle.putString("node_projeto", nodeProjeto);
        bundle.putString("nome_projeto", nomeProjeto);
        bundle.putString("nome_equipe", nome);
        bundle.putString("node_pet", nodePET);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
        ft = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = EquipePageFragment.newInstance();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        Log.d("EQUIPESFRAGMENT", "chamou fragment page");
        ft.commit();
    }

    private void setupRecycler() {

        // Configurando o gerenciador de layout para ser uma lista.
        mRecyclerView = getView().findViewById(R.id.lista_equipes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        // Adiciona o adapter que irá anexar os objetos à lista.
        // Está sendo criado com lista vazia, pois será preenchida posteriormente.
        mAdapter = new LineAdapterEquipe();
        mRecyclerView.setAdapter(mAdapter);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                layoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }
}
