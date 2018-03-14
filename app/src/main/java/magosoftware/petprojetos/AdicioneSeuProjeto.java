package magosoftware.petprojetos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 13/03/18.
 */

public class AdicioneSeuProjeto extends BaseActivity implements View.OnClickListener {

    FirebaseUser user;
    FirebaseAuth mAuth;
    private EditText nomeProjeto;
    private String nomePET;
    private String nomeUsuario;
    private DatabaseReference dbProjetos;
    public SharedPreferences sharedPref;

    @Override
    public void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);
        setContentView(R.layout.adicione_seu_projeto);

        sharedPref = this.getSharedPreferences("todoApp", 0);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Intent intent = getIntent();
        nomePET = intent.getStringExtra("nome");
        nomeUsuario = intent.getStringExtra("nomeUsuario");
        dbProjetos = mDatabase.child("PETs").child(nomePET).child("projetos");
        nomeProjeto = findViewById(R.id.field_nome);
        findViewById(R.id.adicionar_projeto).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.adicionar_projeto) {
            String nomeSemEspaco = nomeProjeto.getText().toString();
            Map<String, String> projeto = new HashMap<>();
            projeto.put("nome", nomeSemEspaco);
            Map<String, String> situacao = new HashMap<>();
            situacao.put(user.getUid(), sharedPref.getString("nome_usuario", "Cumpadi"));
            dbProjetos.child(nomeSemEspaco).setValue(projeto);
            dbProjetos.child(nomeSemEspaco).child("time").setValue(situacao);
            dbProjetos.child(nomeSemEspaco).orderByPriority();
            try {
                nomeSemEspaco = nomeSemEspaco.replace(" ", "_");
            }
            catch (NullPointerException e) {

            }
            String caminho = "imagensProjetos/"+nomeSemEspaco+".jpg";
            Intent intent = new Intent(this, AdicionaImagem.class);
            intent.putExtra("caminho", caminho);
            intent.putExtra("tipo", "novo projeto");
            startActivity(intent);
            finish();
        }
    }
}
