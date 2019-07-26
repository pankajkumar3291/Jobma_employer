package com.jobma.employer.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityLogin;
import com.jobma.employer.adapters.ReportListAdapter;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.reportIssue.EOReportList;
import com.jobma.employer.model.reportIssue.SubAccountRequest;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentReportedIssuesList extends Fragment {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private View view;
    private RecyclerView recReportList;
    private int remainingCount, visibleItems;
    private boolean isfirst = false;
    private ProgressDialog dialog;
    private ProgressBar progressBar;
    private LinearLayoutManager layoutManager;
    private List<EOReportList> reportList = new ArrayList<>();
    private ReportListAdapter reportListAdapter;
    private TextView tvTotlalItems, tvEmptyText;
    private boolean isLoading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_reported_issues_list, container, false);

        this.initView();

        return view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        layoutManager = new LinearLayoutManager(getContext());
        dialog = new ProgressDialog(getContext());
        reportListAdapter = new ReportListAdapter(getContext(), reportList);

        recReportList = view.findViewById(R.id.rec_report_list);
        progressBar = view.findViewById(R.id.progressBar3);
        tvTotlalItems = view.findViewById(R.id.textView60);
        tvEmptyText = view.findViewById(R.id.textView202);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.getWalletExpired();

        remainingCount = 0;
        visibleItems = 1;
        if (reportList.size() > 0) {
            reportList.clear();
            reportListAdapter.notifyDataSetChanged();
        }
        isfirst = true;

        this.trackIssuesReport();
        this.onScrolledPagination();
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

    private void onScrolledPagination() {

        recReportList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (!isLoading && linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == reportList.size() - 1) {
                        isLoading = true;
                        visibleItems += 10;
                        trackIssuesReport();
                    }
                }
            }
        });
    }

    private void trackIssuesReport() {
        if (!ObjectUtil.isEmpty(this.apiKey))
            if (remainingCount > 0 || isfirst) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                } else
                    progressBar.setVisibility(View.VISIBLE);
                try {
                    apiInterface.trackReportIssues(apiKey, String.valueOf(visibleItems), "10").enqueue(new Callback<SubAccountRequest>() {
                        @Override
                        public void onResponse(Call<SubAccountRequest> call, Response<SubAccountRequest> response) {

                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                                dialog = null;
                            } else
                                progressBar.setVisibility(View.GONE);

                            if (!ObjectUtil.isEmpty(response.body())) {
                                if (response.body().getError() == 0) {
                                    if (response.body().getData().getReportList() != null) {
                                        if (response.body().getData().getReportList().size() > 0) {
                                            tvEmptyText.setVisibility(View.GONE);
                                            recReportList.setVisibility(View.VISIBLE);
                                            reportList.addAll(response.body().getData().getReportList());
                                            remainingCount = response.body().getData().getRemaining();
                                            if (isfirst) {
                                                isfirst = false;
                                                recReportList.setHasFixedSize(true);
                                                recReportList.setLayoutManager(layoutManager);
                                                recReportList.setAdapter(reportListAdapter);
                                            }
                                            reportListAdapter.notifyDataSetChanged();
                                            tvTotlalItems.setText("Showing".concat(" ") + reportList.size() + " " + "Reported List");
                                            isLoading = false;
                                        } else {
                                            tvEmptyText.setVisibility(View.VISIBLE);
                                            recReportList.setVisibility(View.GONE);
                                        }
                                    }
                                } else {
                                    tvEmptyText.setVisibility(View.VISIBLE);
                                    recReportList.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SubAccountRequest> call, Throwable t) {
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


