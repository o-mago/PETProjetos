package magosoftware.petprojetos;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pchmn.materialchips.model.Chip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by root on 01/03/18.
 */

public class EncontreSeuPet extends BaseFragment implements SearchView.OnQueryTextListener, LineAdapterPet.OnItemClicked {

    RecyclerView mRecyclerView;
    SearchView searchView;
    private LineAdapterPet mAdapter;
    private List<Pet> mModels;
    FirebaseStorage storage;
    StorageReference storageRef;
    Drawable bitmapDrawablePet;
    FragmentTransaction ft;
    String nomePet;
    private int cont = 0;
    private int i = 0;
    private StorageReference perfilRef;
    final long ONE_MEGABYTE = 1024 * 1024;
    private ValueEventListener valueEventListener;
    ProgressBar progressBar;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    private static List<Pet> filter(List<Pet> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Pet> filteredModelList = new ArrayList<>();
        for (Pet model : models) {
            final String text = model.getNome().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private static final Comparator<Pet> ALPHABETICAL_COMPARATOR = new Comparator<Pet>() {
        @Override
        public int compare(Pet a, Pet b) {
            return a.getNome().compareTo(b.getNome());
        }
    };

    public static EncontreSeuPet newInstance() {
        EncontreSeuPet encontreSeuPet = new EncontreSeuPet();
        return encontreSeuPet;
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
        searchView.setQueryHint("Pesquisar PET");
        setupRecycler();
        mModels = new ArrayList<>();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot listSnapshots : dataSnapshot.getChildren()) {
                    String nomePet = listSnapshots.child("nome").getValue(String.class);
                    String codigo = listSnapshots.getKey();
//                    String universidade = listSnapshots.child("universidade").getValue(String.class);
//                    String siglaUniversidade = universidade.split("-")[0];
                    cont++;
                    Log.d("PETS", nomePet);
                    if (nomePet != null) {
                        try {
                            perfilRef = storageRef.child("imagensPET/" + codigo + ".jpg");
                            Log.d("LOGO", perfilRef.getPath());
                            OnSuccessListener onSuccessListener = new OnSuccessListenerString(codigo, perfilRef, nomePet) {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    try {
                                        Bitmap bitmapPet = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        Bitmap resizedBmp = Bitmap.createScaledBitmap(bitmapPet, toPx(100), toPx(100), false);
                                        bitmapDrawablePet = new BitmapDrawable(getResources(), resizedBmp);
//                                        nomePet = ((StorageReference) variavel2).getName();
//                                        nomePet = nomePet.split(".jpg")[0];
//                                        nomePet = nomePet.replace("_", " ");
                                        Log.d("LOGO", bitmapPet.toString());
                                        mModels.add(new Pet((String) variavel1, (String) variavel3, bitmapDrawablePet));
                                        i++;
                                        if (i == cont) {
                                            progressBar.setVisibility(View.GONE);
                                            searchView.setQuery(" ", false);
                                            searchView.setQuery("", false);
                                        }
                                    }
                                    catch (IllegalStateException e) {

                                    }
                                }
                            };
                            perfilRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(onSuccessListener).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.d("LOGO", "Deu merda");
                                    bitmapDrawablePet = getResources().getDrawable(R.drawable.pet_logo);
                                }
                            });
                        } catch (NullPointerException e) {
//                            bitmapDrawablePet = getResources().getDrawable(R.drawable.pet_logo);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("UNI", "Deu merda");
            }
        };
        mDatabase.child("PETs").addListenerForSingleValueEvent(valueEventListener);
        mAdapter.add(mModels);
        mAdapter.setOnClick(this);
    }

    @Override
    public void onItemClick(int position, String nome, String codigo) {
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        if(nome.equals("Não encontrou seu PET?")) {
            Intent intent = new Intent(getActivity(), AdicioneSeuPetActivity.class);
            startActivity(intent);
        }
        else {
            Bundle bundle = new Bundle();
            bundle.putString("nome", nome);
            bundle.putString("node", codigo);
            ft = getFragmentManager().beginTransaction();
            Fragment fragment = PerfilPetFragment.newInstance();
            fragment.setArguments(bundle);
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void setupRecycler() {

        // Configurando o gerenciador de layout para ser uma lista.
        mRecyclerView = getView().findViewById(R.id.lista_pet);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        // Adiciona o adapter que irá anexar os objetos à lista.
        // Está sendo criado com lista vazia, pois será preenchida posteriormente.
        mAdapter = new LineAdapterPet(ALPHABETICAL_COMPARATOR);
        //mAdapter = new LineAdapterPet(new ArrayList<>(0));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        });

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
        final List<Pet> filteredModelList = filter(mModels, query);
        Drawable padrao = getResources().getDrawable(R.drawable.pet_logo);
        mAdapter.replaceAll(filteredModelList, padrao);
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
        perfilRef = null;
        valueEventListener = null;
        progressBar = null;
    }
}
