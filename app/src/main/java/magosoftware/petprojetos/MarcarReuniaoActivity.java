package magosoftware.petprojetos;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class MarcarReuniaoActivity  extends BaseActivity implements View.OnClickListener{

    FirebaseUser user;
    FirebaseAuth mAuth;
    private Button okButton;
    private Button cancelButton;
    String[] dias;
    LinkedHashMap<String, List<String>> lstItensGrupo;
    LinkedHashMap<Integer, List<String>> lstFiltra;
    List<String> lstGrupo;
    ExpandableListAdapterCompara adaptador;
    private LinkedHashMap<String, List<String>> horariosCoordenador;
    private String reunioesPath;
    private TextView dataSemanal;
    private RelativeLayout clickHorariosEquipe;
    private TextView horarioSemanal;
    private TextView horarioFinalSemanal;
    private Switch semanalSwitch;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;
    private TextView tvTodos;
    private TextView tvMelhores;
    private ColorStateList corPadrao;
    private ColorStateList corPadraoDiaSemana;
    private String diaSelecionado = "";
    private TextView segunda;
    private TextView terca;
    private TextView quarta;
    private TextView quinta;
    private TextView sexta;
    private TextView sabado;
    private TextView domingo;
    DatabaseReference dbReuniao;
    private String nomeProjeto;
    private DateFormat format;
    private ProgressBar progressBar;
    private String nodePET;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marcar_reuniao_activity);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Intent intent = getIntent();
        reunioesPath = intent.getStringExtra("reunioes_path");
        nomeProjeto = intent.getStringExtra("node_projeto");
        dbReuniao = mDatabase.child(reunioesPath);
        okButton = findViewById(R.id.aceitar);
        cancelButton = findViewById(R.id.cancelar);
        dataSemanal = findViewById(R.id.data_text);
        clickHorariosEquipe = findViewById(R.id.click_horarios_equipe);
        horarioSemanal = findViewById(R.id.horario_text);
        horarioFinalSemanal = findViewById(R.id.horario_final_text);
        semanalSwitch = findViewById(R.id.semanal_switch);
        sharedPref = this.getSharedPreferences("todoApp", 0);
        nodePET = sharedPref.getString("node_meu_pet", "nada");
        format = new SimpleDateFormat("HH:mm");
        clickHorariosEquipe.setOnClickListener(this);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        dbReuniao.addListenerForSingleValueEvent(new ValueEventListenerSend(this) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("coordenador")) {
                    for(DataSnapshot coordenadorSnap : dataSnapshot.child("coordenador").getChildren()) {
                        if (coordenadorSnap.getKey().equals(user.getUid())) {
                            dataSemanal.setOnClickListener((View.OnClickListener) variavel);
                            horarioSemanal.setOnClickListener((View.OnClickListener) variavel);
                            horarioFinalSemanal.setOnClickListener((View.OnClickListener) variavel);
                        }
                    }
                }
                else {
                    dataSemanal.setOnClickListener((View.OnClickListener) variavel);
                    horarioSemanal.setOnClickListener((View.OnClickListener) variavel);
                    horarioFinalSemanal.setOnClickListener((View.OnClickListener) variavel);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setDados();

        horariosCoordenador = new LinkedHashMap<>();
        setCoordenadorHorario();
    }

    private void setDados() {
        dbReuniao.child("reunioes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    dataSemanal.setText(dataSnapshot.child("data").getValue(String.class));
                    String[] horarioCompleto = dataSnapshot.child("horario").getValue(String.class).split("\u2013");
                    horarioSemanal.setText(horarioCompleto[0]);
                    horarioFinalSemanal.setText(horarioCompleto[1]);
                } catch (NullPointerException e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setHorarios() {
        mDatabase.child(reunioesPath).child("time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                    Log.d("DEV/MARCAR", listSnapshot.getKey());
                    mDatabase.child("Usuarios").child(listSnapshot.getKey()).child("horarios")
                            .addListenerForSingleValueEvent(new ValueEventListenerSend(listSnapshot.getValue(String.class)) {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot listSnapshot: dataSnapshot.getChildren()) {
                                        for(String listHora : listSnapshot.getValue(String.class).split(";")) {
                                            String nomeHorario = listSnapshot.getKey() + " " + listHora;
                                            try {
                                                horariosCoordenador.get(nomeHorario).add((String) variavel);
                                            } catch (NullPointerException e) {

                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setCoordenadorHorario() {
        mDatabase.child("Usuarios").child(user.getUid()).child("horarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                    for(String listHora : listSnapshot.getValue(String.class).split(";")) {
                        if(!listHora.equals("") && !listHora.contains("<reuniao>")) {
                            horariosCoordenador.put(listSnapshot.getKey() + " " + listHora, new ArrayList<String>());
                        }
                    }
                }
                setHorarios();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private List<String> setArrayHorario(String horario) {
        String[] horarioSeparado = horario.split("\u2013");
        int horarioInicioInteiro = Integer.parseInt(horarioSeparado[0].split(":")[0]);
        int horarioFimInteiro;
        List<String> arrayHorarios = new ArrayList<>();
        if(horarioSeparado[1].split(":")[1].equals("00")) {
            horarioFimInteiro = Integer.parseInt(horarioSeparado[1].split(":")[0]);
        }
        else {
            horarioFimInteiro = Integer.parseInt(horarioSeparado[1].split(":")[0])+1;
        }
        for(int i=0; i<horarioFimInteiro-horarioInicioInteiro; i++) {
            Time dateInicial = new Time((horarioInicioInteiro+i), 0, 0);
            String horarioInicialFormatado = format.format(dateInicial);
            Time dateFinal = new Time((horarioInicioInteiro+i+1), 0, 0);
            String horarioFinalFormatado = format.format(dateFinal);
            arrayHorarios.add(horarioInicialFormatado+"\u2013"+horarioFinalFormatado);
        }
        return arrayHorarios;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.aceitar) {
            final String diaDaSemana = dataSemanal.getText().toString();
            final String horarioDaSemana = horarioSemanal.getText()+"\u2013"+horarioFinalSemanal.getText();
            final List<String> arrayHorario = setArrayHorario(horarioDaSemana);
            dbReuniao.child("reunioes").child("data").setValue(diaDaSemana);
            dbReuniao.child("reunioes").child("horario").setValue(horarioDaSemana);
            dbReuniao.child("time").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                        mDatabase.child("Usuarios").child(listSnapshot.getKey()).child("pet").child(nodePET).child("reunioes")
                                .child("Projeto "+nomeProjeto)
                                .child("caminho")
                                .setValue(reunioesPath);
                        mDatabase.child("Usuarios").child(listSnapshot.getKey()).child("pet").child(nodePET).child("reunioes")
                                .child("Projeto "+nomeProjeto)
                                .child("data")
                                .setValue(diaDaSemana+" "+horarioDaSemana);
                        mDatabase.child("Usuarios").child(listSnapshot.getKey()).child("pet").child(nodePET).child("reunioes")
                                .child("Projeto "+nomeProjeto)
                                .child("nova")
                                .setValue(true);
                        mDatabase.child("Usuarios").child(listSnapshot.getKey()).child("update").setValue(true);
                        mDatabase.child("Usuarios").child(listSnapshot.getKey()).child("horarios")
                                .addListenerForSingleValueEvent(new ValueEventListenerSend(listSnapshot.getKey()) {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                                            String horarios = listSnapshot.getValue(String.class);
                                            horarios = horarios.replace("<reuniao>"+nomeProjeto+"</reuniao>", "");
                                            mDatabase.child("Usuarios")
                                                    .child((String) variavel)
                                                    .child("horarios")
                                                    .child(listSnapshot.getKey())
                                                    .setValue(horarios, new CompletionListenerParameter(listSnapshot.getKey(), horarios, variavel) {
                                                        @Override
                                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                            if (databaseError != null) {
                                                                Log.d("DEV/MARCARREUNIAO", "Deu uma merda aqui");
                                                            } else {
                                                                String diaSemanal = (String) variavel1;
                                                                String horarios = (String) variavel2;
                                                                if (diaSemanal.equals(diaDaSemana)) {
                                                                    String novoHorario = horarios;
                                                                    for (String horario : arrayHorario) {
                                                                        try {
                                                                            novoHorario = novoHorario
                                                                                    .replace(horario, "<reuniao>"+nomeProjeto+"</reuniao>"+ horario);

                                                                        } catch (NullPointerException e) {
                                                                        }
                                                                    }
                                                                    mDatabase.child("Usuarios")
                                                                            .child((String) variavel3)
                                                                            .child("horarios")
                                                                            .child(diaDaSemana)
                                                                            .setValue(novoHorario);
                                                                }
                                                            }
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.d("DEV/MARCARREUNIAO", "Deu uma merda aqui em baixo");
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("DEV/MARCARREUNIAO", "Deu uma merda aqui mais em baixo ainda");
                }
            });
            finish();
        }
        if(id == R.id.cancelar) {
            finish();
        }

        if(id == R.id.click_horarios_equipe) {
            linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.compara_horario_dialog, null, false);
            new AlertDialog.Builder(this)
                    .setView(linearLayout)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    }).show();
            tvTodos = linearLayout.findViewById(R.id.menu_todos);
            tvMelhores = linearLayout.findViewById(R.id.menu_melhores);
            corPadrao = tvMelhores.getTextColors();
            linearLayout.findViewById(R.id.menu_todos_click).setOnClickListener(this);
            linearLayout.findViewById(R.id.menu_melhores_click).setOnClickListener(this);
            setExpandable("tudo");
        }
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
        if(id == R.id.data_text) {
            String dataDaSemana = dataSemanal.getText().toString();
            linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dia_semana_picker, null, false);
            new AlertDialog.Builder(this)
                    .setTitle("Escolha o dia")
                    .setView(linearLayout)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dataSemanal.setText(diaSelecionado);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            }).show();
            segunda = linearLayout.findViewById(R.id.segunda);
            segunda.setOnClickListener(this);
            terca = linearLayout.findViewById(R.id.terca);
            terca.setOnClickListener(this);
            quarta = linearLayout.findViewById(R.id.quarta);
            quarta.setOnClickListener(this);
            quinta = linearLayout.findViewById(R.id.quinta);
            quinta.setOnClickListener(this);
            sexta = linearLayout.findViewById(R.id.sexta);
            sexta.setOnClickListener(this);
            sabado = linearLayout.findViewById(R.id.sabado);
            sabado.setOnClickListener(this);
            domingo = linearLayout.findViewById(R.id.domingo);
            domingo.setOnClickListener(this);

            if(dataDaSemana.equals("SEGUNDA")) {
                diaSelecionado = "SEGUNDA";
                clickSobreDiaDaSemana(diaSelecionado);
            }
            if(dataDaSemana.equals("TERÇA")) {
                diaSelecionado = "TERÇA";
                clickSobreDiaDaSemana(diaSelecionado);
            }
            if(dataDaSemana.equals("QUARTA")) {
                diaSelecionado = "QUARTA";
                clickSobreDiaDaSemana(diaSelecionado);
            }
            if(dataDaSemana.equals("QUINTA")) {
                diaSelecionado = "QUINTA";
                clickSobreDiaDaSemana(diaSelecionado);
            }
            if(dataDaSemana.equals("SEXTA")) {
                diaSelecionado = "SEXTA";
                clickSobreDiaDaSemana(diaSelecionado);
            }
            if(dataDaSemana.equals("SÁBADO")) {
                diaSelecionado = "SÁBADO";
                clickSobreDiaDaSemana(diaSelecionado);
            }
            if(dataDaSemana.equals("DOMINGO")) {
                diaSelecionado = "DOMINGO";
                clickSobreDiaDaSemana(diaSelecionado);
            }
        }
        if(id == R.id.horario_text) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    DateFormat format = new SimpleDateFormat("HH:mm");
                    Date date = new Time(selectedHour, selectedMinute, 0);
                    String horario = format.format(date);
                    try {
                        Date date2 = format.parse(horarioFinalSemanal.getText().toString());
                        if(date2.compareTo(date)<0) {
                            if(date2.getHours() > 0) {
                                date = new Time(date2.getHours() - 1, date2.getMinutes(), 0);
                            }
                            else {
                                date = date2;
                            }
                            horario = format.format(date);
                        }
                    }
                    catch (ParseException e) {

                    }
                    horarioSemanal.setText(horario);
                }
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
        if(id == R.id.horario_final_text) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    DateFormat format = new SimpleDateFormat("HH:mm");
                    Date date = new Time(selectedHour, selectedMinute, 0);
                    String horario = format.format(date);
                    try {
                        Date date2 = format.parse(horarioSemanal.getText().toString());
                        if(date2.compareTo(date)>0) {
                            if(date2.getHours() < 23) {
                                date = new Time(date2.getHours() + 1, date2.getMinutes(), 0);
                            }
                            else {
                                date = date2;
                            }
                            horario = format.format(date);
                        }
                    }
                    catch (ParseException e) {

                    }
                    horarioFinalSemanal.setText(horario);
                }
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        }
        if(id == R.id.segunda) {
            diaSelecionado = "SEGUNDA";
            clickSobreDiaDaSemana(diaSelecionado);
        }
        if(id == R.id.terca) {
            diaSelecionado = "TERÇA";
            clickSobreDiaDaSemana(diaSelecionado);
        }
        if(id == R.id.quarta) {
            diaSelecionado = "QUARTA";
            clickSobreDiaDaSemana(diaSelecionado);
        }
        if(id == R.id.quinta) {
            diaSelecionado = "QUINTA";
            clickSobreDiaDaSemana(diaSelecionado);
        }
        if(id == R.id.sexta) {
            diaSelecionado = "SEXTA";
            clickSobreDiaDaSemana(diaSelecionado);
        }
        if(id == R.id.sabado) {
            diaSelecionado = "SÁBADO";
            clickSobreDiaDaSemana(diaSelecionado);
        }
        if(id == R.id.domingo) {
            diaSelecionado = "DOMINGO";
            clickSobreDiaDaSemana(diaSelecionado);
        }
