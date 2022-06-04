package com.example.faktoeshop.signUpSignIn;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.faktoeshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Fragment_SignUp_1 extends Fragment {
    // Initialising Views
    TextView TVSignUpHeading1;
    EditText ETName, ETPhoneNumber, ETEmail, ETBusinessName;
    EditText ETOtpDialog1, ETOtpDialog2, ETOtpDialog3, ETOtpDialog4, ETOtpDialog5 ,ETOtpDialog6 ;
    Button verifyOtpDialog;
    ProgressBar progressBarDialog;
    TextInputLayout ETNameLayout, ETPhoneNumberLayout, ETEmailLayout, ETBusinessNameLayout; // To Set error backgrounds.
    Button ContinueBtn;
    ProgressBar progressBar;

    private boolean isNumberVerified = false;

    Dialog dialog;

    Handler handler = new Handler();
    Runnable runnable;

    //Global Variables so timer can be canceled after fragment is destroyed.
    CountDownTimer timer;

    // Firebase instance of user.
    FirebaseAuth mAuth;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    // Initializing shopOnboardClass to store data in viewModel.
    ShopOnboardClass shopData;

    // Initializing ViewModel.
    ShopOnboardViewModel viewModel;

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sign_up_1,container,false);

        // Assigning variables to their respective views.

        TVSignUpHeading1 = v.findViewById(R.id.fragment_1_heading);

        // Coloring a part of the Heading as BLUE using SpannableString.

        String txt = "Create your Account";
        SpannableString ss = new SpannableString(txt);

        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(getResources().getColor(R.color.blue));
        ss.setSpan(fcsBlue, 0, 7, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        TVSignUpHeading1.setText(ss);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assigning variables to their respective views.

        ETNameLayout = v.findViewById(R.id.EditText_Name_Layout);
        ETPhoneNumberLayout = v.findViewById(R.id.EditText_PhoneNumber_Layout);
        ETEmailLayout = v.findViewById(R.id.EditText_Email_Layout);
        ETBusinessNameLayout = v.findViewById(R.id.EditText_BusinessName_Layout);

        ETName = v.findViewById(R.id.EditText_Name);
        ETPhoneNumber = v.findViewById(R.id.EditText_PhoneNumber);
        ETEmail = v.findViewById(R.id.EditText_Email);
        ETBusinessName = v.findViewById(R.id.EditText_BusinessName);
        ContinueBtn = v.findViewById(R.id.ContinueBtn_SignUp_1);
        progressBar = v.findViewById(R.id.progressBar_verifying_otp_dialog);

        //Assigning FirebaseAuth to Firebase Instance.
        mAuth = FirebaseAuth.getInstance();

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
                ETName.setText(shopData.getUserName());
                ETPhoneNumber.setText(shopData.getPhoneNumber());
                if(!shopData.getPhoneNumber().equals("")){
                    isNumberVerified = true;
                    ETPhoneNumberLayout.setEndIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                }
                ETEmail.setText(shopData.getEmail());
                ETBusinessName.setText(shopData.getShopName());
            }
        });

        // Functions when OTP is send to phone number.
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            /**
             onVerificationCompleted method will be called when the entered number SIM is in same Device.
             @param credential is send to second fragment to verify user.
             String code is also send to second fragment just to fill the editText automatically if verified.
             */
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
                ETPhoneNumberLayout.setEndIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                isNumberVerified = true;
                progressBar.setVisibility(View.VISIBLE);
                ContinueBtn.setVisibility(View.INVISIBLE);
            }

            /**
             * onVerificationFailed method is called when verification is failed.
             * @param error displays error message if verification failed.
             */
            @Override
            public void onVerificationFailed(@NonNull FirebaseException error) {
                if(verifyOtpDialog != null && progressBarDialog != null){
                    progressBarDialog.setVisibility(View.GONE);
                    verifyOtpDialog.setVisibility(View.VISIBLE);
                }
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            /**
             * onCodeSent method is called
             * @param verificationId is send to second fragment to make credential for verification
             */
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                handler.postDelayed(
                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                if(!isNumberVerified){
                                    if(dialog != null){
                                        dialog.cancel();
                                    }
                                    showDialog(verificationId);
                                }
                            }
                        }, 10000
                );

            }
        };


        /*
        Checking if any EditText is empty.
        Checking if Phone number entered is in correct format.
        Updating data in ShopData in ViewModel and changing the fragment.
         */
        ContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ETName.getText().toString().trim().isEmpty() && !ETPhoneNumber.getText().toString().trim().isEmpty() &&
                        !ETEmail.getText().toString().trim().isEmpty() && !ETBusinessName.getText().toString().trim().isEmpty()){
                    if(ETPhoneNumber.getText().toString().trim().length() == 10){
                        if(isNumberVerified){
                            //Updating data in ShopOnboardClass and then pushing it to ViewModel.
                            shopData.setUserName(ETName.getText().toString());
                            shopData.setPhoneNumber(ETPhoneNumber.getText().toString());
                            shopData.setEmail(ETEmail.getText().toString());
                            shopData.setShopName(ETBusinessName.getText().toString());
                            viewModel.addShopData(shopData);

                            //Changing the fragment.
                            viewModel.setPositionSignUp(2);
                        }
                        else{
                            progressBar.setVisibility(View.VISIBLE);
                            ContinueBtn.setVisibility(View.INVISIBLE);

                            // Requesting for an otp to entered number
                            PhoneAuthOptions options =
                                    PhoneAuthOptions.newBuilder(mAuth)
                                            .setPhoneNumber("+91 " + ETPhoneNumber.getText().toString())       // Phone number to verify
                                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                            .setActivity(requireActivity())                 // Activity (for callback binding)
                                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                            .build();
                            PhoneAuthProvider.verifyPhoneNumber(options);
                        }
                    }else{
                        //If Phone number is not entered in correct format setting text to null to show error background.
                        changeStateTextInputLayout(ETPhoneNumberLayout,"Red");

                        //Toast to alert user to enter correct phone number.
                        Toast.makeText(getActivity(),"Please enter correct Phone Number",Toast.LENGTH_SHORT).show();

                    }
                }else{
                    /*
                    If any EditText is empty setting error background of that EditText.
                     */

                    // If Name EditText is Empty.
                    if(ETName.getText().toString().trim().isEmpty()){

                        //Setting background border and hint color to red.
                        changeStateTextInputLayout(ETNameLayout,"Red");
                    }

                    //If Phone Number EditText is empty.
                    if(ETPhoneNumber.getText().toString().trim().isEmpty()){

                        //Setting background border and hint color to red.
                        changeStateTextInputLayout(ETPhoneNumberLayout,"Red");
                    }

                    //If Email EditText is empty.
                    if(ETEmail.getText().toString().trim().isEmpty()){

                        //Setting background border and hint color to red.
                        changeStateTextInputLayout(ETEmailLayout,"Red");
                    }

                    //If Business name EditText is empty.
                    if(ETBusinessName.getText().toString().trim().isEmpty()){

                        //Setting background border and hint color to red.
                        changeStateTextInputLayout(ETBusinessNameLayout,"Red");
                    }

                    //Toast to alert user to fill all fields.
                    Toast.makeText(getActivity(),"Please Enter all Fields",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showDialog(String verifyId) {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.fragment_sign_up_1_otp_dialog);

        ImageView cancelDialogBtn = dialog.findViewById(R.id.cancel_dialog_btn);
        TextView dialogDescription = dialog.findViewById(R.id.dialog_description);
        TextView TVResendOTPDialog = dialog.findViewById(R.id.TextView_dialog_resend);
        ETOtpDialog1 = dialog.findViewById(R.id.Dialog_Otp1_EditText);
        ETOtpDialog2 = dialog.findViewById(R.id.Dialog_Otp2_EditText);
        ETOtpDialog3 = dialog.findViewById(R.id.Dialog_Otp3_EditText);
        ETOtpDialog4 = dialog.findViewById(R.id.Dialog_Otp4_EditText);
        ETOtpDialog5 = dialog.findViewById(R.id.Dialog_Otp5_EditText);
        ETOtpDialog6 = dialog.findViewById(R.id.Dialog_Otp6_EditText);
        verifyOtpDialog = dialog.findViewById(R.id.VerifyBtn_SignUp_1_dialog);
        progressBarDialog = dialog.findViewById(R.id.progressBar_verifying_otp_in_dialog);

        numberOtpMove();

        cancelDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.GONE);
                ContinueBtn.setVisibility(View.VISIBLE);
                dialog.cancel();

            }
        });

        dialogDescription.setText("A 6-digit code has been sent to +91 " + ETPhoneNumber.getText().toString().trim());

        //Timer to enable Resend OTP button after 60s.
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                TVResendOTPDialog.setText("Resend in  " + l/1000);
            }

            @Override
            public void onFinish() {
                TVResendOTPDialog.setText("Resend");
                TVResendOTPDialog.setClickable(true);
                TVResendOTPDialog.setTextColor(getResources().getColor(R.color.blue));
            }
        };

        TVResendOTPDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TVResendOTPDialog.setClickable(false);
                TVResendOTPDialog.setTextColor(getResources().getColor(R.color.gray));
                progressBarDialog.setVisibility(View.VISIBLE);
                verifyOtpDialog.setVisibility(View.INVISIBLE);
                // Requesting for an otp to entered number
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber("+91 " + ETPhoneNumber.getText().toString())       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(requireActivity())                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });

        timer.start();


        verifyOtpDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ETOtpDialog1.getText().toString().trim().isEmpty() && !ETOtpDialog2.getText().toString().trim().isEmpty()
                        && !ETOtpDialog3.getText().toString().trim().isEmpty() && !ETOtpDialog4.getText().toString().trim().isEmpty()
                        && !ETOtpDialog5.getText().toString().trim().isEmpty() && !ETOtpDialog6.getText().toString().trim().isEmpty()){
                    String enteredOtp = ETOtpDialog1.getText().toString().trim() + ETOtpDialog2.getText().toString().trim()
                            + ETOtpDialog3.getText().toString().trim() + ETOtpDialog4.getText().toString().trim()
                            + ETOtpDialog5.getText().toString().trim() + ETOtpDialog6.getText().toString().trim();
                    progressBarDialog.setVisibility(View.VISIBLE);
                    verifyOtpDialog.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyId, enteredOtp);
                    signInWithPhoneAuthCredential(credential);


                }else{
                    Toast.makeText(getActivity(), "Please enter correct otp",Toast.LENGTH_SHORT).show();
                    OTPError();
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
        ETName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(ETNameLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ETPhoneNumberLayout.setEndIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.light_gray)));
                isNumberVerified = false;
                changeStateTextInputLayout(ETPhoneNumberLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(ETEmailLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETBusinessName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(ETBusinessNameLayout,"Blue");
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

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        ContinueBtn.setVisibility(View.VISIBLE);
                        if(progressBarDialog != null){
                            progressBarDialog.setVisibility(View.GONE);
                            verifyOtpDialog.setVisibility(View.VISIBLE);
                        }
                        if (task.isSuccessful()) {
                            ETPhoneNumberLayout.setEndIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.blue)));
                            isNumberVerified = true;
                            Toast.makeText(getActivity(),"Phone Number Verified press continue again to proceed.",Toast.LENGTH_SHORT).show();
                            if(dialog != null){
                                dialog.cancel();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please Enter correct OTP",Toast.LENGTH_SHORT).show();
                            OTPError();
                        }
                    }
                });
    }

    private void numberOtpMove() {
        ETOtpDialog1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                resetOTPError();
                if (charSequence.length() == 1) {
                    ETOtpDialog2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETOtpDialog2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                resetOTPError();
                if (i2 == 0) {
                    ETOtpDialog1.requestFocus();
                }else{
                    if(charSequence.length() == 1){
                        ETOtpDialog3.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETOtpDialog3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                resetOTPError();
                if (i2 == 0) {
                    ETOtpDialog2.requestFocus();
                }else{
                    if(charSequence.length() == 1){
                        ETOtpDialog4.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETOtpDialog4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                resetOTPError();
                if (i2 == 0) {
                    ETOtpDialog3.requestFocus();
                }else{
                    if(charSequence.length() == 1){
                        ETOtpDialog5.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETOtpDialog5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                resetOTPError();
                if (i2 == 0) {
                    ETOtpDialog4.requestFocus();
                }else{
                    if(charSequence.length() == 1){
                        ETOtpDialog6.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETOtpDialog6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                resetOTPError();
                if (i2 == 0) {
                    ETOtpDialog5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void resetOTPError() {
        ETOtpDialog1.setBackgroundResource(R.drawable.shop_onboard_signin_background);
        ETOtpDialog2.setBackgroundResource(R.drawable.shop_onboard_signin_background);
        ETOtpDialog3.setBackgroundResource(R.drawable.shop_onboard_signin_background);
        ETOtpDialog4.setBackgroundResource(R.drawable.shop_onboard_signin_background);
        ETOtpDialog5.setBackgroundResource(R.drawable.shop_onboard_signin_background);
        ETOtpDialog6.setBackgroundResource(R.drawable.shop_onboard_signin_background);
    }

    private void OTPError() {
        ETOtpDialog1.setBackgroundResource(R.drawable.edit_text_error_border);
        ETOtpDialog2.setBackgroundResource(R.drawable.edit_text_error_border);
        ETOtpDialog3.setBackgroundResource(R.drawable.edit_text_error_border);
        ETOtpDialog4.setBackgroundResource(R.drawable.edit_text_error_border);
        ETOtpDialog5.setBackgroundResource(R.drawable.edit_text_error_border);
        ETOtpDialog6.setBackgroundResource(R.drawable.edit_text_error_border);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null){
            timer.cancel();
        }
        if(runnable != null){
            handler.removeCallbacks(runnable);
        }
    }
}
