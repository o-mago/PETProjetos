package magosoftware.petprojetos;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexandre on 02/07/2017.
 */

public class NewUser extends BaseActivity implements
        View.OnClickListener {

//    private DatabaseReference mDatabase;
    private DatabaseReference dbUsuario;
    FirebaseAuth mAuth;
    EditText nome;
    EditText nick;
    EditText email;
    EditText senha;
    EditTextEnd nascimento;
    Spinner spinner_universidade;
    Spinner spinner_curso;
    ImageView mostra_senha;
    int tamanhoTexto = 0;
    int tamanhoTextoAntes = 0;
    boolean senhaVisivel = false;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuser);

        nome = findViewById(R.id.field_nome);
        email = findViewById(R.id.field_email);
        senha = findViewById(R.id.field_password);
        nick = findViewById(R.id.field_nick);
        nascimento = findViewById(R.id.field_nascimento);
        mostra_senha = findViewById(R.id.mostra_senha);
        findViewById(R.id.mostra_senha).setOnClickListener(this);

        nascimento.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable text) {
                tamanhoTexto = text.length();
                if(tamanhoTexto>tamanhoTextoAntes) {
                    if (text.length() == 2 || text.length() == 5) {
                        text.append('/');
                    }
                }
                else {
                    if (text.length() == 2 || text.length() == 5) {
                        text.delete(text.length()-1, text.length());
                    }
                }
                tamanhoTextoAntes = tamanhoTexto;
            }
        });

        mAuth = FirebaseAuth.getInstance();

        dbUsuario = mDatabase.child("Usuarios");

        findViewById(R.id.criar_conta).setOnClickListener(this);
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
                    Intent intent = new Intent(NewUser.this, NovaUniversidade.class);
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
                    Intent intent = new Intent(NewUser.this, NovoCurso.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


//        ArrayAdapter<CharSequence> adapter_curso = ArrayAdapter.createFromResource(this,
//                R.array.cursos_array, android.R.layout.simple_spinner_item);
//        adapter_curso.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner_curso.setAdapter(adapter_curso);


                // [START create_user_with_email]

//        showProgressDialog();

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.criar_conta) {
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), senha.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("USUARIO", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);

                                // Generate a reference to a new location and add some data using push()
                                //DatabaseReference pushedPostRef = dbUsuario.push();
                                //String postId = pushedPostRef.getKey();
                                String postId = user.getUid();
//                                DatabaseReference newChild = dbUsuario.
                                Map<String, Usuario> users = new HashMap<>();
                                users.put(postId, new Usuario(nome.getText().toString(),
                                        nick.getText().toString(),
                                        email.getText().toString(),
                                        spinner_universidade.getSelectedItem().toString(),
                                        spinner_curso.getSelectedItem().toString(),
                                        nascimento.getText().toString()
                                ));
                                dbUsuario.setValue(users);
                                dbUsuario.child(user.getUid()).orderByPriority();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("USUARIO", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(NewUser.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // [START_EXCLUDE]
                            hideProgressDialog();
                            // [END_EXCLUDE]
                        }
                    });
            //[END create_user_with_email]
        }
        if(i == R.id.mostra_senha) {
            if(senhaVisivel == false) {
                senha.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                senha.setSelection(senha.getText().length());
                mostra_senha.setImageResource(R.drawable.ic_no_visibility);
            }
            else {
                senha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                senha.setSelection(senha.getText().length());
                mostra_senha.setImageResource(R.drawable.ic_visibility);
            }
            senhaVisivel = !senhaVisivel;
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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(NewUser.this, android.R.layout.simple_spinner_item, list)
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

    private void updateUI(final FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            user.sendEmailVerification();
            Toast.makeText(this, "Um email de verificação foi enviado para "+user.getEmail(), Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, EmailPasswordActivity.class);
            //i.putExtra("auth",mAuth.getCurrentUser());
            startActivity(i);
            finish();
        }
    }
}
