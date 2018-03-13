package magosoftware.petprojetos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import iammert.com.expandablelib.ExpandCollapseListener;
import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;

/**
 * Created by root on 02/03/18.
 */

public class PerfilPetFragment extends BaseFragment implements View.OnClickListener {

    RecyclerView mRecyclerView;
    private LineAdapterUsuarios mAdapter;
    private DatabaseReference dbUsuario;
    private DatabaseReference dbBolsistas;
    private DatabaseReference dbOficiais;
    private DatabaseReference dbVoluntarios;
    private ValueEventListener velBolsistas;
    private ValueEventListener velOficiais;
    private ValueEventListener velVoluntarios;
    FirebaseUser user;
    FirebaseAuth mAuth;
    public static final int PICK_IMAGE = 1;
    CircularImageView imagemPerfil;
    TextView site;
    TextView nome;
    TextView email;
    TextView curso;
    TextView universidade;
    TextView localizacao;
    TextView ano;
    FirebaseStorage storage;
    StorageReference storageRef;
    Bitmap bitmapPerfil;
    ImageButton editButton;
    Uri uriPet;
    boolean update = true;
    public SharedPreferences sharedPref;
    public SharedPreferences.Editor editor;
    String nomePet;
    String cidadeEstado;
    Button follow;
    ExpandableLayout expandableLayout;
    String usuarios;
    int contPetianos=0;
    private List<Usuario> mModels;
    Drawable bitmapDrawablePet;
    final long ONE_MEGABYTE = 1024 * 1024;
    Section<String, Usuario> section = new Section<>();
    Boolean temPET = false;
    String nomeOldPET = "";
    String situacaoPET = "";
    Boolean tenhoCerteza = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        dbUsuario = mDatabase.child("Usuarios");
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        nomePet = getArguments().getString("nome");
        dbBolsistas = mDatabase.child("PETs").child(nomePet).child("petianos").child("bolsistas");
        dbOficiais = mDatabase.child("PETs").child(nomePet).child("petianos").child("oficiais");
        dbVoluntarios = mDatabase.child("PETs").child(nomePet).child("petianos").child("voluntarios");
//        File cacheDir = getDiskCacheDir(this, DISK_CACHE_SUBDIR);


        return inflater.inflate(R.layout.activity_perfil_pet, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imagemPerfil = getView().findViewById(R.id.foto_pet);
        site = getView().findViewById(R.id.site);
        nome = getView().findViewById(R.id.nome);
        email = getView().findViewById(R.id.email);
        curso = getView().findViewById(R.id.curso);
        universidade = getView().findViewById(R.id.universidade);
        localizacao = getView().findViewById(R.id.localizacao);
        editButton = getView().findViewById(R.id.edit_button);
        setupEdit();
        follow = getView().findViewById(R.id.follow);
        follow.setOnClickListener(this);
        setupFollow("aguardando", "AGUARDANDO","#FFFF00", getResources().getDrawable(R.drawable.background_contorno_aguardando));
        setupFollow("bolsistas", "PETIANO","#00E676", getResources().getDrawable(R.drawable.background_contorno_ok));
        setupFollow("oficiais", "PETIANO","#00E676", getResources().getDrawable(R.drawable.background_contorno_ok));
        setupFollow("voluntarios", "PETIANO","#00E676", getResources().getDrawable(R.drawable.background_contorno_ok));
        getView().findViewById(R.id.edit_button).setOnClickListener(this);
        ano = getView().findViewById(R.id.ano);
        expandableLayout = getView().findViewById(R.id.petianos);
//        setupRecycler();
//        mModels = new ArrayList<>();
        setupExpandable();
        imagemPerfil = getView().findViewById(R.id.foto_pet);
        imagemPerfil.setOnClickListener(this);
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        if (user != null) {
            preenchePerfil();
        } else {
            Log.d("ERRO", "Fudeu Bahia");
        }
    }

