package magosoftware.petprojetos;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

/**
 * Created by root on 09/03/18.
 */

public class ProjetoPageFragment extends BaseFragment implements View.OnClickListener {

    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference dbProjeto;
    RecyclerView mRecyclerView;
    public SharedPreferences sharedPref;
    private String nodeProjeto;
    private String nomePET;
    Uri uriProjeto;
    CircularImageView imagemProjeto;
    RelativeLayout meuProjeto;
    TextView nomeProjetos;
    FrameLayout menuEquipesClick;
    FrameLayout menuReunioesClick;
    FrameLayout menuMembrosClick;
    TextView menuEquipes;
    TextView menuReunioes;
    ColorStateList corPadrao;
    TextView menuMembros;
    private ProgressBar progressBar;
    private RelativeLayout perfilProjeto;
    private LinearLayout menu;
    private String nomeProjeto;

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
        nodeProjeto = getArguments().getString("node_projeto");
        Log.d("DEV/PROJETOSPAGE", "node: "+nodeProjeto);
        nomeProjeto = getArguments().getString("nome_projeto");
        sharedPref = getActivity().getSharedPreferences("todoApp", 0);
        nomePET = sharedPref.getString("nome_meu_pet", "nada");
        dbProjeto = mDatabase.child("PETs").child(nomePET).child("projetos").child(nodeProjeto);
        Log.d("nomePETWOW", nomePET);

        return inflater.inflate(R.layout.projeto_page, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);

        perfilProjeto = getView().findViewById(R.id.perfil_projeto);
        menu = getView().findViewById(R.id.menu);
        perfilProjeto.setVisibility(View.GONE);
        menu.setVisibility(View.GONE);
        imagemProjeto = getView().findViewById(R.id.logo_projeto);
        meuProjeto = getView().findViewById(R.id.meu_projeto);
        nomeProjetos = getView().findViewById(R.id.nome_projeto);
        progressBar = getView().findViewById(R.id.progress_bar);
        menuEquipesClick = getView().findViewById(R.id.menu_equipes_click);
        menuReunioesClick = getView().findViewById(R.id.menu_reunioes_click);
        menuMembrosClick = getView().findViewById(R.id.menu_membros_click);
        menuEquipesClick.setOnClickListener(this);
        menuReunioesClick.setOnClickListener(this);
        menuMembrosClick.setOnClickListener(this);
        menuEquipes = getView().findViewById(R.id.menu_equipes);
        menuReunioes = getView().findViewById(R.id.menu_reunioes);
        corPadrao = menuReunioes.getTextColors();
        menuMembros = getView().findViewById(R.id.menu_membros);
        nomeProjetos.setText(nomeProjeto);

        setImagemProjeto();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.menu_equipes_click) {
            progressBar.setVisibility(View.GONE);
            menuEquipes.setTextColor(Color.parseColor("#03A9F4"));
            menuReunioes.setTextColor(corPadrao);
            menuMembros.setTextColor(corPadrao);
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("node_projeto", nodeProjeto);
            Fragment fragment= EquipesFragment.newInstance();
            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container_child, fragment);
            transaction.commit();
        }
        else if (id == R.id.menu_reunioes_click) {
            progressBar.setVisibility(View.GONE);
            menuEquipes.setTextColor(corPadrao);
            menuReunioes.setTextColor(Color.parseColor("#03A9F4"));
            menuMembros.setTextColor(corPadrao);
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("reunioes_path", "PETs/"+nomePET+"/projetos/"+nodeProjeto);
            bundle.putString("node_projeto", nodeProjeto);
            Fragment fragment= ReunioesFragment.newInstance();
            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container_child, fragment);
            transaction.commit();
        }
        else if (id == R.id.menu_membros_click) {
            progressBar.setVisibility(View.GONE);
            menuEquipes.setTextColor(corPadrao);
            menuReunioes.setTextColor(corPadrao);
            menuMembros.setTextColor(Color.parseColor("#03A9F4"));
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("membros_path", "PETs/"+nomePET+"/projetos/"+nodeProjeto);
            bundle.putString("origem", "projetos");
            Fragment fragment= MembrosFragment.newInstance();
            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container_child, fragment);
            transaction.commit();
        }
    }

    public void setImagemProjeto() {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        String nodeProjetoFoto = nodeProjeto;
        try {
            nodeProjetoFoto = nodeProjeto.replace(" ", "_");
        }
        catch (NullPointerException e) {

        }
        Log.d("ENTROU", nodeProjetoFoto);
        StorageReference perfilRef = storageRef.child("imagensProjetos/" + nodeProjetoFoto + ".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    Log.d("ENTROU", "foi");
                    Bitmap bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
//                    uriProjeto = getImageUri(getActivity(), bitmapPerfil);
                    imagemProjeto.setImageBitmap(bitmapPerfil);
                    progressBar.setVisibility(View.GONE);
                    perfilProjeto.setVisibility(View.VISIBLE);
                    menu.setVisibility(View.VISIBLE);
                    FragmentManager manager = getChildFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("node_projeto", nodeProjeto);
                    Fragment fragment = EquipesFragment.newInstance();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.fragment_container_child, fragment);
                    transaction.commit();
                }
                catch (IllegalStateException e) {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

}
