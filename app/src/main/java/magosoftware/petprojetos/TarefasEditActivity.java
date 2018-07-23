package magosoftware.petprojetos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.Chip;
import com.pchmn.materialchips.model.ChipInterface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by root on 16/03/18.
 */

public class TarefasEditActivity extends BaseActivity implements View.OnClickListener, ChipsInput.ChipsListener {

    private ImageView certo;
    private ImageView cancela;
    private EditText titulo;
    private ChipsInput chipsPetianos;
    private TextView dataText;
    private EditText descricao;
    private DatePicker calendario;
    private LinearLayout linearLayout;
    private String dataSelecionada;
    private String tarefaPath;
    private DatabaseReference dbTarefa;
    private String node = "NADAIEJ993R8JFN";
    private DatabaseReference dbAtualizaTarefa;
    private DatePickerDialog datePickerDialog;
    private String situacaoTarefa;
    private List<String> petianoRetirado;
    private List<String> petianoSelecionado;
    private List<String> petianoAnterior;
    private String nomePET;
    private SharedPreferences sharedPref;
    private String nodePET;
    private boolean nova = false;
    private String nomeProjeto;
    int dia;
    int mes;
    int ano;

    @Override
    public void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);
        setContentView(R.layout.tarefa_edit);

        sharedPref = this.getSharedPreferences("todoApp", 0);
        nomePET = sharedPref.getString( "nome_meu_pet", "nada");
        nodePET = sharedPref.getString( "node_meu_pet", "nada");
        certo = findViewById(R.id.certo);
        cancela = findViewById(R.id.cancela);
        dataText = findViewById(R.id.data_text);
        certo.setOnClickListener(this);
        cancela.setOnClickListener(this);
        dataText.setOnClickListener(this);
        titulo = findViewById(R.id.titulo);
        chipsPetianos = findViewById(R.id.chips_responsaveis);
        chipsPetianos.addChipsListener(this);
        descricao = findViewById(R.id.descricao);
        Intent intent = getIntent();
        nomeProjeto = intent.getStringExtra("nome_projeto");
        TextView projeto = findViewById(R.id.projeto);
        projeto.setText(nomeProjeto);
        tarefaPath = intent.getStringExtra("tarefa_path");
        situacaoTarefa = intent.getStringExtra("situacao_tarefa");
        dbTarefa = mDatabase.child(tarefaPath);
        petianoRetirado = new ArrayList<>();
        petianoSelecionado = new ArrayList<>();
        petianoAnterior = new ArrayList<>();
        getPetianos(chipsPetianos);
