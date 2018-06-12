package magosoftware.petprojetos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 09/03/18.
 */

public class ProjetosFragment extends BaseFragment implements LineAdapterProjetos.OnItemClicked, View.OnClickListener {

    FirebaseUser user;
    FirebaseAuth mAuth;
    private LineAdapterProjetos mAdapter;
    private List<Projeto> mModels;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference dbPET;
    RecyclerView mRecyclerView;
    public SharedPreferences sharedPref;
    String nomePET;
    String nodePET;
    Drawable bitmapDrawableProjeto;
    FragmentTransaction ft;
    String situacao = "fora";
    private int i = 0;
    private int cont = 0;
    private String nomeProjeto;
    ProgressBar progress;
    private FloatingActionButton addProjeto;
    private CoordinatorLayout coordinator;
    private TextView aviso;
    private ValueEventListener velSetupLista;

    public static ProjetosFragment newInstance() {
        ProjetosFragment projetosFragment = new ProjetosFragment();
        return projetosFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        sharedPref = getActivity().getSharedPreferences("todoApp", 0);
        nomePET = sharedPref.getString("nome_meu_pet", "nada");
        nodePET = sharedPref.getString("node_meu_pet", "nada");
        dbPET = mDatabase.child("PETs").child(nodePET);
        Log.d("nomePETWOW", nomePET);

        return inflater.inflate(R.layout.projetos, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);
        progress = getView().findViewById(R.id.progress_bar);
        addProjeto = getView().findViewById(R.id.add_projeto);
        coordinator = (CoordinatorLayout) getView().findViewById(R.id.coordinator);
        addProjeto.setVisibility(View.GONE);
        addProjeto.setOnClickListener(this);
        setupRecycler();
        setupLista();
        Log.d("ENTROU3", "PASSOU");
        mAdapter.setOnClick(this);
//        mAdapter.add(mModels);
//        mAdapter.setOnClick(ProjetosFragment.this);
    }

