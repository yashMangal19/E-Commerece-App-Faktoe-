package com.example.faktoeshop.Dashboard;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.faktoeshop.R;
import com.example.faktoeshop.signUpSignIn.ShopOnboardClass;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Fragment_Dashboard_Home extends Fragment {
    View v;

    /*
    Edit Product Dialog Variables
     */

    RecyclerView productsRecyclerView;
    ProductsAdapter productsAdapter;
    List<Product> productsList;

    TextInputLayout ETProductNameLayout;
    TextInputLayout ETProductDescriptionLayout;
    TextInputLayout ETProductBrandLayout;
    TextInputLayout ETProductMRPLayout;
    TextInputLayout ETProductSPLayout;
    TextInputLayout ACTSelectCategoryLayout;

    EditText ETProductName ;
    EditText ETProductDescription;
    EditText ETProductBrand;
    EditText ETProductMRP ;
    EditText ETProductSP ;
    AutoCompleteTextView ACTSelectCategory;

    View bottomBarCategory ;
    View bottomBarProductImage;
    View bottomBarGeneral ;

    ImageView IVProductImage;
    ImageView IVNewImage;

    CardView CVShowFilters;
    EditText ETSearchProduct;
    CardView CVSearchProductBtn;

    /*
    _______________________________________________
     */

    private String ShopState, ShopCity, ShopId, ShopPhoneNumber;

    private SharedPreferences mPreferences;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference productRef;

    Dialog dialog;
    Dialog FilterDialog;

    /*
    Filter Dialog Variables
     */

    HashMap<String,String> FilterMap = new HashMap<String,String>();

    RecyclerView RecyclerViewCategoryFilter;
    ArrayList<String> categoryFilterList;
    CategoryFilterAdapter categoryFilterAdapter;
    String categoryListToBeFiltered;
    private ShopOnboardClass mapObjectFilter;

    Typeface typeMajorantMedium;
    Typeface typeMajorantRegular;

    LinearLayout CurrentSelectedFilterLayout;
    View CurrentSelectedFilter;

    RadioButton CurrentSortFilter;
    RadioButton CurrentRatingFilter;
    RadioButton CurrentAvailabilityFilter;

    RadioButton RBSortDefault ;
    RadioButton RBSortRating ;
    RadioButton RBSortCostHTL ;
    RadioButton RBSortCostLTH ;
    RadioButton RBRating4_5 ;
    RadioButton RBRating4 ;
    RadioButton RBRating3_5;
    RadioButton RBRating0 ;
    RadioButton RBAvailabilityInStock;
    RadioButton RBAvailabilityOutOfStock;

    private ListenerRegistration noteListener;


    /*
    ____________________________________________
     */

    private ArrayAdapter<String> selectCategoryAdapter;
    private ShopOnboardClass mapObject;
    private ActivityResultLauncher<String> launcher;
    private Uri uri;
    private String ImageUrl;
    private FirebaseStorage storage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_dashboard_home, container, false);

        FilterMap.put("Sort",null);
        FilterMap.put("Rating",null);
        FilterMap.put("Availability",null);
        FilterMap.put("Category",null);

        typeMajorantMedium = Typeface.createFromAsset(requireActivity().getAssets(),"fonts/majorant_medium.ttf");
        typeMajorantRegular = Typeface.createFromAsset(requireActivity().getAssets(),"fonts/manjorant_regular.ttf");

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        productsRecyclerView = v.findViewById(R.id.RecyclerView_ProductList);
        CVShowFilters = v.findViewById(R.id.ShowFilterBtn_CardView);
        ETSearchProduct = v.findViewById(R.id.EditText_searchProduct);
        CVSearchProductBtn = v.findViewById(R.id.SearchProductBtn_CardView);

        productsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL, false));

        ShopState = mPreferences.getString(getString(R.string.ShopState),"").toUpperCase();
        ShopCity = mPreferences.getString(getString(R.string.ShopDistrict),"");
        ShopPhoneNumber = mPreferences.getString(getString(R.string.ShopPhoneNumber),"");
        ShopId = mPreferences.getString(getString(R.string.ShopId),"");

        productRef = db.collection("Shop").document(ShopState)
                .collection(ShopCity).document(ShopId).collection("Products");

        noteListener = productRef.whereEqualTo("InStock","true")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        productsList = new ArrayList<>();
                        productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                        for (QueryDocumentSnapshot document : value) {
                            Log.d("0000000",document.get("SellingPrice").getClass().toString());
                            Product product = document.toObject(Product.class);
                            productsList.add(product);
                            productsAdapter.notifyDataSetChanged();

                        }
                        productsRecyclerView.setAdapter(productsAdapter);
                    }
                });

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent()
                , new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        IVProductImage.setImageURI(result);
                        uri = result;
                        bottomBarProductImage.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                        IVNewImage.setVisibility(View.INVISIBLE);
                    }
                });

        CVShowFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });

        CVSearchProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ETSearchProduct.getText().toString().trim().isEmpty()){
                    productRef.orderBy("Name")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    productsList = new ArrayList<>();
                                    productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Product product = document.toObject(Product.class);
                                        if(product.getName().toUpperCase(Locale.ROOT).contains(ETSearchProduct.getText().toString().trim().toUpperCase(Locale.ROOT))){
                                            productsList.add(product);
                                            productsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    productsRecyclerView.setAdapter(productsAdapter);
                                }
                            });
                }else{
                    Toast.makeText(getActivity(), "Enter something in search bar",Toast.LENGTH_SHORT).show();
                }

            }
        });


        return v;
    }

    private void showFilterDialog(){
        FilterDialog = new Dialog(getActivity());
        FilterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        FilterDialog.setCancelable(false);
        FilterDialog.setContentView(R.layout.fragment_dashboard_home_dialog_filters);

        ImageView DismissFilterDialogBtn = FilterDialog.findViewById(R.id.FilterDialogCloseBtn);

        LinearLayout LLSelectSortFilter = FilterDialog.findViewById(R.id.SelectSortFilter_LinearLayout);
        LinearLayout LLSelectCategoryFilter = FilterDialog.findViewById(R.id.SelectCategoryFilter_LinearLayout);
        LinearLayout LLSelectRatingFilter = FilterDialog.findViewById(R.id.SelectRatingFilter_LinearLayout);
        LinearLayout LLSelectAvailabilityFilter = FilterDialog.findViewById(R.id.SelectAvailabilityFilter_LinearLayout);

        View VSideBarSortFilter = FilterDialog.findViewById(R.id.sideBarSortFilter);
        View VSideBarCategoryFilter = FilterDialog.findViewById(R.id.sideBarCategoryFilter);
        View VSideBarRatingFilter = FilterDialog.findViewById(R.id.sideBarRatingFilter);
        View VSideBarAvailabilityFilter = FilterDialog.findViewById(R.id.sideBarAvailabilityFilter);

        LinearLayout LLSortFilter = FilterDialog.findViewById(R.id.FilterDialogSortFilter_LinearLayout);
        LinearLayout LLCategoryFilter = FilterDialog.findViewById(R.id.FilterDialogCategoryFilter_LinearLayout);
        LinearLayout LLRatingFilter = FilterDialog.findViewById(R.id.FilterDialogRatingFilter_LinearLayout);
        LinearLayout LLAvailabilityFilter = FilterDialog.findViewById(R.id.FilterDialogAvailabilityFilter_LinearLayout);

        RecyclerViewCategoryFilter = FilterDialog.findViewById(R.id.CategoryFilter_RecyclerView);

        RBSortDefault = FilterDialog.findViewById(R.id.SortFilter_Default_RadioBtn);
        RBSortRating = FilterDialog.findViewById(R.id.SortFilter_Rating_RadioBtn);
        RBSortCostHTL = FilterDialog.findViewById(R.id.SortFilter_CostHighToLow_RadioBtn);
        RBSortCostLTH = FilterDialog.findViewById(R.id.SortFilter_CostLowToHigh_RadioBtn);

        RBRating4_5 = FilterDialog.findViewById(R.id.RatingFilter_4_5_RadioBtn);
        RBRating4 = FilterDialog.findViewById(R.id.RatingFilter_4_0_RadioBtn);
        RBRating3_5 = FilterDialog.findViewById(R.id.RatingFilter_3_5_RadioBtn);
        RBRating0 = FilterDialog.findViewById(R.id.RatingFilter_0_0_RadioBtn);

        RBAvailabilityInStock = FilterDialog.findViewById(R.id.AvailabilityFilter_InStock_RadioBtn);
        RBAvailabilityOutOfStock = FilterDialog.findViewById(R.id.AvailabilityFilter_OutOfStock_RadioBtn);

        Button BtnApplyFilters = FilterDialog.findViewById(R.id.BtnApplyFilters);
        TextView BtnClearFilters = FilterDialog.findViewById(R.id.BtnClearFilters);

        if(FilterMap.get("Sort") == null){
            CurrentSortFilter = RBSortDefault;
        }else if(FilterMap.get("Sort").equals("Rating")){
            CurrentSortFilter = RBSortRating;
        }else if(FilterMap.get("Sort").equals("SellingPriceD")){
            CurrentSortFilter = RBSortCostHTL;
        }else if(FilterMap.get("Sort").equals("SellingPriceA")){
            CurrentSortFilter = RBSortCostLTH;
        }

        if(FilterMap.get("Rating") == null){
            CurrentRatingFilter = RBRating0;
        }else if(FilterMap.get("Rating").equals("4.5")){
            CurrentRatingFilter = RBRating4_5;
        }else if(FilterMap.get("Rating").equals("4.0")){
            CurrentRatingFilter = RBRating4;
        }else if(FilterMap.get("Rating").equals("3.5")){
            CurrentRatingFilter = RBRating3_5;
        }

        if(FilterMap.get("Availability") == null){
            CurrentAvailabilityFilter = RBAvailabilityInStock;
        }else if(FilterMap.get("Availability").equals("false")){
            CurrentAvailabilityFilter = RBAvailabilityOutOfStock;
        }

        CurrentSelectedFilterLayout = LLSortFilter;
        CurrentSelectedFilter = VSideBarSortFilter;

        CurrentSortFilter.setChecked(true);
        CurrentSortFilter.setTextColor(getResources().getColor(R.color.black));
        CurrentSortFilter.setTypeface(typeMajorantMedium);

        CurrentAvailabilityFilter.setChecked(true);
        CurrentAvailabilityFilter.setTextColor(getResources().getColor(R.color.black));
        CurrentAvailabilityFilter.setTypeface(typeMajorantMedium);

        CurrentRatingFilter.setChecked(true);
        CurrentRatingFilter.setTextColor(getResources().getColor(R.color.black));
        CurrentRatingFilter.setTypeface(typeMajorantMedium);

        DismissFilterDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialog.dismiss();
            }
        });

        RecyclerViewCategoryFilter.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL, false));

        db.collection("Shop").document(ShopState)
                .collection(ShopCity)
                .whereEqualTo("phoneNumber",ShopPhoneNumber)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        categoryFilterList = new ArrayList<>();
                        categoryFilterAdapter = new CategoryFilterAdapter(getContext(), categoryFilterList,communicationCategoryFilter);
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            mapObjectFilter = documentSnapshot.toObject(ShopOnboardClass.class);
                            for(String category : mapObjectFilter.getCategory()){
                                categoryFilterList.add(category);
                                categoryFilterAdapter.notifyDataSetChanged();
                            }
                        }
                        RecyclerViewCategoryFilter.setAdapter(categoryFilterAdapter);
                    }
                });

        LLSelectSortFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentSelectedFilterLayout.setVisibility(View.GONE);
                CurrentSelectedFilter.setVisibility(View.INVISIBLE);

                CurrentSelectedFilterLayout = LLSortFilter;
                CurrentSelectedFilter = VSideBarSortFilter;

                CurrentSelectedFilterLayout.setVisibility(View.VISIBLE);
                CurrentSelectedFilter.setVisibility(View.VISIBLE);

            }
        });

        LLSelectCategoryFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentSelectedFilterLayout.setVisibility(View.GONE);
                CurrentSelectedFilter.setVisibility(View.INVISIBLE);

                CurrentSelectedFilterLayout = LLCategoryFilter;
                CurrentSelectedFilter = VSideBarCategoryFilter;

                CurrentSelectedFilterLayout.setVisibility(View.VISIBLE);
                CurrentSelectedFilter.setVisibility(View.VISIBLE);

            }
        });

        LLSelectRatingFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentSelectedFilterLayout.setVisibility(View.GONE);
                CurrentSelectedFilter.setVisibility(View.INVISIBLE);

                CurrentSelectedFilterLayout = LLRatingFilter;
                CurrentSelectedFilter = VSideBarRatingFilter;

                CurrentSelectedFilterLayout.setVisibility(View.VISIBLE);
                CurrentSelectedFilter.setVisibility(View.VISIBLE);

            }
        });

        LLSelectAvailabilityFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentSelectedFilterLayout.setVisibility(View.GONE);
                CurrentSelectedFilter.setVisibility(View.INVISIBLE);

                CurrentSelectedFilterLayout = LLAvailabilityFilter;
                CurrentSelectedFilter = VSideBarAvailabilityFilter;

                CurrentSelectedFilterLayout.setVisibility(View.VISIBLE);
                CurrentSelectedFilter.setVisibility(View.VISIBLE);

            }
        });

        RBSortRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentSortFilter.setTypeface(typeMajorantRegular);
                CurrentSortFilter.setTextColor(getResources().getColor(R.color.default_gray));
                CurrentSortFilter.setChecked(false);

                CurrentSortFilter = RBSortRating;
                FilterMap.put("Sort","Rating");

                CurrentSortFilter.setTypeface(typeMajorantMedium);
                CurrentSortFilter.setTextColor(getResources().getColor(R.color.black));
                CurrentSortFilter.setChecked(true);

            }
        });

        RBSortCostHTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentSortFilter.setTypeface(typeMajorantRegular);
                CurrentSortFilter.setTextColor(getResources().getColor(R.color.default_gray));
                CurrentSortFilter.setChecked(false);

                CurrentSortFilter = RBSortCostHTL;
                FilterMap.put("Sort","SellingPriceD");

                CurrentSortFilter.setTypeface(typeMajorantMedium);
                CurrentSortFilter.setTextColor(getResources().getColor(R.color.black));
                CurrentSortFilter.setChecked(true);

            }
        });

        RBSortCostLTH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentSortFilter.setTypeface(typeMajorantRegular);
                CurrentSortFilter.setTextColor(getResources().getColor(R.color.default_gray));
                CurrentSortFilter.setChecked(false);

                CurrentSortFilter = RBSortCostLTH;
                FilterMap.put("Sort","SellingPriceA");

                CurrentSortFilter.setTypeface(typeMajorantMedium);
                CurrentSortFilter.setTextColor(getResources().getColor(R.color.black));
                CurrentSortFilter.setChecked(true);
            }
        });

        RBSortDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentSortFilter.setTypeface(typeMajorantRegular);
                CurrentSortFilter.setTextColor(getResources().getColor(R.color.default_gray));
                CurrentSortFilter.setChecked(false);

                CurrentSortFilter = RBSortDefault;
                FilterMap.put("Sort",null);

                CurrentSortFilter.setTypeface(typeMajorantMedium);
                CurrentSortFilter.setTextColor(getResources().getColor(R.color.black));
                CurrentSortFilter.setChecked(true);
            }
        });

        RBRating4_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentRatingFilter.setTypeface(typeMajorantRegular);
                CurrentRatingFilter.setTextColor(getResources().getColor(R.color.default_gray));
                CurrentRatingFilter.setChecked(false);

                CurrentRatingFilter = RBRating4_5;
                FilterMap.put("Rating","4.5");

                CurrentRatingFilter.setTypeface(typeMajorantMedium);
                CurrentRatingFilter.setTextColor(getResources().getColor(R.color.black));
                CurrentRatingFilter.setChecked(true);
            }
        });

        RBRating4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentRatingFilter.setTypeface(typeMajorantRegular);
                CurrentRatingFilter.setTextColor(getResources().getColor(R.color.default_gray));
                CurrentRatingFilter.setChecked(false);

                CurrentRatingFilter = RBRating4;
                FilterMap.put("Rating","4.0");

                CurrentRatingFilter.setTypeface(typeMajorantMedium);
                CurrentRatingFilter.setTextColor(getResources().getColor(R.color.black));
                CurrentRatingFilter.setChecked(true);
            }
        });

        RBRating3_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentRatingFilter.setTypeface(typeMajorantRegular);
                CurrentRatingFilter.setTextColor(getResources().getColor(R.color.default_gray));
                CurrentRatingFilter.setChecked(false);

                CurrentRatingFilter = RBRating3_5;
                FilterMap.put("Rating","3.5");

                CurrentRatingFilter.setTypeface(typeMajorantMedium);
                CurrentRatingFilter.setTextColor(getResources().getColor(R.color.black));
                CurrentRatingFilter.setChecked(true);

            }
        });

        RBRating0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentRatingFilter.setTypeface(typeMajorantRegular);
                CurrentRatingFilter.setTextColor(getResources().getColor(R.color.default_gray));
                CurrentRatingFilter.setChecked(false);

                CurrentRatingFilter = RBRating0;
                FilterMap.put("Rating",null);

                CurrentRatingFilter.setTypeface(typeMajorantMedium);
                CurrentRatingFilter.setTextColor(getResources().getColor(R.color.black));
                CurrentRatingFilter.setChecked(true);
            }
        });

        RBAvailabilityInStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentAvailabilityFilter.setTypeface(typeMajorantRegular);
                CurrentAvailabilityFilter.setTextColor(getResources().getColor(R.color.default_gray));
                CurrentAvailabilityFilter.setChecked(false);

                CurrentAvailabilityFilter = RBAvailabilityInStock;
                FilterMap.put("Availability",null);

                CurrentAvailabilityFilter.setTypeface(typeMajorantMedium);
                CurrentAvailabilityFilter.setTextColor(getResources().getColor(R.color.black));
                CurrentAvailabilityFilter.setChecked(true);

            }
        });

        RBAvailabilityOutOfStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentAvailabilityFilter.setTypeface(typeMajorantRegular);
                CurrentAvailabilityFilter.setTextColor(getResources().getColor(R.color.default_gray));
                CurrentAvailabilityFilter.setChecked(false);

                CurrentAvailabilityFilter = RBAvailabilityOutOfStock;
                FilterMap.put("Availability","false");

                CurrentAvailabilityFilter.setTypeface(typeMajorantMedium);
                CurrentAvailabilityFilter.setTextColor(getResources().getColor(R.color.black));

            }
        });

        BtnApplyFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApplyFilter();
                FilterDialog.dismiss();
            }
        });

        BtnClearFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterMap.put("Sort",null);
                FilterMap.put("Rating",null);
                FilterMap.put("Availability",null);
                FilterMap.put("Category",null);

                productRef.whereEqualTo("InStock","true")
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : value) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });

                FilterDialog.dismiss();
            }
        });

        FilterDialog.show();
        FilterDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        FilterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        FilterDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        FilterDialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    /*
    Applying Filters
    Checking if any Filter is selected if FilterMap does not contain default value
     */
    private void ApplyFilter() {

        Log.d("0000000",FilterMap.toString());
        if(FilterMap.get("Sort") != null && FilterMap.get("Category") != null
                &&  FilterMap.get("Rating") != null && FilterMap.get("Availability") != null){

            noteListener.remove();

            if(FilterMap.get("Sort").equals("SellingPriceD")){
                productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                        .orderBy("Rating")
                        .whereEqualTo("InStock","false")
                        .whereEqualTo("Category",FilterMap.get("Category"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<Long> list = new ArrayList<>();
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Product product = document.toObject(Product.class);
                                    list.add(product.getSellingPrice());

                                }
                                Collections.sort(list,Collections.reverseOrder());
                                int i ;
                                for( i = 0 ; i < list.size(); i++){
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Product product = document.toObject(Product.class);
                                        if(product.getSellingPrice() == list.get(0)){
                                            productsList.add(product);
                                            productsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else if(FilterMap.get("Sort").equals("SellingPriceA")){
                productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                        .orderBy("Rating")
                        .whereEqualTo("InStock","false")
                        .whereEqualTo("Category",FilterMap.get("Category"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<Long> list = new ArrayList<>();
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Product product = document.toObject(Product.class);
                                    list.add(product.getSellingPrice());

                                }
                                Collections.sort(list);
                                int i ;
                                for( i = 0 ; i < list.size(); i++){
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Product product = document.toObject(Product.class);
                                        if(product.getSellingPrice() == list.get(0)){
                                            productsList.add(product);
                                            productsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else{
                productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                        .orderBy("Rating", Query.Direction.DESCENDING)
                        .whereEqualTo("InStock","false")
                        .whereEqualTo("Category",FilterMap.get("Category"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }


        }

        if(FilterMap.get("Sort") == null && FilterMap.get("Category") != null
                &&  FilterMap.get("Rating") != null && FilterMap.get("Availability") != null){
            noteListener.remove();

            productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                    .orderBy("Rating", Query.Direction.DESCENDING)
                    .whereEqualTo("InStock","false")
                    .whereEqualTo("Category",FilterMap.get("Category"))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            productsList = new ArrayList<>();
                            productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                Product product = document.toObject(Product.class);
                                productsList.add(product);
                                productsAdapter.notifyDataSetChanged();

                            }
                            productsRecyclerView.setAdapter(productsAdapter);
                        }
                    });

        }

        if(FilterMap.get("Sort") != null && FilterMap.get("Category") == null
                &&  FilterMap.get("Rating") != null && FilterMap.get("Availability") != null){
            noteListener.remove();

            if(FilterMap.get("Sort").equals("SellingPriceD")){
                productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                        .orderBy("Rating")
                        .whereEqualTo("InStock","false")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<Long> list = new ArrayList<>();
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Product product = document.toObject(Product.class);
                                    list.add(product.getSellingPrice());

                                }
                                Collections.sort(list,Collections.reverseOrder());
                                int i ;
                                for( i = 0 ; i < list.size(); i++){
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Product product = document.toObject(Product.class);
                                        if(product.getSellingPrice() == list.get(i)){
                                            productsList.add(product);
                                            productsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else if(FilterMap.get("Sort").equals("SellingPriceA")){
                productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                        .orderBy("Rating")
                        .whereEqualTo("InStock","false")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<Long> list = new ArrayList<>();
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Product product = document.toObject(Product.class);
                                    list.add(product.getSellingPrice());

                                }
                                Collections.sort(list);
                                int i ;
                                for( i = 0 ; i < list.size(); i++){
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Product product = document.toObject(Product.class);
                                        if(product.getSellingPrice() == list.get(i)){
                                            productsList.add(product);
                                            productsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else{
                productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                        .orderBy("Rating", Query.Direction.DESCENDING)
                        .whereEqualTo("InStock","false")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }

        }

        if(FilterMap.get("Sort") != null && FilterMap.get("Category") != null
                &&  FilterMap.get("Rating") == null && FilterMap.get("Availability") != null){

            noteListener.remove();
            if(FilterMap.get("Sort").equals("SellingPriceD")){
                productRef.orderBy("SellingPrice", Query.Direction.DESCENDING)
                        .whereEqualTo("InStock","false")
                        .whereEqualTo("Category",FilterMap.get("Category"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else if(FilterMap.get("Sort").equals("SellingPriceA")){
                productRef.orderBy("SellingPrice", Query.Direction.ASCENDING)
                        .whereEqualTo("InStock","false")
                        .whereEqualTo("Category",FilterMap.get("Category"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else{
                productRef.orderBy("Rating", Query.Direction.DESCENDING)
                        .whereEqualTo("InStock","false")
                        .whereEqualTo("Category",FilterMap.get("Category"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }
        }

        if(FilterMap.get("Sort") != null && FilterMap.get("Category") != null
                &&  FilterMap.get("Rating") != null && FilterMap.get("Availability") == null){

            noteListener.remove();
            if(FilterMap.get("Sort").equals("SellingPriceD")){
                productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                        .orderBy("Rating")
                        .whereEqualTo("InStock","true")
                        .whereEqualTo("Category",FilterMap.get("Category"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<Long> list = new ArrayList<>();
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Product product = document.toObject(Product.class);
                                    list.add(product.getSellingPrice());

                                }
                                Collections.sort(list,Collections.reverseOrder());
                                int i ;
                                for( i = 0 ; i < list.size(); i++){
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Product product = document.toObject(Product.class);
                                        if(product.getSellingPrice() == list.get(i)){
                                            productsList.add(product);
                                            productsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else if(FilterMap.get("Sort").equals("SellingPriceA")){
                productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                        .orderBy("Rating")
                        .whereEqualTo("InStock","true")
                        .whereEqualTo("Category",FilterMap.get("Category"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<Long> list = new ArrayList<>();
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Product product = document.toObject(Product.class);
                                    list.add(product.getSellingPrice());

                                }
                                Collections.sort(list);
                                int i ;
                                for( i = 0 ; i < list.size(); i++){
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Product product = document.toObject(Product.class);
                                        if(product.getSellingPrice() == list.get(i)){
                                            productsList.add(product);
                                            productsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else{
                productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                        .orderBy("Rating", Query.Direction.DESCENDING)
                        .whereEqualTo("InStock","true")
                        .whereEqualTo("Category",FilterMap.get("Category"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }
        }

        if(FilterMap.get("Sort") == null && FilterMap.get("Category") == null
                &&  FilterMap.get("Rating") != null && FilterMap.get("Availability") != null){

            noteListener.remove();
            productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                    .orderBy("Rating", Query.Direction.DESCENDING)
                    .whereEqualTo("InStock","true")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            productsList = new ArrayList<>();
                            productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                Product product = document.toObject(Product.class);
                                productsList.add(product);
                                productsAdapter.notifyDataSetChanged();

                            }
                            productsRecyclerView.setAdapter(productsAdapter);
                        }
                    });
        }

        if(FilterMap.get("Sort") == null && FilterMap.get("Category") != null
                &&  FilterMap.get("Rating") == null && FilterMap.get("Availability") != null){

            noteListener.remove();
            productRef.whereEqualTo("InStock","true")
                    .whereEqualTo("Category",FilterMap.get("Category"))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            productsList = new ArrayList<>();
                            productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                Product product = document.toObject(Product.class);
                                productsList.add(product);
                                productsAdapter.notifyDataSetChanged();

                            }
                            productsRecyclerView.setAdapter(productsAdapter);
                        }
                    });
        }

        if(FilterMap.get("Sort") == null && FilterMap.get("Category") != null
                &&  FilterMap.get("Rating") != null && FilterMap.get("Availability") == null){

            noteListener.remove();
            productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                    .orderBy("Rating", Query.Direction.DESCENDING)
                    .whereEqualTo("InStock","true")
                    .whereEqualTo("Category",FilterMap.get("Category"))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            productsList = new ArrayList<>();
                            productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                Product product = document.toObject(Product.class);
                                productsList.add(product);
                                productsAdapter.notifyDataSetChanged();

                            }
                            productsRecyclerView.setAdapter(productsAdapter);
                        }
                    });
        }

        if(FilterMap.get("Sort") != null && FilterMap.get("Category") == null
                &&  FilterMap.get("Rating") == null && FilterMap.get("Availability") != null){

            noteListener.remove();

            if(FilterMap.get("Sort").equals("SellingPriceD")){
                productRef.orderBy("SellingPrice", Query.Direction.DESCENDING)
                        .whereEqualTo("InStock","false")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else if(FilterMap.get("Sort").equals("SellingPriceA")){
                productRef.orderBy("SellingPrice", Query.Direction.ASCENDING)
                        .whereEqualTo("InStock","false")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else{
                productRef.orderBy("Rating", Query.Direction.DESCENDING)
                        .whereEqualTo("InStock","false")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }
        }

        if(FilterMap.get("Sort") != null && FilterMap.get("Category") == null
                &&  FilterMap.get("Rating") != null && FilterMap.get("Availability") == null){

            noteListener.remove();
            if(FilterMap.get("Sort").equals("SellingPriceD")){
                productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                        .orderBy("Rating")
                        .whereEqualTo("InStock","true")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<Long> list = new ArrayList<>();
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Product product = document.toObject(Product.class);
                                    list.add(product.getSellingPrice());

                                }
                                Collections.sort(list,Collections.reverseOrder());
                                int i ;
                                for( i = 0 ; i < list.size(); i++){
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Product product = document.toObject(Product.class);
                                        if(product.getSellingPrice() == list.get(i)){
                                            productsList.add(product);
                                            productsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else if(FilterMap.get("Sort").equals("SellingPriceA")){
                productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                        .orderBy("Rating")
                        .whereEqualTo("InStock","true")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                ArrayList<Long> list = new ArrayList<>();
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Product product = document.toObject(Product.class);
                                    list.add(product.getSellingPrice());

                                }
                                Collections.sort(list);
                                int i ;
                                for( i = 0 ; i < list.size(); i++){
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Product product = document.toObject(Product.class);
                                        if(product.getSellingPrice() == list.get(i)){
                                            productsList.add(product);
                                            productsAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else{
                productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                        .orderBy("Rating", Query.Direction.DESCENDING)
                        .whereEqualTo("InStock","true")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }
        }

        if(FilterMap.get("Sort") != null && FilterMap.get("Category") != null
                &&  FilterMap.get("Rating") == null && FilterMap.get("Availability") == null){

            noteListener.remove();
            if(FilterMap.get("Sort").equals("SellingPriceD")){
                productRef.orderBy("SellingPrice", Query.Direction.DESCENDING)
                        .whereEqualTo("InStock","true")
                        .whereEqualTo("Category",FilterMap.get("Category"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else if(FilterMap.get("Sort").equals("SellingPriceA")){
                productRef.orderBy("SellingPrice", Query.Direction.ASCENDING)
                        .whereEqualTo("InStock","true")
                        .whereEqualTo("Category",FilterMap.get("Category"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else{
                productRef.orderBy("Rating", Query.Direction.DESCENDING)
                        .whereEqualTo("InStock","true")
                        .whereEqualTo("Category",FilterMap.get("Category"))
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }
        }

        if(FilterMap.get("Sort") != null && FilterMap.get("Category") == null
                &&  FilterMap.get("Rating") == null && FilterMap.get("Availability") == null){

            noteListener.remove();
            if(FilterMap.get("Sort").equals("SellingPriceD")){
                productRef.orderBy("SellingPrice", Query.Direction.DESCENDING)
                        .whereEqualTo("InStock","true")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else if(FilterMap.get("Sort").equals("SellingPriceA")){
                productRef.orderBy("SellingPrice", Query.Direction.ASCENDING)
                        .whereEqualTo("InStock","true")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }else{
                productRef.orderBy("Rating", Query.Direction.DESCENDING)
                        .whereEqualTo("InStock","true")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                productsList = new ArrayList<>();
                                productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                    Product product = document.toObject(Product.class);
                                    productsList.add(product);
                                    productsAdapter.notifyDataSetChanged();

                                }
                                productsRecyclerView.setAdapter(productsAdapter);
                            }
                        });
            }
        }

        if(FilterMap.get("Sort") == null && FilterMap.get("Category") != null
                &&  FilterMap.get("Rating") == null && FilterMap.get("Availability") == null){

            noteListener.remove();
            productRef.whereEqualTo("InStock","true")
                    .whereEqualTo("Category",FilterMap.get("Category"))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            productsList = new ArrayList<>();
                            productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                Product product = document.toObject(Product.class);
                                productsList.add(product);
                                productsAdapter.notifyDataSetChanged();

                            }
                            productsRecyclerView.setAdapter(productsAdapter);
                        }
                    });
        }

        if(FilterMap.get("Sort") == null && FilterMap.get("Category") == null
                &&  FilterMap.get("Rating") != null && FilterMap.get("Availability") == null){

            noteListener.remove();
            productRef.whereGreaterThanOrEqualTo("Rating",FilterMap.get("Rating"))
                    .orderBy("Rating", Query.Direction.DESCENDING)
                    .whereEqualTo("InStock","true")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            productsList = new ArrayList<>();
                            productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                Product product = document.toObject(Product.class);
                                productsList.add(product);
                                productsAdapter.notifyDataSetChanged();

                            }
                            productsRecyclerView.setAdapter(productsAdapter);
                        }
                    });
        }

        if(FilterMap.get("Sort") == null && FilterMap.get("Category") == null
                &&  FilterMap.get("Rating") == null && FilterMap.get("Availability") != null){

            noteListener.remove();
            productRef.whereEqualTo("InStock","false")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            productsList = new ArrayList<>();
                            productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                Product product = document.toObject(Product.class);
                                productsList.add(product);
                                productsAdapter.notifyDataSetChanged();

                            }
                            productsRecyclerView.setAdapter(productsAdapter);
                        }
                    });
        }

        if(FilterMap.get("Sort") == null && FilterMap.get("Category") == null
                &&  FilterMap.get("Rating") == null && FilterMap.get("Availability") == null){
            noteListener = productRef.whereEqualTo("InStock","true")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            productsList = new ArrayList<>();
                            productsAdapter = new ProductsAdapter(getContext(), productsList, productRef,communication);
                            for (QueryDocumentSnapshot document : value) {
                                Log.d("0000000",document.get("SellingPrice").getClass().toString());
                                Product product = document.toObject(Product.class);
                                productsList.add(product);
                                productsAdapter.notifyDataSetChanged();

                            }
                            productsRecyclerView.setAdapter(productsAdapter);
                        }
                    });
        }

    }


    private void showDialog(Product product) {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.fargment_dashboard_home_product_edit_dialog);

        ImageView ImageCloseDialog = dialog.findViewById(R.id.EditDialogCloseBtn);

        ETProductNameLayout = dialog.findViewById(R.id.EditText_productName_Layout_editProduct_dialog);
        ETProductDescriptionLayout = dialog.findViewById(R.id.EditText_productDescription_Layout_editProduct_dialog);
        ETProductBrandLayout = dialog.findViewById(R.id.EditText_productBrand_Layout_editProduct_dialog);
        ETProductMRPLayout = dialog.findViewById(R.id.EditText_productMRP_Layout_editProduct_dialog);
        ETProductSPLayout = dialog.findViewById(R.id.EditText_productSP_Layout_editProduct_dialog);
        ACTSelectCategoryLayout = dialog.findViewById(R.id.EditText_Select_Category_Layout_editProduct_dialog);

        ETProductName = dialog.findViewById(R.id.EditText_productName_editProduct_dialog);
        ETProductDescription = dialog.findViewById(R.id.EditText_productDescription_editProduct_dialog);
        ETProductBrand = dialog.findViewById(R.id.EditText_productBrand_editProduct_dialog);
        ETProductMRP = dialog.findViewById(R.id.EditText_productMRP_editProduct_dialog);
        ETProductSP = dialog.findViewById(R.id.EditText_productSP_editProduct_dialog);

        CardView CVSelectProductImage = dialog.findViewById(R.id.CardView_SelectProductImage_editProduct_dialog);
        IVProductImage = dialog.findViewById(R.id.ImageView_ProductImage_editProduct_dialog);
        IVNewImage = dialog.findViewById(R.id.ImageView_NewImage_editProduct_dialog);

        ACTSelectCategory = dialog.findViewById(R.id.EditText_Select_Category_editProduct_dialog);

        bottomBarCategory = dialog.findViewById(R.id.bottom_bar_category_editProduct_dialog);
        bottomBarProductImage = dialog.findViewById(R.id.bottom_bar_product_image_editProduct_dialog);
        bottomBarGeneral = dialog.findViewById(R.id.bottom_bar_general_editProduct_dialog);

        Button BtnEditProduct = dialog.findViewById(R.id.Btn_editProduct_dialog);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar_editingProduct_dialog);

        ETProductName.setText(product.getName());
        ETProductDescription.setText(product.getDescription());
        ETProductBrand.setText(product.getBrand());
        ETProductMRP.setText(String.valueOf(product.getMRP()));
        ETProductSP.setText(String.valueOf(product.getSellingPrice()));
        ACTSelectCategory.setText(product.getCategory());
        ImageUrl = product.getImageUrl();

        Picasso.get().load(product.getImageUrl()).into(IVProductImage);

        storage = FirebaseStorage.getInstance();

        resetError();

        ImageCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        db.collection("Shop").document(ShopState)
                .collection(ShopCity)
                .whereEqualTo("phoneNumber",ShopPhoneNumber)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            mapObject = documentSnapshot.toObject(ShopOnboardClass.class);

                            selectCategoryAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_layout, mapObject.getCategory());
                            selectCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            ACTSelectCategory.setAdapter(selectCategoryAdapter);
                        }
                    }
                });

        CVSelectProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        BtnEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !ACTSelectCategory.getText().toString().trim().isEmpty()
                        && !ETProductName.getText().toString().trim().isEmpty()
                        && !ETProductMRP.getText().toString().trim().isEmpty()
                        && !ETProductSP.getText().toString().trim().isEmpty()
                        && !(uri == null && ImageUrl == null)){

                    progressBar.setVisibility(View.VISIBLE);
                    BtnEditProduct.setVisibility(View.INVISIBLE);

                    Map<String, Object> values = new HashMap<>();
                    values.put("Category",ACTSelectCategory.getText().toString().trim());
                    values.put("Name",ETProductName.getText().toString().trim());
                    values.put("Description",ETProductDescription.getText().toString().trim());
                    values.put("Rating","0.0");
                    values.put("InStock", "true");
                    values.put("Brand",ETProductBrand.getText().toString().trim());
                    values.put("MRP",Integer.parseInt(ETProductMRP.getText().toString().trim()));
                    values.put("SellingPrice",Integer.parseInt(ETProductSP.getText().toString().trim()));
                    values.put("ShopId",ShopId);

                    if(uri == null){
                        values.put("ImageUrl", ImageUrl);
                        DocumentReference ref = db.collection("Shop").document(ShopState)
                                .collection(ShopCity).document(ShopId).collection("Products").document(product.getId());

                        ref.update(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressBar.setVisibility(View.GONE);
                                BtnEditProduct.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(), "Product Edited", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }else{
                        final StorageReference reference = storage.getReference()
                                .child(ShopPhoneNumber).child(ETProductName.getText().toString().trim());

                        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        values.put("ImageUrl", uri.toString());
                                        DocumentReference ref = db.collection("Shop").document(ShopState)
                                                .collection(ShopCity).document(ShopId).collection("Products").document(product.getId());

                                        ref.update(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressBar.setVisibility(View.GONE);
                                                BtnEditProduct.setVisibility(View.VISIBLE);
                                                Toast.makeText(getActivity(), "Product Edited", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }else{
                    if(ACTSelectCategory.getText().toString().trim().isEmpty()){
                        bottomBarCategory.setBackgroundResource(R.drawable.error_bar);
                        changeStateTextInputLayout(ACTSelectCategoryLayout,"Red");
                    }
                    if(uri == null){
                        bottomBarProductImage.setBackgroundResource(R.drawable.error_bar);
                    }
                    if(ETProductName.getText().toString().trim().isEmpty()){
                        bottomBarGeneral.setBackgroundResource(R.drawable.error_bar);
                        changeStateTextInputLayout(ETProductNameLayout,"Red");
                    }
                    if(ETProductMRP.getText().toString().trim().isEmpty()){
                        bottomBarGeneral.setBackgroundResource(R.drawable.error_bar);
                        changeStateTextInputLayout(ETProductMRPLayout,"Red");
                    }
                    if(ETProductSP.getText().toString().trim().isEmpty()){
                        bottomBarGeneral.setBackgroundResource(R.drawable.error_bar);
                        changeStateTextInputLayout(ETProductSPLayout,"Red");
                    }

                }
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    /*
Method to reset error backgrounds on text changed.
Setting background to normal editText and hint color to blue.
*/
    public void resetError(){
        ETProductName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bottomBarGeneral.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                changeStateTextInputLayout(ETProductNameLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETProductMRP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bottomBarGeneral.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                changeStateTextInputLayout(ETProductMRPLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETProductSP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bottomBarGeneral.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                changeStateTextInputLayout(ETProductSPLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ACTSelectCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bottomBarCategory.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                changeStateTextInputLayout(ACTSelectCategoryLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void changeStateTextInputLayout(TextInputLayout textInputLayout, String color){
        if(color.equals("Blue")){
            textInputLayout.setBackgroundResource(R.drawable.edit_text_border);
            textInputLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
        }else if(color.equals("Red")){
            textInputLayout.setBackgroundResource(R.drawable.edit_text_error_border);
            textInputLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        }
    }

    FragmentCommunicationProductEdit communication = new FragmentCommunicationProductEdit() {
        @Override
        public void respond(int position, Product product) {
            showDialog(product);
        }
    };



    FragmentCommunicationCategoryFilter communicationCategoryFilter = new FragmentCommunicationCategoryFilter() {
        @Override
        public void AddElement(int position, String category) {
            RecyclerViewCategoryFilter.post(new Runnable() {
                @Override public void run()
                {
                    categoryFilterAdapter.notifyDataSetChanged();
                }
            });
            categoryListToBeFiltered = category;
            Log.d("000000", categoryListToBeFiltered);
        }

    };
}
