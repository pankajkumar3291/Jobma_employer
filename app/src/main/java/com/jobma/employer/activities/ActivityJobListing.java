package com.jobma.employer.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.jobList.EOIndustryFunctional;
import com.jobma.employer.model.jobList.EOJobDescription;
import com.jobma.employer.model.jobList.EOJobDescriptionData;
import com.jobma.employer.model.jobList.EOMessageObject;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class ActivityJobListing extends AppCompatActivity implements View.OnClickListener {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private NoNet noNet;
    private CardView cardView;
    private boolean isopen;
    private int jobId;
    private String jobStatus;
    private ImageView backbtn, ivApprove, ivDisapprove, ivOpenDialog;
    private ConstraintLayout constraintLayout311;
    private TextView tvTitle, tvPostedOn, tvCompanyBy, tvLocation, tvExperience, tvJobType, tvJoiningTime,
            tvFunctionalArea, tvIndustry, tvEducation, tvKeySkills, tvJobDescription, tvAboutCompany, tvRecruiterName,
            tvRecruiterEmail, tvRecruiterPhone, tvCompanyDetails, tvApprove, tvDisapprove;
    private EOJobDescriptionData jobDescriptionData;
    private ArrayList<String> functionalArray = new ArrayList<>();
    private ArrayList<String> industryArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_posted_detail);

        if (!ObjectUtil.isEmpty(this.getIntent().getIntExtra("jobId", 0)) || !ObjectUtil.isEmpty(this.getIntent().getStringExtra("jobStatus"))) {
            this.jobId = this.getIntent().getIntExtra("jobId", 0);
            this.jobStatus = this.getIntent().getStringExtra("jobStatus");
        }

        this.initView();
        this.setOnClickListener();
        this.jobListJobDescription();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.cardView = this.findViewById(R.id.cardView);
        this.backbtn = this.findViewById(R.id.backbtn);
        this.constraintLayout311 = this.findViewById(R.id.constraintLayout311);
        this.tvTitle = this.findViewById(R.id.textView173);
        this.tvPostedOn = this.findViewById(R.id.textView177);
        this.tvCompanyBy = this.findViewById(R.id.textView175);
        this.tvLocation = this.findViewById(R.id.textView178);
        this.tvExperience = this.findViewById(R.id.textView181);
        this.tvJobType = this.findViewById(R.id.textView182);
        this.tvJoiningTime = this.findViewById(R.id.textView185);
        this.tvFunctionalArea = this.findViewById(R.id.textView187);
        this.tvIndustry = this.findViewById(R.id.textView189);
        this.tvEducation = this.findViewById(R.id.textView190);
        this.tvKeySkills = this.findViewById(R.id.textView1901);
        this.tvJobDescription = this.findViewById(R.id.textView1900);
        this.tvAboutCompany = this.findViewById(R.id.textView19);
        this.tvRecruiterName = this.findViewById(R.id.textView195);
        this.tvRecruiterEmail = this.findViewById(R.id.textView196);
        this.tvRecruiterPhone = this.findViewById(R.id.textView197);
        this.tvCompanyDetails = this.findViewById(R.id.textView1951);
        this.ivApprove = this.findViewById(R.id.imageView772);
        this.ivDisapprove = this.findViewById(R.id.imageView773);
        this.ivOpenDialog = this.findViewById(R.id.imageView741);
        this.tvApprove = this.findViewById(R.id.textView198);
        this.tvDisapprove = this.findViewById(R.id.textView199);
    }

    private void setOnClickListener() {
        this.backbtn.setOnClickListener(this);
        this.constraintLayout311.setOnClickListener(this);
        this.ivApprove.setOnClickListener(this);
        this.ivDisapprove.setOnClickListener(this);
        this.ivOpenDialog.setOnClickListener(this);
        this.tvApprove.setOnClickListener(this);
        this.tvDisapprove.setOnClickListener(this);
    }

    private void jobListJobDescription() {
        if (!ObjectUtil.isEmpty(this.apiKey) && !ObjectUtil.isEmpty(jobId)) {
            progress.showProgressBar();
            apiInterface.jobListJobDescription(apiKey, jobId).enqueue(new Callback<EOJobDescription>() {
                @Override
                public void onResponse(Call<EOJobDescription> call, Response<EOJobDescription> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOJobDescription eoJobDescription = response.body();
                        if (!ObjectUtil.isEmpty(eoJobDescription)) {
                            if (eoJobDescription.getError() == RESPONSE_SUCCESS) {
                                jobDescriptionData = eoJobDescription.getData();
                                dataToView();
                            } else {
                                Toast.makeText(ActivityJobListing.this, "" + eoJobDescription.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOJobDescription> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityJobListing.this, "Failed Error :" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void dataToView() {
        if (this.jobStatus.equals("0")) {
            this.constraintLayout311.setVisibility(View.GONE);
        } else {
            this.constraintLayout311.setVisibility(View.VISIBLE);
        }

        if (!ObjectUtil.isEmpty(this.jobDescriptionData)) {
            this.tvTitle.setText(ObjectUtil.isEmpty(jobDescriptionData.getJobTitle()) ? "N/A" : jobDescriptionData.getJobTitle());
            this.tvPostedOn.setText(ObjectUtil.isEmpty(jobDescriptionData.getCreateDate()) ? "N/A" : jobDescriptionData.getCreateDate());
            this.tvCompanyBy.setText(ObjectUtil.isEmpty(jobDescriptionData.getCompanyName()) ? "N/A" : jobDescriptionData.getCompanyName());
            this.tvLocation.setText(ObjectUtil.isEmpty(jobDescriptionData.getLocations()) ? "N/A" : jobDescriptionData.getLocations());
            this.tvExperience.setText((ObjectUtil.isEmpty(jobDescriptionData.getMinExp()) || ObjectUtil.isEmpty(jobDescriptionData.getMaxExp())) ? "N/A" :
                    jobDescriptionData.getMinExp().concat(" - ").concat(jobDescriptionData.getMaxExp()).concat(" Years"));
            this.tvJobType.setText(ObjectUtil.isEmpty(jobDescriptionData.getJobType()) ? "N/A" : jobDescriptionData.getJobType());
            this.tvJoiningTime.setText(ObjectUtil.isEmpty(jobDescriptionData.getNoticePeriod()) ? "N/A" : jobDescriptionData.getNoticePeriod());

            if (!ObjectUtil.isEmpty(jobDescriptionData.getFunctional())) {
                for (EOIndustryFunctional functional : jobDescriptionData.getFunctional()) {
                    functionalArray.add(functional.getValue());
                }
                this.tvFunctionalArea.setText(ObjectUtil.isEmpty(functionalArray) ? "N/A" : TextUtils.join(",", functionalArray));
            } else {
                this.tvFunctionalArea.setText("N/A");
            }

            if (!ObjectUtil.isEmpty(jobDescriptionData.getIndustry())) {
                for (EOIndustryFunctional industry : jobDescriptionData.getIndustry()) {
                    industryArray.add(industry.getValue());
                }
                this.tvIndustry.setText(ObjectUtil.isEmpty(industryArray) ? "N/A" : TextUtils.join(",", industryArray));
            } else {
                this.tvIndustry.setText("N/A");
            }

            this.tvEducation.setText(ObjectUtil.isEmpty(jobDescriptionData.getQualification()) ? "N/A" : jobDescriptionData.getQualification());
            this.tvKeySkills.setText(ObjectUtil.isEmpty(jobDescriptionData.getKeywords()) ? "N/A" : jobDescriptionData.getKeywords());
            this.tvJobDescription.setText(ObjectUtil.isEmpty(jobDescriptionData.getDescription()) ? "N/A" : jobDescriptionData.getDescription());
            this.tvAboutCompany.setText(ObjectUtil.isEmpty(jobDescriptionData.getCompanyProfile()) ? "N/A" : jobDescriptionData.getCompanyProfile());
            this.tvRecruiterName.setText(ObjectUtil.isEmpty(jobDescriptionData.getRecruiterName()) ? "N/A" : jobDescriptionData.getRecruiterName());
            this.tvRecruiterEmail.setText(ObjectUtil.isEmpty(jobDescriptionData.getRecruiterEmail()) ? "N/A" : jobDescriptionData.getRecruiterEmail());
            this.tvRecruiterPhone.setText(ObjectUtil.isEmpty(jobDescriptionData.getRecruiterPhone()) ? "N/A" : jobDescriptionData.getRecruiterPhone());
            this.tvCompanyDetails.setText(ObjectUtil.isEmpty(jobDescriptionData.getCompanyWeb()) ? "N/A" : jobDescriptionData.getCompanyWeb());
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.constraintLayout311:
            case R.id.imageView741:
                if (isopen) {
                    isopen = false;
                    cardView.setVisibility(View.GONE);
                    ivOpenDialog.setImageResource(R.drawable.ic_up_arrow);
                } else {
                    isopen = true;
                    cardView.setVisibility(View.VISIBLE);
                    ivOpenDialog.setImageResource(R.drawable.ic_down_arrow);
                }
                break;
            case R.id.backbtn:
                this.finish();
                break;
            case R.id.textView198:
            case R.id.imageView772:
                //TODO call approve job from here
                jobApproved();
                break;
            case R.id.textView199:
            case R.id.imageView773:
                //TODO call disapprove job from here
                showDialogJobDisapproved();
                break;
        }
    }

    private void showDialogJobDisapproved() {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_disapprove);

        final EditText etReason = dialog.findViewById(R.id.editText18);
        Button btnOk = dialog.findViewById(R.id.button22);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etReason.getText().toString())) {
                    etReason.setError("Can't be Empty !");
                    etReason.setFocusable(true);
                } else {
                    dialog.dismiss();
                    jobDisapproved(etReason.getText().toString().trim());
                }
            }
        });

        Button btnCencel = dialog.findViewById(R.id.button23);
        btnCencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void jobDisapproved(String disapproveReason) {
        if (!ObjectUtil.isEmpty(this.apiKey) && !ObjectUtil.isEmpty(this.jobId)) {
            progress.showProgressBar();
            apiInterface.disapprovedJob(apiKey, jobId, "2", disapproveReason).enqueue(new Callback<EOMessageObject>() {
                @Override
                public void onResponse(Call<EOMessageObject> call, Response<EOMessageObject> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOMessageObject approval = response.body();
                        if (!ObjectUtil.isEmpty(approval)) {
                            if (approval.getError().equals(String.valueOf(RESPONSE_SUCCESS))) {
                                Toast.makeText(ActivityJobListing.this, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                                if (approval.getMessage().equalsIgnoreCase("Job disapproved successfully.")) {
                                    ivApprove.setImageResource(R.drawable.ic_tick_gray);
                                    ivDisapprove.setImageResource(R.drawable.ic_tick_green);
                                } else {
                                    ivDisapprove.setImageResource(R.drawable.ic_tick_gray);
                                }
                            } else {
                                Toast.makeText(ActivityJobListing.this, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOMessageObject> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityJobListing.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void jobApproved() {
        if (!ObjectUtil.isEmpty(this.apiKey) && !ObjectUtil.isEmpty(this.jobId)) {
            progress.showProgressBar();
            apiInterface.approvedJob(apiKey, jobId, "1").enqueue(new Callback<EOMessageObject>() {
                @Override
                public void onResponse(Call<EOMessageObject> call, Response<EOMessageObject> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOMessageObject approval = response.body();
                        if (!ObjectUtil.isEmpty(approval)) {
                            if (approval.getError().equals(String.valueOf(RESPONSE_SUCCESS))) {
                                Toast.makeText(ActivityJobListing.this, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                                if (approval.getMessage().equalsIgnoreCase("Job approved successfully.")) {
                                    ivDisapprove.setImageResource(R.drawable.ic_tick_gray);
                                    ivApprove.setImageResource(R.drawable.ic_tick_green);
                                } else {
                                    ivApprove.setImageResource(R.drawable.ic_tick_gray);
                                }
                            } else {
                                Toast.makeText(ActivityJobListing.this, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOMessageObject> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityJobListing.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

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
