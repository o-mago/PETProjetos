package magosoftware.petprojetos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by root on 28/03/18.
 */

public class ReunioesFragment extends BaseFragment implements LineAdapterReuniao.OnItemClicked, View.OnClickListener{

    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    private LineAdapterReuniao mAdapter;
    private List<Reuniao> mModels;
    StorageReference storageRef;
    RecyclerView mRecyclerView;
    DatabaseReference dbReuniao;
    String reunioesPath;
    String nomeProjeto;
    private FloatingActionButton fabMais;
    private FloatingActionButton fabAdd;
    private FloatingActionButton fabEdit;
    private boolean isFabOpen = false;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private TextView aviso;

    public static ReunioesFragment newInstance() {
        ReunioesFragment reunioesFragment = new ReunioesFragment();
        return reunioesFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        reunioesPath = getArguments().getString("reunioes_path");
        nomeProjeto = getArguments().getString("node_projeto");
        dbReuniao = mDatabase.child(reunioesPath);
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(),R.anim.rotate_backward);
        Log.d("TAREFASFRAGMENT", reunioesPath);

        return inflater.inflate(R.layout.reunioes_layout, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);
        fabMais = getView().findViewById(R.id.opcoes_reuniao);
        fabAdd = getView().findViewById(R.id.nova_reuniao);
        fabEdit = getView().findViewById(R.id.horario_reuniao);
        aviso = getView().findViewById(R.id.aviso);
        fabMais.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        fabEdit.setOnClickListener(this);
        setupRecycler();
        setupLista();
        Log.d("ENTROU4", "PASSOU");
        mAdapter.setOnClick(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.nova_reuniao) {
            animateFAB();
            Intent intent = new Intent(getActivity(), ReunioesEditActivity.class);
            intent.putExtra("node", "NADA9232CMXC3");
            intent.putExtra("reunioes_path", reunioesPath);
            startActivity(intent);
        }
        if(id == R.id.horario_reuniao) {
            animateFAB();
            Intent intent = new Intent(getActivity(), MarcarReuniaoActivity.class);
            intent.putExtra("reunioes_path", reunioesPath);
            intent.putExtra("node_projeto", nomeProjeto);
            startActivity(intent);
        }
        if(id == R.id.opcoes_reuniao) {
            animateFAB();
        }
    }

    private void animateFAB() {
        if(isFabOpen){

            fabMais.startAnimation(rotate_backward);
            fabAdd.startAnimation(fab_close);
            fabEdit.startAnimation(fab_close);
            fabAdd.setClickable(false);
            fabEdit.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fabMais.startAnimation(rotate_forward);
            fabAdd.startAnimation(fab_open);
            fabEdit.startAnimation(fab_open);
            fabAdd.setClickable(true);
            fabEdit.setClickable(true);
            isFabOpen = true;
            Log.d("Raj","open");

        }
    }

    @Override
    public void onItemClick(int position, int id, final String titulo, String node) {
        if(id == R.id.card) {
            Intent intent = new Intent(getActivity(), ReunioesEditActivity.class);
            intent.putExtra("reunioes_path", reunioesPath);
            intent.putExtra("node", node);
            startActivity(intent);
        }
        if(id == R.id.frame_deletar) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Você tem certeza?")
                    .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbReuniao.child("reunioes").child("historico").child(titulo).removeValue();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }

    private void setupLista() {
        mModels = new ArrayList<>();
        dbReuniao.child("reunioes").child("historico").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    aviso.setVisibility(View.GONE);
                    for (DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                        String titulo = listSnapshot.child("titulo").getValue(String.class);
                        String node = listSnapshot.getKey();
                        String data = listSnapshot.child("data").getValue(String.class);
                        mModels.add(new Reuniao(titulo, data, node));
                    }
                    Log.d("DEV/REUNIOESFRAGMENT", Integer.toString(mModels.size()));
                    mAdapter.replaceAll(mModels);
                    mAdapter.notifyDataSetChanged();
                    mModels.clear();
//                mAdapter.add(mModels);
                    mRecyclerView.scrollToPosition(0);
                } else {
                    aviso.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupRecycler() {

        // Configurando o gerenciador de layout para ser uma lista.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = getView().findViewById(R.id.lista_reunioes);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new LineAdapterReuniao();
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }
}
