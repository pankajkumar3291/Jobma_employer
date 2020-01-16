package com.jobma.employer.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityLogin;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.reportIssue.SubAccountRequest;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.EMPLOYEE_EMAIL;
import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentReportIssue extends Fragment implements View.OnClickListener {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey, userEmail;
    private View view;
    private TextInputLayout edEmail, etSubject, edMessage;
    private Button submitBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_report_issue, container, false);

        initView();
        this.setOnClickListener();
        this.getWalletExpired();

        return view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
        this.userEmail = loginPreferences.getString(EMPLOYEE_EMAIL, "");

        this.submitBtn = view.findViewById(R.id.button8);
        this.edEmail = view.findViewById(R.id.textInputLayout6);
        this.edMessage = view.findViewById(R.id.et_message);
        this.etSubject = view.findViewById(R.id.textInputLayout7);
    }

    private void setOnClickListener() {
        this.submitBtn.setOnClickListener(this);
        if (!ObjectUtil.isEmpty(this.userEmail)) {
            this.edEmail.getEditText().setText(this.userEmail);
        }
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button8) {
            if (isValidReportIssue()) {
                this.submitReportIssue();
            }
        }
    }

    private void submitReportIssue() {
        if (!ObjectUtil.isEmpty(this.apiKey) && !ObjectUtil.isEmpty(this.userEmail)) {
            progress.showProgressBar();
            apiInterface.reportIssues(apiKey, edEmail.getEditText().getText().toString().trim(), etSubject.getEditText().getText().toString().trim(), edMessage.getEditText().getText().toString().trim()).enqueue(new Callback<SubAccountRequest>() {
                @Override
                public void onResponse(Call<SubAccountRequest> call, Response<SubAccountRequest> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        SubAccountRequest reportIssueObj = response.body();
                        if (!ObjectUtil.isEmpty(reportIssueObj)) {
                            if (reportIssueObj.getError() == RESPONSE_SUCCESS) {
                                Toast.makeText(getActivity(), "" + reportIssueObj.getData().getInformation(), Toast.LENGTH_LONG).show();
                                clearEditText();
                            } else {
                                Toast.makeText(getActivity(), "" + reportIssueObj.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<SubAccountRequest> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void clearEditText() {
        this.etSubject.getEditText().setText("");
        this.edMessage.getEditText().setText("");
        this.etSubject.requestFocus();
    }

    private boolean isValidReportIssue() {
        String errorMsg = null;

        String subject = ObjectUtil.getTextFromView(etSubject.getEditText());
        String message = ObjectUtil.getTextFromView(edMessage.getEditText());

        if (ObjectUtil.isEmptyStr(subject) || ObjectUtil.isEmptyStr(message)) {
            errorMsg = this.getString(R.string.all_fields_required);
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
