package com.example.faktoeshop.Dashboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.faktoeshop.R;
import com.example.faktoeshop.signUpSignIn.ShopOnboardClass;
import com.example.faktoeshop.signUpSignIn.ShopOnboardViewModel;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class ShopDashboard extends AppCompatActivity {

    ChipNavigationBar bottomNav;

    ShopDashboardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_dashboard);

        bottomNav = findViewById(R.id.BottomNavBar);

        viewModel = new ViewModelProvider(this).get(ShopDashboardViewModel.class);

        viewModel.getCurrentDashboardPosition().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

            }
        });

        loadFragment(new Fragment_Dashboard_Home());

        bottomNav.setItemSelected(R.id.home,true);

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                if(i == R.id.home){
                    loadFragment(new Fragment_Dashboard_Home());
                }else if(i == R.id.add){
                    loadFragment(new Fragment_Dashboard_NewProduct());
                }
            }
        });


    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view_dashboard,fragment);
        fragmentTransaction.commit();
    }
}