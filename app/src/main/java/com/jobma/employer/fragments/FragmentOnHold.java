package com.jobma.employer.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jobma.employer.R;
import com.jobma.employer.adapters.InvitedAndEvaluateAdapter;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.candidateTrack.EOInvitedCandidates;
import com.jobma.employer.model.candidateTrack.InvitedDatum;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.Constants;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentOnHold extends Fragment {

    private View view;
    private RecyclerView recOnHold;
    private List<InvitedDatum> onholdList = new ArrayList<>();
    private int remainingCount, visibleItems = 1;
    private boolean isfirst = true;
    private GlobalProgressDialog dialog;
    private ProgressBar progressBar;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private TextView emptyText;
    private LinearLayoutManager layoutManager;
    private InvitedAndEvaluateAdapter invitedAndEvaluateAdapter;
    private String id, path;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_on_hold, container, false);

        this.initView();
        this.setView();
        this.recyclerViewSetup();

        return view;
    }

    private void initView() {
        layoutManager = new LinearLayoutManager(getContext());
        dialog = new GlobalProgressDialog(getContext());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        recOnHold = view.findViewById(R.id.rec_onhold);
        progressBar = view.findViewById(R.id.progressBar4);
        emptyText = view.findViewById(R.id.textView207);
    }

    private void setView() {
        if (getArguments() != null) {
            id = getArguments().getString("jobId");
            if (getArguments().getString("Evaluate").equalsIgnoreCase("Track")) {
                path = "track-candidate";
                invitedAndEvaluateAdapter = new InvitedAndEvaluateAdapter(onholdList, getContext(), "Track");
                callApi();
            } else if (getArguments().getString("Evaluate").equalsIgnoreCase("Evaluate")) {
                path = "candidates-evaluation-list";
                invitedAndEvaluateAdapter = new InvitedAndEvaluateAdapter(onholdList, getContext(), "Evaluate");
                callApi();
            }
        }
    }

    private void recyclerViewSetup() {
        recOnHold.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == onholdList.size() - 1) {
                        visibleItems += 7;
                        callApi();
                    }
                }
            }
        });
    }

    private void callApi() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            if (remainingCount > 0 || isfirst) {
                if (dialog == null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                try {
                    apiInterface.trackCandidates(Constants.BASE_URL + path, apiKey, String.valueOf(visibleItems), "10", id, "1").enqueue(new Callback<EOInvitedCandidates>() {
                        @Override
                        public void onResponse(Call<EOInvitedCandidates> call, Response<EOInvitedCandidates> response) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                                dialog = null;
                            } else
                                progressBar.setVisibility(View.GONE);
                            if (response.body() != null) {
                                if (response.body().getError() == 0) {

                                    if (response.body().getData().getInvitedData() != null) {
                                        onholdList.addAll(response.body().getData().getInvitedData());
                                        remainingCount = response.body().getData().getRemaining();
                                        if (onholdList.size() > 0) {
                                            emptyText.setVisibility(View.GONE);
                                            recOnHold.setVisibility(View.VISIBLE);
                                            if (isfirst) {
                                                isfirst = false;
                                                recOnHold.setHasFixedSize(true);
                                                recOnHold.setLayoutManager(layoutManager);
                                                recOnHold.setAdapter(invitedAndEvaluateAdapter);
                                            }
                                            invitedAndEvaluateAdapter.notifyDataSetChanged();
                                        } else {
                                            emptyText.setVisibility(View.VISIBLE);
                                            recOnHold.setVisibility(View.GONE);
                                        }
                                    } else {
                                        emptyText.setVisibility(View.VISIBLE);
                                        recOnHold.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<EOInvitedCandidates> call, Throwable t) {
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


}