//        if(id == R.id.segunda) {
//            diaSelecionado = "SEGUNDA";
//            segunda.setBackgroundResource(R.drawable.circle_colorido);
//            terca.setBackgroundResource(R.drawable.circulo);
//            quarta.setBackgroundResource(R.drawable.circulo);
//            quinta.setBackgroundResource(R.drawable.circulo);
//            sexta.setBackgroundResource(R.drawable.circulo);
//            sabado.setBackgroundResource(R.drawable.circulo);
//            domingo.setBackgroundResource(R.drawable.circulo);
//        }
//        if(id == R.id.terca) {
//            diaSelecionado = "TERÇA";
//            segunda.setBackgroundResource(R.drawable.circulo);
//            terca.setBackgroundResource(R.drawable.circle_colorido);
//            quarta.setBackgroundResource(R.drawable.circulo);
//            quinta.setBackgroundResource(R.drawable.circulo);
//            sexta.setBackgroundResource(R.drawable.circulo);
//            sabado.setBackgroundResource(R.drawable.circulo);
//            domingo.setBackgroundResource(R.drawable.circulo);
//        }
//        if(id == R.id.quarta) {
//            diaSelecionado = "QUARTA";
//            segunda.setBackgroundResource(R.drawable.circulo);
//            terca.setBackgroundResource(R.drawable.circulo);
//            quarta.setBackgroundResource(R.drawable.circle_colorido);
//            quinta.setBackgroundResource(R.drawable.circulo);
//            sexta.setBackgroundResource(R.drawable.circulo);
//            sabado.setBackgroundResource(R.drawable.circulo);
//            domingo.setBackgroundResource(R.drawable.circulo);
//        }
//        if(id == R.id.quinta) {
//            diaSelecionado = "QUINTA";
//            segunda.setBackgroundResource(R.drawable.circulo);
//            terca.setBackgroundResource(R.drawable.circulo);
//            quarta.setBackgroundResource(R.drawable.circulo);
//            quinta.setBackgroundResource(R.drawable.circle_colorido);
//            sexta.setBackgroundResource(R.drawable.circulo);
//            sabado.setBackgroundResource(R.drawable.circulo);
//            domingo.setBackgroundResource(R.drawable.circulo);
//        }
//        if(id == R.id.sexta) {
//            diaSelecionado = "SEXTA";
//            segunda.setBackgroundResource(R.drawable.circulo);
//            terca.setBackgroundResource(R.drawable.circulo);
//            quarta.setBackgroundResource(R.drawable.circulo);
//            quinta.setBackgroundResource(R.drawable.circulo);
//            sexta.setBackgroundResource(R.drawable.circle_colorido);
//            sabado.setBackgroundResource(R.drawable.circulo);
//            domingo.setBackgroundResource(R.drawable.circulo);
//        }
//        if(id == R.id.sabado) {
//            diaSelecionado = "SÁBADO";
//            segunda.setBackgroundResource(R.drawable.circulo);
//            terca.setBackgroundResource(R.drawable.circulo);
//            quarta.setBackgroundResource(R.drawable.circulo);
//            quinta.setBackgroundResource(R.drawable.circulo);
//            sexta.setBackgroundResource(R.drawable.circulo);
//            sabado.setBackgroundResource(R.drawable.circle_colorido);
//            domingo.setBackgroundResource(R.drawable.circulo);
//        }
//        if(id == R.id.domingo) {
//            diaSelecionado = "DOMINGO";
//            segunda.setBackgroundResource(R.drawable.circulo);
//            terca.setBackgroundResource(R.drawable.circulo);
//            quarta.setBackgroundResource(R.drawable.circulo);
//            quinta.setBackgroundResource(R.drawable.circulo);
//            sexta.setBackgroundResource(R.drawable.circulo);
//            sabado.setBackgroundResource(R.drawable.circulo);
//            domingo.setBackgroundResource(R.drawable.circle_colorido);
//        }
    }

    private void clickSobreDiaDaSemana(String diaSelecionado) {
        if(diaSelecionado.equals("SEGUNDA")) {
            segunda.setBackgroundResource(R.drawable.circle_colorido);
        }
        else {
            segunda.setBackgroundResource(R.drawable.circulo);
        }
        if(diaSelecionado.equals("TERÇA")) {
            terca.setBackgroundResource(R.drawable.circle_colorido);
        }
        else {
            terca.setBackgroundResource(R.drawable.circulo);
        }
        if(diaSelecionado.equals("QUARTA")) {
            quarta.setBackgroundResource(R.drawable.circle_colorido);
        }
        else {
            quarta.setBackgroundResource(R.drawable.circulo);
        }
        if(diaSelecionado.equals("QUINTA")) {
            quinta.setBackgroundResource(R.drawable.circle_colorido);
        }
        else {
            quinta.setBackgroundResource(R.drawable.circulo);
        }
        if(diaSelecionado.equals("SEXTA")) {
            sexta.setBackgroundResource(R.drawable.circle_colorido);
        }
        else {
            sexta.setBackgroundResource(R.drawable.circulo);
        }
        if(diaSelecionado.equals("SÁBADO")) {
            sabado.setBackgroundResource(R.drawable.circle_colorido);
        }
        else {
            sabado.setBackgroundResource(R.drawable.circulo);
        }
        if(diaSelecionado.equals("DOMINGO")) {
            domingo.setBackgroundResource(R.drawable.circle_colorido);
        }
        else {
            domingo.setBackgroundResource(R.drawable.circulo);
        }
    }

    private void setExpandable(String opcao) {
        ExpandableListView expandableListView = (ExpandableListView) linearLayout.findViewById(R.id.lista_horarios);

        // cria os grupos
        lstItensGrupo = new LinkedHashMap<>();
        lstGrupo = new ArrayList<>();
//        progressBar = relativeLayout.findViewById(R.id.progress_bar);
//        progressBar.setVisibility(View.GONE);
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
        adaptador = new ExpandableListAdapterCompara(this, lstGrupo, lstItensGrupo);
        // define o apadtador do ExpandableListView
        expandableListView.setAdapter(adaptador);
    }

    Comparator<String> dateComparator = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            try{
                SimpleDateFormat format = new SimpleDateFormat("EEE", new Locale("pt", "BR"));
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

//    public static <K, V extends Comparable<Integer>> Map<K, List<V>> sortByValues(final Map<K, List<V>> map) {
//        Comparator<K> valueComparator =  new Comparator<K>() {
//            public int compare(K k1, K k2) {
//                int compare = ((Integer)map.get(k2).size()).compareTo(map.get(k1).size());
//                if (compare == 0) return 1;
//                else return compare;
//            }
//        };
//        Map<K, List<V>> sortedByValues = new TreeMap<K, List<V>>(valueComparator);
//        sortedByValues.putAll(map);
//        return sortedByValues;
//    }
//
//    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
//        Comparator<K> valueComparator =  new Comparator<K>() {
//            public int compare(K k1, K k2) {
//                int compare = map.get(k2).compareTo(map.get(k1));
//                if (compare == 0) return 1;
//                else return compare;
//            }
//        };
//        Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
//        sortedByValues.putAll(map);
//        return sortedByValues;
//    }
}
