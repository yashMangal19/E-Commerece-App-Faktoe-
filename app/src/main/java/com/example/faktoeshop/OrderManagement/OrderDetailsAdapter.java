package com.example.faktoeshop.OrderManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.faktoeshop.R;

import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.ViewHolder>{

    private Context context;
    private List<OrderDetailsModel> list;

    public OrderDetailsAdapter(Context context, List<OrderDetailsModel> orderList) {
        this.context = context;
        this.list = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderDetailsAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dashboard_orders_details_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.ProductName.setText(list.get(position).getName());
        holder.ProductCount.setText("x"+String.valueOf(list.get(position).getCount()));
        holder.ProductPrice.setText("₹"+String.valueOf(list.get(position).getSellingPrice()*list.get(position).getCount()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView ProductName , ProductCount, ProductPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ProductName = itemView.findViewById(R.id.OrderProductName);
            ProductCount = itemView.findViewById(R.id.OrderProductCount);
            ProductPrice = itemView.findViewById(R.id.OrderProductPrice);

        }
    }
}