    public static PerfilPetFragment newInstance() {
        PerfilPetFragment perfilPetFragment = new PerfilPetFragment();
        return perfilPetFragment;
    }

    public void setupEdit() {
        dbUsuario.child(user.getUid()).child("pet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String codigo;
                for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                    String condicao = listSnapshots.getValue(String.class);
                    if(condicao.equals("bolsistas") || condicao.equals("oficiais") || condicao.equals("voluntarios")) {
                        editButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });

    }

    public void setupFollow(String categoria, final String mensagem, final String cor, final Drawable fundo) {
        mDatabase.child("PETs").child(nomePet).child("petianos/"+categoria).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String codigo;
                for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                    codigo = listSnapshots.getKey();
                    if(codigo.equals(user.getUid())) {
                        follow.setText(mensagem);
                        follow.setTextColor(Color.parseColor(cor));
                        follow.setBackgroundDrawable(fundo);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.foto_pet) {
            Log.d("UNI", "CLICOU");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
        if(i == R.id.edit_button) {
            Intent intent = new Intent(getActivity(), AdicionaImagem.class);
            startActivity(intent);
        }
        if(i == R.id.follow) {
            dbUsuario.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("pet")) {
                        temPET = true;
                        for (DataSnapshot listSnapshots : dataSnapshot.child("pet").getChildren()) {
                            nomeOldPET = listSnapshots.getKey();
                            situacaoPET = listSnapshots.getValue(String.class);
                        }
                    }
                    else {
                        temPET = false;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
            });
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Selecione seu status:");
            builder.setItems(getResources().getStringArray(R.array.opcoes_petiano), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(temCerteza()) {
                        if(temPET) {
                            Map<String, String> novoPetiano = new HashMap<>();
                            novoPetiano.put(user.getUid(), "bolsistas");
                            mDatabase.child("PETs").child(nomeOldPET).child("petianos").child(situacaoPET).removeValue();
                        }
                        Log.d("WHICH", Integer.toString(which));
                        if (which == 0) {
                            Map<String, String> novoPetiano = new HashMap<>();
                            novoPetiano.put(user.getUid(), "bolsistas");
                            mDatabase.child("PETs").child(nomePet).child("petianos").child("aguardando").setValue(novoPetiano);
                        }
                        if (which == 1) {
                            Map<String, String> novoPetiano = new HashMap<>();
                            novoPetiano.put(user.getUid(), "oficiais");
                            mDatabase.child("PETs").child(nomePet).child("petianos").child("aguardando").setValue(novoPetiano);
                        }
                        if (which == 2) {
                            Map<String, String> novoPetiano = new HashMap<>();
                            novoPetiano.put(user.getUid(), "voluntarios");
                            mDatabase.child("PETs").child(nomePet).child("petianos").child("aguardando").setValue(novoPetiano);
                        }
                        Map<String, String> pet = new HashMap<>();
                        pet.put(nomePet, "aguardando");
                        dbUsuario.child(user.getUid()).child("pet").setValue(pet);
                        setupFollow("aguardando", "AGUARDANDO", "#FFFF00", getResources().getDrawable(R.drawable.background_contorno_aguardando));
                    }
                }
            });
            builder.show();
        }
    }

    public Boolean temCerteza() {
        if(temPET) {
            final AlertDialog.Builder builderCerteza = new AlertDialog.Builder(getActivity());
            builderCerteza.setTitle("Você tem certeza que deseja sair de "+nomeOldPET+"?");
            builderCerteza.setItems(getResources().getStringArray(R.array.tem_certeza), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d("WHICH", Integer.toString(which));
                    if (which == 0) {
                        tenhoCerteza = true;
                        Map<String, String> pet = new HashMap<>();
                        pet.put(nomePet, "aguardando");
                        dbUsuario.child(user.getUid()).child("pet").setValue(pet);
                        setupFollow("aguardando", "AGUARDANDO", "#FFFF00", getResources().getDrawable(R.drawable.background_contorno_aguardando));
                    }
                    if (which == 1) {
                        tenhoCerteza = false;
                    }
                }
            });
            builderCerteza.show();
        }
        return tenhoCerteza;
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
//        nome.setText((String)DataBaseMethods.getData("PETs/"+user.getUid()+"/nome"));
//        nick.setText((String)DataBaseMethods.getData("PETs/"+user.getUid()+"/nick"));
//        email.setText((String)DataBaseMethods.getData("PETs/"+user.getUid()+"/email"));
//        universidade.setText((String)DataBaseMethods.getData("PETs/"+user.getUid()+"/universidade"));
//        pet.setText((String)DataBaseMethods.getData("PETs/"+user.getUid()+"/pet"));
//        curso.setText((String)DataBaseMethods.getData("PETs/"+user.getUid()+"/curso"));
//        nascimento.setText((String)DataBaseMethods.getData("PETs/"+user.getUid()+"/nascimento"));
        String siteSP = sharedPref.getString("site_pet", null);
        String nomeSP = sharedPref.getString("nome_pet", null);
        String emailSP = sharedPref.getString("email_pet", null);
        String cursoSP = sharedPref.getString("cursos_pet", null);
        String universidadeSP = sharedPref.getString("universidade_pet", null);
        String localizacaoSP = sharedPref.getString("localizacao_pet", null);
        final String anoSP = sharedPref.getString("ano_pet", null);
        if(nomeSP == null || update) {
            mDatabase.child("PETs").child(nomePet).child("nome").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    nome.setText(dataSnapshot.getValue(String.class));
                    editor.putString("nome_pet", dataSnapshot.getValue(String.class));
                    editor.commit();
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
            mDatabase.child("PETs").child(nomePet).child("email").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    email.setText(dataSnapshot.getValue(String.class));
                    editor.putString("email_pet" , dataSnapshot.getValue(String.class));
                    editor.commit();
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
            mDatabase.child("PETs").child(nomePet).child("cursos").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String cursosConcat = "";
                    //String[] cursos;
                    Map<String,Object> map = (Map<String,Object>)dataSnapshot.getValue();
                    String[] cursosArray = new String[10];
                    int i=0;
                    //cursos = Arrays.copyOf(map.values().toArray(), map.values().toArray().length, String[].class);
                    for(Map.Entry<String, Object> entry : map.entrySet()) {
                        cursosArray[i] = entry.getValue().toString();
                        cursosConcat = cursosConcat.concat(entry.getValue().toString()).concat(", ");
                        i++;
                    }
                    cursosConcat = cursosConcat.replace((cursosArray[i-1]+", "), cursosArray[i-1]);
                    curso.setText(cursosConcat);
                    editor.putString("cursos_pet", cursosConcat);
                    editor.commit();
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
            mDatabase.child("PETs").child(nomePet).child("universidade").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    universidade.setText(dataSnapshot.getValue(String.class));
                    editor.putString("universidade_pet", dataSnapshot.getValue(String.class));
                    editor.commit();
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

        if(anoSP == null || update) {
            mDatabase.child("PETs").child(nomePet).child("ano").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ano.setText(dataSnapshot.getValue(String.class));
                    editor.putString("ano_pet", dataSnapshot.getValue(String.class));
                    editor.commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
            });
        }
        else {
            ano.setText(anoSP);
        }

        if(siteSP == null || update) {
            mDatabase.child("PETs").child(nomePet).child("site").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    site.setText(dataSnapshot.getValue(String.class));
                    editor.putString("site_pet", dataSnapshot.getValue(String.class));
                    editor.commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
            });
        }
        else {
            site.setText(siteSP);
        }

        if(localizacaoSP == null || update) {

            mDatabase.child("PETs").child(nomePet).child("cidade").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    cidadeEstado = dataSnapshot.getValue(String.class);
                    mDatabase.child("PETs").child(nomePet).child("estado").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            cidadeEstado = cidadeEstado.concat(" - ").concat(dataSnapshot.getValue(String.class));
                            localizacao.setText(cidadeEstado);
                            editor.putString("localizacao_pet", cidadeEstado);
                            editor.commit();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("UNI", "Deu merda");
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
            });
        }
        else {
            localizacao.setText(localizacaoSP);
        }
        try {
            uriPet = Uri.parse(sharedPref.getString("uri_pet", null));
        }
        catch (NullPointerException e) {
            uriPet = null;
        }
        if(uriPet == null || update) {
            String nomePETFoto = nomePet.replace(" ", "_");
            StorageReference perfilRef = storageRef.child("imagensPET/" + nomePETFoto + ".jpg");

            final long ONE_MEGABYTE = 1024 * 1024;
            perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    uriPet = getImageUri(getActivity(), bitmapPerfil);
                    editor.putString("uri_pet", uriPet.toString());
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
        else {
            Picasso.with(getActivity()).load(uriPet).into(imagemPerfil);
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void setupExpandable() {
        expandableLayout.setRenderer(new ExpandableLayout.Renderer<String, Usuario>() {
            @Override
            public void renderParent(View view, String model, boolean isExpanded, int parentPosition) {
                ((TextView) view.findViewById(R.id.tvParent)).setText(model);
                ((TextView) view.findViewById(R.id.nPetianos)).setText(Integer.toString(contPetianos));
                view.findViewById(R.id.arrow).setBackgroundResource(isExpanded ? R.drawable.arrow_up : R.drawable.arrow_down);
            }

            @Override
            public void renderChild(View view, Usuario model, int parentPosition, int childPosition) {
                ((TextView) view.findViewById(R.id.nome_pet)).setText(model.nome);
                ((CircularImageView) view.findViewById(R.id.imagem_pet)).setImageDrawable(model.foto);
            }
        });

        expandableLayout.setExpandListener(new ExpandCollapseListener.ExpandListener<String>() {
            @Override
            public void onExpanded(int parentIndex, String parent, View view) {
                //Layout expanded
            }
        });

        expandableLayout.setCollapseListener(new ExpandCollapseListener.CollapseListener<String>() {
            @Override
            public void onCollapsed(int parentIndex, String parent, View view) {
                //Layout collapsed
            }
        });

        velBolsistas = dbBolsistas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String[] cursos;
//                Map<String,Object> map = (Map<String,Object>)dataSnapshot.getValue();
                //cursos = Arrays.copyOf(map.values().toArray(), map.values().toArray().length, String[].class);
                for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                    contPetianos++;
                    expandableLayout.notifyParentChanged(0);
                    usuarios = listSnapshots.getValue(String.class);
                    StorageReference perfilRef = storageRef.child("imagensPerfil/" + listSnapshots.getKey() + ".jpg");
                    perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListenerString(usuarios) {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPerfil, toPx(100), toPx(100), false);
                            bitmapDrawablePet = new BitmapDrawable(getResources(), resizedBmp);
                            String[] nomes = variavel.split(" ");
                            if(nomes.length > 1) {
                                String nomeSobrenome = nomes[0] + " " + nomes[1];
                                Log.d("NOME", nomeSobrenome);
                                section.children.add(new Usuario(nomeSobrenome, bitmapDrawablePet));
                            }
                            else {
                                String nomeSobrenome = variavel;
                                Log.d("NOME", nomeSobrenome);
                                section.children.add(new Usuario(nomeSobrenome, bitmapDrawablePet));
                            }
                        }
//                            uriPerfil = getImageUri(getActivity(), bitmapPerfil);
//                            imagemPerfil.setImageBitmap(bitmapPerfil);
//                            mModels.add(new Usuario(usuarios[contPetianos], bitmapDrawablePet));
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

                }
                velOficiais = dbOficiais.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //String[] cursos;
//                        Map<String,Object> map = (Map<String,Object>)dataSnapshot.getValue();
                        //cursos = Arrays.copyOf(map.values().toArray(), map.values().toArray().length, String[].class);
                        for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                            contPetianos++;
                            expandableLayout.notifyParentChanged(0);
                            usuarios = listSnapshots.getValue(String.class);
                            StorageReference perfilRef = storageRef.child("imagensPerfil/" + listSnapshots.getKey() + ".jpg");
                            perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListenerString(usuarios) {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPerfil, toPx(100), toPx(100), false);
                                    bitmapDrawablePet = new BitmapDrawable(getResources(), resizedBmp);
//                            uriPerfil = getImageUri(getActivity(), bitmapPerfil);
//                            imagemPerfil.setImageBitmap(bitmapPerfil);
//                                    mModels.add(new Usuario(usuarios[contPetianos], bitmapDrawablePet));
                                    String[] nomes = variavel.split(" ");
                                    String nomeSobrenome = nomes[0]+" "+nomes[1];
                                    Log.d("NOME", nomeSobrenome);
                                    section.children.add(new Usuario(nomeSobrenome, bitmapDrawablePet));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }
                        velVoluntarios = dbVoluntarios.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //String[] cursos;
