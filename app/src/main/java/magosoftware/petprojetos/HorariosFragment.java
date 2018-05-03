package magosoftware.petprojetos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class HorariosFragment extends BaseFragment implements View.OnClickListener, LineAdapterPresenca.OnItemClicked {

    FirebaseUser user;
    FirebaseAuth mAuth;
    private Button okButton;
    private Button cancelButton;
    String[] dias;
    LinkedHashMap<String, List<String>> lstItensGrupo;
    LinkedHashMap<Integer, List<String>> lstFiltra;
    List<String> lstGrupo;
    ExpandableListAdapterCompara adaptador;
//    private LinkedHashMap<String, List<String>> horariosCoordenador;
    private String reunioesPath;
    private TextView dataSemanal;
    private LinearLayout linearLayout;
    private TextView tvTodos;
    private TextView tvMelhores;
    private ColorStateList corPadrao;
    private ColorStateList corPadraoDiaSemana;
    DatabaseReference dbReuniao;
    private String nomeProjeto;
    private LinkedHashMap<String, List<String>> horariosCoordenador;
    private DatabaseReference dbTimePet;
    public SharedPreferences sharedPref;
    private String nomePET;
    private int cont = 0;
    private int meetCont = 0;
    private ProgressBar progressBar;
    private ExpandableListView expandableListView;


    public static HorariosFragment newInstance() {
        HorariosFragment horariosFragment = new HorariosFragment();
        return horariosFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        sharedPref = getActivity().getSharedPreferences("todoApp", 0);
        nomePET = sharedPref.getString("nome_meu_pet", null);
        dbTimePet = mDatabase.child("PETs").child(nomePET).child("time");
        return inflater.inflate(R.layout.compara_horarios, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);
        tvTodos = getView().findViewById(R.id.menu_todos);
        tvMelhores = getView().findViewById(R.id.menu_melhores);
        corPadrao = tvMelhores.getTextColors();
        expandableListView = (ExpandableListView) getView().findViewById(R.id.lista_horarios);
        progressBar = getView().findViewById(R.id.progress_bar);
        getView().findViewById(R.id.menu_todos_click).setOnClickListener(this);
        getView().findViewById(R.id.menu_melhores_click).setOnClickListener(this);
        horariosCoordenador = new LinkedHashMap<>();
        setHorarios();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.menu_todos_click) {
            tvTodos.setTextColor(Color.parseColor("#03A9F4"));
            tvMelhores.setTextColor(corPadrao);
            setExpandable("tudo");
        }
        if(id == R.id.menu_melhores_click) {
            tvTodos.setTextColor(corPadrao);
            tvMelhores.setTextColor(Color.parseColor("#03A9F4"));
            setExpandable("melhores");
        }
    }

    @Override
    public void onItemClick(int position, String nome) {

    }

    private void setHorarios() {
        dbTimePet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                    if (!listSnapshot.getKey().equals("aguardando")) {
                        for (DataSnapshot subListSnapshot : listSnapshot.getChildren()) {
                            cont++;
                            Log.d("DEV/MARCAR", subListSnapshot.getKey());
                            mDatabase.child("Usuarios").child(subListSnapshot.getKey()).child("horarios")
                                    .addListenerForSingleValueEvent(new ValueEventListenerSend(subListSnapshot.getValue(String.class)) {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            meetCont++;
                                            for (DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                                                for (String listHora : listSnapshot.getValue(String.class).split(";")) {
                                                    String nomeHorario = listSnapshot.getKey() + " " + listHora;
                                                    Log.d("DEV/HORARIOSFRAG", "nomeHorario: "+nomeHorario);
                                                    Log.d("DEV/HORARIOSFRAG", "variavel: "+variavel);
                                                    try {
                                                        horariosCoordenador.get(nomeHorario).add((String) variavel);
                                                    } catch (NullPointerException e) {
                                                        if(!listHora.equals("") && !listHora.contains("<reuniao>")) {
                                                            horariosCoordenador
                                                                    .put(listSnapshot.getKey() + " " + listHora, new ArrayList<String>());
                                                            horariosCoordenador.get(nomeHorario).add((String) variavel);
                                                        }
                                                    }
                                                }
                                            }
                                            if(cont == meetCont) {
                                                Log.d("DEV/HORARIOSFRAG", "cont == meetCont");
                                                setExpandable("tudo");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setExpandable(String opcao) {
        // cria os grupos
        lstItensGrupo = new LinkedHashMap<>();
        lstGrupo = new ArrayList<>();
        int quantidade = 0;
        int maiorQuantidade = 0;

        if(opcao.equals("tudo")) {
            for (String key : horariosCoordenador.keySet()) {
                lstItensGrupo.put(key, horariosCoordenador.get(key));
            }
            lstGrupo.addAll(horariosCoordenador.keySet());
        }
        else if(opcao.equals("melhores")) {
            lstFiltra = new LinkedHashMap<>();
//            lstFiltraNomes = new HashMap<>();
            for (String key : horariosCoordenador.keySet()) {
                if(horariosCoordenador.get(key).size() > quantidade) {
                    maiorQuantidade = horariosCoordenador.get(key).size();
                }
                quantidade = horariosCoordenador.get(key).size();
                try {
                    lstFiltra.get(quantidade).add(key);
//                    lstFiltraNomes.get(key).addAll(horariosCoordenador.get(key));
                } catch (NullPointerException e) {
                    Log.d("DEV/MARCAR", "Reiniciou a parada");
                    lstFiltra.put(quantidade, new ArrayList<String>());
                    lstFiltra.get(quantidade).add(key);
//                    lstFiltraNomes.put(key, new ArrayList<String>());
//                    lstFiltraNomes.get(key).addAll(horariosCoordenador.get(key));
                }
            }
            for(String dia : lstFiltra.get(maiorQuantidade)) {
                Log.d("DEV/MARCAR", "Quantos dias");
                lstItensGrupo.put(dia, horariosCoordenador.get(dia));
                lstGrupo.add(dia);
            }
        }
        Collections.sort(lstGrupo, dateComparator);
        // cria um adaptador (BaseExpandableListAdapter) com os dados acima
        adaptador = new ExpandableListAdapterCompara(getActivity(), lstGrupo, lstItensGrupo);
        progressBar.setVisibility(View.GONE);
        // define o apadtador do ExpandableListView
        expandableListView.setAdapter(adaptador);
    }

    Comparator<String> dateComparator = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            try{
                SimpleDateFormat format = new SimpleDateFormat("EEE");
                Date d1 = format.parse(s1);
                Date d2 = format.parse(s2);
                if(d1.equals(d2)){
                    return s1.substring(s1.indexOf(" ") + 1).compareTo(s2.substring(s2.indexOf(" ") + 1));
                }else{
                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(d1);
                    cal2.setTime(d2);
                    return cal1.get(Calendar.DAY_OF_WEEK) - cal2.get(Calendar.DAY_OF_WEEK);
                }
            }catch(ParseException pe){
                throw new RuntimeException(pe);
            }
        }
    };

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        mDatabase.child("PETs").removeEventListener(valueEventListener);
//        searchView.setOnQueryTextListener(null);
//        mAdapter.setOnClick(null);
//
//        mRecyclerView = null;
//        searchView = null;
//        mAdapter = null;
//        mModels = null;
//        storage = null;
//        storageRef = null;
//        bitmapDrawablePet = null;
//        ft = null;
//        nomePet = null;
//        perfilRef = null;
//        valueEventListener = null;
//        progressBar = null;
//    }
}