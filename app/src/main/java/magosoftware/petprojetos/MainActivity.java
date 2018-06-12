package magosoftware.petprojetos;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beloo.widget.chipslayoutmanager.layouter.IMeasureSupporter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener, DrawerAdapter.OnItemClicked, View.OnClickListener, LineAdapterNotificacao.OnItemClicked {

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    private static final String TAG = "MainActivity";

    //Navigation
    private RecyclerView mDrawerList;
    private DrawerAdapter mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private CustomDrawer mDrawerLayout;
    private String mActivityTitle;
    FragmentTransaction ft;
    DatabaseReference dbUsuario;
    FirebaseUser user;
    public SharedPreferences sharedPref;
    public SharedPreferences.Editor editor;
    FirebaseStorage storage;
    StorageReference storageRef;
    private String nomeMeuPet = "";
    private String nodeMeuPet = "";
    private List<ItemMenu> mModels;
    private ProgressBar progressBar;
    private final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 3;
    private Toolbar myToolbar;
    private Menu menu;
    private Animation rotate_forward,rotate_backward;
    private boolean isNotificacaoOpen = false;
    private Fragment fragment;
    private ImageView iv;
    private RelativeLayout navNotificacoes;

    private RecyclerView recyclerView;
    private LineAdapterNotificacao mAdapterNotificacao;
    private ValueEventListener valueEventListener;
    private List<Notificacao> mModelsNotificacao;
    private int j = 0;
    private int i = 0;
    private String nodePet;
    private String nodeProjeto;
    private DatabaseReference dbPetUsuario;
    private TextView aviso;
    int width;
    int height;

    private Animation slideRight;
    private Animation slideLeft;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userFirebase = database.getReference("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        myToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.sino));
        setSupportActionBar(myToolbar);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        sharedPref = this.getSharedPreferences("todoApp", 0);
        editor = sharedPref.edit();
        progressBar = findViewById(R.id.progress_bar);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        navNotificacoes = findViewById(R.id.navNotificacao);
        recyclerView = findViewById(R.id.lista_notificacoes);

        slideRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right);
        slideLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        ViewGroup.LayoutParams params = navNotificacoes.getLayoutParams();

        params.width = width;
        navNotificacoes.setLayoutParams(params);

        dbPetUsuario = mDatabase.child("Usuarios").child(user.getUid()).child("pet");
        aviso = findViewById(R.id.aviso);
        aviso.setVisibility(View.GONE);
        setupRecyclerView();
        setupLista();
        mAdapterNotificacao.setOnClick(this);

        rotate_forward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);

        //Navigation
