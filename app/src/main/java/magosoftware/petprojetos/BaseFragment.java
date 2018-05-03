package magosoftware.petprojetos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by root on 22/02/18.
 */

public class BaseFragment extends Fragment {
    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    public DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public BaseFragment() {}

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public <T> void ordenar(List<T> list) {
        Collections.sort(list, new Comparator<T>() {
            @Override
            public int compare(T a1, T a2) {
                Log.d("DEV/BASEFRAGMENT", a1.getClass().toString());
                if(a1.getClass() == Usuario.class) {
                    return (((Usuario)a1).getNome()).compareTo(((Usuario)a2).getNome());
                }
                else if(a1.getClass() == Pet.class){
                    return (((Pet)a1).getNome()).compareTo(((Pet)a2).getNome());
                }
                else {
                    return (((Projeto)a1).getNome()).compareTo(((Projeto)a2).getNome());
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
