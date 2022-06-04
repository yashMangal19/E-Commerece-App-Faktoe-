package com.example.faktoeshop.signUpSignIn;

import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.faktoeshop.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Locale;

public class Fragment_SignUp_3 extends Fragment {
    // Initialising Views

    TextView TVSignUpHeading3;
    AutoCompleteTextView ACTAnnualTurnover, ACTNumberOfProducts;
    EditText ETOpenTime, ETCloseTime;
    TextInputLayout ETOpenTimeLayout, ETCloseTimeLayout, ACTAnnualTurnoverLayout, ACTNumberOfProductsLayout;
    Button BtnSunday, BtnMonday, BtnTuesday, BtnWednesday, BtnThursday, BtnFriday, BtnSaturday;
    CheckBox CBGeneralStore, CBPharmaceutics, CBEntertainment, CBStationary, CBHomeAppliances,
            CBGrocery, CBElectronics, CBHomeDecor, CBPersonalCare, CBClothing, CBFootwear, CBJewelry,
            CBSports, CBBabyCare, CBPetShop;
    View bottomBarCategory, bottomBarTimings, bottomBarOperatingDays;
    Button ContinueBtn;

    private int hourOpen, minuteOpen, hourClose, minuteClose;
    private Boolean isSundaySelected = false, isMondaySelected = false, isTuesdaySelected = false, isWednesdaySelected = false,
            isThursdaySelected = false, isFridaySelected = false, isSaturdaySelected = false;
    private ArrayList<String> categoryList, operatingDaysList;

    //Initializing shopOnboardClass to store data in viewModel.
    ShopOnboardClass shopData;

