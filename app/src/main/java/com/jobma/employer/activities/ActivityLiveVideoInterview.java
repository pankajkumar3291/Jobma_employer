package com.jobma.employer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.liveInterview.EOEncodedResponse;
import com.jobma.employer.model.liveInterview.EOLiveInterview;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.networking.APIClientLiveInterview;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.webRtc.WebRtcActivity;

import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;
import static com.jobma.employer.util.Constants.SUB_USER;
import static com.jobma.employer.util.Constants.USER_ID;
import static com.jobma.employer.util.Constants.USER_TYPE;

public class ActivityLiveVideoInterview extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private APIClientLiveInterview.APIInterfaceLiveInterview apiInterfaceLiveInterview;
    private SessionSecuredPreferences loginPreferences;
    private EditText et_access_token;
    private Button btnLogin;
    private String apiKey;
    private String userType;
    private int userId;
    private int invitedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_video_interview);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!ObjectUtil.isEmpty(this.getIntent().getIntExtra("invitedId", 0))) {
            this.invitedId = this.getIntent().getIntExtra("invitedId", 0);
        }

        this.initView();
        this.setOnClickListener();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.apiInterfaceLiveInterview = APIClientLiveInterview.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
        this.userId = loginPreferences.getInt(USER_ID, 0);
        this.userType = loginPreferences.getString(USER_TYPE, "");

        this.et_access_token = this.findViewById(R.id.et_access_token);
        this.btnLogin = this.findViewById(R.id.btnLogin);
    }

    private void setOnClickListener() {
        this.btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {
            this.liveInterviewCheckApi();
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

    private void liveInterviewCheckApi() {
        if (ObjectUtil.isNonEmptyStr(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.liveInterviewCheck(apiKey, this.invitedId, ObjectUtil.getTextFromView(this.et_access_token)).enqueue(new Callback<EOLiveInterview>() {
                @Override
                public void onResponse(Call<EOLiveInterview> call, Response<EOLiveInterview> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOLiveInterview liveInterview = response.body();
                        if (!ObjectUtil.isEmpty(liveInterview)) {
                            if (liveInterview.getError() == RESPONSE_SUCCESS) {
                                Toast.makeText(ActivityLiveVideoInterview.this, "" + liveInterview.getMessage(), Toast.LENGTH_SHORT).show();
                                if (ObjectUtil.isNonEmptyStr(userType) && userId != 0 && invitedId != 0) {
                                    //TODO: from here conversion of [pitcherId,jobId,roleId] ->[924,3890,3]-> for main user: 3 for subUser: 5
                                    StringBuilder builder = new StringBuilder();
                                    builder.append("[").append(userId).append(",").append(invitedId).append(",").append(userType.equals(SUB_USER) ? 5 : 3).append("]");
                                    byte[] data = builder.toString().getBytes(StandardCharsets.UTF_8);
                                    String encodedIntoBase64 = Base64.encodeToString(data, Base64.DEFAULT);
                                    getJsonResponseFromEncodedData(encodedIntoBase64);
                                }
                            } else {
                                Toast.makeText(ActivityLiveVideoInterview.this, "" + liveInterview.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(ActivityLiveVideoInterview.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<EOLiveInterview> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityLiveVideoInterview.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getJsonResponseFromEncodedData(String encodedValue) {
        if (ObjectUtil.isNonEmptyStr(this.apiKey)) {
            progress.showProgressBar();
            apiInterfaceLiveInterview.getResponseFromEncodedData("1", encodedValue).enqueue(new Callback<EOEncodedResponse>() {
                @Override
                public void onResponse(Call<EOEncodedResponse> call, Response<EOEncodedResponse> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOEncodedResponse encodedResponse = response.body();

                        if (encodedResponse.getError() == 0) {
                            //Toast.makeText(ActivityLiveVideoInterview.this, "" + encodedResponse.getData().getStatus(), Toast.LENGTH_SHORT).show();
                            Intent webRtcIntent = new Intent(ActivityLiveVideoInterview.this, WebRtcActivity.class);
                            webRtcIntent.putExtra("encodedData", encodedResponse.getData());
                            ActivityLiveVideoInterview.this.startActivity(webRtcIntent);
                        }
                    } else {
                        Toast.makeText(ActivityLiveVideoInterview.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<EOEncodedResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityLiveVideoInterview.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
