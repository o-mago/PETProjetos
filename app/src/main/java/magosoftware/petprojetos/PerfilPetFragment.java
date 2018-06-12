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
    private String nomeUsuario;
    private ValueEventListener valueEventListener;
    private String nodePet;
    private DatabaseReference dbTime;
    private ScrollView mainPerfil;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        dbUsuario = mDatabase.child("Usuarios");
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        nomePet = getArguments().getString("nome");
        nodePet = getArguments().getString("node");
        dbBolsistas = mDatabase.child("PETs").child(nodePet).child("time").child("bolsistas");
        dbOficiais = mDatabase.child("PETs").child(nodePet).child("time").child("oficiais");
        dbVoluntarios = mDatabase.child("PETs").child(nodePet).child("time").child("voluntarios");
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
        mainPerfil = getView().findViewById(R.id.main_perfil);
        progressBar = getView().findViewById(R.id.progress_bar);
        mainPerfil.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
//        setupEdit();
        follow = getView().findViewById(R.id.follow);
        follow.setOnClickListener(this);
        setupFollow("bolsistas", "AGUARDANDO","#FFFF00", getResources().getDrawable(R.drawable.background_contorno_aguardando), true);
        setupFollow("oficiais", "AGUARDANDO","#FFFF00", getResources().getDrawable(R.drawable.background_contorno_aguardando), true);
        setupFollow("voluntarios", "AGUARDANDO","#FFFF00", getResources().getDrawable(R.drawable.background_contorno_aguardando), true);
        setupFollow("bolsistas", "PETIANO","#00E676", getResources().getDrawable(R.drawable.background_contorno_ok), false);
        setupFollow("oficiais", "PETIANO","#00E676", getResources().getDrawable(R.drawable.background_contorno_ok), false);
        setupFollow("voluntarios", "PETIANO","#00E676", getResources().getDrawable(R.drawable.background_contorno_ok), false);
        getView().findViewById(R.id.edit_button).setOnClickListener(this);
        editButton.setVisibility(View.GONE);
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

