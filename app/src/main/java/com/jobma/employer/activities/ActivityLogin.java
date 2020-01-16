package com.jobma.employer.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOLoginResponse;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.GlobalUtil;
import com.jobma.employer.util.ObjectUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.jobma.employer.util.Constants.EMPLOYEE_EMAIL;
import static com.jobma.employer.util.Constants.EMPLOYEE_NAME;
import static com.jobma.employer.util.Constants.EMPLOYEE_PHOTO;
import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;
import static com.jobma.employer.util.Constants.USER_ID;
import static com.jobma.employer.util.Constants.USER_TYPE;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterfac;
    private SessionSecuredPreferences loginPreferences;
    private TextView tvResetPassword;
    private Button btnSignin;
    private EditText et_user_id, et_password;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.initView();
        this.setOnClickListener();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.progress = new GlobalProgressDialog(this);
        this.apiInterfac = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);

        this.et_user_id = this.findViewById(R.id.et_user_id);
        this.et_password = this.findViewById(R.id.et_password);
        this.btnSignin = this.findViewById(R.id.btnSignin);
        this.tvResetPassword = this.findViewById(R.id.tvResetPassword);
    }

    private void setOnClickListener() {
        this.btnSignin.setOnClickListener(this);
        this.tvResetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvResetPassword:
                forgetPasswordActivity();
                break;
            case R.id.btnSignin:
                if (isValidLogin()) {
                    loginEmployer();
                }
                break;
        }
    }

    private void loginEmployer() {
        progress.showProgressBar();
        apiInterfac.employeeLogin(et_user_id.getText().toString().trim(), et_password.getText().toString().trim()).enqueue(new Callback<EOLoginResponse>() {
            @Override
            public void onResponse(Call<EOLoginResponse> call, Response<EOLoginResponse> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EOLoginResponse loginResponse = response.body();
                    if (!ObjectUtil.isEmpty(loginResponse)) {
                        if (loginResponse.getError() == RESPONSE_SUCCESS) {
                            //TODO save user api key to hit next url
                            loginPreferences.edit().putString(SELECTED_API_KEY, loginResponse.getData().getApiKey()).apply();
                            loginPreferences.edit().putString(USER_TYPE, loginResponse.getData().getUserType()).apply();
                            loginPreferences.edit().putString(EMPLOYEE_NAME, loginResponse.getData().getJobmaCatcherCompany()).apply();
                            loginPreferences.edit().putString(EMPLOYEE_EMAIL, loginResponse.getData().getEmail()).apply();
                            loginPreferences.edit().putString(EMPLOYEE_PHOTO, loginResponse.getData().getJobmaCatcherPhoto()).apply();
                            loginPreferences.edit().putInt(USER_ID, loginResponse.getData().getId()).apply();

                            Intent dashboardIntent = new Intent(ActivityLogin.this, ActivityDashboard.class);
                            startActivity(dashboardIntent);
                            ActivityLogin.this.finish();
                        } else {
                            Toast.makeText(ActivityLogin.this, "" + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(ActivityLogin.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EOLoginResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(ActivityLogin.this, "Failed Error :" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isValidLogin() {
        String errorMsg = null;

        String emailId = ObjectUtil.getTextFromView(et_user_id);
        String password = ObjectUtil.getTextFromView(et_password);

        if (ObjectUtil.isEmptyStr(emailId) || ObjectUtil.isEmptyStr(password)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (!GlobalUtil.isValidEmail(emailId)) {
            errorMsg = this.getString(R.string.valid_email);
        } else if (password.length() < 6) {
            errorMsg = this.getString(R.string.password_min_character);
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void forgetPasswordActivity() {
        Intent forgetIntent = new Intent(ActivityLogin.this, ActivityForgetPassword.class);
        this.startActivity(forgetIntent);
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
