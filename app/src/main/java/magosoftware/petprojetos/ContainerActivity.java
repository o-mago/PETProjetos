package magosoftware.petprojetos;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by root on 02/03/18.
 */

public class ContainerActivity extends BaseActivity {

    FragmentTransaction ft;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.container_activity);

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, EncontreSeuPet.newInstance());
        ft.commit();
    }
}
