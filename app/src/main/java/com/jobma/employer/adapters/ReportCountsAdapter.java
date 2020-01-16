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
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.applicantReports.ApplicantsData;
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

public class ReportCountsAdapter extends RecyclerView.Adapter<ReportCountsAdapter.ApplicantReportViewHolder> {

    private Context context;
    private List<ApplicantsData> reportCountsList;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    public ReportCountsAdapter(Context context, List<ApplicantsData> applicantList) {
        this.context = context;
        this.reportCountsList = applicantList;
        this.progress = new GlobalProgressDialog(context);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
    }

    @NonNull
    @Override
    public ApplicantReportViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_applicant_report, viewGroup, false);
        return new ApplicantReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ApplicantReportViewHolder viewHolder, int position) {
        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        ApplicantsData applicantsData = this.reportCountsList.get(position);

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.showProgressBar();
                //callApi(applicantsData);
                getWalletExpired(applicantsData);
            }
        });
        if (!ObjectUtil.isEmpty(applicantsData.getJobmaPitcherPhoto()))
            loadImages(applicantsData.getJobmaPitcherPhoto(), viewHolder.profileImage);

        viewHolder.tvName.setText(applicantsData.getJobmaPitcherFname().concat(" ").concat(applicantsData.getJobmaPitcherLname()));
        viewHolder.tvEmail.setText(ObjectUtil.isEmpty(applicantsData.getJobmaPitcherEmail()) ? "N/A" : applicantsData.getJobmaPitcherEmail());
        viewHolder.tvMobile.setText(ObjectUtil.isEmpty(applicantsData.getJobmaPitcherPhone()) ? "N/A" : applicantsData.getJobmaPitcherPhone());
        viewHolder.tvInterview.setText(ObjectUtil.isEmpty(applicantsData.getApplyMode()) ? "N/A" : applicantsData.getApplyMode());
        viewHolder.tvApplied.setText(ObjectUtil.isEmpty(applicantsData.getJobmaJobTitle()) ? "N/A" : applicantsData.getJobmaJobTitle());
        viewHolder.tvDate.setText(ObjectUtil.isEmpty(applicantsData.getJobmaAppliedDate()) ? "N/A" : applicantsData.getJobmaAppliedDate());

        if (applicantsData.getPreRecordedPaymentStatus().equals("1")) {
            viewHolder.tvViewed.setVisibility(View.VISIBLE);
            UIUtil.setBackgroundRound(viewHolder.tvViewed, R.color.viewed_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        }

        if (applicantsData.getStatus().equalsIgnoreCase("Selected")) {
            viewHolder.tvStatus.setVisibility(View.VISIBLE);
            viewHolder.tvStatus.setText("Selected");
            viewHolder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.selected_color));
            viewHolder.tvSideView.setBackgroundColor(context.getResources().getColor(R.color.selected_color));
            UIUtil.setBackgroundRound(viewHolder.tvStatus, R.color.selected_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        } else if (applicantsData.getStatus().equalsIgnoreCase("Rejected")) {
            viewHolder.tvStatus.setVisibility(View.VISIBLE);
            viewHolder.tvStatus.setText("Rejected");
            viewHolder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.rejected_color));
            viewHolder.tvSideView.setBackgroundColor(context.getResources().getColor(R.color.rejected_color));
            UIUtil.setBackgroundRound(viewHolder.tvStatus, R.color.rejected_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        } else if (applicantsData.getStatus().equalsIgnoreCase("On Hold")) {
            viewHolder.tvStatus.setVisibility(View.VISIBLE);
            viewHolder.tvStatus.setText("On Hold");
            viewHolder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.on_hold_color));
            viewHolder.tvSideView.setBackgroundColor(context.getResources().getColor(R.color.on_hold_color));
            UIUtil.setBackgroundRound(viewHolder.tvStatus, R.color.on_hold_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        } else if (applicantsData.getStatus().equalsIgnoreCase("Applied")) {
            viewHolder.tvStatus.setVisibility(View.VISIBLE);
            viewHolder.tvStatus.setText("Applied");
            viewHolder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.applied_color));
            viewHolder.tvSideView.setBackgroundColor(context.getResources().getColor(R.color.applied_color));
            UIUtil.setBackgroundRound(viewHolder.tvStatus, R.color.applied_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        } else if (applicantsData.getStatus().equalsIgnoreCase("Pending")) {
            viewHolder.tvStatus.setVisibility(View.VISIBLE);
            viewHolder.tvStatus.setText("Pending");
            viewHolder.tvStatus.setBackgroundColor(context.getResources().getColor(R.color.pending_color));
            viewHolder.tvSideView.setBackgroundColor(context.getResources().getColor(R.color.pending_color));
            UIUtil.setBackgroundRound(viewHolder.tvStatus, R.color.pending_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
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

    private void getWalletExpired(ApplicantsData applicantsData) {
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
                                callApi(applicantsData);
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

    private void callApi(ApplicantsData applicantsData) {
        try {
            apiInterface.getCandidateDetail(apiKey, String.valueOf(applicantsData.getJobmaAppliedId())).enqueue(new Callback<GetCandidateDetail>() {
                @Override
                public void onResponse(Call<GetCandidateDetail> call, Response<GetCandidateDetail> response) {
                    progress.hideProgressBar();
                    if (response.body() != null) {
                        if (response.body().getError() == 0) {
                            context.startActivity(new Intent(context, ActivityEvaluation.class).putExtra("appliedId", applicantsData.getJobmaAppliedId()).putExtra("pitcherId", applicantsData.getJobmaPitcherId()).putExtra("jobId", applicantsData.getJobmaJobPostId()));
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

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get()
                .load(imagePath)
                .error(R.drawable.ic_profile)
                .fit()
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(this.reportCountsList) ? 0 : this.reportCountsList.size();
    }

    class ApplicantReportViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profileImage;
        private ConstraintLayout mainLayout;
        private TextView tvName, tvEmail, tvMobile, tvInterview, tvApplied, tvDate, tvSideView, tvViewed, tvStatus;

        private ApplicantReportViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.circleImageView2);
            tvName = itemView.findViewById(R.id.textView80);
            tvEmail = itemView.findViewById(R.id.textView84);
            tvMobile = itemView.findViewById(R.id.textView85);
            tvInterview = itemView.findViewById(R.id.textView86);
            tvApplied = itemView.findViewById(R.id.textView88);
            tvDate = itemView.findViewById(R.id.textView83);
            tvSideView = itemView.findViewById(R.id.textView89);
            tvViewed = itemView.findViewById(R.id.textView90);
            tvStatus = itemView.findViewById(R.id.textView91);
            mainLayout = itemView.findViewById(R.id.constraintLayout9);

        }
    }
}
