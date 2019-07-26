package com.jobma.employer.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.get_candidate_detail.EvalutionList;
import com.jobma.employer.model.get_candidate_detail.GetCandidateDetail;
import com.jobma.employer.model.profile.EOForwardObject;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.GlobalUtil;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class ActivityForwardProfile extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private ImageView backArrowImage;
    private CircleImageView circleImageView;
    private TextView tvEmpName, tvEmail, tvPhone;
    private ScaleRatingBar simpleRatingBar, simpleRatingBaryellow, simpleRatingBargreen;
    private EditText et_email_id, et_reply_email, et_enter_message;
    private CheckBox checkbox_shared_profile;
    private Button btnSend;
    private int appliedId;
    private EvalutionList evalutionList;
    private String sharedProfile = "0";
    private Map<String, String> params = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forward_profile);

        if (!ObjectUtil.isEmpty(this.getIntent().getIntExtra("appliedId", 0))) {
            this.appliedId = this.getIntent().getIntExtra("appliedId", 0);
        }

        this.initView();
        this.setOnClickListner();
        this.getWalletExpired();
        this.getEmployeeData();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());

        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.backArrowImage = this.findViewById(R.id.backArrowImage);
        this.circleImageView = this.findViewById(R.id.circleImageView);
        this.tvEmpName = this.findViewById(R.id.tvEmpName);
        this.tvEmail = this.findViewById(R.id.tvEmail);
        this.tvPhone = this.findViewById(R.id.tvPhone);
        this.simpleRatingBar = this.findViewById(R.id.simpleRatingBar);
        this.simpleRatingBaryellow = this.findViewById(R.id.simpleRatingBaryellow);
        this.simpleRatingBargreen = this.findViewById(R.id.simpleRatingBargreen);
        this.et_email_id = this.findViewById(R.id.et_email_id);
        this.et_reply_email = this.findViewById(R.id.et_reply_email);
        this.et_enter_message = this.findViewById(R.id.et_enter_message);
        this.checkbox_shared_profile = this.findViewById(R.id.checkbox_shared_profile);
        this.btnSend = this.findViewById(R.id.btnSend);
    }

    private void setOnClickListner() {
        this.backArrowImage.setOnClickListener(this);
        this.btnSend.setOnClickListener(this);
        this.checkbox_shared_profile.setOnClickListener(this);
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
                        Toast.makeText(ActivityForwardProfile.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Intent loginIntent = new Intent(ActivityForwardProfile.this, ActivityLogin.class);
                    ActivityForwardProfile.this.startActivity(loginIntent);
                    ActivityForwardProfile.this.finish();
                }
            }
        });
        dialog.show();
    }

    private void getEmployeeData() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.getCandidateDetail(apiKey, String.valueOf(appliedId)).enqueue(new Callback<GetCandidateDetail>() {
                @Override
                public void onResponse(Call<GetCandidateDetail> call, Response<GetCandidateDetail> response) {
                    progress.hideProgressBar();

                    if (!ObjectUtil.isEmpty(response.body())) {
                        GetCandidateDetail getCandidateDetail = response.body();
                        if (!ObjectUtil.isEmpty(getCandidateDetail)) {
                            if (getCandidateDetail.getError() == RESPONSE_SUCCESS) {

                                if (!ObjectUtil.isEmpty(getCandidateDetail.getData())) {
                                    evalutionList = getCandidateDetail.getData().getEvalutionList();
                                    dataToView();
                                }

                            } else {
                                Toast.makeText(ActivityForwardProfile.this, "" + getCandidateDetail.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<GetCandidateDetail> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityForwardProfile.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void dataToView() {
        if (!ObjectUtil.isEmpty(evalutionList.getPitcherData().getJobmaPitcherPhoto())) {
            Picasso.get().load(evalutionList.getPitcherData().getJobmaPitcherPhoto()).resize(100, 100).error(R.drawable.ic_profile).into(circleImageView);
        }
        this.tvEmpName.setText(evalutionList.getPitcherData().getJobmaPitcherFname().concat(" ").concat(evalutionList.getPitcherData().getJobmaPitcherLname()));
        this.tvEmail.setText(evalutionList.getPitcherData().getJobmaPitcherEmail());
        this.tvPhone.setText(evalutionList.getPitcherData().getJobmaPitcherPhone());
        if (evalutionList.getAvgRating() <= 1) {
            this.simpleRatingBar.setRating((float) evalutionList.getAvgRating());
            this.simpleRatingBar.setVisibility(View.VISIBLE);
            this.simpleRatingBaryellow.setVisibility(View.GONE);
            this.simpleRatingBargreen.setVisibility(View.GONE);
        } else if (evalutionList.getAvgRating() > 1 && evalutionList.getAvgRating() <= 3) {
            this.simpleRatingBar.setVisibility(View.GONE);
            this.simpleRatingBaryellow.setVisibility(View.VISIBLE);
            this.simpleRatingBargreen.setVisibility(View.GONE);
            this.simpleRatingBaryellow.setRating((float) evalutionList.getAvgRating());
        } else if (evalutionList.getAvgRating() > 3) {
            this.simpleRatingBar.setVisibility(View.GONE);
            this.simpleRatingBaryellow.setVisibility(View.GONE);
            this.simpleRatingBargreen.setVisibility(View.VISIBLE);
            this.simpleRatingBargreen.setRating((float) evalutionList.getAvgRating());
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backArrowImage:
                this.finish();
                break;
            case R.id.checkbox_shared_profile:
                if (checkbox_shared_profile.isChecked())
                    sharedProfile = "1";
                else
                    sharedProfile = "0";
                break;
            case R.id.btnSend:
                if (isValidSendMailAndMessage()) {
                    this.sendProfile();
                }
                break;
        }
    }

    private void sendProfile() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {

            params.put("applied_id", String.valueOf(appliedId));
            params.put("email", ObjectUtil.getTextFromView(et_email_id));
            params.put("message", ObjectUtil.getTextFromView(et_enter_message));
            if (!ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_reply_email)))
                params.put("reply_email", ObjectUtil.getTextFromView(et_reply_email));
            if (sharedProfile.equals("1"))
                params.put("shareProfile", sharedProfile);

            progress.showProgressBar();
            apiInterface.forwardProfile(apiKey, params).enqueue(new Callback<EOForwardObject>() {
                @Override
                public void onResponse(Call<EOForwardObject> call, Response<EOForwardObject> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForwardObject forwardProfile = response.body();
                        if (!ObjectUtil.isEmpty(forwardProfile)) {
                            if (forwardProfile.getError() == RESPONSE_SUCCESS) {
                                Toast.makeText(ActivityForwardProfile.this, "" + forwardProfile.getMessage(), Toast.LENGTH_SHORT).show();
                                ActivityForwardProfile.this.finish();
                            } else {
                                Toast.makeText(ActivityForwardProfile.this, "" + forwardProfile.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForwardObject> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityForwardProfile.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean isValidSendMailAndMessage() {
        String errorMsg = null;
        String replyEmail = ObjectUtil.getTextFromView(et_reply_email);

        if (ObjectUtil.isEmptyStr(ObjectUtil.getTextFromView(et_email_id))) {
            errorMsg = "Send to email id is mandatory.";
        } else if (ObjectUtil.isEmptyStr(ObjectUtil.getTextFromView(et_enter_message))) {
            errorMsg = "Message field is mandatory.";
        } else {
            String[] emailArray = ObjectUtil.getTextFromView(et_email_id).split(",");
            for (String email : emailArray) {
                if (email.matches("\\S+"))
                    email.trim();
                else
                    errorMsg = !GlobalUtil.isValidEmail(email.trim()) ? this.getString(R.string.valid_email) : "";
            }
        }

        if (!TextUtils.isEmpty(replyEmail)) {
            errorMsg = !GlobalUtil.isValidEmail(replyEmail) ? this.getString(R.string.valid_email) : "";
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}
