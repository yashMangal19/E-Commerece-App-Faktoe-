package com.example.faktoeshop.signUpSignIn;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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

import com.example.faktoeshop.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Fragment_SignIn_1_MobileNumber extends Fragment {
    // Initialising Views.
    EditText enterMobileNumber;
    Button ContinueBtn;
    TextView TVSignInHeading1;

    // Initialising ViewModel.
    ShopOnboardViewModel viewModel;

    // Firebase instance of user.
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String code;
    private boolean isRegistered = false, isVerified = false;
    private final HashMap<String,String> map = new HashMap<String,String>();

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sign_in_1_mobile_number,container,false);

        // Assigning variables to their respective views.

        TVSignInHeading1 = v.findViewById(R.id.fragment_1_sign_in_heading);
        enterMobileNumber = v.findViewById(R.id.EditText_Phone_Verification);
        ContinueBtn = v.findViewById(R.id.ContinueBtn_SignIn_1);

        //Assigning FirebaseAuth to Firebase Instance.
        mAuth = FirebaseAuth.getInstance();


        // Coloring a part of the Heading as BLUE using SpannableString.

        String txt = "Mobile Verification";
        SpannableString ss = new SpannableString(txt);

        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(getResources().getColor(R.color.blue));
        ss.setSpan(fcsBlue, 0, 6, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        TVSignInHeading1.setText(ss);

        // Assigning viewModel to ShopOnBoardViewModel.
        viewModel = new ViewModelProvider(requireActivity()).get(ShopOnboardViewModel.class);

        // Initialising progress bar which will show when continue button is clicked.
        final ProgressBar progressBar = v.findViewById(R.id.progressBar_sending_otp);

        // Functions when OTP is send to phone number.
        PhoneAuthProvider.OnVerificationStateChangedCallbacks
                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            /**
             onVerificationCompleted method will be called when the entered number SIM is in same Device.
             @param credential is send to second fragment to verify user.
             String code is also send to second fragment just to fill the editText automatically if verified.
             */
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                code = credential.getSmsCode();

                viewModel.setSmsCode(code);
                viewModel.setAuthCredential(credential);
                viewModel.setShopDirectoryDetails(map);
                viewModel.setPositionSignIn(2);
                progressBar.setVisibility(View.GONE);
                ContinueBtn.setVisibility(View.VISIBLE);
            }

            /**
             * onVerificationFailed method is called when verification is failed.
             * @param error displays error message if verification failed.
             */
            @Override
            public void onVerificationFailed(@NonNull FirebaseException error) {
                progressBar.setVisibility(View.GONE);
                ContinueBtn.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }

            /**
             * onCodeSent method is called
             * @param verificationId is send to second fragment to make credential for verification
             */
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                viewModel.setVerificationID(verificationId);
                                if(code == null){
                                    viewModel.setShopDirectoryDetails(map);
                                    viewModel.setPositionSignIn(2);
                                }
                            }
                        }, 10000
                );

            }
        };

        // Continue button checking if number entered is not empty and correct
        ContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!enterMobileNumber.getText().toString().trim().isEmpty()){
                    if(enterMobileNumber.getText().toString().trim().length() == 10){
                        progressBar.setVisibility(View.VISIBLE);
                        ContinueBtn.setVisibility(View.INVISIBLE);
                        db.collection("RegisteredShops")
                                .whereEqualTo("phoneNo",enterMobileNumber.getText().toString().trim())
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(!queryDocumentSnapshots.isEmpty()){
                                    Log.d("000000","some");
                                    isRegistered = true;
                                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                        Map<String,Object> mapObject = new HashMap<String,Object>();
                                        mapObject = documentSnapshot.getData();
                                        for (Map.Entry<String, Object> entry : mapObject.entrySet()) {
                                            if(entry.getValue() instanceof String){
                                                map.put(entry.getKey(), (String) entry.getValue());
                                            }
                                        }
                                    }
                                    Log.d("0000000",map.get("isVerified"));
                                    isVerified = Objects.equals(map.get("isVerified"), "true");
                                }else{
                                    isRegistered = false;
                                }

                                if(isVerified && isRegistered){
                                    viewModel.setPhoneEntered("+91 " + enterMobileNumber.getText().toString().trim());

                                    // Requesting for an otp to entered number
                                    PhoneAuthOptions options =
                                            PhoneAuthOptions.newBuilder(mAuth)
                                                    .setPhoneNumber("+91" + enterMobileNumber.getText().toString())       // Phone number to verify
                                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                                    .setActivity(requireActivity())                 // Activity (for callback binding)
                                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                                    .build();
                                    PhoneAuthProvider.verifyPhoneNumber(options);
                                }
                                else{
                                    progressBar.setVisibility(View.GONE);
                                    ContinueBtn.setVisibility(View.VISIBLE);
                                    if(!isRegistered){
                                        Toast.makeText(getActivity(), "Number not registered please sign up", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getActivity(), "Account is not verified yet", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        });
                    }else{
                        Toast.makeText(getActivity(), "Please Enter Correct Number", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return v;
    }
}
