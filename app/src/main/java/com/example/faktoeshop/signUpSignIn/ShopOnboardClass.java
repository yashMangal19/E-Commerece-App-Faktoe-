package com.example.faktoeshop.signUpSignIn;

import java.util.ArrayList;

/*
@ShopOnboardClass to upload data to FireBase and to use with ShopOnboardViewModel
 */
public class ShopOnboardClass {
    private String UserName, PhoneNumber, Email, ShopName, Address, State, City, Postcode, OpenHour, OpenMinute, CloseHour, CloseMinute, AnnualTurnover;
    private String NumberOfProducts, GST_Number, PAN_Number, SignatureImage, AccountHolderName, AccountType, AccountNumber, IFSC_Code;
    private ArrayList<String> Category , OperatingDays;


    //Empty Constructor required for FireBase

    public ShopOnboardClass() {

    }

    //Custom Clone constructor.

    public ShopOnboardClass(ShopOnboardClass anotherClass){
        this.UserName = anotherClass.UserName;
        this.PhoneNumber = anotherClass.PhoneNumber;
        this.Email = anotherClass.Email;
        this.ShopName = anotherClass.ShopName;
        this.Address = anotherClass.Address;
        this.State = anotherClass.State;
        this.City = anotherClass.City;
        this.Postcode = anotherClass.Postcode;
        this.OpenHour = anotherClass.OpenHour;
        this.OpenMinute = anotherClass.OpenMinute;
        this.CloseHour = anotherClass.CloseHour;
        this.CloseMinute = anotherClass.CloseMinute;
        this.AnnualTurnover = anotherClass.AnnualTurnover;
        this.NumberOfProducts = anotherClass.NumberOfProducts;
        this.GST_Number = anotherClass.GST_Number;
        this.PAN_Number = anotherClass.PAN_Number;
        this.SignatureImage = anotherClass.SignatureImage;
        this.AccountHolderName = anotherClass.AccountHolderName;
        this.AccountType = anotherClass.AccountType;
        this.AccountNumber = anotherClass.AccountNumber;
        this.IFSC_Code = anotherClass.IFSC_Code;
        this.Category = anotherClass.Category;
        this.OperatingDays = anotherClass.OperatingDays;
    }

    public ShopOnboardClass(String userName, String phoneNumber, String email, String shopName, String address, String state, String city, String postcode, String openHour, String openMinute, String closeHour, String closeMinute, String annualTurnover, String numberOfProducts, String GST_Number, String PAN_Number, String signatureImage, String accountHolderName, String accountType, String accountNumber, String IFSC_Code, ArrayList<String> category, ArrayList<String> operatingDays) {
        this.UserName = userName;
        this.PhoneNumber = phoneNumber;
        this.Email = email;
        this.ShopName = shopName;
        this.Address = address;
        this.State = state;
        this.City = city;
        this.Postcode = postcode;
        this.OpenHour = openHour;
        this.OpenMinute = openMinute;
        this.CloseHour = closeHour;
        this.CloseMinute = closeMinute;
        this.AnnualTurnover = annualTurnover;
        this.NumberOfProducts = numberOfProducts;
        this.GST_Number = GST_Number;
        this.PAN_Number = PAN_Number;
        this.SignatureImage = signatureImage;
        this.AccountHolderName = accountHolderName;
        this.AccountType = accountType;
        this.AccountNumber = accountNumber;
        this.IFSC_Code = IFSC_Code;
        this.Category = category;
        this.OperatingDays = operatingDays;
    }

    // Setters and Getter for all Variables

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getShopName() {
        return ShopName;
    }

    public void setShopName(String shopName) {
        ShopName = shopName;
    }

    public String getAddress(){
        return Address;
    }

    public void setAddress(String address){
        Address = address;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getPostcode() {
        return Postcode;
    }

    public void setPostcode(String postcode) {
        Postcode = postcode;
    }

    public String getOpenHour() {
        return OpenHour;
    }

    public void setOpenHour(String openHour) {
        OpenHour = openHour;
    }

    public String getOpenMinute() {
        return OpenMinute;
    }

    public void setOpenMinute(String openMinute) {
        OpenMinute = openMinute;
    }

    public String getCloseHour() {
        return CloseHour;
    }

    public void setCloseHour(String closeHour) {
        CloseHour = closeHour;
    }

    public String getCloseMinute() {
        return CloseMinute;
    }

    public void setCloseMinute(String closeMinute) {
        CloseMinute = closeMinute;
    }

    public String getAnnualTurnover() {
        return AnnualTurnover;
    }

    public void setAnnualTurnover(String annualTurnover) {
        AnnualTurnover = annualTurnover;
    }

    public String getNumberOfProducts() {
        return NumberOfProducts;
    }

    public void setNumberOfProducts(String numberOfProducts) {
        NumberOfProducts = numberOfProducts;
    }

    public String getGST_Number() {
        return GST_Number;
    }

    public void setGST_Number(String GST_Number) {
        this.GST_Number = GST_Number;
    }

    public String getPAN_Number() {
        return PAN_Number;
    }

    public void setPAN_Number(String PAN_Number) {
        this.PAN_Number = PAN_Number;
    }

    public String getSignatureImage() {
        return SignatureImage;
    }

    public void setSignatureImage(String signatureImage) {
        SignatureImage = signatureImage;
    }

    public String getAccountHolderName() {
        return AccountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        AccountHolderName = accountHolderName;
    }

    public String getAccountType() {
        return AccountType;
    }

    public void setAccountType(String accountType) {
        AccountType = accountType;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        AccountNumber = accountNumber;
    }

    public String getIFSC_Code() {
        return IFSC_Code;
    }

    public void setIFSC_Code(String IFSC_Code) {
        this.IFSC_Code = IFSC_Code;
    }

    public ArrayList<String> getCategory() {
        return Category;
    }

    public void setCategory(ArrayList<String> category) {
        Category = category;
    }

    public ArrayList<String> getOperatingDays() {
        return OperatingDays;
    }

    public void setOperatingDays(ArrayList<String> operatingDays) {
        OperatingDays = operatingDays;
    }
}
