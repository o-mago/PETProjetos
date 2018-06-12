package magosoftware.petprojetos;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ValueEventListenerSend implements ValueEventListener {

    Object variavel;
    Object variavel2;
    Object variavel3;

    public ValueEventListenerSend(Object variavel) {
        this.variavel = variavel;
    }

    public ValueEventListenerSend(Object variavel, Object variavel2) {
        this.variavel = variavel;
        this.variavel2 = variavel2;
    }

    public ValueEventListenerSend(Object variavel, Object variavel2, Object variavel3) {
        this.variavel = variavel;
        this.variavel2 = variavel2;
        this.variavel3 = variavel3;
    }

    public void onDataChange(DataSnapshot var1) {

    }

    public void onCancelled(DatabaseError var1) {

    }
}
