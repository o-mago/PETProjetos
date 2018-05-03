package magosoftware.petprojetos;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class CompletionListenerParameter implements DatabaseReference.CompletionListener {
    Object variavel1;
    Object variavel2;
    Object variavel3;


    public CompletionListenerParameter (Object variavel1) {
        this.variavel1 = variavel1;
    }

    public CompletionListenerParameter (Object variavel1, Object variavel2) {
        this.variavel1 = variavel1;
        this.variavel2 = variavel2;

    }

    public CompletionListenerParameter (Object variavel1, Object variavel2, Object variavel3) {
        this.variavel1 = variavel1;
        this.variavel2 = variavel2;
        this.variavel3 = variavel3;

    }

    public void onComplete(DatabaseError var1, DatabaseReference var2) {

    }
}
