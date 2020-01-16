package com.jobma.employer.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityLogin;
import com.jobma.employer.adapters.InterviewKitAdapter;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.interviewKit.EOInterviewKitList;
import com.jobma.employer.model.interviewKit.EOKitList;
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

public class FragmentInterViewKit extends Fragment {

    private View view;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private RecyclerView recInterviewkit;
    private TextView tv_no_data;

    private int offset = 1;
    private int remainingCounts;
    private static final String PER_PAGE_ITEMS = "10";
    private List<EOKitList> eoKitListArray = new ArrayList<>();

    private InterviewKitAdapter interviewKitAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_inter_view_kit, container, false);

        this.initView();

        return this.view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.recInterviewkit = view.findViewById(R.id.rec_interview_kit);
        this.tv_no_data = view.findViewById(R.id.tv_no_data);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.getWalletExpired();

        eoKitListArray.clear();
        offset = 1;
        remainingCounts = 0;
        this.getInterviewKitData();
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

    private void getInterviewKitData() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            try {
                apiInterface.interviewKit(this.apiKey, offset, PER_PAGE_ITEMS).enqueue(new Callback<EOInterviewKitList>() {
                    @Override
                    public void onResponse(Call<EOInterviewKitList> call, Response<EOInterviewKitList> response) {
                        progress.hideProgressBar();
                        if (!ObjectUtil.isEmpty(response.body())) {
                            EOInterviewKitList eoEvaluateCandidates = response.body();

                            if (!ObjectUtil.isEmpty(eoEvaluateCandidates)) {
                                if (eoEvaluateCandidates.getError() == 0) {
                                    //TODO At first time load by default first page
                                    eoKitListArray.addAll(eoEvaluateCandidates.getData().getKitList());
                                    if (!ObjectUtil.isEmpty(eoEvaluateCandidates.getData().getRemaining()))
                                        remainingCounts = eoEvaluateCandidates.getData().getRemaining();
                                    if (!ObjectUtil.isEmpty(eoKitListArray)) {
                                        tv_no_data.setVisibility(View.GONE);
                                        recInterviewkit.setVisibility(View.VISIBLE);
                                        recInterviewkit.setHasFixedSize(true);
                                        interviewKitAdapter = new InterviewKitAdapter(getContext(), eoKitListArray);
                                        recInterviewkit.setItemAnimator(new DefaultItemAnimator());
                                        recInterviewkit.setAdapter(interviewKitAdapter);
                                    } else {
                                        tv_no_data.setVisibility(View.VISIBLE);
                                        recInterviewkit.setVisibility(View.GONE);
                                    }

                                    //TODO when user scroll then this api will call again & again
                                    recInterviewkit.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                            super.onScrollStateChanged(recyclerView, newState);
                                        }

                                        @Override
                                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                            super.onScrolled(recyclerView, dx, dy);
                                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == eoKitListArray.size() - 1) {
                                                offset += 10;
                                                loadNextPage();
                                            }
                                        }
                                    });

                                } else {
                                    progress.hideProgressBar();
                                    tv_no_data.setVisibility(View.VISIBLE);
                                    recInterviewkit.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "" + eoEvaluateCandidates.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EOInterviewKitList> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            Toast.makeText(getActivity(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                progress.hideProgressBar();
            }
        }
    }

    private void loadNextPage() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {

            if (remainingCounts > 0) {
                progress.showProgressBar();
                apiInterface.interviewKit(apiKey, offset, PER_PAGE_ITEMS).enqueue(new Callback<EOInterviewKitList>() {
                    @Override
                    public void onResponse(Call<EOInterviewKitList> call, Response<EOInterviewKitList> response) {
                        progress.hideProgressBar();
                        if (!ObjectUtil.isEmpty(response.body())) {
                            EOInterviewKitList eoEvaluateCandidates = response.body();
                            if (!ObjectUtil.isEmpty(eoEvaluateCandidates)) {
                                if (eoEvaluateCandidates.getError() == 0) {
                                    eoKitListArray.addAll(eoEvaluateCandidates.getData().getKitList());
                                    if (!ObjectUtil.isEmpty(eoEvaluateCandidates.getData().getRemaining()))
                                        remainingCounts = eoEvaluateCandidates.getData().getRemaining();
                                    if (!ObjectUtil.isEmpty(eoKitListArray)) {
                                        tv_no_data.setVisibility(View.GONE);
                                        recInterviewkit.setVisibility(View.VISIBLE);
                                        interviewKitAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    progress.hideProgressBar();
                                    tv_no_data.setVisibility(View.VISIBLE);
                                    recInterviewkit.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "" + eoEvaluateCandidates.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EOInterviewKitList> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            Toast.makeText(getActivity(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

}
