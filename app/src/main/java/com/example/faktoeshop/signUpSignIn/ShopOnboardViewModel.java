package com.example.faktoeshop.signUpSignIn;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.PhoneAuthCredential;

import java.util.ArrayList;
import java.util.HashMap;

public class ShopOnboardViewModel extends ViewModel {
    //Initialising an empty ShopOnboardClass Instance.
    ArrayList<String> initialCategory = new ArrayList<>();
    ArrayList<String> initialOperatingDays = new ArrayList<>();
    ShopOnboardClass initialData = new ShopOnboardClass("","","","","","","", "","","","","","","","","","","","","","",initialCategory,initialOperatingDays);

    /*
    @MutableLiveData ShopData.
    Data of a class to store all data to be pushed to Firebase.
     */
    private final MutableLiveData<ShopOnboardClass> ShopData = new MutableLiveData<>(initialData);

    public void addShopData(ShopOnboardClass input){
        ShopData.setValue(input);
    }

    public MutableLiveData<ShopOnboardClass> getShopData() {
        return ShopData;
    }

    /*
    @MutableLiveData PositionSignUp.
    To navigate between fragments and store current position of SignUp fragment.
     */
    private final MutableLiveData<Integer> PositionSignUp = new MutableLiveData<>();

    public void setPositionSignUp(Integer value){
        PositionSignUp.setValue(value);
    }

    public MutableLiveData<Integer> getPositionSignUp() {
        return PositionSignUp;
    }

    /*
    @MutableLiveData PositionSignIn
    To navigate between fragments and store current position of SignIn fragment.
     */
    private final MutableLiveData<Integer> PositionSignIn = new MutableLiveData<>();

    public void setPositionSignIn(Integer value){
        PositionSignIn.setValue(value);
    }

    public MutableLiveData<Integer> getPositionSignIn() {
        return PositionSignIn;
    }

    /*
    @MutableLiveData AuthCredential
    Send to second SignIn fragment to verify user when automatically verified.
     */
    private final MutableLiveData<PhoneAuthCredential> AuthCredential = new MutableLiveData<>();

    public void setAuthCredential(PhoneAuthCredential value){
        AuthCredential.setValue(value);
    }

    public MutableLiveData<PhoneAuthCredential> getAuthCredential() {
        return AuthCredential;
    }

    /*
    @MutableLiveData smsCode
    Send th second SignIn fragment to create a credential using this ID and OTP entered by user.
     */
    private final MutableLiveData<String> smsCode = new MutableLiveData<>("");

    public void setSmsCode(String value){
        smsCode.setValue(value);
    }

    public MutableLiveData<String> getSmsCode() {
        return smsCode;
    }

    /*
    @MutableLiveData VerificationID
    Sent to second SignIn fragment to automatically fill up and signIn if verified automatically.
     */
    private final MutableLiveData<String> VerificationID = new MutableLiveData<>();

    public void setVerificationID(String value){
        VerificationID.setValue(value);
    }

    public MutableLiveData<String> getVerificationID() {
        return VerificationID;
    }

    /*
    @MutableLiveData phoneEntered
    Sent to second SignIn fragment to edit TextView so user can see the number he/she entered and change it if wrong.
     */
    private final MutableLiveData<String> phoneEntered = new MutableLiveData<>();

    public void setPhoneEntered(String value){
        phoneEntered.setValue(value);
    }

    public MutableLiveData<String> getPhoneEntered() {
        return phoneEntered;
    }

    /*
    @MutableLiveData shopDirectoryDetails
    Sent to second SignIn fragment to store shop directory in shared preferences.
     */
    private final MutableLiveData<HashMap<String,String>> shopDirectoryDetails = new MutableLiveData<>();

    public void setShopDirectoryDetails(HashMap<String,String> hashMap){
        shopDirectoryDetails.setValue(hashMap);
    }

    public MutableLiveData<HashMap<String,String>> getShopDirectoryDetails(){
        return shopDirectoryDetails;
    }

}
