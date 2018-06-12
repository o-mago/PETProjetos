package magosoftware.petprojetos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

/**
 * Created by root on 09/03/18.
 */

public class TarefasFragment extends BaseFragment implements LineAdapterTarefa.OnItemClicked, View.OnClickListener{

    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    private LineAdapterTarefa mAdapter;
    private List<Tarefa> mModels;
    StorageReference storageRef;
    RecyclerView mRecyclerView;
    DatabaseReference dbEquipe;
    String equipePath;
    String situacaoTarefas;
    private LinearLayout containerTarefas;

    public static TarefasFragment newInstance() {
        TarefasFragment tarefasFragment = new TarefasFragment();
        return tarefasFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        equipePath = getArguments().getString("equipe_path");
        situacaoTarefas = getArguments().getString("situacao_tarefas");
        dbEquipe = mDatabase.child(equipePath);
        Log.d("TAREFASFRAGMENT", equipePath);

        return inflater.inflate(R.layout.tarefas_page, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);
        containerTarefas = getView().findViewById(R.id.container_tarefas);
        getView().findViewById(R.id.adicionar_tarefa).setOnClickListener(this);
        setupRecycler();
        setupLista();
        Log.d("ENTROU4", "PASSOU");
        mAdapter.setOnClick(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.adicionar_tarefa) {
            Intent intent = new Intent(getActivity(), TarefasEditActivity.class);
            intent.putExtra("equipe_path", equipePath);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(int position,int id, final String nome, String node) {
        if(id == R.id.card) {
            Intent intent = new Intent(getActivity(), TarefasEditActivity.class);
            intent.putExtra("equipe_path", equipePath);
            intent.putExtra("node", node);
            startActivity(intent);
        }
        if(id == R.id.frame_deletar) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Você tem certeza?")
                    .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbEquipe.child("tarefas").child("fazer").child(nome).removeValue();
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
            if (situacaoTarefas.equals("fazer")) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("A tarefa foi concluída?")
                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbEquipe.child("tarefas").child("fazer").child(nome).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Object data = dataSnapshot.getValue();
                                        dbEquipe.child("tarefas").child("concluidas").child(nome).setValue(data, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                dbEquipe.child("tarefas").child("fazer").child(nome).removeValue();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
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
            else {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Tem certeza?")
                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbEquipe.child("tarefas").child("concluidas").child(nome).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Object data = dataSnapshot.getValue();
                                        dbEquipe.child("tarefas").child("fazer").child(nome).setValue(data, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                dbEquipe.child("tarefas").child("concluidas").child(nome).removeValue();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
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
        }
    }

    private void setupLista() {
        mModels = new ArrayList<>();
        dbEquipe.child("tarefas").child(situacaoTarefas).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                        String node = listSnapshot.getKey();
                        String titulo = listSnapshot.child("titulo").getValue(String.class);
                        String descricao = listSnapshot.child("descricao").getValue(String.class);
//                    long data = (listSnapshot.child("prazo").getValue(String.class));
                        String data = listSnapshot.child("prazo").getValue(String.class);
                        long dataLong = dateToMillis(data);
                        long dataAtual = System.currentTimeMillis() - 1000;
                        Log.d("DEV/DATAS", getDate(dataLong, "dd/MM/yyyy"));
                        Log.d("DEV/DATAS", getDate(dataAtual, "dd/MM/yyyy"));
                        Log.d("DEV/TAREFASFRAGMENT", titulo);
                        String situacaoPrazo;
                        Log.d("DEV/DATAS", Long.toString(dataLong - dataAtual));
                        if (situacaoTarefas.equals("concluidas")) {
                            situacaoPrazo = "concluido";
                        } else if (dataLong - dataAtual < 259200000) {
                            situacaoPrazo = "proximo";
                        } else {
                            situacaoPrazo = "ok";
                        }
                        mModels.add(new Tarefa(titulo, descricao, false, situacaoPrazo, node));
                    }
                    Log.d("DEV/TAREFASFRAGMENT", Integer.toString(mModels.size()));
                    mAdapter.replaceAll(mModels);
                    mAdapter.notifyDataSetChanged();
                    mModels.clear();
//                mAdapter.add(mModels);
                    mRecyclerView.scrollToPosition(0);
                }
                else {
                    nenhumaTarefa(containerTarefas);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void nenhumaTarefa(ViewGroup viewGroup) {
        viewGroup.removeAllViews();
        TextView aviso = new TextView(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        aviso.setLayoutParams(params);
        aviso.setText("Não há tarefas");
        aviso.setGravity(Gravity.CENTER);
        aviso.setTextSize(20);
        viewGroup.addView(aviso);
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

    private void setupRecycler() {

        // Configurando o gerenciador de layout para ser uma lista.
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = getView().findViewById(R.id.lista_tarefas);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new LineAdapterTarefa();
        mRecyclerView.setAdapter(mAdapter);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                layoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }
}
