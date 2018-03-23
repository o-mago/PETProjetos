package magosoftware.petprojetos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerPalette;
import com.android.colorpicker.ColorPickerSwatch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by root on 14/03/18.
 */

public class AdicioneEquipe extends BaseActivity implements View.OnClickListener {
    FirebaseUser user;
    FirebaseAuth mAuth;
    private EditText nomeEquipe;
    private String nomePET;
    private String nomeProjeto;
    private DatabaseReference dbEquipes;
    public SharedPreferences sharedPref;
    private AlertDialog alert;
    int corEscolhida;
    FrameLayout frameCor;

    @Override
    public void onCreate(Bundle savedInstantState) {
        super.onCreate(savedInstantState);
        setContentView(R.layout.adiciona_equipe);

        sharedPref = this.getSharedPreferences("todoApp", 0);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Intent intent = getIntent();
        nomePET = intent.getStringExtra("nome_pet");
        nomeProjeto = intent.getStringExtra("nome_projeto");
        dbEquipes = mDatabase.child("PETs").child(nomePET).child("projetos").child(nomeProjeto).child("equipes");
        nomeEquipe = findViewById(R.id.field_nome);
        frameCor = findViewById(R.id.corEscolhida);
        findViewById(R.id.adicionar_equipe).setOnClickListener(this);
        findViewById(R.id.seleciona_cor).setOnClickListener(this);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final ColorPickerPalette colorPickerPalette =
                (ColorPickerPalette) layoutInflater.inflate(R.layout.color_picker_pallete, null);

        final int[] colors = getResources().getIntArray(R.array.cores);

        alert = new AlertDialog.Builder(this, R.style.Theme_AppCompat)
                .setTitle(R.string.titulo_color_picker)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setView(colorPickerPalette)
                .create();
        colorPickerPalette.init(colors.length, 5, new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                corEscolhida = color;
                frameCor.setBackgroundColor(corEscolhida);
                colorPickerPalette.drawPalette(colors, color);
            }
        });
        colorPickerPalette.drawPalette(colors, Color.parseColor("#F6402C"));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.seleciona_cor) {
            alert.show();
        }

        if(id == R.id.adicionar_equipe) {
            String nome = nomeEquipe.getText().toString();
            String nomeSemEspaco = nome;
            try {
                nomeSemEspaco = nomeSemEspaco.replace(" ", "_");
            }
            catch (NullPointerException e) {

            }
            dbEquipes.child(nomeSemEspaco).child("nome").setValue(nome);
            dbEquipes.child(nomeSemEspaco).child("cor").setValue(corEscolhida);
            dbEquipes.child(nomeSemEspaco).orderByPriority();
            finish();
        }
    }
}
