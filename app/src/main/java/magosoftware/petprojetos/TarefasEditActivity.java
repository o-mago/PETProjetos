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

    private Button certo;
    private Button cancela;
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
    private String nomePET;
    private SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);
        setContentView(R.layout.tarefa_edit);

        sharedPref = this.getSharedPreferences("todoApp", 0);
        nomePET = sharedPref.getString( "nome_meu_pet", "nada");
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
        tarefaPath = intent.getStringExtra("tarefa_path");
        situacaoTarefa = intent.getStringExtra("situacao_tarefa");
        dbTarefa = mDatabase.child(tarefaPath);
        petianoRetirado = new ArrayList<>();
        petianoSelecionado = new ArrayList<>();
        getPetianos(chipsPetianos);
        setupCalendar();
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
                descricao.setText(dataSnapshot.child("titulo").getValue(String.class));
                dataText.setText(dataSnapshot.child("prazo").getValue(String.class));

                for(DataSnapshot listSnapshot : dataSnapshot.child("time").getChildren()) {
                    Log.d("DEV/TAREFASEDIT", "getInfo: "+listSnapshot.getValue(String.class));
                    Chip chip = new Chip(listSnapshot.getKey(), listSnapshot.getValue(String.class), "");
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
        }, 2018, 3, 1);
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
                        .child("pet").child(nomePET).child("tarefas")
                        .child(node).removeValue();
            }

            for (Chip c : contactsSelected) {
                Log.d("DEV/TAREFASEDIT", (String) c.getId());
                dbAtualizaTarefa.child("time").child((String) c.getId()).setValue(c.getLabel());
                mDatabase.child("Usuarios").child((String) c.getId())
                        .child("pet").child(nomePET).child("tarefas")
                        .child(node).setValue(tarefaPath + "/" + node);
            }
            finish();
        }
        if(id == R.id.cancela) {
            finish();
        }
        if(id == R.id.data_text) {
            datePickerDialog.show();
        }
    }

    private void getPetianos(final ChipsInput chipsPetianos) {
        String[] caminhos = tarefaPath.split("/");
        DatabaseReference timeProjeto = mDatabase.child(tarefaPath).child("time");
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
}
