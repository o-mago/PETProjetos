package magosoftware.petprojetos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 19/02/18.
 */

public class Perfil extends BaseActivity implements View.OnClickListener {

    RecyclerView mRecyclerView;
    private LineAdapter mAdapter;
    private DatabaseReference dbUsuario;
    FirebaseUser user;
    FirebaseAuth mAuth;
    public static final int PICK_IMAGE = 1;
    CircularImageView imagemPerfil;
    TextView nick;
    TextView nome;
    TextView email;
    TextView curso;
    TextView universidade;
    TextView pet;
    TextView nascimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        dbUsuario = mDatabase.child("Usuarios");
        user = mAuth.getCurrentUser();

        imagemPerfil = findViewById(R.id.foto_perfil);
        nick = findViewById(R.id.nick);
        nome = findViewById(R.id.nome);
        email = findViewById(R.id.email);
        curso = findViewById(R.id.curso);
        universidade = findViewById(R.id.universidade);
        pet = findViewById(R.id.pet);
        nascimento = findViewById(R.id.nascimento);

        if(user != null) {
            preenchePerfil();
        }
        else {
            Log.d("ERRO", "Fudeu Bahia");
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.foto_perfil) {
            Log.d("UNI", "CLICOU");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {

        }
    }

    public void preenchePerfil() {
//        nome.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/nome"));
//        nick.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/nick"));
//        email.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/email"));
//        universidade.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/universidade"));
//        pet.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/pet"));
//        curso.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/curso"));
//        nascimento.setText((String)DataBaseMethods.getData("Usuarios/"+user.getUid()+"/nascimento"));
        mDatabase.child("Usuarios").child(user.getUid()).child("nome").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    nome.setText(dataSnapshot.getValue(String.class));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("UNI", "Deu merda");
                }
        });

        mDatabase.child("Usuarios").child(user.getUid()).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });

        mDatabase.child("Usuarios").child(user.getUid()).child("curso").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                curso.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });

        mDatabase.child("Usuarios").child(user.getUid()).child("universidade").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                universidade.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });

        mDatabase.child("Usuarios").child(user.getUid()).child("nascimento").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nascimento.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });

        mDatabase.child("Usuarios").child(user.getUid()).child("nick").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nick.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });

        mDatabase.child("Usuarios").child(user.getUid()).child("pet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pet.setText(dataSnapshot.getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });
    }

//    protected void feedList(String db, final RecyclerView recycler) {
//        dbUsuario.child(db).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                final List<String> list = new ArrayList<String>();
//                Log.d("UNI", "ON DATA CHANGED OK");
//                for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
//                    String nome = listSnapshots.getValue(String.class);
//                    Log.d("UNI", nome);
//                    list.add(nome);
//                }
//
//                // Configurando o gerenciador de layout para ser uma lista.
//                LinearLayoutManager layoutManager = new LinearLayoutManager(Perfil.this);
//                recycler.setLayoutManager(layoutManager);
//
//                // Adiciona o adapter que irá anexar os objetos à lista.
//                // Está sendo criado com lista vazia, pois será preenchida posteriormente.
//                mAdapter = new LineAdapter(list);
//                recycler.setAdapter(mAdapter);
//
//                // Configurando um dividr entre linhas, para uma melhor visualização.
//                recycler.addItemDecoration(
//                        new DividerItemDecoration(Perfil.this, DividerItemDecoration.VERTICAL));
//
//                // Creating adapter for spinner
////                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Perfil.this, android.R.layout.simple_spinner_item, list);
//
//                // Drop down layout style - list view with radio button
////                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//                // attaching data adapter to spinner
////                spinner.setAdapter(dataAdapter);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("UNI", "Deu merda");
//            }
//        });
//
//    }
}