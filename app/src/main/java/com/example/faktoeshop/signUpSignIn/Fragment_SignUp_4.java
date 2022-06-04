package com.example.faktoeshop.signUpSignIn;

import android.content.res.ColorStateList;
import android.net.Uri;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.faktoeshop.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fragment_SignUp_4 extends Fragment {

    // Initialising Views

    TextView TVSignUpHeading4;
    CheckBox CBHaveGSTNumber;
    EditText ETGSTNumber, ETPANumber;
    TextInputLayout ETGSTNumberLayout, ETPANumberLayout;
    Button BtnChooseImage, BtnUploadImage, ContinueBtn;
    View bottomBarSignatureImage;
    ImageView IVSignatureImage;
    ProgressBar PBUploadingImage;

    private Uri uri;
    private String ImageUrl;

    FirebaseStorage storage;
    ActivityResultLauncher<String> launcher;

    //Initializing shopOnboardClass to store data in viewModel.
    ShopOnboardClass shopData;

    //Initializing ViewModel
    ShopOnboardViewModel viewModel;

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sign_up_4,container,false);

        // Assigning variables to their respective views

        TVSignUpHeading4 = v.findViewById(R.id.fragment_4_heading);

        // Coloring a part of the Heading as BLUE using SpannableString

        String txt = "Legal Information";
        SpannableString ss = new SpannableString(txt);

        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(getResources().getColor(R.color.blue));
        ss.setSpan(fcsBlue, 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        TVSignUpHeading4.setText(ss);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CBHaveGSTNumber = v.findViewById(R.id.CBHaveGSTNumber);
        ETGSTNumber = v.findViewById(R.id.EditText_GSTNumber);
        ETPANumber = v.findViewById(R.id.EditText_PANNumber);
        ETGSTNumberLayout = v.findViewById(R.id.EditText_GSTNumber_Layout );
        ETPANumberLayout = v.findViewById(R.id.EditText_PANNumber_Layout );
        BtnChooseImage = v.findViewById(R.id.choose_image_btn);
        BtnUploadImage = v.findViewById(R.id.upload_btn);
        bottomBarSignatureImage = v.findViewById(R.id.bottom_bar_signature_image);
        IVSignatureImage = v.findViewById(R.id.signature_image);
        ContinueBtn = v.findViewById(R.id.ContinueBtn_SignUp_4);

        PBUploadingImage = v.findViewById(R.id.progressBar_uploading_image);

        resetError();

        storage = FirebaseStorage.getInstance();

        // Assigning viewModel to ShopOnBoardViewModel.
        viewModel = new ViewModelProvider(requireActivity()).get(ShopOnboardViewModel.class);

        //Observing shop data and setting data in EditTexts respectively.
        viewModel.getShopData().observe(getViewLifecycleOwner(), new Observer<ShopOnboardClass>() {
            @Override
            public void onChanged(ShopOnboardClass shopOnboardClass) {
                //Cloning ShopOnboardClass object into new object to add and update data in ViewModel.
                shopData = new ShopOnboardClass(shopOnboardClass);

                if(shopData.getAnnualTurnover().equals("Between 20 Lakh to 1 Crore") || shopData.getAnnualTurnover().equals("More than 1 Core")){
                    CBHaveGSTNumber.setChecked(true);
                    CBHaveGSTNumber.setEnabled(false);
                }

                ETGSTNumber.setText(shopData.getGST_Number());
                ETPANumber.setText(shopData.getPAN_Number());
                ImageUrl = shopData.getSignatureImage();

                if(!ImageUrl.equals("")){
                    Picasso.get().load(ImageUrl).into(IVSignatureImage);
                }

            }
        });


        launcher = registerForActivityResult(new ActivityResultContracts.GetContent()
                , new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        IVSignatureImage.setImageURI(result);
                        uri = result;
                        BtnUploadImage.setBackgroundResource(R.drawable.shop_onboard_signin_background);
                        BtnUploadImage.setTextColor(getResources().getColor(R.color.blue));
                        BtnUploadImage.setEnabled(true);
                    }
                });

        CBHaveGSTNumber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    ETGSTNumber.setEnabled(true);
                }
                else{
                    ETGSTNumber.setEnabled(false);
                    ETGSTNumber.setText("");
                }
            }
        });

        BtnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnUploadImage.setVisibility(View.INVISIBLE);
                PBUploadingImage.setVisibility(View.VISIBLE);
                if(uri != null){
                    final StorageReference reference = storage.getReference()
                            .child(shopData.getPhoneNumber()).child("Signature");

                    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    BtnUploadImage.setVisibility(View.VISIBLE);
                                    PBUploadingImage.setVisibility(View.GONE);
                                    ImageUrl = uri.toString();
                                    bottomBarSignatureImage.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                                    Toast.makeText(getActivity(), "Image Uploaded Successful", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        BtnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch("image/*");
            }
        });

        ContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CBHaveGSTNumber.isChecked()){
                    if(!ETGSTNumber.getText().toString().trim().isEmpty() && !ETPANumber.getText().toString().trim().isEmpty() && ETGSTNumber.getText().toString().trim().length() == 15 && ETPANumber.getText().toString().trim().length() == 10 && !ImageUrl.equals("")){
                        String regexPAN = "[A-Z]{5}[0-9]{4}[A-Z]";
                        String regexGST = "^[0-9]{2}" + ETPANumber.getText().toString().trim()
                                + "[1-9A-Z]"
                                + "Z[0-9A-Z]$";

                        Pattern pPAN = Pattern.compile(regexPAN);
                        Pattern pGST = Pattern.compile(regexGST);
                        Matcher mPAN = pPAN.matcher(ETPANumber.getText().toString().trim());
                        Matcher mGST = pGST.matcher(ETGSTNumber.getText().toString().trim());
                        if(mPAN.matches() && mGST.matches()){
                            shopData.setGST_Number(ETGSTNumber.getText().toString().trim());
                            shopData.setPAN_Number(ETPANumber.getText().toString().trim());
                            shopData.setSignatureImage(ImageUrl);

                            viewModel.addShopData(shopData);
                            viewModel.setPositionSignUp(5);
                        }else{
                            changeStateTextInputLayout(ETPANumberLayout,"Red");
                            changeStateTextInputLayout(ETGSTNumberLayout,"Red");
                            Toast.makeText(getActivity(), "PAN Number and GST Number does not Match" , Toast.LENGTH_SHORT).show();

                        }
                    }else{
                        if(ImageUrl.equals("")){
                            bottomBarSignatureImage.setBackgroundResource(R.drawable.error_bar);
                        }

                        if(ETPANumber.getText().toString().trim().isEmpty() || ETPANumber.getText().toString().trim().length() != 10){

                            //Setting background border and hint color to red.
                            changeStateTextInputLayout(ETPANumberLayout,"Red");
                        }
                        if(ETGSTNumber.getText().toString().trim().isEmpty() || ETGSTNumber.getText().toString().trim().length() != 15){

                            //Setting background border and hint color to red.
                            changeStateTextInputLayout(ETGSTNumberLayout,"Red");
                        }

                        Toast.makeText(getActivity(), "Enter Valid Entries" , Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(!ETPANumber.getText().toString().trim().isEmpty() && ETPANumber.getText().toString().trim().length() == 10 && !ImageUrl.equals("")){
                        String regexPAN = "^[A-Z]{5}[0-9]{4}[A-Z]$";

                        Pattern pPAN = Pattern.compile(regexPAN);
                        Matcher mPAN = pPAN.matcher(ETPANumber.getText().toString().trim());

                        if(mPAN.matches()){
                            shopData.setGST_Number("");
                            shopData.setPAN_Number(ETPANumber.getText().toString().trim());
                            shopData.setSignatureImage(ImageUrl);

                            viewModel.addShopData(shopData);
                            viewModel.setPositionSignUp(5);
                        }else{
                            changeStateTextInputLayout(ETPANumberLayout,"Red");
                            Toast.makeText(getActivity(), "PAN Number format is inValid" , Toast.LENGTH_SHORT).show();

                        }

                    }else{
                        if(ImageUrl.equals("")){
                            bottomBarSignatureImage.setBackgroundResource(R.drawable.error_bar);
                        }

                        if(ETPANumber.getText().toString().trim().isEmpty() || ETPANumber.getText().toString().trim().length() != 10){

                            //Setting background border and hint color to red.
                            changeStateTextInputLayout(ETPANumberLayout,"Red");
                        }
                        Toast.makeText(getActivity(), "Enter Valid Entries" , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    public void resetError(){

        ETPANumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(ETPANumberLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETGSTNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(ETGSTNumberLayout,"Blue");
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
