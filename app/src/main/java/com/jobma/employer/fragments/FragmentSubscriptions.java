package com.jobma.employer.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityLogin;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.subscriptions.EOSubscription;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentSubscriptions extends Fragment implements View.OnClickListener {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private View view;
    private TextView tvStartDate, tvEndDate, tvCredit, tvCreditTwo, tvLiveInterview, tvPreRecordedInterview, tvstatus, tvType, tvCostType;
    private Switch status;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_subscriptions, container, false);

        this.initView();
        this.getWalletExpired();
        this.subscriptionsData();
        return view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        tvStartDate = view.findViewById(R.id.textView127);
        tvEndDate = view.findViewById(R.id.textView128);
        tvCredit = view.findViewById(R.id.textView129);
        tvCreditTwo = view.findViewById(R.id.textView132);
        tvLiveInterview = view.findViewById(R.id.textView1321);
        tvPreRecordedInterview = view.findViewById(R.id.textView13212);
        status = view.findViewById(R.id.switch2);
        tvstatus = view.findViewById(R.id.textView126);
        tvType = view.findViewById(R.id.textView122);
        tvCostType = view.findViewById(R.id.textView13312);
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

    private void subscriptionsData() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.subscription(apiKey).enqueue(new Callback<EOSubscription>() {
                @Override
                public void onResponse(Call<EOSubscription> call, Response<EOSubscription> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOSubscription eoSubscription = response.body();
                        if (!ObjectUtil.isEmpty(eoSubscription)) {
                            if (eoSubscription.getError().equals(String.valueOf(RESPONSE_SUCCESS))) {
                                tvStartDate.setText("Start date " + eoSubscription.getData().getStartDate());
                                tvEndDate.setText("End date " + eoSubscription.getData().getExpiryDate());
                                tvCredit.setText("Available credits " + eoSubscription.getData().getAmount());
                                tvCreditTwo.setText(eoSubscription.getData().getCreditValue());
                                tvPreRecordedInterview.setText(eoSubscription.getData().getPreRecoreded());
                                tvLiveInterview.setText(eoSubscription.getData().getLiveInterview());
                                tvstatus.setText(eoSubscription.getData().getStatus());
                                tvCostType.setText("Credit / " + eoSubscription.getData().getCostType());
                                tvType.setText("Current Subscription Detail (" + eoSubscription.getData().getPlan() + ")");
                                if (eoSubscription.getData().getStatus().equalsIgnoreCase("Active")) {
                                    status.setChecked(true);
                                    status.setClickable(false);
                                } else {
                                    status.setChecked(false);
                                    status.setClickable(false);
                                }
                            } else {
                                Toast.makeText(getActivity(), "" + eoSubscription.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOSubscription> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {

    }


}
