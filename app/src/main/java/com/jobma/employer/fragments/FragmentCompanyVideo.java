package com.jobma.employer.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityVideoRecording;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.exoPlayer.PlayerActivity;
import com.jobma.employer.exoPlayer.Sample;
import com.jobma.employer.exoPlayer.UriSample;
import com.jobma.employer.model.companyProfile.EOCompanyVideo;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.upload.ProgressRequestBody;
import com.jobma.employer.util.MessageEvent;
import com.jobma.employer.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import net.alhazmy13.mediapicker.Video.VideoPicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentCompanyVideo extends Fragment implements View.OnClickListener, ProgressRequestBody.UploadCallbacks {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private View view;
    private Button btnRecordVideo, btnUploadVideo;
    private ImageView videoView, playVideo;
    private EOCompanyVideo companyVideo;
    private String VIDEO_URL;
    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        this.view = inflater.inflate(R.layout.fragment_company_video, container, false);

        this.initView();
        this.setOnClickListener();
        this.getCompanyVideo();
        return view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
        this.mProgressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);

        this.btnRecordVideo = view.findViewById(R.id.button2);
        this.btnUploadVideo = view.findViewById(R.id.button3);
        this.videoView = view.findViewById(R.id.videoView2);
        this.playVideo = view.findViewById(R.id.imageView30);
    }

    private void setOnClickListener() {
        this.btnUploadVideo.setOnClickListener(this);
        this.btnRecordVideo.setOnClickListener(this);
        this.playVideo.setOnClickListener(this);
    }

    private void getCompanyVideo() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.getCompanyVideo(apiKey).enqueue(new Callback<EOCompanyVideo>() {
                @Override
                public void onResponse(Call<EOCompanyVideo> call, Response<EOCompanyVideo> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        companyVideo = response.body();
                        if (!ObjectUtil.isEmpty(companyVideo)) {
                            if (companyVideo.getError() == RESPONSE_SUCCESS) {
                                loadImages(companyVideo.getPath().getPoster(), videoView);

                                if (companyVideo.getPath().getHls().equalsIgnoreCase("")) { //TODO HLS BALANK THEN FILE URL
                                    VIDEO_URL = companyVideo.getPath().getFileurl();  //TODO FILE URL
                                } else if (!companyVideo.getPath().getHls().equalsIgnoreCase("")) { //TODO  NOT BLANK THEN HLS ITSELF
                                    VIDEO_URL = companyVideo.getPath().getHls(); //TODO  HLS URL
                                } else if (companyVideo.getPath().getHls().equalsIgnoreCase("") && companyVideo.getPath().getFileurl().equalsIgnoreCase("")) {
                                    VIDEO_URL = "";
                                }

                            } else {
                                Toast.makeText(getActivity(), "" + companyVideo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOCompanyVideo> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get()
                .load(imagePath)
                .fit()
                .into(imageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                Intent intentRecordVideo = new Intent(getActivity(), ActivityVideoRecording.class);
                this.startActivity(intentRecordVideo);
                Animatoo.animateFade(getActivity());
                break;
            case R.id.button3:
                new VideoPicker.Builder(getActivity())
                        .mode(VideoPicker.Mode.GALLERY)
                        .directory(VideoPicker.Directory.DEFAULT)
                        .extension(VideoPicker.Extension.MP4)
                        .enableDebuggingMode(true)
                        .build();
                break;
            case R.id.imageView30:
                if (VIDEO_URL != null) {
                    if (VIDEO_URL.equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "No Video Found", Toast.LENGTH_LONG).show();
                    } else {
                        Sample sample = new UriSample("Super speed (PlayReady)", null, Uri.parse(VIDEO_URL), null, null, null);
                        startActivity(sample.buildIntent(getActivity(), false, PlayerActivity.ABR_ALGORITHM_DEFAULT));
                    }
                } else {
                    Toast.makeText(getActivity(), "Please upload the video first", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        loadingProgress();
        uploadVideoOnServer(event.getPath());
    }

    private void uploadVideoOnServer(String videoPath) {
        File file = new File(videoPath);
        ProgressRequestBody fileBody = new ProgressRequestBody(file, this);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            apiInterface.uploadVideo(apiKey, part).enqueue(new Callback<EOCompanyVideo>() {
                @Override
                public void onResponse(Call<EOCompanyVideo> call, Response<EOCompanyVideo> response) {
                    mProgressDialog.dismiss();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOCompanyVideo companyVideo = response.body();
                        if (!ObjectUtil.isEmpty(companyVideo)) {
                            if (companyVideo.getError() == RESPONSE_SUCCESS) {
                                //TODO from here load poster on videoview
                                loadImages(companyVideo.getPath().getPoster(), videoView);
                                VIDEO_URL = companyVideo.getPath().getUploaded_file_url();
                                Toast.makeText(getActivity(), "" + companyVideo.getMessage(), Toast.LENGTH_SHORT).show();
                                //TODO load get video api to refresh video in fragment
                                getCompanyVideo();
                            } else {
                                Toast.makeText(getActivity(), "" + companyVideo.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOCompanyVideo> call, Throwable t) {
                    if (t.getMessage() != null) {
                        mProgressDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> videoPaths = data.getStringArrayListExtra(VideoPicker.EXTRA_VIDEO_PATH);
            //TODO Your Code here
            loadingProgress();
            uploadVideoOnServer(videoPaths.get(0));
        }
    }

    private void loadingProgress() {
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

    @Override
    public void onProgressUpdate(int percentage) {
        mProgressDialog.setMessage("Uploading..." + percentage + "%");
        mProgressDialog.setProgress(percentage);
    }

    @Override
    public void onError() {
        progress.showProgressBar();
        progress.setProgressLabel("Uploaded Failed!");
        progress.hideProgressBar();
    }

    @Override
    public void onFinish() {
        progress.hideProgressBar();
    }

    @Override
    public void uploadStart() {
        //progress.setVisibility(View.VISIBLE);
        //mProgressDialog.setMessage("Uploading.. " + 0 + "%");
//        progress.setProgress(0);
        //progress.setProgressLabel("Uploading.. " + 0 + "%");
        //Toast.makeText(getActivity(), "Upload started", Toast.LENGTH_SHORT).show();
    }

}
