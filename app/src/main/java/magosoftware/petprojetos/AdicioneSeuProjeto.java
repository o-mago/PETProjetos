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
    private String nodePET;

    @Override
    public void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);
        setContentView(R.layout.adicione_seu_projeto);

        sharedPref = this.getSharedPreferences("todoApp", 0);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Intent intent = getIntent();
        nomePET = intent.getStringExtra("nome");
        nodePET = intent.getStringExtra("node");
        nomeUsuario = intent.getStringExtra("nomeUsuario");
        dbProjetos = mDatabase.child("PETs").child(nodePET).child("projetos");
        nomeProjeto = findViewById(R.id.field_nome);
        nomeUsuario = sharedPref.getString("nome_usuario", "Cumpadi");
        findViewById(R.id.adicionar_projeto).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.adicionar_projeto) {
//            String nomeSemEspaco = nomeProjeto.getText().toString();
//            Map<String, String> projeto = new HashMap<>();
//            projeto.put("nome", nomeSemEspaco);
            Map<String, String> situacao = new HashMap<>();
            situacao.put(user.getUid(), nomeUsuario);
            DatabaseReference dbNovoProjeto = dbProjetos.push();
//            dbNovoProjeto.setValue(projeto);
            dbNovoProjeto.child("nome").setValue(nomeProjeto.getText().toString());
            dbNovoProjeto.child("time").setValue(situacao);
            dbNovoProjeto.orderByPriority();
            dbNovoProjeto.child("coordenador").child(user.getUid()).setValue(nomeUsuario);
//            try {
//                nomeSemEspaco = nomeSemEspaco.replace(" ", "_");
//            }
//            catch (NullPointerException e) {
//
//            }
            String caminho = "imagensProjetos/"+dbNovoProjeto.getKey()+".jpg";
            Intent intent = new Intent(this, AdicionaImagem.class);
            intent.putExtra("caminho", caminho);
            intent.putExtra("tipo", "novo projeto");
            startActivity(intent);
            finish();
        }
    }
}
