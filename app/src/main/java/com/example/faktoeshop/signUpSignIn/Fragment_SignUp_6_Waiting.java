package com.example.faktoeshop.signUpSignIn;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.faktoeshop.R;

public class Fragment_SignUp_6_Waiting extends Fragment {
    // Initialising Views
    TextView TVSignUpHeading6;
    Button SignInBtn;

    ShopOnboardViewModel viewModel;

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sign_up_6_waiting, container, false);

        // Assigning variables to their respective views

        TVSignUpHeading6 = v.findViewById(R.id.fragment_6_heading);
        SignInBtn = v.findViewById(R.id.SignInBtn_Fragment_6);

        // Coloring a part of the Heading as BLUE using SpannableString

        String txt = "Phone Verification Complete";
        SpannableString ss = new SpannableString(txt);

        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(getResources().getColor(R.color.blue));
        ss.setSpan(fcsBlue, 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        TVSignUpHeading6.setText(ss);

        // Assigning viewModel to ShopOnBoardViewModel.
        viewModel = new ViewModelProvider(requireActivity()).get(ShopOnboardViewModel.class);

        SignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setPositionSignIn(1);
            }
        });

        return v;
    }
}
