package magosoftware.petprojetos;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Documentos extends BaseFragment implements LineAdapterDocumentos.OnItemClicked {

    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageRef;
    RecyclerView mRecyclerView;
    LineAdapterDocumentos mAdapter;
    List<Documento> mModels;
    ProgressBar progressBar;
    WebView webView;

    public static Documentos newInstance() {
        Documentos documentos = new Documentos();
        return documentos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
//        Log.d("TAREFASFRAGMENT", tarefaPath);
        return inflater.inflate(R.layout.documentos, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);
        progressBar = getView().findViewById(R.id.progress_bar);
        webView = getView().findViewById(R.id.webview);
        webView.setVisibility(View.GONE);
        setupRecycler();
        setupLista();
    }

    @Override
    public void onItemClick(int position, String path) {
//        webView.setVisibility(View.GONE);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=http://peteletrica.com/wp-content/uploads/2018/07/"+path);
//        final long ONE_MEGABYTE = 1024 * 1024;
//        storageRef.child("documentos/"+path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                webView.setVisibility(View.VISIBLE);
//                WebSettings settings = webView.getSettings();
//                settings.setJavaScriptEnabled(true);
//                settings.setBuiltInZoomControls(true);
//                webView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=http://peteletrica.com/wp-content/uploads/2018/07/"+);
////                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
////                intent.setType("application/pdf");
////                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
////
////                Intent chooser = Intent.createChooser(intent, "Abrir com");
////                try {
////                    startActivity(chooser);
////                } catch (ActivityNotFoundException e) {
////                    // Instruct the user to install a PDF reader here, or something
////                }
////                intent.addCategory(Intent.CATEGORY_OPENABLE);
////                PackageManager pm = getActivity().getPackageManager();
////                List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
////                if (activities.size() > 0) {
////                    startActivity(intent);
////                } else {
////                    Log.d("DEV/DOCUMENTOS", "Não deu");
////                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//
//            }
//        });
    }

    private void setupLista() {
        mModels = new ArrayList<>();
        mDatabase.child("Documentos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot doc : dataSnapshot.getChildren()) {
                    String path = doc.getKey()+".pdf";
                    String nome = doc.getValue(String.class);
                    String tipo = path.split("-")[0];
                    mModels.add(new Documento(tipo, nome, path));
                }
                mAdapter.add(mModels);
                mAdapter.notifyDataSetChanged();
                mModels.clear();
                mRecyclerView.scrollToPosition(0);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mAdapter.setOnClick(this);
    }

    private void setupRecycler() {

        mRecyclerView = getView().findViewById(R.id.lista_documentos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new LineAdapterDocumentos();

        mRecyclerView.setAdapter(mAdapter);
    }
}
