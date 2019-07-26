package com.jobma.employer.fragments;

import android.app.Activity;
import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityEvaluateFilter;
import com.jobma.employer.activities.ActivityLogin;
import com.jobma.employer.adapters.JobListingAdapter;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.applicants.EOEvaluateCandidates;
import com.jobma.employer.model.applicants.JobDatum;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentJobListing extends Fragment implements View.OnClickListener {

    private View view;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private RecyclerView recJobLising;
    private TextView tv_no_data;
    private FloatingActionButton floatingActionButton;
    private ArrayList<JobDatum> candidatesList = new ArrayList<>();
    private JobListingAdapter jobListingAdapter;

    private int offset = 1;
    private int remainingCounts;
    private static final String PER_PAGE_ITEMS = "10";
    private Map<String, String> params = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_job_listing, container, false);

        this.initView();
        return view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.recJobLising = view.findViewById(R.id.rec_job_listing);
        this.tv_no_data = view.findViewById(R.id.tv_no_data);
        this.floatingActionButton = view.findViewById(R.id.floatingActionButton);
        this.floatingActionButton.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        this.getWalletExpired();
        this.candidatesList.clear();
        this.getJobList();
    }

    private void getWalletExpired() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.getWalletExpiry(apiKey).enqueue(new Callback<EOForgetPassword>() {
                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword walletExpire = response.body();
                        if (!ObjectUtil.isEmpty(walletExpire)) {
                            if (walletExpire.getError() == RESPONSE_SUCCESS) {

                            } else {
                                //Toast.makeText(ActivityDashboard.this, "" + walletExpire.getMessage(), Toast.LENGTH_SHORT).show();
                                //TODO in case error 1 show popup for expiry wallet and logout from app
                                showNoCreditWalletDialog(walletExpire.getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showNoCreditWalletDialog(String dialogMessage) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_subscription_expiray);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.bg_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        Button dialogBtn_logout = dialog.findViewById(R.id.button21);
        TextView message = dialog.findViewById(R.id.textView164);
        ImageView imgtik = dialog.findViewById(R.id.imageView68);
        imgtik.setImageResource(R.drawable.ic_cross);
        message.setText(dialogMessage);
        dialogBtn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //TODO when user is logout out then clear the login shared preferences
                if (loginPreferences.contains(SELECTED_API_KEY)) {
                    loginPreferences.edit().clear().apply();
                    Intent loginIntent = new Intent(getActivity(), ActivityLogin.class);
                    getActivity().startActivity(loginIntent);
                    getActivity().finish();
                }
            }
        });
        dialog.show();
    }

    private void getJobList() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {

            params.put("offset", String.valueOf(offset));
            params.put("limit", PER_PAGE_ITEMS);

            progress.showProgressBar();
            apiInterface.evaluateCandidate(this.apiKey, params).enqueue(new Callback<EOEvaluateCandidates>() {
                @Override
                public void onResponse(Call<EOEvaluateCandidates> call, Response<EOEvaluateCandidates> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOEvaluateCandidates eoEvaluateCandidates = response.body();

                        if (!ObjectUtil.isEmpty(eoEvaluateCandidates)) {
                            if (eoEvaluateCandidates.getError() == RESPONSE_SUCCESS) {

                                //TODO At first time load by default first page
                                candidatesList.addAll(eoEvaluateCandidates.getData().getJobData());

                                if (!ObjectUtil.isEmpty(eoEvaluateCandidates.getData().getRemaining()))
                                    remainingCounts = eoEvaluateCandidates.getData().getRemaining();

                                if (!ObjectUtil.isEmpty(candidatesList)) {
                                    tv_no_data.setVisibility(View.GONE);
                                    recJobLising.setVisibility(View.VISIBLE);
                                    recJobLising.setHasFixedSize(true);
                                    jobListingAdapter = new JobListingAdapter(getContext(), candidatesList);
                                    recJobLising.setItemAnimator(new DefaultItemAnimator());
                                    recJobLising.setAdapter(jobListingAdapter);
                                } else {
                                    tv_no_data.setVisibility(View.VISIBLE);
                                    recJobLising.setVisibility(View.GONE);
                                }

                                //TODO when user scroll then this api will call again & again
                                recJobLising.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                    }

                                    @Override
                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == candidatesList.size() - 1) {
                                            offset += 10;
                                            loadNextPage();
                                        }
                                    }
                                });

                            } else {
                                progress.hideProgressBar();
                                tv_no_data.setVisibility(View.VISIBLE);
                                recJobLising.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "" + eoEvaluateCandidates.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOEvaluateCandidates> call, Throwable t) {
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

            params.put("offset", String.valueOf(offset));
            params.put("limit", PER_PAGE_ITEMS);

            if (remainingCounts > 0) {
                progress.showProgressBar();
                apiInterface.evaluateCandidate(apiKey, params).enqueue(new Callback<EOEvaluateCandidates>() {
                    @Override
                    public void onResponse(Call<EOEvaluateCandidates> call, Response<EOEvaluateCandidates> response) {
                        progress.hideProgressBar();
                        if (!ObjectUtil.isEmpty(response.body())) {
                            EOEvaluateCandidates eoEvaluateCandidates = response.body();
                            if (!ObjectUtil.isEmpty(eoEvaluateCandidates)) {
                                if (eoEvaluateCandidates.getError() == RESPONSE_SUCCESS) {

                                    candidatesList.addAll(eoEvaluateCandidates.getData().getJobData());

                                    if (!ObjectUtil.isEmpty(eoEvaluateCandidates.getData().getRemaining()))
                                        remainingCounts = eoEvaluateCandidates.getData().getRemaining();

                                    if (!ObjectUtil.isEmpty(candidatesList)) {
                                        tv_no_data.setVisibility(View.GONE);
                                        recJobLising.setVisibility(View.VISIBLE);
                                        jobListingAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    progress.hideProgressBar();
                                    tv_no_data.setVisibility(View.VISIBLE);
                                    recJobLising.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "" + eoEvaluateCandidates.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EOEvaluateCandidates> call, Throwable t) {
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
        if (resultCode == Activity.RESULT_OK && requestCode == 222) {
            params = (Map<String, String>) data.getSerializableExtra("mapData");
            candidatesList.clear();
            offset = 1;
            remainingCounts = 0;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floatingActionButton) {
            Intent intent = new Intent(getActivity(), ActivityEvaluateFilter.class);
            this.startActivityForResult(intent, 222);
        }
    }

}
