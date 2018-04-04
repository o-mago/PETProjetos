package magosoftware.petprojetos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    private ImageButton horarios;
    HashMap<String,Object> map;
    private DatabaseReference dbUsuario;
    FirebaseUser user;
    FirebaseAuth mAuth;

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

        try {
            Intent intent = getIntent();

        } catch (NullPointerException e) {

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.aceitar) {
            try {
                dbUsuario.child("horarios").updateChildren(map);
            } catch (NullPointerException e) {

            }
            finish();
        }
        if (id == R.id.cancelar) {
            finish();
        }
        if (id == R.id.horarios) {
            Intent intent = new Intent(this, HorariosEdit.class);
            startActivityForResult(intent, 1);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                map = (HashMap<String,Object>) data.getSerializableExtra("map");
                Log.d("DEV/PERFILEDIT", map.toString());
            }
        }
    }
}