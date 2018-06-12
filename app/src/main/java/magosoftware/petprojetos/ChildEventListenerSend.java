package magosoftware.petprojetos;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class ChildEventListenerSend implements ChildEventListener {

    Object variavel;
    Object variavel2;
    Object variavel3;

    public ChildEventListenerSend(Object variavel) {
        this.variavel = variavel;
    }

    public ChildEventListenerSend(Object variavel, Object variavel2) {
        this.variavel = variavel;
        this.variavel2 = variavel2;
    }

    public ChildEventListenerSend(Object variavel, Object variavel2, Object variavel3) {
        this.variavel = variavel;
        this.variavel2 = variavel2;
        this.variavel3 = variavel3;
    }

    public void onChildAdded(DataSnapshot var1, String var2) {

    }

    public void onChildChanged(DataSnapshot var1, String var2) {

    }

    public void onChildRemoved(DataSnapshot var1) {

    }

    public void onChildMoved(DataSnapshot var1, String var2) {

    }

    public void onCancelled(DatabaseError var1) {

    }
}