//                                Map<String,Object> map = (Map<String,Object>)dataSnapshot.getValue();
                                //cursos = Arrays.copyOf(map.values().toArray(), map.values().toArray().length, String[].class);
                                for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                                    contPetianos++;
                                    expandableLayout.notifyParentChanged(0);
                                    usuarios = listSnapshots.getValue(String.class);
                                    StorageReference perfilRef = storageRef.child("imagensPerfil/" + listSnapshots.getKey() + ".jpg");
                                    perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListenerString(usuarios) {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPerfil, toPx(100), toPx(100), false);
                                            bitmapDrawablePet = new BitmapDrawable(getResources(), resizedBmp);
//                            uriPerfil = getImageUri(getActivity(), bitmapPerfil);
//                            imagemPerfil.setImageBitmap(bitmapPerfil);
//                                            mModels.add(new Usuario(usuarios[contPetianos], bitmapDrawablePet));
                                            String[] nomes = variavel.split(" ");
                                            String nomeSobrenome = nomes[0]+" "+nomes[1];
                                            Log.d("NOME", nomeSobrenome);
                                            section.children.add(new Usuario(nomeSobrenome, bitmapDrawablePet));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                        }
                                    });
                                }
                                dbBolsistas.removeEventListener(velVoluntarios);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("UNI", "Deu merda");
                            }
                        });
                        dbBolsistas.removeEventListener(velOficiais);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("UNI", "Deu merda");
                    }
                });
                dbBolsistas.removeEventListener(velBolsistas);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });

