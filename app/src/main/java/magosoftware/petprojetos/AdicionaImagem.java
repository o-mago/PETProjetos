package magosoftware.petprojetos;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    String tipo = "";
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adiciona_imagem_activity);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        seekBar = findViewById(R.id.zoom_imagem);
        seekBar.setProgress(0);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        adicionarImagem = findViewById(R.id.adicionar_imagem);
        adicionarImagem.setOnClickListener(this);
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
        try {
            tipo = intent.getStringExtra("tipo");
        }
        catch (Exception e) {
            tipo = "";
        }
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
                int tamanhoBD = toPx(80);
                DisplayMetrics displayMetrics = AdicionaImagem.this.getResources().getDisplayMetrics();
                int dpWidth = (int) (bitmapBorda.getWidth()/displayMetrics.density);
                int dpHeight = (int) (bitmapBorda.getHeight()/displayMetrics.density);
                int inicioHorizontal = toPx((dpWidth-radius)/2);
                if(inicioHorizontal<0) {
                    inicioHorizontal=0;
                }
                int inicioVertical = toPx((dpHeight-radius)/2);
                if(inicioVertical<0) {
                    inicioVertical=0;
                }
                int raio = toPx(radius);
//                if(radius > )
                Log.d("DEV/ADICIONAIMAGEM", "dpWidth: "+dpWidth
                        +"; dpHeight: "+dpHeight
                        +"; inicioHorizontal: "+inicioHorizontal
                        +"; inicioVertical: "+inicioVertical
                        +"; raio: "+raio);
                Bitmap croppedBmp = Bitmap.createBitmap(bitmapBorda,
                        inicioHorizontal,
                        inicioVertical,
                        (int)raio,
                        (int)raio);
                Bitmap resizedBmp = Bitmap.createScaledBitmap(croppedBmp, tamanhoBD, tamanhoBD, false);
//                mDatabase.child("Usuarios").child(user.getUid()).child("update").setValue(true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resizedBmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
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
//                            Intent intent = new Intent(AdicionaImagem.this, MainActivity.class);
//                            startActivity(intent);
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
                    if(radius>300) {
                        radius = 300;
                    }
                }
                else {
                    focusView.setRadius(toDp(bitmapBorda.getHeight()));
                    radiusMax = dpWidth;
                    radius = radiusMax;
                    if(radius>300) {
                        radius = 300;
                    }
                }
                focusView.setRadius(radius);
                Log.d("DEV/ADICIONAIMAGEM", ""+radius);
            }
            catch(NullPointerException | IOException e) {

            }
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
        radius = (float)(radiusMax-progresValue*2);
        focusView.setRadius(radius);
        Log.d("DEV/ADICIONAIMAGEM", ""+radius);
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
            verificaPermissao();
            Log.d("UNI", "CLICOU");
        }
        if(i == R.id.pular) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmapPadrao = BitmapFactory.decodeResource(getResources(), R.drawable.pet_logo);
            Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPadrao, toPx(80), toPx(80), false);
            resizedBmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
//            bitmapPadrao.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
//                        Intent intent = new Intent(AdicionaImagem.this, EmailPasswordActivity.class);
//                        startActivity(intent);
                        finish();
                    }
                    else {
//                        Intent intent = new Intent(AdicionaImagem.this, MainActivity.class);
//                        startActivity(intent);
                        finish();
                    }
                }
            });

            if(tipo.equals("novo usuario")) {
//                Intent intent = new Intent(AdicionaImagem.this, EmailPasswordActivity.class);
//                startActivity(intent);
                finish();
            }
            else {
//                Intent intent = new Intent(AdicionaImagem.this, MainActivity.class);
//                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void verificaPermissao() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= 23) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    }

    @Override
    public void onBackPressed() {
        if(tipo.equals("novo usuario") || tipo.equals("novo projeto") || tipo.equals("novo pet")) {

        }
        else {
            super.onBackPressed();
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
