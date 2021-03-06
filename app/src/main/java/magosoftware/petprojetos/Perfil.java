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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import com.squareup.picasso.RequestCreator;

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
    String codigo;
    boolean update = false;
    public SharedPreferences sharedPref;
    public SharedPreferences.Editor editor;
    private String nomePET;
    private Picasso.Listener builderListener;
    private Picasso.Builder builder;
    private String nodePET;
    private ScrollView mainPerfil;
    private ProgressBar progressBar;
    private ImageButton editButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        dbUsuario = mDatabase.child("Usuarios");
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        sharedPref = getActivity().getSharedPreferences("todoApp", 0);
        nomePET = sharedPref.getString("nome_meu_pet", null);
        nodePET = sharedPref.getString("node_meu_pet", null);
        try {
            codigo = getArguments().getString("codigo");
        }
        catch (NullPointerException e) {
            codigo = user.getUid();
        }
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
        getView().findViewById(R.id.edit_button).setOnClickListener(this);
        nascimento = getView().findViewById(R.id.nascimento);
        imagemPerfil = getView().findViewById(R.id.foto_perfil);
        imagemPerfil.setOnClickListener(this);
        mainPerfil = getView().findViewById(R.id.main_perfil);
        progressBar = getView().findViewById(R.id.progress_bar);
        editButton = getView().findViewById(R.id.edit_button);
        mainPerfil.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
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
            Intent intent = new Intent(getActivity(), AdicionaImagem.class);
//                intent.putExtra("imagem", selectedImage.toString());
            intent.putExtra("caminho", "imagensPerfil/"+user.getUid()+".jpg");
            intent.putExtra("tipo", "usuario");
            startActivity(intent);
