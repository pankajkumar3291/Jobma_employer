package com.jobma.employer.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityEvaluateCandidates;
import com.jobma.employer.activities.ActivityJobListing;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.fragments.FragmentJobListing;
import com.jobma.employer.model.applicants.JobDatum;
import com.jobma.employer.model.jobList.EOMessageObject;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class JobListingAdapter extends RecyclerView.Adapter<JobListingAdapter.JobListingViewHolder> {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private ArrayList<JobDatum> jobList;
    private Context context;
    private static EditText etDate;

    public JobListingAdapter(Context context, ArrayList<JobDatum> jobList) {
        this.context = context;
        this.jobList = jobList;

        this.progress = new GlobalProgressDialog(context);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
    }

    @NonNull
    @Override
    public JobListingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_job_list, viewGroup, false);
        return new JobListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final JobListingViewHolder viewHolder, int position) {
        JobDatum jobDatum = this.jobList.get(position);

        viewHolder.tvtitlle.setText(jobDatum.getJobTitle());
        viewHolder.posterBy.setText(jobDatum.getCatcher().getFname().concat(" ").concat(jobDatum.getCatcher().getLname()));
        viewHolder.postedOn.setText(jobDatum.getCreateDate().concat(","));

        viewHolder.tvApplicants.setText(String.valueOf(jobDatum.getApplicants()));
        viewHolder.tvInvited.setText(String.valueOf(jobDatum.getInvited()));
        viewHolder.tvSelected.setText(String.valueOf(jobDatum.getSelected()));
        viewHolder.tvOnHold.setText(String.valueOf(jobDatum.getHold()));
        viewHolder.tvRejected.setText(String.valueOf(jobDatum.getRejected()));

        if (!ObjectUtil.isEmpty(jobDatum.getExpiryDate())) {
            String[] expiryDate = jobDatum.getExpiryDate().split("-");
            viewHolder.expirayDate.setText(expiryDate[1].concat("/").concat(expiryDate[2].concat("/").concat(expiryDate[0])));
        }

        //TODO first time checking is user is approval or not
        if (jobDatum.getApproval().equalsIgnoreCase("1")) {
            viewHolder.checkBoxApprovedJob.setChecked(true);
        } else {
            viewHolder.checkBoxApprovedJob.setChecked(false);
        }

        //TODO first time checking is user is activate or not
        if (jobDatum.getJobStatus().equalsIgnoreCase("1")) {
            viewHolder.tvStatus.setText("Activate");
            viewHolder.mainlayout.setAlpha(1.0f);
            viewHolder.swDisapprove.setChecked(true);
        } else {
            viewHolder.tvStatus.setText("Deactivated");
            viewHolder.mainlayout.setAlpha(0.4f);
            viewHolder.swDisapprove.setChecked(false);
        }

        viewHolder.checkBoxApprovedJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewHolder.checkBoxApprovedJob.isChecked()) {
                    showDialogJobDisapproved(jobDatum.getApproval(), viewHolder.checkBoxApprovedJob, jobDatum.getJobId());
                } else {
                    jobApproved(viewHolder.checkBoxApprovedJob, jobDatum.getJobId(), "1");
                }
            }
        });

        viewHolder.tvtitlle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO from here we are going to description page
                Intent descriptionIntent = new Intent(context, ActivityJobListing.class);
                descriptionIntent.putExtra("jobId", jobDatum.getJobId());
                descriptionIntent.putExtra("jobStatus", jobDatum.getJobStatus());
                context.startActivity(descriptionIntent);
            }
        });


        viewHolder.swDisapprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date expiredDate = null;
                long millis = System.currentTimeMillis();
                java.sql.Date systemDate = new java.sql.Date(millis);
                SimpleDateFormat sD = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    expiredDate = sD.parse(jobDatum.getExpiryDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (!viewHolder.swDisapprove.isChecked()) {
                    showDialogJobDeactivate(jobDatum.getJobStatus(), viewHolder.mainlayout, viewHolder.tvStatus, viewHolder.swDisapprove, jobDatum.getJobId());
                } else if (jobDatum.getJobStatus().equals("1") || expiredDate.before(systemDate)) {
                    showDialogJobExpired(viewHolder.mainlayout, jobDatum.getJobStatus(), viewHolder.tvStatus, viewHolder.swDisapprove, jobDatum.getJobId());
                } else {
                    jobActivate(viewHolder.mainlayout, viewHolder.tvStatus, viewHolder.swDisapprove, jobDatum.getJobId(), "1");
                }
            }
        });

        viewHolder.iv_doted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jobDatum.getJobStatus().equals("1")) {
                    if (jobDatum.getApplicants() != 0 && jobDatum.getInvited() != 0) {
                        showSelectionDialog(jobDatum.getJobId(), true, true, true);
                    } else if (jobDatum.getApplicants() == 0 && jobDatum.getInvited() == 0) {
                        showSelectionDialog(jobDatum.getJobId(), true, false, false);
                    } else if (jobDatum.getApplicants() == 0) {
                        showSelectionDialog(jobDatum.getJobId(), true, false, true);
                    } else if (jobDatum.getInvited() == 0) {
                        showSelectionDialog(jobDatum.getJobId(), true, true, false);
                    }
                }
            }
        });
    }

    private void showSelectionDialog(int jobId, @NonNull Boolean isRenew, @NonNull Boolean isEvaluate, @NonNull Boolean isTrack) {
        final Dialog dialog = new Dialog(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_select_action);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.constraintLayout), R.color.colorWhite, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        ConstraintLayout renewLayout = dialog.findViewById(R.id.constraintLayout2);
        ConstraintLayout evaluateLayout = dialog.findViewById(R.id.constraintLayout3);
        ConstraintLayout trackLayout = dialog.findViewById(R.id.constraintLayout4);

        if (isRenew && isEvaluate && isTrack) {
            renewLayout.setVisibility(View.VISIBLE);
            evaluateLayout.setVisibility(View.VISIBLE);
            trackLayout.setVisibility(View.VISIBLE);
        }
        if (isRenew) {
            renewLayout.setVisibility(View.VISIBLE);
        }
        if (isRenew && isTrack) {
            renewLayout.setVisibility(View.VISIBLE);
            trackLayout.setVisibility(View.VISIBLE);
        }
        if (isRenew && isEvaluate) {
            renewLayout.setVisibility(View.VISIBLE);
            evaluateLayout.setVisibility(View.VISIBLE);
        }

        renewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                jobRenewApi(jobId);
            }
        });
        evaluateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                context.startActivity(new Intent(context, ActivityEvaluateCandidates.class).putExtra("Evaluate", "Evaluate").putExtra("jobId", String.valueOf(jobId)));
            }
        });
        trackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                context.startActivity(new Intent(context, ActivityEvaluateCandidates.class).putExtra("Evaluate", "Track").putExtra("jobId", String.valueOf(jobId)));
            }
        });

        ImageView ivCross = dialog.findViewById(R.id.imageView81);
        ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void jobRenewApi(int jobId) {
        if (!ObjectUtil.isEmpty(this.apiKey) && !ObjectUtil.isEmpty(jobId)) {
            progress.showProgressBar();
            apiInterface.renewJob(apiKey, jobId).enqueue(new Callback<EOMessageObject>() {
                @Override
                public void onResponse(Call<EOMessageObject> call, Response<EOMessageObject> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOMessageObject approval = response.body();
                        if (!ObjectUtil.isEmpty(approval)) {
                            if (approval.getError().equals(String.valueOf(RESPONSE_SUCCESS))) {
                                Toast.makeText(context, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                                if (approval.getMessage().equalsIgnoreCase("Job renewed successfully.")) {
                                    //TODO reload page for refreshing
                                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.containerlayout, new FragmentJobListing())
                                            .commit();
                                }
                            } else {
                                Toast.makeText(context, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOMessageObject> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(context, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void jobDisapproved(final CheckBox disapprovedCheckbox, int jobId, String disapproval, String disapproveReason) {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.disapprovedJob(apiKey, jobId, disapproval, disapproveReason).enqueue(new Callback<EOMessageObject>() {
                @Override
                public void onResponse(Call<EOMessageObject> call, Response<EOMessageObject> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOMessageObject approval = response.body();
                        if (!ObjectUtil.isEmpty(approval)) {
                            if (approval.getError().equals(String.valueOf(RESPONSE_SUCCESS))) {
                                Toast.makeText(context, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                                if (approval.getMessage().equalsIgnoreCase("Job disapproved successfully.")) {
                                    disapprovedCheckbox.setChecked(false);
                                }
                            } else {
                                //Toast.makeText(context, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                                disapprovedCheckbox.setChecked(true);
                                showNoDataFoundDialog(approval.getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOMessageObject> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(context, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void jobApproved(final CheckBox imgcheck, int jobId, String approval) {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.approvedJob(apiKey, jobId, approval).enqueue(new Callback<EOMessageObject>() {
                @Override
                public void onResponse(Call<EOMessageObject> call, Response<EOMessageObject> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOMessageObject approval = response.body();
                        if (!ObjectUtil.isEmpty(approval)) {
                            if (approval.getError().equals(String.valueOf(RESPONSE_SUCCESS))) {
                                Toast.makeText(context, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                                if (approval.getMessage().equalsIgnoreCase("Job approved successfully.")) {
                                    imgcheck.setChecked(true);
                                }
                            } else {
                                //Toast.makeText(context, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                                imgcheck.setChecked(false);
                                showNoDataFoundDialog(approval.getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOMessageObject> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(context, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
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


    private void showDialogJobDisapproved(String approval, CheckBox disapproveCheckbox, int jobId) {
        final Dialog dialog = new Dialog(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_approve);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.bg_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        final EditText etReason = dialog.findViewById(R.id.editText18);
        Button btnOk = dialog.findViewById(R.id.button22);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etReason.getText().toString())) {
                    etReason.setError("Can't be Empty !");
                    etReason.setFocusable(true);
                } else {
                    dialog.dismiss();
                    jobDisapproved(disapproveCheckbox, jobId, "2", etReason.getText().toString().trim());
                }
            }
        });

        Button btnCencel = dialog.findViewById(R.id.button23);
        btnCencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (approval.equalsIgnoreCase("1")) {
                    disapproveCheckbox.setChecked(true);
                } else {
                    disapproveCheckbox.setChecked(false);
                }
            }
        });
        dialog.show();
    }

    private void showDialogJobDeactivate(String status, ConstraintLayout mainLayout, TextView tvStatus, Switch jobDeactivateSwitch, int jobId) {
        final Dialog dialog = new Dialog(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_approve);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.bg_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        final EditText etReason = dialog.findViewById(R.id.editText18);
        Button btnOk = dialog.findViewById(R.id.button22);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etReason.getText().toString())) {
                    etReason.setError("Can't be Empty !");
                    etReason.setFocusable(true);
                } else {
                    dialog.dismiss();
                    jobDeactivate(mainLayout, tvStatus, jobDeactivateSwitch, jobId, "0", etReason.getText().toString().trim());
                }
            }
        });

        Button btnCencel = dialog.findViewById(R.id.button23);
        btnCencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (status.equalsIgnoreCase("1")) {
                    jobDeactivateSwitch.setChecked(true);
                } else {
                    jobDeactivateSwitch.setChecked(false);
                }
            }
        });

        dialog.show();
    }


    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker();
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            etDate.setClickable(true);
            String startDate = String.valueOf(year).concat("-").concat(String.valueOf(month + 1)).concat("-").concat(String.valueOf(dayOfMonth));
            Date date = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = simpleDateFormat.parse(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long millis = System.currentTimeMillis();
            java.sql.Date sysDate = new java.sql.Date(millis);
            if (date.after(sysDate)) {
                etDate.setText(startDate);
            } else {
                Toast toast = Toast.makeText(ApplicationHelper.application(), "Selected date must be greater from current date", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }


    //TODO from here we are opening job expired dialog with calender
    private void showDialogJobExpired(ConstraintLayout mainLayout, String jobStatus, TextView tvStatus, Switch deactivateSwitch, int jobId) {
        final Dialog dialog = new Dialog(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_expiray_calander);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.bg_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});


        etDate = dialog.findViewById(R.id.editText18);
        ImageView ivCalendar = dialog.findViewById(R.id.imageView82);

        ivCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(((Activity) context).getFragmentManager(), "datePicker");
            }
        });

        Button btnSubmit = dialog.findViewById(R.id.button22);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etDate.getText().toString())) {
                    Toast.makeText(context, "Please select date", Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    jobActivateExpiredDate(mainLayout, tvStatus, deactivateSwitch, jobId, etDate.getText().toString());
                }
            }
        });

        Button btnCancel = dialog.findViewById(R.id.button23);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (jobStatus.equalsIgnoreCase("1")) {
                    deactivateSwitch.setChecked(true);
                } else {
                    deactivateSwitch.setChecked(false);
                }
            }
        });
        dialog.show();
    }

    //TODO Renew expired date job
    private void jobActivateExpiredDate(final ConstraintLayout mainLayout, TextView tvStatus, Switch jobDeactivateSwitch, int jobId, String expirayDate) {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.activateExpiredJob(apiKey, jobId, expirayDate).enqueue(new Callback<EOMessageObject>() {
                @Override
                public void onResponse(Call<EOMessageObject> call, Response<EOMessageObject> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOMessageObject activateObject = response.body();
                        if (!ObjectUtil.isEmpty(activateObject)) {
                            if (activateObject.getError().equals(String.valueOf(RESPONSE_SUCCESS))) {
                                Toast.makeText(context, "" + activateObject.getMessage(), Toast.LENGTH_SHORT).show();
                                if (activateObject.getMessage().equalsIgnoreCase("Job activated successfully.")) {
                                    mainLayout.setAlpha(1.0f);
                                    tvStatus.setText("Activate");
                                    jobDeactivateSwitch.setChecked(true);
                                    //TODO reload page for refreshing
                                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.containerlayout, new FragmentJobListing())
                                            .commit();
                                }
                            } else {
                                Toast.makeText(context, "" + activateObject.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOMessageObject> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(context, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void jobDeactivate(final ConstraintLayout mainLayout, TextView tvStatus, Switch jobDeactivateSwitch, int jobId, String deactivate, String deactivateReason) {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.deactivateJob(apiKey, jobId, deactivate, deactivateReason).enqueue(new Callback<EOMessageObject>() {
                @Override
                public void onResponse(Call<EOMessageObject> call, Response<EOMessageObject> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOMessageObject approval = response.body();
                        if (!ObjectUtil.isEmpty(approval)) {
                            if (approval.getError().equals(String.valueOf(RESPONSE_SUCCESS))) {
                                Toast.makeText(context, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                                if (approval.getMessage().equalsIgnoreCase("Job deactivate successfully.")) {
                                    mainLayout.setAlpha(0.4f);
                                    tvStatus.setText("Deactivated");
                                    jobDeactivateSwitch.setChecked(false);
                                    //TODO reload page for refreshing
                                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.containerlayout, new FragmentJobListing())
                                            .commit();
                                }
                            } else {
                                Toast.makeText(context, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOMessageObject> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(context, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void jobActivate(final ConstraintLayout mainLayout, TextView tvStatus, Switch jobActivateSwitch, int jobId, String activate) {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.activateJob(apiKey, jobId, activate).enqueue(new Callback<EOMessageObject>() {
                @Override
                public void onResponse(Call<EOMessageObject> call, Response<EOMessageObject> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOMessageObject approval = response.body();
                        if (!ObjectUtil.isEmpty(approval)) {
                            if (approval.getError().equals(String.valueOf(RESPONSE_SUCCESS))) {
                                Toast.makeText(context, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                                if (approval.getMessage().equalsIgnoreCase("Job activated successfully.")) {
                                    mainLayout.setAlpha(1.0f);
                                    tvStatus.setText("Activate");
                                    jobActivateSwitch.setChecked(true);
                                    //TODO reload page for refreshing
                                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.containerlayout, new FragmentJobListing())
                                            .commit();
                                }
                            } else {
                                Toast.makeText(context, "" + approval.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOMessageObject> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(context, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(this.jobList) ? 0 : this.jobList.size();
    }

    class JobListingViewHolder extends RecyclerView.ViewHolder {

        private TextView tvtitlle, posterBy, postedOn, expirayDate, tvApplicants, tvInvited, tvSelected, tvOnHold, tvRejected, tvStatus;
        private Switch swDisapprove;
        private ConstraintLayout mainlayout;
        private CheckBox checkBoxApprovedJob;
        private ImageView iv_doted;

        private JobListingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvtitlle = itemView.findViewById(R.id.textView124);
            swDisapprove = itemView.findViewById(R.id.switch3);
            mainlayout = itemView.findViewById(R.id.mainlayout);
            posterBy = itemView.findViewById(R.id.textView166);
            postedOn = itemView.findViewById(R.id.textView168);
            expirayDate = itemView.findViewById(R.id.textView172);
            tvApplicants = itemView.findViewById(R.id.textView137);
            tvInvited = itemView.findViewById(R.id.textView138);
            tvSelected = itemView.findViewById(R.id.textView139);
            tvOnHold = itemView.findViewById(R.id.textView140);
            tvRejected = itemView.findViewById(R.id.textView141);
            checkBoxApprovedJob = itemView.findViewById(R.id.imageView50);
            tvStatus = itemView.findViewById(R.id.textView170);
            iv_doted = itemView.findViewById(R.id.imageView72);
        }
    }

}