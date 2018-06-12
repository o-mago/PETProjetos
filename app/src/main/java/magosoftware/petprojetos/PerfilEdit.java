package magosoftware.petprojetos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfilEdit extends BaseActivity implements View.OnClickListener {

    private Button aceitar;
    private Button cancelar;
    private EditText nick;
    private String oldNick;
    private EditTextEnd nascimento;
    private String oldNascimento;
    private Spinner spinner_universidade;
    private String oldUniversidade;
    private Spinner spinner_curso;
    private String oldCurso;
    private RelativeLayout horarios;
    HashMap<String, Object> map;
    private DatabaseReference dbUsuario;
    FirebaseUser user;
    FirebaseAuth mAuth;
    private int indexUniversidade;
    private int indexCurso;
    private HashMap<String, List<Horario>> oldLstItensGrupo;

    @Override
    public void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);
        setContentView(R.layout.perfil_edit);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        dbUsuario = mDatabase.child("Usuarios").child(user.getUid());

        aceitar = findViewById(R.id.aceitar);
        cancelar = findViewById(R.id.cancelar);
        horarios = findViewById(R.id.horarios);
        aceitar.setOnClickListener(this);
        cancelar.setOnClickListener(this);
        horarios.setOnClickListener(this);
        nick = findViewById(R.id.field_nick);
        nascimento = findViewById(R.id.field_nascimento);
        final Spinner spinner_universidade = (Spinner) findViewById(R.id.universidade_spinner);
        final Spinner spinner_curso = (Spinner) findViewById(R.id.curso_spinner);
        this.spinner_universidade = spinner_universidade;
        this.spinner_curso = spinner_curso;

        spinner_universidade.setSelection(0);
        spinner_curso.setSelection(0);

        feedSpinner("Base Universidades", spinner_universidade);
        feedSpinner("BaseCursos", spinner_curso);

        spinner_universidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 1) {
                    Intent intent = new Intent(PerfilEdit.this, NovaUniversidade.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        spinner_curso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 1) {
                    Intent intent = new Intent(PerfilEdit.this, NovoCurso.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        getInfoFirebase();
    }

    public void getInfoFirebase() {
        dbUsuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                oldNick = dataSnapshot.child("nick").getValue(String.class);
                nick.setText(oldNick);
                oldNascimento = dataSnapshot.child("nascimento").getValue(String.class);
                nascimento.setText(oldNascimento);
                oldUniversidade = dataSnapshot.child("universidade").getValue(String.class);
                mDatabase.child("Base Universidades").addListenerForSingleValueEvent(new ValueEventListenerSend(oldUniversidade) {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i=0;
                        for(DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                            if(listSnapshot.getValue(String.class).equals(variavel)) {
                                indexUniversidade = i;
                                spinner_universidade.setSelection(indexUniversidade);
                                break;
                            }
                            i++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                oldCurso = dataSnapshot.child("curso").getValue(String.class);
                mDatabase.child("BaseCursos").addListenerForSingleValueEvent(new ValueEventListenerSend(oldCurso) {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int i = 0;
                        for(DataSnapshot listSnapshot : dataSnapshot.getChildren()) {
                            if(listSnapshot.getValue(String.class).equals(variavel)) {
                                indexCurso = i;
                                spinner_curso.setSelection(indexCurso);
                                break;
                            }
                            i++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.aceitar) {
            try {
                dbUsuario.child("horarios").updateChildren(map);
                if(!nick.getText().toString().equals("") && nick.getText().toString().equals(oldNick)) {
                    dbUsuario.child("nick").setValue(nick.getText().toString());
                }
                if(!nascimento.getText().toString().equals("") && nascimento.getText().toString().equals(oldNascimento)) {
                    dbUsuario.child("nascimento").setValue(nascimento.getText().toString());
                }
                if(!spinner_universidade.getSelectedItem().toString().equals("") && spinner_universidade.getSelectedItem().toString().equals(oldUniversidade)) {
                    dbUsuario.child("universidade").setValue(spinner_universidade.getSelectedItem().toString());
                }
                if(!spinner_curso.getSelectedItem().toString().equals("") && spinner_curso.getSelectedItem().toString().equals(oldCurso)) {
                    dbUsuario.child("curso").setValue(spinner_curso.getSelectedItem().toString());
                }
            } catch (NullPointerException e) {

            }
            finish();
        }
        if (id == R.id.cancelar) {
            finish();
        }
        if (id == R.id.horarios) {
            Intent intent = new Intent(this, HorariosEdit.class);
            if(oldLstItensGrupo!=null) {
                intent.putExtra("oldLstItensGrupo", oldLstItensGrupo);
            }
            startActivityForResult(intent, 1);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                map = (HashMap<String, Object>) data.getSerializableExtra("map");
                oldLstItensGrupo = (HashMap<String, List<Horario>>) data.getSerializableExtra("lstItensGrupo");
                Log.d("DEV/PERFILEDIT", map.toString());
            }
        }
    }

    protected void feedSpinner(String db, final Spinner spinner) {
        mDatabase.child(db).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> list = new ArrayList<String>();
                for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                    String nome = listSnapshots.getValue(String.class);
                    Log.d("UNI", nome);
                    list.add(nome);
                }

                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PerfilEdit.this, android.R.layout.simple_spinner_item, list)
                {
                    @Override
                    public boolean isEnabled(int position){
                        if(position == 0)
                        {
                            // Disable the first item from Spinner
                            // First item will be use for hint
                            return false;
                        }
                        else
                        {
                            return true;
                        }
                    }
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(position == 0){
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                            tv.setTextSize(20);
                        }
                        else {
                            tv.setTextColor(Color.BLACK);
                            tv.setTextSize(16);
                        }
                        return view;
                    }
                };

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spinner.setAdapter(dataAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });

    }
}