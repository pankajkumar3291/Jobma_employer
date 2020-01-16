package com.jobma.employer.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.applicantReports.ApplicantsData;
import com.jobma.employer.model.candidateTrack.InvitedDatum;
import com.jobma.employer.model.get_candidate_detail.GetCandidateDetail;
import com.jobma.employer.model.recennt_applicants.RecentDatum;
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

public class RecentApplicantAdapter extends RecyclerView.Adapter<RecentApplicantAdapter.RecentApplicantViewHolder> {

    private Context context;
    private List<RecentDatum> applicantList;

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    public RecentApplicantAdapter(Context context, List<RecentDatum> applicantList) {
        this.applicantList = applicantList;
        this.context = context;
        this.apiInterface = APIClient.getClient();
        this.progress = new GlobalProgressDialog(context);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecentApplicantViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_recent_applicant, viewGroup, false);
        return new RecentApplicantViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentApplicantViewHolder viewHolder, int i) {

        RecentDatum recentDatum = applicantList.get(i);
        String[] mColors = {"#3DC8DF", "#FFAF3C", "#90D9BA"};

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.showProgressBar();
                //callApi(recentDatum);
                getWalletExpired(recentDatum);
            }
        });
        viewHolder.sideview.setBackgroundColor(Color.parseColor(mColors[i % 3]));

        String name;
        if (!recentDatum.getPitcherTitle().isEmpty())
            name = recentDatum.getJobmaPitcherFname() + " " + recentDatum.getJobmaPitcherLname() + " is " + recentDatum.getPitcherTitle();
        else
            name = recentDatum.getJobmaPitcherFname() + " " + recentDatum.getJobmaPitcherLname();

        viewHolder.tvname.setText(name);

        viewHolder.tvJobmaTitle.setText(recentDatum.getJobmaJobTitle());
        if (!recentDatum.getJobmaPitcherPhoto().isEmpty())
            Picasso.get().load(recentDatum.getJobmaPitcherPhoto()).error(R.drawable.ic_profile).resize(100, 100).into(viewHolder.userImg);

        viewHolder.tvDays.setText(recentDatum.getLastLogin());
        viewHolder.tvInterviewType.setText(recentDatum.getJobmaAppliedDate());
    }

    private void getWalletExpired(RecentDatum recentDatum) {
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
                                callApi(recentDatum);
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

    private void callApi(RecentDatum recentDatum) {
        try {
            apiInterface.getCandidateDetail(apiKey, String.valueOf(recentDatum.getJobmaAppliedId())).enqueue(new Callback<GetCandidateDetail>() {
                @Override
                public void onResponse(Call<GetCandidateDetail> call, Response<GetCandidateDetail> response) {
                    progress.hideProgressBar();
                    if (response.body() != null) {
                        if (response.body().getError() == 0) {
                            context.startActivity(new Intent(context, ActivityEvaluation.class).putExtra("appliedId", recentDatum.getJobmaAppliedId()).putExtra("pitcherId", recentDatum.getJobmaPitcherId()).putExtra("jobId", recentDatum.getJobmaJobPostId()));
                        } else {
                            showNoDataFoundDialog(response.body().getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<GetCandidateDetail> call, Throwable t) {
                    progress.hideProgressBar();
                    System.out.println("ActivityEvaluation.onFailure" + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            progress.hideProgressBar();
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
        ImageView imgtik = dialog.findViewById(R.id.imageView68);
        imgtik.setImageResource(R.drawable.ic_cross);
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
        return ObjectUtil.isEmpty(applicantList) ? 0 : applicantList.size();
    }

    class RecentApplicantViewHolder extends RecyclerView.ViewHolder {

        private TextView tvname, sideview, tvDays, tvInterviewType, tvJobmaTitle;
        private CircleImageView userImg;
        private ConstraintLayout mainLayout;

        private RecentApplicantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.textView30);
            sideview = itemView.findViewById(R.id.textView34);
            tvDays = itemView.findViewById(R.id.textView32);
            tvInterviewType = itemView.findViewById(R.id.textView31);
            tvJobmaTitle = itemView.findViewById(R.id.textView217);
            userImg = itemView.findViewById(R.id.imageView17);
            mainLayout = itemView.findViewById(R.id.main_layout);
        }
    }

}
