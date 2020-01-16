package com.jobma.employer.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.google.android.material.textfield.TextInputEditText;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.setupInterview.EOCreateJob;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class ActivityCreateJob extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private ImageView btnback;
    private CheckBox btnPublic;
    private Button btnNext;
    private TextInputEditText et_job_title, et_job_description;
    private static TextInputEditText et_select_date;
    private String publicJob = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_job);

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

        this.btnback = findViewById(R.id.btnback);
        this.btnPublic = findViewById(R.id.radioButton3);
        this.et_job_title = this.findViewById(R.id.et_job_title);
        et_select_date = this.findViewById(R.id.et_select_date);
        this.et_job_description = this.findViewById(R.id.et_job_description);
        this.btnNext = this.findViewById(R.id.button13);
    }

    private void setOnClickListener() {
        this.btnback.setOnClickListener(this);
        this.btnPublic.setOnClickListener(this);
        et_select_date.setOnClickListener(this);
        this.btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnback:
                finish();
                break;
            case R.id.radioButton3:
                if (btnPublic.isChecked())
                    publicJob = "1";
                else
                    publicJob = "0";
                break;
            case R.id.et_select_date:
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getFragmentManager(), "datePicker");
                break;
            case R.id.button13:
                if (isValidFields()) {
                    this.createJob();
                }
                break;
        }
    }

    private void createJob() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.createJob(apiKey, ObjectUtil.getTextFromView(et_job_title), ObjectUtil.getTextFromView(et_select_date), ObjectUtil.getTextFromView(et_job_description), publicJob).enqueue(new Callback<EOCreateJob>() {
                @Override
                public void onResponse(Call<EOCreateJob> call, Response<EOCreateJob> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOCreateJob createJob = response.body();
                        if (!ObjectUtil.isEmpty(createJob)) {
                            if (createJob.getError() == RESPONSE_SUCCESS) {
                                Intent intent = new Intent(ActivityCreateJob.this, ActivityInterviewInvite.class);
                                intent.putExtra("jobId", createJob.getData().getJobId());
                                startActivity(intent);
                                ActivityCreateJob.this.finish();
                            } else {
                                Toast.makeText(ActivityCreateJob.this, "" + createJob.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOCreateJob> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityCreateJob.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean isValidFields() {
        String errorMsg = null;

        String jobTitle = ObjectUtil.getTextFromView(et_job_title);
        String jobDescription = ObjectUtil.getTextFromView(et_job_description);
        String selectedDate = ObjectUtil.getTextFromView(et_select_date);

        if (ObjectUtil.isEmptyStr(jobTitle) || ObjectUtil.isEmptyStr(jobDescription)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (ObjectUtil.isEmptyStr(selectedDate)) {
            errorMsg = "Please select job expiry date";
        }
        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker();
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            et_select_date.setClickable(true);
            String startDate = String.valueOf(month + 1).concat("/").concat(String.valueOf(dayOfMonth)).concat("/").concat(String.valueOf(year));
            Date date = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                date = simpleDateFormat.parse(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long millis = System.currentTimeMillis();
            java.sql.Date sysDate = new java.sql.Date(millis);
            if (date.after(sysDate)) {
                et_select_date.setText(startDate);
            } else {
                Toast toast = Toast.makeText(ApplicationHelper.application(), "Selected date must be greater from current date", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
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
