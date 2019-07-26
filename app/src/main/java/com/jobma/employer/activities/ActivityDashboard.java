package com.jobma.employer.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.fxn.pix.Pix;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.fragments.FragmentCompanyProfile;
import com.jobma.employer.fragments.FragmentDashboard;
import com.jobma.employer.fragments.FragmentEvaluateCandidates;
import com.jobma.employer.fragments.FragmentInterViewKit;
import com.jobma.employer.fragments.FragmentInvite;
import com.jobma.employer.fragments.FragmentJobListing;
import com.jobma.employer.fragments.FragmentReportIssue;
import com.jobma.employer.fragments.FragmentReportedIssuesList;
import com.jobma.employer.fragments.FragmentSetting;
import com.jobma.employer.fragments.FragmentSubAccount;
import com.jobma.employer.fragments.FragmentSubscriptions;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.companyProfile.EOCompanyProfilePic;
import com.jobma.employer.model.dashboard.EOWalletObject;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.MessageEvent;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.StringUtil;
import com.jobma.employer.util.UIUtil;
import com.squareup.picasso.Picasso;

import net.alhazmy13.mediapicker.Video.VideoPicker;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.jobma.employer.util.Constants.CREDIT_WALLET;
import static com.jobma.employer.util.Constants.EMPLOYEE_EMAIL;
import static com.jobma.employer.util.Constants.EMPLOYEE_NAME;
import static com.jobma.employer.util.Constants.EMPLOYEE_PHOTO;
import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.REQUEST_CODE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;
import static com.jobma.employer.util.Constants.SUB_USER;
import static com.jobma.employer.util.Constants.USER_TYPE;

