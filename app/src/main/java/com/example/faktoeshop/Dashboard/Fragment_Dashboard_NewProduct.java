package com.example.faktoeshop.Dashboard;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.faktoeshop.R;
import com.example.faktoeshop.signUpSignIn.ShopOnboardClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment_Dashboard_NewProduct extends Fragment {
    View v;

    TextInputLayout ETProductNameLayout, ETProductDescriptionLayout, ETProductBrandLayout, ETProductMRPLayout, ETProductSPLayout, ACTSelectCategoryLayout;
    EditText ETProductName, ETProductBrand, ETProductMRP, ETProductSP, ETProductDescription;
    AutoCompleteTextView ACTSelectCategory;
    LinearLayout LLImageSelect;
    Button BtnAddProduct;
    View bottomBarCategory, bottomBarProductImage, bottomBarGeneral;
    ProgressBar progressBar;
    ArrayAdapter<String> selectCategoryAdapter;

    CardView CVSelectProductImage;
    ImageView IVProductImage, IVNewImage;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private SharedPreferences mPreferences;

    private String ShopState, ShopCity, ShopId, ShopPhoneNumber;
    private ShopOnboardClass mapObject;
    private Uri uri;

    private FirebaseStorage storage;

    ActivityResultLauncher<String> launcher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_dashboard_new_product, container, false);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ShopState = mPreferences.getString(getString(R.string.ShopState),"").toUpperCase();
        ShopCity = mPreferences.getString(getString(R.string.ShopDistrict),"");
        ShopPhoneNumber = mPreferences.getString(getString(R.string.ShopPhoneNumber),"");
        ShopId = mPreferences.getString(getString(R.string.ShopId),"");

        ETProductNameLayout = v.findViewById(R.id.EditText_productName_Layout);
        ETProductDescriptionLayout = v.findViewById(R.id.EditText_productDescription_Layout);
        ETProductBrandLayout = v.findViewById(R.id.EditText_productBrand_Layout);
        ETProductMRPLayout = v.findViewById(R.id.EditText_productMRP_Layout);
        ETProductSPLayout = v.findViewById(R.id.EditText_productSP_Layout);
        ACTSelectCategoryLayout = v.findViewById(R.id.EditText_Select_Category_Layout);

        ETProductName = v.findViewById(R.id.EditText_productName);
        ETProductDescription = v.findViewById(R.id.EditText_productDescription);
        ETProductBrand = v.findViewById(R.id.EditText_productBrand);
        ETProductMRP = v.findViewById(R.id.EditText_productMRP);
        ETProductSP = v.findViewById(R.id.EditText_productSP);

        CVSelectProductImage = v.findViewById(R.id.CardView_SelectProductImage);
        IVProductImage = v.findViewById(R.id.ImageView_ProductImage);
        IVNewImage = v.findViewById(R.id.ImageView_NewImage);

        ACTSelectCategory = v.findViewById(R.id.EditText_Select_Category);

        bottomBarCategory = v.findViewById(R.id.bottom_bar_category_newProduct);
        bottomBarProductImage = v.findViewById(R.id.bottom_bar_product_image);
        bottomBarGeneral = v.findViewById(R.id.bottom_bar_general_newProduct);

        BtnAddProduct = v.findViewById(R.id.Btn_addProduct);
        progressBar = v.findViewById(R.id.progressBar_adding_newProduct);

        storage = FirebaseStorage.getInstance();

        resetError();

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

        CVSelectProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        BtnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !ACTSelectCategory.getText().toString().trim().isEmpty()
                        && !ETProductName.getText().toString().trim().isEmpty()
                        && !ETProductMRP.getText().toString().trim().isEmpty()
                        && !ETProductSP.getText().toString().trim().isEmpty()
                        && !(uri == null)){

                    progressBar.setVisibility(View.VISIBLE);
                    BtnAddProduct.setVisibility(View.INVISIBLE);

                    Map<String, String> values = new HashMap<>();
                    values.put("Category",ACTSelectCategory.getText().toString().trim());
                    values.put("Name",ETProductName.getText().toString().trim());
                    values.put("Description",ETProductDescription.getText().toString().trim());
                    values.put("Rating","0.0");
                    values.put("InStock", "true");
                    values.put("Brand",ETProductBrand.getText().toString().trim());
                    values.put("MRP",ETProductMRP.getText().toString().trim());
                    values.put("SellingPrice",ETProductSP.getText().toString().trim());

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
                                            .collection(ShopCity).document(ShopId).collection("Products").document();

                                    String id = ref.getId();
                                    values.put("Id",id);

                                    ref.set(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressBar.setVisibility(View.GONE);
                                            BtnAddProduct.setVisibility(View.VISIBLE);
                                            Toast.makeText(getActivity(), "New Product Added", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });

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
        return v;
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
}
