package com.example.faktoeshop.Dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.faktoeshop.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private Context context;
    private List<Product> list;
    CollectionReference ref;
    FragmentCommunicationProductEdit mCommunicator;

    public ProductsAdapter(Context context, List<Product> list, CollectionReference ref, FragmentCommunicationProductEdit Communicator) {
        this.list = list;
        this.context = context;
        this.mCommunicator = Communicator;
        this.ref = ref;
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductsAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dashboard_home_product_layout, parent, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ViewHolder holder, int position) {
        holder.productDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.document(list.get(holder.getAbsoluteAdapterPosition()).getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Product " + " is deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        holder.productTitle.setText(list.get(position).getName());

        if(list.get(position).getMRP() == (list.get(position).getSellingPrice())){
            holder.productPrice.setText("Rs. " + list.get(position).getSellingPrice());
        }else{
            holder.productPrice.setText("Rs. " + list.get(position).getMRP());
            holder.productPrice.setTextColor(R.color.light_gray);
            holder.productPrice.setPaintFlags(holder.productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.productPriceSelling.setText("Rs. " + list.get(position).getSellingPrice());
        }

        holder.productCategory.setText(list.get(position).getCategory());

        if(list.get(position).getBrand().equals("")){
            holder.productBrand.setVisibility(View.INVISIBLE);
        }else{
            holder.productBrand.setText(list.get(position).getBrand());
        }

        holder.productRating.setText(list.get(position).getRating());
        holder.productDescription.setText(list.get(position).getDescription());

        Picasso.get().load(list.get(position).getImageUrl()).into(holder.productImage);

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

        if(list.get(holder.getAbsoluteAdapterPosition()).getInStock().equals("true")){
            holder.productOutOfStockBtn.setImageResource(R.drawable.icon_out_of_stock);
        }else if(list.get(holder.getAbsoluteAdapterPosition()).getInStock().equals("false")){
            holder.productOutOfStockBtn.setImageResource(R.drawable.icon_restock);
        }



        holder.productOutOfStockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.get(holder.getAbsoluteAdapterPosition()).getInStock().equals("true")){

                    Map<String,Object> outOfStockMap = new HashMap<>();
                    outOfStockMap.put("InStock","false");
                    ref.document(list.get(holder.getAbsoluteAdapterPosition()).getId()).
                            update(outOfStockMap);
                }else if(list.get(holder.getAbsoluteAdapterPosition()).getInStock().equals("false")){

                    Map<String,Object> outOfStockMap = new HashMap<>();
                    outOfStockMap.put("InStock","true");
                    ref.document(list.get(holder.getAbsoluteAdapterPosition()).getId()).
                            update(outOfStockMap);
                }
            }
        });

        holder.productEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommunicator.respond(holder.getAbsoluteAdapterPosition(),list.get(holder.getAbsoluteAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView productTitle, productPrice, productPriceSelling, productCategory, productBrand, productRating, productDescription, productDescriptionEditor;
        ImageView productImage, productDeleteBtn, productOutOfStockBtn;
        LinearLayout productEditBtn;

        public ViewHolder(@NonNull View itemView) {
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
            productEditBtn = itemView.findViewById(R.id.productEditBtn);

        }
    }
}