    public void setupLista() {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 8;
        mModels = new ArrayList<>();
        Log.d("ENTROU3", mDatabase.child("PETs").child(nodePET).child("projetos").toString());
        velSetupLista = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                        cont++;
                        final String node = listSnapshots.getKey();
                        Log.d("DEV/PROJETOSFRAG", "node: " + node);
                        final String nome = listSnapshots.child("nome").getValue(String.class);
                        try {
                            DataSnapshot dbRef = listSnapshots.child("time");
                            for (DataSnapshot timeSnapshot : dbRef.getChildren()) {
                                Log.d("DEV/PROJETOSFRAG", nome + " ;" + timeSnapshot.getKey() + " ;" + user.getUid());
                                if (timeSnapshot.getKey().equals(user.getUid())) {
                                    situacao = "membro";
                                    break;
                                } else {
                                    situacao = "fora";
                                }
                            }
                        } catch (NullPointerException e) {

                        }
                        try {
                            DataSnapshot dbRef = listSnapshots.child("aguardando");
                            for (DataSnapshot aguardandoSnapshot : dbRef.getChildren()) {
                                if (aguardandoSnapshot.getKey().equals(user.getUid())) {
                                    situacao = "aguardando";
                                    break;
                                } else if (!situacao.equals("membro")) {
                                    situacao = "fora";
                                }
                            }
                        } catch (NullPointerException e) {

                        }
                        Log.d("PROJETOSWOW", situacao);
                        if (nome != null) {
                            try {
                                final StorageReference projetoRef = storageRef.child("imagensProjetos/" + node + ".jpg");
                                Log.d("Imagem", projetoRef.getPath());
                                final long ONE_MEGABYTE = 1024 * 1024;
                                projetoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListenerString(situacao, node) {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        try {
                                            i++;
                                            Bitmap bitmapPet = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPet, toPx(60), toPx(60), false);
                                            bitmapDrawableProjeto = new BitmapDrawable(getResources(), resizedBmp);
                                            Log.d("LOGO", bitmapPet.toString());
//                                    nomeProjeto = projetoRef.getName();
//                                    nomeProjeto = nomeProjeto.split(".jpg")[0];
//                                    try {
//                                        nomeProjeto = nomeProjeto.replace("_", " ");
//                                    }
//                                    catch (NullPointerException e) {
//
//                                    }
                                            mModels.add(new Projeto(nome, bitmapDrawableProjeto, (String) variavel1, (String) variavel2));
//                                        bitmapPet.recycle();
//                                        bitmapPet = null;
//                                        resizedBmp.recycle();
//                                        resizedBmp = null;
                                            if (i == cont) {
                                                Log.d("QUANTIDADE", Integer.toString(mModels.size()));
                                                Log.d("QUANTIDADE2", Integer.toString(mAdapter.getItemCount()));
                                                ordenar(mModels);
                                                mAdapter.replaceAll(mModels);
                                                mAdapter.notifyDataSetChanged();
                                                mModels.clear();
                                                Log.d("QUANTIDADE", Integer.toString(mModels.size()));
                                                Log.d("QUANTIDADE2", Integer.toString(mAdapter.getItemCount()));
                                                mRecyclerView.scrollToPosition(0);
                                                Log.d("ENTROU3", "mModelsNaVeia");
                                                progress.setVisibility(View.GONE);
                                                addProjeto.setVisibility(View.VISIBLE);
                                            }
                                        } catch (IllegalStateException e) {

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        try {
                                            Log.d("LOGO", "Deu merda");
                                            bitmapDrawableProjeto = getResources().getDrawable(R.drawable.pet_logo);
                                            mModels.add(new Projeto(nomeProjeto, bitmapDrawableProjeto, situacao, node));
                                        }
                                        catch (IllegalStateException ise) {

                                        }
                                    }
                                });
                            } catch (NullPointerException e) {
                                try {
                                    bitmapDrawableProjeto = getResources().getDrawable(R.drawable.pet_logo);
                                }
                                catch (IllegalStateException ise) {

                                }
                            }
                        }
                    }
                }
                else {
                    addProjeto.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
//                    semProjeto();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        };
        mDatabase.child("PETs").child(nodePET).child("projetos").addValueEventListener(velSetupLista);
    }

    private void semProjeto() {
        aviso = new TextView(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        aviso.setLayoutParams(params);
        aviso.setText("O seu PET ainda não possui projetos");
        aviso.setGravity(Gravity.CENTER);
        aviso.setTextSize(20);
        coordinator.addView(aviso);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.add_projeto) {
            Intent intent = new Intent(getActivity(), AdicioneSeuProjeto.class);
            intent.putExtra("nome", nomePET);
            intent.putExtra("node", nodePET);
            startActivity(intent);
//            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//            fragmentTransaction.remove(this);
//            fragmentTransaction.commit();
        }
    }

    @Override
    public void onItemClick(int position, String nome, String situacao, String node) {
        if(situacao.equals("membro")) {
            Bundle bundle = new Bundle();
            bundle.putString("node_projeto", node);
            Log.d("DEV/PROJETOSFRAG", "node2: "+node);
            bundle.putString("nome_projeto", nome);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.detach(this);
            fragmentTransaction.commit();
            ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment fragment = ProjetoPageFragment.newInstance();
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(null);
            ft.commit();
//            MainActivity.changeFragment(getFragmentManager(), nome);
        }
        if(situacao.equals("fora")) {
            dbPET.child("projetos").child(node).child("aguardando").child(user.getUid()).setValue(sharedPref.getString("nome_usuario", "Cumpadi"));
            Snackbar.make(getView().findViewById(R.id.coordinator), "Solicitação enviada",
                    Snackbar.LENGTH_SHORT).show();
//            mAdapter.removeAll();
//            setupLista();
        }
        if(situacao.equals("aguardando")) {
            Snackbar.make(getView().findViewById(R.id.coordinator), "Aguardando aprovação",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("DEV/PROJETOSFRAG","onPause");
        mDatabase.child("PETs").child(nodePET).child("projetos").removeEventListener(velSetupLista);
    }

    private void setupRecycler() {

        // Configurando o gerenciador de layout para ser uma lista.
        mRecyclerView = getView().findViewById(R.id.lista_projetos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        // Adiciona o adapter que irá anexar os objetos à lista.
        // Está sendo criado com lista vazia, pois será preenchida posteriormente.
        mAdapter = new LineAdapterProjetos(getResources().getDrawable(R.drawable.background_contorno),
                getResources().getDrawable(R.drawable.background_contorno_aguardando),
                getResources().getDrawable(R.drawable.background_contorno_ok));
        //mAdapter = new LineAdapterPet(new ArrayList<>(0));
        mRecyclerView.setAdapter(mAdapter);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                layoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // Configurando um dividr entre linhas, para uma melhor visualização.
//        mRecyclerView.addItemDecoration(
//                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    public int toPx(float dp){
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        return px;
    }

}
