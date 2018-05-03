package magosoftware.petprojetos;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ValueEventListenerSend implements ValueEventListener {

    Object variavel;
    public ValueEventListenerSend(Object variavel) {
        this.variavel = variavel;
    }

    public void onDataChange(DataSnapshot var1) {

    }

    public void onCancelled(DatabaseError var1) {

    }
}
