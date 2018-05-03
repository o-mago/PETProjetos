package magosoftware.petprojetos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarLogin();
            }
        }, 2000);
    }

    private void mostrarLogin() {
        Intent intent = new Intent(this, EmailPasswordActivity.class);
        startActivity(intent);
        finish();
    }
}
