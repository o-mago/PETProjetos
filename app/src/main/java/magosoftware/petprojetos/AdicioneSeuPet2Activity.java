package magosoftware.petprojetos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pchmn.materialchips.model.Chip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 27/02/18.
 */

public class AdicioneSeuPet2Activity extends BaseActivity implements View.OnClickListener {

    public static final int PICK_IMAGE = 1;
    private ImageView imagem_pet;
    private Button adicionarImagem;
    private LinearLayout botoes;
    private Button cancelar;
    private Button ok;
    private FrameLayout containerFoto;
    private Bitmap bitmap;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference petRef;

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);

        setContentView(R.layout.activity_adicione_seu_pet_2);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        adicionarImagem = findViewById(R.id.adicionar_imagem);
        botoes = findViewById(R.id.botoes);
        findViewById(R.id.adicionar_imagem).setOnClickListener(this);
        imagem_pet = findViewById(R.id.imagem_pet);
        //containerFoto = findViewById(R.id.container_foto);
        containerFoto.removeView(imagem_pet);
        Intent intent = getIntent();
//        String nomePet = intent.getStringExtra("nome");
        String nomePet = "PET F";
        petRef = storageRef.child("imagensPET/"+nomePet.replace(" ", "_")+".jpg");
        setButtons();
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            try {
                Uri selectedImage = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                imagem_pet.setBackgroundDrawable(bitmapDrawable);
                //imagem_pet.setImageURI(selectedImage);
                containerFoto.addView(imagem_pet);
                botoes.removeView(adicionarImagem);
                botoes.addView(ok);
                botoes.addView(cancelar);
            }
            catch(NullPointerException | IOException e) {

            }
        }
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
                containerFoto.removeView(imagem_pet);
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
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
                        Intent intent = new Intent(AdicioneSeuPet2Activity.this, ContainerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        });
    }
}
