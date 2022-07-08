package com.example.faktoeshop.OrderManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.faktoeshop.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{

    private Context context;
    private List<OrderModel> list;
    private List<OrderDetailsModel> orderList;
    private DocumentReference ShopRef;
    private CollectionReference UserRef;
    private String ShopId;
    private int cost;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OrderAdapter(Context context, List<OrderModel> list, DocumentReference ShopRef,CollectionReference UserRef, String ShopId) {
        this.context = context;
        this.list = list;
        this.ShopRef = ShopRef;
        this.UserRef = UserRef;
        this.ShopId = ShopId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dashboard_orders_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.TVShopName.setText(list.get(position).getUserName());

        holder.RVOrderDetails.setLayoutManager(new LinearLayoutManager(context,RecyclerView.VERTICAL, false));

        ShopRef.collection("Orders").document(list.get(position).getUserId())
                .collection("Products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        orderList = new ArrayList<>();
                        cost = 0;
                        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(context, orderList);
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                            OrderDetailsModel orderDetailsModel = document.toObject(OrderDetailsModel.class);
                            cost += orderDetailsModel.getSellingPrice()*orderDetailsModel.getCount();
                            orderList.add(orderDetailsModel);
                        }
                        holder.TVTotalCost.setText(String.valueOf(cost));
                        holder.RVOrderDetails.setAdapter(orderDetailsAdapter);
                    }
                });

        holder.BtnPacked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> isPacked = new HashMap<>();
                isPacked.put("Packed","true");
                UserRef.document(list.get(holder.getAbsoluteAdapterPosition()).getUserId().substring(36))
                        .collection("Orders")
                        .document(list.get(holder.getAbsoluteAdapterPosition()).getUserId().substring(0,36) + ShopId)
                        .update(isPacked);

                ShopRef.collection("Orders")
                        .document(list.get(holder.getAbsoluteAdapterPosition()).getUserId())
                        .delete();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView TVShopName;
        TextView TVTotalCost;
        RecyclerView RVOrderDetails;
        Button BtnPacked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            TVShopName = itemView.findViewById(R.id.OrderShopName);
            TVTotalCost = itemView.findViewById(R.id.OrderManagementTotalCoast);
            RVOrderDetails = itemView.findViewById(R.id.OrderDetailsRecyclerView);
            BtnPacked = itemView.findViewById(R.id.PackedBtn);
        }
    }
}
