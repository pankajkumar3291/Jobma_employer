package com.jobma.employer.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.util.ObjectUtil;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ActivityReportFilter extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private ConstraintLayout layoutAppliedOn, layout_jobtitle;
    private TextView tvkeywords, tvappliedon, tvjobtitle, tvclear, tvstartDate, tvendDate;
    private ImageView btnback;
    private EditText et_search_by_email, et_job_title;
    private Button btnFilter;
    private boolean isStartdate = false;
    private String startdate = "";
    private Map<String, String> filteredMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_filter);

        this.initView();
        this.setOnClickListner();

        this.tvkeywords.setBackgroundColor(getResources().getColor(R.color.colorDarkSkyBlue));
        this.tvkeywords.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());

        this.layoutAppliedOn = findViewById(R.id.constraintLayout10);
        this.layout_jobtitle = findViewById(R.id.layout_jobtitle);
        this.btnback = findViewById(R.id.btnback);
        this.tvkeywords = findViewById(R.id.textView92);
        this.tvappliedon = findViewById(R.id.textView93);
        this.tvjobtitle = findViewById(R.id.textView94);
        this.et_search_by_email = findViewById(R.id.et_search_by_email);
        this.tvclear = findViewById(R.id.tvclear);
        this.tvstartDate = findViewById(R.id.textView95);
        this.tvendDate = findViewById(R.id.textView96);
        this.et_job_title = this.findViewById(R.id.et_job_title);
        this.btnFilter = this.findViewById(R.id.button9);
    }

    private void setOnClickListner() {
        this.btnback.setOnClickListener(this);
        this.tvkeywords.setOnClickListener(this);
        this.tvappliedon.setOnClickListener(this);
        this.tvjobtitle.setOnClickListener(this);
        this.tvclear.setOnClickListener(this);
        this.tvstartDate.setOnClickListener(this);
        this.tvendDate.setOnClickListener(this);
        this.btnFilter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView92:
                this.setlayout("keyword");
                break;
            case R.id.textView93:
                this.setlayout("applied");
                break;
            case R.id.textView94:
                this.setlayout("jobTitle");
                break;
            case R.id.btnback:
                this.finish();
                break;
            case R.id.tvclear:
                this.clear();
                break;
            case R.id.textView95:
                this.setDate("start");
                break;
            case R.id.textView96:
                if (isStartdate)
                    this.setDate("end");
                else
                    Toast.makeText(ActivityReportFilter.this, "Please select start date", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button9:
                this.getAllData();
                break;
        }
    }

    private void getAllData() {
        String startDate = tvstartDate.getText().toString().equalsIgnoreCase("Select Start Date") ? "" : tvstartDate.getText().toString();
        String endDate = tvendDate.getText().toString().equalsIgnoreCase("Select End Date") ? "" : tvendDate.getText().toString();

        if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_search_by_email)) && ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_job_title)) && ObjectUtil.isEmpty(startDate) && ObjectUtil.isEmpty(endDate)) {
            filteredMap.put("keyword", "");
            filteredMap.put("job_title", "");
            Intent intent = new Intent();
            intent.putExtra("mapData", (Serializable) filteredMap);
            setResult(RESULT_OK, intent);
            ActivityReportFilter.this.finish();
        } else if (!ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_search_by_email))) {
            filteredMap.put("keyword", et_search_by_email.getText().toString().trim());
            Intent intent = new Intent();
            intent.putExtra("mapData", (Serializable) filteredMap);
            setResult(RESULT_OK, intent);
            ActivityReportFilter.this.finish();
        } else if (!ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_job_title))) {
            filteredMap.put("job_title", et_job_title.getText().toString().trim());
            Intent intent = new Intent();
            intent.putExtra("mapData", (Serializable) filteredMap);
            setResult(RESULT_OK, intent);
            ActivityReportFilter.this.finish();
        } else {
            if (ObjectUtil.isEmpty(startDate) || ObjectUtil.isEmpty(endDate)) {
                Toast.makeText(ActivityReportFilter.this, "Please select end date", Toast.LENGTH_SHORT).show();
            } else {
                filteredMap.put("start_date", startDate);
                filteredMap.put("end_date", endDate);
                Intent intent = new Intent();
                intent.putExtra("mapData", (Serializable) filteredMap);
                setResult(RESULT_OK, intent);
                ActivityReportFilter.this.finish();
            }
        }

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
        } else if (value.equalsIgnoreCase("applied")) {
            tvkeywords.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvkeywords.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvappliedon.setBackgroundColor(getResources().getColor(R.color.colorDarkSkyBlue));
            tvappliedon.setTextColor(getResources().getColor(R.color.colorWhite));
            tvjobtitle.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvjobtitle.setTextColor(getResources().getColor(R.color.text_gray_color));
            et_search_by_email.setVisibility(View.GONE);
            layoutAppliedOn.setVisibility(View.VISIBLE);
            layout_jobtitle.setVisibility(View.GONE);
        } else if (value.equalsIgnoreCase("jobTitle")) {
            tvkeywords.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvkeywords.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvappliedon.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            tvappliedon.setTextColor(getResources().getColor(R.color.text_gray_color));
            tvjobtitle.setBackgroundColor(getResources().getColor(R.color.colorDarkSkyBlue));
            tvjobtitle.setTextColor(getResources().getColor(R.color.colorWhite));
            et_search_by_email.setVisibility(View.GONE);
            layoutAppliedOn.setVisibility(View.GONE);
            layout_jobtitle.setVisibility(View.VISIBLE);
        }
    }

    private void clear() {
        this.et_search_by_email.setText("");
        this.tvstartDate.setText("Select Start Date");
        this.tvendDate.setText("Select End Date");
        this.et_job_title.setText("");
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
                        Toast toast = Toast.makeText(ActivityReportFilter.this, "After Date Greater than start date", Toast.LENGTH_LONG);
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
