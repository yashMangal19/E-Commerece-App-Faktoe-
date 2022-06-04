package com.example.faktoeshop.Dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.faktoeshop.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Fragment_Dashboard_Home extends Fragment {
    View v;

    private String ShopState, ShopCity, ShopId, ShopPhoneNumber;
    private Button InStockBtn, OutOfStockBtn;

    private SharedPreferences mPreferences;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef;

    private ProductAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_dashboard_home, container, false);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ShopState = mPreferences.getString(getString(R.string.ShopState),"").toUpperCase();
        ShopCity = mPreferences.getString(getString(R.string.ShopDistrict),"");
        ShopPhoneNumber = mPreferences.getString(getString(R.string.ShopPhoneNumber),"");
        ShopId = mPreferences.getString(getString(R.string.ShopId),"");
        InStockBtn = v.findViewById(R.id.InStock_Btn);
        OutOfStockBtn = v.findViewById(R.id.OutOfStock_Btn);

        productRef = db.collection("Shop").document(ShopState)
                .collection(ShopCity).document(ShopId).collection("Products");

        setUpRecyclerView();

        return v;
    }

    private void setUpRecyclerView() {
        Query query = productRef.whereEqualTo("InStock", "true");
        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();

        adapter = new ProductAdapter(getActivity(),options, productRef);

        RecyclerView recyclerView = v.findViewById(R.id.RecyclerView_ProductList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
