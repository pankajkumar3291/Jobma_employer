package com.jobma.employer.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.setupInterview.EOInterviewKitData;
import com.jobma.employer.model.setupInterview.EOInterviewKitList;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class ActivityInviteMessage extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private ImageView btnBack;
    private TextInputEditText et_select_message;
    private Spinner spinnerInterviewKit;
    private Button btnSend;
    private String selectedKit, interViewMode, allowCandidate, selectedDate, timeZone, startTime, endTime, name, email, phone;
    private int selectedKitId, jobId;
    private ArrayList<EOInterviewKitData> eoInterviewKitList = new ArrayList<>();

    private ArrayList<String> nameArray = new ArrayList<>();
    private ArrayList<String> emailArray = new ArrayList<>();
    private ArrayList<String> phoneArray = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_message);

        if (!ObjectUtil.isEmpty(this.getIntent().getStringExtra("interViewMode"))) {
            this.interViewMode = this.getIntent().getStringExtra("interViewMode");
        }

        if (this.interViewMode.equals("1")) {
            if (!ObjectUtil.isEmpty(this.getIntent().getStringExtra("selectedDate")) && !ObjectUtil.isEmpty(this.getIntent().getStringExtra("allowCandidate"))
                    && !ObjectUtil.isEmpty(this.getIntent().getSerializableExtra("nameArray")) && !ObjectUtil.isEmpty(this.getIntent().getSerializableExtra("emailArray"))
                    && !ObjectUtil.isEmpty(this.getIntent().getSerializableExtra("phoneArray")) && !ObjectUtil.isEmpty(this.getIntent().getIntExtra("jobId", 0))) {

                this.jobId = this.getIntent().getIntExtra("jobId", 0);
                this.selectedDate = this.getIntent().getStringExtra("selectedDate");
                this.allowCandidate = this.getIntent().getStringExtra("allowCandidate");
                this.nameArray = (ArrayList<String>) this.getIntent().getSerializableExtra("nameArray");
                this.emailArray = (ArrayList<String>) this.getIntent().getSerializableExtra("emailArray");
                this.phoneArray = (ArrayList<String>) this.getIntent().getSerializableExtra("phoneArray");
            }
        } else {
            if (!ObjectUtil.isEmpty(this.getIntent().getStringExtra("selectedDate")) && !ObjectUtil.isEmpty(this.getIntent().getIntExtra("jobId", 0))
                    && !ObjectUtil.isEmpty(this.getIntent().getStringExtra("timeZone")) && !ObjectUtil.isEmpty(this.getIntent().getStringExtra("startTime"))
                    && !ObjectUtil.isEmpty(this.getIntent().getStringExtra("endTime")) && !ObjectUtil.isEmpty(this.getIntent().getStringExtra("name"))
                    && !ObjectUtil.isEmpty(this.getIntent().getStringExtra("email")) && !ObjectUtil.isEmpty(this.getIntent().getStringExtra("phone"))) {

                this.jobId = this.getIntent().getIntExtra("jobId", 0);
                this.selectedDate = this.getIntent().getStringExtra("selectedDate");
                this.timeZone = this.getIntent().getStringExtra("timeZone");
                this.startTime = this.getIntent().getStringExtra("startTime");
                this.endTime = this.getIntent().getStringExtra("endTime");
                this.name = this.getIntent().getStringExtra("name");
                this.email = this.getIntent().getStringExtra("email");
                this.phone = this.getIntent().getStringExtra("phone");
            }
        }

        this.initView();
        this.setOnClickListener();
        if (this.interViewMode.equals("1")) {
            this.getInterViewKit();
        } else {
            this.spinnerInterviewKit.setVisibility(View.GONE);
            this.findViewById(R.id.imageView14).setVisibility(View.GONE);
        }
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.btnBack = this.findViewById(R.id.btnBack);
        this.spinnerInterviewKit = this.findViewById(R.id.spinnerInterviewKit);
        this.et_select_message = this.findViewById(R.id.et_select_message);
        this.btnSend = this.findViewById(R.id.button19);
    }

    private void setOnClickListener() {
        this.btnBack.setOnClickListener(this);
        this.btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button19:
                if (this.interViewMode.equals("1")) {
                    if (!ObjectUtil.isEmpty(selectedKit)) {
                        if (selectedKit.equalsIgnoreCase("Select interview kit")) {
                            Toast.makeText(this, "Please select interview kit", Toast.LENGTH_SHORT).show();
                        } else if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_select_message))) {
                            Toast.makeText(this, "Please enter message", Toast.LENGTH_SHORT).show();
                        } else {
                            invitationApi();
                        }
                    } else {
                        Toast.makeText(ActivityInviteMessage.this, "Please select interview kit", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_select_message))) {
                        Toast.makeText(this, "Please enter message", Toast.LENGTH_SHORT).show();
                    } else {
                        invitationApi();
                    }
                }
                break;
            case R.id.btnBack:
                this.finish();
                break;
        }
    }

    private void invitationApi() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {

            if (this.interViewMode.equals("1")) {
                progress.showProgressBar();
                apiInterface.invitationForPreRecorded(apiKey, TextUtils.join(",", nameArray), TextUtils.join(",", emailArray), TextUtils.join(",", phoneArray),
                        interViewMode, String.valueOf(jobId), String.valueOf(selectedKitId), allowCandidate, ObjectUtil.getTextFromView(et_select_message), selectedDate).enqueue(new Callback<EOForgetPassword>() {
                    @Override
                    public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                        progress.hideProgressBar();
                        if (!ObjectUtil.isEmpty(response.body())) {
                            EOForgetPassword invitationMessage = response.body();
                            if (!ObjectUtil.isEmpty(invitationMessage)) {
                                if (invitationMessage.getError() == RESPONSE_SUCCESS) {
                                    showSuccessDialog(invitationMessage.getMessage());
                                } else
                                    Toast.makeText(ActivityInviteMessage.this, "" + invitationMessage.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            Toast.makeText(ActivityInviteMessage.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                progress.showProgressBar();
                apiInterface.invitationForLiveInterview(apiKey, name, email, phone, interViewMode, String.valueOf(jobId), ObjectUtil.getTextFromView(et_select_message),
                        startTime, endTime, timeZone, selectedDate).enqueue(new Callback<EOForgetPassword>() {
                    @Override
                    public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                        progress.hideProgressBar();
                        if (!ObjectUtil.isEmpty(response.body())) {
                            EOForgetPassword invitationMessage = response.body();
                            if (!ObjectUtil.isEmpty(invitationMessage)) {
                                if (invitationMessage.getError() == RESPONSE_SUCCESS) {
                                    showSuccessDialog(invitationMessage.getMessage());
                                } else
                                    Toast.makeText(ActivityInviteMessage.this, "" + invitationMessage.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            Toast.makeText(ActivityInviteMessage.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void getInterViewKit() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.getInterviewKit(apiKey).enqueue(new Callback<EOInterviewKitList>() {
                @Override
                public void onResponse(Call<EOInterviewKitList> call, Response<EOInterviewKitList> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOInterviewKitList interviewKitList = response.body();
                        if (!ObjectUtil.isEmpty(interviewKitList)) {
                            if (interviewKitList.getError() == RESPONSE_SUCCESS) {
                                eoInterviewKitList = (ArrayList<EOInterviewKitData>) interviewKitList.getData();
                                dataToView();
                            } else
                                Toast.makeText(ActivityInviteMessage.this, "" + interviewKitList.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOInterviewKitList> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityInviteMessage.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void dataToView() {
        ArrayList<String> interviewKitList = new ArrayList<>();
        if (!ObjectUtil.isEmpty(eoInterviewKitList)) {
            for (EOInterviewKitData kitData : eoInterviewKitList) {
                interviewKitList.add(kitData.getTitle());
            }
        }
        SpinnerAdapter arrayAdapter = new SpinnerAdapter(this, R.layout.spinner_item);
        arrayAdapter.addAll(interviewKitList);
        arrayAdapter.add("Select interview kit");
        spinnerInterviewKit.setAdapter(arrayAdapter);
        spinnerInterviewKit.setSelection(arrayAdapter.getCount());
        spinnerInterviewKit.setOnItemSelectedListener(onInterviewKitSelectedListener);
    }

    AdapterView.OnItemSelectedListener onInterviewKitSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedKit = (String) parent.getItemAtPosition(position);
            for (EOInterviewKitData kitData : eoInterviewKitList) {
                if (kitData.getTitle().equalsIgnoreCase(selectedKit)) {
                    selectedKitId = kitData.getId();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void showSuccessDialog(String successMessage) {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_invitation);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.bg_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        Button dialogBtn_cancel = dialog.findViewById(R.id.button21);
        TextView message = dialog.findViewById(R.id.textView164);

        message.setText(successMessage);

        dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityInterviewInvite.activityPreRecorded.get().finish(); //TODO here finish pre recorded activity

                if (!ObjectUtil.isEmpty(interViewMode)) {
                    if (interViewMode.equals("2"))
                        ActivityLiveInterview.activityLiveInterview.get().finish(); //TODO here finish pre live interview activity
                }
                ActivityInviteMessage.this.finish();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private class SpinnerAdapter extends ArrayAdapter<String> {

        private SpinnerAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            int count = super.getCount();
            return count > 0 ? count - 1 : count;
        }
    }


}
