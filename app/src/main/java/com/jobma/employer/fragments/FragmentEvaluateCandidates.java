package com.jobma.employer.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
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
import com.jobma.employer.activities.ActivityEvaluateCandidates;
import com.jobma.employer.activities.ActivityEvaluateFilter;
import com.jobma.employer.activities.ActivityLogin;
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
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentEvaluateCandidates extends Fragment {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private RecyclerView recEvaluateCandidates;
    private View view;
    private List<JobDatum> candidatesList = new ArrayList<>();
    private FloatingActionButton btnfilter;
    private int remainingCount, visibleItems = 1;
    private LinearLayoutManager layoutManager;
    private ProgressBar progressBar;
    private boolean isfirst = true;
    private TextView emptyText;
    private Map<String, String> params = new HashMap<>();
    private EvaluateCandidateAdapter evaluateCandidateAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_evaluate_candidates, container, false);

        this.initView();

        this.getWalletExpired();
        this.recyclerViewSetup();
        this.getApplicantsData();

        return view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getContext());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
        layoutManager = new LinearLayoutManager(getContext());
        evaluateCandidateAdapter = new EvaluateCandidateAdapter(getContext(), candidatesList);

        recEvaluateCandidates = view.findViewById(R.id.rec_evaluate_candidates);
        emptyText = view.findViewById(R.id.textView204);
        btnfilter = view.findViewById(R.id.floatingActionButton3);
        progressBar = view.findViewById(R.id.progressBar);

        btnfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), ActivityEvaluateFilter.class), 222);
            }
        });
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

    private void recyclerViewSetup() {
        recEvaluateCandidates.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == candidatesList.size() - 1) {
                        visibleItems += 10;
                        getApplicantsData();
                    }
                }
            }
        });
    }

    private void getApplicantsData() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {

            params.put("offset", String.valueOf(visibleItems));
            params.put("limit", "10");

            if (remainingCount > 0 || isfirst) {
                if (progress == null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                try {
                    apiInterface.evaluateCandidate(this.apiKey, params).enqueue(new Callback<EOEvaluateCandidates>() {
                        @Override
                        public void onResponse(Call<EOEvaluateCandidates> call, Response<EOEvaluateCandidates> response) {
                            if (progress != null && progress.isShowing()) {
                                progress.dismiss();
                                progress = null;
                            } else
                                progressBar.setVisibility(View.GONE);
                            if (response.body().getError() == 0) {
                                candidatesList.addAll(response.body().getData().getJobData());
                                remainingCount = response.body().getData().getRemaining();
                                if (candidatesList.size() > 0) {
                                    emptyText.setVisibility(View.GONE);
                                    recEvaluateCandidates.setVisibility(View.VISIBLE);
                                    if (isfirst) {
                                        isfirst = false;
                                        recEvaluateCandidates.setHasFixedSize(true);
                                        recEvaluateCandidates.setLayoutManager(layoutManager);
                                        recEvaluateCandidates.setAdapter(evaluateCandidateAdapter);
                                    }
                                    evaluateCandidateAdapter.notifyDataSetChanged();
                                } else {
                                    emptyText.setVisibility(View.VISIBLE);
                                    recEvaluateCandidates.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<EOEvaluateCandidates> call, Throwable t) {
                            if (progress != null && progress.isShowing()) {
                                progress.dismiss();
                                progress = null;
                            } else
                                progressBar.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    if (progress != null && progress.isShowing()) {
                        progress.dismiss();
                        progress = null;
                    } else
                        progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 222) {
            params = (Map<String, String>) data.getSerializableExtra("mapData");
            progress = new GlobalProgressDialog(getContext());
            candidatesList.clear();
            visibleItems = 1;
            isfirst = true;
            progress.showProgressBar();
            this.getApplicantsData();
        }
    }

    //************************** Adapter Class ***************************************
    class EvaluateCandidateAdapter extends RecyclerView.Adapter<EvaluateCandidateAdapter.EvaluateCandiateViewHolder> {

        private Context context;
        private List<JobDatum> candidatelist;

        private EvaluateCandidateAdapter(Context context, List<JobDatum> candidatelist) {
            this.context = context;
            this.candidatelist = candidatelist;
        }

        @NonNull
        @Override
        public EvaluateCandiateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_evaluate_candidates, viewGroup, false);
            return new EvaluateCandiateViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EvaluateCandiateViewHolder evaluateCandiateViewHolder, int i) {
            final JobDatum jobDatum = candidatelist.get(i);
            evaluateCandiateViewHolder.tvname.setText(jobDatum.getJobTitle());
            evaluateCandiateViewHolder.tvApplicants.setText(jobDatum.getApplicants().toString());
            evaluateCandiateViewHolder.tvRejected.setText(jobDatum.getRejected().toString());
            evaluateCandiateViewHolder.tvOnHold.setText(jobDatum.getHold().toString());
            evaluateCandiateViewHolder.tvSelected.setText(jobDatum.getSelected().toString());
            evaluateCandiateViewHolder.tvInvited.setText(jobDatum.getInvited().toString());
            evaluateCandiateViewHolder.tvFullName.setText("Posted by : " + jobDatum.getCatcher().getFname() + " " + jobDatum.getCatcher().getLname());

            evaluateCandiateViewHolder.btntrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, ActivityEvaluateCandidates.class).putExtra("Evaluate", "Track").putExtra("jobId", jobDatum.getJobId().toString()));
                }
            });

            evaluateCandiateViewHolder.btnEbaluate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, ActivityEvaluateCandidates.class).putExtra("Evaluate", "Evaluate").putExtra("jobId", jobDatum.getJobId().toString()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return candidatelist.size();
        }

        class EvaluateCandiateViewHolder extends RecyclerView.ViewHolder {
            private TextView tvname, tvFullName, tvApplicants, tvInvited, tvSelected, tvOnHold, tvRejected;
            private ConstraintLayout btntrack, btnEbaluate;

            private EvaluateCandiateViewHolder(@NonNull View itemView) {
                super(itemView);
                tvname = itemView.findViewById(R.id.textView124);
                tvFullName = itemView.findViewById(R.id.textView136);
                tvApplicants = itemView.findViewById(R.id.textView137);
                tvInvited = itemView.findViewById(R.id.textView138);
                tvSelected = itemView.findViewById(R.id.textView139);
                tvOnHold = itemView.findViewById(R.id.textView140);
                tvRejected = itemView.findViewById(R.id.textView141);
                btntrack = itemView.findViewById(R.id.constraintLayout22);
                btnEbaluate = itemView.findViewById(R.id.constraintLayout24);
            }
        }
    }
}