//            Log.d("UNI", "CLICOU");
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
        if(i == R.id.edit_button) {
            Intent intent = new Intent(getActivity(), PerfilEdit.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            try {
                Uri selectedImage = data.getData();
                Intent intent = new Intent(getActivity(), AdicionaImagem.class);
//                intent.putExtra("imagem", selectedImage.toString());
                intent.putExtra("caminho", "imagensPerfil/"+user.getUid()+".jpg");
                intent.putExtra("tipo", "usuario");
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
        if(nomeSP == null || emailSP == null || cursoSP == null || universidadeSP == null || nascimentoSP == null || nickSP == null || update) {
            mDatabase.child("Usuarios").child(codigo).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    nome.setText(dataSnapshot.child("nome").getValue(String.class));
                    editor.putString("nome_perfil", dataSnapshot.child("nome").getValue(String.class));
                    email.setText(dataSnapshot.child("email").getValue(String.class));
                    editor.putString("email_perfil", dataSnapshot.child("email").getValue(String.class));
                    curso.setText(dataSnapshot.child("curso").getValue(String.class));
                    editor.putString("curso_perfil", dataSnapshot.child("curso").getValue(String.class));
                    universidade.setText(dataSnapshot.child("universidade").getValue(String.class));
                    editor.putString("universidade_perfil", dataSnapshot.child("universidade").getValue(String.class));
                    nascimento.setText(dataSnapshot.child("nascimento").getValue(String.class));
                    editor.putString("nascimento_perfil", dataSnapshot.child("nascimento").getValue(String.class));
                    nick.setText(dataSnapshot.child("nick").getValue(String.class));
                    editor.putString("nick_perfil", dataSnapshot.child("nick").getValue(String.class));
                    if(dataSnapshot.hasChild("update")) {
                        if(dataSnapshot.child("update").getValue(Boolean.class).equals(true)) {
                            update = true;
                            setImagemPerfil(update);
                            mDatabase.child("Usuarios").child(codigo).child("update").setValue(false);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
            });
        }
        else {
            nome.setText(nomeSP);
            email.setText(emailSP);
            curso.setText(cursoSP);
            universidade.setText(universidadeSP);
            nascimento.setText(nascimentoSP);
            nick.setText(nickSP);
        }
//        if(emailSP == null || update) {
//            mDatabase.child("Usuarios").child(codigo).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    email.setText(dataSnapshot.getValue(String.class));
//                    editor.putString("email_perfil", dataSnapshot.getValue(String.class));
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d("UNI", "Deu merda");
//                }
//            });
//        }
//        else {
//            email.setText(emailSP);
//        }
//        if(cursoSP == null || update) {
//            mDatabase.child("Usuarios").child(codigo).child("curso").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    curso.setText(dataSnapshot.getValue(String.class));
//                    editor.putString("curso_perfil", dataSnapshot.getValue(String.class));
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d("UNI", "Deu merda");
//                }
//            });
//        }
//        else {
//            curso.setText(cursoSP);
//        }

//        if(universidadeSP == null || update) {
//            mDatabase.child("Usuarios").child(codigo).child("universidade").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    universidade.setText(dataSnapshot.getValue(String.class));
//                    editor.putString("universidade_perfil", dataSnapshot.getValue(String.class));
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d("UNI", "Deu merda");
//                }
//            });
//        }
//        else {
//            universidade.setText(universidadeSP);
//        }

//        if(nascimentoSP == null || update) {
//            mDatabase.child("Usuarios").child(codigo).child("nascimento").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    nascimento.setText(dataSnapshot.getValue(String.class));
//                    editor.putString("nascimento_perfil", dataSnapshot.getValue(String.class));
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d("UNI", "Deu merda");
//                }
//            });
//        }
//        else {
//            nascimento.setText(nascimentoSP);
//        }

//        if(nickSP == null || update) {
//            mDatabase.child("Usuarios").child(codigo).child("nick").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    nick.setText(dataSnapshot.getValue(String.class));
//                    editor.putString("nick_perfil", dataSnapshot.getValue(String.class));
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                    Log.d("UNI", "Deu merda");
//                }
//            });
//        }
//        else {
//            nick.setText(nickSP);
//        }

        if(petSP == null || update) {
            mDatabase.child("Usuarios").child(codigo).child("pet").child(nodePET).child("situacao").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (!dataSnapshot.getValue(String.class).equals("aguardando")) {
                            pet.setText(nomePET);
                        }
                    }
                    catch (NullPointerException e) {

                    }
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
        setImagemPerfil(update);
    }

    private void setImagemPerfil(boolean atualiza) {
//        if(!atualiza) {
//            builder = new Picasso.Builder(getActivity());
//            builderListener = new Picasso.Listener() {
//                @Override
//                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
//                    uriPerfil = null;
//                    StorageReference perfilRef = storageRef.child("imagensPerfil/" + codigo + ".jpg");
//
//                    final long ONE_MEGABYTE = 1024 * 1024;
//                    perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                        @Override
//                        public void onSuccess(byte[] bytes) {
//                            bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                            uriPerfil = getImageUri(getActivity(), bitmapPerfil);
//                            editor.putString("uri_perfil", uriPerfil.toString());
//                            editor.commit();
//                            imagemPerfil.setImageBitmap(bitmapPerfil);
//                            editButton.setVisibility(View.VISIBLE);
//                            mainPerfil.setVisibility(View.VISIBLE);
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            // Handle any errors
//                        }
//                    });
//                }
//            };
//            builder.listener(builderListener);
//            try {
//                uriPerfil = Uri.parse(sharedPref.getString("uri_perfil", null));
//                builder.build().load(uriPerfil).into(imagemPerfil);
//                editButton.setVisibility(View.VISIBLE);
//                mainPerfil.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);
//            } catch (NullPointerException e) {
//                Log.d("DEV/PERFIL", "Deu merda aqui");
//            }
//        }
//        else {
        update = false;
        uriPerfil = null;
        StorageReference perfilRef = storageRef.child("imagensPerfil/" + codigo + ".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                uriPerfil = getImageUri(getActivity(), bitmapPerfil);
                editor.putString("uri_perfil", uriPerfil.toString());
                editor.commit();
                imagemPerfil.setImageBitmap(bitmapPerfil);
                editButton.setVisibility(View.VISIBLE);
                mainPerfil.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        builder.remove
//    }


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