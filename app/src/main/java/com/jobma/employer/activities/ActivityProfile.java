package com.jobma.employer.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.adapters.CertificationAdapter;
import com.jobma.employer.adapters.EducationAdapter;
import com.jobma.employer.adapters.EmploymentAdapter;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.exoPlayer.PlayerActivity;
import com.jobma.employer.exoPlayer.Sample;
import com.jobma.employer.exoPlayer.UriSample;
import com.jobma.employer.model.profile.EOCertificationData;
import com.jobma.employer.model.profile.EOEducationData;
import com.jobma.employer.model.profile.EOIndustryFunctional;
import com.jobma.employer.model.profile.EOKeySkillData;
import com.jobma.employer.model.profile.EOProfessionalData;
import com.jobma.employer.model.profile.EOProfileData;
import com.jobma.employer.model.profile.EOProfileObject;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
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

public class ActivityProfile extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private String companyImage;
    private String VIDEO_URL;
    private ImageView ivBackBtn, videoView, ivPlayIcon;
    private CircleImageView circleImageView;
    private RecyclerView recyclerEmpBackground, recyclerEducation, recyclerCertification;
    private Button btnViewResume;
    private TextView tvEmpTitle, tvDesignation, tvTitle, tvEmail, tvPhone, tvAddress, tvVisaStatus, tvCurrentCompany, tvWorkLocation, tvCurrentCTC,
            tvExpectedCTC, tvTotalExp, tvDesiredLocation, tvWillingToRelocate, tvOpenForContract, tvJobType, tvAvailableFor, tvIndustry,
            tvFunctionalArea, tv_no_emp_data, tv_no_education, tv_no_certification, tvKeySkills;
    private EOProfileData profileData;
    private ArrayList<String> functionalList;
    private ArrayList<String> industryList;
    private ArrayList<String> keySkillsList;
    private int pitcherId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (!ObjectUtil.isEmpty(this.getIntent().getIntExtra("pitcherId", 0))) {
            this.pitcherId = this.getIntent().getIntExtra("pitcherId", 0);
        }

        this.initView();
        this.setOnClickListener();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());

        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
        this.companyImage = loginPreferences.getString(EMPLOYEE_PHOTO, "");

        this.ivBackBtn = this.findViewById(R.id.ivBackBtn);
        this.circleImageView = this.findViewById(R.id.circleImageView);
        this.videoView = this.findViewById(R.id.videoView);
        this.ivPlayIcon = this.findViewById(R.id.imageView23);
        this.tvEmpTitle = this.findViewById(R.id.textView15);
        this.tvDesignation = this.findViewById(R.id.textView16);
        this.tvTitle = this.findViewById(R.id.textView20);
        this.tvEmail = this.findViewById(R.id.textView22);
        this.tvPhone = this.findViewById(R.id.textView23);
        this.tvAddress = this.findViewById(R.id.textView24);
        this.tvVisaStatus = this.findViewById(R.id.textView225);
        this.tvCurrentCompany = this.findViewById(R.id.textView25);
        this.tvWorkLocation = this.findViewById(R.id.textView26);
        this.tvCurrentCTC = this.findViewById(R.id.textView27);
        this.tvExpectedCTC = this.findViewById(R.id.textView29);
        this.tvTotalExp = this.findViewById(R.id.textView31);
        this.tvDesiredLocation = this.findViewById(R.id.textView33);
        this.tvWillingToRelocate = this.findViewById(R.id.textView35);
        this.tvOpenForContract = this.findViewById(R.id.textView37);
        this.tvJobType = this.findViewById(R.id.textView39);
        this.tvAvailableFor = this.findViewById(R.id.textView41);
        this.tvIndustry = this.findViewById(R.id.textView53);
        this.tvFunctionalArea = this.findViewById(R.id.textView63);
        this.recyclerEmpBackground = this.findViewById(R.id.recyclerEmpBackground);
        this.tv_no_emp_data = this.findViewById(R.id.tv_no_emp_data);
        this.recyclerEducation = this.findViewById(R.id.recyclerEducation);
        this.tv_no_education = this.findViewById(R.id.tv_no_education);
        this.recyclerCertification = this.findViewById(R.id.recyclerCertification);
        this.tv_no_certification = this.findViewById(R.id.tv_no_certification);
        this.tvKeySkills = this.findViewById(R.id.textView103);
        this.btnViewResume = this.findViewById(R.id.btnViewResume);

        if (!ObjectUtil.isEmpty(this.companyImage))
            loadImages(this.companyImage, circleImageView);

    }

    private void setOnClickListener() {
        this.ivBackBtn.setOnClickListener(this);
        this.ivPlayIcon.setOnClickListener(this);
        this.btnViewResume.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        this.noNet.RegisterNoNet();
        super.onResume();

        this.getProfileData();
    }

    @Override
    protected void onPause() {
        this.noNet.unRegisterNoNet();
        super.onPause();
    }

    private void getProfileData() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.getPitcherProfile(apiKey, pitcherId).enqueue(new Callback<EOProfileObject>() {
                @Override
                public void onResponse(Call<EOProfileObject> call, Response<EOProfileObject> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOProfileObject eoProfileObject = response.body();
                        if (!ObjectUtil.isEmpty(eoProfileObject)) {
                            if (eoProfileObject.getError() == RESPONSE_SUCCESS) {

                                if (!ObjectUtil.isEmpty(eoProfileObject.getData())) {
                                    profileData = eoProfileObject.getData();
                                    dataToView();
                                }

                            } else {
                                Toast.makeText(ActivityProfile.this, "" + eoProfileObject.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOProfileObject> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityProfile.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void dataToView() {

        if (!ObjectUtil.isEmpty(profileData.getVideoPath())) {

            if (!ObjectUtil.isEmpty(profileData.getVideoPath().getPoster()))
                loadImages(profileData.getVideoPath().getPoster(), videoView);

            if (profileData.getVideoPath().getHls().equalsIgnoreCase("")) { //TODO HLS BALANK THEN FILE URL
                VIDEO_URL = profileData.getVideoPath().getFileurl();  //TODO FILE URL
            } else if (!profileData.getVideoPath().getHls().equalsIgnoreCase("")) { //TODO  NOT BLANK THEN HLS ITSELF
                VIDEO_URL = profileData.getVideoPath().getHls(); //TODO  HLS URL
            } else if (profileData.getVideoPath().getHls().equalsIgnoreCase("") && profileData.getVideoPath().getFileurl().equalsIgnoreCase("")) {
                VIDEO_URL = "";
            }
        }
        if (!ObjectUtil.isEmpty(profileData.getPitcherData())) {
            if (ObjectUtil.isEmpty(profileData.getPitcherData().getFname()) && ObjectUtil.isEmpty(profileData.getPitcherData().getLname())) {
                this.tvEmpTitle.setText("N/A");
            } else {
                if (!ObjectUtil.isEmpty(profileData.getPitcherData().getLname()))
                    this.tvEmpTitle.setText(profileData.getPitcherData().getFname().concat(" ").concat(profileData.getPitcherData().getLname()));
                else
                    this.tvEmpTitle.setText(profileData.getPitcherData().getFname());
            }
            this.tvEmail.setText(ObjectUtil.isEmpty(profileData.getPitcherData().getEmail()) ? "N/A" : profileData.getPitcherData().getEmail());
            this.tvPhone.setText(ObjectUtil.isEmpty(profileData.getPitcherData().getPhone()) ? "N/A" : profileData.getPitcherData().getPhone());
            this.tvAddress.setText(ObjectUtil.isEmpty(profileData.getPitcherData().getAddress()) ? "N/A" : profileData.getPitcherData().getAddress());
        }

        if (!ObjectUtil.isEmpty(profileData.getSummary())) {
            this.tvTitle.setText(ObjectUtil.isEmpty(profileData.getSummary().getObjective()) ? "N/A" : profileData.getSummary().getObjective());
            this.tvDesignation.setText(ObjectUtil.isEmpty(profileData.getSummary().getCurrentDesignation()) ? "N/A" : profileData.getSummary().getCurrentDesignation());
            this.tvVisaStatus.setText(ObjectUtil.isEmpty(profileData.getSummary().getVisaStatus()) ? "N/A" : profileData.getSummary().getVisaStatus());
            this.tvCurrentCompany.setText(ObjectUtil.isEmpty(profileData.getSummary().getCurrentCompany()) ? "N/A" : profileData.getSummary().getCurrentCompany());
            this.tvWorkLocation.setText(ObjectUtil.isEmpty(profileData.getSummary().getCurrentLocation()) ? "N/A" : profileData.getSummary().getCurrentLocation());
            this.tvCurrentCTC.setText(ObjectUtil.isEmpty(profileData.getSummary().getCurrentSalary()) ? "N/A" : profileData.getSummary().getCurrentSalary().concat(" Annually"));
            this.tvExpectedCTC.setText(ObjectUtil.isEmpty(profileData.getSummary().getExpectedSalary()) ? "N/A" : profileData.getSummary().getExpectedSalary().concat(" Annually"));
            if (ObjectUtil.isEmpty(profileData.getSummary().getExpYear()) || ObjectUtil.isEmpty(profileData.getSummary().getExpMonth())) {
                this.tvTotalExp.setText("N/A");
            } else {
                this.tvTotalExp.setText(String.valueOf(profileData.getSummary().getExpYear()).concat(" ").concat("years").concat(" ").concat(String.valueOf(profileData.getSummary().getExpMonth())).concat(" ").concat("months"));
            }
            this.tvDesiredLocation.setText(ObjectUtil.isEmpty(profileData.getSummary().getDesireLocation()) ? "N/A" : profileData.getSummary().getDesireLocation());
            this.tvWillingToRelocate.setText(ObjectUtil.isEmpty(profileData.getSummary().getRelocate()) ? "N/A" : profileData.getSummary().getRelocate());
            this.tvOpenForContract.setText(ObjectUtil.isEmpty(profileData.getSummary().getOpenForContract()) ? "N/A" : profileData.getSummary().getOpenForContract());
            this.tvJobType.setText(ObjectUtil.isEmpty(profileData.getSummary().getDesireJobtype()) ? "N/A" : profileData.getSummary().getDesireJobtype());
            this.tvAvailableFor.setText(ObjectUtil.isEmpty(profileData.getSummary().getNoticePeriod()) ? "N/A" : profileData.getSummary().getNoticePeriod().concat(" ").concat("days"));

            if (!ObjectUtil.isEmpty(profileData.getSummary().getIndustry()) || !ObjectUtil.isEmpty(profileData.getSummary().getFunctional())) {
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

                for (EOIndustryFunctional industryArea : profileData.getSummary().getIndustry()) {
                    industryList.add(industryArea.getValue());
                }
                tvIndustry.setText(TextUtils.join(" , ", industryList));

                for (EOIndustryFunctional functionArea : profileData.getSummary().getFunctional()) {
                    functionalList.add(functionArea.getValue());
                }
                tvFunctionalArea.setText(TextUtils.join(" , ", functionalList));
            } else {
                tvIndustry.setText("N/A");
                tvFunctionalArea.setText("N/A");
            }
        }

        if (!ObjectUtil.isEmpty(profileData.getKeyskills())) {
            if (keySkillsList == null) {
                keySkillsList = new ArrayList<>();
            } else {
                keySkillsList.clear();
            }
            for (EOKeySkillData keySkill : profileData.getKeyskills()) {
                keySkillsList.add(keySkill.getKey());
            }
            tvKeySkills.setText(TextUtils.join(" , ", keySkillsList));
        } else {
            tvKeySkills.setText("N/A");
        }

        if (!ObjectUtil.isEmpty(profileData.getPitcherresume())) {
            if (!ObjectUtil.isEmpty(profileData.getPitcherresume().getJobmaPitcherPdfResume()))
                this.btnViewResume.setVisibility(View.VISIBLE);
        }

        if (!ObjectUtil.isEmpty(profileData.getProfessional())) {
            this.tv_no_emp_data.setVisibility(View.GONE);
            this.recyclerEmpBackground.setVisibility(View.VISIBLE);
            this.recyclerEmpBackground.setHasFixedSize(true);
            this.recyclerEmpBackground.setAdapter(new EmploymentAdapter(this, (ArrayList<EOProfessionalData>) profileData.getProfessional()));
        } else {
            this.tv_no_emp_data.setVisibility(View.VISIBLE);
            this.recyclerEmpBackground.setVisibility(View.GONE);
        }

        if (!ObjectUtil.isEmpty(profileData.getEducation())) {
            this.tv_no_education.setVisibility(View.GONE);
            this.recyclerEducation.setVisibility(View.VISIBLE);
            this.recyclerEducation.setHasFixedSize(true);
            this.recyclerEducation.setAdapter(new EducationAdapter(this, (ArrayList<EOEducationData>) profileData.getEducation()));
        } else {
            this.tv_no_education.setVisibility(View.VISIBLE);
            this.recyclerEducation.setVisibility(View.GONE);
        }

        if (!ObjectUtil.isEmpty(profileData.getCertification())) {
            this.tv_no_certification.setVisibility(View.GONE);
            this.recyclerCertification.setVisibility(View.VISIBLE);
            this.recyclerCertification.setHasFixedSize(true);
            this.recyclerCertification.setAdapter(new CertificationAdapter(this, (ArrayList<EOCertificationData>) profileData.getCertification()));
        } else {
            this.tv_no_certification.setVisibility(View.VISIBLE);
            this.recyclerCertification.setVisibility(View.GONE);
        }
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get()
                .load(imagePath)
                .fit()
                .into(imageView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBackBtn:
                this.finish();
                break;
            case R.id.imageView23:
                //TODO from here play video
                if (VIDEO_URL != null) {
                    if (VIDEO_URL.equalsIgnoreCase("")) {
                        Toast.makeText(this, "No Video Found", Toast.LENGTH_LONG).show();
                    } else {
                        Sample sample = new UriSample("Super speed (PlayReady)", null, Uri.parse(VIDEO_URL), null, null, null);
                        startActivity(sample.buildIntent(this, false, PlayerActivity.ABR_ALGORITHM_DEFAULT));
                    }
                } else {
                    Toast.makeText(this, "Please upload the video first", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnViewResume:
                Intent viewResumeIntent = new Intent(this, ActivityViewResume.class);
                if (!ObjectUtil.isEmpty(profileData.getPitcherresume().getJobmaPitcherPdfResume()))
                    viewResumeIntent.putExtra("viewResume", this.profileData.getPitcherresume().getJobmaPitcherPdfResume());
                this.startActivity(viewResumeIntent);
                break;
        }
    }


}
