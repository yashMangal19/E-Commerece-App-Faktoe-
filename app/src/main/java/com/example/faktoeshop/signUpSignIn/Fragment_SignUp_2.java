package com.example.faktoeshop.signUpSignIn;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.faktoeshop.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Fragment_SignUp_2 extends Fragment {
    // Initialising Views
    TextView TVSignUpHeading2;
    AutoCompleteTextView stateSpinner, districtSpinner;
    EditText ETPostcode, ETAddress;
    Button ContinueBtn;
    TextInputLayout ETPostcodeLayout, ETAddressLayout, stateSpinnerLayout, districtSpinnerLayout;
    private ArrayAdapter<CharSequence> stateAdapter, districtAdapter;

    View v;

    // creating a variable for our string.
    // Initialising variables for filling up State and City spinners
    private String pinCode;
    private String selectedState , selectedDistrict;

    // creating a variable for request queue.
    private RequestQueue mRequestQueue;

    //Initializing shopOnboardClass to store data in viewModel.
    ShopOnboardClass shopData;

    //Initializing ViewModel
    ShopOnboardViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sign_up_2, container,false);

        // Assigning variables to their respective views

        TVSignUpHeading2 = v.findViewById(R.id.fragment_2_heading);

        // Coloring a part of the Heading as BLUE using SpannableString

        String txt = "Shop Address Details";
        SpannableString ss = new SpannableString(txt);

        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(getResources().getColor(R.color.blue));
        ss.setSpan(fcsBlue, 0, 4, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        TVSignUpHeading2.setText(ss);


        //Assigning Views and Adapter for Spinners

        stateSpinner = v.findViewById(R.id.EditText_State);
        stateAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.array_indian_states,R.layout.spinner_layout);

        //Setting up adapter with simple spinner dropdown item layout.
        //Setting adapter to the spinner.
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        // Filling up City Spinners once a state is selected in State Spinner

        districtSpinner = v.findViewById(R.id.EditText_City);

        stateSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                districtSpinner.setText("");
                selectedState = parent.getItemAtPosition(i).toString();

                switch(selectedState){
                    case "Andhra Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_andhra_pradesh_districts, R.layout.spinner_layout);
                        break;
                    case "Arunachal Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_arunachal_pradesh_districts, R.layout.spinner_layout);
                        break;
                    case "Assam": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_assam_districts, R.layout.spinner_layout);
                        break;
                    case "Bihar": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_bihar_districts, R.layout.spinner_layout);
                        break;
                    case "Chhattisgarh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_chhattisgarh_districts, R.layout.spinner_layout);
                        break;
                    case "Goa": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_goa_districts, R.layout.spinner_layout);
                        break;
                    case "Gujarat": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_gujarat_districts, R.layout.spinner_layout);
                        break;
                    case "Haryana": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_haryana_districts, R.layout.spinner_layout);
                        break;
                    case "Himachal Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_himachal_pradesh_districts, R.layout.spinner_layout);
                        break;
                    case "Jharkhand": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_jharkhand_districts, R.layout.spinner_layout);
                        break;
                    case "Karnataka": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_karnataka_districts, R.layout.spinner_layout);
                        break;
                    case "Kerala": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_kerala_districts, R.layout.spinner_layout);
                        break;
                    case "Madhya Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_madhya_pradesh_districts, R.layout.spinner_layout);
                        break;
                    case "Maharashtra": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_maharashtra_districts, R.layout.spinner_layout);
                        break;
                    case "Manipur": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_manipur_districts, R.layout.spinner_layout);
                        break;
                    case "Meghalaya": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_meghalaya_districts, R.layout.spinner_layout);
                        break;
                    case "Mizoram": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_mizoram_districts, R.layout.spinner_layout);
                        break;
                    case "Nagaland": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_nagaland_districts, R.layout.spinner_layout);
                        break;
                    case "Odisha": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_odisha_districts, R.layout.spinner_layout);
                        break;
                    case "Punjab": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_punjab_districts, R.layout.spinner_layout);
                        break;
                    case "Rajasthan": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_rajasthan_districts, R.layout.spinner_layout);
                        break;
                    case "Sikkim": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_sikkim_districts, R.layout.spinner_layout);
                        break;
                    case "Tamil Nadu": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_tamil_nadu_districts, R.layout.spinner_layout);
                        break;
                    case "Telangana": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_telangana_districts, R.layout.spinner_layout);
                        break;
                    case "Tripura": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_tripura_districts, R.layout.spinner_layout);
                        break;
                    case "Uttar Pradesh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_uttar_pradesh_districts, R.layout.spinner_layout);
                        break;
                    case "Uttarakhand": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_uttarakhand_districts, R.layout.spinner_layout);
                        break;
                    case "West Bengal": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_west_bengal_districts, R.layout.spinner_layout);
                        break;
                    case "Andaman and Nicobar Islands": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_andaman_nicobar_districts, R.layout.spinner_layout);
                        break;
                    case "Chandigarh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_chandigarh_districts, R.layout.spinner_layout);
                        break;
                    case "Dadra and Nagar Haveli": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_dadra_nagar_haveli_districts, R.layout.spinner_layout);
                        break;
                    case "Daman and Diu": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_daman_diu_districts, R.layout.spinner_layout);
                        break;
                    case "Delhi": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_delhi_districts, R.layout.spinner_layout);
                        break;
                    case "Jammu and Kashmir": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_jammu_kashmir_districts, R.layout.spinner_layout);
                        break;
                    case "Lakshadweep": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_lakshadweep_districts, R.layout.spinner_layout);
                        break;
                    case "Ladakh": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_ladakh_districts, R.layout.spinner_layout);
                        break;
                    case "Puducherry": districtAdapter = ArrayAdapter.createFromResource(parent.getContext(),
                            R.array.array_puducherry_districts, R.layout.spinner_layout);
                        break;
                    default:  break;
                }

                //Setting up adapter with simple spinner dropdown item layout.
                //Setting adapter to the spinner.
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                districtSpinner.setAdapter(districtAdapter);

            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assigning variables to their respective views.
        ETPostcode = v.findViewById(R.id.EditText_PinCode);
        ETAddress = v.findViewById(R.id.EditText_Address);
        ContinueBtn = v.findViewById(R.id.ContinueBtn_SignUp_2);

        ETPostcodeLayout = v.findViewById(R.id.EditText_PinCode_Layout);
        ETAddressLayout = v.findViewById(R.id.EditText_Address_Layout);
        stateSpinnerLayout = v.findViewById(R.id.EditText_State_Layout);
        districtSpinnerLayout = v.findViewById(R.id.EditText_City_Layout);

        //Resetting error background on text changed in the editText.
        resetError();

        // initializing our request que variable with request
        // queue and passing our context to it.
        mRequestQueue = Volley.newRequestQueue(requireActivity());

        // Assigning viewModel to ShopOnBoardViewModel.
        viewModel = new ViewModelProvider(requireActivity()).get(ShopOnboardViewModel.class);

        //Observing shop data and setting data in EditTexts respectively.
        viewModel.getShopData().observe(getViewLifecycleOwner(), new Observer<ShopOnboardClass>() {
            @Override
            public void onChanged(ShopOnboardClass shopOnboardClass) {
                //Cloning ShopOnboardClass object into new object to add and update data in ViewModel.
                shopData = new ShopOnboardClass(shopOnboardClass);

                //Setting Data in EditTexts.
                ETAddress.setText(shopData.getAddress());
                stateSpinner.setText(shopData.getState());
                districtSpinner.setText(shopData.getCity());
                ETPostcode.setText(shopData.getPostcode());
            }
        });

        /*
        Checking if any EditText is empty.
        Checking if Pin Code entered match with the district entered.
        Updating data in ShopData in ViewModel and changing the fragment.
         */
        ContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ETAddress.getText().toString().trim().isEmpty() && !stateSpinner.getText().toString().trim().isEmpty()
                        && !districtSpinner.getText().toString().trim().isEmpty() && !ETPostcode.getText().toString().trim().isEmpty()){

                    // getting string from EditText.
                    pinCode = ETPostcode.getText().toString().trim();

                    //Checking if Pin Code entered match with the district entered.
                    getDataFromPinCode(pinCode);

                }else{
                     /*
                    If any EditText is empty setting error background of that EditText.
                     */

                    // If Name EditText is Empty.
                    if(ETAddress.getText().toString().trim().isEmpty()){

                        //Setting background border and hint color to red.
                        changeStateTextInputLayout(ETAddressLayout,"Red");
                    }

                    //If Phone Number EditText is empty.
                    if(stateSpinner.getText().toString().trim().isEmpty()){

                        //Setting background border and hint color to red.
                        changeStateTextInputLayout(stateSpinnerLayout,"Red");
                    }

                    //If Email EditText is empty.
                    if(districtSpinner.getText().toString().trim().isEmpty()){

                        //Setting background border and hint color to red.
                        changeStateTextInputLayout(districtSpinnerLayout,"Red");
                    }

                    //If Business name EditText is empty.
                    if(ETPostcode.getText().toString().trim().isEmpty()){

                        //Setting background border and hint color to red.
                        changeStateTextInputLayout(ETPostcodeLayout,"Red");
                    }

                    //Toast to alert user to fill all fields.
                    Toast.makeText(getActivity(),"Please Enter all Fields",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void getDataFromPinCode(String pinCode) {

        // clearing our cache of request queue.
        mRequestQueue.getCache().clear();

        // below is the url from where we will be getting
        // our response in the json format.
        String url = "https://api.data.gov.in/resource/6176ee09-3d56-4a3b-8115-21841576b2f6?api-key=579b464db66ec23bdd000001cdd3946e44ce4aad7209ff7b23ac571b&format=json&offset=0&limit=1&filters[pincode]=" + pinCode;

        // below line is use to initialize our request queue.
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        // in below line we are creating a
        // object request using volley.
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // inside this method we will get two methods
                // such as on response method
                // inside on response method we are extracting
                // data from the json format.
                try {
                    // we are getting data of post office
                    // in the form of JSON file.
                    JSONArray postOfficeArray = response.getJSONArray("records");
                    if (response.getString("count").equals("0")) {
                        // validating if the response status is success or failure.
                        // in this method the response status is having error and
                        // we are setting text to TextView as invalid pincode.
                        changeStateTextInputLayout(ETPostcodeLayout,"Red");
                        Toast.makeText(requireActivity(), "Pin code is not valid.", Toast.LENGTH_SHORT).show();
                    } else {
                        // if the status is success we are calling this method
                        // in which we are getting data from post office object
                        // here we are calling first object of our json array.
                        JSONObject obj = postOfficeArray.getJSONObject(0);

                        // inside our json array we are getting district name,
                        // state and country from our data.
                        String district = obj.getString("districtname");
                        boolean isValid = district.toLowerCase().trim().equals(districtSpinner.getText().toString().toLowerCase());
                        boolean isStateValid = false;
                        for( String s : getResources().getStringArray(R.array.array_indian_states)){
                            if(s.equals(stateSpinner.getText().toString())){
                                isStateValid = true;
                                break;
                            }
                        }

                        if(isValid && isStateValid){

                            //Updating data in ShopOnboardClass and then pushing it to ViewModel.
                            shopData.setAddress(ETAddress.getText().toString());
                            shopData.setState(stateSpinner.getText().toString());
                            shopData.setCity(districtSpinner.getText().toString());
                            shopData.setPostcode(ETPostcode.getText().toString());
                            viewModel.addShopData(shopData);

                            //Changing the fragment.
                            viewModel.setPositionSignUp(3);
                        }
                        else{

                            //If PinCode entered does not matches with the district entered.
                            changeStateTextInputLayout(ETPostcodeLayout,"Red");

                            //Toast to alert user to enter correct phone number.
                            Toast.makeText(getActivity(),"Postcode does not match with State or District",Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (JSONException e) {
                    // if we gets any error then it
                    // will be printed in log cat.
                    e.printStackTrace();
                    changeStateTextInputLayout(ETPostcodeLayout,"Red");
                    Toast.makeText(requireActivity(), "Pin code is not valid.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // below method is called if we get
                // any error while fetching data from API.
                // below line is use to display an error message.
                changeStateTextInputLayout(ETPostcodeLayout,"Red");
                Toast.makeText(requireActivity(), "Pin code is not valid.", Toast.LENGTH_SHORT).show();
            }
        });
        // below line is use for adding object
        // request to our request queue.
        queue.add(objectRequest);

    }

    /*
    Method to reset error backgrounds on text changed.
    Setting background to normal editText and hint color to blue.
     */
    public void resetError(){
        ETAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(ETAddressLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        stateSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(stateSpinnerLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        districtSpinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(districtSpinnerLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ETPostcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                changeStateTextInputLayout(ETPostcodeLayout,"Blue");
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
