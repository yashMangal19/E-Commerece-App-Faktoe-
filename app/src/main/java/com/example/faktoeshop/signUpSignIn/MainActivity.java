package com.example.faktoeshop.signUpSignIn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.faktoeshop.R;

public class MainActivity extends AppCompatActivity {

    // Initialising SignUp and SignIn Buttons
    Button MainSignUpBtn, MainSignInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assigning Variables to their respective views

        MainSignUpBtn = findViewById(R.id.MainSignUpBtn);
        MainSignInBtn = findViewById(R.id.MainSignIpBtn);

        //Opening new Activity on clicking SignUp and SignIn Button
        //Assigning a Flag to transfer to the new activity
        MainSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Flag = "SignUp";
                OpenSignUp_SignInBtn(Flag);
            }
        });

        MainSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Flag = "SignIn";
                OpenSignUp_SignInBtn(Flag);
            }
        });
    }

    private void OpenSignUp_SignInBtn(String flag) {
        Intent SignUp = new Intent(MainActivity.this, SignUp_SignIn_Activity.class);
        SignUp.putExtra("Flag",flag);
        startActivity(SignUp);
        SignUp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        SignUp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
    }
}