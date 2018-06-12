package magosoftware.petprojetos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ConfigActivity extends BaseActivity implements View.OnClickListener {

    private EditText linkDrive;
    private String node;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        linkDrive = findViewById(R.id.field_link);
        findViewById(R.id.aceitar).setOnClickListener(this);
        findViewById(R.id.cancelar).setOnClickListener(this);

        Intent intent = getIntent();
        node = intent.getStringExtra("node");
        preencheForm();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.aceitar) {
            String linkLapidado = linkDrive.getText().toString();
            try {
                linkLapidado = linkLapidado.replace("/mobile", "");
                linkLapidado = linkLapidado.split("\\?")[0];
            } catch (Exception e) {

            }
            mDatabase.child(node).child("drive").setValue(linkLapidado);
            finish();
        }
        else if(id == R.id.cancelar){
            finish();
        }
    }

    private void preencheForm() {
        mDatabase.child(node).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("drive")) {
                    linkDrive.setText(dataSnapshot.child("drive").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