//    public void setupEdit() {
//        dbUsuario.child(user.getUid()).child("pet").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.hasChildren()) {
//                    String codigo;
//                    for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
//                        String condicao = listSnapshots.child("situacao").getValue(String.class);
//                        if (condicao.equals("bolsistas") || condicao.equals("oficiais") || condicao.equals("voluntarios")) {
//                            editButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
//                            break;
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("UNI", "Deu merda");
//            }
//        });
//
//    }

    public void setupFollow(String categoria, final String mensagem, final String cor, final Drawable fundo, boolean aguardando) {
        dbTime = mDatabase.child("PETs").child(nodePet).child("time");
        if(aguardando) {
            dbTime = dbTime.child("aguardando");
        }
        dbTime.child(categoria).addListenerForSingleValueEvent(new ValueEventListenerSend(mensagem) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String codigo;
                for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                    try {
                        codigo = listSnapshots.getKey();
                        Log.d("DEV/PERFILPET", "codigo: " + codigo);
                        if (codigo.equals(user.getUid())) {
                            if(variavel.equals("PETIANO")) {
                                editButton.setVisibility(View.VISIBLE);
                            }
                            follow.setText(mensagem);
                            follow.setTextColor(Color.parseColor(cor));
                            follow.setBackgroundDrawable(fundo);
                            break;
                        }
                    } catch (NullPointerException e) {

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
//            Uri selectedImage = data.getData();
            Intent intent = new Intent(getActivity(), AdicionaImagem.class);
            intent.putExtra("caminho", "/imagensPET/" + nodePet +".jpg");
            intent.putExtra("tipo", "pet");
            startActivity(intent);
//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
        if(i == R.id.edit_button) {
            Intent intent = new Intent(getActivity(), AdicioneSeuPetActivity.class);
            intent.putExtra("node", nodePet);
            startActivity(intent);
        }
        if(i == R.id.follow) {
            dbUsuario.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("pet")) {
                        Log.d("DEV/PERFILPET", "temPET");
                        temPET = true;
                        for (DataSnapshot listSnapshots : dataSnapshot.child("pet").getChildren()) {
                            nomeOldPET = listSnapshots.getKey();
                            situacaoPET = listSnapshots.child("situacao").getValue(String.class);
                        }
                    }
                    else {
                        Log.d("DEV/PERFILPET", "Não temPET");
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
                    temCerteza(which);
                }
            });
            builder.show();
        }
    }

    public void temCerteza(final int escolha) {
        Log.d("DEV/PERFILPET", "temCerteza");
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
                        dbUsuario.child(user.getUid()).child("pet").child(nodePet).child("situacao").setValue("aguardando");
                        dbUsuario.child(user.getUid()).child("pet").child(nodePet).child("nome").setValue(nomePet);
                        if(temPET) {
                            Map<String, String> novoPetiano = new HashMap<>();
                            novoPetiano.put(user.getUid(), "bolsistas");
                            mDatabase.child("PETs").child(nomeOldPET).child("time").child(situacaoPET).removeValue();
                        }
                        Log.d("WHICH", Integer.toString(which));
                        if (escolha == 0) {
                            setupFollow("bolsistas", "AGUARDANDO", "#FFFF00", getResources().getDrawable(R.drawable.background_contorno_aguardando), true);
                            dbUsuario.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    nomeUsuario = dataSnapshot.child("nome").getValue(String.class);
                                    mDatabase.child("PETs").child(nodePet).child("time")
                                            .child("aguardando")
                                            .child("bolsistas")
                                            .child(user.getUid())
                                            .setValue(nomeUsuario);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        if (escolha == 1) {
                            setupFollow("oficiais", "AGUARDANDO", "#FFFF00", getResources().getDrawable(R.drawable.background_contorno_aguardando), true);
                            dbUsuario.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    nomeUsuario = dataSnapshot.child("nome").getValue(String.class);
                                    mDatabase.child("PETs").child(nodePet).child("time")
                                            .child("aguardando")
                                            .child("oficiais")
                                            .child(user.getUid())
                                            .setValue(nomeUsuario);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        if (escolha == 2) {
                            setupFollow("voluntários", "AGUARDANDO", "#FFFF00", getResources().getDrawable(R.drawable.background_contorno_aguardando), true);
                            dbUsuario.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    nomeUsuario = dataSnapshot.child("nome").getValue(String.class);
                                    mDatabase.child("PETs").child(nodePet).child("time")
                                            .child("aguardando")
                                            .child("voluntarios")
                                            .child(user.getUid())
                                            .setValue(nomeUsuario);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    if (which == 1) {

                    }
                }
            });
            builderCerteza.show();
        }
        else {
            Log.d("DEV/PERFILPET", "else temCerteza");
            dbUsuario.child(user.getUid()).child("pet").child(nodePet).child("situacao").setValue("aguardando");
            dbUsuario.child(user.getUid()).child("pet").child(nodePet).child("nome").setValue(nomePet);
            if (escolha == 0) {
                setupFollow("bolsistas", "AGUARDANDO", "#FFFF00", getResources().getDrawable(R.drawable.background_contorno_aguardando), true);
                Log.d("DEV/PERFILPET", "escolha == 0");
                dbUsuario.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("DEV/PERFILPET", "datasnapshoot");
                        nomeUsuario = dataSnapshot.child("nome").getValue(String.class);
                        Log.d("DEV/PERFILPET", "nomeUsuario: "+nomeUsuario);
                        mDatabase.child("PETs").child(nodePet).child("time")
                                .child("aguardando")
                                .child("bolsistas")
                                .child(user.getUid())
                                .setValue(nomeUsuario);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            if (escolha == 1) {
                setupFollow("oficiais", "AGUARDANDO", "#FFFF00", getResources().getDrawable(R.drawable.background_contorno_aguardando), true);
                dbUsuario.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        nomeUsuario = dataSnapshot.child("nome").getValue(String.class);
                        mDatabase.child("PETs").child(nodePet).child("time")
                                .child("aguardando")
                                .child("oficiais")
                                .child(user.getUid())
                                .setValue(nomeUsuario);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            if (escolha == 2) {
                setupFollow("voluntarios", "AGUARDANDO", "#FFFF00", getResources().getDrawable(R.drawable.background_contorno_aguardando), true);
                dbUsuario.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        nomeUsuario = dataSnapshot.child("nome").getValue(String.class);
                        mDatabase.child("PETs").child(nodePet).child("time")
                                .child("aguardando")
                                .child("voluntarios")
                                .child(user.getUid())
                                .setValue(nomeUsuario);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            try {
                Uri selectedImage = data.getData();
                Intent intent = new Intent(getActivity(), AdicionaImagem.class);
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

        if(nomeSP == null || emailSP == null || siteSP == null || cursoSP == null || universidadeSP == null || localizacaoSP == null || update) {
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    nome.setText(dataSnapshot.child("nome").getValue(String.class));
                    editor.putString("nome_pet", dataSnapshot.child("nome").getValue(String.class));
                    email.setText(dataSnapshot.child("email").getValue(String.class));
                    editor.putString("email_pet" , dataSnapshot.child("email").getValue(String.class));
                    universidade.setText(dataSnapshot.child("universidade").getValue(String.class));
                    editor.putString("universidade_pet", dataSnapshot.child("universidade").getValue(String.class));
                    ano.setText(dataSnapshot.child("ano").getValue(String.class));
                    editor.putString("ano_pet", dataSnapshot.child("ano").getValue(String.class));
                    site.setText(dataSnapshot.child("site").getValue(String.class));
                    editor.putString("site_pet", dataSnapshot.child("site").getValue(String.class));
                    String cursosConcat = "";
                    //String[] cursos;
                    Map<String,Object> map = (Map<String,Object>)dataSnapshot.child("cursos").getValue();
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

                    cidadeEstado = dataSnapshot.child("cidade").getValue(String.class);
                    cidadeEstado = cidadeEstado.concat(" - ").concat(dataSnapshot.child("estado").getValue(String.class));
                    localizacao.setText(cidadeEstado);
                    editor.putString("localizacao_pet", cidadeEstado);

                    editor.commit();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
            };
            mDatabase.child("PETs").child(nodePet).addListenerForSingleValueEvent(valueEventListener);
        }
        else {
            nome.setText(nomeSP);
            email.setText(emailSP);
            universidade.setText(universidadeSP);
            ano.setText(anoSP);
            site.setText(siteSP);
            curso.setText(cursoSP);
            localizacao.setText(localizacaoSP);
        }
        try {
            uriPet = Uri.parse(sharedPref.getString("uri_pet", null));
        }
        catch (NullPointerException e) {
            uriPet = null;
        }
//        if(uriPet == null || update) {
            StorageReference perfilRef = storageRef.child("imagensPET/" + nodePet + ".jpg");
            try {
                perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        try {
                            bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                            uriPet = getImageUri(getActivity(), bitmapPerfil);
//                            editor.putString("uri_pet", uriPet.toString());
//                            editor.commit();
                            imagemPerfil.setImageBitmap(bitmapPerfil);
                            editButton.setVisibility(View.VISIBLE);
                            mainPerfil.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                        catch (Exception e) {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
            catch (Exception e) {

            }
//        }
//        else {
//            Picasso.with(getActivity()).load(uriPet).into(imagemPerfil);
//        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void setDados() {

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

        velBolsistas = new ValueEventListener() {
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
                            try {
                                bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPerfil, toPx(70), toPx(70), false);
                                bitmapDrawablePet = new BitmapDrawable(getResources(), resizedBmp);
                                String[] nomes = ((String) variavel1).split(" ");
                                if (nomes.length > 1) {
                                    String nomeSobrenome = nomes[0] + " " + nomes[1];
                                    Log.d("NOME", nomeSobrenome);
                                    section.children.add(new Usuario(nomeSobrenome, bitmapDrawablePet));
                                } else {
                                    String nomeSobrenome = (String) variavel1;
                                    Log.d("NOME", nomeSobrenome);
                                    section.children.add(new Usuario(nomeSobrenome, bitmapDrawablePet));
                                }
                            }
                            catch (IllegalStateException e) {

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
                                    try {
                                        bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPerfil, toPx(70), toPx(70), false);
                                        bitmapDrawablePet = new BitmapDrawable(getResources(), resizedBmp);
//                            uriPerfil = getImageUri(getActivity(), bitmapPerfil);
//                            imagemPerfil.setImageBitmap(bitmapPerfil);
//                                    mModels.add(new Usuario(usuarios[contPetianos], bitmapDrawablePet));
                                        String[] nomes = ((String) variavel1).split(" ");
                                        String nomeSobrenome = nomes[0] + " " + nomes[1];
                                        Log.d("NOME", nomeSobrenome);
                                        section.children.add(new Usuario(nomeSobrenome, bitmapDrawablePet));
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
                                            try {
                                                bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPerfil, toPx(70), toPx(70), false);
                                                bitmapDrawablePet = new BitmapDrawable(getResources(), resizedBmp);
//                            uriPerfil = getImageUri(getActivity(), bitmapPerfil);
//                            imagemPerfil.setImageBitmap(bitmapPerfil);
//                                            mModels.add(new Usuario(usuarios[contPetianos], bitmapDrawablePet));
                                                String[] nomes = ((String) variavel1).split(" ");
                                                String nomeSobrenome = nomes[0] + " " + nomes[1];
                                                Log.d("NOME", nomeSobrenome);
                                                section.children.add(new Usuario(nomeSobrenome, bitmapDrawablePet));
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
        };

        dbBolsistas.addListenerForSingleValueEvent(velBolsistas);

//        mAdapter.add(mModels);

        section.parent = "Equipe";
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dbBolsistas.removeEventListener(velBolsistas);
        getView().findViewById(R.id.edit_button).setOnClickListener(null);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDatabase.child("PETs").child(nodePet).removeEventListener(valueEventListener);

        follow.setOnClickListener(null);
        imagemPerfil.setOnClickListener(null);
        mRecyclerView = null;
        mAdapter = null;
        dbUsuario = null;
        dbBolsistas = null;
        dbOficiais = null;
        dbVoluntarios = null;
        velBolsistas = null;
        velOficiais = null;
        velVoluntarios = null;
        user = null;
        mAuth = null;
        imagemPerfil = null;
        site = null;
        nome = null;
        email = null;
        curso = null;
        universidade = null;
        localizacao = null;
        ano = null;
        storage = null;
        storageRef = null;
        bitmapPerfil = null;
        editButton = null;
        uriPet = null;
        sharedPref = null;
        editor = null;
        nomePet = null;
        cidadeEstado = null;
        follow = null;
        expandableLayout = null;
        usuarios = null;
        mModels = null;
        bitmapDrawablePet = null;
        section = null;
        nomeOldPET = null;
        situacaoPET = null;
        valueEventListener = null;
    }
}