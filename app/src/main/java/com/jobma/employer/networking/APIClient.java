package com.jobma.employer.networking;

import com.google.gson.Gson;
import com.jobma.employer.model.account.EOChangePassword;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.account.EOLoginResponse;
import com.jobma.employer.model.applicantReports.EOApplicantReportObject;
import com.jobma.employer.model.applicants.EOAccount;
import com.jobma.employer.model.applicants.EOEvaluateCandidates;
import com.jobma.employer.model.candidateTrack.EOInvitedCandidates;
import com.jobma.employer.model.candidate_rating.EOGetRatingRow;
import com.jobma.employer.model.chat_history.EOChatHistory;
import com.jobma.employer.model.chat_history.EOSendMessage;
import com.jobma.employer.model.companyProfile.EOCompanyProfile;
import com.jobma.employer.model.companyProfile.EOCompanyProfilePic;
import com.jobma.employer.model.companyProfile.EOCompanyVideo;
import com.jobma.employer.model.companyProfile.EOCountry;
import com.jobma.employer.model.dashboard.EOInterviewCounts;
import com.jobma.employer.model.dashboard.EOOverviewList;
import com.jobma.employer.model.dashboard.EOWalletObject;
import com.jobma.employer.model.feedback.ChangeProfileStatus;
import com.jobma.employer.model.feedback.GetFeedBack;
import com.jobma.employer.model.get_candidate_detail.GetCandidateDetail;
import com.jobma.employer.model.get_interview.EOGetInterviewRequest;
import com.jobma.employer.model.get_interview_question_ans.GetInterviewQuestion;
import com.jobma.employer.model.get_summary_detail.GetSummaryDetail;
import com.jobma.employer.model.interviewKit.EOCreateInterviewKit;
import com.jobma.employer.model.interviewKit.EOInterviewKitList;
import com.jobma.employer.model.interview_log.EOInvitationLog;
import com.jobma.employer.model.jobList.EOJobDescription;
import com.jobma.employer.model.jobList.EOMessageObject;
import com.jobma.employer.model.liveInterview.EOLiveInterview;
import com.jobma.employer.model.mcq_question_request.EOMCQuestionRequest;
import com.jobma.employer.model.profile.EOForwardObject;
import com.jobma.employer.model.profile.EOProfileObject;
import com.jobma.employer.model.recennt_applicants.RecentApplicantsRequest;
import com.jobma.employer.model.reportIssue.SubAccountRequest;
import com.jobma.employer.model.setupInterview.EOCreateJob;
import com.jobma.employer.model.setupInterview.EOInterviewJobList;
import com.jobma.employer.model.setupInterview.EOTimeZoneList;
import com.jobma.employer.model.subAccounts.EOSubAccounts;
import com.jobma.employer.model.subscriptions.EOSubscription;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

import static com.jobma.employer.util.Constants.BASE_URL;

public class APIClient {

