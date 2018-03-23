package magosoftware.petprojetos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by root on 22/02/18.
 */

public class NovaUniversidade extends BaseActivity implements View.OnClickListener {

    EditText nomeUniversidade;
    EditText siglaUniversidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_universidade);

        nomeUniversidade = findViewById(R.id.field_universidade);
        siglaUniversidade = findViewById(R.id.field_sigla);
        findViewById(R.id.adicionar_universidade).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.adicionar_universidade) {
            String universidade = nomeUniversidade.getText().toString();
            universidade = universidade.toLowerCase();
            String[] arrayUniversidade = universidade.split(" ");
            universidade = "";
            for(int j = 0; j < arrayUniversidade.length; j++) {
                arrayUniversidade[j] = Character.toString(arrayUniversidade[j].charAt(0)).toUpperCase()+arrayUniversidade[j].substring(1);
                if(j < arrayUniversidade.length-1) {
                    universidade = universidade+arrayUniversidade[j]+" ";
                }
                else {
                    universidade = universidade+arrayUniversidade[j];
                }
            }
            String sigla = siglaUniversidade.getText().toString();
            sigla = sigla.toUpperCase();
            mDatabase.child("Base Universidades").child(sigla).setValue(sigla+"-"+universidade);
            Intent intent = new Intent(this, NewUser.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
