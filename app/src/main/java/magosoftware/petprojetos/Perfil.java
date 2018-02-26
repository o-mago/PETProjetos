package magosoftware.petprojetos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by root on 19/02/18.
 */

public class Perfil extends BaseFragment implements View.OnClickListener {

    RecyclerView mRecyclerView;
    private LineAdapter mAdapter;
    private DatabaseReference dbUsuario;
    FirebaseUser user;
    FirebaseAuth mAuth;
    public static final int PICK_IMAGE = 1;
    CircularImageView imagemPerfil;
    TextView nick;
    TextView nome;
    TextView email;
    TextView curso;
    TextView universidade;
    TextView pet;
    TextView nascimento;
    FirebaseStorage storage;
    StorageReference storageRef;
    Bitmap bitmapPerfil;
    Uri uriPerfil;
    boolean update = false;
    public SharedPreferences sharedPref;
    public SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        dbUsuario = mDatabase.child("Usuarios");
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
//        File cacheDir = getDiskCacheDir(this, DISK_CACHE_SUBDIR);


        return inflater.inflate(R.layout.activity_perfil, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imagemPerfil = getView().findViewById(R.id.foto_perfil);
        nick = getView().findViewById(R.id.nick);
        nome = getView().findViewById(R.id.nome);
        email = getView().findViewById(R.id.email);
        curso = getView().findViewById(R.id.curso);
        universidade = getView().findViewById(R.id.universidade);
        pet = getView().findViewById(R.id.pet);
        nascimento = getView().findViewById(R.id.nascimento);
        imagemPerfil = getView().findViewById(R.id.foto_perfil);
        imagemPerfil.setOnClickListener(this);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        if (user != null) {
            preenchePerfil();
        } else {
            Log.d("ERRO", "Fudeu Bahia");
        }
    }

    public static Perfil newInstance() {
        Perfil perfilFragment = new Perfil();
        return perfilFragment;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.foto_perfil) {
            Log.d("UNI", "CLICOU");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            try {
                Uri selectedImage = data.getData();
                Intent intent = new Intent(getActivity(), AjustaImagem.class);
                intent.putExtra("imagem", selectedImage.toString());
                startActivity(intent);
            }
            catch (NullPointerException e) {

            }
        }
    }

    public void preenchePerfil() {
//        nome.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/nome"));
//        nick.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/nick"));
//        email.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/email"));
//        universidade.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/universidade"));
//        pet.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/pet"));
//        curso.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/curso"));
//        nascimento.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/nascimento"));
        String nickSP = sharedPref.getString(getString(R.string.nick_perfil), null);
        String nomeSP = sharedPref.getString(getString(R.string.nome_perfil), null);
        String emailSP = sharedPref.getString(getString(R.string.email_perfil), null);
        String cursoSP = sharedPref.getString(getString(R.string.curso_perfil), null);
        String universidadeSP = sharedPref.getString(getString(R.string.universidade_perfil), null);
        String petSP = sharedPref.getString(getString(R.string.pet_perfil), null);
        String nascimentoSP = sharedPref.getString(getString(R.string.nascimento_perfil), null);
        if(nomeSP == null || update) {
            mDatabase.child("Usuarios").child(user.getUid()).child("nome").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    nome.setText(dataSnapshot.getValue(String.class));
                    editor.putString(getString(R.string.nome_perfil), uriPerfil.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
            });
        }
        else {
            nome.setText(nomeSP);
        }
        if(emailSP == null || update) {
            mDatabase.child("Usuarios").child(user.getUid()).child("email").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    email.setText(dataSnapshot.getValue(String.class));
                    editor.putString(getString(R.string.email_perfil), uriPerfil.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
            });
        }
        else {
            email.setText(emailSP);
        }
        if(cursoSP == null || update) {
            mDatabase.child("Usuarios").child(user.getUid()).child("curso").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    curso.setText(dataSnapshot.getValue(String.class));
                    editor.putString(getString(R.string.curso_perfil), uriPerfil.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
            });
        }
        else {
            curso.setText(cursoSP);
        }

        if(universidadeSP == null || update) {
            mDatabase.child("Usuarios").child(user.getUid()).child("universidade").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    universidade.setText(dataSnapshot.getValue(String.class));
                    editor.putString(getString(R.string.universidade_perfil), uriPerfil.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
            });
        }
        else {
            universidade.setText(universidadeSP);
        }

        if(nascimentoSP == null || update) {
            mDatabase.child("Usuarios").child(user.getUid()).child("nascimento").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    nascimento.setText(dataSnapshot.getValue(String.class));
                    editor.putString(getString(R.string.nascimento_perfil), uriPerfil.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
            });
        }
        else {
            nascimento.setText(nascimentoSP);
        }

        if(nickSP == null || update) {
            mDatabase.child("Usuarios").child(user.getUid()).child("nick").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    nick.setText(dataSnapshot.getValue(String.class));
                    editor.putString(getString(R.string.nick_perfil), uriPerfil.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
            });
        }
        else {
            nick.setText(nickSP);
        }

        if(petSP == null || update) {
            mDatabase.child("Usuarios").child(user.getUid()).child("pet").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pet.setText(dataSnapshot.getValue(String.class));
                    editor.putString(getString(R.string.pet_perfil), uriPerfil.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
            });
        }
        else {
            pet.setText(petSP);
        }

        try {
            uriPerfil = Uri.parse(sharedPref.getString(getString(R.string.uri_perfil), null));
        }
        catch (NullPointerException e) {
            uriPerfil = null;
        }
        if(uriPerfil != null) {
            Picasso.with(getActivity()).load(uriPerfil).into(imagemPerfil);
        }
        else {
            StorageReference perfilRef = storageRef.child("imagensPerfil/" + user.getUid() + ".jpg");

            final long ONE_MEGABYTE = 1024 * 1024;
            perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    uriPerfil = getImageUri(getActivity(), bitmapPerfil);
                    editor.putString(getString(R.string.uri_perfil), uriPerfil.toString());
                    editor.commit();
                    imagemPerfil.setImageBitmap(bitmapPerfil);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


//    protected void feedList(String db, final RecyclerView recycler) {
//        dbUsuario.child(db).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                final List<String> list = new ArrayList<String>();
//                Log.d("UNI", "ON DATA CHANGED OK");
//                for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
//                    String nome = listSnapshots.getValue(String.class);
//                    Log.d("UNI", nome);
//                    list.add(nome);
//                }
//
//                // Configurando o gerenciador de layout para ser uma lista.
//                LinearLayoutManager layoutManager = new LinearLayoutManager(Perfil.this);
//                recycler.setLayoutManager(layoutManager);
//
//                // Adiciona o adapter que irá anexar os objetos à lista.
//                // Está sendo criado com lista vazia, pois será preenchida posteriormente.
//                mAdapter = new LineAdapter(list);
//                recycler.setAdapter(mAdapter);
//
//                // Configurando um dividr entre linhas, para uma melhor visualização.
//                recycler.addItemDecoration(
//                        new DividerItemDecoration(Perfil.this, DividerItemDecoration.VERTICAL));
//
//                // Creating adapter for spinner
////                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Perfil.this, android.R.layout.simple_spinner_item, list);
//
//                // Drop down layout style - list view with radio button
////                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//                // attaching data adapter to spinner
////                spinner.setAdapter(dataAdapter);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("UNI", "Deu merda");
//            }
//        });
//
//    }
}