    public static APIInterface getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES)
                .sslSocketFactory(getSSLSocketFactory())
                .addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).client(client)
                //.addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        return retrofit.create(APIInterface.class);
    }

    private static SSLSocketFactory getSSLSocketFactory() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return sslSocketFactory;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    public interface APIInterface {

        @FormUrlEncoded
        @POST("employer-login")
        Call<EOLoginResponse> employeeLogin(@Field("email") String email,
                                            @Field("password") String password);

        @FormUrlEncoded
        @POST("forgot-password")
        Call<EOForgetPassword> forgetPassword(@Field("email") String email);

        @FormUrlEncoded
        @POST("resent-application")
        Call<RecentApplicantsRequest> recentApplicants(@Header("key") String key,
                                                       @Field("offset") String offset,
                                                       @Field("limit") String limit);

        @FormUrlEncoded
        @POST("chp")
        Call<EOChangePassword> changePassword(@Header("key") String key,
                                              @Field("current_password") String currentPassword,
                                              @Field("new_password") String newPassword);

        @GET("gvid")
        Call<EOCompanyVideo> getCompanyVideo(@Header("key") String key);

        @Multipart
        @POST("uploadvid")
        Call<EOCompanyVideo> uploadVideo(@Header("key") String key,
                                         @Part MultipartBody.Part file);

        @GET("company-info")
        Call<EOCompanyProfile> getCompanyInfo(@Header("key") String key);

        @GET("countries")
        Call<EOCountry> getCountryApi();

        @GET()
        Call<EOCountry> getStateApi(@Url String url);

        @GET()
        Call<EOCountry> getCityApi(@Url String url);

        @GET("get-industry")
        Call<EOCountry> getIndustrialArea();

        @GET("get-functionalarea")
        Call<EOCountry> getFunctionalArea();

        @FormUrlEncoded
        @POST("update-info")
        Call<EOForgetPassword> updateCompanyInfo(@Header("key") String key,
                                                 @Field("company") String companyName,
                                                 @Field("website") String website,
                                                 @Field("company_email") String companyEmail,
                                                 @Field("organisation") String organization,
                                                 @Field("address") String address,
                                                 @Field("address2") String address2,
                                                 @Field("country") String country,
                                                 @Field("state") String state,
                                                 @Field("city") String city,
                                                 @Field("zip") String zip,
                                                 @Field("phone") String phone,
                                                 @Field("fax") String fax,
                                                 @Field("ext") String exNumber,
                                                 @Field("about_company") String about_company,
                                                 @Field("industry") String industry,
                                                 @Field("functional_area") String functional_area);

        @Multipart
        @POST("uploadcatpic")
        Call<EOCompanyProfilePic> uploadProfileImage(@Header("key") String key,
                                                     @Part MultipartBody.Part file);

        @FormUrlEncoded
        @POST("save-issue")
        Call<SubAccountRequest> reportIssues(@Header("key") String key,
                                             @Field("email") String email,
                                             @Field("subject") String subject,
                                             @Field("message") String message);

        @FormUrlEncoded
        @POST("list-issue")
        Call<SubAccountRequest> trackReportIssues(@Header("key") String key,
                                                  @Field("offset") String offset,
                                                  @Field("limit") String limit);

        @GET("subscription-detail")
        Call<EOSubscription> subscription(@Header("key") String key);

        @FormUrlEncoded
        @POST("sub-users")
        Call<EOSubAccounts> subAccountsUsers(@Header("key") String key,
                                             @Field("offset") int offset,
                                             @Field("limit") String limit);


        @FormUrlEncoded
        @POST("sub-users")
        Call<EOSubAccounts> subAccountList(@Header("key") String key,
                                           @FieldMap Map<String, String> stringMap);


        @FormUrlEncoded
        @POST("filter-job")
        Call<EOEvaluateCandidates> evaluateCandidate(@Header("key") String key,
                                                     @FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("active-deactive")
        Call<EOForgetPassword> activateDeactivateAccounts(@Header("key") String key,
                                                          @Field("sub_user_id") String sub_user_id,
                                                          @Field("action") String action);

        @FormUrlEncoded
        @POST("approval")
        Call<EOForgetPassword> approvalSubAccounts(@Header("key") String key,
                                                   @Field("sub_user_id") String sub_user_id,
                                                   @Field("action") String action);


        @GET("employer-list")
        Call<EOAccount> getAccountList(@Header("key") String key);

        @FormUrlEncoded
        @POST("get-permission")
        Call<EOForgetPassword> getPermissionSubAccounts(@Header("key") String key,
                                                        @Field("sub_user_id") String sub_user_id);

        @FormUrlEncoded
        @POST("subuser-permission")
        Call<EOForgetPassword> updatePermissionSubAccounts(@Header("key") String key,
                                                           @Field("sub_user_id") String sub_user_id,
                                                           @Field("permission") String permission);

        @FormUrlEncoded
        @POST
        Call<EOInvitedCandidates> invitedCandidate(@Url String url,
                                                   @Header("key") String key,
                                                   @Field("offset") String offset,
                                                   @Field("limit") String limit,
                                                   @Field("job_id") String jobid);

        @FormUrlEncoded
        @POST("add-sub-user")
        Call<EOForgetPassword> addSubAccountApi(@Header("key") String key,
                                                @Field("full_name") String fullName,
                                                @Field("email") String email,
                                                @Field("password") String password,
                                                @Field("approval") String approval,
                                                @Field("permission") String permission);

        @FormUrlEncoded
        @POST
        Call<EOInvitedCandidates> trackCandidates(@Url String url,
                                                  @Header("key") String key,
                                                  @Field("offset") String offset,
                                                  @Field("limit") String limit,
                                                  @Field("job_id") String jobid,
                                                  @Field("status") String status);

        @FormUrlEncoded
        @POST("invitation-log")
        Call<EOInvitationLog> invitationLog(@Header("key") String key,
                                            @Field("job_id") String jobid);

        @GET("interview-job-list")
        Call<EOInterviewJobList> getInterviewJobList(@Header("key") String key);


        @FormUrlEncoded
        @POST("create-job")
        Call<EOCreateJob> createJob(@Header("key") String key,
                                    @Field("title") String title,
                                    @Field("expirydate") String expirydate,
                                    @Field("description") String description,
                                    @Field("public") String publicKey);

        @FormUrlEncoded
        @POST("chat-history")
        Call<EOChatHistory> chatHistory(@Header("key") String key,
                                        @Field("contact_id") String id,
                                        @Field("offset") String ofset,
                                        @Field("limit") String limit);


        @FormUrlEncoded
        @POST("send-chat")
        Call<EOSendMessage> sendChatMessage(@Header("key") String key,
                                            @Field("jobma_contact_id") String id,
                                            @Field("message") String message);


        @FormUrlEncoded
        @POST("close-chat")
        Call<EOForgetPassword> closeChat(@Header("key") String key,
                                         @Field("jobma_contact_id") String chatId,
                                         @Field("message") String closeMsgReason);

        @GET("time-zone-list")
        Call<EOTimeZoneList> getTimeZone();

        @GET("interview-job-kit")
        Call<com.jobma.employer.model.setupInterview.EOInterviewKitList> getInterviewKit(@Header("key") String key);

        @FormUrlEncoded
        @POST("schedule-interview")
        Call<EOForgetPassword> invitationForPreRecorded(@Header("key") String key,
                                                        @Field("name") String fullName,
                                                        @Field("email") String email,
                                                        @Field("phone") String phone,
                                                        @Field("mode") String mode,
                                                        @Field("job_id") String job_id,
                                                        @Field("kit_id") String kit_id,
                                                        @Field("confirmation") String confirmation,
                                                        @Field("message") String message,
                                                        @Field("expiry_date") String expiry_date);

        @FormUrlEncoded
        @POST("schedule-interview")
        Call<EOForgetPassword> invitationForLiveInterview(@Header("key") String key,
                                                          @Field("name") String fullName,
                                                          @Field("email") String email,
                                                          @Field("phone") String phone,
                                                          @Field("mode") String mode,
                                                          @Field("job_id") String job_id,
                                                          @Field("message") String message,
                                                          @Field("start_time") String start_time,
                                                          @Field("end_time") String end_time,
                                                          @Field("timezone") String timezone,
                                                          @Field("invite_date") String invite_date);


        @FormUrlEncoded
        @POST("job-approve")
        Call<EOMessageObject> disapprovedJob(@Header("key") String key,
                                             @Field("job_id") int job_id,
                                             @Field("approval") String disapproval,
                                             @Field("disapprove_reason") String disapproveReason);

        @FormUrlEncoded
        @POST("job-approve")
        Call<EOMessageObject> approvedJob(@Header("key") String key,
                                          @Field("job_id") int job_id,
                                          @Field("approval") String approval);


        @FormUrlEncoded
        @POST("job-status")
        Call<EOMessageObject> deactivateJob(@Header("key") String key,
                                            @Field("job_id") int job_id,
                                            @Field("job_status") String job_status,
                                            @Field("deactive_reason") String deactive_reason);

        @FormUrlEncoded
        @POST("job-status")
        Call<EOMessageObject> activateExpiredJob(@Header("key") String key,
                                                 @Field("job_id") int job_id,
                                                 @Field("expiry_date") String expiry_date);

        @FormUrlEncoded
        @POST("job-status")
        Call<EOMessageObject> activateJob(@Header("key") String key,
                                          @Field("job_id") int job_id,
                                          @Field("job_status") String job_status);

        @FormUrlEncoded
        @POST("renew-job")
        Call<EOMessageObject> renewJob(@Header("key") String key,
                                       @Field("job_id") int job_id);

        @FormUrlEncoded
        @POST("jobdescription")
        Call<EOJobDescription> jobListJobDescription(@Header("key") String key,
                                                     @Field("job_id") int job_id);

        @FormUrlEncoded
        @POST("kit-list")
        Call<EOInterviewKitList> interviewKit(@Header("key") String key,
                                              @Field("offset") int offset,
                                              @Field("limit") String limit);

        @GET("interview-count")
        Call<EOInterviewCounts> getInterviewCounts(@Header("key") String key);


        @FormUrlEncoded
        @POST("question-kit")
        Call<EOGetInterviewRequest> inerview(@Header("key") String key,
                                             @Field("kit_id") String kitId);

        @FormUrlEncoded
        @POST("interview-date")
        Call<EOOverviewList> scheduleInterviewsData(@Header("key") String key,
                                                    @Field("date") String date);

        @POST("add-question")
        Call<EOMCQuestionRequest> addInterviewQuestions(@Header("key") String key,
                                                        @Body RequestBody params);

        @Multipart
        @POST("add-question")
        Call<EOMCQuestionRequest> addVideoquestion(@Header("key") String key,
                                                   @Part("qtype") int type,
                                                   @Part("qcontent") RequestBody qcontent,
                                                   @Part MultipartBody.Part file);


        @FormUrlEncoded
        @POST("interview-status-detail")
        Call<EOApplicantReportObject> getApplicantReports(@Header("key") String key,
                                                          @FieldMap Map<String, String> params);


        @FormUrlEncoded
        @POST("{kit}")
        Call<EOCreateInterviewKit> addKitRequest(@Path("kit") String path, @Header("key") String key, @FieldMap HashMap<String, String> data);


        @GET("feedback-param")
        Call<EOGetRatingRow> getRatings(@Header("key") String key);

        @FormUrlEncoded
        @POST("pitcher")
        Call<EOProfileObject> getPitcherProfile(@Header("key") String key,
                                                @Field("pitcher_id") int pitcher_id);

        @FormUrlEncoded
        @POST("candidate-detail")
        Call<GetCandidateDetail> getCandidateDetail(@Header("key") String key,
                                                    @Field("applied_id") String appliedId);

        @FormUrlEncoded
        @POST("interview-question")
        Call<GetInterviewQuestion> getCandidateAllQuestions(@Header("key") String key,
                                                            @Field("job_id") String appliedId,
                                                            @Field("pitcher_id") String pitcherId);

        @FormUrlEncoded
        @POST("forward")
        Call<EOForwardObject> forwardProfile(@Header("key") String key,
                                             @FieldMap Map<String, String> params);

        @FormUrlEncoded
        @POST("add-rating")
        Call<EOForgetPassword> addRatingToCandidates(@Header("key") String key,
                                                     @Field("applied_id") int applied_id,
                                                     @Field("comment") String comment,
                                                     @Field("recommend") String recommend,
                                                     @Field("value") String value);

        @FormUrlEncoded
        @POST("rating-summary")
        Call<GetSummaryDetail> getSummary(@Header("key") String key,
                                          @Field("applied_id") String appliedId);


        @FormUrlEncoded
        @POST("get-rating")
        Call<GetFeedBack> getFeedBack(@Header("key") String key,
                                      @Field("applied_id") String appliedId);


        @FormUrlEncoded
        @POST("change-status")
        Call<ChangeProfileStatus> changeProfileStatus(@Header("key") String key,
                                                      @Field("applied_id") String appliedId,
                                                      @Field("current_status") String currentStatus,
                                                      @Field("contact_text") String contactText);

        @GET("wallet-amount")
        Call<EOWalletObject> getWalletAmount(@Header("key") String key);

        @GET("wallet-credit")
        Call<EOForgetPassword> getCreditWallet(@Header("key") String key);

        @GET("check-expiry")
        Call<EOForgetPassword> getWalletExpiry(@Header("key") String key);

        @FormUrlEncoded
        @POST("live-interview-check")
        Call<EOLiveInterview> liveInterviewCheck(@Header("key") String key,
                                                 @Field("invite_id") int inviteId,
                                                 @Field("token") String token);


    }

}
