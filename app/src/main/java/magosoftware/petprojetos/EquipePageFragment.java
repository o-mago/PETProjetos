package magosoftware.petprojetos;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by root on 15/03/18.
 */

public class EquipePageFragment extends BaseFragment implements View.OnClickListener {
    FirebaseUser user;
    FirebaseAuth mAuth; FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference dbProjeto;
    public SharedPreferences sharedPref;
    private String nomeProjeto;
    private String nomePET;
    CircularImageView imagemProjeto;
    RelativeLayout minhaEquipe;
    TextView nomeEquipes;
    FrameLayout menuTarefasClick;
    FrameLayout menuTarefasConcluidasClick;
    FrameLayout menuReunioesClick;
    TextView menuTarefas;
    TextView menuTarefasConcluidas;
    TextView menuReunioes;
    ColorStateList corPadrao;
    TextView menuMembros;
    String nomeEquipe;

    public static ProjetoPageFragment newInstance() {
        ProjetoPageFragment projetoPageFragment = new ProjetoPageFragment();
        return projetoPageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        nomeProjeto = getArguments().getString("nome_projeto");
        nomeEquipe = getArguments().getString("nome_equipe");
        sharedPref = getActivity().getSharedPreferences("todoApp", 0);
        nomePET = sharedPref.getString("nome_meu_pet", "nada");
        dbProjeto = mDatabase.child("PETs").child(nomePET).child("projetos").child(nomeProjeto);
        Log.d("nomePETWOW", nomePET);

        return inflater.inflate(R.layout.equipe_page, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);

        minhaEquipe = getView().findViewById(R.id.minha_equipe);
        nomeEquipes = getView().findViewById(R.id.nome_equipe);
        menuTarefasClick = getView().findViewById(R.id.menu_tarefas_click);
        menuTarefasConcluidasClick = getView().findViewById(R.id.menu_tarefas_concluidas_click);
        menuReunioesClick = getView().findViewById(R.id.menu_reunioes_click);
        menuTarefasClick.setOnClickListener(this);
        menuTarefasConcluidasClick.setOnClickListener(this);
        menuReunioesClick.setOnClickListener(this);
        menuTarefas = getView().findViewById(R.id.menu_tarefas);
        menuTarefasConcluidas = getView().findViewById(R.id.menu_tarefas_concluidas);
        menuReunioes = getView().findViewById(R.id.menu_reunioes);
        corPadrao = menuReunioes.getTextColors();

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("equipe_path", nomeProjeto);
        Fragment fragment= TarefasFragment.newInstance();
        fragment.setArguments(bundle);
        transaction.add(R.id.fragment_container_projeto, fragment);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.menu_tarefas_click) {
            menuTarefas.setTextColor(Color.parseColor("#03A9F4"));
            menuTarefasConcluidas.setTextColor(corPadrao);
            menuReunioes.setTextColor(corPadrao);
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("equipe_path", nomeProjeto);
            Fragment fragment= TarefasFragment.newInstance();
            fragment.setArguments(bundle);
            transaction.add(R.id.fragment_container_equipe, fragment);
            transaction.commit();
        }
        else if (id == R.id.menu_tarefas_concluidas_click) {
            menuTarefas.setTextColor(corPadrao);
            menuTarefasConcluidas.setTextColor(Color.parseColor("#03A9F4"));
            menuReunioes.setTextColor(corPadrao);
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("equipe_path", nomeProjeto);
            Fragment fragment= TarefasFragment.newInstance();
            fragment.setArguments(bundle);
            transaction.add(R.id.fragment_container_equipe, fragment);
            transaction.commit();
        }
        else if (id == R.id.menu_reunioes_click) {
            menuTarefas.setTextColor(corPadrao);
            menuTarefasConcluidas.setTextColor(corPadrao);
            menuReunioes.setTextColor(Color.parseColor("#03A9F4"));
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("equipe_path", nomeProjeto);
            Fragment fragment= TarefasFragment.newInstance();
            fragment.setArguments(bundle);
            transaction.add(R.id.fragment_container_equipe, fragment);
            transaction.commit();
        }
    }
}
