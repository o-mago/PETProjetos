package magosoftware.petprojetos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by root on 27/02/18.
 */

public class AdicioneSeuPet2Activity extends BaseActivity implements View.OnClickListener {

    public static final int PICK_IMAGE = 1;
    private ImageView imagem_pet;

    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);

        setContentView(R.layout.activity_adicione_seu_pet_2);

        findViewById(R.id.adicionar_imagem).setOnClickListener(this);
        imagem_pet = findViewById(R.id.imagem_pet);
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
            try {
                Uri selectedImage = data.getData();
                imagem_pet.setImageURI(selectedImage);
            }
            catch (NullPointerException e) {

            }
        }
    }
}
