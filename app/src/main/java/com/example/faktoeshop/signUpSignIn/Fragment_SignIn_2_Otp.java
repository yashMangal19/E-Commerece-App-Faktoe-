package com.example.faktoeshop.signUpSignIn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.faktoeshop.Dashboard.ShopDashboard;
import com.example.faktoeshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Fragment_SignIn_2_Otp extends Fragment {
    // Initialising Views.
    TextView TVSignInHeading2, TVSignInDescription2, TVChangeNumber;
    Button ContinueBtn, ResendOtpBtn;
    EditText inputNumber1, inputNumber2, inputNumber3, inputNumber4, inputNumber5, inputNumber6;
    ProgressBar progressBar;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    HashMap<String,String> map = new HashMap<>();

    //Initialising variables for getting information from viewModel.
    private String backEndOtp;
    private PhoneAuthCredential backEndCredential;
    private String backEndVerificationID;
    private String phoneNumberEntered;

    //Firebase Auth for resending OTP.
    FirebaseAuth mAuth;

    //Global Variables so timer can be canceled after fragment is destroyed.
    CountDownTimer timer;

    // Initialising ViewModel.
    ShopOnboardViewModel viewModel;

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sign_in_2_otp, container, false);
        // Assigning variables to their respective views

        TVSignInHeading2 = v.findViewById(R.id.fragment_2_sign_in_heading);
        TVSignInDescription2 = v.findViewById(R.id.fragment_2_sign_in_description);
        TVChangeNumber = v.findViewById(R.id.TextViewChangeNumber);
        ContinueBtn = v.findViewById(R.id.ContinueBtn_SignIn_2);
        progressBar = v.findViewById(R.id.progressBar_verifying_otp);
        ResendOtpBtn = v.findViewById(R.id.ResendOtpBtn);
        inputNumber1 = v.findViewById(R.id.Otp1_EditText);
        inputNumber2 = v.findViewById(R.id.Otp2_EditText);
        inputNumber3 = v.findViewById(R.id.Otp3_EditText);
        inputNumber4 = v.findViewById(R.id.Otp4_EditText);
        inputNumber5 = v.findViewById(R.id.Otp5_EditText);
        inputNumber6 = v.findViewById(R.id.Otp6_EditText);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mEditor = mPreferences.edit();

        //Assigning FirebaseAuth to Firebase Instance.
        mAuth = FirebaseAuth.getInstance();

        // Coloring a part of the Heading as BLUE using SpannableString

        String txt = "Mobile Verification";
        SpannableString ss = new SpannableString(txt);

        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(getResources().getColor(R.color.blue));
        ss.setSpan(fcsBlue, 0, 6, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        TVSignInHeading2.setText(ss);

        //Method to manage focus change between OTP EditTexts.
        numberOtpMove();

        //Timer to enable Resend OTP button after 60s.
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                ResendOtpBtn.setText("Resend in  " + l/1000);
            }

            @Override
            public void onFinish() {
                ResendOtpBtn.setText("Resend");
                ResendOtpBtn.setEnabled(true);
                ResendOtpBtn.setBackgroundResource(R.drawable.shop_onboard_signin_background);
                ResendOtpBtn.setTextColor(getResources().getColor(R.color.blue));
            }
        };

        timer.start();

        // Assigning viewModel to ShopOnBoardViewModel.
        viewModel = new ViewModelProvider(requireActivity()).get(ShopOnboardViewModel.class);


        //Getting Backend OTP if user is verified on previous fragment.
        //Filling the Otp Automatically if user is verified.
        viewModel.getSmsCode().observe(requireActivity(), item ->{
             if(!item.equals("")){
                 backEndOtp = item;
                 new android.os.Handler().postDelayed(
                         new Runnable() {
                             @Override
                             public void run() {
                                 inputNumber1.setText(String.valueOf(backEndOtp.charAt(0)));
                                 inputNumber2.setText(String.valueOf(backEndOtp.charAt(1)));
                                 inputNumber3.setText(String.valueOf(backEndOtp.charAt(2)));
                                 inputNumber4.setText(String.valueOf(backEndOtp.charAt(3)));
                                 inputNumber5.setText(String.valueOf(backEndOtp.charAt(4)));
                                 inputNumber6.setText(String.valueOf(backEndOtp.charAt(5)));
                             }
                         }, 2000
                 );
             }
        });

        //Getting Credential of user if already verified on previous fragment.
        viewModel.getAuthCredential().observe(requireActivity(), item ->{
            if(item != null){
                backEndCredential = item;
            }
        });

        //Getting VerificationID to create PhoneAuthCredential to verify user.
        viewModel.getVerificationID().observe(requireActivity(), item ->{
            if(item != null){
                backEndVerificationID = item;
            }
        });

        //Getting PhoneNumber entered on previous fragment to resend otp if required.
        viewModel.getPhoneEntered().observe(requireActivity(), item->{
            if(item != null){
                phoneNumberEntered = item;
            }
        });

        viewModel.getShopDirectoryDetails().observe(requireActivity(), item->{
            if(item != null){
                map = item;
            }
        });

        //Changing text of Description to contain number entered on previous fragment so user can check if it is right.
        TVSignInDescription2.setText("Enter a 6-digit code send to your mobile number " + phoneNumberEntered);

        /*
        Sending user to first SignIn fragment if he/she decides to change the number.
        Setting all viewModel Data to null assigned according to previous number.
         */
        TVChangeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setSmsCode("");
                viewModel.setVerificationID(null);
                viewModel.setPhoneEntered(null);
                viewModel.setAuthCredential(null);
                viewModel.setPositionSignIn(1);
            }
        });

        /*
        Firstly, checking if any EditText is Empty.
        Secondly, if backEndOtp is null meaning user is not verified therefore creating PhoneAuthCredential and Signing In using that credential.
        If backEndOtp is not null therefore user if verified therefore Signing In using the backEndCredential received from previous fragment.
         */
        ContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputNumber1.getText().toString().trim().isEmpty() && !inputNumber2.getText().toString().trim().isEmpty()
                        && !inputNumber3.getText().toString().trim().isEmpty() && !inputNumber4.getText().toString().trim().isEmpty() &&
                        !inputNumber5.getText().toString().trim().isEmpty() && !inputNumber6.getText().toString().trim().isEmpty()) {
                    String enteredOtp = inputNumber1.getText().toString() +
                            inputNumber2.getText().toString() +
                            inputNumber3.getText().toString() +
                            inputNumber4.getText().toString() +
                            inputNumber5.getText().toString() +
                            inputNumber6.getText().toString();

                    if(backEndOtp == null){
                        progressBar.setVisibility(View.VISIBLE);
                        ContinueBtn.setVisibility(View.INVISIBLE);
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(backEndVerificationID, enteredOtp);
                        signInWithPhoneAuthCredential(credential);

                        }else{
                        progressBar.setVisibility(View.VISIBLE);
                        ContinueBtn.setVisibility(View.INVISIBLE);
                        signInWithPhoneAuthCredential(backEndCredential);
                    }
                    }else{
                    Toast.makeText(getActivity(), "Please enter all numbers", Toast.LENGTH_SHORT).show();
                }
                }
        });

        // Functions when OTP is send to phone number.
        PhoneAuthProvider.OnVerificationStateChangedCallbacks
                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            /**
             onVerificationCompleted method will be called when the entered number SIM is in same Device.
             @param credential is updated in ViewModel to verify user.
             String code is also updated in ViewModel to fill the editText automatically if verified.
             */
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                String code = credential.getSmsCode();

                viewModel.setSmsCode(code);
                viewModel.setAuthCredential(credential);
            }

            /**
             * onVerificationFailed method is called when verification is failed.
             * @param error displays error message if verification failed.
             */
            @Override
            public void onVerificationFailed(@NonNull FirebaseException error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }

            /**
             * onCodeSent method is called
             * @param verificationId is updated in ViewModel to make credential for verification
             */
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                viewModel.setVerificationID(verificationId);
            }
        };

        //Resending OTP to the number entered on previous fragment.
        ResendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Requesting for an otp to entered number
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber(phoneNumberEntered)       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(requireActivity())                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
                ResendOtpBtn.setTextColor(getResources().getColor(R.color.dark_gray));
                ResendOtpBtn.setEnabled(false);
                timer.start();

            }
        });

        return v;
    }

    //Signing In user using the credential either mage from @backEndVerificationID or directly by @backEndCredential
    private void signInWithPhoneAuthCredential(PhoneAuthCredential cred) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(cred)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        ContinueBtn.setVisibility(View.VISIBLE);

                        if(task.isSuccessful()){
                            mEditor.putString(getString(R.string.ShopState),map.get("State"));
                            mEditor.commit();

                            mEditor.putString(getString(R.string.ShopDistrict),map.get("District"));
                            mEditor.commit();

                            mEditor.putString(getString(R.string.ShopPhoneNumber),map.get("phoneNo"));
                            mEditor.commit();

                            mEditor.putString(getString(R.string.ShopId),map.get("id"));
                            mEditor.commit();

                            Intent intent = new Intent(getActivity(), ShopDashboard.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                        else{
                            Toast.makeText(getActivity(),"Please Enter correct OTP",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Method to manage focus change between OTP EditTexts
     * When a number is entered in any EditText then focus changed to next EditText.
     * When a number is removed i.e. when backspace is pressed in non-empty EditText then focus changed to previous EditText.
     * @Bug when a EditText is empty and then backSpace is pressed focus doesn't change to previous EditText.
     */
    private void numberOtpMove() {
        inputNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    inputNumber2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 == 0) {
                    inputNumber1.requestFocus();
                }else{
                    if(charSequence.length() == 1){
                        inputNumber3.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputNumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 == 0) {
                    inputNumber2.requestFocus();
                }else{
                    if(charSequence.length() == 1){
                        inputNumber4.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputNumber4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 == 0) {
                    inputNumber3.requestFocus();
                }else{
                    if(charSequence.length() == 1){
                        inputNumber5.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputNumber5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 == 0) {
                    inputNumber4.requestFocus();
                }else{
                    if(charSequence.length() == 1){
                        inputNumber6.requestFocus();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputNumber6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 == 0) {
                    inputNumber5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //Canceling the timer if fragment is changed.
    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
