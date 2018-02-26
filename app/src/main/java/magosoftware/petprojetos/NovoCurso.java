package magosoftware.petprojetos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by root on 22/02/18.
 */

public class NovoCurso extends BaseActivity implements View.OnClickListener {

    EditText nomeCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_curso);

        nomeCurso = findViewById(R.id.field_curso);
        findViewById(R.id.adicionar_curso).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if(i == R.id.adicionar_curso) {
            String curso = nomeCurso.getText().toString();
            curso = curso.toLowerCase();
            String[] arrayCurso = curso.split(" ");
            curso = "";
            for(int j = 0; j < arrayCurso.length; j++) {
                arrayCurso[j] = Character.toString(arrayCurso[j].charAt(0)).toUpperCase()+arrayCurso[j].substring(1);
                if(j < arrayCurso.length-1) {
                    curso = curso+arrayCurso[j]+" ";
                }
                else {
                    curso = curso+arrayCurso[j];
                }
            }
            mDatabase.child("BaseCursos").child(curso).setValue(curso);
            Intent intent = new Intent(this, NewUser.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
