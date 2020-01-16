package com.jobma.employer.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.google.android.material.textfield.TextInputLayout;
import com.jobma.employer.R;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.UIUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityForgetPassword extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private Button btnSendRequest, btnOkay;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private TextInputLayout etEmail;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        this.initView();
        this.setOnClickListener();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.btnSendRequest = findViewById(R.id.btnSendRequest);
        this.etEmail = findViewById(R.id.textInputLayout3);
    }

    private void setOnClickListener() {
        btnSendRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSendRequest:
                this.resetPassword();
                break;
        }
    }

    private void resetPassword() {
        if (TextUtils.isEmpty(etEmail.getEditText().getText().toString())) {
            etEmail.setError("Can't be empty");
        } else {
            try {
                apiInterface.forgetPassword(etEmail.getEditText().getText().toString().trim()).enqueue(new Callback<EOForgetPassword>() {
                    @Override
                    public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                        if (response.body().getError() == 0) {
                            dialogForgetPassword();
                        } else {
                            Toast.makeText(ActivityForgetPassword.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void dialogForgetPassword() {
        LayoutInflater li = LayoutInflater.from(ActivityForgetPassword.this);
        View dialogView = li.inflate(R.layout.dialog_forgot_password, null);
        findIds(dialogView);
        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialogView.findViewById(R.id.mainLayout), R.color.bg_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        alertDialogBuilder = new AlertDialog.Builder(ActivityForgetPassword.this);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder.setCancelable(true);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void findIds(View dialogView) {

        btnOkay = dialogView.findViewById(R.id.btnOkay);

        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ActivityForgetPassword.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
