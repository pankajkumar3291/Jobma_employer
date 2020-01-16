package com.jobma.employer.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityEvaluation;
import com.jobma.employer.activities.ActivityLogin;
import com.jobma.employer.activities.ActivityProfile;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.candidateTrack.InvitedDatum;
import com.jobma.employer.model.get_candidate_detail.GetCandidateDetail;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class InvitedAndEvaluateAdapter extends RecyclerView.Adapter<InvitedAndEvaluateAdapter.InvitedCandidateViewHolder> {

    private List<InvitedDatum> candidatelist;
    private Context context;
    private String view;
    private String apiKey;
    private GlobalProgressDialog progress;
    private SessionSecuredPreferences loginPreferences;
    private APIClient.APIInterface apiInterface;

    public InvitedAndEvaluateAdapter(List<InvitedDatum> candidatelist, Context context, String view) {
        this.candidatelist = candidatelist;
        this.context = context;
        this.view = view;
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.progress = new GlobalProgressDialog(context);
        this.apiInterface = APIClient.getClient();
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
    }

    @NonNull
    @Override
    public InvitedCandidateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_invited_evaluate_candidatess, viewGroup, false);
        return new InvitedCandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitedCandidateViewHolder invitedCandidateViewHolder, int i) {
        InvitedDatum invitedDatum = candidatelist.get(i);
        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        if (view.equalsIgnoreCase("Evaluate")) {
            invitedCandidateViewHolder.tvCurrentStatus.setVisibility(View.VISIBLE);
            if (invitedDatum.getApplyMode().equalsIgnoreCase("1") && invitedDatum.getPreRecordedPaymentStatus().equalsIgnoreCase("1") || invitedDatum.getApplyMode().equalsIgnoreCase("2") && invitedDatum.getLiveInterviewStatus().equalsIgnoreCase("1")) {
                invitedCandidateViewHolder.btnpending.setVisibility(View.VISIBLE);
                UIUtil.setBackgroundRound(invitedCandidateViewHolder.btnpending, R.color.viewed_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
            } else
                invitedCandidateViewHolder.btnpending.setVisibility(View.GONE);
            if (invitedDatum.getCurrentStatus().equalsIgnoreCase("On Hold")) {
                invitedCandidateViewHolder.tvCurrentStatus.setText(invitedDatum.getCurrentStatus());
                UIUtil.setBackgroundRound(invitedCandidateViewHolder.tvCurrentStatus, R.color.on_hold_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
            } else if (invitedDatum.getCurrentStatus().equalsIgnoreCase("Selected")) {
                invitedCandidateViewHolder.tvCurrentStatus.setText(invitedDatum.getCurrentStatus());
                UIUtil.setBackgroundRound(invitedCandidateViewHolder.tvCurrentStatus, R.color.selected_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
            } else if (invitedDatum.getCurrentStatus().equalsIgnoreCase("Rejected")) {
                UIUtil.setBackgroundRound(invitedCandidateViewHolder.tvCurrentStatus, R.color.rejected_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
                invitedCandidateViewHolder.tvCurrentStatus.setText(invitedDatum.getCurrentStatus());
            } else if (invitedDatum.getCurrentStatus().equalsIgnoreCase("Pending")) {
                UIUtil.setBackgroundRound(invitedCandidateViewHolder.tvCurrentStatus, R.color.pending_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
                invitedCandidateViewHolder.tvCurrentStatus.setText(invitedDatum.getCurrentStatus());
            }
        }
        invitedCandidateViewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (invitedDatum.getStatus() != null) {
                    //callApi(String.valueOf(invitedDatum.getAppliedId()), invitedDatum);
                    //TODO show wallet subscription expiry api
                    getWalletExpired(invitedDatum.getAppliedId(), invitedDatum);
                } else {
                    context.startActivity(new Intent(context, ActivityProfile.class).putExtra("pitcherId", invitedDatum.getJobmaPitcherId()));
                }
            }
        });
        invitedCandidateViewHolder.tvname.setText(invitedDatum.getPitcher().getJobmaPitcherFname() + "" + invitedDatum.getPitcher().getJobmaPitcherLname());
        invitedCandidateViewHolder.tvDate.setText(invitedDatum.getJobmaInvitationDate());
        invitedCandidateViewHolder.tvPhone.setText(invitedDatum.getPitcher().getJobmaPitcherPhone());
        invitedCandidateViewHolder.tvemail.setText(invitedDatum.getPitcher().getJobmaPitcherEmail());
        invitedCandidateViewHolder.tvInterview.setText(invitedDatum.getInterviewMode());
        if (!ObjectUtil.isEmpty(invitedDatum.getPitcher().getJobmaPitcherPhoto()))
            Picasso.get().load(invitedDatum.getPitcher().getJobmaPitcherPhoto()).resize(100, 100).error(R.drawable.ic_profile).into(invitedCandidateViewHolder.img);
    }

    private void getWalletExpired(int pitcherId, InvitedDatum invitedDatum) {
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
                                callApi(String.valueOf(pitcherId), invitedDatum);
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
                        Toast.makeText(context, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showNoCreditWalletDialog(String dialogMessage) {
        final Dialog dialog = new Dialog(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
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
                    Intent loginIntent = new Intent(context, ActivityLogin.class);
                    context.startActivity(loginIntent);
                    ((Activity) context).finish();
                }
            }
        });
        dialog.show();
    }

    private void callApi(String appliedId, InvitedDatum invitedDatum) {
        if (!ObjectUtil.isEmpty(apiKey)) {
            progress.showProgressBar();
            try {
                apiInterface.getCandidateDetail(apiKey, appliedId).enqueue(new Callback<GetCandidateDetail>() {
                    @Override
                    public void onResponse(Call<GetCandidateDetail> call, Response<GetCandidateDetail> response) {
                        progress.hideProgressBar();
                        if (response.body() != null) {
                            if (response.body().getError() == RESPONSE_SUCCESS) {
                                context.startActivity(new Intent(context, ActivityEvaluation.class).putExtra("appliedId", invitedDatum.getAppliedId()).putExtra("pitcherId", invitedDatum.getPitcher().getJobmaPitcherId()).putExtra("jobId", invitedDatum.getJobId()));
                            } else {
                                //Toast.makeText(context, "No data found!", Toast.LENGTH_SHORT).show();
                                showNoDataFoundDialog(response.body().getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<GetCandidateDetail> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            Toast.makeText(context, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                progress.hideProgressBar();
            }
        }
    }

    private void showNoDataFoundDialog(String dialogMessage) {
        final Dialog dialog = new Dialog(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_invitation);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.bg_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        Button dialogBtn_cancel = dialog.findViewById(R.id.button21);
        TextView message = dialog.findViewById(R.id.textView164);
        ImageView dialogIcon = dialog.findViewById(R.id.imageView68);

        dialogIcon.setImageResource(R.drawable.ic_cross);
        message.setText(dialogMessage);

        dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(candidatelist) ? 0 : candidatelist.size();
    }

    class InvitedCandidateViewHolder extends RecyclerView.ViewHolder {
        private TextView tvname, btnpending, tvDate, tvInterview, tvemail, tvPhone, tvCurrentStatus;
        private CircleImageView img;
        private ConstraintLayout mainLayout;

        private InvitedCandidateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.textView80);
            btnpending = itemView.findViewById(R.id.textView90);
            tvDate = itemView.findViewById(R.id.textView84);
            tvInterview = itemView.findViewById(R.id.textView85);
            tvemail = itemView.findViewById(R.id.textView86);
            tvPhone = itemView.findViewById(R.id.textView87);
            img = itemView.findViewById(R.id.circleImageView2);
            tvCurrentStatus = itemView.findViewById(R.id.textView91);
            mainLayout = itemView.findViewById(R.id.constraintLayout9);
        }
    }

}