//        mDrawerList = (RecyclerView) findViewById(R.id.navList);
//        setupRecycler();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        dbUsuario = mDatabase.child("Usuarios");
        setupDrawer();
        addDrawerItems();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        requestPermission();

        setupMeuPET();

        dbUsuario.child(user.getUid()).child("pet").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                        String condicao = listSnapshots.child("situacao").getValue(String.class);
                        Log.d("DEV/MAIN", "Entrou situacao");
                        ft = getSupportFragmentManager().beginTransaction();
                        ft.addToBackStack(null);
                        ft.replace(R.id.fragment_container, MeuPetFragment.newInstance());
                        ft.commit();
                        progressBar.setVisibility(View.GONE);
                        break;
                    }
                }
                else {
                    Log.d("DEV/MAIN", "Entrou else");
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.addToBackStack(null);
                    ft.replace(R.id.fragment_container, EncontreSeuPet.newInstance());
                    ft.commit();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });
    }

    private void setupLista() {
        Log.d("DEV/NOTIFICACAO", "setupLista");
        mModelsNotificacao = new ArrayList<>();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DEV/NOTIFICACAO", "PrimeiroOnDataChange");
                for(DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                    String nodePET = listSnapshot.getKey();
                    i = 0;
                    j = 0;
                    if (listSnapshot.hasChild("tarefas")) {
                        Log.d("DEV/NOTIFICACAO", "PrimeiroIf");
                        for (DataSnapshot subListSnapshoot : listSnapshot.child("tarefas").getChildren()) {
//                            Log.d("DEV/NOTIFICACAO", ""+subListSnapshoot.child("nova").getValue(Boolean.class));
                            if (subListSnapshoot.child("nova").getValue(Boolean.class).equals(true)) {
                                i++;
                                Log.d("DEV/NOTIFICACAO", "SegundoIf");
//                                mDatabase.child("Usuarios").child(user.getUid()).child("pet")
//                                        .child(nodePET).child("tarefas")
//                                        .child(subListSnapshoot.getKey())
//                                        .child("nova")
//                                        .setValue(false);
                                mDatabase.child(subListSnapshoot.child("caminho").getValue(String.class))
                                        .addListenerForSingleValueEvent(new ValueEventListenerSend(subListSnapshoot.child("caminho").getValue(String.class)) {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                j++;
                                                Log.d("DEV/NOTIFICACAO", "SegundoOnDataChange");
                                                mModelsNotificacao.add(new Notificacao("tarefa",
                                                        dataSnapshot.child("titulo").getValue(String.class),
                                                        dataSnapshot.child("prazo").getValue(String.class),
                                                        (String) variavel, "Você foi marcado em uma tarefa"));
                                                if(i == j) {
                                                    mAdapterNotificacao.replaceAll(mModelsNotificacao);
                                                    mAdapterNotificacao.notifyDataSetChanged();
//                                                    navNotificacoes.bringToFront();
                                                    if(isNotificacaoOpen) {
                                                        closeNotificacao();
//                                                        navNotificacoes.setX(-width);
                                                    }
                                                    mModelsNotificacao.clear();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                        if(i == 0) {
                            mAdapterNotificacao.replaceAll(mModelsNotificacao);
                            mAdapterNotificacao.notifyDataSetChanged();
                            mModelsNotificacao.clear();
                            semNotificacoes();
                        }
                        else {
                            aviso.setVisibility(View.GONE);
                            mModelsNotificacao.clear();
                        }
                    }
                    if (listSnapshot.hasChild("reunioes")) {
                        for (DataSnapshot subListSnapshoot : listSnapshot.child("reunioes").getChildren()) {
//                            Log.d("DEV/NOTIFICACAO", ""+subListSnapshoot.child("nova").getValue(Boolean.class));
                            if (subListSnapshoot.child("nova").getValue(Boolean.class).equals(true)) {
                                i++;
                                Log.d("DEV/NOTIFICACAO", "SegundoIf");
//                                mDatabase.child("Usuarios").child(user.getUid()).child("pet")
//                                        .child(nodePET).child("tarefas")
//                                        .child(subListSnapshoot.getKey())
//                                        .child("nova")
//                                        .setValue(false);
                                mDatabase.child(subListSnapshoot.child("caminho").getValue(String.class))
                                        .addListenerForSingleValueEvent(new ValueEventListenerSend(subListSnapshoot.child("caminho").getValue(String.class)) {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                j++;
                                                Log.d("DEV/NOTIFICACAO", "SegundoOnDataChange");
                                                mModelsNotificacao.add(new Notificacao("reuniao",
                                                        dataSnapshot.child("nome").getValue(String.class),
                                                        dataSnapshot.child("reunioes").child("data").getValue(String.class)+" "+
                                                                dataSnapshot.child("reunioes").child("horario").getValue(String.class),
                                                        (String) variavel, "Nova reunião"));
                                                if(i == j) {
                                                    mAdapterNotificacao.replaceAll(mModelsNotificacao);
                                                    mAdapterNotificacao.notifyDataSetChanged();
//                                                    navNotificacoes.bringToFront();
                                                    if(isNotificacaoOpen) {
                                                        closeNotificacao();
//                                                        navNotificacoes.setX(-width);
                                                    }
                                                    mModelsNotificacao.clear();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                    }
                    if(i == 0) {
                        mAdapterNotificacao.replaceAll(mModelsNotificacao);
                        mAdapterNotificacao.notifyDataSetChanged();
                        mModelsNotificacao.clear();
                        semNotificacoes();
                    }
                    else {
                        aviso.setVisibility(View.GONE);
                        mModelsNotificacao.clear();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        dbPetUsuario.addValueEventListener(valueEventListener);
    }

    private void semNotificacoes() {
        aviso.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.sino_notificacao) {
            iv.setImageResource(R.drawable.sino);
            if(isNotificacaoOpen){
                closeNotificacao();
            } else {
                openNotificacao();
            }
        }
    }

    private void closeNotificacao() {
        iv.startAnimation(rotate_backward);
        mDrawerLayout.closeDrawer(Gravity.RIGHT);
        isNotificacaoOpen = false;
    }

    private void openNotificacao() {
        mDatabase.child("Usuarios").child(user.getUid()).child("update").setValue(false);
        iv.startAnimation(rotate_forward);
        mDrawerLayout.openDrawer(Gravity.RIGHT);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Notificações");
        isNotificacaoOpen = true;
    }

//    private void closeNotificacao() {
//        iv.startAnimation(rotate_backward);
////        navNotificacoes.startAnimation(slideRight);
//        ObjectAnimator transAnimation = ObjectAnimator.ofFloat(navNotificacoes, "translationX", -width, 0);
//        transAnimation.setDuration(500);//set duration
//        transAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
////        transAnimation.setTarget(navNotificacoes);
//        transAnimation.start();//start animation
//        isNotificacaoOpen = false;
//    }
//
//    private void openNotificacao() {
//        mDatabase.child("Usuarios").child(user.getUid()).child("update").setValue(false);
//        iv.startAnimation(rotate_forward);
//        ObjectAnimator transAnimation = ObjectAnimator.ofFloat(navNotificacoes, "translationX", 0, -width);
//        transAnimation.setDuration(500);//set duration
////        transAnimation.setTarget(navNotificacoes);
//        transAnimation.start();//start animation
//        navNotificacoes.setVisibility(View.VISIBLE);
////        navNotificacoes.startAnimation(slideLeft);
//        isNotificacaoOpen = true;
//    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 23) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//        int countChild = fragment.getChildFragmentManager().getBackStackEntryCount();
//        Log.d("DEV/MAIN", "Fragment: "+count);
//        Log.d("DEV/MAIN", "ChildFragment: "+countChild);

        if (count == 0) {
            Log.d("DEV/MAINACTIVITY", "count == 0");
            super.onBackPressed();
        }
        else if(count == 1){
            Log.d("DEV/MAINACTIVITY", "count == 1");
            getSupportFragmentManager().popBackStack();
            super.onBackPressed();
        }
        else {
            Log.d("DEV/MAINACTIVITY", "count > 1");
//            getSupportFragmentManager().popBackStackImmediate();
            super.onBackPressed();
//            getSupportFragmentManager().popBackStack();
//            getSupportFragmentManager().popBackStackImmediate();
        }
//        else if (countChild == 0) {
//            getSupportFragmentManager().popBackStack();
//        }
//        else {
//            fragment.getChildFragmentManager().popBackStack();
//            getSupportFragmentManager().popBackStack();
//            Log.d("DEV/MAIN", "Child closed");
//        }
    }

    private void setupMeuPET() {
        mDatabase.child("Usuarios").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("update").getValue(Boolean.class).equals(true)) {
                    Log.d("DEV/MAINACTIVITY", "update == true");
//                    myToolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.sino_new));
                    ActionMenuItemView notificacao = findViewById(R.id.notificacao);
                    iv.setImageResource(R.drawable.sino_new);
//                    menu.getItem(0).setIcon(R.mipmap.sino_new);
                }
                editor.putString("nome_usuario", dataSnapshot.child("nome").getValue(String.class));
                for (DataSnapshot listSnapshots : dataSnapshot.child("pet").getChildren()) {
                    String condicao = listSnapshots.child("situacao").getValue(String.class);
                    nomeMeuPet = listSnapshots.child("nome").getValue(String.class);
                    nodeMeuPet = listSnapshots.getKey();
                    editor.putString("nome_meu_pet", nomeMeuPet);
                    editor.putString("node_meu_pet", nodeMeuPet);
                    editor.putString("condicao_meu_pet", condicao);
                    editor.apply();
                    break;
                }
//                String nomeImagemPet = nomeMeuPet;
//                try {
//                    nomeImagemPet = nomeMeuPet.replace(" ", "_");
//                }
//                catch (NullPointerException e) {
//
//                }
//                Log.d("ENTROU2", nomeImagemPet);
//                StorageReference perfilRef = storageRef.child("imagensPET/" + nomeImagemPet + ".jpg");
//
//                final long ONE_MEGABYTE = 1024 * 1024;
//                perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Bitmap bitmapPerfil = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        Uri uriPet = getImageUri(MainActivity.this, bitmapPerfil);
//                        editor.putString("uri_pet", uriPet.toString());
//                        editor.commit();
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle any errors
//                    }
//                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    // [END on_start_check_user]

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();
        volta_login();
        //Google Logout
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.d("Intenta", "ÉEEEEE");
                        volta_login();
                    }
                });
    }

    public void volta_login(){
        Intent i = new Intent(this, EmailPasswordActivity.class);
        startActivity(i);
        finish();
    }

    //Navigation
    private void addDrawerItems() {
//        String[] osArray = { "Meu PET", "Perfil", "Pesquisar PET", "Pesquisar Petiano"};
        mModels = new ArrayList<>();
        mModels.add(new ItemMenu("Meu PET", getResources().getDrawable(R.drawable.pet_icone)));
        mModels.add(new ItemMenu("Perfil", getResources().getDrawable(R.drawable.icone_perfil)));
        mModels.add(new ItemMenu("Horários Petianos", getResources().getDrawable(R.drawable.time)));
        mModels.add(new ItemMenu("Calendário", getResources().getDrawable(R.drawable.calendar)));
        mModels.add(new ItemMenu("Pesquisar PET", getResources().getDrawable(R.drawable.search)));
        mModels.add(new ItemMenu("Pesquisar Petiano", getResources().getDrawable(R.drawable.pessoas)));
        mModels.add(new ItemMenu("Sair", getResources().getDrawable(R.drawable.exit_rotate)));
        //mUser = new ImageButton(this, R.id.)
//        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mAdapter.add(mModels);
        mAdapter.setOnClick(this);
        mDrawerList.setAdapter(mAdapter);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onItemClick(int position, String nome, final String node, final String tipo) {
        closeNotificacao();
        nodePet = node.split("/")[1];
        try {
            nodeProjeto = node.split("PETs/"+nodePet+"projetos")[0];
        } catch (Exception e) {

        }
        mDatabase.child("PETs").child(nodePet).addListenerForSingleValueEvent(new ValueEventListenerSend(nodeProjeto, nodePet, this) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(tipo.equals("tarefa")) {
                    String nomeProjeto = dataSnapshot.child("nome").getValue(String.class);
                    String nomeProjetoSeparado = dataSnapshot.child("projetos").child((String) variavel).child("nome").getValue(String.class);
                    if (nomeProjetoSeparado != null) {
                        nomeProjeto = nomeProjeto + "-" + dataSnapshot.child("projetos").child((String) variavel).child("nome").getValue(String.class);
                    }
                    int indice1 = node.indexOf("/tarefas/") + "/tarefas/".length();
                    int indice2 = node.indexOf("/", indice1);
                    String situacaoTarefa = node.substring(indice1, indice2);
                    closeNotificacao();
                    Intent intent = new Intent((Context) variavel3, TarefasEditActivity.class);
                    dbPetUsuario.child((String) variavel2).child("tarefas")
                            .child(node.split(situacaoTarefa + "/")[1])
                            .child("nova")
                            .setValue(false);
                    intent.putExtra("nome_projeto", nomeProjeto);
                    intent.putExtra("situacao_tarefa", situacaoTarefa);
                    intent.putExtra("node", node.split(situacaoTarefa + "/")[1]);
                    intent.putExtra("tarefa_path", node.split("/tarefas/")[0]);
                    startActivity(intent);
                }
                else if(tipo.equals("reuniao")) {
                    closeNotificacao();
                    Intent intent = new Intent((Context) variavel3, MarcarReuniaoActivity.class);
                    dbPetUsuario.child((String) variavel2).child("reunioes")
                            .child("Projeto "+node.split("/")[node.split("/").length-1])
                            .child("nova")
                            .setValue(false);
                    intent.putExtra("node_projeto", nodeProjeto);
                    intent.putExtra("reunioes_path", node);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapterNotificacao = new LineAdapterNotificacao();
//        mAdapterNotificacao.setHasStableIds(true);
        recyclerView.setAdapter(mAdapterNotificacao);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onItemClick(int position, View itemView, String opcaoEscolhida) {
        if(position == 0) {
            ft = getSupportFragmentManager().beginTransaction();
//            ft.addToBackStack(null);
            getSupportFragmentManager().popBackStack();
            ft.replace(R.id.fragment_container, MeuPetFragment.newInstance());
            ft.commit();
            getSupportFragmentManager().popBackStackImmediate();
        }
        if(position == 1) {
            ft = getSupportFragmentManager().beginTransaction();
//            ft.addToBackStack(null);
            getSupportFragmentManager().popBackStack();
            ft.replace(R.id.fragment_container, Perfil.newInstance());
            ft.commit();
            getSupportFragmentManager().popBackStackImmediate();
        }
        if(position == 2) {
            ft = getSupportFragmentManager().beginTransaction();
//            ft.addToBackStack(null);
            getSupportFragmentManager().popBackStack();
            ft.replace(R.id.fragment_container, HorariosFragment.newInstance());
            ft.commit();
            getSupportFragmentManager().popBackStackImmediate();
        }
        if(position == 3) {
            ft = getSupportFragmentManager().beginTransaction();
//            ft.addToBackStack(null);
            getSupportFragmentManager().popBackStack();
            ft.replace(R.id.fragment_container, Perfil.newInstance());
            ft.commit();
            getSupportFragmentManager().popBackStackImmediate();
        }
        if(position == 4) {
            ft = getSupportFragmentManager().beginTransaction();
//            ft.addToBackStack(null);
            getSupportFragmentManager().popBackStack();
            ft.replace(R.id.fragment_container, EncontreSeuPet.newInstance());
            ft.commit();
            getSupportFragmentManager().popBackStackImmediate();
        }
        if(position == 5) {
            ft = getSupportFragmentManager().beginTransaction();
//            ft.addToBackStack(null);
            getSupportFragmentManager().popBackStack();
            ft.replace(R.id.fragment_container, PesquisarPetiano.newInstance());
            ft.commit();
            getSupportFragmentManager().popBackStackImmediate();
        }
        if(position == 6) {
            revokeAccess();
        }
        mDrawerLayout.closeDrawers();
    }

    private void setupDrawer() {

        LinearLayoutManager layoutManager= new LinearLayoutManager(this);

        mDrawerList = findViewById(R.id.navList);
        mDrawerList.setLayoutManager(layoutManager);

        mAdapter = new DrawerAdapter();
        mDrawerList.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecorationRequisicao = new DividerItemDecoration(mDrawerList.getContext(),
                layoutManager.getOrientation());
        mDrawerList.addItemDecoration(dividerItemDecorationRequisicao);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0);
                if(mDrawerLayout.isDrawerVisible(Gravity.LEFT)){
                    mDrawerLayout.setScrimColor(0x99000000);
                    if(mDrawerLayout.isDrawerVisible(Gravity.RIGHT)) {
                        closeNotificacao();
                    }
                }
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                super.onDrawerSlide(drawerView, 0);
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                assert getSupportActionBar() != null;
                getSupportActionBar().setTitle(mActivityTitle);
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        iv = (ImageView)inflater.inflate(R.layout.iv_refresh, null);
        menu.findItem(R.id.notificacao).setActionView(iv);
        menu.findItem(R.id.notificacao).getActionView().setOnClickListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            revokeAccess();
//            return true;
//        }
        if (id == R.id.notificacao) {
//            menu.getItem(0).setIcon(R.drawable.sino);
//            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            ImageView iv = (ImageView)inflater.inflate(R.layout.iv_refresh, null);
//            Log.d("Raj", "entrou no if notificacao");
//            if(isNotificacaoOpen){
//                iv.startAnimation(rotate_backward);
////                menu.getItem(0).getActionView().startAnimation(rotate_backward);
//                isNotificacaoOpen = false;
//                ft = getSupportFragmentManager().beginTransaction();
//                //getSupportFragmentManager().popBackStack();
//                ft.remove(fragment);
//                ft.commit();
//                fragment = NotificacaoFragment.newInstance();
//                Log.d("Raj", "close");
//            } else {
//                iv.startAnimation(rotate_forward);
////                menu.getItem(0).getActionView().startAnimation(rotate_forward);
//                isNotificacaoOpen = true;
//                ft = getSupportFragmentManager().beginTransaction();
//                //getSupportFragmentManager().popBackStack();
//                ft.replace(R.id.fragment_container, fragment);
//                ft.commit();
//                Log.d("Raj","open");
//
//            }
//            menu.findItem(R.id.notificacao).setActionView(iv);

            //getSupportFragmentManager().popBackStackImmediate();
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