public class ActivityDashboard extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 4;
    private NoNet noNet;
    boolean doubleBackToExitPressedOnce = false;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private Toolbar toolbar;
    private TextView tvtitle, btnContactUs, btnreportIssues, tvapplicants, tvsubAccount,
            tvsubscription, tvSetupInterview, tvJoblist, employerName, employerEmailId, logout, tvReportIssues, tvTrackReportedIssue,
            tvCreditLabel, tvCreditValue;
    private DrawerLayout drawer;
    private ImageView imgContactUs;
    private String pageView;
    private ConstraintLayout headerLayout, constraintLayout45;
    private CircleImageView circleImageView;
    private ImageView imgEditIcon, addSubAccountimg, uploadImageIcon;
    private boolean isShowContactMenu;
    private String employeeName, employeeEmail, employeePhoto, userType;
    private boolean ispermissions = false;
    private Dialog dialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        this.initView();
        this.setOnClickListener();
        this.dataToView();

        if (savedInstanceState == null) {
            addHomeFragmentsFromHere(new FragmentDashboard());
        }
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.employeeName = loginPreferences.getString(EMPLOYEE_NAME, "");
        this.employeeEmail = loginPreferences.getString(EMPLOYEE_EMAIL, "");
        this.employeePhoto = loginPreferences.getString(EMPLOYEE_PHOTO, "");
        this.userType = loginPreferences.getString(USER_TYPE, "");
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        toolbar = findViewById(R.id.toolbar);
        tvsubAccount = findViewById(R.id.textView14);
        tvapplicants = findViewById(R.id.textView13);
        btnreportIssues = findViewById(R.id.textView16);
        btnContactUs = findViewById(R.id.textView17);
        drawer = findViewById(R.id.drawer);
        headerLayout = findViewById(R.id.constraintLayout4);
        tvtitle = findViewById(R.id.tv_title);
        addSubAccountimg = findViewById(R.id.add_account);
        tvsubscription = findViewById(R.id.textView21);
        imgEditIcon = findViewById(R.id.edit_icon);
        tvSetupInterview = findViewById(R.id.textView12);
        tvJoblist = findViewById(R.id.textView10);
        imgContactUs = findViewById(R.id.imageView15);
        tvReportIssues = findViewById(R.id.textView165);
        tvTrackReportedIssue = findViewById(R.id.textView9);
        constraintLayout45 = findViewById(R.id.constraintLayout45);

        this.employerName = this.findViewById(R.id.textView6);
        this.employerEmailId = this.findViewById(R.id.textView7);
        this.circleImageView = this.findViewById(R.id.circleImageView);
        this.logout = this.findViewById(R.id.textView19);
        this.uploadImageIcon = this.findViewById(R.id.imageButton2);
        this.tvCreditLabel = this.findViewById(R.id.tvCreditLabel);
        this.tvCreditValue = this.findViewById(R.id.tvCreditValue);

        findViewById(R.id.textView8).setOnClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });

        findViewById(R.id.textView11).setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();
        } else {
            // code for lollipop and pre-lollipop devices
        }
    }

    @Override
    protected void onRestart() {
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        if (hasPermissions(this, PERMISSIONS)) {
            if (dialog != null) {
                dialog.dismiss();
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkAndRequestPermissions();
            } else {
                // code for lollipop and pre-lollipop devices
            }
        }
        super.onRestart();
    }

    private void setOnClickListener() {
        headerLayout.setOnClickListener(this);
        imgContactUs.setOnClickListener(this);
        btnContactUs.setOnClickListener(this);
        btnreportIssues.setOnClickListener(this);
        tvapplicants.setOnClickListener(this);
        tvsubAccount.setOnClickListener(this);
        addSubAccountimg.setOnClickListener(this);
        tvsubscription.setOnClickListener(this);
        tvSetupInterview.setOnClickListener(this);
        tvJoblist.setOnClickListener(this);
        this.logout.setOnClickListener(this);
        tvReportIssues.setOnClickListener(this);
        tvTrackReportedIssue.setOnClickListener(this);
        this.uploadImageIcon.setOnClickListener(this);
    }

    private void dataToView() {
        if (this.userType.equalsIgnoreCase(SUB_USER)) {
            this.tvsubAccount.setVisibility(View.GONE);
            this.tvsubscription.setVisibility(View.GONE);
            this.findViewById(R.id.imageView9).setVisibility(View.GONE);
            this.findViewById(R.id.imageView10).setVisibility(View.GONE);
        }

        if (!ObjectUtil.isEmpty(this.employeeName))
            this.employerName.setText(this.employeeName);
        if (!ObjectUtil.isEmpty(this.employeeEmail))
            this.employerEmailId.setText(this.employeeEmail);
        if (!ObjectUtil.isEmpty(this.employeePhoto))
            loadImages(this.employeePhoto, this.circleImageView);
    }

    private void getWalletAmount() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            //progress.showProgressBar();
            apiInterface.getWalletAmount(apiKey).enqueue(new Callback<EOWalletObject>() {
                @Override
                public void onResponse(Call<EOWalletObject> call, Response<EOWalletObject> response) {
                    //progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOWalletObject eoWalletObject = response.body();
                        if (!ObjectUtil.isEmpty(eoWalletObject)) {
                            if (eoWalletObject.getError() == RESPONSE_SUCCESS) {
                                if (!ObjectUtil.isEmpty(eoWalletObject.getData().getAmount())) {
                                    tvCreditValue.setText(eoWalletObject.getData().getAmount());
                                    //TODO save credit value globally in SP to check live interview
                                    loginPreferences.edit().putString(CREDIT_WALLET, eoWalletObject.getData().getAmount()).apply();
                                }
                            } else {
                                Toast.makeText(ActivityDashboard.this, "" + eoWalletObject.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOWalletObject> call, Throwable t) {
                    if (t.getMessage() != null) {
                        //progress.hideProgressBar();
                        Toast.makeText(ActivityDashboard.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
                        Toast.makeText(ActivityDashboard.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    if (drawer.isDrawerOpen(Gravity.END)) {
                        drawer.closeDrawer(Gravity.END);
                    }
                    Intent loginIntent = new Intent(ActivityDashboard.this, ActivityLogin.class);
                    ActivityDashboard.this.startActivity(loginIntent);
                    ActivityDashboard.this.finish();
                }
            }
        });
        dialog.show();
    }

    private void addHomeFragmentsFromHere(FragmentDashboard fragmentDashboard) {
        replaceFragment(fragmentDashboard, "dashboardmain");
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get()
                .load(imagePath)
                .error(R.drawable.ic_profile)
                .fit()
                .into(imageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView8:
                imgEditIcon.setVisibility(View.GONE);
                addSubAccountimg.setVisibility(View.GONE);
                tvCreditLabel.setVisibility(View.VISIBLE);
                tvCreditValue.setVisibility(View.VISIBLE);
                tvtitle.setText("Dashboard");
                replaceFragment(new FragmentDashboard(), "dashboardmain");
                drawer.closeDrawer(Gravity.START);
                break;
            case R.id.textView21:
                imgEditIcon.setVisibility(View.GONE);
                addSubAccountimg.setVisibility(View.GONE);
                tvCreditLabel.setVisibility(View.GONE);
                tvCreditValue.setVisibility(View.GONE);
                tvtitle.setText("Subscriptions");
                replaceFragment(new FragmentSubscriptions(), "subscriptions");
                drawer.closeDrawer(Gravity.START);
                break;
            case R.id.textView14:
                imgEditIcon.setVisibility(View.GONE);
                addSubAccountimg.setVisibility(View.VISIBLE);
                tvCreditLabel.setVisibility(View.GONE);
                tvCreditValue.setVisibility(View.GONE);
                pageView = "subAccount";
                tvtitle.setText("Sub Accounts");
                replaceFragment(new FragmentSubAccount(), "subAccount");
                addSubAccountimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ActivityDashboard.this, ActivityAddSubAccount.class));
                    }
                });
                drawer.closeDrawer(Gravity.START);
                break;
            case R.id.constraintLayout4:
                imgEditIcon.setVisibility(View.GONE);
                addSubAccountimg.setVisibility(View.GONE);
                tvCreditLabel.setVisibility(View.GONE);
                tvCreditValue.setVisibility(View.GONE);
                replaceFragment(new FragmentCompanyProfile(), "companyProfile");
                drawer.closeDrawer(Gravity.START);
                break;
            case R.id.textView16:
                imgEditIcon.setVisibility(View.GONE);
                addSubAccountimg.setVisibility(View.GONE);
                tvCreditLabel.setVisibility(View.GONE);
                tvCreditValue.setVisibility(View.GONE);
                replaceFragment(new FragmentSetting(), "setting");
                drawer.closeDrawer(Gravity.START);
                break;
            case R.id.textView9:
                imgEditIcon.setVisibility(View.GONE);
                addSubAccountimg.setVisibility(View.GONE);
                tvCreditLabel.setVisibility(View.GONE);
                tvCreditValue.setVisibility(View.GONE);
                FragmentReportedIssuesList fragmentReportedIssuesList = new FragmentReportedIssuesList();
                replaceFragment(fragmentReportedIssuesList, "reportedissues");
                tvtitle.setText("Track Reported Issues");
                drawer.closeDrawer(Gravity.START);
                break;
            case R.id.textView165:
                imgEditIcon.setVisibility(View.GONE);
                addSubAccountimg.setVisibility(View.GONE);
                tvCreditLabel.setVisibility(View.GONE);
                tvCreditValue.setVisibility(View.GONE);
                FragmentReportIssue fragmentReportIssue = new FragmentReportIssue();
                replaceFragment(fragmentReportIssue, "reportIssues");
                tvtitle.setText("Report an issue");
                drawer.closeDrawer(Gravity.START);
                break;
            case R.id.textView13:
                imgEditIcon.setVisibility(View.GONE);
                addSubAccountimg.setVisibility(View.GONE);
                tvCreditLabel.setVisibility(View.GONE);
                tvCreditValue.setVisibility(View.GONE);
                FragmentEvaluateCandidates fragmentApplicantReport = new FragmentEvaluateCandidates();
                replaceFragment(fragmentApplicantReport, "evaluationCandidates");
                tvtitle.setText("Applicants Reports");
                drawer.closeDrawer(Gravity.START);
                break;
            case R.id.textView12:
                imgEditIcon.setVisibility(View.GONE);
                addSubAccountimg.setVisibility(View.GONE);
                tvCreditLabel.setVisibility(View.GONE);
                tvCreditValue.setVisibility(View.GONE);
                FragmentInvite fragmentInvite = new FragmentInvite();
                Bundle bundle = new Bundle();
                bundle.putString("fromDashboard", "dashboard");
                fragmentInvite.setArguments(bundle);
                replaceFragment(fragmentInvite, "evaluationCandidates");
                tvtitle.setText("Invite");
                drawer.closeDrawer(Gravity.START);
                break;

            case R.id.textView17:
            case R.id.imageView15:
                if (isShowContactMenu) {
                    isShowContactMenu = false;
                    constraintLayout45.setVisibility(View.GONE);
                    imgContactUs.setRotation(0f);
                } else {
                    isShowContactMenu = true;
                    constraintLayout45.setVisibility(View.VISIBLE);
                    imgContactUs.setRotation(90f);
                }
                break;
            case R.id.textView10:
                imgEditIcon.setVisibility(View.GONE);
                addSubAccountimg.setVisibility(View.GONE);
                tvCreditLabel.setVisibility(View.GONE);
                tvCreditValue.setVisibility(View.GONE);
                FragmentJobListing fragmentJobListing = new FragmentJobListing();
                replaceFragment(fragmentJobListing, "jobListing");
                tvtitle.setText("Job Listing");
                drawer.closeDrawer(Gravity.START);
                break;
            case R.id.textView11:
                imgEditIcon.setVisibility(View.GONE);
                tvCreditLabel.setVisibility(View.GONE);
                tvCreditValue.setVisibility(View.GONE);
                pageView = "interviewKit";
                addSubAccountimg.setVisibility(View.VISIBLE);
                FragmentInterViewKit fragmentInterViewKit = new FragmentInterViewKit();
                replaceFragment(fragmentInterViewKit, "interviewkit");
                tvtitle.setText("Interview Kit");
                drawer.closeDrawer(Gravity.START);
                break;
            case R.id.textView19:
                this.logoutFromApplication();
                break;
            case R.id.imageButton2:
                Pix.start(this, REQUEST_CODE, 1);
                break;
            case R.id.add_account:
                if (pageView.equalsIgnoreCase("interviewKit"))
                    startActivity(new Intent(ActivityDashboard.this, ActivityInterviewKit.class));
                else
                    //todo     for sub Account
                    // startActivity(new Intent(ActivityDashboard.this,ActivityInterviewKit.class));
                    break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!ObjectUtil.isEmpty(data) && resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            ArrayList<String> resultArray = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            File file = new File(resultArray.get(0));
            Bitmap bitmap = new BitmapDrawable(getApplication().getResources(), file.getAbsolutePath()).getBitmap();
            uploadImageOnServer(persistImage(bitmap));
        }

        if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> videoPaths = data.getStringArrayListExtra(VideoPicker.EXTRA_VIDEO_PATH);
            EventBus.getDefault().post(new MessageEvent(videoPaths.get(0)));
        }
    }

    private void uploadImageOnServer(File path) {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), path);
        MultipartBody.Part body = MultipartBody.Part.createFormData("imagefile", path.getName(), reqFile);

        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            this.apiInterface.uploadProfileImage(apiKey, body).enqueue(new Callback<EOCompanyProfilePic>() {
                @Override
                public void onResponse(Call<EOCompanyProfilePic> call, Response<EOCompanyProfilePic> response) {
                    progress.hideProgressBar();

                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOCompanyProfilePic eoProfileImage = response.body();
                        if (!ObjectUtil.isEmpty(eoProfileImage)) {
                            if (eoProfileImage.getError().equals(String.valueOf(RESPONSE_SUCCESS))) {
                                Toast.makeText(ActivityDashboard.this, "" + eoProfileImage.getMessage(), Toast.LENGTH_SHORT).show();
                                if (!ObjectUtil.isEmpty(eoProfileImage.getPath())) {
                                    loadImages(eoProfileImage.getPath(), circleImageView);
                                    loginPreferences.edit().putString(EMPLOYEE_PHOTO, eoProfileImage.getPath()).apply();
                                }
                            } else {
                                Toast.makeText(ActivityDashboard.this, "" + eoProfileImage.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOCompanyProfilePic> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityDashboard.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private File persistImage(Bitmap bitmap) {
        File filesDir = getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, StringUtil.getStringForID(R.string.app_name) + ".jpg");
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }

    private void logoutFromApplication() {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_log_out);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.bg_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        Button btnOk = dialog.findViewById(R.id.button26);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO when user is logout out then clear the login shared preferences
                if (loginPreferences.contains(SELECTED_API_KEY)) {
                    loginPreferences.edit().clear().apply();
                    if (drawer.isDrawerOpen(Gravity.END)) {
                        drawer.closeDrawer(Gravity.END);
                    }
                    Intent loginIntent = new Intent(ActivityDashboard.this, ActivityLogin.class);
                    ActivityDashboard.this.startActivity(loginIntent);
                    ActivityDashboard.this.finish();
                }
            }
        });

        Button btnCancel = dialog.findViewById(R.id.button25);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerlayout, fragment, tag);
        fragmentTransaction.commit();
    }

    public void checkFragmentVisibility(String title) {
        if (title.equalsIgnoreCase("companyprofile")) {
            tvtitle.setText("Company Profile");
            imgEditIcon.setVisibility(View.VISIBLE);

            imgEditIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userType.equals(SUB_USER))
                        Toast.makeText(ActivityDashboard.this, "You Don't have permission to Edit the Company Profile", Toast.LENGTH_SHORT).show();
                    else
                        startActivity(new Intent(getApplicationContext(), ActivityEditCompanyProfile.class));
                }
            });
        }

        if (title.equalsIgnoreCase("Dashboard")) {
            tvtitle.setText("Dashboard");
            imgEditIcon.setVisibility(View.GONE);
        }
        if (title.equalsIgnoreCase("setting")) {
            tvtitle.setText("Settings");
        }
    }

    private boolean checkfragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("dashboardmain");
        if (fragment != null && fragment.isVisible()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        this.noNet.RegisterNoNet();
        super.onResume();

        this.getWalletAmount();
        this.getWalletExpired();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        if (checkfragment()) {
            this.doubleBackToExitPressedOnce = true;
            Snackbar snackbar1 = Snackbar.make(findViewById(R.id.containerlayout), "Please click BACK again to exit!", Snackbar.LENGTH_SHORT);
            snackbar1.show();

//            Toast toast = Toast.makeText(ActivityDashboard.this, "Please click BACK again to exit", Toast.LENGTH_SHORT);
//            View view = toast.getView();
//
////Gets the actual oval background of the Toast then sets the colour filter
//            view.getBackground().setColorFilter(getResources().getColor(R.color.selected_color), PorterDuff.Mode.SRC_IN);
//
////Gets the TextView from the Toast so it can be editted
//            TextView text = view.findViewById(android.R.id.message);
//            text.setTextColor(getResources().getColor(R.color.colorWhite));
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            replaceFragment(new FragmentDashboard(), "dashboardmain");
        }
    }

    @Override
    protected void onPause() {
        this.noNet.unRegisterNoNet();
        super.onPause();
    }

    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int wtite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int recordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (recordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with all permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for all permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                            showDialogOK("Camera and Storage Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", ActivityDashboard.this.getPackageName(), null);
                                                    intent.setData(uri);
                                                    ActivityDashboard.this.startActivity(intent);
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            dialog = new Dialog(ActivityDashboard.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.dialog_permission);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            Button dialogBtn_cancel = (Button) dialog.findViewById(R.id.button23);
                            dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                    System.exit(0);
                                }
                            });
                            Button dialogBtn_okay = (Button) dialog.findViewById(R.id.button22);
                            dialogBtn_okay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", ActivityDashboard.this.getPackageName(), null);
                                    intent.setData(uri);
                                    ActivityDashboard.this.startActivity(intent);
                                }
                            });
                            dialog.show();
                        }
                    }
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


}
