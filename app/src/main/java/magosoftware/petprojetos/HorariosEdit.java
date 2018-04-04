package magosoftware.petprojetos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HorariosEdit extends BaseActivity implements View.OnClickListener{

    private ImageView check;
    private ImageView cancelar;
    String[] dias;
    HashMap<String, List<Horario>> lstItensGrupo;
    ExpandableListAdapter adaptador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.horarios_activity);

        check = findViewById(R.id.certo);
        cancelar = findViewById(R.id.cancela);
        check.setOnClickListener(this);

        setExpandable();


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.certo) {
            HashMap<String,Object> horariosMap = new HashMap<>();
            for(int i=0; i<adaptador.getGroupCount(); i++) {
                String horariosDoDia = "";
                for(int j=0; j<adaptador.getChildrenCount(i); j++) {
                    if(adaptador.getChild(i, j).isDisponivel()) {
                        horariosDoDia = horariosDoDia + adaptador.getChild(i, j).getHora() + ";";
                    }
                }
                Log.d("DEV/HORARIOSEDIT", horariosDoDia);
                horariosMap.put(adaptador.getGroup(i).toString(),horariosDoDia);
            }
            Intent intent = new Intent();
            intent.putExtra("map", horariosMap);
            setResult(RESULT_OK, intent);
            finish();
        }
        if(id == R.id.cancela) {

        }
    }

    private void setExpandable() {
        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.lista_dias);

        // cria os grupos
        String[] dias = getResources().getStringArray(R.array.dias);
        String[] horarios = getResources().getStringArray(R.array.horarios);
        lstItensGrupo = new HashMap<>();

        for(int j = 0; j<dias.length; j++) {
            List<Horario> listaHorario = new ArrayList<>();
            for(int i = 0; i<horarios.length; i++) {
                listaHorario.add(new Horario(horarios[i], false));
            }
            lstItensGrupo.put(dias[j], listaHorario);
        }

        // cria um adaptador (BaseExpandableListAdapter) com os dados acima
        adaptador = new ExpandableListAdapter(this, new ArrayList<>(Arrays.asList(dias)), lstItensGrupo);
        // define o apadtador do ExpandableListView
        expandableListView.setAdapter(adaptador);
    }
}
