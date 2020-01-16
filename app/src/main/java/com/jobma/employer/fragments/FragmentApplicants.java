package com.jobma.employer.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.jobma.employer.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentApplicants extends Fragment implements View.OnClickListener {

    private View view;
    private RecyclerView recapplicants;
    private FloatingActionButton btnfilter;
    private int remainingCount, visibleItems = 1;
    private boolean isfirst = true;
    private GlobalProgressDialog dialog;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private ProgressBar progressBar;
    private TextView emptyText;
    private LinearLayoutManager layoutManager;
    private List<InvitedDatum> invitedList = new ArrayList<>();
    private InvitedAndEvaluateAdapter invitedAndEvaluateAdapter;
    private String id, path;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_applicants, container, false);

        this.initView();
        this.setView();
        this.recyclerViewSetup();

        return view;
    }

    private void initView() {
        this.dialog = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        recapplicants = view.findViewById(R.id.recApplicants);
        progressBar = view.findViewById(R.id.progressBar);
        emptyText = view.findViewById(R.id.textView205);
    }

    private void setView() {
        if (getArguments() != null) {
            id = getArguments().getString("jobId");
            if (getArguments().getString("Evaluate").equalsIgnoreCase("Track")) {
                dialog.showProgressBar();
                path = "track-candidate";
                displaytrackitems(id, path);
            } else if (getArguments().getString("Evaluate").equalsIgnoreCase("Evaluate")) {
                path = "candidates-evaluation-list";
                dialog.showProgressBar();
                displayevaluateitems(id, path);
            }
        }
    }

    private void recyclerViewSetup() {
        recapplicants.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == invitedList.size() - 1) {
                        visibleItems += 7;
                        callapi(id, path);
                    }
                }
            }
        });
    }

    private void displayevaluateitems(String id, String path) {
        layoutManager = new LinearLayoutManager(getContext());
        invitedAndEvaluateAdapter = new InvitedAndEvaluateAdapter(invitedList, getContext(), "Evaluate");
        recapplicants.setAdapter(invitedAndEvaluateAdapter);
        callapi(id, path);
    }

    private void displaytrackitems(String id, String path) {
        layoutManager = new LinearLayoutManager(getContext());
        invitedAndEvaluateAdapter = new InvitedAndEvaluateAdapter(invitedList, getContext(), "Track");
        callapi(id, path);
    }

    @Override
    public void onClick(View v) {

    }

    private void callapi(String id, String path) {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            if (remainingCount > 0 || isfirst) {
                if (dialog == null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                try {
                    apiInterface.invitedCandidate(Constants.BASE_URL + path, apiKey, String.valueOf(visibleItems), "10", id).enqueue(new Callback<EOInvitedCandidates>() {
                        @Override
                        public void onResponse(Call<EOInvitedCandidates> call, Response<EOInvitedCandidates> response) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.hideProgressBar();
                                dialog = null;
                            } else
                                progressBar.setVisibility(View.GONE);

                            if (response.body() != null) {
                                if (response.body().getError() == 0) {

                                    if (response.body().getData().getInvitedData() != null) {
                                        invitedList.addAll(response.body().getData().getInvitedData());
                                        remainingCount = response.body().getData().getRemaining();
                                        if (invitedList.size() > 0) {
                                            emptyText.setVisibility(View.GONE);
                                            recapplicants.setVisibility(View.VISIBLE);
                                            if (isfirst) {
                                                isfirst = false;
                                                recapplicants.setHasFixedSize(true);
                                                recapplicants.setLayoutManager(layoutManager);
                                                recapplicants.setAdapter(invitedAndEvaluateAdapter);
                                            }
                                            invitedAndEvaluateAdapter.notifyDataSetChanged();
                                        } else {
                                            emptyText.setVisibility(View.VISIBLE);
                                            recapplicants.setVisibility(View.GONE);
                                        }

                                    } else {
                                        emptyText.setVisibility(View.VISIBLE);
                                        recapplicants.setVisibility(View.GONE);
                                    }
                                } else {
                                    showNoDataFoundDialog(response.body().getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<EOInvitedCandidates> call, Throwable t) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.hideProgressBar();
                                dialog = null;
                            } else
                                progressBar.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.hideProgressBar();
                        dialog = null;
                    } else
                        progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        }
    }

    private void showNoDataFoundDialog(String dialogMessage) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_invitation);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.bg_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        Button dialogBtn_cancel = dialog.findViewById(R.id.button21);
        TextView message = dialog.findViewById(R.id.textView164);
        ImageView imgtik = dialog.findViewById(R.id.imageView68);
        imgtik.setImageResource(R.drawable.ic_cross);
        message.setText(dialogMessage);
        dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getActivity().finish();
            }
        });
        dialog.show();
    }


}
