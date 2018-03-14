package magosoftware.petprojetos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by root on 01/03/18.
 */

public class AdicionaImagem extends BaseActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener{

    BitmapDrawable bitmapDrawable;
    SeekBar seekBar;
    float radius;
    float radiusMax;
    StorageReference perfilRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    Bitmap resizedPerfil;
    public static final int PICK_IMAGE = 1;
    private FocusView focusView;
    private Button adicionarImagem;
    private LinearLayout botoes;
    private Button cancelar;
    private Button ok;
    private FrameLayout containerZoom;
    private FrameLayout containerFoto;
    private Bitmap bitmap;
    private LinearLayout pai;
    private TextView pular;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference petRef;
    Bitmap bitmapBorda;
    String tipo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adiciona_imagem_activity);

        seekBar = findViewById(R.id.zoom_imagem);
        seekBar.setProgress(0);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        adicionarImagem = findViewById(R.id.adicionar_imagem);
        botoes = findViewById(R.id.botoes);
        pai = findViewById(R.id.pai);
        findViewById(R.id.pular).setOnClickListener(this);
        pular = findViewById(R.id.pular);
        pular.setOnClickListener(this);
        focusView = findViewById(R.id.imagem_pet);
        containerZoom = findViewById(R.id.container_zoom);
        containerZoom.removeView(seekBar);
        containerFoto = findViewById(R.id.container_foto);
        FrameLayout.LayoutParams lpFoto = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, toPx(0));
        focusView.setLayoutParams(lpFoto);
        Intent intent = getIntent();
//        String nomePet = intent.getStringExtra("nome");
        String child = intent.getStringExtra("caminho");
        tipo = intent.getStringExtra("tipo");
        if(!tipo.equals("novo usuario") && !tipo.equals("novo pet") && !tipo.equals("novo projeto")) {
            pai.removeView(pular);
        }
        petRef = storageRef.child(child);
        setButtons();
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void setButtons() {
        ContextThemeWrapper contextButton = new ContextThemeWrapper(this, R.style.ThemeOverlay_MyDarkButton);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.weight = 1.0f;
        cancelar = new Button(contextButton);
        cancelar.setLayoutParams(lp);
        cancelar.setText("Cancelar");
        cancelar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                botoes.addView(adicionarImagem);
                containerZoom.removeView(focusView);
                botoes.removeView(ok);
                botoes.removeView(cancelar);
            }
        });
        ok = new Button(contextButton);
        ok.setLayoutParams(lp);
        ok.setText("OK");
        ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int tamanhoBD = toPx(150);
                DisplayMetrics displayMetrics = AdicionaImagem.this.getResources().getDisplayMetrics();
                int dpWidth = (int) (bitmapBorda.getWidth()/displayMetrics.density);
                int dpHeight = (int) (bitmapBorda.getHeight()/displayMetrics.density);
                int inicioHorizontal = toPx((dpWidth-radius)/2);
                int inicioVertical = toPx((dpHeight-radius)/2);
                int raio = toPx(radius);
                Bitmap croppedBmp = Bitmap.createBitmap(bitmapBorda,
                        inicioHorizontal,
                        inicioVertical,
                        (int)raio,
                        (int)raio);
                Bitmap resizedBmp = Bitmap.createScaledBitmap(croppedBmp, tamanhoBD, tamanhoBD, false);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resizedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = petRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        if(tipo.equals("novo usuario")) {
                            Intent intent = new Intent(AdicionaImagem.this, EmailPasswordActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(AdicionaImagem.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            try {
                Uri selectedImage = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                //imagem_pet.setImageURI(selectedImage);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(toPx(25), toPx(250));
                lp.gravity = Gravity.CENTER_VERTICAL;
                lp.weight = 0.0f;
                containerZoom.setLayoutParams(lp);
                containerZoom.addView(seekBar);
                botoes.removeView(adicionarImagem);
                botoes.addView(ok);
                botoes.addView(cancelar);
                FrameLayout.LayoutParams lpFoto = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, toPx(300));
                focusView.setLayoutParams(lpFoto);
                focusView.setImageDrawable(bitmapDrawable);
                int dpWidth = toDp(focusView.getWidth());
                int dpHeight = toDp(focusView.getHeight());
                Matrix m = new Matrix();
                m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, toPx(300), toPx(300)), Matrix.ScaleToFit.CENTER);
                Bitmap bitmapFocus = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                bitmapBorda = addWhiteBorder(bitmapFocus, toPx(300));
                if(bitmapBorda.getWidth() > bitmapBorda.getHeight()) {
                    focusView.setRadius(toDp(bitmapBorda.getWidth()));
                    radiusMax = dpWidth;
                    radius = radiusMax;
                }
                else {
                    focusView.setRadius(toDp(bitmapBorda.getHeight()));
                    radiusMax = dpWidth;
                    radius = radiusMax;
                }
                focusView.setRadius(radius);
            }
            catch(NullPointerException | IOException e) {

            }
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
        radius = (float)(radiusMax-progresValue*2);
        focusView.setRadius(radius);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
//        Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
//        Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.adicionar_imagem) {
            Log.d("UNI", "CLICOU");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
        if(i == R.id.pular) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmapPadrao = BitmapFactory.decodeResource(getResources(), R.drawable.ninosca);
            bitmapPadrao.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = petRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    if(tipo.equals("novo usuario")) {
                        Intent intent = new Intent(AdicionaImagem.this, EmailPasswordActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Intent intent = new Intent(AdicionaImagem.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });

            if(tipo.equals("novo usuario")) {
                Intent intent = new Intent(AdicionaImagem.this, EmailPasswordActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Intent intent = new Intent(AdicionaImagem.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public int toPx(float dp){
        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        return px;
    }

    public int toDp(float px){
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int dp = (int) (px/displayMetrics.density);
        return dp;
    }

    private Bitmap addWhiteBorder(Bitmap bmp, int size) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(size, size, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.WHITE);
        int left = (size - bmp.getWidth())/2;
        int top = (size - bmp.getHeight())/2;
        canvas.drawBitmap(bmp, left, top, null);
        return bmpWithBorder;
    }
}
