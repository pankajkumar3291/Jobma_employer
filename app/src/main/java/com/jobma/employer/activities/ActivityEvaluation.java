package com.jobma.employer.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.exoPlayer.PlayerActivity;
import com.jobma.employer.exoPlayer.Sample;
import com.jobma.employer.exoPlayer.UriSample;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.feedback.ChangeProfileStatus;
import com.jobma.employer.model.feedback.FeedBackData;
import com.jobma.employer.model.feedback.FeedbackParam;
import com.jobma.employer.model.feedback.GetFeedBack;
import com.jobma.employer.model.get_candidate_detail.GetCandidateDetail;
import com.jobma.employer.model.get_interview_question_ans.GetInterviewQuestion;
import com.jobma.employer.model.get_interview_question_ans.InterViewQuestion;
import com.jobma.employer.model.get_summary_detail.GetSummaryDetail;
import com.jobma.employer.model.get_summary_detail.SummaryRatingData;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class ActivityEvaluation extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView circleImageView;
    private TextView tvname, tvemail, tvphone, tvStartus, tvEmptyQuestion, tvEmptySummary, tvShowRating, tvRecoYes, tvRecoNo, tvEmptyFeedback, tvForwardProfile,
            tvSelected, tvRejected, tvOnhold, tvViewProfile;
    private GlobalProgressDialog progress;
    private ScaleRatingBar ratingBar, simpleRatingBaryellow, simpleRatingBargreen;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private ScaleRatingBar summaryOverallRatingred, summaryOverallRatingyellow, summaryOverallRatingGreen;
    private LinearLayout summaryLayout;
    private String appliedId;
    private ImageView backArrowImage;
    private FloatingActionButton floatingEditButton;
    private RecyclerView recQuestionList, recSummary, recFeedBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        this.progress = new GlobalProgressDialog(ActivityEvaluation.this);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
        this.initView();
        this.setOnClickListener();
        this.apiInterface = APIClient.getClient();
        if (getIntent().hasExtra("appliedId")) {
            int pitcherid = getIntent().getIntExtra("pitcherId", 0);
            if (pitcherid != 0) {
                callGetAllQuestionsApi(String.valueOf(pitcherid), String.valueOf(getIntent().getIntExtra("jobId", 0)));
            }
            appliedId = String.valueOf(getIntent().getIntExtra("appliedId", 0));
            //callApi(String.valueOf(getIntent().getIntExtra("appliedId", 0)));
            //TODO from here call wallet expiry api to check subscription, if error 0 then call this api like : callApi(String.valueOf(getIntent().getIntExtra("appliedId", 0)));
            //TODO when get error 1 then show popup subscription is end
            this.getWalletExpired();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(appliedId)) {
            getFeedBackApi(String.valueOf(appliedId));
            getCandidateSummaryApi(String.valueOf(appliedId));
        }
    }

    private void initView() {
        tvRejected = findViewById(R.id.tv_rejected);

        tvRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDialog("3");
            }
        });
        tvOnhold = findViewById(R.id.tv_onhold);
        tvOnhold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDialog("1");
            }
        });
        tvSelected = findViewById(R.id.tv_selected);
        tvSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDialog("2");
            }
        });
        findViewById(R.id.backArrowImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvEmptyFeedback = findViewById(R.id.tv_feedback);
        tvRecoNo = findViewById(R.id.reco_no);
        tvRecoYes = findViewById(R.id.reco_yes);
        recFeedBack = findViewById(R.id.rec_feedback);
        tvShowRating = findViewById(R.id.tv_show_rating);
        summaryOverallRatingyellow = findViewById(R.id.ratingbar_overall_yellow);
        summaryOverallRatingGreen = findViewById(R.id.ratingbar_overall_green);
        summaryOverallRatingred = findViewById(R.id.ratingbar_overall_red);
        tvEmptySummary = findViewById(R.id.textView245);
        recSummary = findViewById(R.id.rec_summary);
        tvEmptyQuestion = findViewById(R.id.empty_question);
        recQuestionList = findViewById(R.id.rec_question_list);
        circleImageView = findViewById(R.id.user_img);
        tvname = findViewById(R.id.person_name);
        tvemail = findViewById(R.id.user_email);
        tvphone = findViewById(R.id.user_phone);
        ratingBar = findViewById(R.id.simpleRatingBar);
        tvStartus = findViewById(R.id.startus);
        summaryLayout = findViewById(R.id.linearLayout4);
        this.backArrowImage = this.findViewById(R.id.backArrowImage);
        this.tvForwardProfile = this.findViewById(R.id.tvForwardProfile);
        this.tvViewProfile = this.findViewById(R.id.tvViewProfile);
        this.floatingEditButton = this.findViewById(R.id.floatingEditButton);
        this.simpleRatingBaryellow = this.findViewById(R.id.simpleRatingBaryellow);
        this.simpleRatingBargreen = this.findViewById(R.id.simpleRatingBargreen);
    }

    private void setOnClickListener() {
        this.backArrowImage.setOnClickListener(this);
        this.tvForwardProfile.setOnClickListener(this);
        this.tvViewProfile.setOnClickListener(this);
        this.floatingEditButton.setOnClickListener(this);
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
                                if (!ObjectUtil.isEmpty(appliedId))
                                    callApi(appliedId);
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
                        Toast.makeText(ActivityEvaluation.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showNoCreditWalletDialog(String dialogMessage) {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
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
                    Intent loginIntent = new Intent(ActivityEvaluation.this, ActivityLogin.class);
                    ActivityEvaluation.this.startActivity(loginIntent);
                    ActivityEvaluation.this.finish();
                }
            }
        });
        dialog.show();
    }


    private void getFeedBackApi(String appliedId) {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            try {
                apiInterface.getFeedBack(apiKey, appliedId).enqueue(new Callback<GetFeedBack>() {
                    @Override
                    public void onResponse(Call<GetFeedBack> call, Response<GetFeedBack> response) {
                        //progress.hideProgressBar();
                        if (response.body() != null) {
                            if (response.body().getError() == 0 && response.body().getData().size() > 0) {
                                recFeedBack.setVisibility(View.VISIBLE);
                                tvEmptyFeedback.setVisibility(View.GONE);
                                recFeedBack.setHasFixedSize(true);
                                recFeedBack.setLayoutManager(new LinearLayoutManager(ActivityEvaluation.this));
                                recFeedBack.setAdapter(new FeedBackAdapter(response.body().getData(), ActivityEvaluation.this));
                            } else {
                                recFeedBack.setVisibility(View.GONE);
                                tvEmptyFeedback.setVisibility(View.VISIBLE);
                            }
                        } else {
                            recFeedBack.setVisibility(View.GONE);
                            tvEmptyFeedback.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<GetFeedBack> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            Toast.makeText(ActivityEvaluation.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                progress.hideProgressBar();
                recFeedBack.setVisibility(View.GONE);
                tvEmptyFeedback.setVisibility(View.VISIBLE);
            }
        }
    }

    private ArrayList<SummaryRatingData> keyList = new ArrayList<>();

    private void getCandidateSummaryApi(String appliedId) {
        if (!ObjectUtil.isEmpty(apiKey)) {
            try {
                apiInterface.getSummary(apiKey, appliedId).enqueue(new Callback<GetSummaryDetail>() {
                    @Override
                    public void onResponse(Call<GetSummaryDetail> call, Response<GetSummaryDetail> response) {

                        if (progress.isShowing()) {
                            progress.hideProgressBar();
                        }
                        if (response.body().getError() == 0 && response.body().getData().getRatingData() != null) {
                            String mains = response.body().getData().getRatingData().toString().replace("{", "").replace("}", "");
                            tvShowRating.setText(response.body().getData().getAvgRating() + " Stars");
                            List<String> tempList = new ArrayList<>(Arrays.asList(mains.split(",")));
                            tvRecoYes.setText(response.body().getData().getRecommendedYes());
                            tvRecoNo.setText(response.body().getData().getRecommendedNo());
                            keyList.clear();
                            for (String mainStr : tempList) {
                                String[] array = mainStr.split("=");
                                keyList.add(new SummaryRatingData(array[0].toString().trim(), array[1].toString().trim()));
                            }
                            recSummary.setHasFixedSize(true);
                            float value = Float.parseFloat(response.body().getData().getAvgRating());
                            if (value < 2.0) {
                                summaryOverallRatingred.setVisibility(View.VISIBLE);
                                summaryOverallRatingyellow.setVisibility(View.GONE);
                                summaryOverallRatingGreen.setVisibility(View.GONE);
                                summaryOverallRatingred.setRating(Float.parseFloat(response.body().getData().getAvgRating()));
                            }
                            if (value < 4.0 && value >= 2.0) {
                                summaryOverallRatingred.setVisibility(View.GONE);
                                summaryOverallRatingyellow.setVisibility(View.VISIBLE);
                                summaryOverallRatingGreen.setVisibility(View.GONE);
                                summaryOverallRatingyellow.setRating(Float.parseFloat(response.body().getData().getAvgRating()));
                            }
                            if (value <= 5.0 && value >= 4.0) {
                                summaryOverallRatingred.setVisibility(View.GONE);
                                summaryOverallRatingyellow.setVisibility(View.GONE);
                                summaryOverallRatingGreen.setVisibility(View.VISIBLE);
                                summaryOverallRatingGreen.setRating(Float.parseFloat(response.body().getData().getAvgRating()));
                            }
                            summaryLayout.setVisibility(View.VISIBLE);
                            tvEmptySummary.setVisibility(View.GONE);
                            recSummary.setAdapter(new SummaryAdapter(ActivityEvaluation.this, keyList));
                        } else {
                            if (progress.isShowing()) {
                                progress.hideProgressBar();
                            }
                            summaryLayout.setVisibility(View.GONE);
                            tvEmptySummary.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<GetSummaryDetail> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            Toast.makeText(ActivityEvaluation.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                summaryLayout.setVisibility(View.GONE);
                tvEmptySummary.setVisibility(View.VISIBLE);
            }
        }
    }

    private void callGetAllQuestionsApi(String s, String appliedId) {
        if (!ObjectUtil.isEmpty(apiKey)) {
            apiInterface.getCandidateAllQuestions(apiKey, appliedId, s).enqueue(new Callback<GetInterviewQuestion>() {
                @Override
                public void onResponse(Call<GetInterviewQuestion> call, Response<GetInterviewQuestion> response) {
                    progress.hideProgressBar();
                    if (response.body() != null) {
                        if (response.body().getError() == 0 && response.body().getData().getJobmaAnswers().getQuestion().size() > 0) {
                            recQuestionList.setLayoutManager(new LinearLayoutManager(ActivityEvaluation.this));
                            recQuestionList.setHasFixedSize(true);
                            recQuestionList.setAdapter(new QuestionAdapter(ActivityEvaluation.this, response.body().getData().getJobmaAnswers().getQuestion()));
                        } else {
                            tvEmptyQuestion.setVisibility(View.VISIBLE);
                            recQuestionList.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<GetInterviewQuestion> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityEvaluation.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void callApi(String appliedId) {
        if (!ObjectUtil.isEmpty(apiKey)) {
            try {
                apiInterface.getCandidateDetail(apiKey, appliedId).enqueue(new Callback<GetCandidateDetail>() {
                    @Override
                    public void onResponse(Call<GetCandidateDetail> call, Response<GetCandidateDetail> response) {
                        progress.hideProgressBar();
                        if (response.body() != null) {
                            if (response.body().getError() == 0) {
                                if (!TextUtils.isEmpty(response.body().getData().getEvalutionList().getPitcherData().getJobmaPitcherPhoto())) {
                                    Picasso.get().load(response.body().getData().getEvalutionList().getPitcherData().getJobmaPitcherPhoto()).resize(100, 100).error(R.drawable.ic_profile).into(circleImageView);
                                }
                                if (response.body().getData().getEvalutionList().getCurrentStatus().equalsIgnoreCase("Selected")) {
                                    tvSelected.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.selected_color));
                                    tvSelected.setTextColor(getResources().getColor(R.color.colorWhite));
                                    setTextViewDrawableColor(tvSelected, R.color.colorWhite);
                                } else if (response.body().getData().getEvalutionList().getCurrentStatus().equalsIgnoreCase("Rejected")) {
                                    tvRejected.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.rejected_color));
                                    tvRejected.setTextColor(getResources().getColor(R.color.colorWhite));
                                    setTextViewDrawableColor(tvRejected, R.color.colorWhite);
                                } else if (response.body().getData().getEvalutionList().getCurrentStatus().equalsIgnoreCase("On Hold")) {
                                    tvOnhold.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.on_hold_color));
                                    tvOnhold.setTextColor(getResources().getColor(R.color.colorWhite));
                                    setTextViewDrawableColor(tvOnhold, R.color.colorWhite);
                                }
                                tvname.setText(response.body().getData().getEvalutionList().getPitcherData().getJobmaPitcherFname() + " " + response.body().getData().getEvalutionList().getPitcherData().getJobmaPitcherLname());
                                tvemail.setText(response.body().getData().getEvalutionList().getPitcherData().getJobmaPitcherEmail());
                                tvphone.setText(response.body().getData().getEvalutionList().getPitcherData().getJobmaPitcherPhone());
                                tvStartus.setText(response.body().getData().getEvalutionList().getCurrentStatus());

                                if (response.body().getData().getEvalutionList().getAvgRating() <= 1) {
                                    ratingBar.setRating((float) response.body().getData().getEvalutionList().getAvgRating());
                                    ratingBar.setVisibility(View.VISIBLE);
                                    simpleRatingBaryellow.setVisibility(View.GONE);
                                    simpleRatingBargreen.setVisibility(View.GONE);
                                } else if (response.body().getData().getEvalutionList().getAvgRating() > 1 && response.body().getData().getEvalutionList().getAvgRating() <= 3) {
                                    ratingBar.setVisibility(View.GONE);
                                    simpleRatingBaryellow.setVisibility(View.VISIBLE);
                                    simpleRatingBargreen.setVisibility(View.GONE);
                                    simpleRatingBaryellow.setRating((float) response.body().getData().getEvalutionList().getAvgRating());
                                } else if (response.body().getData().getEvalutionList().getAvgRating() > 3) {
                                    ratingBar.setVisibility(View.GONE);
                                    simpleRatingBaryellow.setVisibility(View.GONE);
                                    simpleRatingBargreen.setVisibility(View.VISIBLE);
                                    simpleRatingBargreen.setRating((float) response.body().getData().getEvalutionList().getAvgRating());
                                }

                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<GetCandidateDetail> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            Toast.makeText(ActivityEvaluation.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void OpenDialog(String s) {
        final Dialog dialog = new Dialog(ActivityEvaluation.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_job_status);
        final EditText etReason = dialog.findViewById(R.id.editText18);
        Button btnOk = dialog.findViewById(R.id.button22);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etReason.getText().toString())) {
                    etReason.setError("Can't be Empty !");
                    etReason.setFocusable(true);
                } else {
                    progress.showProgressBar();
                    callApiChangeStatus(s, etReason.getText().toString());
                    dialog.dismiss();
                }
            }
        });
        Button btnCencel = dialog.findViewById(R.id.button23);
        btnCencel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backArrowImage:
                this.finish();
                break;
            case R.id.tvForwardProfile:
                if (!ObjectUtil.isEmpty(getIntent().getIntExtra("appliedId", 0))) {
                    int appliedId = getIntent().getIntExtra("appliedId", 0);
                    Intent forwardProfileIntent = new Intent(this, ActivityForwardProfile.class);
                    forwardProfileIntent.putExtra("appliedId", appliedId);
                    this.startActivity(forwardProfileIntent);
                }
                break;
            case R.id.tvViewProfile:
                if (!ObjectUtil.isEmpty(getIntent().getIntExtra("pitcherId", 0))) {
                    int pitcherId = getIntent().getIntExtra("pitcherId", 0);
                    Intent viewProfileIntent = new Intent(this, ActivityProfile.class);
                    viewProfileIntent.putExtra("pitcherId", pitcherId);
                    this.startActivity(viewProfileIntent);
                }
                break;
            case R.id.floatingEditButton:
                if (!ObjectUtil.isEmpty(getIntent().getIntExtra("appliedId", 0))) {
                    int appliedId = getIntent().getIntExtra("appliedId", 0);
                    Intent rateIntent = new Intent(this, ActivityRateCandidate.class);
                    rateIntent.putExtra("appliedId", appliedId);
                    this.startActivity(rateIntent);
                    this.startActivity(rateIntent);
                }
                break;
        }
    }

    private void callApiChangeStatus(String s, String text) {
        if (!ObjectUtil.isEmpty(apiKey)) {
            progress.showProgressBar();
            try {
                apiInterface.changeProfileStatus(apiKey, !TextUtils.isEmpty(appliedId) ? appliedId : "", s, text).enqueue(new Callback<ChangeProfileStatus>() {
                    @Override
                    public void onResponse(Call<ChangeProfileStatus> call, Response<ChangeProfileStatus> response) {
                        progress.hideProgressBar();
                        if (response.body() != null) {
                            if (response.body().getError() == 0) {
                                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ChangeProfileStatus> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            Toast.makeText(ActivityEvaluation.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                progress.hideProgressBar();
            }
        }
    }


    //TODO question Adapter class
    public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

        private Context context;
        private List<InterViewQuestion> questionList;

        private boolean isCheck = true;

        private QuestionAdapter(Context context, List<InterViewQuestion> questionList) {
            this.context = context;
            this.questionList = questionList;
        }

        @NonNull
        @Override
        public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_interview_questions, viewGroup, false);
            return new QuestionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull QuestionViewHolder questionViewHolder, int i) {
            final String[] VIDEO_URL = {""};
            InterViewQuestion interViewQuestion = questionList.get(i);

            if (interViewQuestion.getQtype().equalsIgnoreCase("1")) {

                if (interViewQuestion.getHls().equalsIgnoreCase("")) { //TODO HLS BALANK THEN FILE URL
                    VIDEO_URL[0] = interViewQuestion.getFileurl();  //TODO FILE URL
                } else if (!interViewQuestion.getHls().equalsIgnoreCase("")) { //TODO  NOT BLANK THEN HLS ITSELF
                    VIDEO_URL[0] = interViewQuestion.getHls(); //TODO  HLS URL
                } else if (interViewQuestion.getHls().equalsIgnoreCase("") && interViewQuestion.getFileurl().equalsIgnoreCase("")) {
                    VIDEO_URL[0] = "";
                }

                questionViewHolder.videoLayout.setVisibility(View.VISIBLE);
                questionViewHolder.essaLayout.setVisibility(View.GONE);
                questionViewHolder.multipleLayout.setVisibility(View.GONE);
                questionViewHolder.tvVideoTitle.setText(interViewQuestion.getQuesTitle());

                if (!TextUtils.isEmpty(interViewQuestion.getPoster()))
                    Picasso.get().load(interViewQuestion.getPoster()).error(R.drawable.background).resize(100, 100).into(questionViewHolder.imgVideoPoster);

                questionViewHolder.imgPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (VIDEO_URL[0] != null) {
                            if (VIDEO_URL[0].equalsIgnoreCase("")) {
                                Toast.makeText(context, "No Video Found", Toast.LENGTH_LONG).show();
                            } else {
                                Sample sample = new UriSample("Super speed (PlayReady)", null, Uri.parse(VIDEO_URL[0]), null, null, null);
                                startActivity(sample.buildIntent(context, false, PlayerActivity.ABR_ALGORITHM_DEFAULT));
                            }
                        } else {
                            Toast.makeText(context, "Please upload the video first", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            if (interViewQuestion.getQtype().equalsIgnoreCase("2")) {
                questionViewHolder.videoLayout.setVisibility(View.GONE);
                questionViewHolder.essaLayout.setVisibility(View.GONE);
                questionViewHolder.multipleLayout.setVisibility(View.VISIBLE);
                questionViewHolder.tvMultiTitle.setText(interViewQuestion.getQuesTitle());
                questionViewHolder.recyclerView.setHasFixedSize(true);
                questionViewHolder.tvCorrectAns.setText(interViewQuestion.getOptions().get(Integer.valueOf(interViewQuestion.getCorrect())));
                if (!TextUtils.isEmpty(interViewQuestion.getAnswer())) {
                    questionViewHolder.tvUserAnswer.setText(interViewQuestion.getOptions().get(Integer.valueOf(interViewQuestion.getAnswer())));
                    if (interViewQuestion.getOptions().get(Integer.valueOf(interViewQuestion.getAnswer())).equalsIgnoreCase(interViewQuestion.getOptions().get(Integer.valueOf(interViewQuestion.getCorrect()))))
                        questionViewHolder.tvUserAnswer.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tik_small_green, 0);
                    else
                        questionViewHolder.tvUserAnswer.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_tik_small_red, 0);
                } else questionViewHolder.tvUserAnswer.setText("N/A");
                questionViewHolder.recyclerView.setAdapter(new MultiAnsAdapter(context, interViewQuestion.getOptions()));
            }

            if (interViewQuestion.getQtype().equalsIgnoreCase("3")) {

                questionViewHolder.tvEssayAnswer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (questionViewHolder.tvEssayAnswer.getLineCount() > 3) {
                            questionViewHolder.imgReadMore.setVisibility(View.VISIBLE);
                        }
                    }
                });

                questionViewHolder.imgReadMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isCheck) {
                            questionViewHolder.tvEssayAnswer.setMaxLines(100);
                            questionViewHolder.imgReadMore.setRotation(180f);
                            isCheck = false;
                        } else {
                            questionViewHolder.tvEssayAnswer.setMaxLines(3);
                            questionViewHolder.imgReadMore.setRotation(0f);
                            isCheck = true;
                        }
                    }

                });

                questionViewHolder.videoLayout.setVisibility(View.GONE);
                questionViewHolder.essaLayout.setVisibility(View.VISIBLE);
                questionViewHolder.multipleLayout.setVisibility(View.GONE);
                questionViewHolder.tvEssayTitle.setText(Html.fromHtml(interViewQuestion.getQuesTitle()));

                if (!TextUtils.isEmpty(interViewQuestion.getAnswer())) {
                    questionViewHolder.tvEssayAnswer.setText(Html.fromHtml(interViewQuestion.getAnswer()));
                } else
                    questionViewHolder.tvEssayAnswer.setText("N/A");

                if (questionViewHolder.tvEssayAnswer.getLineCount() > 0) {
                    questionViewHolder.imgReadMore.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return ObjectUtil.isEmpty(questionList) ? 0 : questionList.size();
        }

        public class QuestionViewHolder extends RecyclerView.ViewHolder {
            private ConstraintLayout videoLayout, essaLayout, multipleLayout;
            private TextView tvVideoTitle, tvEssayTitle, tvEssayAnswer, tvMultiTitle, tvUserAnswer, tvCorrectAns;
            private ImageView imgVideoPoster, imgReadMore, imgPlay;
            private RecyclerView recyclerView;

            private QuestionViewHolder(@NonNull View itemView) {
                super(itemView);
                tvEssayAnswer = itemView.findViewById(R.id.textView249);
                tvEssayTitle = itemView.findViewById(R.id.textView241);
                imgVideoPoster = itemView.findViewById(R.id.imageView89);
                tvVideoTitle = itemView.findViewById(R.id.textView246);
                tvMultiTitle = itemView.findViewById(R.id.textView24);
                videoLayout = itemView.findViewById(R.id.constraintLayout61);
                multipleLayout = itemView.findViewById(R.id.constraintLayout611);
                essaLayout = itemView.findViewById(R.id.constraintLayout6111);
                imgReadMore = itemView.findViewById(R.id.imageView91);
                recyclerView = itemView.findViewById(R.id.recyclerView4);
                tvUserAnswer = itemView.findViewById(R.id.textView251);
                tvCorrectAns = itemView.findViewById(R.id.textView254);
                imgPlay = itemView.findViewById(R.id.imageView90);
            }
        }
    }

    //TODO Adapter for multiple Answer
    public class MultiAnsAdapter extends RecyclerView.Adapter<MultiAnsAdapter.MultiAnsViewHolder> {

        private Context context;
        private List<String> multiAnsList;
        private List<String> orderList = new ArrayList<>();

        private MultiAnsAdapter(Context context, List<String> multiAnsList) {
            this.context = context;
            this.multiAnsList = multiAnsList;
            orderList.add("a.");
            orderList.add("b.");
            orderList.add("c.");
            orderList.add("d.");
            orderList.add("e.");
            orderList.add("f.");
        }

        @NonNull
        @Override
        public MultiAnsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_multiple_answer, viewGroup, false);
            return new MultiAnsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MultiAnsViewHolder multiAnsViewHolder, int i) {
            multiAnsViewHolder.tvAns.setText(orderList.get(i) + " " + multiAnsList.get(i));
        }

        @Override
        public int getItemCount() {
            return multiAnsList.size();
        }

        public class MultiAnsViewHolder extends RecyclerView.ViewHolder {

            private TextView tvAns;

            private MultiAnsViewHolder(@NonNull View itemView) {
                super(itemView);
                tvAns = itemView.findViewById(R.id.textView247);
            }
        }
    }

    // TODO Summary Adapter class
    public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.SummaryViewHolder> {

        private Context context;
        private List<SummaryRatingData> summrylist;
        private List<FeedbackParam> feedbackRatingList;
        private String view;

        private SummaryAdapter(Context context, List<SummaryRatingData> summrylist) {
            this.context = context;
            this.summrylist = summrylist;
        }

        private SummaryAdapter(Context context, List<FeedbackParam> summrylist, String view) {
            this.context = context;
            this.feedbackRatingList = summrylist;
            this.view = view;
        }

        @NonNull
        @Override
        public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_rating_layout, viewGroup, false);
            return new SummaryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SummaryViewHolder summaryViewHolder, int i) {

            SummaryRatingData summaryRatingData = null;
            FeedbackParam feedbackParam = null;
            float value;
            summaryViewHolder.ratingBar.setIsIndicator(true);
            if (summrylist != null) {
                summaryRatingData = summrylist.get(i);
            } else {
                feedbackParam = feedbackRatingList.get(i);
            }
            if (view != null)
                value = Float.parseFloat(feedbackParam.getValue());
            else
                value = Float.parseFloat(summaryRatingData.getValue());
            if (value < 2.0) {
                summaryViewHolder.redstarLayout.setVisibility(View.VISIBLE);
                summaryViewHolder.greenstarLayout.setVisibility(View.GONE);
                summaryViewHolder.yellowstarLayuot.setVisibility(View.GONE);
                if (view != null) {
                    summaryViewHolder.ratingBar.setRating(Float.parseFloat(feedbackParam.getValue()));
                    summaryViewHolder.tvRatingType.setText(feedbackParam.getKey());
                } else {
                    summaryViewHolder.ratingBar.setRating(Float.parseFloat(summaryRatingData.getValue()));
                    summaryViewHolder.tvRatingType.setText(summaryRatingData.getKey());
                }
            }
            if (value < 4.0 && value >= 2.0) {
                summaryViewHolder.redstarLayout.setVisibility(View.GONE);
                summaryViewHolder.greenstarLayout.setVisibility(View.GONE);
                summaryViewHolder.yellowstarLayuot.setVisibility(View.VISIBLE);
                if (view != null) {
                    summaryViewHolder.yelloRatingBar.setRating(Float.parseFloat(feedbackParam.getValue()));
                    summaryViewHolder.tvYelloRatingType.setText(feedbackParam.getKey());
                } else {
                    summaryViewHolder.yelloRatingBar.setRating(Float.parseFloat(summaryRatingData.getValue()));
                    summaryViewHolder.tvYelloRatingType.setText(summaryRatingData.getKey());
                }
            }
            if (value <= 5.0 && value >= 4.0) {
                summaryViewHolder.redstarLayout.setVisibility(View.GONE);
                summaryViewHolder.greenstarLayout.setVisibility(View.VISIBLE);
                summaryViewHolder.yellowstarLayuot.setVisibility(View.GONE);
                if (view != null) {
                    summaryViewHolder.greenRatingBar.setRating(Float.parseFloat(feedbackParam.getValue()));
                    summaryViewHolder.tvGreenRatingType.setText(feedbackParam.getKey());
                } else {
                    summaryViewHolder.greenRatingBar.setRating(Float.parseFloat(summaryRatingData.getValue()));
                    summaryViewHolder.tvGreenRatingType.setText(summaryRatingData.getKey());
                }
            }
        }

        @Override
        public int getItemCount() {
            if (view != null) {
                return feedbackRatingList.size();
            } else {
                return summrylist.size();
            }
        }

        public class SummaryViewHolder extends RecyclerView.ViewHolder {
            private TextView tvRatingType, tvYelloRatingType, tvGreenRatingType;
            private ScaleRatingBar ratingBar, yelloRatingBar, greenRatingBar;
            private ConstraintLayout redstarLayout, yellowstarLayuot, greenstarLayout;

            private SummaryViewHolder(@NonNull View itemView) {
                super(itemView);
                tvRatingType = itemView.findViewById(R.id.textView241);
                ratingBar = itemView.findViewById(R.id.ratingBar);
                redstarLayout = itemView.findViewById(R.id.constraintLayout63);
                yellowstarLayuot = itemView.findViewById(R.id.constraintLayout64);
                greenstarLayout = itemView.findViewById(R.id.layout65);
                yelloRatingBar = itemView.findViewById(R.id.ratingBar1);
                greenRatingBar = itemView.findViewById(R.id.ratingBar2);
                tvGreenRatingType = itemView.findViewById(R.id.textView2412);
                tvYelloRatingType = itemView.findViewById(R.id.textView2411);
            }
        }
    }

    //TODO FeedBack Adpater class
    public class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.FeedBackViewHolder> {

        private List<FeedBackData> feedbackList;
        private Context context;
        private boolean isopen;

        private FeedBackAdapter(List<FeedBackData> feedbackList, Context context) {
            this.feedbackList = feedbackList;
            this.context = context;
        }

        @NonNull
        @Override
        public FeedBackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_team_feedback, viewGroup, false);
            return new FeedBackViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FeedBackViewHolder feedBackViewHolder, int i) {

            FeedBackData feedBackData = feedbackList.get(i);
            feedBackViewHolder.tvEmail.setText(feedBackData.getRatedBy());
            feedBackViewHolder.tvTime.setText(feedBackData.getTimeUpdated());
            if (feedBackData.getRecommended().equalsIgnoreCase("1")) {
                feedBackViewHolder.tvRecoYes.setText("Yes");
                feedBackViewHolder.tvRecoNo.setVisibility(View.GONE);
                feedBackViewHolder.tvRecoYes.setVisibility(View.VISIBLE);
            } else {
                feedBackViewHolder.tvRecoNo.setText("No");
                feedBackViewHolder.tvRecoYes.setVisibility(View.GONE);
                feedBackViewHolder.tvRecoNo.setVisibility(View.VISIBLE);
            }

            feedBackViewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isopen) {
                        feedBackViewHolder.dropDownimg.setRotation(0);
                        feedBackViewHolder.childLayout.setVisibility(View.GONE);
                        isopen = false;
                    } else {
                        feedBackViewHolder.recFeedbackRating.setHasFixedSize(true);
                        feedBackViewHolder.recFeedbackRating.setAdapter(new SummaryAdapter(context, feedBackData.getFeedbackParams(), "feedBack"));
                        feedBackViewHolder.childLayout.setVisibility(View.VISIBLE);
                        feedBackViewHolder.childLayout.setVisibility(View.VISIBLE);
                        isopen = true;
                        feedBackViewHolder.dropDownimg.setRotation(180);
                    }
                }
            });

            float value = Float.parseFloat(feedBackData.getRating());
            if (value < 2.0) {
                feedBackViewHolder.summaryOverallRatingred.setVisibility(View.VISIBLE);
                feedBackViewHolder.summaryOverallRatingyellow.setVisibility(View.GONE);
                feedBackViewHolder.summaryOverallRatingGreen.setVisibility(View.GONE);
                feedBackViewHolder.summaryOverallRatingred.setRating(Float.parseFloat(feedBackData.getRating()));
            }
            if (value < 4.0 && value >= 2.0) {
                feedBackViewHolder.summaryOverallRatingred.setVisibility(View.GONE);
                feedBackViewHolder.summaryOverallRatingyellow.setVisibility(View.VISIBLE);
                feedBackViewHolder.summaryOverallRatingGreen.setVisibility(View.GONE);
                feedBackViewHolder.summaryOverallRatingyellow.setRating(Float.parseFloat(feedBackData.getRating()));
            }
            if (value <= 5.0 && value >= 4.0) {
                feedBackViewHolder.summaryOverallRatingred.setVisibility(View.GONE);
                feedBackViewHolder.summaryOverallRatingyellow.setVisibility(View.GONE);
                feedBackViewHolder.summaryOverallRatingGreen.setVisibility(View.VISIBLE);
                feedBackViewHolder.summaryOverallRatingGreen.setRating(Float.parseFloat(feedBackData.getRating()));
            }
            feedBackViewHolder.tvShowRating.setText("" + feedBackData.getRating() + " Stars");
        }

        @Override
        public int getItemCount() {
            return feedbackList.size();
        }

        public class FeedBackViewHolder extends RecyclerView.ViewHolder {

            private TextView tvEmail, tvTime, tvShowRating, tvRecoYes, tvRecoNo;
            private ImageView dropDownimg;
            private LinearLayout childLayout;
            private RecyclerView recFeedbackRating;
            private ConstraintLayout constraintLayout;
            private ScaleRatingBar summaryOverallRatingred, summaryOverallRatingyellow, summaryOverallRatingGreen;

            private FeedBackViewHolder(@NonNull View itemView) {
                super(itemView);
                tvEmail = itemView.findViewById(R.id.textView255);
                tvTime = itemView.findViewById(R.id.textView256);
                dropDownimg = itemView.findViewById(R.id.imageView92);
                childLayout = itemView.findViewById(R.id.feedback_child);
                recFeedbackRating = itemView.findViewById(R.id.rec_feedback);
                summaryOverallRatingyellow = itemView.findViewById(R.id.ratingbar_overall_yellow);
                summaryOverallRatingGreen = itemView.findViewById(R.id.ratingbar_overall_green);
                summaryOverallRatingred = itemView.findViewById(R.id.ratingbar_overall_red);
                tvShowRating = itemView.findViewById(R.id.tv_show_rating);
                tvRecoYes = itemView.findViewById(R.id.reco_yes);
                tvRecoNo = itemView.findViewById(R.id.reco_no);
                constraintLayout = itemView.findViewById(R.id.constraintLayout65);
            }
        }

    }
}
