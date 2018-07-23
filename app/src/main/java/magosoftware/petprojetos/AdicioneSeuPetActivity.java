package magosoftware.petprojetos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.model.Chip;
import com.pchmn.materialchips.model.ChipInterface;
//import com.pchmn.materialchips.ChipsInput;

import java.io.Serializable;
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
    //    private EditText nPetianos;
    private EditText nome;
    private EditText cidade;
    private EditText email;
    private EditText site;
    private EditText ano_surgimento;
    ChipsInput chipsCursos;
    private Spinner spinner_universidade;
    private Spinner spinner_estado;
    private Spinner spinner_condicao;
    private String oldNode = "";
    private String oldNome;
    private String oldCidade;
    private String oldEmail;
    private String oldSite;
    private String oldAno_surgimento;
    private List<String> oldCursos;
    private List<String> deleteChip;
    private String oldUniversidade;
    private String oldEstado;
    private String tipo;
    private int indexUniversidade;
    private int indexCurso;
    private String nomeUsuario;
    private FirebaseUser user;

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
//        nPetianos = findViewById(R.id.numero_petianos);
        ano_surgimento = findViewById(R.id.field_ano);
        site = findViewById(R.id.field_site);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        dbPets = mDatabase.child("PETs");
        findViewById(R.id.adicionar_pet).setOnClickListener(this);
        final Spinner spinner_universidade = (Spinner) findViewById(R.id.universidade_spinner);
        Spinner spinner_estado = findViewById(R.id.estado_spinner);
        this.spinner_universidade = spinner_universidade;
        this.spinner_estado = spinner_estado;
        Spinner spinner_condicao = findViewById(R.id.condicao_spinner);
        this.spinner_condicao = spinner_condicao;

        Intent intent = getIntent();
        try {
            oldNode = intent.getStringExtra("node");
            if(!oldNode.equals("")) {
                spinner_condicao.setVisibility(View.GONE);
                adicionar_pet.setText("OK");
                getInfo();
            }
        }
        catch (Exception e) {

        }
        oldCursos = new ArrayList<>();

        final ChipsInput chipsCursos = (ChipsInput) findViewById(R.id.chips_cursos);
        this.chipsCursos = chipsCursos;
        getCursos(chipsCursos);
        chipsCursos.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chipInterface, int i) {
                oldCursos.add(chipInterface.getLabel());
            }

            @Override
            public void onChipRemoved(ChipInterface chipInterface, int i) {
                oldCursos.remove(chipInterface.getLabel());
            }

            @Override
            public void onTextChanged(CharSequence charSequence) {

            }
        });
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

        String[] lista_condicao = getResources().getStringArray(R.array.condicao);
        ArrayAdapter<String> adapter_condicao = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista_condicao) {
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
        adapter_condicao.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_condicao.setAdapter(adapter_condicao);

        spinner_universidade.setSelection(0);
        spinner_estado.setSelection(0);
        spinner_condicao.setSelection(0);

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

        mDatabase.child("Usuarios").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nomeUsuario = dataSnapshot.child("nome").getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getInfo() {
        dbPets.child(oldNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                oldNome = dataSnapshot.child("nome").getValue(String.class);
                nome.setText(oldNome);
                oldCidade = dataSnapshot.child("cidade").getValue(String.class);
                cidade.setText(oldCidade);
                oldEmail = dataSnapshot.child("email").getValue(String.class);
                email.setText(oldEmail);
                oldSite = dataSnapshot.child("site").getValue(String.class);
                site.setText(oldSite);
                oldAno_surgimento = dataSnapshot.child("ano").getValue(String.class);
                ano_surgimento.setText(oldAno_surgimento);
                for(DataSnapshot listSnapshot : dataSnapshot.child("cursos").getChildren()) {
                    oldCursos.add(listSnapshot.getValue(String.class));
                    chipsCursos.addChip(listSnapshot.getValue(String.class), "");
                }
                oldEstado = dataSnapshot.child("estado").getValue(String.class);
                String[] lista = getResources().getStringArray(R.array.estados);
                int j=0;
                for(String item : lista) {
                    if(item.equals(oldEstado)) {
                        spinner_estado.setSelection(j);
                        break;
                    }
                    j++;
                }
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.adicionar_pet) {
            DatabaseReference dbNovoPet;
            if(oldNode.equals("")) {
                dbNovoPet = dbPets.push();
            }
            else {
                dbNovoPet = dbPets.child(oldNode);
            }
            String nomePet = nome.getText().toString();
//            Map<String, String> pets = new HashMap<>();
//            pets.put("nome", nome.getText().toString());
////            pets.put("nPetianos", nPetianos.getText().toString());
//            pets.put("email", email.getText().toString());
//            pets.put("universidade", spinner_universidade.getSelectedItem().toString());
//            pets.put("ano", ano_surgimento.getText().toString());
//            pets.put("site", site.getText().toString());
//            pets.put("cidade", arrumaTexto(cidade.getText().toString()));
//            pets.put("estado", spinner_estado.getSelectedItem().toString());
//            pets.put("criador", user.getUid());
//            dbNovoPet.setValue(pets);
            dbNovoPet.child("nome").setValue(nome.getText().toString());
            dbNovoPet.child("email").setValue(email.getText().toString());
            dbNovoPet.child("universidade").setValue(spinner_universidade.getSelectedItem().toString());
            dbNovoPet.child("ano").setValue(ano_surgimento.getText().toString());
            dbNovoPet.child("site").setValue(site.getText().toString());
            dbNovoPet.child("cidade").setValue(arrumaTexto(cidade.getText().toString()));
            dbNovoPet.child("estado").setValue(spinner_estado.getSelectedItem().toString());
            dbNovoPet.child("criador").setValue(user.getUid());

            if(oldNode.equals("")) {
                String condicao = "";
                if (spinner_condicao.getSelectedItemPosition() == 1) {
                    condicao = "bolsistas";
                } else if (spinner_condicao.getSelectedItemPosition() == 2) {
                    condicao = "oficiais";
                } else if (spinner_condicao.getSelectedItemPosition() == 3) {
                    condicao = "voluntarios";
                }
                dbNovoPet.child("time").child(condicao).child(user.getUid()).setValue(nomeUsuario);
                mDatabase.child("Usuarios").child(user.getUid())
                        .child("pet").child(dbNovoPet.getKey())
                        .child("situacao")
                        .setValue(condicao);
            }
            mDatabase.child("Usuarios").child(user.getUid())
                    .child("pet").child(dbNovoPet.getKey())
                    .child("nome")
                    .setValue(nomePet);
            dbNovoPet.orderByPriority();

//            List<Chip> contactsSelected = (List<Chip>) chipsCursos.getSelectedChipList();
//
//            for(Chip c : contactsSelected) {
//                dbNovoPet.child("cursos").child(c.getLabel()).setValue(c.getLabel());
//                dbNovoPet.child("cursos").orderByPriority();
//            }
            for(String contato : oldCursos) {
                dbNovoPet.child("cursos").removeValue();
                dbNovoPet.child("cursos").child(contato).setValue(contato);
                dbNovoPet.child("cursos").orderByPriority();
            }
//            String nomeSemEspaco = "";
//            try {
//                nomeSemEspaco = nomePet.replace(" ", "_");
//            }
//            catch (NullPointerException e) {
//
//            }
            if(oldNode.equals("")) {
                String caminho = "imagensPET/" + dbNovoPet.getKey() + ".jpg";
                Intent intent = new Intent(this, AdicionaImagem.class);
                intent.putExtra("caminho", caminho);
                intent.putExtra("tipo", "novo pet");
                startActivity(intent);
            }
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
