//package magosoftware.petprojetos;
//
//import android.support.v4.app.FragmentTransaction;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class NotificacaoFragment extends BaseFragment implements LineAdapterNotificacao.OnItemClicked{
//
//    private FirebaseUser user;
//    private FirebaseAuth mAuth;
//    private RecyclerView recyclerView;
//    private LineAdapterNotificacao mAdapter;
//    private ValueEventListener valueEventListener;
//    private List<Notificacao> mModels;
//    private int j = 0;
//    private int i = 0;
//    private String nodePet;
//    private String nodeProjeto;
//    private DatabaseReference dbPetUsuario;
//    private TextView aviso;
//
//    public static NotificacaoFragment newInstance() {
//        NotificacaoFragment notificacaoFragment = new NotificacaoFragment();
//        return notificacaoFragment;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mAuth = FirebaseAuth.getInstance();
//        user = mAuth.getCurrentUser();
//        return inflater.inflate(R.layout.notificacao_layout, container, false);
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mDatabase.child("Usuarios").child(user.getUid()).child("update").setValue(false);
//        dbPetUsuario = mDatabase.child("Usuarios").child(user.getUid()).child("pet");
//        recyclerView = getView().findViewById(R.id.lista_notificacoes);
//        aviso = getView().findViewById(R.id.aviso);
//        aviso.setVisibility(View.GONE);
//        setupRecyclerView();
//        setupLista();
//        mAdapter.setOnClick(this);
//    }
//
//    private void setupLista() {
//        Log.d("DEV/NOTIFICACAO", "setupLista");
//        mModels = new ArrayList<>();
//        valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("DEV/NOTIFICACAO", "PrimeiroOnDataChange");
//                for(DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
//                    String nodePET = listSnapshot.getKey();
//                    if (listSnapshot.hasChild("tarefas") || listSnapshot.hasChild("reunioes")) {
//                        Log.d("DEV/NOTIFICACAO", "PrimeiroIf");
//                        i = 0;
//                        j = 0;
//                        for (DataSnapshot subListSnapshoot : listSnapshot.child("tarefas").getChildren()) {
////                            Log.d("DEV/NOTIFICACAO", ""+subListSnapshoot.child("nova").getValue(Boolean.class));
//                            if (subListSnapshoot.child("nova").getValue(Boolean.class).equals(true)) {
//                                i++;
//                                Log.d("DEV/NOTIFICACAO", "SegundoIf");
////                                mDatabase.child("Usuarios").child(user.getUid()).child("pet")
////                                        .child(nodePET).child("tarefas")
////                                        .child(subListSnapshoot.getKey())
////                                        .child("nova")
////                                        .setValue(false);
//                                mDatabase.child(subListSnapshoot.child("caminho").getValue(String.class))
//                                        .addListenerForSingleValueEvent(new ValueEventListenerSend(subListSnapshoot.child("caminho").getValue(String.class)) {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                j++;
//                                                Log.d("DEV/NOTIFICACAO", "SegundoOnDataChange");
//                                                mModels.add(new Notificacao("tarefa",
//                                                        dataSnapshot.child("titulo").getValue(String.class),
//                                                        dataSnapshot.child("prazo").getValue(String.class),
//                                                        (String) variavel, "Você foi marcado em uma tarefa"));
//                                                if(i == j) {
//                                                    mAdapter.replaceAll(mModels);
//                                                    mAdapter.notifyDataSetChanged();
//                                                    mModels.clear();
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//
//                                            }
//                                        });
//                            }
//                        }
//                        if(i == 0) {
//                            mAdapter.replaceAll(mModels);
//                            mAdapter.notifyDataSetChanged();
//                            mModels.clear();
//                            semNotificacoes();
//                        }
//                        else {
//                            aviso.setVisibility(View.GONE);
//                            mModels.clear();
//                        }
//                        for (DataSnapshot subListSnapshoot : listSnapshot.child("reunioes").getChildren()) {
//                        }
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        dbPetUsuario.addValueEventListener(valueEventListener);
//    }
//
//    private void semNotificacoes() {
//        aviso.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.d("DEV/NOTIFICACOES","onPause");
//        dbPetUsuario.removeEventListener(valueEventListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        Log.d("DEV/NOTIFICACOES","onStop");
//        dbPetUsuario.removeEventListener(valueEventListener);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.d("DEV/NOTIFICACOES","onDestroy");
//        dbPetUsuario.removeEventListener(valueEventListener);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        dbPetUsuario.addValueEventListener(valueEventListener);
//    }
//
////    @Override
////    public void onDestroy() {
////        Log.d("DEV/NOTIFICACOES","onDestroy");
////        super.onDestroy();
////        FragmentTransaction ft;
////        ft = getActivity().getSupportFragmentManager().beginTransaction();
//////            ft.addToBackStack(null);
////        getActivity().getSupportFragmentManager().popBackStack();
////        ft.remove(this);
////        ft.commit();
////    }
//
//    @Override
//    public void onItemClick(int position, String nome, final String node) {
//        nodePet = node.split("/")[1];
//        try {
//            nodeProjeto = node.split("PETs/"+nodePet+"projetos")[0];
//        } catch (Exception e) {
//
//        }
//        mDatabase.child("PETs").child(nodePet).addListenerForSingleValueEvent(new ValueEventListenerSend(nodeProjeto, nodePet) {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String nomeProjeto = dataSnapshot.child("nome").getValue(String.class);
//                String nomeProjetoSeparado = dataSnapshot.child("projetos").child((String) variavel).child("nome").getValue(String.class);
//                if(nomeProjetoSeparado != null) {
//                    nomeProjeto = nomeProjeto + "-" + dataSnapshot.child("projetos").child((String) variavel).child("nome").getValue(String.class);
//                }
//                int indice1 = node.indexOf("/tarefas/")+"/tarefas/".length();
//                int indice2 = node.indexOf("/", indice1);
//                String situacaoTarefa = node.substring(indice1, indice2);
//                Intent intent = new Intent(getActivity(), TarefasEditActivity.class);
//                dbPetUsuario.child((String) variavel2).child("tarefas")
//                        .child(node.split(situacaoTarefa+"/")[1])
//                        .child("nova")
//                        .setValue(false);
//                intent.putExtra("nome_projeto", nomeProjeto);
//                intent.putExtra("situacao_tarefa", situacaoTarefa);
//                intent.putExtra("node", node.split(situacaoTarefa+"/")[1]);
//                intent.putExtra("tarefa_path", node.split("/tarefas/")[0]);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void setupRecyclerView() {
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(layoutManager);
//        mAdapter = new LineAdapterNotificacao();
//        recyclerView.setAdapter(mAdapter);
//    }
//}
