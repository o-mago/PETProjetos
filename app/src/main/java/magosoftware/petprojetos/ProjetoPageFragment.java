package magosoftware.petprojetos;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
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
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
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
    private CardView menu;
    private String nomeProjeto;
    private String nodePET;
    private ImageView drive;
    private ImageView config;
    private String nomeCoordenador;
    private ImageView newMembro;

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
        nodePET = sharedPref.getString("node_meu_pet", "nada");
        dbProjeto = mDatabase.child("PETs").child(nodePET).child("projetos").child(nodeProjeto);
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
        newMembro = getView().findViewById(R.id.new_membro);
        newMembro.setVisibility(View.GONE);
        drive = getView().findViewById(R.id.drive);
        drive.setOnClickListener(this);
        config = getView().findViewById(R.id.config);
        config.setOnClickListener(this);
        menuEquipesClick.setOnClickListener(this);
        menuReunioesClick.setOnClickListener(this);
        menuMembrosClick.setOnClickListener(this);
        menuEquipes = getView().findViewById(R.id.menu_equipes);
        menuReunioes = getView().findViewById(R.id.menu_reunioes);
        corPadrao = menuReunioes.getTextColors();
        menuMembros = getView().findViewById(R.id.menu_membros);
        nomeProjetos.setText(nomeProjeto);

        setImagemProjeto();
        verificaMembros();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.menu_equipes_click) {
            progressBar.setVisibility(View.GONE);
            menuEquipes.setTextColor(getResources().getColor(R.color.colorSecondary));
            menuReunioes.setTextColor(corPadrao);
            menuMembros.setTextColor(corPadrao);
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("node_projeto", nodeProjeto);
            bundle.putString("nome_projeto", nomeProjeto);
            bundle.putString("nome_coordenador", nomeCoordenador);
            Fragment fragment= EquipesFragment.newInstance();
            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container_child, fragment);
            transaction.commit();
        }
        else if (id == R.id.menu_reunioes_click) {
            progressBar.setVisibility(View.GONE);
            menuEquipes.setTextColor(corPadrao);
            menuReunioes.setTextColor(getResources().getColor(R.color.colorSecondary));
            menuMembros.setTextColor(corPadrao);
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("reunioes_path", "PETs/"+nodePET+"/projetos/"+nodeProjeto);
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
            menuMembros.setTextColor(getResources().getColor(R.color.colorSecondary));
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("membros_path", "PETs/"+nodePET+"/projetos/"+nodeProjeto);
            bundle.putString("origem", "projetos");
            Fragment fragment= MembrosFragment.newInstance();
            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container_child, fragment);
            transaction.commit();
        }
        else if (id == R.id.drive) {
            mDatabase.child("PETs").child(nodePET).child("projetos").child(nodeProjeto).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot listSnapshot : dataSnapshot.child("coordenador").getChildren()) {
                        nomeCoordenador = listSnapshot.getValue(String.class);
                    }
                    if(dataSnapshot.hasChild("drive")) {
                        String link = dataSnapshot.child("drive").getValue(String.class);
                        try {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            startActivity(browserIntent);
                        }
                        catch (ActivityNotFoundException e) {
                            Toast.makeText(getActivity(), "Link inválido",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ConfigActivity.class);
                            intent.putExtra("node", "PETs/"+nodePET+"/projetos/"+nodeProjeto);
                            startActivity(intent);
                        }
                    }
                    else {
                        Intent intent = new Intent(getActivity(), ConfigActivity.class);
                        intent.putExtra("node", "PETs/"+nodePET+"/projetos/"+nodeProjeto);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if (id == R.id.config) {
            Intent intent = new Intent(getActivity(), ConfigActivity.class);
            intent.putExtra("node", "PETs/"+nodePET+"/projetos/"+nodeProjeto);
            startActivity(intent);
        }
    }

    public void verificaMembros() {
        dbProjeto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("aguardando").exists()) {
                    newMembro.setVisibility(View.VISIBLE);
                } else {
                    newMembro.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("DEV/MEUPETFRAGMENT", "onPause()");
//        dbSituacaoPET.removeEventListener(valueEventListener);
        if (getChildFragmentManager().getBackStackEntryCount() > 0){
            // Get the fragment fragment manager - and pop the backstack
            getChildFragmentManager().popBackStack();
        }
    }

    public void setImagemProjeto() {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 8;
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
                    Bitmap bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                    uriProjeto = getImageUri(getActivity(), bitmapPerfil);
                    imagemProjeto.setImageBitmap(bitmapPerfil);
                    progressBar.setVisibility(View.GONE);
                    perfilProjeto.setVisibility(View.VISIBLE);
                    menu.setVisibility(View.VISIBLE);
                    FragmentManager manager = getChildFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString("node_projeto", nodeProjeto);
                    bundle.putString("nome_projeto", nomeProjeto);
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
