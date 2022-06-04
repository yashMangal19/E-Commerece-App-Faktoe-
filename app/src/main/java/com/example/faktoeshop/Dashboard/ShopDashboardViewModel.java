package com.example.faktoeshop.Dashboard;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.faktoeshop.signUpSignIn.ShopOnboardClass;

public class ShopDashboardViewModel extends ViewModel {

    private final MutableLiveData<String> CurrentDashboardPosition = new MutableLiveData<>();

    public void setCurrentDashboardPosition(String input){
        CurrentDashboardPosition.setValue(input);
    }

    public MutableLiveData<String> getCurrentDashboardPosition() {
        return CurrentDashboardPosition;
    }
}
