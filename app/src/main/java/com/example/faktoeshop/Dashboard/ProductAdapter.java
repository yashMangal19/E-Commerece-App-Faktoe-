package com.example.faktoeshop.Dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.faktoeshop.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProductAdapter extends FirestoreRecyclerAdapter<Product, ProductAdapter.ProductHolder> {

    CollectionReference ref;
    Context context;

    public ProductAdapter(Context context, @NonNull FirestoreRecyclerOptions<Product> options, CollectionReference ref) {
        super(options);
        this.ref = ref;
        this.context = context;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Product model) {

        holder.productDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.document(model.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Product " + model.getName() + " is deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        holder.productTitle.setText(model.getName());

        if(model.getMRP().equals(model.getSellingPrice())){
            holder.productPrice.setText("Rs. " + model.getSellingPrice());
        }else{
            holder.productPrice.setText("Rs. " +model.getMRP());
            holder.productPrice.setTextColor(R.color.light_gray);
            holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productPriceSelling.setText("Rs. " +model.getSellingPrice());
        }

        holder.productCategory.setText(model.getCategory());

        if(model.getBrand().equals("")){
            holder.productBrand.setVisibility(View.INVISIBLE);
        }else{
            holder.productBrand.setText(model.getBrand());
        }

        holder.productRating.setText(model.getRating());
        holder.productDescription.setText(model.getDescription());

        Picasso.get().load(model.getImageUrl()).into(holder.productImage);

        holder.productDescriptionEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.productDescriptionEditor.getText().toString().equals("Read more")){
                    holder.productDescription.setMaxLines(100);
                    holder.productTitle.setMaxLines(5);
                    holder.productDescriptionEditor.setText("Read less");
                }else{
                    holder.productDescription.setMaxLines(1);
                    holder.productTitle.setMaxLines(1);
                    holder.productDescriptionEditor.setText("Read more");
                }
            }
        });

        holder.productOutOfStockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> outOfStockMap = new HashMap<>();
                outOfStockMap.put("InStock","false");
                ref.document(model.getId()).
                        update(outOfStockMap);
            }
        });
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dashboard_home_product_layout, parent, false);

        return new ProductHolder(v);
    }

    static class ProductHolder extends RecyclerView.ViewHolder {
        TextView productTitle, productPrice, productPriceSelling, productCategory, productBrand, productRating, productDescription, productDescriptionEditor;
        ImageView productImage, productDeleteBtn, productOutOfStockBtn;
        public ProductHolder(@NonNull View itemView) {
            super(itemView);

            productDeleteBtn = itemView.findViewById(R.id.productDeleteBtn);
            productTitle = itemView.findViewById(R.id.productTitle);
            productOutOfStockBtn = itemView.findViewById(R.id.productOutOfStockBtn);
            productPrice = itemView.findViewById(R.id.productPrice);
            productPriceSelling = itemView.findViewById(R.id.productPriceSelling);
            productCategory = itemView.findViewById(R.id.productCategory);
            productBrand = itemView.findViewById(R.id.productBrand);
            productRating = itemView.findViewById(R.id.productRating);
            productDescription = itemView.findViewById(R.id.productDescription);
            productImage = itemView.findViewById(R.id.productImage);
            productDescriptionEditor = itemView.findViewById(R.id.productDescriptionEditor);
        }
    }

}
