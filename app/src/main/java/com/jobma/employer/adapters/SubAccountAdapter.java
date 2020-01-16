package com.jobma.employer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.dialogs.SubAccountsPermissionDialog;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.reportIssue.GetUserList;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class SubAccountAdapter extends RecyclerView.Adapter<SubAccountAdapter.SubAccountViewHolder> {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private Context context;
    private ArrayList<GetUserList> subAccountList;

    public SubAccountAdapter(Context context, ArrayList<GetUserList> subAccountList) {
        this.context = context;
        this.subAccountList = subAccountList;

        this.progress = new GlobalProgressDialog(context);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
    }

    @NonNull
    @Override
    public SubAccountAdapter.SubAccountViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_sub_account, viewGroup, false);
        return new SubAccountAdapter.SubAccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubAccountAdapter.SubAccountViewHolder holder, int position) {
        final GetUserList getUserList = this.subAccountList.get(position);

        holder.tvName.setText(getUserList.getFullName());
        holder.tvLastLogin.setText("Last Login : ".concat(getUserList.getJobmaLastLogin()));
        holder.tvEmail.setText(getUserList.getJobmaUserName());
        if (!ObjectUtil.isEmpty(getUserList.getFullName())) {
            holder.circleImageView5.setText(getUserList.getFullName().substring(0, 1).toUpperCase());
        }

        //TODO first time checking is user is approval or not
        if (getUserList.getApproval().equalsIgnoreCase("1")) {
            holder.imgcheck.setChecked(true);
        } else {
            holder.imgcheck.setChecked(false);
        }


        //TODO first time checking is user is activate or not
        if (getUserList.getConfirmed().equalsIgnoreCase("1")) {
            holder.tvstatus.setText("Activate");
            holder.mainLayout.setAlpha(1.0f);
            holder.aSwitch.setChecked(true);
        } else {
            holder.tvstatus.setText("Deactivated");
            holder.mainLayout.setAlpha(0.4f);
            holder.aSwitch.setChecked(false);
        }

        holder.aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.aSwitch.isChecked()) {
                    activateDeactivate(holder.tvstatus, holder.mainLayout, String.valueOf(getUserList.getJobmaUserId()), "1");
                } else {
                    activateDeactivate(holder.tvstatus, holder.mainLayout, String.valueOf(getUserList.getJobmaUserId()), "0");
                }
            }
        });

        holder.imgcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.imgcheck.isChecked()) {
                    approveDisapprove(holder.imgcheck, String.valueOf(getUserList.getJobmaUserId()), "1");
                } else {
                    approveDisapprove(holder.imgcheck, String.valueOf(getUserList.getJobmaUserId()), "0");
                }
            }
        });

        holder.dotedimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubAccountsPermissionDialog permissionDialog = new SubAccountsPermissionDialog(context, String.valueOf(getUserList.getJobmaUserId()));
                permissionDialog.show();
            }
        });

    }

    private void activateDeactivate(final TextView tvstatus, final ConstraintLayout mainLayout, String subUserId, String action) {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.activateDeactivateAccounts(apiKey, subUserId, action).enqueue(new Callback<EOForgetPassword>() {
                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword activateDeactivate = response.body();
                        if (!ObjectUtil.isEmpty(activateDeactivate)) {
                            if (activateDeactivate.getError() == RESPONSE_SUCCESS) {
                                Toast.makeText(context, "" + activateDeactivate.getMessage(), Toast.LENGTH_SHORT).show();
                                if (activateDeactivate.getMessage().equalsIgnoreCase("Sub-User has been activated successfully.")) {
                                    tvstatus.setText("Activate");
                                    mainLayout.setAlpha(1.0f);
                                } else {
                                    tvstatus.setText("DeActivate");
                                    mainLayout.setAlpha(0.4f);
                                }
                            } else {
                                Toast.makeText(context, "" + activateDeactivate.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(context, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void approveDisapprove(final CheckBox imgcheck, String subUserId, String action) {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.approvalSubAccounts(apiKey, subUserId, action).enqueue(new Callback<EOForgetPassword>() {
                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword approval = response.body();
                        if (!ObjectUtil.isEmpty(approval)) {
                            if (approval.getError() == RESPONSE_SUCCESS) {
                                Toast.makeText(context, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                                if (approval.getMessage().equalsIgnoreCase("Approval authority provided.")) {
                                    imgcheck.setChecked(true);
                                } else {
                                    imgcheck.setChecked(false);
                                }
                            } else {
                                Toast.makeText(context, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(context, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(this.subAccountList) ? 0 : this.subAccountList.size();
    }

    class SubAccountViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvLastLogin, tvEmail, tvJob, tvstatus, circleImageView5;
        private Switch aSwitch;
        private ConstraintLayout mainLayout;
        private ImageView dotedimg;
        private CheckBox imgcheck;

        public SubAccountViewHolder(@NonNull View itemView) {
            super(itemView);
            imgcheck = itemView.findViewById(R.id.imageView50);
            tvName = itemView.findViewById(R.id.textView115);
            tvLastLogin = itemView.findViewById(R.id.textView116);
            tvEmail = itemView.findViewById(R.id.textView117);
            tvJob = itemView.findViewById(R.id.textView118);
            tvstatus = itemView.findViewById(R.id.textView119);
            aSwitch = itemView.findViewById(R.id.switch1);
            mainLayout = itemView.findViewById(R.id.constraintLayout15);
            dotedimg = itemView.findViewById(R.id.imageView51);
            circleImageView5 = itemView.findViewById(R.id.circleImageView5);
        }
    }

}
