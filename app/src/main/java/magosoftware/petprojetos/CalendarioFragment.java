package magosoftware.petprojetos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CalendarioFragment extends BaseFragment implements LineAdapterEvento.OnItemClicked, View.OnClickListener {

    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    private LineAdapterEvento mAdapter;
    private List<Evento> mModels;
    private List<List<Evento>> listaModels;
    StorageReference storageRef;
    RecyclerView mRecyclerView;
    DatabaseReference dbPetUsuario;
    String tarefaPath;
    String situacaoTarefas;
    private LinearLayout containerTarefas;
    ViewPager viewPager;
    private int pagerViewPosition = 1;
    private boolean inicio = true;
    private TextView tituloAno;
    private ImageView setaDireita;
    private ImageView setaEsquerda;
    private ValueEventListenerSend valueEventListener;
    private ProgressBar progressBar;
    private boolean movimentoParaFora = false;
    private String nomeProjeto;
    private String nodePET;
    private TextView aviso;
    private boolean inicio2 = true;
    private TabLayout tabLayout;
    private SharedPreferences sharedPref;
    private int posicaoAtual;
    private int anoSelecionado = 2018;
    private int mesSelecionado;
    private int diaAtual;
    private int mesAtual;
    private int anoAtual;
    private PagerAdapterEvento pagerAdapter;
    private List<Evento> eventosEncontrados;
    private int comparaContTarefas = 0;
    private RelativeLayout menuAno;
    private LinearLayout diasSemana;

    public static CalendarioFragment newInstance() {
        CalendarioFragment calendarioFragment = new CalendarioFragment();
        return calendarioFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        sharedPref = getActivity().getSharedPreferences("todoApp", 0);
        nodePET = sharedPref.getString("node_meu_pet", "nada");
        Log.d("DEV/TAREFASFRAGMENT", "node_meu_pet: "+nodePET);
        dbPetUsuario = mDatabase.child("Usuarios").child(user.getUid()).child("pet").child(nodePET);
//        Log.d("TAREFASFRAGMENT", tarefaPath);
        return inflater.inflate(R.layout.eventos, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);
        viewPager = (ViewPager) getView().findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(12);
        tabLayout = (TabLayout) getView().findViewById(R.id.tab_layout);
        getView().findViewById(R.id.seta_esquerda).setOnClickListener(this);
        getView().findViewById(R.id.seta_direita).setOnClickListener(this);
        menuAno = getView().findViewById(R.id.menu_ano);
        diasSemana = getView().findViewById(R.id.dias_semana);
        menuAno.setVisibility(View.GONE);
        diasSemana.setVisibility(View.GONE);
        tituloAno = getView().findViewById(R.id.titulo_ano);
        pagerAdapter = new PagerAdapterEvento(getActivity());
        progressBar = getView().findViewById(R.id.progress_bar);
        Calendar c = Calendar.getInstance();
        anoSelecionado = c.get(Calendar.YEAR);
        anoAtual = anoSelecionado;
        mesSelecionado = c.get(Calendar.MONTH);
        mesAtual = mesSelecionado+1;
        diaAtual = c.get(Calendar.DAY_OF_MONTH);
        getEventos();
    }

    public void setupViewPager() {
        Log.d("DEV/CALENDARIOFRAG", "setupViewPager");
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(mesSelecionado);
        viewPager.addOnPageChangeListener(new OnPageChangeListenerSend(this) {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(inicio) {
                    Log.d("DEV/TAREFASCONCENT", "INICIOU");
                    inicio = false;
                    setupRecycler(R.id.lista_dias);
                    setDiasMes();
                }
            }

            @Override
            public void onPageSelected(int position) {
                mesSelecionado = position;
                setupRecycler(R.id.lista_dias);
                mAdapter.replaceAll(listaModels.get(position));
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(0);
                mAdapter.setOnClick(CalendarioFragment.this);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        setupTabItems();
    }

    public void setupTabItems() {
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    public void setDiasMes() {
        Log.d("DEV/CALENDARIOFRAG", "setDiasMes");
        progressBar.setVisibility(View.VISIBLE);
        listaModels = new ArrayList<>();
        for(int j = 1; j<=12; j++) {
            Calendar calendar = new GregorianCalendar(anoSelecionado, j-1, 1);
            int diasNoMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            int contDiaSemana = calendar.get(Calendar.DAY_OF_WEEK);
            mModels = new ArrayList<>();
            for (int i = 1; i < contDiaSemana; i++) {
                mModels.add(new Evento(true));
            }
            for (int i = 1; i <= diasNoMes; i++) {
                Log.d("DEV/CALENDARIOFRAG", "i = "+i);
                boolean jaFoi = false;
                Evento eventoDoDia = new Evento();
                for (Evento listaEventos : eventosEncontrados) {
                    if(listaEventos.diaSemana == contDiaSemana ||
                            (listaEventos.getAno() == anoSelecionado && listaEventos.getMes() == j && listaEventos.getDia() == i) ||
                            (listaEventos.getMes() == j && listaEventos.getDia() == i && listaEventos.isAniversario())) {
                        Log.d("DEV/CALENDARIOFRAG", "Adicionou evento");
                        eventoDoDia.diaSemana = contDiaSemana;
                        eventoDoDia.dia = i;
                        eventoDoDia.mes = j;
                        eventoDoDia.ano = anoSelecionado;
                        eventoDoDia.caminhoTarefa = eventoDoDia.caminhoTarefa+" "+listaEventos.caminhoTarefa;
                        eventoDoDia.caminhoReuniao = eventoDoDia.caminhoReuniao+" "+listaEventos.caminhoReuniao;
                        eventoDoDia.caminhoAniversario = eventoDoDia.caminhoAniversario+" "+listaEventos.caminhoAniversario;
                        eventoDoDia.caminhoEvento = eventoDoDia.caminhoEvento+" "+listaEventos.caminhoEvento;
                        if(listaEventos.isTarefa()) {
                            eventoDoDia.tarefa = true;
                        }
                        if(listaEventos.isReuniao()) {
                            eventoDoDia.reuniao = true;
                        }
                        if(listaEventos.isAniversario()) {
                            eventoDoDia.aniversario = true;
                        }
                        if(listaEventos.isEvento()) {
                            eventoDoDia.evento = true;
                        }
                        jaFoi = true;
                    }
                }
                if(jaFoi) {
                    if(anoAtual == anoSelecionado && mesAtual == j && i == diaAtual) {
                        eventoDoDia.hoje = true;
                    }
                    mModels.add(eventoDoDia);
                } else {
                    Evento diaNormal = new Evento(i, j, anoSelecionado, contDiaSemana);
                    if(anoAtual == anoSelecionado && mesAtual == j && i == diaAtual) {
                        diaNormal.hoje = true;
                    }
                    mModels.add(diaNormal);
                }
                contDiaSemana++;
                if (contDiaSemana > 7) {
                    contDiaSemana = 1;
                }
            }
            listaModels.add(mModels);
        }
        progressBar.setVisibility(View.GONE);
        menuAno.setVisibility(View.VISIBLE);
        diasSemana.setVisibility(View.VISIBLE);
        mAdapter.replaceAll(listaModels.get(mesSelecionado));
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }

    public void getEventos() {
        eventosEncontrados = new ArrayList<>();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DataSnapshot petUsuario = dataSnapshot.child("Usuarios").child(user.getUid()).child("pet").child(nodePET);
                DataSnapshot timePET = dataSnapshot.child("PETs").child(nodePET).child("time");

                if (petUsuario.hasChild("reunioes")) {
                    for (DataSnapshot listSnapshot : petUsuario.child("reunioes").getChildren()) {
                        Log.d("DEV/CALENDARIOFRAG", "Adicionou reunião à lista");
                        String diaSemana = listSnapshot.child("data").getValue(String.class).split(" ")[0];
                        String caminho = listSnapshot.child("caminho").getValue(String.class);
                        caminho = caminho + "/reunioes";
                        eventosEncontrados.add(new Evento(getDiaSemana(diaSemana),
                                false, true, false, false,
                                "", caminho, "", ""));
                    }
                }
                if (petUsuario.hasChild("tarefas")) {
                    for (DataSnapshot listSnapshot : petUsuario.child("tarefas").getChildren()) {
                        Log.d("DEV/CALENDARIOFRAG", "Adicionou tarefa à lista");
                        String caminho = listSnapshot.child("caminho").getValue(String.class);
                        comparaContTarefas++;
                        String[] data = dataSnapshot.child(caminho).child("prazo").getValue(String.class).split("/");
                        int dia = Integer.parseInt(data[0]);
                        int mes = Integer.parseInt(data[1]);
                        int ano = Integer.parseInt(data[2]);
                        Log.d("DEV/CALENDARIOFRAG", "Data: "+dia+"/"+mes+"/"+ano);
                        eventosEncontrados.add(new Evento(dia, mes, ano,
                                true, false, false, false,
                                caminho, "", "", ""));
                    }
                }
                if (timePET.hasChildren()) {
                    for (DataSnapshot listSnapshot : timePET.getChildren()) {
                        for (DataSnapshot subListSnapshot : listSnapshot.getChildren()) {
                            String uid = subListSnapshot.getKey();
                            String data[] = dataSnapshot.child("Usuarios").child(uid).child("nascimento").getValue(String.class).split("/");
//                            String nome = dataSnapshot.child("Usuarios").child(uid).child("nome").getValue(String.class);
                            String caminho = "Usuarios/"+uid+"/nascimento";
                            int dia = Integer.parseInt(data[0]);
                            int mes = Integer.parseInt(data[1]);
                            int ano = Integer.parseInt(data[2]);
                            Log.d("DEV/CALENDARIOFRAG", "Data: "+dia+"/"+mes+"/"+ano);
                            eventosEncontrados.add(new Evento(dia, mes, ano,
                                    false, false, true, false,
                                    "", "", caminho, ""));
                        }
                    }
                }
                setupViewPager();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public int getDiaSemana(String data) {
        int diaSemana = 0;
        if (data.equals("DOMINGO")) {
            diaSemana = 1;
        } else if (data.equals("SEGUNDA")) {
            diaSemana = 2;
        } else if (data.equals("TERÇA")) {
            diaSemana = 3;
        } else if (data.equals("QUARTA")) {
            diaSemana = 4;
        } else if (data.equals("QUINTA")) {
            diaSemana = 5;
        } else if (data.equals("SEXTA")) {
            diaSemana = 6;
        } else if (data.equals("SÁBADO")) {
            diaSemana = 7;
        }
        return  diaSemana;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
//        if(id == R.id.adicionar_tarefa) {
//            Intent intent = new Intent(getActivity(), TarefasEditActivity.class);
//            intent.putExtra("tarefa_path", tarefaPath);
//            intent.putExtra("situacao_tarefa", "nada");
//            intent.putExtra("nome_projeto", nomeProjeto);
//            startActivity(intent);
//            viewPager.setCurrentItem(0);
//        }
        if(id == R.id.seta_esquerda) {
//            progressBar.setVisibility(View.VISIBLE);
            anoSelecionado = anoSelecionado - 1;
            String anoSelecionadoString = Integer.toString(anoSelecionado);
            tituloAno.setText(anoSelecionadoString);
            setDiasMes();
        }
        if(id == R.id.seta_direita) {
//            progressBar.setVisibility(View.VISIBLE);
            anoSelecionado = anoSelecionado + 1;
            String anoSelecionadoString = Integer.toString(anoSelecionado);
            tituloAno.setText(anoSelecionadoString);
            setDiasMes();
        }
    }

    @Override
    public void onItemClick(int position, int id, final int dia, final int mes, final int ano, int diaSemana,
                            String caminhoReuniao, String caminhoTarefa, String caminhoAniversario, String caminhoEvento) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("path_reuniao", caminhoReuniao);
        bundle.putString("path_tarefa", caminhoTarefa);
        bundle.putString("path_aniversario", caminhoAniversario);
        bundle.putString("path_evento", caminhoEvento);
        bundle.putInt("dia", dia);
        bundle.putInt("mes", mes);
        bundle.putInt("ano", ano);
        Fragment fragment = ListaEventos.newInstance();
        fragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
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
        Log.d("DEV/TAREFASCONC", "Entrou");
        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView = pagerAdapter.listaViews.get(mesSelecionado).findViewById(recyclerLayout);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new LineAdapterEvento(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnClick(this);
        Log.d("DEV/TAREFASCONC", "Passou do trem");
    }
}