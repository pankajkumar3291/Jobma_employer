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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityLogin;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOChangePassword;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentChangePassword extends Fragment implements View.OnClickListener {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private View view;
    private TextInputEditText et_current_password, et_new_password, et_confirm_password;
    private Button saveButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_password, container, false);

        this.initView();
        this.setOnClickListener();
        this.getWalletExpired();

        return view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.et_current_password = view.findViewById(R.id.et_current_password);
        this.et_new_password = view.findViewById(R.id.et_new_password);
        this.et_confirm_password = view.findViewById(R.id.et_confirm_password);
        this.saveButton = view.findViewById(R.id.button6);

    }

    private void setOnClickListener() {
        this.saveButton.setOnClickListener(this);
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
        if (v.getId() == R.id.button6) {
            if (isValidFields()) {
                changePassword();
            }
        }
    }

    private void changePassword() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.changePassword(apiKey, et_current_password.getText().toString().trim(), et_new_password.getText().toString().trim()).enqueue(new Callback<EOChangePassword>() {
                @Override
                public void onResponse(Call<EOChangePassword> call, Response<EOChangePassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOChangePassword forgetPassword = response.body();
                        if (!ObjectUtil.isEmpty(forgetPassword)) {
                            if (forgetPassword.getError() == RESPONSE_SUCCESS) {
                                Toast.makeText(getActivity(), "" + forgetPassword.getMessage(), Toast.LENGTH_SHORT).show();

                                et_current_password.setText("");
                                et_new_password.setText("");
                                et_confirm_password.setText("");
                                et_current_password.requestFocus();

                            } else {
                                Toast.makeText(getActivity(), "" + forgetPassword.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOChangePassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private boolean isValidFields() {
        String errorMsg = null;

        String oldPassword = ObjectUtil.getTextFromView(et_current_password);
        String password = ObjectUtil.getTextFromView(et_new_password);
        String confirmPassword = ObjectUtil.getTextFromView(et_confirm_password);

        if (ObjectUtil.isEmptyStr(oldPassword) || ObjectUtil.isEmptyStr(password) || ObjectUtil.isEmptyStr(confirmPassword)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (oldPassword.length() < 6) {
            errorMsg = this.getString(R.string.password_min_character);
        } else if (password.length() < 6) {
            errorMsg = this.getString(R.string.password_min_character);
        } else if (confirmPassword.length() < 6) {
            errorMsg = this.getString(R.string.password_min_character);
        } else if (!password.equals(confirmPassword)) {
            errorMsg = this.getString(R.string.confirm_password_not_matched);
        }
        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
