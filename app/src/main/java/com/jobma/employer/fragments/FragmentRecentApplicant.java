package com.jobma.employer.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jobma.employer.R;
import com.jobma.employer.adapters.RecentApplicantAdapter;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.model.recennt_applicants.RecentApplicantsRequest;
import com.jobma.employer.model.recennt_applicants.RecentDatum;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentRecentApplicant extends Fragment {

    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private View view;

    private RecyclerView recApplicant;
    private List<RecentDatum> applicantlist = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private boolean isfirst = true;
    private ProgressDialog dialog;
    private int remainingCount, visibleItems = 1;
    private RecentApplicantAdapter recentApplicantAdapter;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_recent_applicant, container, false);

        this.initView();

        this.getRecentApplicantsData();
        this.recyclerViewSetup();

        return view;
    }

    private void initView() {
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
        this.dialog = new ProgressDialog(getContext());
        this.dialog.show();
        this.recentApplicantAdapter = new RecentApplicantAdapter(getContext(), applicantlist);

        this.layoutManager = new LinearLayoutManager(getContext());
        this.recApplicant = view.findViewById(R.id.recApplicant);
        this.progressBar = view.findViewById(R.id.progressBar2);
    }

    private void recyclerViewSetup() {
        recApplicant.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == applicantlist.size() - 1) {
                        visibleItems += 10;
                        getRecentApplicantsData();
                    }
                }
            }
        });
    }

    private void getRecentApplicantsData() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            if (remainingCount > 0 || isfirst) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                } else
                    progressBar.setVisibility(View.VISIBLE);
                try {
                    apiInterface.recentApplicants(apiKey, String.valueOf(visibleItems), "10").enqueue(new Callback<RecentApplicantsRequest>() {
                        @Override
                        public void onResponse(Call<RecentApplicantsRequest> call, Response<RecentApplicantsRequest> response) {
                            if (!ObjectUtil.isEmpty(response.body())) {

                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                    dialog = null;
                                } else
                                    progressBar.setVisibility(View.GONE);

                                if (response.body().getError() == 0) {
                                    applicantlist.addAll(response.body().getData().getRecentData());
                                    remainingCount = response.body().getData().getRemaining();
                                    if (isfirst) {
                                        isfirst = false;
                                        recApplicant.setHasFixedSize(true);
                                        recApplicant.setLayoutManager(layoutManager);
                                        recApplicant.setAdapter(recentApplicantAdapter);
                                    }
                                    recentApplicantAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<RecentApplicantsRequest> call, Throwable t) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                                dialog = null;
                            } else
                                progressBar.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                        dialog = null;
                    } else
                        progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

    }

}
