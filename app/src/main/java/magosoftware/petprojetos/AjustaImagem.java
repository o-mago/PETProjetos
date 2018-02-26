package magosoftware.petprojetos;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

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
 * Created by root on 23/02/18.
 */

public class AjustaImagem extends BaseActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener{

    Bitmap bitmap;
    FocusView focusView;
    BitmapDrawable bitmapDrawable;
    SeekBar seekBar;
    float radius = 270;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference perfilRef;
    FirebaseUser user;
    FirebaseAuth mAuth;
    Bitmap resizedPerfil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajusta_imagem);

        focusView = findViewById(R.id.foto_perfil);
        seekBar = findViewById(R.id.zoom_imagem);
        seekBar.setProgress(0);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        perfilRef = storageRef.child("imagensPerfil/"+user.getUid()+".jpg");


        findViewById(R.id.botao_crop).setOnClickListener(this);
        findViewById(R.id.botao_voltar).setOnClickListener(this);

        try {
            Intent intent_perfil = getIntent();
            Uri imagem_uri = Uri.parse(intent_perfil.getStringExtra("imagem"));
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagem_uri);
            int tamanhoPerfil = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 270, getResources().getDisplayMetrics());
            resizedPerfil = Bitmap.createScaledBitmap(bitmap, tamanhoPerfil, tamanhoPerfil, false);
            bitmapDrawable = new BitmapDrawable(getResources(), resizedPerfil);
            focusView.setBackgroundDrawable(bitmapDrawable);
            focusView.setRadius(270);
        }
        catch (IOException ioe) {

        }
        seekBar.setOnSeekBarChangeListener(this);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
        radius = (float)(270-progresValue*2);
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
        if(i == R.id.botao_crop) {
            int tamanhoBD = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
            int inicio = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (270-radius)/2, getResources().getDisplayMetrics());
            int raio = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, radius, getResources().getDisplayMetrics());
            Bitmap croppedBmp = Bitmap.createBitmap(resizedPerfil,
                    inicio,
                    inicio,
                    (int)raio,
                    (int)raio);
            Bitmap resizedBmp = Bitmap.createScaledBitmap(croppedBmp, tamanhoBD, tamanhoBD, false);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = perfilRef.putBytes(data);
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
                    Intent intent = new Intent(AjustaImagem.this, MainActivity.class);
                    startActivity(intent);
                }
            });

        }
        if(i == R.id.botao_voltar) {

        }
    }
}
