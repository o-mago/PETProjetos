package magosoftware.petprojetos;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by root on 03/03/18.
 */

public class OnSuccessListenerString implements OnSuccessListener<byte[]> {

    String variavel;
    public OnSuccessListenerString(String variavel) {
        this.variavel = variavel;
    }

    public void onSuccess(byte[] var1){

    }
}
