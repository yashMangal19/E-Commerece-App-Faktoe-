package com.example.faktoeshop.Dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.faktoeshop.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryFilterAdapter extends RecyclerView.Adapter<CategoryFilterAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> list;
    int CurrentSelectedCategory = -1;
    Typeface typeMajorantMedium;
    Typeface typeMajorantRegular;
    FragmentCommunicationCategoryFilter mCommunication;


    public CategoryFilterAdapter(Context context, ArrayList<String> list,FragmentCommunicationCategoryFilter Communication) {
        this.context = context;
        this.list = list;
        this.mCommunication = Communication;
        typeMajorantMedium = Typeface.createFromAsset(context.getAssets(),"fonts/majorant_medium.ttf");
        typeMajorantRegular = Typeface.createFromAsset(context.getAssets(),"fonts/manjorant_regular.ttf");
    }


    @NonNull
    @Override
    public CategoryFilterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryFilterAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dashboard_home_filter_category_checkbox, parent, false));
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CategoryFilterAdapter.ViewHolder holder, int position) {
        holder.RBCategory.setText(list.get(position));

        holder.RBCategory.setChecked(position == CurrentSelectedCategory);

        holder.RBCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    CurrentSelectedCategory = holder.getAbsoluteAdapterPosition();

                    mCommunication.AddElement(holder.getAbsoluteAdapterPosition(), holder.RBCategory.getText().toString());
                }
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

        RadioButton RBCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            RBCategory = itemView.findViewById(R.id.CategoryFilter_RadioBtn);
        }
    }
}

