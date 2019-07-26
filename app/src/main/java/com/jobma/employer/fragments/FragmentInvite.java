package com.jobma.employer.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityCreateJob;
import com.jobma.employer.activities.ActivityInterviewInvite;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.setupInterview.EOInterviewData;
import com.jobma.employer.model.setupInterview.EOInterviewJobList;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentInvite extends Fragment implements View.OnClickListener {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private Button btnCreateJob, btnNext;
    private Spinner jobSpinner;
    private View view;
    private ArrayList<EOInterviewData> interviewDataList;
    private String selectedJob;
    private int selectedJobId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_invite, container, false);

        this.initView();
        return this.view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.jobSpinner = view.findViewById(R.id.spinner7);
        this.btnCreateJob = view.findViewById(R.id.button7);
        this.btnCreateJob.setOnClickListener(this);
        this.btnNext = view.findViewById(R.id.button11);
        this.btnNext.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getCompanyInfo();
    }

    private void getCompanyInfo() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.getInterviewJobList(apiKey).enqueue(new Callback<EOInterviewJobList>() {
                @Override
                public void onResponse(Call<EOInterviewJobList> call, Response<EOInterviewJobList> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOInterviewJobList jobList = response.body();
                        if (!ObjectUtil.isEmpty(jobList)) {
                            if (jobList.getError() == RESPONSE_SUCCESS) {
                                interviewDataList = (ArrayList<EOInterviewData>) jobList.getData();
                                dataToView();
                            } else {
                                Toast.makeText(getActivity(), "" + jobList.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOInterviewJobList> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void dataToView() {
        ArrayList<String> jobList = new ArrayList<>();
        if (!ObjectUtil.isEmpty(interviewDataList)) {
            for (EOInterviewData interviewData : interviewDataList) {
                jobList.add(interviewData.getJobmaJobTitle());
            }
        }

        SpinnerAdapter arrayAdapter = new SpinnerAdapter(getActivity(), R.layout.spinner_item);
        arrayAdapter.addAll(jobList);
        arrayAdapter.add("Select a job");
        jobSpinner.setAdapter(arrayAdapter);
        jobSpinner.setSelection(arrayAdapter.getCount());
        jobSpinner.setOnItemSelectedListener(onJobSelectedListener);
    }

    AdapterView.OnItemSelectedListener onJobSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedJob = (String) parent.getItemAtPosition(position);
            for (EOInterviewData interviewData : interviewDataList) {
                if (interviewData.getJobmaJobTitle().equalsIgnoreCase(selectedJob)) {
                    selectedJobId = interviewData.getJobmaJobPostId();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button7:
                startActivity(new Intent(getContext(), ActivityCreateJob.class));
                break;
            case R.id.button11:
                if (!ObjectUtil.isEmpty(selectedJob)) {
                    if (selectedJob.equalsIgnoreCase("Select a job")) {
                        Toast.makeText(getActivity(), "Please select a job", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getActivity(), ActivityInterviewInvite.class);
                        intent.putExtra("jobId", selectedJobId);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getActivity(), "Please select job first", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
