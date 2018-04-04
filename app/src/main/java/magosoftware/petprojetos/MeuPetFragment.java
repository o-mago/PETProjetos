package magosoftware.petprojetos;

import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    FrameLayout menuEventosClick;
    TextView menuEventos;
    ColorStateList corPadrao;

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

        imagemMeuPet = getView().findViewById(R.id.logo_pet);
        meuPET = getView().findViewById(R.id.meu_pet);
        nomeMeuPet = getView().findViewById(R.id.nome_pet);
        menuProjetosClick = getView().findViewById(R.id.menu_projetos_click);
        menuTarefasClick = getView().findViewById(R.id.menu_tarefas_click);
        menuEventosClick = getView().findViewById(R.id.menu_eventos_click);
        menuProjetosClick.setOnClickListener(this);
        menuTarefasClick.setOnClickListener(this);
        menuEventosClick.setOnClickListener(this);
        menuProjetos = getView().findViewById(R.id.menu_projetos);
        menuTarefas = getView().findViewById(R.id.menu_tarefas);
        corPadrao = menuTarefas.getTextColors();
        menuEventos = getView().findViewById(R.id.menu_eventos);
        getPET();
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_container_meu_pet, ProjetosFragment.newInstance());
        transaction.commit();
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
            menuEventos.setTextColor(corPadrao);
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_container_meu_pet, ProjetosFragment.newInstance());
            transaction.commit();
        }
        else if (id == R.id.menu_tarefas_click) {
            menuProjetos.setTextColor(corPadrao);
            menuTarefas.setTextColor(Color.parseColor("#03A9F4"));
            menuEventos.setTextColor(corPadrao);
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_container_meu_pet, TarefasFragment.newInstance());
            transaction.commit();
        }
        else if (id == R.id.menu_eventos_click) {
            menuProjetos.setTextColor(corPadrao);
            menuTarefas.setTextColor(corPadrao);
            menuEventos.setTextColor(Color.parseColor("#03A9F4"));
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_container_meu_pet, EventosFragment.newInstance());
            transaction.commit();
        }
    }

    private void semPet() {
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
        nomePET = sharedPref.getString("nome_meu_pet", "nada");
        condicaoPET = sharedPref.getString("condicao_meu_pet", "nada");
        Log.d("nomePET", nomePET);
        if(nomePET.equals("nada") || condicaoPET.equals("aguardando")) {
            semPet();
        }
        else {
            nomeMeuPet.setText(nomePET);
            setImagemPet();
        }
    }

    public void setImagemPet() {
        try {
            uriPet = Uri.parse(sharedPref.getString("uri_pet", null));
        }
        catch (NullPointerException e) {
            uriPet = null;
        }
        if(uriPet == null) {
            String nomePETFoto = nomePET.replace(" ", "_");
            Log.d("ENTROU", nomePETFoto);
            StorageReference perfilRef = storageRef.child("imagensPET/" + nomePETFoto + ".jpg");

            final long ONE_MEGABYTE = 1024 * 1024;
            perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Log.d("ENTROU", "foi");
                    bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    uriPet = getImageUri(getActivity(), bitmapPerfil);
                    editor.putString("uri_pet", uriPet.toString());
                    editor.commit();
                    imagemMeuPet.setImageBitmap(bitmapPerfil);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
        else {
            Log.d("ENTROU", "Picasso");
            Picasso.with(getActivity()).load(uriPet).into(imagemMeuPet);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


}
