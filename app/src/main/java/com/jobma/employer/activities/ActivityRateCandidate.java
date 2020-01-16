package com.jobma.employer.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.candidate_rating.CandidateRatingModel;
import com.jobma.employer.model.candidate_rating.EOGetRatingRow;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class ActivityRateCandidate extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private RecyclerView recRating;
    private List<CandidateRatingModel> candidateRatingList = new ArrayList<>();
    private ScaleRatingBar overallRatingbar;
    private TextView tvOverallRating;
    private RadioGroup radioGroup;
    private RadioButton radioYes, radioNo;
    private Button btnSubmit;
    private ImageView ivBackBtn;
    private EditText et_enter_comment;
    private String recommended;
    private int appliedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rate_candidate);

        if (!ObjectUtil.isEmpty(this.getIntent().getIntExtra("appliedId", 0))) {
            this.appliedId = this.getIntent().getIntExtra("appliedId", 0);
        }

        this.initView();
        this.setOnclickListner();

        this.getRatingValues();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());

        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.overallRatingbar = findViewById(R.id.simpleRatingBar);
        this.tvOverallRating = findViewById(R.id.textView229);
        this.recRating = findViewById(R.id.recyclerView5);
        this.radioGroup = this.findViewById(R.id.radioGroup);
        this.radioYes = this.findViewById(R.id.radioYes);
        this.radioNo = this.findViewById(R.id.radioNo);
        this.btnSubmit = this.findViewById(R.id.button27);
        this.ivBackBtn = this.findViewById(R.id.ivBackBtn);
        this.et_enter_comment = this.findViewById(R.id.editText21);
    }

    private void setOnclickListner() {

        this.btnSubmit.setOnClickListener(this);
        this.ivBackBtn.setOnClickListener(this);
        this.radioGroup.setOnCheckedChangeListener(this.onCheckedChangeListener);

        overallRatingbar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating, boolean fromUser) {
                if (rating == 1)
                    ratingBar.setFilledDrawableRes(R.drawable.ic_red_star);
                if (rating == 2)
                    ratingBar.setFilledDrawableRes(R.drawable.ic_yellow_star);
                if (rating == 3)
                    ratingBar.setFilledDrawableRes(R.drawable.ic_yellow_star);
                if (rating == 4)
                    ratingBar.setFilledDrawableRes(R.drawable.ic_green_star);
                if (rating == 5)
                    ratingBar.setFilledDrawableRes(R.drawable.ic_green_star);
            }
        });
    }

    private void getRatingValues() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.getRatings(apiKey).enqueue(new Callback<EOGetRatingRow>() {
                @Override
                public void onResponse(Call<EOGetRatingRow> call, Response<EOGetRatingRow> response) {
                    progress.hideProgressBar();
                    if (response.body() != null) {

                        if (response.body().getError() == 0 && response.body().getData().size() > 0) {
                            for (String s : response.body().getData()) {
                                candidateRatingList.add(new CandidateRatingModel(s, 0));
                            }
                            recRating.setAdapter(new RatingAdapter(ActivityRateCandidate.this, candidateRatingList));
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOGetRatingRow> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityRateCandidate.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void overallRating() {
        float totalRating = 0;

        if (candidateRatingList.size() > 0) {
            for (CandidateRatingModel candidateRatingModel : candidateRatingList) {
                totalRating = totalRating + candidateRatingModel.getRatingCount();
            }
        }

        overallRatingbar.setRating((totalRating * 5) / (candidateRatingList.size() * 5));
        tvOverallRating.setText(String.valueOf((totalRating * 5) / (candidateRatingList.size() * 5)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBackBtn:
                this.finish();
                break;
            case R.id.button27:
                if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_enter_comment))) {
                    Toast.makeText(this, "Please enter comment", Toast.LENGTH_SHORT).show();
                } else {
                    submitRatingToCandidate();
                }
                break;
        }
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            RadioButton checkedRadioButton = group.findViewById(checkedId);
            switch (checkedRadioButton.getId()) {
                case R.id.radioYes:
                    recommended = "1";
                    return;
                case R.id.radioNo:
                    recommended = "0";
            }
        }
    };

    private void submitRatingToCandidate() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.addRatingToCandidates(apiKey, appliedId, ObjectUtil.getTextFromView(et_enter_comment), recommended, TextUtils.join(",", getRatingsValue())).enqueue(new Callback<EOForgetPassword>() {
                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword ratingProfile = response.body();
                        if (!ObjectUtil.isEmpty(ratingProfile)) {
                            if (ratingProfile.getError() == RESPONSE_SUCCESS) {
                                Toast.makeText(ActivityRateCandidate.this, "" + ratingProfile.getMessage(), Toast.LENGTH_SHORT).show();
                                ActivityRateCandidate.this.finish();
                            } else {
                                //Toast.makeText(ActivityRateCandidate.this, "" + ratingProfile.getMessage(), Toast.LENGTH_SHORT).show();
                                showNoDataFoundDialog(ratingProfile.getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityRateCandidate.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showNoDataFoundDialog(String dialogMessage) {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
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

    private ArrayList<Integer> getRatingsValue() {
        ArrayList<Integer> valueList = new ArrayList<>();
        for (CandidateRatingModel candidateRatingModel : candidateRatingList) {
            valueList.add((int) candidateRatingModel.getRatingCount());
        }
        return valueList;
    }

    //TODO Adapter class
    public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingViewHolder> {

        private Context context;
        private List<CandidateRatingModel> ratingList;

        private RatingAdapter(Context context, List<CandidateRatingModel> ratingList) {
            this.context = context;
            this.ratingList = ratingList;
        }

        @NonNull
        @Override
        public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_rating_layout, viewGroup, false);
            return new RatingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RatingViewHolder ratingViewHolder, int i) {

            CandidateRatingModel candidateRatingModel = ratingList.get(i);
            ratingViewHolder.ratingType.setText(candidateRatingModel.getRatingType());

            ratingViewHolder.ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
                @Override
                public void onRatingChange(BaseRatingBar ratingBar, float rating, boolean fromUser) {

                    if (rating == 1)
                        ratingBar.setFilledDrawableRes(R.drawable.ic_red_star);
                    if (rating == 2)
                        ratingBar.setFilledDrawableRes(R.drawable.ic_yellow_star);
                    if (rating == 3)
                        ratingBar.setFilledDrawableRes(R.drawable.ic_yellow_star);
                    if (rating == 4)
                        ratingBar.setFilledDrawableRes(R.drawable.ic_green_star);
                    if (rating == 5)
                        ratingBar.setFilledDrawableRes(R.drawable.ic_green_star);

                    candidateRatingModel.setRatingCount(rating);

                    overallRating();
                }
            });

        }

        @Override
        public int getItemCount() {
            return ObjectUtil.isEmpty(ratingList) ? 0 : ratingList.size();
        }

        class RatingViewHolder extends RecyclerView.ViewHolder {

            private TextView ratingType;
            private ScaleRatingBar ratingBar;

            private RatingViewHolder(@NonNull View itemView) {
                super(itemView);
                ratingType = itemView.findViewById(R.id.textView241);
                ratingBar = itemView.findViewById(R.id.ratingBar);
            }
        }
    }
}
