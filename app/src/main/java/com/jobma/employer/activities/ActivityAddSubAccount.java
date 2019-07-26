package com.jobma.employer.activities;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.AddSubAccountPermissionDialog;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.subAccounts.AddPermissionDialog;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.GlobalUtil;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class ActivityAddSubAccount extends AppCompatActivity implements View.OnClickListener {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private NoNet noNet;
    public static EditText et_select_permission;
    private ImageView btn_selection_permission, btnback;
    private AddSubAccountPermissionDialog permissionDialog;
    private CheckBox checkBox;
    private Button btn_submit;
    private TextInputEditText et_full_name, et_email_id, et_password, et_confirm_password;
    private String approvalPostedJob = "0";
    private ArrayList<AddPermissionDialog> permissionDialogs = new ArrayList<>();
    private ArrayList<String> positionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub_account);

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

        et_select_permission = findViewById(R.id.textView123);
        this.btn_selection_permission = findViewById(R.id.imageView54);
        this.btnback = findViewById(R.id.backicon);
        this.checkBox = this.findViewById(R.id.checkBox);
        this.btn_submit = this.findViewById(R.id.button10);
        this.et_full_name = this.findViewById(R.id.et_full_name);
        this.et_email_id = this.findViewById(R.id.et_email_id);
        this.et_password = this.findViewById(R.id.et_password);
        this.et_confirm_password = this.findViewById(R.id.et_confirm_password);

        permissionDialogs.add(new AddPermissionDialog(true, "Evaluate Candidates", "1"));
        permissionDialogs.add(new AddPermissionDialog(true, "Manage Sub Account", "2"));
        permissionDialogs.add(new AddPermissionDialog(true, "Invite Candidates", "3"));
        permissionDialogs.add(new AddPermissionDialog(true, "Integration Interviews", "4"));
        permissionDialogs.add(new AddPermissionDialog(true, "Jobma Interviews", "5"));
        permissionDialogs.add(new AddPermissionDialog(true, "Company Profile", "6"));
        permissionDialogs.add(new AddPermissionDialog(true, "Company Video", "7"));
        permissionDialogs.add(new AddPermissionDialog(true, "Interview Template", "8"));

    }

    private void setOnClickListener() {
        this.btn_selection_permission.setOnClickListener(this);
        this.btnback.setOnClickListener(this);
        this.btn_submit.setOnClickListener(this);
        this.checkBox.setOnClickListener(this);
        et_select_permission.setOnClickListener(this);
    }

    public void addSelectedPositions() {
        positionList.clear();
        et_select_permission.setText("Permission's has been Added");
        et_select_permission.setTextColor(getResources().getColor(R.color.text_gray_color));
        for (AddPermissionDialog addPermissionDialog : permissionDialogs) {
            if (addPermissionDialog.isChecked()) {
                positionList.add(addPermissionDialog.getAddedPermission());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView123:
            case R.id.imageView54:
                permissionDialog = new AddSubAccountPermissionDialog(this, permissionDialogs);
                permissionDialog.show();
                break;
            case R.id.backicon:
                finish();
                break;
            case R.id.button10:
                if (isValidFields()) {
                    this.addSubAccount();
                }
                break;
            case R.id.checkBox:
                if (checkBox.isChecked())
                    approvalPostedJob = "1";
                else
                    approvalPostedJob = "0";
                break;
        }
    }

    private void addSubAccount() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.addSubAccountApi(apiKey, ObjectUtil.getTextFromView(et_full_name), ObjectUtil.getTextFromView(et_email_id), ObjectUtil.getTextFromView(et_password),
                    approvalPostedJob, TextUtils.join(",", positionList)).enqueue(new Callback<EOForgetPassword>() {
                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword eoSubAccounts = response.body();
                        if (!ObjectUtil.isEmpty(eoSubAccounts)) {
                            if (eoSubAccounts.getError() == RESPONSE_SUCCESS) {
                                Toast.makeText(ActivityAddSubAccount.this, "" + eoSubAccounts.getMessage(), Toast.LENGTH_SHORT).show();
                                ActivityAddSubAccount.this.finish();
                            } else {
                                Toast.makeText(ActivityAddSubAccount.this, "" + eoSubAccounts.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityAddSubAccount.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean isValidFields() {
        String errorMsg = null;

        String fullName = ObjectUtil.getTextFromView(et_full_name);
        String emailId = ObjectUtil.getTextFromView(et_email_id);
        String password = ObjectUtil.getTextFromView(et_password);
        String confirmPassword = ObjectUtil.getTextFromView(et_confirm_password);

        if (ObjectUtil.isEmptyStr(fullName) || ObjectUtil.isEmptyStr(emailId) || ObjectUtil.isEmptyStr(password)
                || ObjectUtil.isEmptyStr(confirmPassword) || ObjectUtil.isEmpty(positionList)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (!GlobalUtil.isValidEmail(emailId)) {
            errorMsg = this.getString(R.string.valid_email);
        } else if (password.length() < 6) {
            errorMsg = this.getString(R.string.password_min_character);
        } else if (confirmPassword.length() < 6) {
            errorMsg = this.getString(R.string.password_min_character);
        } else if (!password.equals(confirmPassword)) {
            errorMsg = this.getString(R.string.confirm_password_not_matched);
        }
        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
