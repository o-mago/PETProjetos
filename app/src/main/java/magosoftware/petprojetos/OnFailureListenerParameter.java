package magosoftware.petprojetos;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class OnFailureListenerParameter implements OnFailureListener {

    Object variavel1;
    Object variavel2;
    public OnFailureListenerParameter(Object variavel1) {
        this.variavel1 = variavel1;
    }

    public OnFailureListenerParameter(Object variavel1, Object variavel2) {
        this.variavel1 = variavel1;
        this.variavel2 = variavel2;
    }

    public void onFailure(@NonNull Exception var1) {

    }
}
