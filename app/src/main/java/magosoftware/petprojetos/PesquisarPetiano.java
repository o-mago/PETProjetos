package magosoftware.petprojetos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by root on 07/03/18.
 */

public class PesquisarPetiano extends BaseFragment implements SearchView.OnQueryTextListener, LineAdapterPesquisarPetiano.OnItemClicked {

    RecyclerView mRecyclerView;
    SearchView searchView;
    private LineAdapterPesquisarPetiano mAdapter;
    private List<Usuario> mModels;
    FirebaseStorage storage;
    StorageReference storageRef;
    Drawable bitmapDrawablePet;
    FragmentTransaction ft;
    String nomePet;
    private int cont = 0;
    private int i = 0;
    private ProgressBar progressBar;
    private ValueEventListener valueEventListener;

    private static List<Usuario> filter(List<Usuario> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Usuario> filteredModelList = new ArrayList<>();
        for (Usuario model : models) {
            final String text = model.getNome().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private static final Comparator<Usuario> ALPHABETICAL_COMPARATOR = new Comparator<Usuario>() {
        @Override
        public int compare(Usuario a, Usuario b) {
            return a.getNome().compareTo(b.getNome());
        }
    };

    public static PesquisarPetiano newInstance() {
        PesquisarPetiano pesquisarPetiano = new PesquisarPetiano();
        return pesquisarPetiano;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        return inflater.inflate(R.layout.activity_encontre_seu_pet, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedIntanceState) {
        super.onActivityCreated(savedIntanceState);

//        // Get the intent, verify the action and get the query
//        Intent intent = getIntent();
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            doMySearch(query);
//        }
        progressBar = getView().findViewById(R.id.progress_bar);
        searchView = getView().findViewById(R.id.field_search);
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);
        searchView.clearFocus();
//        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
//        searchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchView.setIconified(false);
//            }
//        });
        searchView.setQueryHint("Pesquisar Petiano");
        setupRecycler();
        mModels = new ArrayList<>();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                    final String nome = listSnapshots.child("nome").getValue(String.class);
                    final String codigo = listSnapshots.getKey();
//                    String universidade = listSnapshots.child("universidade").getValue(String.class);
//                    String siglaUniversidade = universidade.split("-")[0];
                    cont++;
                    Log.d("USUARIOS", codigo);
                    if (codigo != null) {
                        try {
                            final StorageReference perfilRef = storageRef.child("imagensPerfil/" + codigo + ".jpg");
                            Log.d("Imagem", perfilRef.getPath());
                            final long ONE_MEGABYTE = 1024 * 1024;
                            perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    try{
                                        Bitmap bitmapPet = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPet, toPx(100), toPx(100), false);
                                        bitmapDrawablePet = new BitmapDrawable(getResources(), resizedBmp);
                                        String[] nomes = nome.split(" ");
                                        String nomeSobrenome;
                                        if(nomes.length > 1) {
                                            nomeSobrenome = nomes[0] + " " + nomes[1];
                                            Log.d("NOME", nomeSobrenome);
                                        }
                                        else {
                                            nomeSobrenome = nome;
                                        }
                                        mModels.add(new Usuario(nomeSobrenome, bitmapDrawablePet, codigo));
                                        i++;
                                        if(i == cont) {
                                            Log.d("DEV/PESQUISARPETIANO", "Mostra petianos");
                                            progressBar.setVisibility(View.GONE);
                                            searchView.setQuery(" ", false);
                                            searchView.setQuery("", false);
                                        }
                                    } catch (IllegalStateException e) {

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.d("LOGO", "Deu merda");
                                    bitmapDrawablePet = getResources().getDrawable(R.drawable.pet_logo);
                                }
                            });
                        } catch (NullPointerException e) {
                            bitmapDrawablePet = getResources().getDrawable(R.drawable.pet_logo);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        };
        mDatabase.child("Usuarios").addListenerForSingleValueEvent(valueEventListener);
        mAdapter.add(mModels);
        mAdapter.setOnClick(this);
    }

    @Override
    public void onItemClick(int position, String codigo) {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        Bundle bundle = new Bundle();
        bundle.putString("codigo", codigo);
        ft = getFragmentManager().beginTransaction();
        Fragment fragment = Perfil.newInstance();
        fragment.setArguments(bundle);
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void setupRecycler() {

        // Configurando o gerenciador de layout para ser uma lista.
        mRecyclerView = getView().findViewById(R.id.lista_pet);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        // Adiciona o adapter que irá anexar os objetos à lista.
        // Está sendo criado com lista vazia, pois será preenchida posteriormente.
        mAdapter = new LineAdapterPesquisarPetiano(ALPHABETICAL_COMPARATOR);
        //mAdapter = new LineAdapterPet(new ArrayList<>(0));
        mRecyclerView.setAdapter(mAdapter);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
//                layoutManager.getOrientation());
//        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // Configurando um dividr entre linhas, para uma melhor visualização.
//        mRecyclerView.addItemDecoration(
//                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<Usuario> filteredModelList = filter(mModels, query);
        mAdapter.replaceAll(filteredModelList);
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    public int toPx(float dp){
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        return px;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mDatabase.child("PETs").removeEventListener(valueEventListener);
        searchView.setOnQueryTextListener(null);
        mAdapter.setOnClick(null);

        mRecyclerView = null;
        searchView = null;
        mAdapter = null;
        mModels = null;
        storage = null;
        storageRef = null;
        bitmapDrawablePet = null;
        ft = null;
        nomePet = null;
        progressBar = null;
    }
}