    //Initializing ViewModel
    ShopOnboardViewModel viewModel;

    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sign_up_3,container,false);

        // Assigning variables to their respective views

        TVSignUpHeading3 = v.findViewById(R.id.fragment_3_heading);
        ACTAnnualTurnover = v.findViewById(R.id.EditText_AnnualTurnover);
        ACTNumberOfProducts = v.findViewById(R.id.EditText_NumberOfProducts);



        // Coloring a part of the Heading as BLUE using SpannableString

        String txt = "Shop Information";
        SpannableString ss = new SpannableString(txt);

        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(getResources().getColor(R.color.blue));
        ss.setSpan(fcsBlue, 0, 4, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        TVSignUpHeading3.setText(ss);

        //Assigning Views and Adapter for Spinners
        ArrayAdapter<CharSequence> turnoverAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.annual_turnover_array, R.layout.spinner_layout);

        //Setting up adapter with simple spinner dropdown item layout.
        //Setting adapter to the spinner.
        turnoverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ACTAnnualTurnover.setAdapter(turnoverAdapter);

        //Assigning Views and Adapter for Spinners
        ArrayAdapter<CharSequence> noOfProductsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.number_of_products_array, R.layout.spinner_layout);

        //Setting up adapter with simple spinner dropdown item layout.
        //Setting adapter to the spinner.
        noOfProductsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ACTNumberOfProducts.setAdapter(noOfProductsAdapter);


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ETOpenTime = v.findViewById(R.id.EditText_OpenTime);
        ETCloseTime = v.findViewById(R.id.EditText_CloseTime);
        ContinueBtn = v.findViewById(R.id.ContinueBtn_SignUp_3);

        ETOpenTimeLayout = v.findViewById(R.id.EditText_OpenTime_Layout);
        ETCloseTimeLayout = v.findViewById(R.id.EditText_CloseTime_Layout);
        ACTAnnualTurnoverLayout = v.findViewById(R.id.EditText_AnnualTurnover_Layout);
        ACTNumberOfProductsLayout = v.findViewById(R.id.EditText_NumberOfProducts_Layout);

        bottomBarCategory = v.findViewById(R.id.bottom_bar_category);
        bottomBarTimings = v.findViewById(R.id.bottom_bar_timings);
        bottomBarOperatingDays = v.findViewById(R.id.bottom_bar_operatingDays);

        CBGeneralStore = v.findViewById(R.id.CheckBox_GeneralStore);
        CBPharmaceutics = v.findViewById(R.id.CheckBox_Pharmaceutics);
        CBEntertainment = v.findViewById(R.id.CheckBox_Entertainment);
        CBStationary = v.findViewById(R.id.CheckBox_Stationary);
        CBHomeAppliances = v.findViewById(R.id.CheckBox_HomeAppliances);
        CBGrocery = v.findViewById(R.id.CheckBox_Grocery);
        CBElectronics = v.findViewById(R.id.CheckBox_Electronics);
        CBHomeDecor = v.findViewById(R.id.CheckBox_HomeDecor);
        CBPersonalCare = v.findViewById(R.id.CheckBox_PersonalCare);
        CBClothing = v.findViewById(R.id.CheckBox_Clothing);
        CBFootwear = v.findViewById(R.id.CheckBox_Footwear);
        CBJewelry = v.findViewById(R.id.CheckBox_Jewelry);
        CBSports = v.findViewById(R.id.CheckBox_Sports);
        CBBabyCare = v.findViewById(R.id.CheckBox_BabyCare);
        CBPetShop = v.findViewById(R.id.CheckBox_PetShop);

        BtnSunday = v.findViewById(R.id.Button_Sunday);
        BtnMonday = v.findViewById(R.id.Button_Monday);
        BtnTuesday = v.findViewById(R.id.Button_Tuesday);
        BtnWednesday = v.findViewById(R.id.Button_Wednesday);
        BtnThursday = v.findViewById(R.id.Button_Thursday);
        BtnFriday = v.findViewById(R.id.Button_Friday);
        BtnSaturday = v.findViewById(R.id.Button_Saturday);

        resetError();

        BtnSunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSundaySelected){
                    isSundaySelected = false;
                    BtnSunday.setBackgroundResource(R.drawable.days_unselected);
                    BtnSunday.setTextColor(getResources().getColor(R.color.gray));
                }else{
                    isSundaySelected = true;
                    bottomBarOperatingDays.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                    BtnSunday.setBackgroundResource(R.drawable.days_selected);
                    BtnSunday.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        BtnMonday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMondaySelected){
                    isMondaySelected = false;
                    BtnMonday.setBackgroundResource(R.drawable.days_unselected);
                    BtnMonday.setTextColor(getResources().getColor(R.color.gray));
                }else{
                    isMondaySelected = true;
                    bottomBarOperatingDays.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                    BtnMonday.setBackgroundResource(R.drawable.days_selected);
                    BtnMonday.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        BtnTuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isTuesdaySelected){
                    isTuesdaySelected = false;
                    BtnTuesday.setBackgroundResource(R.drawable.days_unselected);
                    BtnTuesday.setTextColor(getResources().getColor(R.color.gray));
                }else{
                    isTuesdaySelected = true;
                    bottomBarOperatingDays.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                    BtnTuesday.setBackgroundResource(R.drawable.days_selected);
                    BtnTuesday.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        BtnWednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isWednesdaySelected){
                    isWednesdaySelected = false;
                    BtnWednesday.setBackgroundResource(R.drawable.days_unselected);
                    BtnWednesday.setTextColor(getResources().getColor(R.color.gray));
                }else{
                    isWednesdaySelected = true;
                    bottomBarOperatingDays.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                    BtnWednesday.setBackgroundResource(R.drawable.days_selected);
                    BtnWednesday.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        BtnThursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isThursdaySelected){
                    isThursdaySelected = false;
                    BtnThursday.setBackgroundResource(R.drawable.days_unselected);
                    BtnThursday.setTextColor(getResources().getColor(R.color.gray));
                }else{
                    isThursdaySelected = true;
                    bottomBarOperatingDays.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                    BtnThursday.setBackgroundResource(R.drawable.days_selected);
                    BtnThursday.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        BtnFriday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFridaySelected){
                    isFridaySelected = false;
                    BtnFriday.setBackgroundResource(R.drawable.days_unselected);
                    BtnFriday.setTextColor(getResources().getColor(R.color.gray));
                }else{
                    isFridaySelected = true;
                    bottomBarOperatingDays.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                    BtnFriday.setBackgroundResource(R.drawable.days_selected);
                    BtnFriday.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        BtnSaturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSaturdaySelected){
                    isSaturdaySelected = false;
                    BtnSaturday.setBackgroundResource(R.drawable.days_unselected);
                    BtnSaturday.setTextColor(getResources().getColor(R.color.gray));
                }else{
                    isSaturdaySelected = true;
                    bottomBarOperatingDays.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                    BtnSaturday.setBackgroundResource(R.drawable.days_selected);
                    BtnSaturday.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        ETOpenTime.setFocusable(false);
        ETCloseTime.setFocusable(false);

        ETOpenTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTimeOpen();
            }
        });

        ETOpenTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTimeOpen();
            }
        });

        ETCloseTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTimeClose();
            }
        });

        ETCloseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTimeClose();
            }
        });

        // Assigning viewModel to ShopOnBoardViewModel.
        viewModel = new ViewModelProvider(requireActivity()).get(ShopOnboardViewModel.class);

        //Observing shop data and setting data in EditTexts respectively.
        viewModel.getShopData().observe(getViewLifecycleOwner(), new Observer<ShopOnboardClass>() {
            @Override
            public void onChanged(ShopOnboardClass shopOnboardClass) {
                //Cloning ShopOnboardClass object into new object to add and update data in ViewModel.
                shopData = new ShopOnboardClass(shopOnboardClass);

                //Setting Data in CheckBoxes.
                if(shopData.getCategory().contains("General Store")){
                    CBGeneralStore.setChecked(true);
                }
                if(shopData.getCategory().contains("Pharmaceutics")){
                    CBPharmaceutics.setChecked(true);
                }
                if(shopData.getCategory().contains("Entertainment")){
                    CBEntertainment.setChecked(true);
                }
                if(shopData.getCategory().contains("Stationary/Bookstore")){
                    CBStationary.setChecked(true);
                }
                if(shopData.getCategory().contains("Home Appliances")){
                    CBHomeAppliances.setChecked(true);
                }
                if(shopData.getCategory().contains("Grocery")){
                    CBGrocery.setChecked(true);
                }
                if(shopData.getCategory().contains("Electronics")){
                    CBElectronics.setChecked(true);
                }
                if(shopData.getCategory().contains("Home Decor")){
                    CBHomeDecor.setChecked(true);
                }
                if(shopData.getCategory().contains("Personal Care")){
                    CBPersonalCare.setChecked(true);
                }
                if(shopData.getCategory().contains("Clothing")){
                    CBClothing.setChecked(true);
                }
                if(shopData.getCategory().contains("Footwear")){
                    CBFootwear.setChecked(true);
                }
                if(shopData.getCategory().contains("Jewelry")){
                    CBJewelry.setChecked(true);
                }
                if(shopData.getCategory().contains("Sports")){
                    CBSports.setChecked(true);
                }
                if(shopData.getCategory().contains("Baby Care")){
                    CBBabyCare.setChecked(true);
                }
                if(shopData.getCategory().contains("Pet Shop")){
                    CBPetShop.setChecked(true);
                }

                if(shopData.getOperatingDays().contains("Sunday")){
                    isSundaySelected = true;
                    BtnSunday.setBackgroundResource(R.drawable.days_selected);
                    BtnSunday.setTextColor(getResources().getColor(R.color.white));
                }
                if(shopData.getOperatingDays().contains("Monday")){
                    isMondaySelected = true;
                    BtnMonday.setBackgroundResource(R.drawable.days_selected);
                    BtnMonday.setTextColor(getResources().getColor(R.color.white));
                }
                if(shopData.getOperatingDays().contains("Tuesday")){
                    isTuesdaySelected = true;
                    BtnTuesday.setBackgroundResource(R.drawable.days_selected);
                    BtnTuesday.setTextColor(getResources().getColor(R.color.white));
                }
                if(shopData.getOperatingDays().contains("Wednesday")){
                    isWednesdaySelected = true;
                    BtnWednesday.setBackgroundResource(R.drawable.days_selected);
                    BtnWednesday.setTextColor(getResources().getColor(R.color.white));
                }
                if(shopData.getOperatingDays().contains("Thursday")){
                    isThursdaySelected = true;
                    BtnThursday.setBackgroundResource(R.drawable.days_selected);
                    BtnThursday.setTextColor(getResources().getColor(R.color.white));
                }
                if(shopData.getOperatingDays().contains("Friday")){
                    isFridaySelected = true;
                    BtnFriday.setBackgroundResource(R.drawable.days_selected);
                    BtnFriday.setTextColor(getResources().getColor(R.color.white));
                }
                if(shopData.getOperatingDays().contains("Saturday")){
                    isSaturdaySelected = true;
                    BtnSaturday.setBackgroundResource(R.drawable.days_selected);
                    BtnSaturday.setTextColor(getResources().getColor(R.color.white));
                }


                //Setting Data in EditTexts.
                ETOpenTime.setText(shopData.getOpenHour() + " : " + shopData.getOpenMinute());
                try{
                    hourOpen = Integer.parseInt(shopData.getOpenHour());
                } catch(NumberFormatException ex){ // handle your exception
                    ETOpenTime.setText("");
                }
                try{
                    minuteOpen = Integer.parseInt(shopData.getOpenMinute());
                } catch(NumberFormatException ex){ // handle your exception
                    ETOpenTime.setText("");
                }
                ETCloseTime.setText(shopData.getCloseHour() + " : " + shopData.getCloseMinute());
                try{
                    hourClose = Integer.parseInt(shopData.getCloseHour());
                } catch(NumberFormatException ex){ // handle your exception
                    ETCloseTime.setText("");
                }
                try{
                    minuteClose = Integer.parseInt(shopData.getCloseMinute());
                } catch(NumberFormatException ex){ // handle your exception
                    ETCloseTime.setText("");
                }
                ACTAnnualTurnover.setText(shopData.getAnnualTurnover());
                ACTNumberOfProducts.setText(shopData.getNumberOfProducts());
            }
        });

        ContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(!CBGeneralStore.isChecked() && !CBPharmaceutics.isChecked() && !CBEntertainment.isChecked() &&
                        !CBStationary.isChecked() && !CBHomeAppliances.isChecked() && !CBGrocery.isChecked() &&
                        !CBElectronics.isChecked() && !CBHomeDecor.isChecked() && !CBPersonalCare.isChecked() &&
                        !CBClothing.isChecked() && !CBFootwear.isChecked() && !CBJewelry.isChecked() &&
                        !CBSports.isChecked() && !CBBabyCare.isChecked() && !CBPetShop.isChecked())
                        && !(!isSundaySelected && !isMondaySelected && !isTuesdaySelected &&
                        !isWednesdaySelected && !isThursdaySelected && !isFridaySelected && !isSaturdaySelected)
                        && !ETOpenTime.getText().toString().trim().isEmpty() && !ETCloseTime.getText().toString().trim().isEmpty()
                        && !ACTAnnualTurnover.getText().toString().trim().isEmpty() && !ACTNumberOfProducts.getText().toString().trim().isEmpty()){

                    categoryList = new ArrayList<String>();

                    if(CBGeneralStore.isChecked()){
                        categoryList.add("General Store");
                    }
                    if(CBPharmaceutics.isChecked()){
                        categoryList.add("Pharmaceutics");
                    }
                    if(CBEntertainment.isChecked()){
                        categoryList.add("Entertainment");
                    }
                    if(CBStationary.isChecked()){
                        categoryList.add("Stationary/Bookstore");
                    }
                    if(CBHomeAppliances.isChecked()){
                        categoryList.add("Home Appliances");
                    }
                    if(CBGrocery.isChecked()){
                        categoryList.add("Grocery");
                    }
                    if(CBElectronics.isChecked()){
                        categoryList.add("Electronics");
                    }
                    if(CBHomeDecor.isChecked()){
                        categoryList.add("Home Decor");
                    }
                    if(CBPersonalCare.isChecked()){
                        categoryList.add("Personal Care");
                    }
                    if(CBClothing.isChecked()){
                        categoryList.add("Clothing");
                    }
                    if(CBFootwear.isChecked()){
                        categoryList.add("Footwear");
                    }
                    if(CBJewelry.isChecked()){
                        categoryList.add("Jewelry");
                    }
                    if(CBSports.isChecked()){
                        categoryList.add("Sports");
                    }
                    if(CBBabyCare.isChecked()){
                        categoryList.add("Baby Care");
                    }
                    if(CBPetShop.isChecked()){
                        categoryList.add("Pet Shop");
                    }

                    operatingDaysList = new ArrayList<String>();

                    if(isSundaySelected){
                        operatingDaysList.add("Sunday");
                    }
                    if(isMondaySelected){
                        operatingDaysList.add("Monday");
                    }
                    if(isTuesdaySelected){
                        operatingDaysList.add("Tuesday");
                    }
                    if(isWednesdaySelected){
                        operatingDaysList.add("Wednesday");
                    }
                    if(isThursdaySelected){
                        operatingDaysList.add("Thursday");
                    }
                    if(isFridaySelected){
                        operatingDaysList.add("Friday");
                    }
                    if(isSaturdaySelected){
                        operatingDaysList.add("Saturday");
                    }

                    shopData.setCategory(categoryList);
                    shopData.setOperatingDays(operatingDaysList);
                    shopData.setOpenHour(String.valueOf(hourOpen));
                    shopData.setOpenMinute(String.valueOf(minuteOpen));
                    shopData.setCloseHour(String.valueOf(hourClose));
                    shopData.setCloseMinute(String.valueOf(minuteClose));
                    shopData.setAnnualTurnover(ACTAnnualTurnover.getText().toString().trim());
                    shopData.setNumberOfProducts(ACTNumberOfProducts.getText().toString().trim());
                    viewModel.addShopData(shopData);
                    viewModel.setPositionSignUp(4);

                }else{
                    if((!CBGeneralStore.isChecked() && !CBPharmaceutics.isChecked() && !CBEntertainment.isChecked() &&
                            !CBEntertainment.isChecked() && !CBHomeAppliances.isChecked() && !CBGrocery.isChecked() &&
                            !CBElectronics.isChecked() && !CBHomeDecor.isChecked() && !CBPersonalCare.isChecked() &&
                            !CBClothing.isChecked() && !CBFootwear.isChecked() && !CBJewelry.isChecked() &&
                            !CBSports.isChecked() && !CBBabyCare.isChecked() && !CBPetShop.isChecked())){
                        bottomBarCategory.setBackgroundResource(R.drawable.error_bar);
                    }
                    if((!isSundaySelected && !isMondaySelected && !isTuesdaySelected &&
                            !isWednesdaySelected && !isThursdaySelected && !isFridaySelected && !isSaturdaySelected)){
                        bottomBarOperatingDays.setBackgroundResource(R.drawable.error_bar);
                    }
                    if(ETOpenTime.getText().toString().trim().isEmpty()){
                        bottomBarTimings.setBackgroundResource(R.drawable.error_bar);
                        changeStateTextInputLayout(ETOpenTimeLayout,"Red");
                    }
                    if(ETCloseTime.getText().toString().trim().isEmpty()){
                        bottomBarTimings.setBackgroundResource(R.drawable.error_bar);
                        changeStateTextInputLayout(ETCloseTimeLayout,"Red");
                    }
                    if(ACTAnnualTurnover.getText().toString().trim().isEmpty()){
                        changeStateTextInputLayout(ACTAnnualTurnoverLayout,"Red");
                    }
                    if(ACTNumberOfProducts.getText().toString().trim().isEmpty()){
                        changeStateTextInputLayout(ACTNumberOfProductsLayout,"Red");
                    }

                    Toast.makeText(getActivity(),"Please Enter all Fields",Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private void selectTimeOpen() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hourOpen = selectedHour;
                minuteOpen = selectedMinute;
                ETOpenTime.setText(String.format(Locale.getDefault(),"%02d : %02d",hourOpen,minuteOpen));

            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),R.style.TimePickerTheme,onTimeSetListener, hourOpen,minuteOpen, true );
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    private void selectTimeClose() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hourClose = selectedHour;
                minuteClose = selectedMinute;
                ETCloseTime.setText(String.format(Locale.getDefault(),"%02d : %02d",hourClose,minuteClose));

            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),R.style.TimePickerTheme,onTimeSetListener, hourClose,minuteClose, true );
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public void resetError(){
        resetErrorCheckBox(CBGeneralStore);
        resetErrorCheckBox(CBPharmaceutics);
        resetErrorCheckBox(CBEntertainment);
        resetErrorCheckBox(CBStationary);
        resetErrorCheckBox(CBHomeAppliances);
        resetErrorCheckBox(CBGrocery);
        resetErrorCheckBox(CBElectronics);
        resetErrorCheckBox(CBHomeDecor);
        resetErrorCheckBox(CBPersonalCare);
        resetErrorCheckBox(CBClothing);
        resetErrorCheckBox(CBFootwear);
        resetErrorCheckBox(CBJewelry);
        resetErrorCheckBox(CBBabyCare);
        resetErrorCheckBox(CBSports);
        resetErrorCheckBox(CBPetShop);


        ETOpenTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bottomBarTimings.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                changeStateTextInputLayout(ETOpenTimeLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETCloseTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bottomBarTimings.setBackgroundResource(R.drawable.shop_onboard_signup_background);
                changeStateTextInputLayout(ETCloseTimeLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ACTAnnualTurnover.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(ACTAnnualTurnoverLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ACTNumberOfProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(ACTNumberOfProductsLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

   public void resetErrorCheckBox(CheckBox checkBox){
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                bottomBarCategory.setBackgroundResource(R.drawable.shop_onboard_signup_background);
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

}