//        setupCalendar();
        try {
            node = intent.getStringExtra("node");
            dbAtualizaTarefa = dbTarefa.child("tarefas").child(situacaoTarefa).child(node);
            getInfoTarefa();
        }
        catch (NullPointerException e) {
            node = "NADAIEJ993R8JFN";
        }
    }

    @Override
    public void onChipRemoved(ChipInterface chip, int size) {
        Log.d("DEV/TAREFASEDIT", "onChipRemoved");
        Log.d("DEV/TAREFASEDIT", "ID: "+chip.getId());
        if(!petianoSelecionado.contains((String) chip.getId())) {
            petianoRetirado.add((String) chip.getId());
            Log.d("DEV/TAREFASEDIT", "Adicionou petiano: "+chip.getId());
        }
        else {
            petianoSelecionado.remove((String) chip.getId());
        }
    }

    @Override
    public void onChipAdded(ChipInterface chip, int size) {
        if(petianoRetirado.contains((String) chip.getId())) {
            petianoRetirado.remove((String) chip.getId());
        }
        else {
            petianoSelecionado.add((String) chip.getId());
        }
    }

    @Override
    public void onTextChanged(CharSequence var1) {

    }

    private void getInfoTarefa() {
        dbTarefa.child("tarefas").child(situacaoTarefa).child(node).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DEV/TAREFASEDIT", "getInfo");
                titulo.setText(dataSnapshot.child("titulo").getValue(String.class));
                descricao.setText(dataSnapshot.child("descricao").getValue(String.class));
                dataText.setText(dataSnapshot.child("prazo").getValue(String.class));

                for(DataSnapshot listSnapshot : dataSnapshot.child("time").getChildren()) {
                    Log.d("DEV/TAREFASEDIT", "getInfo: "+listSnapshot.getValue(String.class));
                    Chip chip = new Chip(listSnapshot.getKey(), listSnapshot.getValue(String.class), "");
                    petianoAnterior.add(chip.getLabel());
                    chipsPetianos.addChip(chip);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupCalendar() {
//        linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.calendario_alert, null, false);
//        calendario = (DatePicker) linearLayout.getChildAt(0);
//        calendario.setMinDate(System.currentTimeMillis()-1000);
//        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                dataSelecionada = dayOfMonth+"/"+month+"/"+year;
//            }
//        });
//        new AlertDialog.Builder(this)
//                .setTitle("Prazo")
//                .setMessage("Selecione o Prazo")
//                .setView(linearLayout)
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        dataText.setText(dataSelecionada);
//                        dialog.dismiss();
//                    }
//                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        dialog.dismiss();
//                    }
//                }
//        ).show();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                dataSelecionada = dayOfMonth + "/" + (month+1) + "/" + year;
                dataSelecionada = formatoData(year, month, dayOfMonth, "dd/MM/yyyy");
                dataText.setText(dataSelecionada);
            }
        }, ano, mes-1, dia);
    }

    public String formatoData(int year, int month, int date, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        Date data = new Date(year-1900, month, date);
        return format.format(data);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.certo) {
            if(node.equals("NADAIEJ993R8JFN")) {
                dbAtualizaTarefa = dbTarefa.child("tarefas").child("fazer").push();
                node = dbAtualizaTarefa.getKey();
                nova = true;
            }
            String tarefa = titulo.getText().toString();
            dbAtualizaTarefa.child("titulo").setValue(titulo.getText().toString());
            dbAtualizaTarefa.child("prazo").setValue(dataSelecionada);
            dbAtualizaTarefa.child("descricao").setValue(descricao.getText().toString());
            List<Chip> contactsSelected = (List<Chip>) chipsPetianos.getSelectedChipList();
            Log.d("DEV/TAREFASEDIT", "Tamanho contactsSelected "+contactsSelected.size());
            for (String removePetiano : petianoRetirado) {
                Log.d("DEV/TAREFASEDIT", "Entrou");
                dbAtualizaTarefa.child("time").child(removePetiano).removeValue();
                mDatabase.child("Usuarios").child(removePetiano)
                        .child("pet").child(nodePET).child("tarefas")
                        .child(node).removeValue();
            }

            for (Chip c : contactsSelected) {
                Log.d("DEV/TAREFASEDIT", (String) c.getId());
                if(nova || !petianoAnterior.contains(c.getLabel())) {
                    mDatabase.child("Usuarios").child((String) c.getId())
                            .child("pet").child(nodePET).child("tarefas")
                            .child(node).child("nova").setValue(true);
                    mDatabase.child("Usuarios").child((String) c.getId())
                            .child("pet").child(nodePET).child("tarefas")
                            .child(node).child("avisoPrazo").setValue(false);
                    mDatabase.child("Usuarios").child((String) c.getId())
                            .child("update").setValue(true);
                }
                dbAtualizaTarefa.child("time").child((String) c.getId()).setValue(c.getLabel());
//                mDatabase.child("Usuarios").child((String) c.getId())
//                        .child("pet").child(nodePET).child("tarefas")
//                        .child(node).child("caminho").setValue(tarefaPath + "/tarefas/fazer/" + node);
                mDatabase.child("Usuarios").child((String) c.getId())
                        .child("pet").child(nodePET).child("tarefas")
                        .child(node).child("caminho").setValue(dbAtualizaTarefa.getRef().toString().split("\\.firebaseio\\.com/")[1]);
            }
            finish();
        }
        if(id == R.id.cancela) {
            finish();
        }
        if(id == R.id.data_text) {
            try {
                Log.d("DEV/TAREFASEDIT", "data: "+dataText.getText().toString());
                Log.d("DEV/TAREFASEDIT", "dia: "+dataText.getText().toString().split("/")[0]);
                Log.d("DEV/TAREFASEDIT", "mes: "+dataText.getText().toString().split("/")[1]);
                Log.d("DEV/TAREFASEDIT", "ano: "+dataText.getText().toString().split("/")[2]);
                dia = Integer.parseInt(dataText.getText().toString().split("/")[0]);
                mes = Integer.parseInt(dataText.getText().toString().split("/")[1]);
                ano = Integer.parseInt(dataText.getText().toString().split("/")[2]);
                Log.d("DEV/TAREFASEDIT", "diaInt: "+dia);
                Log.d("DEV/TAREFASEDIT", "mesInt: "+mes);
                Log.d("DEV/TAREFASEDIT", "anoInt: "+ano);
            } catch (Exception e) {
                long dataAtual = System.currentTimeMillis() - 1000;
                String dataString = getDate(dataAtual, "dd/MM/yyyy");
                dia = Integer.parseInt(dataString.split("/")[0]);
                mes = Integer.parseInt(dataString.split("/")[1]);
                ano = Integer.parseInt(dataString.split("/")[2]);
            }
            setupCalendar();
            datePickerDialog.show();
        }
    }

    private void getPetianos(final ChipsInput chipsPetianos) {
        String[] caminhos = tarefaPath.split("/");
        DatabaseReference timeProjeto;
        try {
            tarefaPath = tarefaPath.split("/equipes/")[0];
            timeProjeto = mDatabase.child(tarefaPath).child("time");
        } catch (Exception e) {
            timeProjeto = mDatabase.child(tarefaPath).child("time");
        }
//        DatabaseReference timeProjeto = mDatabase;
//        for(int i =0; i < 4; i++) {
//            timeProjeto = timeProjeto.child(caminhos[i]);
//        }
//        timeProjeto = timeProjeto.child("time");
        Log.d("TAREFASEDITACTIVITY", timeProjeto.toString());
        timeProjeto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Chip> mPetianosList = new ArrayList<>();
                for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                    if(listSnapshots.hasChildren()) {
                        for (DataSnapshot subListSnapshots : listSnapshots.getChildren()) {
                            if (!listSnapshots.getKey().equals("aguardando")) {
                                String nome = subListSnapshots.getValue(String.class);
                                Log.d("OI", nome);
                                mPetianosList.add(new Chip(subListSnapshots.getKey(), nome, ""));
                            }
                        }
                    }
                    else {
                        String nome = listSnapshots.getValue(String.class);
                        Log.d("OI", nome);
                        mPetianosList.add(new Chip(listSnapshots.getKey(), nome, ""));
                    }
                }
                chipsPetianos.setFilterableList(mPetianosList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });
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
}
