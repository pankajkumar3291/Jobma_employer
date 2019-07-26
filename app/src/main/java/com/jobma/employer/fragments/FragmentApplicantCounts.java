package com.jobma.employer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityFilter;
import com.jobma.employer.activities.ActivityReportFilter;
import com.jobma.employer.adapters.ReportCountsAdapter;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.applicantReports.ApplicantsData;
import com.jobma.employer.model.applicantReports.EOApplicantReportObject;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentApplicantCounts extends Fragment implements View.OnClickListener {

    private View view;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private RecyclerView recyclerView;
    private TextView tv_no_data;
    private FloatingActionButton floatingActionButton;

    private ArrayList<ApplicantsData> applicantsDataList = new ArrayList<>();
    private ReportCountsAdapter reportCountsAdapter;
    private int offset = 1;
    private int remainingCounts;
    private static final String PER_PAGE_ITEMS = "10";
    private Map<String, String> params = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_applicant_counts, container, false);

        this.initView();
        this.setOnClickListner();

        return view;
    }

    private void initView() {
        this.apiInterface = APIClient.getClient();
        this.progress = new GlobalProgressDialog(getContext());
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.floatingActionButton = view.findViewById(R.id.floatingActionButton);
        this.recyclerView = view.findViewById(R.id.recyclerView);
        this.tv_no_data = view.findViewById(R.id.tv_no_data);

    }

    private void setOnClickListner() {
        this.floatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.getApplicantReports();
    }

    private void getApplicantReports() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {

            params.put("status", "0");
            params.put("offset", String.valueOf(offset));
            params.put("limit", PER_PAGE_ITEMS);

            progress.showProgressBar();
            apiInterface.getApplicantReports(this.apiKey, params).enqueue(new Callback<EOApplicantReportObject>() {
                @Override
                public void onResponse(Call<EOApplicantReportObject> call, Response<EOApplicantReportObject> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {

                        EOApplicantReportObject applicantReportObject = response.body();

                        if (!ObjectUtil.isEmpty(applicantReportObject)) {
                            if (applicantReportObject.getError() == RESPONSE_SUCCESS) {

                                //TODO At first time load by default first page
                                applicantsDataList.addAll(applicantReportObject.getData().getAppliedData());

                                if (!ObjectUtil.isEmpty(applicantReportObject.getData().getRemaining()))
                                    remainingCounts = applicantReportObject.getData().getRemaining();

                                if (!ObjectUtil.isEmpty(applicantsDataList)) {
                                    tv_no_data.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    recyclerView.setHasFixedSize(true);
                                    reportCountsAdapter = new ReportCountsAdapter(getContext(), applicantsDataList);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    recyclerView.setAdapter(reportCountsAdapter);
                                } else {
                                    tv_no_data.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                }

                                //TODO when user scroll then this api will call again & again
                                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                    }

                                    @Override
                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == applicantsDataList.size() - 1) {
                                            offset += 10;
                                            loadNextPage();
                                        }
                                    }
                                });

                            } else {
                                progress.hideProgressBar();
                                tv_no_data.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "" + applicantReportObject.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOApplicantReportObject> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void loadNextPage() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {

            params.put("status", "0");
            params.put("offset", String.valueOf(offset));
            params.put("limit", PER_PAGE_ITEMS);

            if (remainingCounts > 0) {
                progress.showProgressBar();
                apiInterface.getApplicantReports(apiKey, params).enqueue(new Callback<EOApplicantReportObject>() {
                    @Override
                    public void onResponse(Call<EOApplicantReportObject> call, Response<EOApplicantReportObject> response) {
                        progress.hideProgressBar();
                        if (!ObjectUtil.isEmpty(response.body())) {
                            EOApplicantReportObject applicantReportObject = response.body();
                            if (!ObjectUtil.isEmpty(applicantReportObject)) {
                                if (applicantReportObject.getError() == RESPONSE_SUCCESS) {

                                    applicantsDataList.addAll(applicantReportObject.getData().getAppliedData());

                                    if (!ObjectUtil.isEmpty(applicantReportObject.getData().getRemaining()))
                                        remainingCounts = applicantReportObject.getData().getRemaining();

                                    if (!ObjectUtil.isEmpty(applicantsDataList)) {
                                        tv_no_data.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        reportCountsAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    progress.hideProgressBar();
                                    tv_no_data.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "" + applicantReportObject.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EOApplicantReportObject> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            Toast.makeText(getActivity(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 111) {
            params = (Map<String, String>) data.getSerializableExtra("mapData");
            applicantsDataList.clear();
            offset = 1;
            remainingCounts = 0;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floatingActionButton) {
            Intent intent = new Intent(getActivity(), ActivityReportFilter.class);
            this.startActivityForResult(intent, 111);
        }
    }
}
