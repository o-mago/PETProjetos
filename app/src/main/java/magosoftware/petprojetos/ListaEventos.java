package magosoftware.petprojetos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.DateFormatSymbols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ListaEventos extends BaseFragment implements LineAdapterNotificacao.OnItemClicked, View.OnClickListener {

    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    private LineAdapterNotificacao mAdapter;
    private List<Notificacao> mModels;
    StorageReference storageRef;
    RecyclerView mRecyclerView;
    DatabaseReference dbPetUsuario;
    private SharedPreferences sharedPref;
    private String nodePET;
    private ProgressBar progressBar;
    private List<String> caminhos;
    private int dia;
    private int mes;
    private int ano;
    private TextView dataTitulo;
    private DateFormatSymbols dateFormat;

    public static ListaEventos newInstance() {
        ListaEventos listaEventos = new ListaEventos();
        return listaEventos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        sharedPref = getActivity().getSharedPreferences("todoApp", 0);
        nodePET = sharedPref.getString("node_meu_pet", "nada");
        caminhos = new ArrayList<>();
        String caminhoReuniao = getArguments().getString("path_reuniao");
        String caminhoTarefa = getArguments().getString("path_tarefa");
        String caminhoAniversario = getArguments().getString("path_aniversario");
        String caminhoEvento = getArguments().getString("path_evento");
        dia = getArguments().getInt("dia");
        mes = getArguments().getInt("mes");
        ano = getArguments().getInt("ano");
        preencheCaminhos(caminhoReuniao);
        preencheCaminhos(caminhoTarefa);
        preencheCaminhos(caminhoAniversario);
        preencheCaminhos(caminhoEvento);
        Log.d("DEV/TAREFASFRAGMENT", "node_meu_pet: "+nodePET);
        dbPetUsuario = mDatabase.child("Usuarios").child(user.getUid()).child("pet").child(nodePET);
//        Log.d("TAREFASFRAGMENT", tarefaPath);
        return inflater.inflate(R.layout.lista_eventos, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);
        progressBar = getView().findViewById(R.id.progress_bar);
        dataTitulo = getView().findViewById(R.id.data_titulo);
        getView().findViewById(R.id.seta_voltar).setOnClickListener(this);
        setupData();
        setupRecyclerView();
        setupCards();
        mAdapter.setOnClick(this);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = getView().findViewById(R.id.recycler_eventos);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new LineAdapterNotificacao();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setupData() {
        dateFormat = new DateFormatSymbols();
        String diaString = ""+dia;
        String mesString = dateFormat.getMonths()[mes-1];
        String anoString = ""+ano;
        String textoData = diaString+", "+mesString+", "+anoString;
        dataTitulo.setText(textoData);
    }

    private void setupCards() {
        mModels = new ArrayList<>();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(String caminho : caminhos) {
                    Log.d("DEV/LISTAEVENTOS", "caminho: "+caminho);
                    if(caminho.contains("reunioes")) {
                        String caminhoNome = caminho.split("/reunioes")[0];
                        String nome = dataSnapshot.child(caminhoNome).child("nome").getValue(String.class);
                        String data = dataSnapshot.child(caminho).child("data").getValue(String.class);
                        String horario = dataSnapshot.child(caminho).child("horario").getValue(String.class);
                        mModels.add(new Notificacao("reuniao", nome, data + " " + horario, caminho, ""));
                    } else if (caminho.contains("tarefas")) {
                        String titulo = dataSnapshot.child(caminho).child("titulo").getValue(String.class);
                        String prazo = dataSnapshot.child(caminho).child("prazo").getValue(String.class);
                        mModels.add(new Notificacao("tarefa", titulo, prazo, caminho, ""));
                    } else if (caminho.contains("nascimento")) {
                        String caminhoUsuario = caminho.split("/nascimento")[0];
                        String nome = dataSnapshot.child(caminhoUsuario).child("nome").getValue(String.class);
                        String data = dataSnapshot.child(caminho).getValue(String.class);
                        mModels.add(new Notificacao("aniversario", nome, data, caminhoUsuario, ""));
                    }
                }
                progressBar.setVisibility(View.GONE);
                mAdapter.replaceAll(mModels);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(int position, String nome, final String node, final String tipo) {
//        if(tipo.equals("tarefa")) {
//            Intent intent = new Intent(getActivity(), TarefasEditActivity.class);
//            intent.putExtra("nome_projeto", nomeProjeto);
//            intent.putExtra("situacao_tarefa", situacaoTarefa);
//            intent.putExtra("node", node.split(situacaoTarefa + "/")[1]);
//            intent.putExtra("tarefa_path", node.split("/tarefas/")[0]);
//            startActivity(intent);
//        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.seta_voltar) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = CalendarioFragment.newInstance();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
            getFragmentManager().popBackStack();
        }

    }

    private void preencheCaminhos(String caminho) {
        Log.d("DEV/LISTAEVENTOS", "CAMINHO: "+ caminho);
        if(caminho.contains(" ")) {
            String[] arrayCaminho = caminho.split(" ");
            caminhos.addAll(Arrays.asList(arrayCaminho));
        } else {
            caminhos.add(caminho);
        }
    }
}
