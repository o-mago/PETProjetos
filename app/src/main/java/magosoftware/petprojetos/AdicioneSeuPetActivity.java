package magosoftware.petprojetos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.pchmn.materialchips.model.Chip;
//import com.pchmn.materialchips.ChipsInput;

import java.util.AbstractList;
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
    private EditText cidade;
    private EditText nPetianos;
    private EditText email;
    private EditText site;
    private EditText ano_surgimento;
    ChipsInput chipsCursos;
    private Spinner spinner_universidade;
    private Spinner spinner_estado;

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_adicione_seu_pet);

        adicionar_pet = findViewById(R.id.adicionar_pet);
        adicionar_pet.setOnClickListener(this);

        nome = findViewById(R.id.field_nome);
        email = findViewById(R.id.field_email);
        cidade = findViewById(R.id.field_cidade);
        nPetianos = findViewById(R.id.numero_petianos);
        ano_surgimento = findViewById(R.id.field_ano);
        site = findViewById(R.id.field_site);

        mAuth = FirebaseAuth.getInstance();

        dbPets = mDatabase.child("PETs");

        findViewById(R.id.adicionar_pet).setOnClickListener(this);
        final Spinner spinner_universidade = (Spinner) findViewById(R.id.universidade_spinner);
        Spinner spinner_estado = findViewById(R.id.estado_spinner);
        this.spinner_universidade = spinner_universidade;
        this.spinner_estado = spinner_estado;

        final ChipsInput chipsCursos = (ChipsInput) findViewById(R.id.chips_cursos);
        this.chipsCursos = chipsCursos;
        getCursos(chipsCursos);
        String[] lista = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapter_estado = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista) {
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
        spinner_estado.setSelection(0);

        feedSpinner("Base Universidades", spinner_universidade);

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

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.adicionar_pet) {
            String postId = nome.getText().toString();
            Map<String, String> pets = new HashMap<>();
            pets.put("nome", nome.getText().toString());
            pets.put("nPetianos", nPetianos.getText().toString());
            pets.put("email", email.getText().toString());
            pets.put("universidade", spinner_universidade.getSelectedItem().toString());
            pets.put("ano", ano_surgimento.getText().toString());
            pets.put("site", site.getText().toString());
            pets.put("cidade", arrumaTexto(cidade.getText().toString()));
            pets.put("estado", spinner_estado.getSelectedItem().toString());
            dbPets.child(postId).setValue(pets);
            dbPets.child(postId).orderByPriority();

            List<Chip> contactsSelected = (List<Chip>) chipsCursos.getSelectedChipList();
            for(Chip c : contactsSelected) {
                dbPets.child(postId).child("cursos").child(c.getLabel()).setValue(c.getLabel());
                dbPets.child(postId).child("cursos").orderByPriority();
            }
            Intent intent = new Intent(this, AdicioneSeuPet2Activity.class);
            intent.putExtra("nome", nome.getText().toString());
            startActivity(intent);
            finish();
        }
//        if(i == R.id.chips_cursos) {
//            String listString = "";
//            for(Chip chip: (List<Chip>)  chipsCursos.getSelectedChipList()) {
//                listString += chip.getLabel() + " (" + (chip.getInfo() != null ? chip.getInfo(): "") + ")" + ", ";
//            }
//
//            mChipListText.setText(listString);
//        }
    }

    private String arrumaTexto(String texto) {
        texto = texto.toLowerCase();
        String[] arrayUniversidade = texto.split(" ");
        texto = "";
        for(int j = 0; j < arrayUniversidade.length; j++) {
            arrayUniversidade[j] = Character.toString(arrayUniversidade[j].charAt(0)).toUpperCase()+arrayUniversidade[j].substring(1);
            if(j < arrayUniversidade.length-1) {
                texto = texto+arrayUniversidade[j]+" ";
            }
            else {
                texto = texto+arrayUniversidade[j];
            }
        }
        return texto;
    }

    private void getCursos(final ChipsInput chipsCursos) {
        mDatabase.child("BaseCursos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<Chip> mCursoList = new ArrayList<>();
                for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                    String nome = listSnapshots.getValue(String.class);
                    Log.d("OI", nome);
                    mCursoList.add(new Chip(nome, ""));
                }
                chipsCursos.setFilterableList(mCursoList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });
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