//        mAdapter.add(mModels);

        section.parent = "Petianos";
        expandableLayout.addSection(section);

        expandableLayout.setExpandListener(new ExpandCollapseListener.ExpandListener<String>() {
            @Override
            public void onExpanded(int parentIndex, String parent, View view) {
                view.findViewById(R.id.arrow).setBackgroundResource(R.drawable.arrow_up);
            }
        });

        expandableLayout.setCollapseListener(new ExpandCollapseListener.CollapseListener<String>() {
            @Override
            public void onCollapsed(int parentIndex, String parent, View view) {
                view.findViewById(R.id.arrow).setBackgroundResource(R.drawable.arrow_down);
            }
        });
    }

    private void setupRecycler() {

        // Configurando o gerenciador de layout para ser uma lista.
        mRecyclerView = getView().findViewById(R.id.lista_pet);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        // Adiciona o adapter que irá anexar os objetos à lista.
        // Está sendo criado com lista vazia, pois será preenchida posteriormente.
        mAdapter = new LineAdapterUsuarios();
        //mAdapter = new LineAdapterPet(new ArrayList<>(0));
        mRecyclerView.setAdapter(mAdapter);

        // Configurando um dividr entre linhas, para uma melhor visualização.
//        mRecyclerView.addItemDecoration(
//                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    public int toPx(float dp){
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        return px;
    }
}