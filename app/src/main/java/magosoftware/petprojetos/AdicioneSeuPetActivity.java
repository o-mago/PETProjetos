package magosoftware.petprojetos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pchmn.materialchips.ChipsInput;
//import com.pchmn.materialchips.ChipsInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 27/02/18.
 */

public class AdicioneSeuPetActivity extends BaseActivity implements View.OnClickListener {

    private Button adicionar_pet;
    private DatabaseReference dbPets;
    FirebaseAuth mAuth;
    private EditText nome;
    private EditText nick;
    private EditText email;
    private EditText senha;
    private EditText ano_surgimento;
    ChipsInput chips_cursos;
    private Spinner spinner_universidade;
    private Spinner spinner_curso;

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);

        setContentView(R.layout.activity_adicione_seu_pet);

        adicionar_pet = findViewById(R.id.adicionar_pet);
        adicionar_pet.setOnClickListener(this);

        nome = findViewById(R.id.field_nome);
        email = findViewById(R.id.field_email);
        senha = findViewById(R.id.field_password);
        nick = findViewById(R.id.field_nick);
        ano_surgimento = findViewById(R.id.field_ano);

        ChipsInput chipsInput = (ChipsInput) findViewById(R.id.chips_input);

        mAuth = FirebaseAuth.getInstance();

        dbPets = mDatabase.child("PETs");

        findViewById(R.id.criar_conta).setOnClickListener(this);
        final Spinner spinner_universidade = (Spinner) findViewById(R.id.universidade_spinner);
        final Spinner spinner_curso = (Spinner) findViewById(R.id.curso_spinner);
        Spinner spinner_estado = findViewById(R.id.estado_spinner);
        this.spinner_universidade = spinner_universidade;
        this.spinner_curso = spinner_curso;

        ArrayAdapter<CharSequence> adapter_estado = new ArrayAdapter<CharSequence>(this,
        R.array.cursos_array, android.R.layout.simple_spinner_item) {
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
        adapter_estado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_estado.setAdapter(adapter_estado);

        spinner_universidade.setSelection(0);
        spinner_curso.setSelection(0);

        feedSpinner("Base Universidades", spinner_universidade);
        feedSpinner("BaseCursos", spinner_curso);

        spinner_universidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position == 1) {
                    Intent intent = new Intent(AdicioneSeuPetActivity.this, NovaUniversidade.class);
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
                    Intent intent = new Intent(AdicioneSeuPetActivity.this, NovoCurso.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.adicionar_pet) {
            String postId = nome.getText().toString();
            Map<String, Usuario> pets = new HashMap<>();
            pets.put(postId, new Usuario(nome.getText().toString(),
                    nick.getText().toString(),
                    email.getText().toString(),
                    spinner_universidade.getSelectedItem().toString(),
                    spinner_curso.getSelectedItem().toString(),
                    ano_surgimento.getText().toString()
            ));
            dbPets.setValue(pets);
            dbPets.child(pets.getUid()).orderByPriority();
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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AdicioneSeuPetActivity.this, android.R.layout.simple_spinner_item, list)
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
