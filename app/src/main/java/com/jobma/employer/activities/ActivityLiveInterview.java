package com.jobma.employer.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.google.android.material.textfield.TextInputEditText;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.setupInterview.EOTimeZoneData;
import com.jobma.employer.model.setupInterview.EOTimeZoneList;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.GlobalUtil;
import com.jobma.employer.util.ObjectUtil;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class ActivityLiveInterview extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private Spinner spinner, spinnerTimeZone;
    private ImageView btnBack;
    private Button btnNext;
    private TextInputEditText et_full_name, et_email_id, et_phone_number;
    private static TextInputEditText et_select_date, et_start_time, et_end_time;
    private int jobId;
    private ArrayList<EOTimeZoneData> timeZoneDataList = new ArrayList<>();
    private String selectedTimeZone;
    public static WeakReference<ActivityLiveInterview> activityLiveInterview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_interview);

        activityLiveInterview = new WeakReference<>(this);

        if (!ObjectUtil.isEmpty(this.getIntent().getIntExtra("jobId", 0)))
            jobId = this.getIntent().getIntExtra("jobId", 0);

        this.initView();
        this.setDataToSpinner();
        this.setOnClickListener();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.btnBack = this.findViewById(R.id.btnBack);
        this.spinner = this.findViewById(R.id.spinner8);
        this.btnNext = this.findViewById(R.id.button20);
        et_select_date = this.findViewById(R.id.et_select_date);
        et_start_time = this.findViewById(R.id.et_start_time);
        et_end_time = this.findViewById(R.id.et_end_time);
        this.et_full_name = this.findViewById(R.id.et_full_name);
        this.et_email_id = this.findViewById(R.id.et_email_id);
        this.et_phone_number = this.findViewById(R.id.et_phone_number);
        this.spinnerTimeZone = this.findViewById(R.id.spinnerTimeZone);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.spinner.setSelection(1);
        this.getTimeZone();
    }

    private void setOnClickListener() {
        this.btnNext.setOnClickListener(this);
        this.spinner.setOnItemSelectedListener(onJobSelectedListener);
        this.btnBack.setOnClickListener(this);
        et_select_date.setOnClickListener(this);
        et_start_time.setOnClickListener(this);
        et_end_time.setOnClickListener(this);
    }

    private void setDataToSpinner() {
        ArrayAdapter<String> interviewAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getResources().getStringArray(R.array.invite));
        interviewAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(interviewAdapter);
    }

    AdapterView.OnItemSelectedListener onJobSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (((String) parent.getItemAtPosition(position)).equalsIgnoreCase("Pre-recorded Video Interview")) {
                ActivityLiveInterview.this.finish();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void getTimeZone() {
        progress.showProgressBar();
        apiInterface.getTimeZone().enqueue(new Callback<EOTimeZoneList>() {
            @Override
            public void onResponse(Call<EOTimeZoneList> call, Response<EOTimeZoneList> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EOTimeZoneList eoTimeZoneList = response.body();
                    if (!ObjectUtil.isEmpty(eoTimeZoneList)) {
                        if (eoTimeZoneList.getError() == RESPONSE_SUCCESS) {
                            timeZoneDataList = (ArrayList<EOTimeZoneData>) eoTimeZoneList.getData();
                            dataToView();
                        } else {
                            Toast.makeText(ActivityLiveInterview.this, "" + eoTimeZoneList.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EOTimeZoneList> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(ActivityLiveInterview.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void dataToView() {
        ArrayList<String> timeZoneList = new ArrayList<>();
        if (!ObjectUtil.isEmpty(timeZoneDataList)) {
            for (EOTimeZoneData timeZoneData : timeZoneDataList) {
                timeZoneList.add(timeZoneData.getKey());
            }
        }
        SpinnerAdapter arrayAdapter = new SpinnerAdapter(this, R.layout.spinner_item);
        arrayAdapter.addAll(timeZoneList);
        arrayAdapter.add("Select time zone");
        spinnerTimeZone.setAdapter(arrayAdapter);
        spinnerTimeZone.setSelection(arrayAdapter.getCount());
        spinnerTimeZone.setOnItemSelectedListener(onTimeZoneSelectedListener);
    }

    AdapterView.OnItemSelectedListener onTimeZoneSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedTimeZone = (String) parent.getItemAtPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                this.finish();
                break;
            case R.id.button20:
                if (isValidAllFields()) {
                    Intent intentMessage = new Intent(ActivityLiveInterview.this, ActivityInviteMessage.class);
                    intentMessage.putExtra("selectedDate", ObjectUtil.getTextFromView(et_select_date));
                    intentMessage.putExtra("interViewMode", "2");
                    intentMessage.putExtra("jobId", jobId);
                    intentMessage.putExtra("timeZone", selectedTimeZone);
                    intentMessage.putExtra("startTime", ObjectUtil.getTextFromView(et_start_time));
                    intentMessage.putExtra("endTime", ObjectUtil.getTextFromView(et_end_time));
                    intentMessage.putExtra("name", ObjectUtil.getTextFromView(et_full_name));
                    intentMessage.putExtra("email", ObjectUtil.getTextFromView(et_email_id));
                    intentMessage.putExtra("phone", ObjectUtil.getTextFromView(et_phone_number));
                    startActivity(intentMessage);
                }
                break;
            case R.id.et_select_date:
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getFragmentManager(), "datePicker");
                break;
            case R.id.et_start_time:
                this.openTimePicker(0);
                break;
            case R.id.et_end_time:
                this.openTimePicker(1);
                break;
        }
    }

    private boolean isValidAllFields() {
        String errorMsg = null;

        String selectDate = ObjectUtil.getTextFromView(et_select_date);
        String startTime = ObjectUtil.getTextFromView(et_start_time);
        String endTime = ObjectUtil.getTextFromView(et_end_time);
        String fullName = ObjectUtil.getTextFromView(et_full_name);
        String emailId = ObjectUtil.getTextFromView(et_email_id);
        String phoneNumber = ObjectUtil.getTextFromView(et_phone_number);

        if (ObjectUtil.isEmptyStr(selectDate) || ObjectUtil.isEmptyStr(startTime) || ObjectUtil.isEmptyStr(endTime)
                || ObjectUtil.isEmptyStr(fullName) || ObjectUtil.isEmptyStr(emailId) || ObjectUtil.isEmptyStr(phoneNumber)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (!GlobalUtil.isValidEmail(emailId)) {
            errorMsg = this.getString(R.string.valid_email);
        } else if (selectedTimeZone.equalsIgnoreCase("Select time zone")) {
            errorMsg = "Please select time zone";
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
            String startDate = String.valueOf(year).concat("-").concat(String.valueOf(month + 1)).concat("-").concat(String.valueOf(dayOfMonth));

            Date date = null;
            Date SystemDate = null;
            long millis = System.currentTimeMillis();
            java.sql.Date sysDate = new java.sql.Date(millis);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = simpleDateFormat.parse(startDate);
                SystemDate = simpleDateFormat.parse(sysDate.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (date.before(SystemDate)) {
                Toast toast = Toast.makeText(ApplicationHelper.application(), "You can not schedule interview in back date", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else if (date.equals(SystemDate)) {
                et_select_date.setText(startDate);
            } else {
                et_select_date.setText(startDate);
            }
        }
    }

    private Date date1 = null;

    public void openTimePicker(final int value) {
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(ActivityLiveInterview.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (value == 0) {
                    et_start_time.setText(selectedHour + ":" + (selectedMinute < 10 ? "0" + selectedMinute : selectedMinute));
                    et_end_time.setText("");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    try {
                        date1 = simpleDateFormat.parse(selectedHour + ":" + selectedMinute);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    String temp;
                    if (TextUtils.isEmpty(et_start_time.getText().toString())) {
                        Toast.makeText(ActivityLiveInterview.this, "please select start time first", Toast.LENGTH_SHORT).show();
                    } else {

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        Date endDate = null;
                        try {
                            endDate = simpleDateFormat.parse(selectedHour + ":" + selectedMinute);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long difference = endDate.getTime() - date1.getTime();
                        if (difference < 0) {
                            Date dateMax = null;
                            Date dateMin = null;
                            try {
                                dateMax = simpleDateFormat.parse("24:00");
                                dateMin = simpleDateFormat.parse("00:00");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            difference = (dateMax.getTime() - date1.getTime()) + (endDate.getTime() - dateMin.getTime());
                        }
                        int days = (int) (difference / (1000 * 60 * 60 * 24));
                        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);

                        if ((hours == 2 && min == 0) || (hours < 2 && min <= 60)) {
                            et_end_time.setText(selectedHour + ":" + (selectedMinute < 10 ? "0" + selectedMinute : selectedMinute));
                        } else {
                            Toast.makeText(ActivityLiveInterview.this, "Maximum interview hours will not exceed from 2 hours", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }, hour, minute, false); //Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
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
