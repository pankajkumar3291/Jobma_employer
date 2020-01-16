package com.jobma.employer.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.google.android.material.textfield.TextInputLayout;
import com.jobma.employer.R;
import com.jobma.employer.adapters.EvaluationFilterAccountAdapter;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.applicants.Catcher;
import com.jobma.employer.model.applicants.EOAccount;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class ActivityEvaluateFilter extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private List<Catcher> accountList = new ArrayList<>();
    private EvaluationFilterAccountAdapter evaluationFilterAccountAdapter;
    private TextInputLayout etsearch;
    private ConstraintLayout layoutAppliedOn, layout_jobtitle;
    private TextView tvkeywords, tvappliedon, tvjobtitle, tvposted, tvaccount, tvclear, tvstartDate, tvendDate;
    private ImageView btnback;
    private RadioGroup radioGroup, statupRadioGroup;
    private RecyclerView recAccount;
    private RadioButton spec1;
    private RadioButton spec2;
    private List<String> accountUserId = new ArrayList<>();
    private String approval = "";
    private String status = "";
    private String startdate = "";
    private boolean isStartdate = false;
    private Map<String, String> params = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate_filter);

        this.initView();
        tvkeywords.setBackgroundColor(getResources().getColor(R.color.colorDarkSkyBlue));
        tvkeywords.setTextColor(getResources().getColor(R.color.colorWhite));
        this.accountApi();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());

        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        recAccount = findViewById(R.id.rec_account);
        layoutAppliedOn = findViewById(R.id.constraintLayout10);
        layout_jobtitle = findViewById(R.id.layout_jobtitle);
        btnback = findViewById(R.id.btnback);
        statupRadioGroup = findViewById(R.id.radiogroup111);
        radioGroup = findViewById(R.id.radiogroup11);
        btnback.setOnClickListener(this);
        tvkeywords = findViewById(R.id.textView92);
        tvkeywords.setOnClickListener(this);
        tvappliedon = findViewById(R.id.textView93);
        tvappliedon.setOnClickListener(this);
        tvjobtitle = findViewById(R.id.textView94);
        tvjobtitle.setOnClickListener(this);
        etsearch = findViewById(R.id.textInputLayout8);
        tvposted = findViewById(R.id.textView148);
        tvposted.setOnClickListener(this);
        tvaccount = findViewById(R.id.textView149);
        tvaccount.setOnClickListener(this);
        tvclear = findViewById(R.id.tvclear);
        tvclear.setOnClickListener(this);
        tvstartDate = findViewById(R.id.textView95);
        tvstartDate.setOnClickListener(this);
        tvendDate = findViewById(R.id.textView96);
        tvendDate.setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
    }

    private void accountApi() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            try {
                apiInterface.getAccountList(this.apiKey).enqueue(new Callback<EOAccount>() {
                    @Override
                    public void onResponse(Call<EOAccount> call, Response<EOAccount> response) {
                        progress.hideProgressBar();
                        if (response.body().getError() == 0) {
                            accountList = response.body().getData();
                            evaluationFilterAccountAdapter = new EvaluationFilterAccountAdapter(accountList, ActivityEvaluateFilter.this);
                            recAccount.setAdapter(evaluationFilterAccountAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<EOAccount> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            Toast.makeText(ActivityEvaluateFilter.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView92:
                setlayout("keyword");
                break;
            case R.id.textView93:
                setlayout("applied");
                break;
            case R.id.textView94:
                setlayout("jobtitle");
                break;
            case R.id.btnback:
                finish();
                break;
            case R.id.textView148:
                setlayout("posted");
                break;
            case R.id.textView149:
                setlayout("account");
                break;
            case R.id.tvclear:
                clear();
                break;
            case R.id.textView95://start date click
                setDate("start");
                break;
            case R.id.textView96:
                if (isStartdate)
                    setDate("end");
                else
                    Toast.makeText(ActivityEvaluateFilter.this, "Please select start date", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button9:
                getalldata();
                break;
        }
    }

    private void getalldata() {

        accountUserId.clear();
        String staccountlist = "";

        RadioButton spec1 = findViewById(statupRadioGroup.getCheckedRadioButtonId());
        RadioButton spec2 = findViewById(radioGroup.getCheckedRadioButtonId());

        if (spec1 != null && spec1.isChecked()) {
            if (spec1.getText().toString().equalsIgnoreCase("Activated"))
                status = "1";
            else if (spec1.getText().toString().equalsIgnoreCase("Deactivated"))
                status = "2";
            else
                status = "0";
        } else
            status = "";
        if (spec2 != null && spec2.isChecked()) {
            if (spec2.getText().toString().equalsIgnoreCase("Approved"))
                approval = "1";
            else
                approval = "2";
        } else
            approval = "";

        for (Catcher catcher : accountList) {
            if (catcher.isIschecked()) {
                accountUserId.add(catcher.getId().toString());
            }
        }
        staccountlist = TextUtils.join(", ", accountUserId);
        params.put("approval", approval);
        params.put("status", status);
        params.put("title", etsearch.getEditText().getText().toString());
        params.put("start_date", tvstartDate.getText().toString().equalsIgnoreCase("Select Start Date") ? "" : tvstartDate.getText().toString());
        params.put("end_date", tvendDate.getText().toString().equalsIgnoreCase("Select End Date") ? "" : tvendDate.getText().toString());
        params.put("employer_id", staccountlist);

        Intent intent = new Intent();
        intent.putExtra("mapData", (Serializable) params);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setlayout(String value) {
        if (value.equalsIgnoreCase("keyword")) {
            tvkeywords.setBackgroundColor(getResources().getColor(R.color.colorDarkSkyBlue));
            tvkeywords.setTextColor(getResources().getColor(R.color.colorWhite));
            tvappliedon.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvappliedon.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvjobtitle.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvjobtitle.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvaccount.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvaccount.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvposted.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvposted.setTextColor(getResources().getColor(R.color.text_gray_color));
            etsearch.setVisibility(View.VISIBLE);
            layoutAppliedOn.setVisibility(View.GONE);
            layout_jobtitle.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            statupRadioGroup.setVisibility(View.GONE);
            recAccount.setVisibility(View.GONE);
        } else if (value.equalsIgnoreCase("applied")) {
            tvkeywords.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvkeywords.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvappliedon.setBackgroundColor(getResources().getColor(R.color.colorDarkSkyBlue));
            tvappliedon.setTextColor(getResources().getColor(R.color.colorWhite));
            tvjobtitle.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvjobtitle.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvposted.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvposted.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvaccount.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvaccount.setTextColor(getResources().getColor(R.color.text_gray_color));
            etsearch.setVisibility(View.GONE);
            statupRadioGroup.setVisibility(View.GONE);
            radioGroup.setVisibility(View.VISIBLE);
            layoutAppliedOn.setVisibility(View.GONE);
            layout_jobtitle.setVisibility(View.GONE);
            recAccount.setVisibility(View.GONE);
        } else if (value.equalsIgnoreCase("jobtitle")) {
            tvkeywords.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvkeywords.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvappliedon.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvappliedon.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvposted.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvposted.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvaccount.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvaccount.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvjobtitle.setBackgroundColor(getResources().getColor(R.color.colorDarkSkyBlue));
            tvjobtitle.setTextColor(getResources().getColor(R.color.colorWhite));
            etsearch.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            layoutAppliedOn.setVisibility(View.GONE);
            statupRadioGroup.setVisibility(View.VISIBLE);
            layout_jobtitle.setVisibility(View.GONE);
            recAccount.setVisibility(View.GONE);
        } else if (value.equalsIgnoreCase("posted")) {
            tvkeywords.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvkeywords.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvappliedon.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvappliedon.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvjobtitle.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvjobtitle.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvposted.setBackgroundColor(getResources().getColor(R.color.colorDarkSkyBlue));
            tvposted.setTextColor(getResources().getColor(R.color.colorWhite));
            tvaccount.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvaccount.setTextColor(getResources().getColor(R.color.text_gray_color));
            etsearch.setVisibility(View.GONE);
            layoutAppliedOn.setVisibility(View.VISIBLE);
            layout_jobtitle.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            statupRadioGroup.setVisibility(View.GONE);
            recAccount.setVisibility(View.GONE);
        } else if (value.equalsIgnoreCase("account")) {
            tvkeywords.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvkeywords.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvappliedon.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvappliedon.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvjobtitle.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvjobtitle.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvposted.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvposted.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvaccount.setBackgroundColor(getResources().getColor(R.color.colorDarkSkyBlue));
            tvaccount.setTextColor(getResources().getColor(R.color.colorWhite));
            etsearch.setVisibility(View.GONE);
            layoutAppliedOn.setVisibility(View.GONE);
            layout_jobtitle.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            statupRadioGroup.setVisibility(View.GONE);
            recAccount.setVisibility(View.VISIBLE);
        }
    }

    private void clear() {
        isStartdate = false;
        etsearch.getEditText().setText("");
        tvstartDate.setText("Select Start Date");
        tvendDate.setText("Select end date");
        spec1 = findViewById(statupRadioGroup.getCheckedRadioButtonId());
        spec2 = findViewById(radioGroup.getCheckedRadioButtonId());
        if (spec1 != null && spec1.isChecked()) {
            spec1.setChecked(false);
        }
        if (spec2 != null && spec2.isChecked()) {
            spec2.setChecked(false);
        }
        for (Catcher catcher : accountList) {
            catcher.setIschecked(false);
        }
        evaluationFilterAccountAdapter.notifyDataSetChanged();
    }

    public void setDate(final String textView) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthofyear, int dayofmonth) {

                if (textView.equalsIgnoreCase("start")) {
                    tvstartDate.setText((monthofyear + 1) + "-" + dayofmonth + "-" + year);
                    startdate = (monthofyear + 1) + "/" + dayofmonth + "/" + year;
                    tvendDate.setText("Select End Date");
                    isStartdate = true;
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    Date strDate = null;
                    Date endDate = null;
                    try {
                        strDate = sdf.parse(startdate);
                        endDate = sdf.parse((monthofyear + 1) + "/" + dayofmonth + "/" + year);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (endDate.before(strDate) || endDate.equals(strDate)) {
                        Toast toast = Toast.makeText(ActivityEvaluateFilter.this, "End date greater than start date", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        tvendDate.setText((monthofyear + 1) + "-" + dayofmonth + "-" + year);
                    }

                }
            }
        }, year, month, day);
        dpd.show();
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