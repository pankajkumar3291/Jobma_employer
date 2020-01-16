package com.jobma.employer.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.DialogMultipleChoice;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.exoPlayer.PlayerActivity;
import com.jobma.employer.exoPlayer.Sample;
import com.jobma.employer.exoPlayer.UriSample;
import com.jobma.employer.model.get_interview.EOGetInterviewRequest;
import com.jobma.employer.model.get_interview.EOInterViewQuestion;
import com.jobma.employer.model.interviewKit.EOCreateInterviewKit;
import com.jobma.employer.model.mcq_question_request.EOMCQuestionRequest;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.MessageEvent;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;

import net.alhazmy13.mediapicker.Video.VideoPicker;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class ActivityInterviewKit extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private ConstraintLayout layoutMultiChoice, essayLayout, videoLayout;
    private ImageView ivBackBtn;
    private RecyclerView recQuestions;
    private String kitId;
    private String apikey;
    private EditText etTitle;
    private Button btnSubmit;
    private ImageView videoThamnail, videoicon;
    private SessionSecuredPreferences loginPreferences;
    private APIClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private List<EOInterViewQuestion> interviewList = new ArrayList<>();
    private InterviewKitAdapter interviewKitAdapter;
    private TextView tvQuestionHeading;
    private DialogMultipleChoice dialogMultipleChoice;
    private Dialog mediaDialog = null;
    private String videopath = "";
    private TextView tvAddVideo;
    private List<String> idList = new ArrayList<>();
    private List<String> durationList = new ArrayList<>();
    private List<String> thinkTimeList = new ArrayList<>();
    private List<String> retakeList = new ArrayList<>();
    private List<String> optionalList = new ArrayList<>();
    private boolean isEditing = false;
    private String kitid = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_interview_kit);

        this.initView();
        this.setOnClickListener();

        if (getIntent() != null && getIntent().hasExtra("kitId")) {
            kitId = String.valueOf(getIntent().getIntExtra("kitId", 0));
            isEditing = true;
            callapi();
        }
    }

    private void callapi() {
        try {
            if (!ObjectUtil.isEmpty(this.apikey)) {
                progress.showProgressBar();
                apiInterface.inerview(apikey, kitId).enqueue(new Callback<EOGetInterviewRequest>() {
                    @Override
                    public void onResponse(Call<EOGetInterviewRequest> call, Response<EOGetInterviewRequest> response) {
                        progress.hideProgressBar();
                        if (!ObjectUtil.isEmpty(response.body())) {
                            EOGetInterviewRequest eoEvaluateCandidates = response.body();
                            if (!ObjectUtil.isEmpty(eoEvaluateCandidates)) {

                                kitid = eoEvaluateCandidates.getData().getId().toString();
                                if (eoEvaluateCandidates.getError() == 0 && eoEvaluateCandidates.getData().getQuestionSet().getQuestion() != null) {
                                    if (eoEvaluateCandidates.getData().getQuestionSet().getQuestion().size() > 0) {
                                        etTitle.setText(eoEvaluateCandidates.getData().getTitle());
                                        interviewList.addAll(eoEvaluateCandidates.getData().getQuestionSet().getQuestion());
                                        interviewKitAdapter.notifyDataSetChanged();
                                    } else {
                                        tvQuestionHeading.setVisibility(View.GONE);
                                    }
                                } else {
                                    tvQuestionHeading.setVisibility(View.GONE);

//                                tv_no_data.setVisibility(View.VISIBLE);
//                                recInterviewkit.setVisibility(View.GONE);
//                                Toast.makeText(getActivity(),""+eoEvaluateCandidates.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                tvQuestionHeading.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EOGetInterviewRequest> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            tvQuestionHeading.setVisibility(View.GONE);
                            Toast.makeText(ActivityInterviewKit.this, "Failed Error :" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            tvQuestionHeading.setVisibility(View.GONE);
            progress.hideProgressBar();
        }
    }

    private void initView() {
        btnSubmit = findViewById(R.id.button12);
        btnSubmit.setOnClickListener(this);
        tvQuestionHeading = findViewById(R.id.textView227);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiInterface = APIClient.getClient();
        progress = new GlobalProgressDialog(ActivityInterviewKit.this);
        this.apikey = loginPreferences.getString(SELECTED_API_KEY, "");
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        interviewKitAdapter = new InterviewKitAdapter(interviewList, ActivityInterviewKit.this);
        this.layoutMultiChoice = findViewById(R.id.constraintLayout27);
        this.recQuestions = findViewById(R.id.rec_questions);
        this.recQuestions.setHasFixedSize(true);
        this.recQuestions.setAdapter(interviewKitAdapter);
        this.recQuestions.setLayoutManager(new LinearLayoutManager(ActivityInterviewKit.this));
        this.ivBackBtn = this.findViewById(R.id.ivBackBtn);
        this.videoLayout = findViewById(R.id.constraintLayout26);
        this.essayLayout = findViewById(R.id.constraintLayout272);
        this.etTitle = findViewById(R.id.ettitle);
    }

    private void setOnClickListener() {
        this.ivBackBtn.setOnClickListener(this);
        this.essayLayout.setOnClickListener(this);
        this.videoLayout.setOnClickListener(this);
        this.layoutMultiChoice.setOnClickListener(this);
    }

    protected void onResume() {
        this.noNet.RegisterNoNet();
        super.onResume();
    }

    @Override
    protected void onPause() {
        this.noNet.unRegisterNoNet();
        super.onPause();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(ActivityInterviewKit.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_essay_question);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.colorWhite, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        EditText etquestion = dialog.findViewById(R.id.editText20);
        Button okButton = dialog.findViewById(R.id.button25);
        Button cancleButton = dialog.findViewById(R.id.button26);
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etquestion.getText().toString())) {
                    etquestion.setError("Can't Empty!");
                    etquestion.setFocusable(true);
                } else {
                    progress.showProgressBar();
                    Map<String, Object> jsonParams = new ArrayMap<>();
                    jsonParams.put("qtype", "3");
                    jsonParams.put("qcontent", etquestion.getText().toString());
                    //todo                   api for essay type question
                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());
                    try {
                        apiInterface.addInterviewQuestions(apikey, body).enqueue(new Callback<EOMCQuestionRequest>() {
                            @Override
                            public void onResponse(Call<EOMCQuestionRequest> call, Response<EOMCQuestionRequest> response) {
                                progress.hideProgressBar();
                                if (response.body() != null) {
                                    if (response.body().getError() == 0 && response.body().getData() != null) {
                                        Toast.makeText(ActivityInterviewKit.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        interviewList.add(new EOInterViewQuestion(response.body().getData().getQid(), response.body().getData().getQcontent(), "3", "", "", "", "", "", "0", null, "", "", "", "", ""));
                                        interviewKitAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(ActivityInterviewKit.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<EOMCQuestionRequest> call, Throwable t) {
                                progress.hideProgressBar();
                                Toast.makeText(ActivityInterviewKit.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        progress.hideProgressBar();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void showVideoDialog() {
        final Dialog dialog = new Dialog(ActivityInterviewKit.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_video);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.colorWhite, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        tvAddVideo = dialog.findViewById(R.id.textView216);
        tvAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaDialog = new Dialog(ActivityInterviewKit.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                mediaDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mediaDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                mediaDialog.setContentView(R.layout.dialog_interviewkit_media_upload);
                Button btnCamera = mediaDialog.findViewById(R.id.btnRetakeAvailable);
                Window window = mediaDialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                mediaDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                wlp.gravity = Gravity.BOTTOM;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                Button btnGallery = mediaDialog.findViewById(R.id.btnPublishAndContiAvailable);

                btnCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentRecordVideo = new Intent(ActivityInterviewKit.this, ActivityVideoRecording.class);
                        intentRecordVideo.putExtra("from", "interviewKit");
                        startActivityForResult(intentRecordVideo, 123);
                        Animatoo.animateFade(ActivityInterviewKit.this);
                    }
                });
                btnGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new VideoPicker.Builder(ActivityInterviewKit.this)
                                .mode(VideoPicker.Mode.GALLERY)
                                .directory(VideoPicker.Directory.DEFAULT)
                                .extension(VideoPicker.Extension.MP4)
                                .enableDebuggingMode(true)
                                .build();
                    }
                });
                mediaDialog.show();
            }
        });
        videoicon = dialog.findViewById(R.id.imageView88);
        videoicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(videopath)) {
                    Sample sample = new UriSample("Super speed (PlayReady)", null, Uri.parse(videopath), null, null, null);
                    startActivity(sample.buildIntent(ActivityInterviewKit.this, false, PlayerActivity.ABR_ALGORITHM_DEFAULT));
                }
            }
        });
        videoThamnail = dialog.findViewById(R.id.imageView84);
        EditText etquestion = dialog.findViewById(R.id.editText20);
        Button okButton = dialog.findViewById(R.id.button25);
        Button cancleButton = dialog.findViewById(R.id.button26);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etquestion.getText().toString())) {
                    MultipartBody.Part body = null;
                    if (!TextUtils.isEmpty(videopath)) {
                        File file = new File(videopath);
                        final RequestBody reqFile = RequestBody.create(MediaType.parse("video/*"), file);
                        body = MultipartBody.Part.createFormData("kit_video", file.getName(), reqFile);
                    }
                    progress.showProgressBar();
                    try {
                        RequestBody qContent = RequestBody.create(MediaType.parse("multipart/form-data"), etquestion.getText().toString());

                        apiInterface.addVideoquestion(apikey, 1, qContent, body).enqueue(new Callback<EOMCQuestionRequest>() {
                            @Override
                            public void onResponse(Call<EOMCQuestionRequest> call, Response<EOMCQuestionRequest> response) {
                                progress.hideProgressBar();
                                if (response.body() != null) {
                                    if (response.body().getError() == 0 && response.body().getData() != null) {
                                        Toast.makeText(ActivityInterviewKit.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        interviewList.add(new EOInterViewQuestion(response.body().getData().getQid(), etquestion.getText().toString(), "1", "", "", "", "-1", "", "0", null, "", "", "", "", ""));
                                        interviewKitAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(ActivityInterviewKit.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<EOMCQuestionRequest> call, Throwable t) {
                                progress.hideProgressBar();
                                Toast.makeText(ActivityInterviewKit.this, t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        progress.hideProgressBar();
                        e.printStackTrace();
                    }
                } else {
                    etquestion.setError("Field required!");
                    etquestion.setFocusable(true);
                }
            }
        });
        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        //Toast.makeText(getApplicationContext(), "called event", Toast.LENGTH_SHORT).show();
    }

    public void addMultiQuestion(int qid, String qTitle, int type) {
        interviewList.add(new EOInterViewQuestion(qid, qTitle, String.valueOf(type), "", "", "", "", "", "0", null, "", "", "", "", ""));
        interviewKitAdapter.notifyDataSetChanged();
        dialogMultipleChoice.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBackBtn:
                this.finish();
                break;
            case R.id.constraintLayout272:
                showDialog();
                break;
            case R.id.constraintLayout26:
//                mypopupWindow.showAsDropDown(view,-153,0);
                showVideoDialog();
                break;
            case R.id.constraintLayout27:
                dialogMultipleChoice = new DialogMultipleChoice(ActivityInterviewKit.this);
                dialogMultipleChoice.show();
                break;
            case R.id.button12:


                submitAllData();
                break;
        }
    }

    private void submitAllData() {
        idList.clear();
        durationList.clear();
        thinkTimeList.clear();
        retakeList.clear();
        optionalList.clear();
        String title = "";

        if (TextUtils.isEmpty(etTitle.getText().toString())) {
            etTitle.setError("Field required !");
            etTitle.setFocusable(true);
        } else if (interviewList.size() == 0) {
            Toast.makeText(ActivityInterviewKit.this, "Please add at lease one question", Toast.LENGTH_SHORT).show();
        } else {
            title = etTitle.getText().toString();

            if (interviewList.size() > 0) {
                for (EOInterViewQuestion eoInterViewQuestion : interviewList) {

                    idList.add(String.valueOf(eoInterViewQuestion.getQues()));

                    if (TextUtils.isEmpty(eoInterViewQuestion.getDuration()))
                        durationList.add("0");
                    else
                        durationList.add(eoInterViewQuestion.getDuration());
                    thinkTimeList.add(eoInterViewQuestion.getThinktime());
                    if (TextUtils.isEmpty(eoInterViewQuestion.getAttempts()))
                        retakeList.add("0");
                    else
                        retakeList.add(eoInterViewQuestion.getAttempts());
                    optionalList.add(eoInterViewQuestion.getOptional());
                }
                try {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("kit_title", title);
                    map.put("qid", TextUtils.join(",", idList));
                    map.put("duration", TextUtils.join(",", durationList));
                    map.put("thinktime", TextUtils.join(",", thinkTimeList));
                    map.put("retake", TextUtils.join(",", retakeList));
                    map.put("optional", TextUtils.join(",", optionalList));
                    map.put("random_ques", "0");
                    map.put("random_option", "0");
                    if (isEditing)
                        map.put("id", kitid);

                    progress.showProgressBar();
                    apiInterface.addKitRequest(isEditing ? "edit-kit" : "add-kit", apikey, map).enqueue(new Callback<EOCreateInterviewKit>() {
                        @Override
                        public void onResponse(Call<EOCreateInterviewKit> call, Response<EOCreateInterviewKit> response) {
                            progress.hideProgressBar();
                            if (response.body().getError() == 0) {
                                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<EOCreateInterviewKit> call, Throwable t) {
                            progress.hideProgressBar();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    progress.hideProgressBar();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == RESULT_OK) {
            mediaDialog.dismiss();
            tvAddVideo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_re_record_two, 0, 0, 0);
            tvAddVideo.setText("Re-Record");
            videopath = data.getStringExtra("path");
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videopath, MediaStore.Images.Thumbnails.MINI_KIND);
            videoThamnail.setVisibility(View.VISIBLE);
            videoicon.setVisibility(View.VISIBLE);
            videoThamnail.setImageBitmap(thumb);
        }
        if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            mediaDialog.dismiss();
            tvAddVideo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_re_record_two, 0, 0, 0);
            tvAddVideo.setText("Re-Record");
            List<String> videoPaths = data.getStringArrayListExtra(VideoPicker.EXTRA_VIDEO_PATH);
            videopath = videoPaths.get(0);
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videopath, MediaStore.Images.Thumbnails.MINI_KIND);
            Uri SevenSecVideo = Uri.fromFile(new File(videopath));
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(ActivityInterviewKit.this, SevenSecVideo);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMilliSec = Long.parseLong(time);
            long duration = timeInMilliSec / 1000;
            long hours = duration / 3600;
            long minutes = (duration - hours * 3600) / 60;
            long seconds = duration - (hours * 3600 + minutes * 60);
            if (minutes <= 2 && seconds <= 60) {
                videoThamnail.setVisibility(View.VISIBLE);
                videoicon.setVisibility(View.VISIBLE);
                videoThamnail.setImageBitmap(thumb);
            } else {
                Toast.makeText(this, "you can't upload video greater than 2 mint", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //TODO ===================================== Adapter class ===================================
    public class InterviewKitAdapter extends RecyclerView.Adapter<InterviewKitAdapter.InterviewKitViewHolder> {

        private List<EOInterViewQuestion> interVewList;
        private Context context;
        private String VIDEO_URL;

        public InterviewKitAdapter(List<EOInterViewQuestion> interVewList, Context context) {
            this.interVewList = interVewList;
            this.context = context;
        }

        @NonNull
        @Override
        public InterviewKitViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_interview_kit, viewGroup, false);
            return new InterviewKitViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull InterviewKitViewHolder interviewKitViewHolder, int i) {
            EOInterViewQuestion eoInterViewQuestion = interVewList.get(i);

            if (eoInterViewQuestion.getHls().equalsIgnoreCase("")) { //TODO HLS BALANK THEN FILE URL
                VIDEO_URL = eoInterViewQuestion.getFileurl();  //TODO FILE URL
            } else if (!eoInterViewQuestion.getHls().equalsIgnoreCase("")) { //TODO  NOT BLANK THEN HLS ITSELF
                VIDEO_URL = eoInterViewQuestion.getHls(); //TODO  HLS URL
            } else if (eoInterViewQuestion.getHls().equalsIgnoreCase("") && eoInterViewQuestion.getFileurl().equalsIgnoreCase("")) {
                VIDEO_URL = "";
            }

            if (!ObjectUtil.isEmpty(VIDEO_URL)) {
                interviewKitViewHolder.tvVideoView.setVisibility(View.VISIBLE);
            } else {
                interviewKitViewHolder.tvVideoView.setVisibility(View.GONE);
            }

            interviewKitViewHolder.crossImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interVewList.remove(i);
                    notifyDataSetChanged();
                }
            });


            if (eoInterViewQuestion.getOptional().equalsIgnoreCase("1"))
                interviewKitViewHolder.checkBox.setChecked(true);
            else
                interviewKitViewHolder.checkBox.setChecked(false);
            interviewKitViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < interVewList.size(); j++) {
                        if (j == i)
                            interVewList.get(j).setOptional("1");
                        else
                            interVewList.get(j).setOptional("0");
                    }
                    notifyDataSetChanged();
                }
            });
            if (eoInterViewQuestion.getQtype().equalsIgnoreCase("1")) {

                interviewKitViewHolder.tvVideoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (VIDEO_URL != null) {
                            if (VIDEO_URL.equalsIgnoreCase("")) {
                                Toast.makeText(context, "No Video Found", Toast.LENGTH_LONG).show();
                            } else {
                                Sample sample = new UriSample("Super speed (PlayReady)", null, Uri.parse(VIDEO_URL), null, null, null);
                                startActivity(sample.buildIntent(context, false, PlayerActivity.ABR_ALGORITHM_DEFAULT));
                            }
                        } else {
                            Toast.makeText(context, "Please upload the video first", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                interviewKitViewHolder.imgicon.setImageResource(R.drawable.video);
                interviewKitViewHolder.reTakeLayout.setVisibility(View.VISIBLE);
                interviewKitViewHolder.thinkTimeLayout.setVisibility(View.VISIBLE);
            } else if (eoInterViewQuestion.getQtype().equalsIgnoreCase("2")) {
                interviewKitViewHolder.tvVideoView.setVisibility(View.GONE);
                interviewKitViewHolder.imgicon.setImageResource(R.drawable.nav_multiple);
                interviewKitViewHolder.reTakeLayout.setVisibility(View.INVISIBLE);
                interviewKitViewHolder.thinkTimeLayout.setVisibility(View.GONE);
            } else {
                interviewKitViewHolder.tvVideoView.setVisibility(View.GONE);
                interviewKitViewHolder.imgicon.setImageResource(R.drawable.essay);
                interviewKitViewHolder.reTakeLayout.setVisibility(View.INVISIBLE);
                interviewKitViewHolder.thinkTimeLayout.setVisibility(View.GONE);
            }
            interviewKitViewHolder.tvtitlw.setText(Html.fromHtml(eoInterViewQuestion.getQuesTitle()));

            //todo for  question Retakes
            ArrayAdapter adapter = ArrayAdapter.createFromResource(context, R.array.retakes, R.layout.spinner_interview_kit);
            interviewKitViewHolder.spRetakes.setAdapter(adapter);

            if (!TextUtils.isEmpty(eoInterViewQuestion.getAttempts())) {

                int spinnerPosition;
                if (eoInterViewQuestion.getAttempts().equalsIgnoreCase("-1"))
                    spinnerPosition = adapter.getPosition("Unlimited");
                else if (eoInterViewQuestion.getAttempts().equalsIgnoreCase("0"))
                    spinnerPosition = adapter.getPosition("No Retakes");
                else
                    spinnerPosition = adapter.getPosition(eoInterViewQuestion.getAttempts());

                interviewKitViewHolder.spRetakes.setSelection(spinnerPosition);
            }
            interviewKitViewHolder.spRetakes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (eoInterViewQuestion.getQtype().equalsIgnoreCase("1")) {
                        if (interviewKitViewHolder.spRetakes.getSelectedItem().toString().equalsIgnoreCase("Unlimited"))
                            eoInterViewQuestion.setAttempts("-1");
                        else if (interviewKitViewHolder.spRetakes.getSelectedItem().toString().equalsIgnoreCase("No Retakes"))
                            eoInterViewQuestion.setAttempts("0");
                        else {
                            eoInterViewQuestion.setAttempts(interviewKitViewHolder.spRetakes.getSelectedItem().toString());
                        }
                    } else {
                        eoInterViewQuestion.setAttempts("0");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });


            //todo for Think Time
            ArrayAdapter adapter1 = ArrayAdapter.createFromResource(context, R.array.thinktime, R.layout.spinner_interview_kit);
            interviewKitViewHolder.spThinkTime.setAdapter(adapter1);
            if (!TextUtils.isEmpty(eoInterViewQuestion.getThinktime())) {
                int spinnerPosition = adapter1.getPosition(eoInterViewQuestion.getThinktime());
                interviewKitViewHolder.spThinkTime.setSelection(spinnerPosition);
            }
            interviewKitViewHolder.spThinkTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (interviewKitViewHolder.spThinkTime.getSelectedItem().toString().equalsIgnoreCase("Unlimited"))
                        interVewList.get(i).setThinktime("0");
                    else
                        interVewList.get(i).setThinktime(interviewKitViewHolder.spThinkTime.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            //todo for Time Duration
            ArrayAdapter adapter3;
            if (eoInterViewQuestion.getQtype().equalsIgnoreCase("1")) {
                adapter3 = ArrayAdapter.createFromResource(context, R.array.ansduration, R.layout.spinner_interview_kit);
            } else {
                adapter3 = ArrayAdapter.createFromResource(context, R.array.multiple_choice, R.layout.spinner_interview_kit);
            }

            interviewKitViewHolder.spTimeDuration.setAdapter(adapter3);
            interviewKitViewHolder.spTimeDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (eoInterViewQuestion.getQtype().equalsIgnoreCase("1")) {
                        eoInterViewQuestion.setDuration(interviewKitViewHolder.spTimeDuration.getSelectedItem().toString());
                    } else {
                        if (interviewKitViewHolder.spTimeDuration.getSelectedItem().toString().equalsIgnoreCase("Unlimited"))
                            eoInterViewQuestion.setThinktime("0");
                        else
                            eoInterViewQuestion.setThinktime(interviewKitViewHolder.spTimeDuration.getSelectedItem().toString());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            if (eoInterViewQuestion.getQtype().equalsIgnoreCase("1")) {
                if (!TextUtils.isEmpty(eoInterViewQuestion.getDuration())) {
                    int spinnerPosition = adapter3.getPosition(eoInterViewQuestion.getDuration());
                    interviewKitViewHolder.spTimeDuration.setSelection(spinnerPosition);
                }
            } else {
                if (!TextUtils.isEmpty(eoInterViewQuestion.getThinktime())) {
                    int spinnerPosition = adapter3.getPosition(eoInterViewQuestion.getThinktime());
                    interviewKitViewHolder.spTimeDuration.setSelection(spinnerPosition);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return interVewList.size();
        }

        class InterviewKitViewHolder extends RecyclerView.ViewHolder {

            private TextView tvtitlw, tvVideoView;
            private Spinner spRetakes, spThinkTime, spTimeDuration;
            private ImageView imgicon, crossImg;
            private ConstraintLayout reTakeLayout, thinkTimeLayout;
            private CheckBox checkBox;

            public InterviewKitViewHolder(@NonNull View itemView) {
                super(itemView);
                tvtitlw = itemView.findViewById(R.id.textView220);
                spRetakes = itemView.findViewById(R.id.spinner10);
                spThinkTime = itemView.findViewById(R.id.spinner11);
                spTimeDuration = itemView.findViewById(R.id.spinner9);
                imgicon = itemView.findViewById(R.id.imageView86);
                reTakeLayout = itemView.findViewById(R.id.constraintLayout50);
                thinkTimeLayout = itemView.findViewById(R.id.constraintLayout51);
                checkBox = itemView.findViewById(R.id.spinner12);
                crossImg = itemView.findViewById(R.id.imageView87);
                tvVideoView = itemView.findViewById(R.id.textView258);
            }
        }

    }
}
