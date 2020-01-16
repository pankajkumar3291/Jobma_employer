package com.jobma.employer.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityDashboard;
import com.jobma.employer.activities.ActivityLogin;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.exoPlayer.PlayerActivity;
import com.jobma.employer.exoPlayer.Sample;
import com.jobma.employer.exoPlayer.UriSample;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.companyProfile.EOCompanyData;
import com.jobma.employer.model.companyProfile.EOCompanyProfile;
import com.jobma.employer.model.companyProfile.EOCompanyVideo;
import com.jobma.employer.model.companyProfile.EOIndustryArea;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.EMPLOYEE_PHOTO;
import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentCompanyProfile extends Fragment implements View.OnClickListener {

    private View view;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private String companyImage;
    private ImageView videoView, playVideo;
    private CircleImageView circleImageView;
    private EOCompanyData companyData;
    private TextView tv_company_name, tv_about_company, tv_company_website, tv_company_email, tv_organization_type,
            tv_location, tv_address, tv_address2, tv_zipCode, tv_phone, tv_ex_number, tv_fax_number, tv_industry, tv_functional_area;
    private ArrayList<String> functionalList;
    private ArrayList<String> industryList;
    private EOCompanyVideo companyVideo;
    private String VIDEO_URL;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_company_profile, container, false);

        this.initView();
        this.setOnClickListener();

        return view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
        this.companyImage = loginPreferences.getString(EMPLOYEE_PHOTO, "");

        this.videoView = view.findViewById(R.id.videoView);
        this.circleImageView = view.findViewById(R.id.circleImageView);

        if (!ObjectUtil.isEmpty(this.companyImage))
            loadImages(this.companyImage, circleImageView);

        this.tv_company_name = view.findViewById(R.id.textView15);
        this.tv_about_company = view.findViewById(R.id.textView20);
        this.tv_company_website = view.findViewById(R.id.textView22);
        this.tv_company_email = view.findViewById(R.id.textView23);
        this.tv_organization_type = view.findViewById(R.id.textView24);
        this.tv_location = view.findViewById(R.id.textView25);
        this.tv_address = view.findViewById(R.id.textView26);
        this.tv_address2 = view.findViewById(R.id.textView27);
        this.tv_zipCode = view.findViewById(R.id.textView29);
        this.tv_phone = view.findViewById(R.id.textView253);
        this.tv_ex_number = view.findViewById(R.id.textView263);
        this.tv_fax_number = view.findViewById(R.id.textView273);
        this.tv_industry = view.findViewById(R.id.textView53);
        this.tv_functional_area = view.findViewById(R.id.textView63);
        this.playVideo = view.findViewById(R.id.imageView23);
    }

    private void setOnClickListener() {
        this.videoView.setOnClickListener(this);
        this.playVideo.setOnClickListener(this);
    }

    private void getCompanyVideo() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.getCompanyVideo(apiKey).enqueue(new Callback<EOCompanyVideo>() {
                @Override
                public void onResponse(Call<EOCompanyVideo> call, Response<EOCompanyVideo> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        companyVideo = response.body();
                        if (!ObjectUtil.isEmpty(companyVideo)) {
                            if (companyVideo.getError() == RESPONSE_SUCCESS) {
                                loadImages(companyVideo.getPath().getPoster(), videoView);

                                if (companyVideo.getPath().getHls().equalsIgnoreCase("")) { //TODO HLS BALANK THEN FILE URL
                                    VIDEO_URL = companyVideo.getPath().getFileurl();  //TODO FILE URL
                                } else if (!companyVideo.getPath().getHls().equalsIgnoreCase("")) { //TODO  NOT BLANK THEN HLS ITSELF
                                    VIDEO_URL = companyVideo.getPath().getHls(); //TODO  HLS URL
                                } else if (companyVideo.getPath().getHls().equalsIgnoreCase("") && companyVideo.getPath().getFileurl().equalsIgnoreCase("")) {
                                    VIDEO_URL = "";
                                }

                            } else {
                                Toast.makeText(getActivity(), "" + companyVideo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOCompanyVideo> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get()
                .load(imagePath)
                .fit()
                .into(imageView);
    }


    @Override
    public void onResume() {
        super.onResume();
        ((ActivityDashboard) getActivity()).checkFragmentVisibility("companyprofile");

        this.getWalletExpired();
        this.getCompanyVideo();
        this.getCompanyInfo();
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
                        Toast.makeText(getActivity(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showNoCreditWalletDialog(String dialogMessage) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
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
                    Intent loginIntent = new Intent(getActivity(), ActivityLogin.class);
                    getActivity().startActivity(loginIntent);
                    getActivity().finish();
                }
            }
        });
        dialog.show();
    }

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
                                Toast.makeText(getActivity(), "" + companyProfile.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOCompanyProfile> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void dataToView() {
        if (!ObjectUtil.isEmpty(companyData)) {
            tv_company_name.setText("Company Name : ".concat(companyData.getCompany().getCompany()));
            tv_about_company.setText(companyData.getCompany().getAboutCompany());
            tv_company_website.setText(companyData.getCompany().getEmployerEmail());
            tv_company_email.setText(companyData.getCompany().getCompanyEmail());
            if (companyData.getCompany().getOrganisation().equalsIgnoreCase("1"))
                tv_organization_type.setText("Organization Type : Corporation");
            else
                tv_organization_type.setText("Organization Type : Recruiting Firm");

            tv_location.setText(companyData.getCompany().getCountryName() + "," + companyData.getCompany().getStateName() + "," + companyData.getCompany().getCityName());
            tv_address.setText(companyData.getCompany().getAddress());
            tv_address2.setText(companyData.getCompany().getAddress2());
            tv_zipCode.setText(companyData.getCompany().getZip());
            tv_phone.setText(companyData.getCompany().getPhone());
            tv_ex_number.setText(companyData.getCompany().getExt());
            tv_fax_number.setText(companyData.getCompany().getFax());

            if (industryList == null) {
                industryList = new ArrayList<>();
            } else {
                industryList.clear();
            }

            if (functionalList == null) {
                functionalList = new ArrayList<>();
            } else {
                functionalList.clear();
            }

            for (EOIndustryArea industryArea : companyData.getIndustry()) {
                industryList.add(industryArea.getTitle());
            }
            tv_industry.setText(TextUtils.join(" , ", industryList));

            for (EOIndustryArea functionArea : companyData.getFunctionalArea()) {
                functionalList.add(functionArea.getTitle());
            }
            tv_functional_area.setText(TextUtils.join(" , ", functionalList));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageView23) {

            if (VIDEO_URL != null) {
                if (VIDEO_URL.equalsIgnoreCase("")) {
                    Toast.makeText(getActivity(), "No Video Found", Toast.LENGTH_LONG).show();
                } else {
                    Sample sample = new UriSample("Super speed (PlayReady)", null, Uri.parse(VIDEO_URL), null, null, null);
                    startActivity(sample.buildIntent(getActivity(), false, PlayerActivity.ABR_ALGORITHM_DEFAULT));
                }
            } else {
                Toast.makeText(getActivity(), "Please upload the video first", Toast.LENGTH_LONG).show();
            }
        }
    }


}
