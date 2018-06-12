package magosoftware.petprojetos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TarefasConcentradas extends BaseFragment implements LineAdapterTarefa.OnItemClicked, View.OnClickListener{

    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    private LineAdapterTarefa mAdapter;
    private List<Tarefa> mModels;
    private List<Tarefa> mModelsFazer;
    private List<Tarefa> mModelsFazendo;
    private List<Tarefa> mModelsFeito;
    StorageReference storageRef;
    RecyclerView mRecyclerView;
    DatabaseReference dbTarefa;
    String tarefaPath;
    String situacaoTarefas;
    private LinearLayout containerTarefas;
    ViewPager viewPager;
    private int pagerViewPosition = 1;
    private boolean inicio = true;
    private TextView tituloTarefas;
    private ImageView setaDireita;
    private ImageView setaEsquerda;
    private ValueEventListenerSend valueEventListener;
    private ProgressBar progressBar;
    private boolean movimentoParaFora = false;
    private String nomeProjeto;
    private String nodePET;
    private TextView aviso;
    private boolean inicio2 = true;

    public static TarefasConcentradas newInstance() {
        TarefasConcentradas tarefasConcentradas = new TarefasConcentradas();
        return tarefasConcentradas;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        nodePET = getArguments().getString("node_pet");
        Log.d("DEV/TAREFASFRAGMENT", "node_pet: "+nodePET);
        tarefaPath = getArguments().getString("tarefa_path");
        nomeProjeto = getArguments().getString("nome_projeto");
        situacaoTarefas = "fazendo";
        dbTarefa = mDatabase.child(tarefaPath);
        Log.d("TAREFASFRAGMENT", tarefaPath);

        return inflater.inflate(R.layout.tarefas_concentradas, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);
        containerTarefas = getView().findViewById(R.id.container_tarefas);
        tituloTarefas = getView().findViewById(R.id.titulo_tarefas);
        setaDireita = getView().findViewById(R.id.seta_direita);
        setaEsquerda = getView().findViewById(R.id.seta_esquerda);
        setaEsquerda.setOnClickListener(this);
        setaDireita.setOnClickListener(this);
        progressBar = getView().findViewById(R.id.progress_bar);
        aviso = getView().findViewById(R.id.aviso);
        aviso.setVisibility(View.GONE);
        setupEventListener();
        getView().findViewById(R.id.adicionar_tarefa).setOnClickListener(this);
        viewPager = (ViewPager) getView().findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapterTarefas(getActivity()));
        viewPager.setCurrentItem(1);
//        setupRecycler(R.id.lista_tarefas);
        viewPager.addOnPageChangeListener(new OnPageChangeListenerSend(this) {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                mAdapter.setOnClick(TarefasConcentradas.class);
//                Log.d("DEV/TAREFASCONCENT", "OnPageScrolled: "+position);
                if(inicio) {
                    pagerViewPosition = 0;
                    Log.d("DEV/TAREFASCONCENT", "INICIOU");
                    inicio = false;
                    setupRecycler(R.id.lista_tarefas);
                    setupLista();
//                    setupLista();
                }
                if((position == 0 || position == 2) && positionOffset == 0) {
                    movimentoParaFora = true;
                }
                else {
                    movimentoParaFora = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("DEV/TAREFASCONCENT", "OnPageSelected");
                dbTarefa.child("tarefas").child(situacaoTarefas).removeEventListener(valueEventListener);
                if(position == 0) {
                    pagerViewPosition = 0;
                    setupRecycler(R.id.lista_tarefas_fazer);

                    mAdapter.replaceAll(mModelsFazer);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(0);
                    mAdapter.setOnClick((LineAdapterTarefa.OnItemClicked) variavel1);

                    if(mModelsFazer.isEmpty()) {
                        Log.d("DEV/TAREFASCONCENT", "mModelsFazer.isEmpty() == true");
                        aviso.setVisibility(View.VISIBLE);
                    }
                    else {
                        aviso.setVisibility(View.GONE);
                    }

                    situacaoTarefas = "fazer";
                    tituloTarefas.setText("FAZER");
                    setaEsquerda.setVisibility(View.GONE);
                    setaDireita.setVisibility(View.VISIBLE);
                }
                else if(position == 1) {
                    pagerViewPosition = 1;
                    setupRecycler(R.id.lista_tarefas);

                    mAdapter.replaceAll(mModelsFazendo);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(0);
                    mAdapter.setOnClick((LineAdapterTarefa.OnItemClicked) variavel1);

                    if(mModelsFazendo.isEmpty()) {
                        Log.d("DEV/TAREFASCONCENT", "mModelsFazendo.isEmpty() == true");
                        aviso.setVisibility(View.VISIBLE);
                    }
                    else {
                        aviso.setVisibility(View.GONE);
                    }

                    situacaoTarefas = "fazendo";
                    tituloTarefas.setText("FAZENDO");
                    setaEsquerda.setVisibility(View.VISIBLE);
                    setaDireita.setVisibility(View.VISIBLE);
                }
                else if(position == 2) {
                    pagerViewPosition = 2;
                    setupRecycler(R.id.lista_tarefas_feitas);

                    mAdapter.replaceAll(mModelsFeito);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(0);
                    mAdapter.setOnClick((LineAdapterTarefa.OnItemClicked) variavel1);

                    if(mModelsFeito.isEmpty()) {
                        Log.d("DEV/TAREFASCONCENT", "mModelsFeito.isEmpty() == true");
                        aviso.setVisibility(View.VISIBLE);
                    }
                    else {
                        aviso.setVisibility(View.GONE);
                    }

                    situacaoTarefas = "feito";
                    tituloTarefas.setText("FEITO");
                    setaEsquerda.setVisibility(View.VISIBLE);
                    setaDireita.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("DEV/TAREFASCONCENT", "OnPageScrollStateChanged " + state);
                if(state == ViewPager.SCROLL_STATE_DRAGGING && !movimentoParaFora) {
//                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
//        getView().findViewById(R.id.add_tarefa).setOnClickListener(TarefasConcentradas.class);
//        setupRecycler();
//        setupLista();
        Log.d("ENTROU4", "PASSOU");
//        mAdapter.setOnClick(this);
    }

    private void refreshLayout() {
        Log.d("DEV/TAREFASCONCEN", "Refresh");
        if(pagerViewPosition == 0) {
            setupRecycler(R.id.lista_tarefas_fazer);
            mAdapter.replaceAll(mModelsFazer);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(0);
            mAdapter.setOnClick(this);

            if(mModelsFazer.isEmpty()) {
                Log.d("DEV/TAREFASCONCENT", "mModelsFazer.isEmpty() == true");
                aviso.setVisibility(View.VISIBLE);
            }
            else {
                aviso.setVisibility(View.GONE);
            }
        }
        else if(pagerViewPosition == 1) {
            setupRecycler(R.id.lista_tarefas);
            mAdapter.replaceAll(mModelsFazendo);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(0);
            mAdapter.setOnClick(this);

            if(mModelsFazer.isEmpty()) {
                Log.d("DEV/TAREFASCONCENT", "mModelsFazendo.isEmpty() == true");
                aviso.setVisibility(View.VISIBLE);
            }
            else {
                aviso.setVisibility(View.GONE);
            }
        }
        else if(pagerViewPosition == 2) {
            setupRecycler(R.id.lista_tarefas_feitas);
            mAdapter.replaceAll(mModelsFeito);
            mAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(0);
            mAdapter.setOnClick(this);

            if(mModelsFazer.isEmpty()) {
                Log.d("DEV/TAREFASCONCENT", "mModelsFeito.isEmpty() == true");
                aviso.setVisibility(View.VISIBLE);
            }
            else {
                aviso.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.adicionar_tarefa) {
            Intent intent = new Intent(getActivity(), TarefasEditActivity.class);
            intent.putExtra("tarefa_path", tarefaPath);
            intent.putExtra("situacao_tarefa", "nada");
            intent.putExtra("nome_projeto", nomeProjeto);
            startActivity(intent);
            viewPager.setCurrentItem(0);
        }
        if(id == R.id.seta_esquerda) {
            progressBar.setVisibility(View.VISIBLE);
            viewPager.setCurrentItem(pagerViewPosition-1);
        }
        if(id == R.id.seta_direita) {
            progressBar.setVisibility(View.VISIBLE);
            viewPager.setCurrentItem(pagerViewPosition+1);
        }
    }

    @Override
    public void onItemClick(int position, int id, final String nome, final String node) {
        if(id == R.id.card) {
            Intent intent = new Intent(getActivity(), TarefasEditActivity.class);
            intent.putExtra("tarefa_path", tarefaPath);
            intent.putExtra("situacao_tarefa", situacaoTarefas);
            intent.putExtra("nome_projeto", nomeProjeto);
            intent.putExtra("node", node);
            startActivity(intent);
        }
        if(id == R.id.frame_deletar) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Você tem certeza?")
                    .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbTarefa.child("tarefas").child(situacaoTarefas).child(node).removeValue();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
        if(id == R.id.frame_concluido) {
//            if (situacaoTarefas.equals("fazer") || situacaoTarefas.equals("fazendo")) {
//                new AlertDialog.Builder(getActivity())
//                        .setTitle("A tarefa foi concluída?")
//                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dbTarefa.child("tarefas").child(situacaoTarefas).child(node).addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        Object data = dataSnapshot.getValue();
//                                        dbTarefa.child("tarefas").child("feito").child(node).setValue(data, new DatabaseReference.CompletionListener() {
//                                            @Override
//                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                                dbTarefa.child("tarefas").child(situacaoTarefas).child(node).removeValue();
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//                                dialog.dismiss();
//                            }
//                        })
//                        .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .create()
//                        .show();
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Mover Tarefa");
            builder.setItems(getResources().getStringArray(R.array.opcoes_tarefa), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which == 0 && situacaoTarefas.equals("fazer")) {

                    } else if(which == 0) {
                        mudaTarefa(node,"fazer");
                    }
                    if(which == 1 && situacaoTarefas.equals("fazendo")) {

                    } else if(which == 1) {
                        mudaTarefa(node,"fazendo");
                    }
                    if(which == 2 && situacaoTarefas.equals("feito")) {

                    } else if(which == 2) {
                        mudaTarefa(node,"feito");
                    }
                }
            });
            builder.show();
//            }
//            else {
//                new AlertDialog.Builder(getActivity())
//                        .setTitle("Tem certeza?")
//                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dbTarefa.child("tarefas").child(situacaoTarefas).child(node).addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        Object data = dataSnapshot.getValue();
//                                        dbTarefa.child("tarefas").child("fazer").child(node).setValue(data, new DatabaseReference.CompletionListener() {
//                                            @Override
//                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                                                dbTarefa.child("tarefas").child(situacaoTarefas).child(node).removeValue();
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//                                dialog.dismiss();
//                            }
//                        })
//                        .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .create()
//                        .show();
//            }
        }
    }

    private void mudaTarefa(final String node, final String tipoTarefa) {
        dbTarefa.child("tarefas").child(situacaoTarefas).child(node).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object data = dataSnapshot.getValue();
                for(DataSnapshot listsnapshot : dataSnapshot.child("time").getChildren()) {
                    String caminho = dbTarefa.child("tarefas").child(tipoTarefa).child(node).getRef().toString().split("\\.firebaseio\\.com/")[1];
                    mDatabase.child("Usuarios").child(listsnapshot.getKey())
                            .child("pet").child(nodePET).child("tarefas")
                            .child(node).child("caminho").setValue(caminho);
                }
                dbTarefa.child("tarefas").child(tipoTarefa).child(node).setValue(data, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        dbTarefa.child("tarefas").child(situacaoTarefas).child(node).removeValue();
                        if(dbTarefa.child("tarefas").child(situacaoTarefas).getKey().isEmpty()) {
                            dbTarefa.child("tarefas").child(situacaoTarefas).setValue("nada");
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void nenhumaTarefa(ViewGroup viewGroup) {
        viewGroup.setVisibility(View.GONE);
        TextView aviso = new TextView(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        aviso.setLayoutParams(params);
        aviso.setText("Não há tarefas");
        aviso.setGravity(Gravity.CENTER);
        aviso.setTextSize(20);
        CoordinatorLayout coordinator = getView().findViewById(R.id.coordinator);
        coordinator.addView(aviso, 1);
    }

    private void setupLista() {
        Log.d("DEV/TAREFASCONC", "Entrou SetupLista");
//        dbTarefa.child("tarefas").child(situacaoTarefas).removeEventListener(valueEventListener);
        dbTarefa.child("tarefas").addValueEventListener(valueEventListener);
//        dbTarefa.child("tarefas").child("fazendo").addValueEventListener(valueEventListener);
//        dbTarefa.child("tarefas").child("feito").addValueEventListener(valueEventListener);
//        dbTarefa.child("tarefas").child(situacaoTarefas).addListenerForSingleValueEvent(valueEventListener);
    }

    private void setupEventListener() {
        mModelsFazer = new ArrayList<>();
        mModelsFazendo = new ArrayList<>();
        mModelsFeito = new ArrayList<>();
        valueEventListener = new ValueEventListenerSend(this) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DEV/TAREFASCONCEN", "Entrou ValueEventListener: "+dataSnapshot.getKey());
                if(dataSnapshot.hasChildren()) {
//                    aviso.setVisibility(View.GONE);
//                    if(containerTarefas.getVisibility() == View.GONE) {
//                        containerTarefas.setVisibility(View.VISIBLE);
//                        containerTarefas.removeViewAt(1);
//                    }
                    Log.d("DEV/TAREFASCONC", "Entrou SetupLista");
//                    if(dataSnapshot.getKey().equals("fazer")) {
                    mModelsFazer.clear();
//                    }
//                    if(dataSnapshot.getKey().equals("fazendo")) {
                    mModelsFazendo.clear();
//                    }
//                    if(dataSnapshot.getKey().equals("feito")) {
                    mModelsFeito.clear();
//                    }
                    for (DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot subListSnapshot : listSnapshot.getChildren()) {
                            String node = subListSnapshot.getKey();
                            String titulo = subListSnapshot.child("titulo").getValue(String.class);
                            String descricao = subListSnapshot.child("descricao").getValue(String.class);
                            Log.d("DEV/TAREFASCONCE", "Entrou no setupEventListener: " + listSnapshot.getKey());
//                    long data = (listSnapshot.child("prazo").getValue(String.class));
                            String situacaoPrazo;
                            if (subListSnapshot.hasChild("prazo")) {
                                String data = subListSnapshot.child("prazo").getValue(String.class);
                                long dataLong = dateToMillis(data);
                                long dataAtual = System.currentTimeMillis() - 1000;
                                Log.d("DEV/DATAS", getDate(dataLong, "dd/MM/yyyy"));
                                Log.d("DEV/DATAS", getDate(dataAtual, "dd/MM/yyyy"));
                                Log.d("DEV/TAREFASFRAGMENT", titulo);
                                Log.d("DEV/DATAS", Long.toString(dataLong - dataAtual));
                                Log.d("DEV/TAREFASCONCE", "data: " + (dataLong - dataAtual));
                                if (listSnapshot.getKey().equals("feito")) {
                                    situacaoPrazo = "concluido";
                                } else if (dataLong - dataAtual < 259200000) {
                                    situacaoPrazo = "proximo";
                                } else {
                                    situacaoPrazo = "ok";
                                }
                            } else {
                                situacaoPrazo = "ok";
                            }
                            if (listSnapshot.getKey().equals("fazer")) {
                                mModelsFazer.add(new Tarefa(titulo, descricao, false, situacaoPrazo, node));
                            }
                            if (listSnapshot.getKey().equals("fazendo")) {
                                mModelsFazendo.add(new Tarefa(titulo, descricao, false, situacaoPrazo, node));
                            }
                            if (listSnapshot.getKey().equals("feito")) {
                                mModelsFeito.add(new Tarefa(titulo, descricao, false, situacaoPrazo, node));
                            }
                        }

                    }
                    progressBar.setVisibility(View.GONE);
//                    Log.d("DEV/TAREFASFRAGMENT", Integer.toString(mModels.size()));
                    if(inicio2) {
                        Log.d("DEV/TAREFASFRAGMENT", Integer.toString(mModelsFazendo.size()));
                        mAdapter.replaceAll(mModelsFazendo);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(0);
                        if(mModelsFazendo.isEmpty()) {
                            aviso.setVisibility(View.VISIBLE);
                        }
                        else {
                            aviso.setVisibility(View.GONE);
                        }
                        mAdapter.setOnClick((LineAdapterTarefa.OnItemClicked) variavel);
                        inicio2 = false;
                    }
                    else {
                        refreshLayout();
                    }

//                    mAdapter.replaceAll(mModels);
//                    mAdapter.notifyDataSetChanged();
//                    mModels.clear();
//                    mRecyclerView.scrollToPosition(0);
                }
                else {
//                    containerTarefas = getView().findViewById(R.id.container_tarefas);
//                    nenhumaTarefa(containerTarefas);
//                    aviso.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private long dateToMillis(String data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        long timeInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(data);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    private void setupRecycler(int recyclerLayout) {

        // Configurando o gerenciador de layout para ser uma lista.
        Log.d("DEV/TAREFASCONC", "Entrou");
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = viewPager.findViewById(recyclerLayout);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new LineAdapterTarefa();
        mRecyclerView.setAdapter(mAdapter);
        Log.d("DEV/TAREFASCONC", "Passou do trem");
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                layoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }
}