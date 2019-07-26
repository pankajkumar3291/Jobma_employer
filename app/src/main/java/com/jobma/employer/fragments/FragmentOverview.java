package com.jobma.employer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityApplicantsReport;
import com.jobma.employer.activities.ActivityInvite;
import com.jobma.employer.adapters.OverViewRecyclerviewAdapter;
import com.jobma.employer.adapters.OverviewSubAccountAdapter;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.dashboard.EOInterviewCounts;
import com.jobma.employer.model.dashboard.EOInterviewCountsData;
import com.jobma.employer.model.dashboard.EOOverviewData;
import com.jobma.employer.model.dashboard.EOOverviewList;
import com.jobma.employer.model.reportIssue.GetUserList;
import com.jobma.employer.model.subAccounts.EOSubAccounts;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import noman.weekcalendar.WeekCalendar;
import noman.weekcalendar.listener.OnDateClickListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentOverview extends Fragment implements View.OnClickListener {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private View view;
    private TextView tvMonth, tv_no_data, tv_no_interview_schedule, tvApplicantCounts, tvSelectedCounts, tvRejectedCounts, tvPendingCounts, tvOnHoldCounts;
    private RecyclerView recyclerInterviews, recyclerSubAccounts;
    private ArrayList<EOOverviewData> overviewDataArrayList;
    private ConstraintLayout applicantLayout;
    private WeekCalendar weekCalendar;
    private ImageView nextButton, dtbackbtn, ivPlusIcon;
    private DateTime dateTime = new DateTime();
    private SimpleDateFormat month_date, currentDateInterview;
    private Calendar calendar;
    private int offset = 1;
    private static final String PER_PAGE_ITEMS = "6";
    private ArrayList<GetUserList> subAccountList = new ArrayList<>();
    private OverviewSubAccountAdapter subAccountAdapter;
    private OverViewRecyclerviewAdapter overViewRecyclerviewAdapter;
    private EOInterviewCountsData interviewCountsData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_overview, container, false);

        this.initView();
        this.setOnClickListener();

        this.getInterviewCounts();
        this.getSubAccountsApi();

        return view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.recyclerInterviews = view.findViewById(R.id.recyclerInterviews);
        this.recyclerSubAccounts = view.findViewById(R.id.recyclerSubAccounts);
        this.applicantLayout = view.findViewById(R.id.applicant_layout);
        this.dtbackbtn = view.findViewById(R.id.imageView38);
        this.tvMonth = view.findViewById(R.id.textView79);
        this.nextButton = view.findViewById(R.id.imageView39);
        this.weekCalendar = view.findViewById(R.id.weekCalendar);

        this.tv_no_data = view.findViewById(R.id.tv_no_data);
        this.tvApplicantCounts = view.findViewById(R.id.textView69);
        this.tvSelectedCounts = view.findViewById(R.id.textView74);
        this.tvRejectedCounts = view.findViewById(R.id.textView75);
        this.tvPendingCounts = view.findViewById(R.id.textView76);
        this.tvOnHoldCounts = view.findViewById(R.id.textView77);
        this.ivPlusIcon = view.findViewById(R.id.imageView85);
        this.tv_no_interview_schedule = view.findViewById(R.id.tv_no_interview_schedule);

        this.month_date = new SimpleDateFormat("MMMM - yyyy");
        this.calendar = Calendar.getInstance();
        this.calendar.set(Calendar.MONTH, dateTime.getMonthOfYear() - 1);
        this.tvMonth.setText(month_date.format(calendar.getTime()));

        //TODO to get current date schedule interviews
        this.currentDateInterview = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = currentDateInterview.format(new Date());
        if (!ObjectUtil.isEmpty(currentDate)) {
            getScheduleInterviews(currentDate);
        }

        weekCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(DateTime dateTime) {
                LocalDate localDate = new LocalDate(dateTime);
                getScheduleInterviews(localDate.toString());
            }
        });

    }

    private void setOnClickListener() {
        this.applicantLayout.setOnClickListener(this);
        this.dtbackbtn.setOnClickListener(this);
        this.nextButton.setOnClickListener(this);
        this.ivPlusIcon.setOnClickListener(this);
    }

    private void getScheduleInterviews(String localDate) {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.scheduleInterviewsData(this.apiKey, localDate).enqueue(new Callback<EOOverviewList>() {
                @Override
                public void onResponse(Call<EOOverviewList> call, Response<EOOverviewList> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOOverviewList eoOverviewList = response.body();
                        if (!ObjectUtil.isEmpty(eoOverviewList)) {
                            if (eoOverviewList.getError() == 0) {

                                if (overviewDataArrayList == null) {
                                    overviewDataArrayList = new ArrayList<>();
                                } else {
                                    overviewDataArrayList.clear();
                                }

                                if (!ObjectUtil.isEmpty(eoOverviewList.getData())) {
                                    overviewDataArrayList.addAll(eoOverviewList.getData());
                                    tv_no_interview_schedule.setVisibility(View.GONE);
                                    recyclerInterviews.setVisibility(View.VISIBLE);
                                    recyclerInterviews.setHasFixedSize(true);
                                    overViewRecyclerviewAdapter = new OverViewRecyclerviewAdapter(getActivity(), overviewDataArrayList);
                                    recyclerInterviews.setItemAnimator(new DefaultItemAnimator());
                                    recyclerInterviews.setAdapter(overViewRecyclerviewAdapter);
                                } else {
                                    tv_no_interview_schedule.setVisibility(View.VISIBLE);
                                    recyclerInterviews.setVisibility(View.GONE);
                                }
                            } else {
                                progress.hideProgressBar();
                                Toast.makeText(getActivity(), "" + eoOverviewList.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        progress.hideProgressBar();
                        tv_no_interview_schedule.setVisibility(View.VISIBLE);
                        recyclerInterviews.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<EOOverviewList> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getInterviewCounts() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.getInterviewCounts(this.apiKey).enqueue(new Callback<EOInterviewCounts>() {
                @Override
                public void onResponse(Call<EOInterviewCounts> call, Response<EOInterviewCounts> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {

                        EOInterviewCounts eoInterviewCounts = response.body();

                        if (!ObjectUtil.isEmpty(eoInterviewCounts)) {
                            if (eoInterviewCounts.getError() == 0) {
                                interviewCountsData = eoInterviewCounts.getData();
                                dataToView();
                            } else {
                                progress.hideProgressBar();
                                Toast.makeText(getActivity(), "" + eoInterviewCounts.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOInterviewCounts> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void dataToView() {
        if (!ObjectUtil.isEmpty(interviewCountsData)) {
            this.tvApplicantCounts.setText(ObjectUtil.isEmpty(interviewCountsData.getApplication()) ? "0" : String.valueOf(interviewCountsData.getApplication()));
            this.tvSelectedCounts.setText(ObjectUtil.isEmpty(interviewCountsData.getSelected()) ? "0" : String.valueOf(interviewCountsData.getSelected()));
            this.tvRejectedCounts.setText(ObjectUtil.isEmpty(interviewCountsData.getRejected()) ? "0" : String.valueOf(interviewCountsData.getRejected()));
            this.tvPendingCounts.setText(ObjectUtil.isEmpty(interviewCountsData.getPending()) ? "0" : String.valueOf(interviewCountsData.getPending()));
            this.tvOnHoldCounts.setText(ObjectUtil.isEmpty(interviewCountsData.getOnHold()) ? "0" : String.valueOf(interviewCountsData.getOnHold()));
        }
    }

    private void getSubAccountsApi() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.subAccountsUsers(this.apiKey, offset, PER_PAGE_ITEMS).enqueue(new Callback<EOSubAccounts>() {
                @Override
                public void onResponse(Call<EOSubAccounts> call, Response<EOSubAccounts> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOSubAccounts accountRequest = response.body();
                        if (!ObjectUtil.isEmpty(accountRequest.getData())) {
                            if (accountRequest.getError() == 0) {

                                if (!ObjectUtil.isEmpty(accountRequest.getData().getGetUserList())) {
                                    subAccountList.addAll(accountRequest.getData().getGetUserList());
                                    tv_no_data.setVisibility(View.GONE);
                                    recyclerSubAccounts.setVisibility(View.VISIBLE);
                                    recyclerSubAccounts.setHasFixedSize(true);
                                    subAccountAdapter = new OverviewSubAccountAdapter(getActivity(), subAccountList);
                                    recyclerSubAccounts.setItemAnimator(new DefaultItemAnimator());
                                    recyclerSubAccounts.setAdapter(subAccountAdapter);
                                } else {
                                    tv_no_data.setVisibility(View.VISIBLE);
                                    recyclerSubAccounts.setVisibility(View.GONE);
                                }

                            } else {
                                progress.hideProgressBar();
                                tv_no_data.setVisibility(View.VISIBLE);
                                recyclerSubAccounts.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "" + accountRequest.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progress.hideProgressBar();
                            tv_no_data.setVisibility(View.VISIBLE);
                            recyclerSubAccounts.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOSubAccounts> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.applicant_layout:
                Intent applicantReportsIntent = new Intent(getActivity(), ActivityApplicantsReport.class);
                applicantReportsIntent.putExtra("countValues", interviewCountsData);
                this.startActivity(applicantReportsIntent);
                break;
            case R.id.imageView38:
                dateTime = dateTime.minusDays(7);
                weekCalendar.setStartDate(dateTime);
                calendar.set(Calendar.MONTH, dateTime.getMonthOfYear());
                tvMonth.setText(month_date.format(calendar.getTime()));
                break;

            case R.id.imageView39:
                dateTime = dateTime.plusDays(7);
                weekCalendar.setStartDate(dateTime);
                calendar.set(Calendar.MONTH, dateTime.getMonthOfYear());
                tvMonth.setText(month_date.format(calendar.getTime()));
                break;
            case R.id.imageView85:
                this.startActivity(new Intent(getActivity(), ActivityInvite.class));
                break;
        }
    }


}
