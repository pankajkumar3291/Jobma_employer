package com.jobma.employer.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.MultiSelectionSpinner;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.companyProfile.EOCompanyData;
import com.jobma.employer.model.companyProfile.EOCompanyProfile;
import com.jobma.employer.model.companyProfile.EOCountry;
import com.jobma.employer.model.companyProfile.EOCountryData;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.GlobalUtil;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;

import java.util.ArrayList;
import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class ActivityEditCompanyProfile extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private Toolbar toolbar2;
    private EditText et_company_name, et_website_url, et_enter_about_company, et_company_email, et_address,
            et_address2, et_zipcode, et_phone, et_ex_number, et_fax;
    private Spinner spinner_country, spinner_state, spinner_city;
    private MultiSelectionSpinner spinner_industry_type, spinner_functional_area;
    private RadioGroup radioGroup_organitation_type;
    private RadioButton radio_corporate, radio_recruting_firm;
    private Button btn_save;
    private EOCompanyData companyData;
    private String organizationType;
    private ArrayList<EOCountryData> countryDataArrayList;
    private ArrayList<EOCountryData> stateDataArrayList;
    private ArrayList<EOCountryData> cityDataArrayList;
    private ArrayList<String> countryList;
    private ArrayList<String> stateList;
    private ArrayList<String> cityList;

    private ArrayList<String> industryList = new ArrayList<>();
    private ArrayList<String> functionalAreaList = new ArrayList<>();
    private String selectedCountry, selectedState, selectedCity;
    private Integer country_id, state_id, city_id;
    private ArrayAdapter<String> countryAdapter;
    private ArrayAdapter<String> stateAdapter;
    private ArrayAdapter<String> cityAdapter;
    private ArrayList<EOCountryData> industryObjList = new ArrayList<>();
    private ArrayList<EOCountryData> functionalObjList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_company_profile);

        this.initView();
        this.setOnClickListener();
        //TODO check wallet expired subscription
        this.getWalletExpired();
        this.getCompanyInfo();
        this.getCountryApi();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.toolbar2 = this.findViewById(R.id.toolbar2);
        this.toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityEditCompanyProfile.this.finish();
            }
        });

        this.et_company_name = this.findViewById(R.id.editText);
        this.et_website_url = this.findViewById(R.id.editText2);
        this.spinner_industry_type = this.findViewById(R.id.spinner);
        this.spinner_functional_area = this.findViewById(R.id.spinner2);
        this.radioGroup_organitation_type = this.findViewById(R.id.radiogroup);
        this.radio_corporate = this.findViewById(R.id.radio_corporate);
        this.radio_recruting_firm = this.findViewById(R.id.radio_recruting_firm);
        this.et_enter_about_company = this.findViewById(R.id.editText4);
        this.et_company_email = this.findViewById(R.id.editText5);
        this.et_address = this.findViewById(R.id.editText6);
        this.et_address2 = this.findViewById(R.id.editText7);
        this.spinner_country = this.findViewById(R.id.spinner4);
        this.spinner_state = this.findViewById(R.id.spinner5);
        this.spinner_city = this.findViewById(R.id.spinner6);
        this.et_zipcode = this.findViewById(R.id.editText9);
        this.et_phone = this.findViewById(R.id.editText10);
        this.et_ex_number = this.findViewById(R.id.editText12);
        this.et_fax = this.findViewById(R.id.editText13);
        this.btn_save = this.findViewById(R.id.button);
    }

    private void setOnClickListener() {
        this.radioGroup_organitation_type.setOnCheckedChangeListener(this.onCheckedChangeListener);
        this.btn_save.setOnClickListener(this);
    }

    private void getWalletExpired() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.getWalletExpiry(apiKey).enqueue(new Callback<EOForgetPassword>() {
                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword walletExpire = response.body();
                        if (!ObjectUtil.isEmpty(walletExpire)) {
                            if (walletExpire.getError() == RESPONSE_SUCCESS) {

                            } else {
                                //Toast.makeText(ActivityDashboard.this, "" + walletExpire.getMessage(), Toast.LENGTH_SHORT).show();
                                //TODO in case error 1 show popup for expiry wallet and logout from app
                                showNoCreditWalletDialog(walletExpire.getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityEditCompanyProfile.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showNoCreditWalletDialog(String dialogMessage) {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_subscription_expiray);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.bg_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        Button dialogBtn_logout = dialog.findViewById(R.id.button21);
        TextView message = dialog.findViewById(R.id.textView164);
        ImageView imgtik = dialog.findViewById(R.id.imageView68);
        imgtik.setImageResource(R.drawable.ic_cross);
        message.setText(dialogMessage);
        dialogBtn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //TODO when user is logout out then clear the login shared preferences
                if (loginPreferences.contains(SELECTED_API_KEY)) {
                    loginPreferences.edit().clear().apply();
                    Intent loginIntent = new Intent(ActivityEditCompanyProfile.this, ActivityLogin.class);
                    ActivityEditCompanyProfile.this.startActivity(loginIntent);
                    ActivityEditCompanyProfile.this.finish();
                }
            }
        });
        dialog.show();
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            switch (checkedRadioButton.getId()) {
                case R.id.radio_corporate:
                    organizationType = "1";
                    return;
                case R.id.radio_recruting_firm:
                    organizationType = "2";
            }
        }
    };

    private void getCompanyInfo() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.getCompanyInfo(apiKey).enqueue(new Callback<EOCompanyProfile>() {
                @Override
                public void onResponse(Call<EOCompanyProfile> call, Response<EOCompanyProfile> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOCompanyProfile companyProfile = response.body();
                        if (!ObjectUtil.isEmpty(companyProfile)) {
                            if (companyProfile.getError() == RESPONSE_SUCCESS) {
                                companyData = companyProfile.getData();
                                dataToView();
                            } else {
                                Toast.makeText(ActivityEditCompanyProfile.this, "" + companyProfile.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOCompanyProfile> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityEditCompanyProfile.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void dataToView() {
        if (!ObjectUtil.isEmpty(companyData)) {
            et_company_name.setText(companyData.getCompany().getCompany());
            et_website_url.setText(companyData.getCompany().getWebsite());

            this.getIndustryTypeApi();
            this.getFunctionalAreaApi();

            if (companyData.getCompany().getOrganisation().equalsIgnoreCase("1"))
                radio_corporate.setChecked(true);
            else
                radio_recruting_firm.setChecked(true);

            et_enter_about_company.setText(companyData.getCompany().getAboutCompany());
            et_company_email.setText(companyData.getCompany().getCompanyEmail());
            et_address.setText(companyData.getCompany().getAddress());
            et_address2.setText(companyData.getCompany().getAddress2());
            et_zipcode.setText(companyData.getCompany().getZip());
            et_phone.setText(companyData.getCompany().getPhone());
            et_ex_number.setText(companyData.getCompany().getExt());
            et_fax.setText(companyData.getCompany().getFax());
        }
    }

    private void getCountryApi() {
        progress.showProgressBar();
        apiInterface.getCountryApi().enqueue(new Callback<EOCountry>() {
            @Override
            public void onResponse(Call<EOCountry> call, Response<EOCountry> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EOCountry eoCountry = response.body();
                    if (!ObjectUtil.isEmpty(eoCountry)) {
                        if (eoCountry.getError() == RESPONSE_SUCCESS) {

                            if (countryDataArrayList == null) {
                                countryDataArrayList = new ArrayList<>();
                            } else {
                                countryDataArrayList.clear();
                            }
                            countryDataArrayList = (ArrayList<EOCountryData>) eoCountry.getData();

                            if (countryList == null) {
                                countryList = new ArrayList<>();
                            } else {
                                countryList.clear();
                            }

                            for (EOCountryData countryData : eoCountry.getData()) {
                                countryList.add(countryData.getName());
                            }

                            countryAdapter = new ArrayAdapter<>(ActivityEditCompanyProfile.this, R.layout.spinner_item, countryList);
                            countryAdapter.setDropDownViewResource(R.layout.spinner_item);
                            spinner_country.setAdapter(countryAdapter);
                            spinner_country.setOnItemSelectedListener(onCountrySelectedListener);

                            for (EOCountryData countryData : eoCountry.getData()) {
                                if (!ObjectUtil.isEmpty(companyData)) {
                                    if (countryData.getId().equals(companyData.getCompany().getCountry())) {
                                        int selectedPos = countryAdapter.getPosition(countryData.getName());
                                        spinner_country.setSelection(selectedPos);
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(ActivityEditCompanyProfile.this, "" + eoCountry.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EOCountry> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(ActivityEditCompanyProfile.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    AdapterView.OnItemSelectedListener onCountrySelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedCountry = (String) parent.getItemAtPosition(position);
            for (EOCountryData eoCountry : countryDataArrayList) {
                if (eoCountry.getName().equalsIgnoreCase(selectedCountry)) {
                    country_id = eoCountry.getId();
                }
            }
            //TODO from here we are getting states api
            getStatesApi();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void getStatesApi() {
        progress.showProgressBar();
        apiInterface.getStateApi("states/" + country_id).enqueue(new Callback<EOCountry>() {
            @Override
            public void onResponse(Call<EOCountry> call, Response<EOCountry> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EOCountry eoSates = response.body();
                    if (!ObjectUtil.isEmpty(eoSates)) {
                        if (eoSates.getError() == RESPONSE_SUCCESS) {
                            if (stateDataArrayList == null) {
                                stateDataArrayList = new ArrayList<>();
                            } else {
                                stateDataArrayList.clear();
                            }
                            stateDataArrayList = (ArrayList<EOCountryData>) eoSates.getData();

                            if (stateList == null) {
                                stateList = new ArrayList<>();
                            } else {
                                stateList.clear();
                            }

                            for (EOCountryData countryData : eoSates.getData()) {
                                stateList.add(countryData.getName());
                            }

                            stateAdapter = new ArrayAdapter<>(ActivityEditCompanyProfile.this, R.layout.spinner_item, stateList);
                            stateAdapter.setDropDownViewResource(R.layout.spinner_item);
                            spinner_state.setAdapter(stateAdapter);
                            spinner_state.setOnItemSelectedListener(onStateSelectedListener);

                            for (EOCountryData countryData : eoSates.getData()) {
                                if (!ObjectUtil.isEmpty(companyData)) {
                                    if (countryData.getId().equals(companyData.getCompany().getState())) {
                                        int selectedPos = stateAdapter.getPosition(countryData.getName());
                                        spinner_state.setSelection(selectedPos);
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(ActivityEditCompanyProfile.this, "" + eoSates.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EOCountry> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(ActivityEditCompanyProfile.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    AdapterView.OnItemSelectedListener onStateSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedState = (String) parent.getItemAtPosition(position);
            for (EOCountryData eoCountry : stateDataArrayList) {
                if (eoCountry.getName().equalsIgnoreCase(selectedState)) {
                    state_id = eoCountry.getId();
                }
            }
            //TODO from here we are getting city api
            getCityApi();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void getCityApi() {
        progress.showProgressBar();
        apiInterface.getCityApi("cities/" + state_id).enqueue(new Callback<EOCountry>() {
            @Override
            public void onResponse(Call<EOCountry> call, Response<EOCountry> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EOCountry eoCity = response.body();
                    if (!ObjectUtil.isEmpty(eoCity)) {
                        if (eoCity.getError() == RESPONSE_SUCCESS) {
                            if (cityDataArrayList == null) {
                                cityDataArrayList = new ArrayList<>();
                            } else {
                                cityDataArrayList.clear();
                            }
                            cityDataArrayList = (ArrayList<EOCountryData>) eoCity.getData();

                            if (cityList == null) {
                                cityList = new ArrayList<>();
                            } else {
                                cityList.clear();
                            }

                            for (EOCountryData countryData : eoCity.getData()) {
                                cityList.add(countryData.getName());
                            }

                            cityAdapter = new ArrayAdapter<>(ActivityEditCompanyProfile.this, R.layout.spinner_item, cityList);
                            cityAdapter.setDropDownViewResource(R.layout.spinner_item);
                            spinner_city.setAdapter(cityAdapter);
                            spinner_city.setOnItemSelectedListener(onCitySelectedListener);

                            for (EOCountryData countryData : eoCity.getData()) {
                                if (!ObjectUtil.isEmpty(companyData)) {
                                    if (countryData.getId().equals(companyData.getCompany().getCity())) {
                                        int selectedPos = cityAdapter.getPosition(countryData.getName());
                                        spinner_city.setSelection(selectedPos);
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(ActivityEditCompanyProfile.this, "" + eoCity.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EOCountry> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(ActivityEditCompanyProfile.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    AdapterView.OnItemSelectedListener onCitySelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedCity = (String) parent.getItemAtPosition(position);
            for (EOCountryData eoCity : cityDataArrayList) {
                if (eoCity.getName().equalsIgnoreCase(selectedCity)) {
                    city_id = eoCity.getId();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void getIndustryTypeApi() {
        progress.showProgressBar();
        apiInterface.getIndustrialArea().enqueue(new Callback<EOCountry>() {
            @Override
            public void onResponse(Call<EOCountry> call, Response<EOCountry> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EOCountry eoIndustry = response.body();
                    if (!ObjectUtil.isEmpty(eoIndustry)) {
                        if (eoIndustry.getError() == RESPONSE_SUCCESS) {

                            industryObjList.addAll(eoIndustry.getData());

                            for (EOCountryData industryData : eoIndustry.getData()) {
                                industryList.add(industryData.getName());
                            }

                            spinner_industry_type.setItems(industryList);

                            //TODO by default selected industry type
                            String[] array = new String[companyData.getIndustry().size()];
                            for (int i = 0; i < companyData.getIndustry().size(); i++) {
                                array[i] = companyData.getIndustry().get(i).getTitle();
                            }
                            spinner_industry_type.setSelection(array);

                        } else {
                            Toast.makeText(ActivityEditCompanyProfile.this, "" + eoIndustry.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EOCountry> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(ActivityEditCompanyProfile.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getFunctionalAreaApi() {
        progress.showProgressBar();
        apiInterface.getFunctionalArea().enqueue(new Callback<EOCountry>() {
            @Override
            public void onResponse(Call<EOCountry> call, Response<EOCountry> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EOCountry eoFunctional = response.body();
                    if (!ObjectUtil.isEmpty(eoFunctional)) {
                        if (eoFunctional.getError() == RESPONSE_SUCCESS) {

                            functionalObjList.addAll(eoFunctional.getData());

                            for (EOCountryData industryData : eoFunctional.getData()) {
                                functionalAreaList.add(industryData.getName());
                            }

                            spinner_functional_area.setItems(functionalAreaList);

                            //TODO by default selected functional area type
                            String[] array = new String[companyData.getFunctionalArea().size()];
                            for (int i = 0; i < companyData.getFunctionalArea().size(); i++) {
                                array[i] = companyData.getFunctionalArea().get(i).getTitle();
                            }
                            spinner_functional_area.setSelection(array);

                        } else {
                            Toast.makeText(ActivityEditCompanyProfile.this, "" + eoFunctional.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EOCountry> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(ActivityEditCompanyProfile.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        ArrayList<Integer> integersIndustryList = getIndustryIds();
        ArrayList<Integer> integersFunctionalList = getFunctionalIds();

        if (v.getId() == R.id.button) {
            if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_company_email))) {
                Toast.makeText(this, "Please enter company mail", Toast.LENGTH_SHORT).show();
            } else if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_company_name))) {
                Toast.makeText(this, "Please enter company name", Toast.LENGTH_SHORT).show();
            } else if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_website_url))) {
                Toast.makeText(this, "Please enter company website", Toast.LENGTH_SHORT).show();
            } else if (!GlobalUtil.isValidUrl(ObjectUtil.getTextFromView(et_website_url))) {
                Toast.makeText(this, "Please enter valid web site", Toast.LENGTH_SHORT).show();
            } else if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_address))) {
                Toast.makeText(this, "Please enter address", Toast.LENGTH_SHORT).show();
            } else if (ObjectUtil.isEmpty(organizationType)) {
                Toast.makeText(this, "Please select organization type", Toast.LENGTH_SHORT).show();
            } else if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_enter_about_company))) {
                Toast.makeText(this, "Please enter about company", Toast.LENGTH_SHORT).show();
            } else if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_zipcode))) {
                Toast.makeText(this, "Please enter zip code", Toast.LENGTH_SHORT).show();
            } else if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_phone))) {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show();
            } else {
                this.updateCompanyInfo(integersIndustryList, integersFunctionalList);
            }
        }
    }

    //TODO this method is used to get ids of industry's when user check industry dialog
    private ArrayList<Integer> getIndustryIds() {
        ArrayList<Integer> industryList = new ArrayList<>();
        LinkedList<String> selectedList = (LinkedList<String>) spinner_industry_type.getSelectedStrings();
        for (EOCountryData countryData : industryObjList) {
            for (String str : selectedList) {
                if (countryData.getName().equalsIgnoreCase(str)) {
                    industryList.add(countryData.getId());
                }
            }
        }
        return industryList;
    }

    //TODO this method is used to get ids of industry's when user check functional dialog
    private ArrayList<Integer> getFunctionalIds() {
        ArrayList<Integer> functionalList = new ArrayList<>();
        LinkedList<String> selectedList = (LinkedList<String>) spinner_functional_area.getSelectedStrings();
        for (EOCountryData countryData : functionalObjList) {
            for (String str : selectedList) {
                if (countryData.getName().equalsIgnoreCase(str)) {
                    functionalList.add(countryData.getId());
                }
            }
        }
        return functionalList;
    }

    private void updateCompanyInfo(ArrayList<Integer> integersIndustryList, ArrayList<Integer> integersFunctionalList) {
        if (!ObjectUtil.isEmpty(apiKey)) {
            progress.showProgressBar();
            apiInterface.updateCompanyInfo(apiKey, ObjectUtil.getTextFromView(et_company_name), ObjectUtil.getTextFromView(et_website_url), ObjectUtil.getTextFromView(et_company_email), organizationType,
                    ObjectUtil.getTextFromView(et_address), ObjectUtil.getTextFromView(et_address2), String.valueOf(country_id),
                    String.valueOf(state_id), String.valueOf(city_id), ObjectUtil.getTextFromView(et_zipcode), ObjectUtil.getTextFromView(et_phone), ObjectUtil.getTextFromView(et_fax),
                    ObjectUtil.getTextFromView(et_ex_number), ObjectUtil.getTextFromView(et_enter_about_company), TextUtils.join(",", integersIndustryList),
                    TextUtils.join(",", integersFunctionalList)).enqueue(new Callback<EOForgetPassword>() {

                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword updateInfoObj = response.body();
                        if (!ObjectUtil.isEmpty(updateInfoObj)) {
                            if (updateInfoObj.getError() == RESPONSE_SUCCESS) {
                                Toast.makeText(ActivityEditCompanyProfile.this, "" + updateInfoObj.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent homeIntent = new Intent(ActivityEditCompanyProfile.this, ActivityDashboard.class);
                                startActivity(homeIntent);
                                ActivityEditCompanyProfile.this.finish();
                            } else {
                                //Toast.makeText(ActivityEditCompanyProfile.this, "" + updateInfoObj.getMessage(), Toast.LENGTH_SHORT).show();
                                showNoDataFoundDialog(updateInfoObj.getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityEditCompanyProfile.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void showNoDataFoundDialog(String dialogMessage) {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_invitation);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.bg_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        Button dialogBtn_cancel = dialog.findViewById(R.id.button21);
        TextView message = dialog.findViewById(R.id.textView164);
        ImageView imgtik = dialog.findViewById(R.id.imageView68);
        imgtik.setImageResource(R.drawable.ic_cross);
        message.setText(dialogMessage);

        dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ActivityEditCompanyProfile.this.finish();
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        this.noNet.RegisterNoNet();
        super.onResume();
    }


    @Override
    protected void onPause() {
        this.noNet.unRegisterNoNet();
        super.onPause();
    }
}
