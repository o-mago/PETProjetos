package magosoftware.petprojetos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReunioesEditActivity extends BaseActivity implements View.OnClickListener{

    private Button certo;
    private Button cancela;
    private EditText titulo;
    private TextView dataText;
    private EditText anotacoes;
    private DatePickerDialog calendario;
    private LinearLayout linearLayout;
    private String dataSelecionada;
    private String projetoPath;
    private DatabaseReference dbProjeto;
    private String tituloReuniao = "NADA9232CMXC3";
    private List<String> petianos;
    RecyclerView mRecyclerView;
    private LineAdapterPresenca mAdapter;
    private List<Presenca> mModels;
    private boolean primeiro = true;
    private DatePickerDialog datePickerDialog;

    @Override
    public void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);
        setContentView(R.layout.reuniao_edit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        certo = findViewById(R.id.certo);
        cancela = findViewById(R.id.cancela);
        dataText = findViewById(R.id.data_text);
        certo.setOnClickListener(this);
        cancela.setOnClickListener(this);
        dataText.setOnClickListener(this);
        findViewById(R.id.presenca).setOnClickListener(this);
        titulo = findViewById(R.id.titulo);
        anotacoes = findViewById(R.id.anotacoes);
        Intent intent = getIntent();
        projetoPath = intent.getStringExtra("projeto_path");
        dbProjeto = mDatabase.child(projetoPath);
//        getPetianos(chipsPetianos);
        try {
            tituloReuniao = intent.getStringExtra("titulo_reuniao");
            getInfoTarefa();
        } catch (NullPointerException e) {

        }
        setupCalendar();
    }

    private void getInfoTarefa() {
        dbProjeto.child("reunioes").child("historico").child(tituloReuniao).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                titulo.setText(dataSnapshot.child("titulo").getValue(String.class));
                anotacoes.setText(dataSnapshot.child("anotacoes").getValue(String.class));
                dataText.setText(dataSnapshot.child("data").getValue(String.class));
//                final List<Chip> mPetianosList = new ArrayList<>();
//                for (DataSnapshot listSnapshot : dataSnapshot.child("presentes").getChildren()) {
////                    chipsPetianos.addChip(listSnapshot.getValue(String.class), "");
//                    petianos.add(listSnapshot.getKey()+"/"+listSnapshot.getValue(String.class));
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupCalendar() {
//        linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.calendario_alert, null, false);
//        calendario = (DatePickerDialog) linearLayout.getChildAt(0);
//        calendario.setMinDate(System.currentTimeMillis() - 1000);
//        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                dataSelecionada = dayOfMonth + "/" + month + "/" + year;
//            }
//        });
//        new AlertDialog.Builder(this)
//                .setTitle("Data")
//                .setMessage("Selecione a Data")
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
                dataSelecionada = dayOfMonth + "/" + (month+1) + "/" + year;
                dataText.setText(dataSelecionada);
            }
        }, 2018, 3, 1);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.certo) {
            String tarefa = titulo.getText().toString();
            dbProjeto.child("reunioes").child("historico").child(tituloReuniao).child("titulo").setValue(titulo.getText().toString());
            dbProjeto.child("reunioes").child("historico").child(tituloReuniao).child("data").setValue(dataText.getText().toString());
            dbProjeto.child("reunioes").child("historico").child(tituloReuniao).child("anotacoes").setValue(anotacoes.getText().toString());
            try {
                for (Presenca presenca : mModels) {
                    dbProjeto.child("reunioes").child("historico").child(tituloReuniao).child("lista_presenca").child(presenca.getNome()).setValue(presenca.getSituacao());
                }
            }
            catch (NullPointerException e) {

            }
//            List<Chip> contactsSelected = (List<Chip>) chipsPetianos.getSelectedChipList();
//            for (Chip c : contactsSelected) {
//                Log.d("DEV/TAREFASEDIT", "Entrou");
//                dbEquipe.child("tarefas").child("fazer").child(tarefa).child("time").child(c.getLabel()).setValue(c.getLabel());
//            }
            finish();
        }
        if (id == R.id.cancela) {
            finish();
        }
        if (id == R.id.data_text) {
            datePickerDialog.show();
        }
        if (id == R.id.presenca) {
            setupPresenca();
            setupRecycler();
        }
    }

    private void setupPresenca() {
        linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.lista_presenca, null, false);
        new AlertDialog.Builder(this)
                .setTitle("Lista de Presença")
                .setView(linearLayout)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }
        ).show();
    }

    private void setupRecycler() {

        // Configurando o gerenciador de layout para ser uma lista.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView = linearLayout.findViewById(R.id.lista_presenca);
        mRecyclerView.setLayoutManager(layoutManager);
        if(!primeiro) {
            mModels = mAdapter.getPresenca();
        }
        mAdapter = new LineAdapterPresenca();
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        if(primeiro) {
            if (tituloReuniao.equals("NADA9232CMXC3")) {
                mModels = new ArrayList<>();
                dbProjeto.child("time").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot listSnapshoot : dataSnapshot.getChildren()) {
                            mModels.add(new Presenca(listSnapshoot.getValue(String.class), "ausente"));
                        }
                        mAdapter.replaceAll(mModels);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                mModels = new ArrayList<>();
                dbProjeto.child("reunioes").child("historico").child(tituloReuniao).child("lista_presenca").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot listSnapshoot : dataSnapshot.getChildren()) {
                            mModels.add(new Presenca(listSnapshoot.getKey(), listSnapshoot.getValue(String.class)));
                        }
                        mAdapter.replaceAll(mModels);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            primeiro = false;
        }
        else {
            Log.d("DEV/REUNIOESEDIT", Integer.toString(mModels.size()));
            mAdapter.replaceAll(mModels);
        }
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