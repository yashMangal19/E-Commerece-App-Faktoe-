package com.example.faktoeshop.OrderManagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.faktoeshop.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class FragmentOrderManagement extends Fragment {
    View v;

    RecyclerView OrdersRecyclerView;
    OrderAdapter orderAdapter;

    DocumentReference ShopRef;
    CollectionReference UserRef;

    private SharedPreferences mPreferences;
    private String ShopState, ShopCity, ShopId, ShopPhoneNumber;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<OrderModel> orderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_dashboard_orders, container, false);

        OrdersRecyclerView = v.findViewById(R.id.OrdersRecyclerView);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ShopState = mPreferences.getString(getString(R.string.ShopState),"").toUpperCase();
        ShopCity = mPreferences.getString(getString(R.string.ShopDistrict),"");
        ShopPhoneNumber = mPreferences.getString(getString(R.string.ShopPhoneNumber),"");
        ShopId = mPreferences.getString(getString(R.string.ShopId),"");

        OrdersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL, false));

        ShopRef =  db.collection("Shop").document(ShopState).collection(ShopCity)
                .document(ShopId);

        UserRef =  db.collection("Users").document(ShopState).collection(ShopCity);

        db.collection("Shop").document(ShopState).collection(ShopCity)
                .document(ShopId).collection("Orders")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        orderList = new ArrayList<>();
                        orderAdapter = new OrderAdapter(getContext(), orderList , ShopRef, UserRef, ShopId);
                        for(QueryDocumentSnapshot document : value){
                            db.collection("Users").document(ShopState).collection(ShopCity)
                                    .document(document.getId().substring(36))
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            OrderModel orderModel = new OrderModel();
                                            orderModel.setUserName(documentSnapshot.getString("Name"));
                                            Log.d("000000",document.getId());
                                            orderModel.setUserId(document.getId());
                                            orderList.add(orderModel);
                                            orderAdapter.notifyDataSetChanged();
                                        }
                                    });
                            OrdersRecyclerView.setAdapter(orderAdapter);
                        }
                    }
                });



        return v;
    }
}
