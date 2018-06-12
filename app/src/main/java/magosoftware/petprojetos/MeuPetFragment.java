package magosoftware.petprojetos;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
 * Created by root on 22/02/18.
 */

public class MeuPetFragment extends BaseFragment implements View.OnClickListener {

    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageRef;
    String nomePET;
    String condicaoPET;
    public SharedPreferences sharedPref;
    public SharedPreferences.Editor editor;
    Uri uriPet;
    Bitmap bitmapPerfil;
    CircularImageView imagemMeuPet;
    RelativeLayout meuPET;
    TextView aviso;
    TextView nomeMeuPet;
    FrameLayout menuProjetosClick;
    TextView menuProjetos;
    FrameLayout menuTarefasClick;
    TextView menuTarefas;
    FrameLayout menuMembrosClick;
    TextView menuMembros;
    ColorStateList corPadrao;
    DatabaseReference dbSituacaoPET;
    ValueEventListener valueEventListener;
    private RelativeLayout perfilPet;
    private LinearLayout menu;
    private String nodePET;
    private ProgressBar progressBar;
    private ImageView drive;
    private ImageView config;

    public static MeuPetFragment newInstance() {
        MeuPetFragment meuPetFragment = new MeuPetFragment();
        return meuPetFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        sharedPref = getActivity().getSharedPreferences("todoApp", 0);
        editor = sharedPref.edit();
//        File cacheDir = getDiskCacheDir(this, DISK_CACHE_SUBDIR);
        return inflater.inflate(R.layout.meu_pet, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        perfilPet = getView().findViewById(R.id.perfil_pet);
        menu = getView().findViewById(R.id.menu);
        perfilPet.setVisibility(View.GONE);
        perfilPet.setOnClickListener(this);
        menu.setVisibility(View.GONE);
        imagemMeuPet = getView().findViewById(R.id.logo_pet);
        meuPET = getView().findViewById(R.id.meu_pet);
        nomeMeuPet = getView().findViewById(R.id.nome_pet);
        menuProjetosClick = getView().findViewById(R.id.menu_projetos_click);
        menuTarefasClick = getView().findViewById(R.id.menu_tarefas_click);
        menuMembrosClick = getView().findViewById(R.id.menu_membros_click);
        drive = getView().findViewById(R.id.drive);
        drive.setOnClickListener(this);
        config = getView().findViewById(R.id.config);
        config.setOnClickListener(this);
        menuProjetosClick.setOnClickListener(this);
        menuTarefasClick.setOnClickListener(this);
        menuMembrosClick.setOnClickListener(this);
        menuProjetos = getView().findViewById(R.id.menu_projetos);
        menuTarefas = getView().findViewById(R.id.menu_tarefas);
        corPadrao = menuTarefas.getTextColors();
        menuMembros = getView().findViewById(R.id.menu_membros);
        progressBar = getView().findViewById(R.id.progress_bar);
        dbSituacaoPET = mDatabase.child("Usuarios").child(user.getUid()).child("pet");
        getPET();
//        if(nomePET.equals("nada")) {
//            semPet();
//        }
//        else {
//
//        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.menu_projetos_click) {
            menuProjetos.setTextColor(Color.parseColor("#03A9F4"));
            menuTarefas.setTextColor(corPadrao);
            menuMembros.setTextColor(corPadrao);
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_container_child, ProjetosFragment.newInstance());
            transaction.commit();
        }
        else if(id == R.id.perfil_pet) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("nome", nomePET);
            bundle.putString("node", nodePET);
            Fragment fragment = PerfilPetFragment.newInstance();
            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        }
        else if (id == R.id.menu_tarefas_click) {
            menuProjetos.setTextColor(corPadrao);
            menuTarefas.setTextColor(Color.parseColor("#03A9F4"));
            menuMembros.setTextColor(corPadrao);
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("tarefa_path", "PETs/"+nodePET);
            bundle.putString("nome_projeto", nomePET);
            bundle.putString("node_pet", nodePET);
            Fragment fragment = TarefasConcentradas.newInstance();
            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container_child, fragment);
            transaction.commit();
        }
        else if (id == R.id.menu_membros_click) {
            menuProjetos.setTextColor(corPadrao);
            menuTarefas.setTextColor(corPadrao);
            menuMembros.setTextColor(Color.parseColor("#03A9F4"));
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putString("membros_path", "PETs/"+nodePET+"/time");
            bundle.putString("origem", "meupet");
            Fragment fragment = MembrosFragment.newInstance();
            fragment.setArguments(bundle);
            transaction.replace(R.id.fragment_container_child, fragment);
            transaction.commit();
        }
        else if (id == R.id.drive) {
            mDatabase.child("PETs").child(nodePET).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
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
                            intent.putExtra("node", "PETs/"+nodePET);
                            startActivity(intent);
                        }
                    }
                    else {
                        Intent intent = new Intent(getActivity(), ConfigActivity.class);
                        intent.putExtra("node", "PETs/"+nodePET);
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
            intent.putExtra("node", "PETs/"+nodePET);
            startActivity(intent);
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        FragmentManager manager = getChildFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.replace(R.id.fragment_container_child, ProjetosFragment.newInstance());
//        transaction.commit();
//    }

    private void semPet() {
        progressBar.setVisibility(View.GONE);
        meuPET.removeAllViews();
        aviso = new TextView(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        aviso.setLayoutParams(params);
        aviso.setText("Você ainda não está em um PET");
        aviso.setGravity(Gravity.CENTER);
        aviso.setTextSize(20);
        meuPET.addView(aviso);
    }

    private void getPET() {
        Log.d("DEV/MEUPET", "getPET");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DEV/MEUPET", "Listener");
                if(!dataSnapshot.hasChildren()) {
                    semPet();
                }
                else {
                    for (DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                        Log.d("DEV/MEUPET", "datasnapshoot");
                        try {
                            Log.d("DEV/MEUPET", "try");
                            nodePET = listSnapshot.getKey();
                            condicaoPET = listSnapshot.child("situacao").getValue(String.class);
                            mDatabase.child("PETs").child(nodePET).addListenerForSingleValueEvent(new ValueEventListenerSend(condicaoPET) {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    nomePET = dataSnapshot.child("nome").getValue(String.class);
                                    if (variavel.equals("aguardando")) {
                                        semPet();
                                    } else {
                                        try {
                                            nomeMeuPet.setText(nomePET);
                                            setImagemPet();
                                        }
                                        catch (NullPointerException e) {

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } catch (IllegalStateException e) {
                            Log.d("DEV/MEUPET", "catch");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbSituacaoPET.addListenerForSingleValueEvent(valueEventListener);
    }

    public void setImagemPet() {
//        try {
//            uriPet = Uri.parse(sharedPref.getString("uri_pet", null));
//        }
//        catch (NullPointerException e) {
//            uriPet = null;
//        }
//        if(uriPet == null) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 8;
//        String nomePETFoto = nomePET.replace(" ", "_");
//        Log.d("ENTROU", nomePETFoto);
        StorageReference perfilRef = storageRef.child("imagensPET/" + nodePET + ".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    try {
                        Log.d("ENTROU", "foi");
                        bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                uriPet = getImageUri(getActivity(), bitmapPerfil);
//                editor.putString("uri_pet", uriPet.toString());
//                editor.commit();
                        imagemMeuPet.setImageBitmap(bitmapPerfil);
                        perfilPet.setVisibility(View.VISIBLE);
                        menu.setVisibility(View.VISIBLE);
                        selecionaChildFragment();
//                        FragmentManager manager = getChildFragmentManager();
//                        FragmentTransaction transaction = manager.beginTransaction();
//                        transaction.replace(R.id.fragment_container_child, ProjetosFragment.newInstance());
//                        transaction.commit();
                    } catch (NullPointerException e) {
                        selecionaChildFragment();
//                        FragmentManager manager = getChildFragmentManager();
//                        FragmentTransaction transaction = manager.beginTransaction();
//                        transaction.replace(R.id.fragment_container_child, ProjetosFragment.newInstance());
//                        transaction.commit();
                    }
                    progressBar.setVisibility(View.GONE);
                }
                catch (IllegalStateException ise) {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
//        }
//        else {
//            Log.d("ENTROU", "Picasso");
//            Picasso.with(getActivity()).load(uriPet).into(imagemMeuPet);
//        }
    }

    private void selecionaChildFragment() {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment;
        String nomeFragment = sharedPref.getString("fragment", "ProjetosFragment");
        Log.d("DEV/MEUPETFRAGMENT", "nomeFragment= "+nomeFragment);
        if(nomeFragment.equals("ProjetosFragment")) {
            menuProjetos.setTextColor(Color.parseColor("#03A9F4"));
            menuTarefas.setTextColor(corPadrao);
            menuMembros.setTextColor(corPadrao);
            fragment = ProjetosFragment.newInstance();
        }
        else if(nomeFragment.equals("TarefasConcentradas")) {
            menuProjetos.setTextColor(corPadrao);
            menuTarefas.setTextColor(Color.parseColor("#03A9F4"));
            menuMembros.setTextColor(corPadrao);
            Bundle bundle = new Bundle();
            bundle.putString("tarefa_path", "PETs/"+nodePET);
            bundle.putString("nome_projeto", nomePET);
            bundle.putString("node_pet", nodePET);
            fragment = TarefasConcentradas.newInstance();
            fragment.setArguments(bundle);
        }
        else if(nomeFragment.equals("MembrosFragment")) {
            menuProjetos.setTextColor(corPadrao);
            menuTarefas.setTextColor(corPadrao);
            menuMembros.setTextColor(Color.parseColor("#03A9F4"));
            Bundle bundle = new Bundle();
            bundle.putString("membros_path", "PETs/"+nodePET+"/time");
            bundle.putString("origem", "meupet");
            fragment = MembrosFragment.newInstance();
            fragment.setArguments(bundle);
        }
        else {
            menuProjetos.setTextColor(Color.parseColor("#03A9F4"));
            menuTarefas.setTextColor(corPadrao);
            menuMembros.setTextColor(corPadrao);
            fragment = ProjetosFragment.newInstance();
        }
        transaction.replace(R.id.fragment_container_child, fragment);
        transaction.commit();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

//    @Override
//    public void onDestroyView() {
//        super.onDetach();
//        FragmentManager manager = getChildFragmentManager();
//        for(Fragment fragment : manager.getFragments()) {
//            FragmentTransaction transaction = manager.beginTransaction();
//            transaction.remove(fragment);
//            transaction.commit();
//        }
//        manager.popBackStack();
//    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("DEV/MEUPETFRAGMENT", "onPause()");
        dbSituacaoPET.removeEventListener(valueEventListener);
//        if (getChildFragmentManager().getBackStackEntryCount() > 0){
            // Get the fragment fragment manager - and pop the backstack
        try {
            editor.putString("fragment", getChildFragmentManager().getFragments().get(0).toString().split("\\{")[0]);
            editor.commit();
        }
        catch (IndexOutOfBoundsException e) {

        }
            getChildFragmentManager().popBackStack();
//        }
    }
//
    @Override
    public void onResume() {
        super.onResume();
        Log.d("DEV/MEUPETFRAGMENT", "onResume()");
//        getActivity().getSupportFragmentManager().popBackStack();
        dbSituacaoPET.addListenerForSingleValueEvent(valueEventListener);
//        FragmentManager manager = getChildFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        Fragment fragment;
//        String nomeFragment = sharedPref.getString("fragment", "ProjetosFragment");
//        Log.d("DEV/MEUPETFRAGMENT", "nomeFragment= "+nomeFragment);
//        if(nomeFragment.equals("ProjetosFragment")) {
//            fragment = ProjetosFragment.newInstance();
//        }
//        else if(nomeFragment.equals("TarefasConcentradas")) {
//            Bundle bundle = new Bundle();
//            bundle.putString("tarefa_path", "PETs/"+nodePET);
//            bundle.putString("nome_projeto", nomePET);
//            bundle.putString("node_pet", nodePET);
//            fragment = TarefasConcentradas.newInstance();
//            fragment.setArguments(bundle);
//        }
//        else if(nomeFragment.equals("MembrosFragment")) {
//            Bundle bundle = new Bundle();
//            bundle.putString("membros_path", "PETs/"+nodePET+"/time");
//            bundle.putString("origem", "meupet");
//            fragment = MembrosFragment.newInstance();
//            fragment.setArguments(bundle);
//        }
//        else {
//            fragment = ProjetosFragment.newInstance();
//        }
//        transaction.replace(R.id.fragment_container_child, fragment);
//        transaction.commit();
//    }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        user = null;
        mAuth = null;
        storage = null;
        storageRef = null;
        nomePET = null;
        condicaoPET = null;
        sharedPref = null;
        editor = null;
        uriPet = null;
        bitmapPerfil = null;
        imagemMeuPet = null;
        meuPET = null;
        aviso = null;
        menuMembrosClick.setOnClickListener(null);
        menuProjetosClick.setOnClickListener(null);
        menuTarefasClick.setOnClickListener(null);
        dbSituacaoPET.removeEventListener(valueEventListener);
        nomeMeuPet = null;
        menuProjetosClick = null;
        menuProjetos = null;
        menuTarefasClick = null;
        menuTarefas = null;
        menuMembrosClick = null;
        menuMembros = null;
        corPadrao = null;
        dbSituacaoPET = null;
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.replace(R.id.fragment_container_child, ProjetosFragment.newInstance());
//        transaction.commit();
    }
}
