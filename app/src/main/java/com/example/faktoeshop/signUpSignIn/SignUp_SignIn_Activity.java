package com.example.faktoeshop.signUpSignIn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.faktoeshop.R;

public class SignUp_SignIn_Activity extends AppCompatActivity {
    private long backTimePressed; // To store current time to implement backPress 2 times to exit.
    private Toast backToast; // Initialising toast to cancel it once app closes.

    //Initialising Views.
    ImageButton backBtn; //Back Button at top left corner of screen.
    ImageView profileIcon; // Profile icon on the top left corner of screen.
    View bottomBarSignUp , bottomBarSignIn; // blue line under signIn , signUp toggle buttons in top right.

    //Initializing SignIn and SignUp Linear layouts used as buttons
    LinearLayout SignInBtn, SignUpBtn;

    //String variable to get current fragment type and position i.e SignUp or SignIn
    private String currentFragmentType;
    private int currentFragmentPositionSignIn;
    private int currentFragmentPositionSignUp = 1;

    ShopOnboardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_sign_up);

        //Assigning Variables to their respective views.
        backBtn = findViewById(R.id.BackButton_image_btn);
        profileIcon = findViewById(R.id.Profile_icon_image_view);

        bottomBarSignIn = findViewById(R.id.BottomBar_SignIn);
        bottomBarSignUp = findViewById(R.id.BottomBar_SignUp);

        SignInBtn = findViewById(R.id.SignInBtn);
        SignUpBtn = findViewById(R.id.SignUpBtn);

        //Loading the First Fragment when activity start.
        viewModel = new ViewModelProvider(this).get(ShopOnboardViewModel.class);

        String Flag = getIntent().getStringExtra("Flag");

        // Checking for flag from MainActivity if SignIn or SignUp.
        if(Flag != null){
            if(Flag.equals("SignUp")){

                currentFragmentType = "SignUp";
                viewModel.setPositionSignUp(1);

            }
            else if(Flag.equals("SignIn")){

                currentFragmentType = "SignIn";
                viewModel.setPositionSignIn(1);

            }
        }

        // Getting current SignUp fragment position and displaying the required fragment
        viewModel.getPositionSignUp().observe(this, item ->{
            currentFragmentPositionSignUp = item;
        });

        //Displaying the current SignIn fragment
        viewModel.getPositionSignIn().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                currentFragmentType = "SignIn";
                selectFragment(integer);
            }
        });

        //Displaying the current SignUp Fragment
        viewModel.getPositionSignUp().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                currentFragmentType = "SignUp";
                selectFragment(integer);
            }
        });

        //Changing the current fragment type and updating the position
        SignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFragmentType = "SignIn";
                viewModel.setPositionSignIn(1);

            }
        });

        //Changing the current fragment type and updating the position
        SignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentFragmentType = "SignUp";
                viewModel.setPositionSignUp(currentFragmentPositionSignUp);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentFragmentPositionSignUp > 1 && currentFragmentPositionSignUp < 6){
                    currentFragmentPositionSignUp -= 1;
                    viewModel.setPositionSignUp(currentFragmentPositionSignUp);
                }
            }
        });

    }

    //Selecting the fragment according to the currentType and position
    private void selectFragment(Integer Item) {

        if(currentFragmentType.equals("SignUp")){
            setUpSignUp();
            switch(Item){
                case 1:loadFragment(new Fragment_SignUp_1());
                    break;
                case 2:loadFragment(new Fragment_SignUp_2());
                    break;
                case 3:loadFragment(new Fragment_SignUp_3());
                    break;
                case 4:loadFragment(new Fragment_SignUp_4());
                    break;
                case 5:loadFragment(new Fragment_SignUp_5());
                    break;
                case 6:loadFragment(new Fragment_SignUp_6_Waiting());
                    break;
                default: break;
            }
        }else if(currentFragmentType.equals("SignIn")){
            setUpSignIn();
            switch (Item){
                case 1:loadFragment(new Fragment_SignIn_1_MobileNumber());
                    break;
                case 2: SignUpBtn.setClickable(false);
                    loadFragment(new Fragment_SignIn_2_Otp());
                    break;
                default: break;
            }
        }
    }

    //Method to highlight SignUp TextView and visible Back button.
    private void setUpSignUp() {
        backBtn.setVisibility(View.VISIBLE);
        profileIcon.setVisibility(View.GONE);
        bottomBarSignUp.setVisibility(View.VISIBLE);
        bottomBarSignIn.setVisibility(View.INVISIBLE);
        SignUpBtn.setClickable(false);
        SignInBtn.setClickable(true);

    }
    //Method to highlight SignIn TextView and visible the profile icon.
    private void setUpSignIn() {
        backBtn.setVisibility(View.GONE);
        profileIcon.setVisibility(View.VISIBLE);
        bottomBarSignUp.setVisibility(View.INVISIBLE);
        bottomBarSignIn.setVisibility(View.VISIBLE);
        SignInBtn.setClickable(false);
        SignUpBtn.setClickable(true);
    }
    //Method to load a Fragment.
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view,fragment);
        fragmentTransaction.commit();
    }

    // Implementing back twice to exit.
    @Override
    public void onBackPressed() {
        if(backTimePressed + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }else{
            backToast = Toast.makeText(SignUp_SignIn_Activity.this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backTimePressed = System.currentTimeMillis();
    }
}
