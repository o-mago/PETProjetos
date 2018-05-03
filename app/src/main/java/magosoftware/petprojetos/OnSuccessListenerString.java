package magosoftware.petprojetos;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by root on 03/03/18.
 */

public class OnSuccessListenerString implements OnSuccessListener<byte[]> {

    Object variavel1;
    Object variavel2;
    Object variavel3;
    public OnSuccessListenerString(Object variavel1) {
        this.variavel1 = variavel1;
    }

    public OnSuccessListenerString(Object variavel1, Object variavel2) {
        this.variavel1 = variavel1;
        this.variavel2 = variavel2;
    }

    public OnSuccessListenerString(Object variavel1, Object variavel2, Object variavel3) {
        this.variavel1 = variavel1;
        this.variavel2 = variavel2;
        this.variavel3 = variavel3;
    }

    public void onSuccess(byte[] var1){

    }
}
