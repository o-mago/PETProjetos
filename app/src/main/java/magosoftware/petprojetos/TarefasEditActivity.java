package magosoftware.petprojetos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by root on 16/03/18.
 */

public class TarefasEditActivity extends BaseActivity implements View.OnClickListener {

    private Button certo;
    private Button cancela;
    private EditText titulo;
    private ChipsInput chipsPetianos;
    private TextView dataText;
    private EditText descricao;
    private CalendarView calendario;
    private LinearLayout linearLayout;
    private String dataSelecionada;
    private String caminhoEquipe;
    private DatabaseReference dbEquipe;

    @Override
    public void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);
        setContentView(R.layout.tarefa_edit);

        certo = findViewById(R.id.certo);
        cancela = findViewById(R.id.cancela);
        certo.setOnClickListener(this);
        cancela.setOnClickListener(this);
        titulo = findViewById(R.id.titulo);
        chipsPetianos = findViewById(R.id.chips_responsaveis);
        dataText = findViewById(R.id.data_text);
        descricao = findViewById(R.id.descricao);

        Intent intent = getIntent();
        caminhoEquipe = intent.getStringExtra("equipe_path");
        dbEquipe = mDatabase.child(caminhoEquipe);
        getPetianos(chipsPetianos);
    }

    private void setupCalendar() {
        linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.calendario_alert, null, false);
        calendario = (CalendarView) linearLayout.getChildAt(0);
        calendario.setMinDate(System.currentTimeMillis()-1000);
        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                dataSelecionada = dayOfMonth+"/"+month+"/"+year;
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("Prazo")
                .setMessage("Selecione o Prazo")
                .setView(linearLayout)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dataText.setText(dataSelecionada);
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        ).show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.certo) {
            String tarefa = titulo.getText().toString();
            dbEquipe.child(tarefa).child("titulo").setValue(titulo.getText().toString());
            dbEquipe.child(tarefa).child("prazo").setValue(dataSelecionada);
            dbEquipe.child(tarefa).child("descricao").setValue(descricao.getText().toString());
            List<Chip> contactsSelected = (List<Chip>) chipsPetianos.getSelectedChipList();
            for(Chip c : contactsSelected) {
                mDatabase.child(caminhoEquipe).child(tarefa).child("time").child(c.getLabel()).setValue(c.getLabel());
            }
            finish();
        }
        if(id == R.id.cancela) {
            finish();
        }
        if(id == R.id.data_text) {
            setupCalendar();
        }
    }

    private void getPetianos(final ChipsInput chipsPetianos) {
        String[] caminhos = caminhoEquipe.split("/");
        DatabaseReference timeProjeto = mDatabase;
        for(int i =0; i < 6; i++) {
            timeProjeto = timeProjeto.child(caminhos[i]);
        }
        timeProjeto.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Chip> mPetianosList = new ArrayList<>();
                for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                    String nome = listSnapshots.getValue(String.class);
                    Log.d("OI", nome);
                    mPetianosList.add(new Chip(nome, ""));
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
