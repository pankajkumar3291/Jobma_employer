package com.jobma.employer.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.google.android.material.textfield.TextInputEditText;
import com.jobma.employer.R;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityFilter extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private TextInputEditText et_search_by_email;
    private ConstraintLayout layoutAppliedOn, layout_jobtitle;
    private TextView tvkeywords, tvappliedon, tvjobtitle, tvclear, tvstartDate, tvendDate;
    private ImageView btnback;
    private Spinner spapplijob;
    private RadioGroup radioGroup, statupRadioGroup;
    private Button btnFilter;
    private DatePickerDialog datePickerDialog;
    private boolean isStartdate = false;
    private String startdate = "";
    private String approval = "";
    private String status = "";
    private Map<String, String> filteredMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        this.initView();
        this.checkFilter();

        this.tvkeywords.setBackgroundColor(getResources().getColor(R.color.colorDarkSkyBlue));
        this.tvkeywords.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    private void checkFilter() {
        if (getIntent().hasExtra("subAccount")) {
            tvkeywords.setText("Name");
            tvappliedon.setText("Approval");
            tvjobtitle.setText("Status");
        }
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());

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
        et_search_by_email = findViewById(R.id.et_search_by_email);
        tvclear = findViewById(R.id.tvclear);
        tvclear.setOnClickListener(this);
        tvstartDate = findViewById(R.id.textView95);
        tvstartDate.setOnClickListener(this);
        tvendDate = findViewById(R.id.textView96);
        tvendDate.setOnClickListener(this);
        spapplijob = findViewById(R.id.spinner3);
        this.btnFilter = this.findViewById(R.id.button9);
        this.btnFilter.setOnClickListener(this);
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
            case R.id.btnback://back button click
                finish();
                break;
            case R.id.tvclear://clear button click
                clear();
                break;
            case R.id.textView95://start date click
                setDate("start");
                break;
            case R.id.textView96:
                if (isStartdate)
                    setDate("end");
                else
                    Toast.makeText(ActivityFilter.this, "Please select start date", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button9:
                this.getAllData();
                break;
        }
    }

    private void getAllData() {

        RadioButton radioButton1 = findViewById(statupRadioGroup.getCheckedRadioButtonId());
        RadioButton radioButton2 = findViewById(radioGroup.getCheckedRadioButtonId());

        if (radioButton1 != null && radioButton1.isChecked()) {
            if (radioButton1.getText().toString().equalsIgnoreCase("Activated"))
                status = "1";
            else
                status = "0";
        } else
            status = "";

        if (radioButton2 != null && radioButton2.isChecked()) {
            if (radioButton2.getText().toString().equalsIgnoreCase("Approved"))
                approval = "1";
            else
                approval = "0";
        } else
            approval = "";

        filteredMap.put("approval", approval);
        filteredMap.put("confirmed", status);
        filteredMap.put("keyword", et_search_by_email.getText().toString().trim());

        Intent intent = new Intent();
        intent.putExtra("mapData", (Serializable) filteredMap);
        setResult(RESULT_OK, intent);
        ActivityFilter.this.finish();
    }


    private void setlayout(String value) {
        if (value.equalsIgnoreCase("keyword")) {
            tvkeywords.setBackgroundColor(getResources().getColor(R.color.colorDarkSkyBlue));
            tvkeywords.setTextColor(getResources().getColor(R.color.colorWhite));
            tvappliedon.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvappliedon.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvjobtitle.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvjobtitle.setTextColor(getResources().getColor(R.color.text_gray_color));
            et_search_by_email.setVisibility(View.VISIBLE);
            layoutAppliedOn.setVisibility(View.GONE);
            layout_jobtitle.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            statupRadioGroup.setVisibility(View.GONE);
        } else if (value.equalsIgnoreCase("applied")) {
            tvkeywords.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvkeywords.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvappliedon.setBackgroundColor(getResources().getColor(R.color.colorDarkSkyBlue));
            tvappliedon.setTextColor(getResources().getColor(R.color.colorWhite));
            tvjobtitle.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvjobtitle.setTextColor(getResources().getColor(R.color.text_gray_color));
            et_search_by_email.setVisibility(View.GONE);
            statupRadioGroup.setVisibility(View.GONE);
            if (getIntent().hasExtra("subAccount")) {
                radioGroup.setVisibility(View.VISIBLE);
            } else {
                layoutAppliedOn.setVisibility(View.VISIBLE);
            }
            layout_jobtitle.setVisibility(View.GONE);

        } else if (value.equalsIgnoreCase("jobtitle")) {
            tvkeywords.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvkeywords.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvappliedon.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvappliedon.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvjobtitle.setBackgroundColor(getResources().getColor(R.color.colorDarkSkyBlue));
            tvjobtitle.setTextColor(getResources().getColor(R.color.colorWhite));

            et_search_by_email.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            layoutAppliedOn.setVisibility(View.GONE);
            if (getIntent().hasExtra("subAccount")) {
                statupRadioGroup.setVisibility(View.VISIBLE);
            } else {
                layout_jobtitle.setVisibility(View.VISIBLE);
            }
        }
    }

    private void clear() {
        et_search_by_email.setText("");
        tvstartDate.setText("Select Start Date");
        tvendDate.setText("Select End Date");
        spapplijob.setSelection(0);
        RadioButton radioButton1 = findViewById(statupRadioGroup.getCheckedRadioButtonId());
        RadioButton radioButton2 = findViewById(radioGroup.getCheckedRadioButtonId());

        if (radioButton1 != null && radioButton1.isChecked()) {
            radioButton1.setChecked(false);
        }

        if (radioButton2 != null && radioButton2.isChecked()) {
            radioButton2.setChecked(false);
        }
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
                        Toast toast = Toast.makeText(ActivityFilter.this, "After Date Greater than start date", Toast.LENGTH_LONG);
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
