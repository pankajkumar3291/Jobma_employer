package com.jobma.employer.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.interview_log.Datum;
import com.jobma.employer.model.interview_log.EOInvitationLog;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class ActivityInterviewInvitation extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private RecyclerView recInterviewInvitation;

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private List<Datum> interviewInvitationList = new ArrayList<>();
    private ImageView btnback;
    private boolean isfirst = true;
    private TextView emptyText;
    private LinearLayoutManager layoutManager;
    private String jobId;
    private InvitationInterviewAdapter invitedAndEvaluateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_interview_invitation_log);

        if (!ObjectUtil.isEmpty(this.getIntent().getStringExtra("jobId"))) {
            this.jobId = this.getIntent().getStringExtra("jobId");
        }

        this.initView();
        this.setOnClickListener();

        this.getAccountsApi();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());

        this.progress = new GlobalProgressDialog(ActivityInterviewInvitation.this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.emptyText = findViewById(R.id.textView208);
        this.recInterviewInvitation = findViewById(R.id.rec_interiew_invitation);
        this.btnback = findViewById(R.id.btnback);

        this.invitedAndEvaluateAdapter = new InvitationInterviewAdapter(ActivityInterviewInvitation.this, interviewInvitationList);
        this.layoutManager = new LinearLayoutManager(this);
    }

    private void setOnClickListener() {
        btnback.setOnClickListener(this);
    }

    private void getAccountsApi() {
        if (!ObjectUtil.isEmpty(this.apiKey) && !ObjectUtil.isEmpty(this.jobId)) {
            progress.showProgressBar();
            try {
                apiInterface.invitationLog(this.apiKey, this.jobId).enqueue(new Callback<EOInvitationLog>() {
                    @Override
                    public void onResponse(Call<EOInvitationLog> call, Response<EOInvitationLog> response) {
                        progress.hideProgressBar();
                        if (response.body() != null) {
                            if (response.body().getError() == 0) {
                                if (response.body().getData() != null) {
                                    interviewInvitationList.addAll(response.body().getData());
                                    if (interviewInvitationList.size() > 0) {
                                        emptyText.setVisibility(View.GONE);
                                        recInterviewInvitation.setVisibility(View.VISIBLE);
                                        if (isfirst) {
                                            isfirst = false;
                                            recInterviewInvitation.setHasFixedSize(true);
                                            recInterviewInvitation.setLayoutManager(layoutManager);
                                            recInterviewInvitation.setAdapter(invitedAndEvaluateAdapter);
                                        }
                                        invitedAndEvaluateAdapter.notifyDataSetChanged();
                                    } else {
                                        emptyText.setVisibility(View.VISIBLE);
                                        recInterviewInvitation.setVisibility(View.GONE);
                                    }
                                } else {
                                    emptyText.setVisibility(View.VISIBLE);
                                    recInterviewInvitation.setVisibility(View.GONE);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EOInvitationLog> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            Toast.makeText(ActivityInterviewInvitation.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                progress.hideProgressBar();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnback:
                finish();
                break;
        }
    }

    //TODO ********************************** Adapter Class *********************************************
    class InvitationInterviewAdapter extends RecyclerView.Adapter<InvitationInterviewAdapter.InvitationInterviewHolder> {

        private Context context;
        private List<Datum> invitationInterviewList;

        private InvitationInterviewAdapter(Context context, List<Datum> invitationInterviewList) {
            this.context = context;
            this.invitationInterviewList = invitationInterviewList;
        }

        @NonNull
        @Override
        public InvitationInterviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_invited_candidate_finter, viewGroup, false);
            return new InvitationInterviewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InvitationInterviewHolder invitationInterviewHolder, int i) {
            Datum datum = interviewInvitationList.get(i);
            invitationInterviewHolder.tvname.setText(datum.getJobName());
            if (datum.getStatus() == 1) {
                invitationInterviewHolder.tvstatus.setText(datum.getMsg());
                invitationInterviewHolder.tvstatus.setBackgroundColor(getResources().getColor(R.color.selected_color));
                invitationInterviewHolder.view.setBackgroundResource(R.drawable.circular_view_green);
            } else {
                invitationInterviewHolder.tvstatus.setText(datum.getMsg());
                invitationInterviewHolder.tvstatus.setBackgroundColor(getResources().getColor(R.color.rejected_color));
                invitationInterviewHolder.view.setBackgroundResource(R.drawable.circular_view);
            }
            invitationInterviewHolder.tvJobtype.setText("job Type : " + datum.getInterviewType());
            invitationInterviewHolder.tvinterviewKit.setText("Interview Kit : " + datum.getKitName());
            invitationInterviewHolder.tvEmail.setText(datum.getEmail());
            invitationInterviewHolder.tvTime.setText(datum.getCreatedDate());
            invitationInterviewHolder.tvInvited.setText("Invited for : " + datum.getJobName());
            invitationInterviewHolder.tvname.setText(datum.getName());
        }

        @Override
        public int getItemCount() {
            return ObjectUtil.isEmpty(invitationInterviewList) ? 0 : interviewInvitationList.size();
        }

        class InvitationInterviewHolder extends RecyclerView.ViewHolder {
            private TextView tvname, tvstatus, tvInvited, tvJobtype, tvinterviewKit, tvEmail, tvTime;
            private View view;

            private InvitationInterviewHolder(@NonNull View itemView) {
                super(itemView);
                tvname = itemView.findViewById(R.id.textView80);
                view = itemView.findViewById(R.id.view41);
                tvstatus = itemView.findViewById(R.id.imageView45);
                tvInvited = itemView.findViewById(R.id.textView84);
                tvJobtype = itemView.findViewById(R.id.textView85);
                tvinterviewKit = itemView.findViewById(R.id.textView86);
                tvEmail = itemView.findViewById(R.id.textView150);
                tvTime = itemView.findViewById(R.id.textView87);

            }
        }

    }
}

