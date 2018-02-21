package magosoftware.petprojetos;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by root on 21/02/18.
 */

public class DataBaseMethods {

    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static String data;

    public static String getData(String db) {

        String[] path = db.split("/");
        Log.d("SPLITOU", path[0]+", "+path[1]+", "+path[2]);
        int i = path.length;
        DatabaseReference database = mDatabase;
        for(int j = 0; j < i; j++) {
            database = database.child(path[j]);
        }
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(String.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        });
        return data;
    }
}
