package com.example.faktoeshop.signUpSignIn;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.faktoeshop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fragment_SignUp_5 extends Fragment {
    // Initialising Views

    TextView TVSignUpHeading5;
    EditText ETAccountHolderName, ETAccountType, ETAccountNumber, ETIFSCCode;
    TextInputLayout ETAccountHolderNameLayout, ETAccountTypeLayout, ETAccountNumberLayout, ETIFSCCodeLayout;
    Button ContinueBtn;
    ProgressBar progressBar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Initializing shopOnboardClass to store data in viewModel.
    ShopOnboardClass shopData;

    //Initializing ViewModel
    ShopOnboardViewModel viewModel;

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sign_up_5, container, false);

        // Assigning variables to their respective views

        TVSignUpHeading5 = v.findViewById(R.id.fragment_5_heading);

        // Coloring a part of the Heading as BLUE using SpannableString

        String txt = "Bank Account Details";
        SpannableString ss = new SpannableString(txt);

        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(getResources().getColor(R.color.blue));
        ss.setSpan(fcsBlue, 0, 4, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        TVSignUpHeading5.setText(ss);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ETAccountHolderName = v.findViewById(R.id.EditText_AccountHolderName);
        ETAccountType = v.findViewById(R.id.EditText_AccountType);
        ETAccountNumber = v.findViewById(R.id.EditText_AccountNumber);
        ETIFSCCode = v.findViewById(R.id.EditText_IFSCCode);

        ETAccountHolderNameLayout = v.findViewById(R.id.EditText_AccountHolderName_Layout);
        ETAccountTypeLayout = v.findViewById(R.id.EditText_AccountType_Layout);
        ETAccountNumberLayout = v.findViewById(R.id.EditText_AccountNumber_Layout);
        ETIFSCCodeLayout = v.findViewById(R.id.EditText_IFSCCode_Layout);

        ContinueBtn = v.findViewById(R.id.ContinueBtn_SignUp_5);
        progressBar = v.findViewById(R.id.progressBar_uploading_data);


        //Resetting error background on text changed in the editText.
        resetError();

        // Assigning viewModel to ShopOnBoardViewModel.
        viewModel = new ViewModelProvider(requireActivity()).get(ShopOnboardViewModel.class);

        //Observing shop data and setting data in EditTexts respectively.
        viewModel.getShopData().observe(getViewLifecycleOwner(), new Observer<ShopOnboardClass>() {
            @Override
            public void onChanged(ShopOnboardClass shopOnboardClass) {
                //Cloning ShopOnboardClass object into new object to add and update data in ViewModel.
                shopData = new ShopOnboardClass(shopOnboardClass);

                //Setting Data in EditTexts.
                ETAccountHolderName.setText(shopData.getAccountHolderName());
                ETAccountType.setText(shopData.getAccountType());
                ETAccountNumber.setText(shopData.getAccountNumber());
                ETIFSCCode.setText(shopData.getIFSC_Code());
            }
        });

        /*
        Checking if any EditText is empty.
        Checking if Phone number entered is in correct format.
        Updating data in ShopData in ViewModel and changing the fragment.
         */
        ContinueBtn.setOnClickListener(new View.OnClickListener() {@Override
            public void onClick(View view) {
                if(!ETAccountHolderName.getText().toString().trim().isEmpty() && !ETAccountType.getText().toString().trim().isEmpty() &&
                        !ETAccountNumber.getText().toString().trim().isEmpty() && !ETIFSCCode.getText().toString().trim().isEmpty()
                        && ETIFSCCode.getText().toString().trim().length() == 11 && ETAccountNumber.getText().toString().trim().length() >= 9
                        && ETAccountNumber.getText().toString().trim().length() <= 18){
                    String regexIFSC = "^[A-Z]{4}0[A-Z0-9]{6}$";

                    Pattern pIFSC = Pattern.compile(regexIFSC);
                    Matcher mIFSC = pIFSC.matcher(ETIFSCCode.getText().toString().trim());
                    if(mIFSC.matches()){
                        progressBar.setVisibility(View.VISIBLE);
                        ContinueBtn.setVisibility(View.INVISIBLE);
                        //Updating data in ShopOnboardClass and then pushing it to ViewModel.
                        shopData.setAccountHolderName(ETAccountHolderName.getText().toString());
                        shopData.setAccountType(ETAccountType.getText().toString());
                        shopData.setAccountNumber(ETAccountNumber.getText().toString());
                        shopData.setIFSC_Code(ETIFSCCode.getText().toString());
                        viewModel.addShopData(shopData);


                        DocumentReference ref = db.collection("Shop").document(shopData.getState().toUpperCase()).collection(shopData.getCity()).document();
                        String id = ref.getId();
                                ref.set(shopData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Map<String, String> values = new HashMap<>();
                                        values.put("isVerified","false");
                                        values.put("phoneNo",shopData.getPhoneNumber());
                                        values.put("State",shopData.getState());
                                        values.put("District",shopData.getCity());
                                        values.put("id",id);

                                        db.collection("RegisteredShops").add(values).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(getActivity(),"Data Uploaded",Toast.LENGTH_SHORT).show();
                                                ArrayList<String> EmptyList = new ArrayList<>();
                                                ShopOnboardClass EmptyShop = new ShopOnboardClass("","","","","","","", "","","","","","","","","","","","","","",EmptyList,EmptyList);
                                                viewModel.addShopData(EmptyShop);
                                                progressBar.setVisibility(View.GONE);
                                                ContinueBtn.setVisibility(View.VISIBLE);
                                                viewModel.setPositionSignUp(6);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                ContinueBtn.setVisibility(View.VISIBLE);
                                                Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                ContinueBtn.setVisibility(View.VISIBLE);
                                Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });


                    }else{
                        //Setting background border and hint color to red.
                        changeStateTextInputLayout(ETIFSCCodeLayout,"Red");
                        Toast.makeText(getActivity(),"IFSC format doesn't match",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    /*
                    If any EditText is empty setting error background of that EditText.
                     */

                    // If Name EditText is Empty.
                    if(ETAccountHolderName.getText().toString().trim().isEmpty() && !(ETAccountNumber.getText().toString().trim().length() >= 9
                            && ETAccountNumber.getText().toString().trim().length() <= 18)){

                        //Setting background border and hint color to red.
                        changeStateTextInputLayout(ETAccountHolderNameLayout,"Red");
                    }

                    //If Phone Number EditText is empty.
                    if(ETAccountType.getText().toString().trim().isEmpty()){

                        //Setting background border and hint color to red.
                        changeStateTextInputLayout(ETAccountTypeLayout,"Red");
                    }

                    //If Email EditText is empty.
                    if(ETAccountNumber.getText().toString().trim().isEmpty()){

                        //Setting background border and hint color to red.
                        changeStateTextInputLayout(ETAccountNumberLayout,"Red");
                    }

                    //If Business name EditText is empty.
                    if(ETIFSCCode.getText().toString().trim().isEmpty() && ETIFSCCode.getText().toString().trim().length() != 11){

                        //Setting background border and hint color to red.
                        changeStateTextInputLayout(ETIFSCCodeLayout,"Red");
                    }

                    //Toast to alert user to fill all fields.
                    Toast.makeText(getActivity(),"Please Enter all Fields",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
    Method to reset error backgrounds on text changed.
    Setting background to normal editText and hint color to blue.
     */
    public void resetError(){
        ETAccountHolderName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(ETAccountHolderNameLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETAccountType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(ETAccountTypeLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETAccountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(ETAccountNumberLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETIFSCCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(ETIFSCCodeLayout,"Blue");
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
