package magosoftware.petprojetos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MembrosFragment extends BaseFragment implements LineAdapterMembros.OnItemClicked,
        LineAdapterRequisicao.OnItemClicked, View.OnClickListener {

    FirebaseUser user;
    FirebaseAuth mAuth;
    private LineAdapterMembros mAdapter;
    private List<Usuario> mModels;
    private LineAdapterRequisicao mAdapterRequisicao;
    private List<Usuario> mModelsRequisicao;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference dbMembros;
    RecyclerView mRecyclerView;
    RecyclerView mRecyclerViewRequisicao;
    public SharedPreferences sharedPref;
    String nomePET;
    Drawable bitmapDrawableUsuario;
    FragmentTransaction ft;
    String situacao = "fora";
    private int i = 0;
    private int cont = 0;
    private int i2 = 0;
    private int cont2 = 0;
    private String membrosPath;
    ProgressBar progress;
    private Boolean tenhoCerteza = true;
    String coordenador = "";
    private LinearLayout quadroRequisicoes;
    private RelativeLayout tituloRequisicao;
    private ValueEventListener velTime;
    private String origem;

    public static MembrosFragment newInstance() {
        MembrosFragment membrosFragment = new MembrosFragment();
        return membrosFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        sharedPref = getActivity().getSharedPreferences("todoApp", 0);
        nomePET = sharedPref.getString("nome_meu_pet", "nada");
        membrosPath = getArguments().getString("membros_path");
        origem = getArguments().getString("origem");
        dbMembros = mDatabase.child(membrosPath);
        Log.d("nomePETWOW", nomePET);

        return inflater.inflate(R.layout.membros_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);

        progress = getView().findViewById(R.id.progress_bar);
        quadroRequisicoes = getView().findViewById(R.id.quadro_requisicoes);
        tituloRequisicao = getView().findViewById(R.id.titulo_requisicao);
        setupRecycler();
        setupLista();
        quadroRequisicoes.setVisibility(View.GONE);
        if(!user.getUid().equals(coordenador)) {
//            quadroRequisicoes.setVisibility(View.GONE);
        }
        Log.d("ENTROU3", "PASSOU");
        mAdapter.setOnClick(this);
        mAdapterRequisicao.setOnClick(this);
    }

    public void setupLista() {
        mModels = new ArrayList<>();
        mModelsRequisicao = new ArrayList<>();
        dbMembros.child("aguardando").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cont = 0;
                i = 0;
                if(origem.equals("projetos")) {
                    if (dataSnapshot.hasChildren()) {
                        quadroRequisicoes.setVisibility(View.VISIBLE);
                        for (DataSnapshot listSnapshotsAguardando : dataSnapshot.getChildren()) {
                            cont++;
                            String nomeUsuario = listSnapshotsAguardando.getValue(String.class);
                            String uidUsuario = listSnapshotsAguardando.getKey();
                            final StorageReference projetoRef = storageRef.child("imagensPerfil/" + uidUsuario + ".jpg");
                            Log.d("Imagem", projetoRef.getPath());
                            final long ONE_MEGABYTE = 1024 * 1024;
                            projetoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListenerString(nomeUsuario, uidUsuario) {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    try {
                                        i++;
                                        Bitmap bitmapPet = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPet, toPx(60), toPx(60), false);
                                        bitmapDrawableUsuario = new BitmapDrawable(getResources(), resizedBmp);
                                        Log.d("DEV/MEMBROS_SITUACAO", (String) variavel1);
                                        mModelsRequisicao.add(new Usuario((String) variavel1, bitmapDrawableUsuario, (String) variavel2));
                                        if (i == cont) {
                                            ordenar(mModelsRequisicao);
                                            mAdapterRequisicao.replaceAll(mModelsRequisicao);
                                            mAdapterRequisicao.notifyDataSetChanged();
                                            mModelsRequisicao.clear();
                                            mRecyclerView.scrollToPosition(0);
                                        }
                                    } catch (IllegalStateException e) {

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListenerParameter(situacao, nomeUsuario) {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.d("LOGO", "Deu merda");
                                    bitmapDrawableUsuario = getResources().getDrawable(R.drawable.pet_logo);
                                    mModels.add(new Usuario((String) variavel2, bitmapDrawableUsuario, (String) variavel1));
                                }
                            });
                        }
                    } else {
                        quadroRequisicoes.setVisibility(View.GONE);
                    }
                }
                else if(origem.equals("meupet")) {
                    if (dataSnapshot.hasChildren()) {
                        quadroRequisicoes.setVisibility(View.VISIBLE);
                        for(DataSnapshot firstListSnapshot: dataSnapshot.getChildren()) {
                            situacao = firstListSnapshot.getKey();
                            Log.d("DEV/MEMBROS", "Situação: "+situacao);
                            for(DataSnapshot listSnapshotsAguardando : firstListSnapshot.getChildren()) {
                                cont++;
                                String nomeUsuario = listSnapshotsAguardando.getValue(String.class);
                                String uidUsuario = listSnapshotsAguardando.getKey();
                                final StorageReference projetoRef = storageRef.child("imagensPerfil/" + uidUsuario + ".jpg");
                                Log.d("Imagem", projetoRef.getPath());
                                final long ONE_MEGABYTE = 1024 * 1024;
                                projetoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListenerString(nomeUsuario, uidUsuario, situacao) {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        try {
                                            i++;
                                            Bitmap bitmapPet = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPet, toPx(60), toPx(60), false);
                                            bitmapDrawableUsuario = new BitmapDrawable(getResources(), resizedBmp);
                                            Log.d("DEV/MEMBROS_SITUACAO", (String) variavel1);
                                            mModelsRequisicao.add(new Usuario((String) variavel1, bitmapDrawableUsuario, (String) variavel3, (String) variavel2));
                                            if (i == cont) {
                                                ordenar(mModelsRequisicao);
                                                mAdapterRequisicao.replaceAll(mModelsRequisicao);
                                                mAdapterRequisicao.notifyDataSetChanged();
                                                mModelsRequisicao.clear();
                                                mRecyclerView.scrollToPosition(0);
                                            }
                                        } catch (IllegalStateException e) {

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListenerParameter(situacao, nomeUsuario) {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Log.d("LOGO", "Deu merda");
                                        bitmapDrawableUsuario = getResources().getDrawable(R.drawable.pet_logo);
                                        mModels.add(new Usuario((String) variavel2, bitmapDrawableUsuario, (String) variavel1));
                                    }
                                });
                            }
                        }
                    } else {
                        quadroRequisicoes.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        velTime = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cont2 = 0;
                i2 = 0;
                if(origem.equals("projetos")) {
                    coordenador = dataSnapshot.child("coordenador").getValue(String.class);
                    for (DataSnapshot listSnapshots : dataSnapshot.child("time").getChildren()) {
                        cont2++;
                        String nomeUsuario = listSnapshots.getValue(String.class);
                        String uidUsuario = listSnapshots.getKey();
                        if (uidUsuario.equals(coordenador)) {
                            situacao = "coordenador";
                        } else {
                            situacao = "membro";
                        }
                        final StorageReference projetoRef = storageRef.child("imagensPerfil/" + uidUsuario + ".jpg");
                        Log.d("Imagem", projetoRef.getPath());
                        final long ONE_MEGABYTE = 1024 * 1024;
                        projetoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListenerString(situacao, nomeUsuario, uidUsuario) {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                try {
                                    i2++;
                                    Bitmap bitmapPet = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPet, toPx(60), toPx(60), false);
                                    bitmapDrawableUsuario = new BitmapDrawable(getResources(), resizedBmp);
                                    Log.d("DEV/MEMBROS_SITUACAO", (String) variavel1);
                                    mModels.add(new Usuario((String) variavel2, bitmapDrawableUsuario, (String) variavel1, (String) variavel3));
                                    if (i2 == cont2) {
                                        Log.d("DEV/MEMBROS_QUANTIDADE", Integer.toString(mModels.size()));
                                        Log.d("DEV/MEMBROS_QUANTIDADE2", Integer.toString(mAdapter.getItemCount()));
                                        ordenar(mModels);
                                        mAdapter.replaceAll(mModels);
                                        mAdapter.notifyDataSetChanged();
                                        mModels.clear();
                                        Log.d("DEV/MEMBROS_QUANTIDADE", Integer.toString(mModels.size()));
                                        Log.d("DEV/MEMBROS_QUANTIDADE2", Integer.toString(mAdapter.getItemCount()));
                                        mRecyclerView.scrollToPosition(0);
                                        Log.d("DEV/MEMBROS_ENTROU3", "mModelsNaVeia");
                                        progress.setVisibility(View.GONE);
                                    }
                                } catch (IllegalStateException e) {

                                }
                            }
                        }).addOnFailureListener(new OnFailureListenerParameter(situacao, nomeUsuario) {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.d("LOGO", "Deu merda");
                                bitmapDrawableUsuario = getResources().getDrawable(R.drawable.pet_logo);
                                mModels.add(new Usuario((String) variavel2, bitmapDrawableUsuario, (String) variavel1));
                            }
                        });
                    }
                }
                else if(origem.equals("meupet")) {
                    for(DataSnapshot firstListSnapshots : dataSnapshot.getChildren()) {
                        if(!firstListSnapshots.getKey().equals("aguardando")) {
                            for (DataSnapshot listSnapshots : firstListSnapshots.getChildren()) {
                                cont2++;
                                String nomeUsuario = listSnapshots.getValue(String.class);
                                String uidUsuario = listSnapshots.getKey();
                                situacao = firstListSnapshots.getKey();
                                final StorageReference projetoRef = storageRef.child("imagensPerfil/" + uidUsuario + ".jpg");
                                Log.d("Imagem", projetoRef.getPath());
                                final long ONE_MEGABYTE = 1024 * 1024;
                                projetoRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListenerString(situacao, nomeUsuario, uidUsuario) {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        try {
                                            i2++;
                                            Bitmap bitmapPet = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPet, toPx(60), toPx(60), false);
                                            bitmapDrawableUsuario = new BitmapDrawable(getResources(), resizedBmp);
                                            Log.d("DEV/MEMBROS_SITUACAO", (String) variavel1);
                                            mModels.add(new Usuario((String) variavel2, bitmapDrawableUsuario, (String) variavel1, (String) variavel3));
                                            if (i2 == cont2) {
                                                Log.d("DEV/MEMBROS_QUANTIDADE", Integer.toString(mModels.size()));
                                                Log.d("DEV/MEMBROS_QUANTIDADE2", Integer.toString(mAdapter.getItemCount()));
                                                ordenar(mModels);
                                                mAdapter.replaceAll(mModels);
                                                mAdapter.notifyDataSetChanged();
                                                mModels.clear();
                                                Log.d("DEV/MEMBROS_QUANTIDADE", Integer.toString(mModels.size()));
                                                Log.d("DEV/MEMBROS_QUANTIDADE2", Integer.toString(mAdapter.getItemCount()));
                                                mRecyclerView.scrollToPosition(0);
                                                Log.d("DEV/MEMBROS_ENTROU3", "mModelsNaVeia");
                                                progress.setVisibility(View.GONE);
                                            }
                                        } catch (IllegalStateException e) {

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListenerParameter(situacao, nomeUsuario) {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Log.d("LOGO", "Deu merda");
                                        bitmapDrawableUsuario = getResources().getDrawable(R.drawable.pet_logo);
                                        mModels.add(new Usuario((String) variavel2, bitmapDrawableUsuario, (String) variavel1));
                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        };
        dbMembros.addListenerForSingleValueEvent(velTime);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
    }

    @Override
    public void onItemClick(int position, String codigo, String situacao, String tipo) {
        if(tipo.equals("card")) {
            Bundle bundle = new Bundle();
            bundle.putString("codigo", codigo);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(this);
            fragmentTransaction.commit();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment fragment = Perfil.newInstance();
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
        else if(tipo.equals("opcao") && situacao.equals("membro") && user.getUid().equals(coordenador)) {
            setAlert(codigo);
        }
    }

    @Override
    public void onItemClick(String nome, String codigo, String tipo, String situacao) {
        if(tipo.equals("card")) {
            Bundle bundle = new Bundle();
            bundle.putString("codigo", codigo);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(this);
            fragmentTransaction.commit();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment fragment = Perfil.newInstance();
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
        else if(tipo.equals("ok")) {
            if(origem.equals("projetos")) {
                dbMembros.child("time").child(codigo).setValue(nome);
                dbMembros.child("aguardando").child(codigo).removeValue();
                dbMembros.removeEventListener(velTime);
                dbMembros.addListenerForSingleValueEvent(velTime);
            }
            else if(origem.equals("meupet")) {
                dbMembros.child(situacao).child(codigo).setValue(nome);
                dbMembros.child("aguardando").child(situacao).child(codigo).removeValue();
                mDatabase.child("Usuarios").child(codigo).child("pet").child(nomePET).child("situacao").setValue(situacao);
                dbMembros.removeEventListener(velTime);
                dbMembros.addListenerForSingleValueEvent(velTime);
            }
            Snackbar.make(getView().findViewById(R.id.master), "Solicitação Aprovada",
                    Snackbar.LENGTH_SHORT).show();
        }
        else if(tipo.equals("cancel")) {
            if(origem.equals("projetos")) {
                dbMembros.child("aguardando").child(codigo).removeValue();
                dbMembros.removeEventListener(velTime);
                dbMembros.addListenerForSingleValueEvent(velTime);
            }
            else if(origem.equals("meupet")) {
                dbMembros.child("aguardando").child(situacao).child(codigo).removeValue();
                mDatabase.child("Usuarios").child(codigo).child("pet").child(nomePET).child("situacao").setValue("rejeitado");
                dbMembros.removeEventListener(velTime);
                dbMembros.addListenerForSingleValueEvent(velTime);
            }
            Snackbar.make(getView().findViewById(R.id.master), "Solicitação Negada",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private void setupRecycler() {
        mRecyclerView = getView().findViewById(R.id.lista_membros);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        if(origem.equals("projetos")) {
            mAdapter = new LineAdapterMembros(getResources().getDrawable(R.drawable.background_contorno),
                    getResources().getDrawable(R.drawable.background_contorno_ok));
        }
        else if(origem.equals("meupet")) {
            mAdapter = new LineAdapterMembros(getResources().getDrawable(R.drawable.background_contorno),
                    getResources().getDrawable(R.drawable.background_contorno_ok),
                    getResources().getDrawable(R.drawable.background_contorno_roxo));
        }
        mRecyclerView.setAdapter(mAdapter);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                layoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        LinearLayoutManager layoutManagerRequisicao = new LinearLayoutManager(getActivity());

        mRecyclerViewRequisicao = getView().findViewById(R.id.lista_requisicoes);
        mRecyclerViewRequisicao.setLayoutManager(layoutManagerRequisicao);

        mAdapterRequisicao = new LineAdapterRequisicao();
        mRecyclerViewRequisicao.setAdapter(mAdapterRequisicao);

//        DividerItemDecoration dividerItemDecorationRequisicao = new DividerItemDecoration(mRecyclerViewRequisicao.getContext(),
//                layoutManager.getOrientation());
//        mRecyclerViewRequisicao.addItemDecoration(dividerItemDecorationRequisicao);
    }

    private int toPx(float dp){
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        return px;
    }

    private void setAlert(final String codigo) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Selecione a ação:");
        builder.setItems(getResources().getStringArray(R.array.opcoes_membros), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                temCerteza(which, codigo);
            }
        });
        builder.show();
    }

    private void temCerteza(final int escolha, final String codigo) {
        final AlertDialog.Builder builderCerteza = new AlertDialog.Builder(getActivity());
        builderCerteza.setTitle("Você tem certeza?");
        builderCerteza.setItems(getResources().getStringArray(R.array.tem_certeza), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("WHICH", Integer.toString(which));
                if (which == 0) {
                    if(escolha == 0) {
                        dbMembros.child("coordenador").setValue(codigo);
                    }
                    else if (escolha == 1) {
                        dbMembros.child("time").child(codigo).removeValue();
                    }
                }
                if (which == 1) {

                }
            }
        });
        builderCerteza.show();
    }
}