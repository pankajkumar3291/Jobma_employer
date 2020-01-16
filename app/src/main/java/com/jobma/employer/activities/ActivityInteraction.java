package com.jobma.employer.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.chat_history.Chatdatum;
import com.jobma.employer.model.chat_history.EOChatHistory;
import com.jobma.employer.model.chat_history.EOSendMessage;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class ActivityInteraction extends AppCompatActivity {

    private APIClient.APIInterface apiInterfac;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private RecyclerView recChat;
    private EditText etMessage;
    private List<Chatdatum> chatList = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private APIClient.APIInterface apiInterface = APIClient.getClient();
    private int offset = 1;
    private Chatdatum chatdatum;
    private boolean isfirst = true;
    private FloatingActionButton downarrow;
    private GlobalProgressDialog globalProgressDialog;
    private LinearLayoutManager layoutManager;
    private int remainingCount, visibleItems = 1;
    private TextView spantext;
    private CheckBox checkBox;
    private ConstraintLayout etchatLayout;
    private ConstraintLayout textLayout;
    private Button btnResolved;
    private int chatId;
    private boolean fromessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interaction);

        if (!ObjectUtil.isEmpty(this.getIntent().getIntExtra("contactId", 0))) {
            this.chatId = this.getIntent().getIntExtra("contactId", 0);
        }

        this.globalProgressDialog = new GlobalProgressDialog(this);
        this.apiInterfac = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        chatAdapter = new ChatAdapter(chatList, this);
        layoutManager = new LinearLayoutManager(this);
        globalProgressDialog.showProgressBar();

        this.initView();
        this.getAllMessage();
        this.recyclerViewSetup();
    }

    private void recyclerViewSetup() {
        recChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) //check for scroll down
                {
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == chatList.size() - 1) {
                        visibleItems += 10;
                        globalProgressDialog.showProgressBar();
                        getAllMessage();
                    }
                }
            }
        });
    }

    private void getAllMessage() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            try {
                if (remainingCount > 0 || isfirst || fromessage) {
                    fromessage = false;
                    isfirst = false;
                    apiInterface.chatHistory(apiKey, String.valueOf(this.chatId), String.valueOf(visibleItems), "10").enqueue(new Callback<EOChatHistory>() {
                        @Override
                        public void onResponse(Call<EOChatHistory> call, Response<EOChatHistory> response) {
                            globalProgressDialog.hideProgressBar();
                            if (response.body() != null) {
                                if (response.body().getError() == 0) {
                                    if (response.body().getData().getTitle().getIssueStatus().equalsIgnoreCase("Complete")) {
                                        etchatLayout.setVisibility(View.GONE);
                                        btnResolved.setVisibility(View.VISIBLE);
                                        spantext.setVisibility(View.GONE);
                                    }
                                    if (response.body().getData().getChatdata() != null) {
                                        if (response.body().getData().getChatdata().size() > 0) {

                                            remainingCount = response.body().getData().getRemaining();
                                            chatList.addAll(response.body().getData().getChatdata());
                                            recChat.setAdapter(chatAdapter);
                                            chatAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<EOChatHistory> call, Throwable t) {
                            System.out.println("" + t.getMessage());
                            globalProgressDialog.hideProgressBar();
                        }
                    });
                } else {
                    globalProgressDialog.hideProgressBar();
                }
            } catch (Exception e) {
                e.printStackTrace();
                globalProgressDialog.hideProgressBar();
            }
        }
    }

    private void initView() {
        findViewById(R.id.backbtnimg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnResolved = findViewById(R.id.button24);
        recChat = findViewById(R.id.recchat);
        spantext = findViewById(R.id.textView211);
        checkBox = findViewById(R.id.checkBox3);
        textLayout = findViewById(R.id.readText);
        etchatLayout = findViewById(R.id.constraintLayout41);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {

                } else {

                }
            }
        });
        SpannableString spannable = new SpannableString("Your issue status is pending. Our support team will update it soon. Close issue");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
            }

            @Override
            public void onClick(View widget) {
                closeChatDialog();

//                if (!ObjectUtil.isEmpty(apiKey)) {
//                    globalProgressDialog.showProgressBar();
//                    apiInterface.closeChat(apiKey, String.valueOf(chatId)).enqueue(new Callback<EOForgetPassword>() {
//                        @Override
//                        public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
//                            globalProgressDialog.hideProgressBar();
//                            if (!ObjectUtil.isEmpty(response.body())) {
//                                EOForgetPassword closeChatResponse = response.body();
//                                if (!ObjectUtil.isEmpty(closeChatResponse)) {
//                                    if (closeChatResponse.getError() == RESPONSE_SUCCESS) {
//                                        Toast.makeText(ActivityInteraction.this, "" + closeChatResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                                        ActivityInteraction.this.finish();
//                                    } else {
//                                        Toast.makeText(ActivityInteraction.this, "" + closeChatResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<EOForgetPassword> call, Throwable t) {
//                            if (t.getMessage() != null) {
//                                globalProgressDialog.hideProgressBar();
//                                Toast.makeText(ActivityInteraction.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }


            }
        };

        spannable.setSpan(clickableSpan, 68, 79, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spantext.setText(spannable);
        spantext.setMovementMethod(LinkMovementMethod.getInstance());
        spantext.setHighlightColor(ContextCompat.getColor(this, R.color.green));
        downarrow = findViewById(R.id.floatingActionButton4);
        recChat.setHasFixedSize(true);
        recChat.setLayoutManager(layoutManager);
        etMessage = findViewById(R.id.editText19);

        findViewById(R.id.imageView73).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(etMessage))) {
                    Toast.makeText(ActivityInteraction.this, "Please enter message", Toast.LENGTH_SHORT).show();
                } else {
                    sendMessage(etMessage.getText().toString());
                }
            }
        });
    }

    private void closeChatDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_close_chat);

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
                    closeChatApi(etReason.getText().toString().trim());
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

    private void closeChatApi(String closeChatReason) {
        if (!ObjectUtil.isEmpty(apiKey)) {
            globalProgressDialog.showProgressBar();
            apiInterface.closeChat(apiKey, String.valueOf(chatId), closeChatReason).enqueue(new Callback<EOForgetPassword>() {
                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    globalProgressDialog.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword closeChatResponse = response.body();
                        if (!ObjectUtil.isEmpty(closeChatResponse)) {
                            if (closeChatResponse.getError() == RESPONSE_SUCCESS) {
                                Toast.makeText(ActivityInteraction.this, "" + closeChatResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                ActivityInteraction.this.finish();
                            } else {
                                Toast.makeText(ActivityInteraction.this, "" + closeChatResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        globalProgressDialog.hideProgressBar();
                        Toast.makeText(ActivityInteraction.this, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendMessage(String message) {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            globalProgressDialog.showProgressBar();
            try {
                apiInterface.sendChatMessage(this.apiKey, String.valueOf(this.chatId), message).enqueue(new Callback<EOSendMessage>() {
                    @Override
                    public void onResponse(Call<EOSendMessage> call, Response<EOSendMessage> response) {

                        if (response.body() != null) {
                            if (response.body().getError() == 0) {
                                globalProgressDialog.hideProgressBar();
                                chatList.clear();
                                etMessage.setText("");
                                fromessage = true;
                                getAllMessage();
                            } else {
                                chatAdapter.notifyDataSetChanged();
                                globalProgressDialog.hideProgressBar();
                            }
                        } else {
                            globalProgressDialog.hideProgressBar();
                        }
                    }

                    @Override
                    public void onFailure(Call<EOSendMessage> call, Throwable t) {
                        globalProgressDialog.hideProgressBar();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                globalProgressDialog.hideProgressBar();
            }
        }
    }


    //TODO  ====================================== Adapter Class ======================================
    public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

        private List<Chatdatum> chatList;
        private Context context;

        private ChatAdapter(List<Chatdatum> chatList, Context context) {
            this.chatList = chatList;
            this.context = context;
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_layout, viewGroup, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {
            Chatdatum chatdatum = chatList.get(i);

            if (chatdatum.getInteractionBy() == 0) {
                chatViewHolder.sendLayout.setVisibility(View.VISIBLE);
                chatViewHolder.reciveLayout.setVisibility(View.GONE);
                chatViewHolder.tvSendMessage.setText(UIUtil.fromHtml(chatdatum.getMessage()));
                chatViewHolder.tvSendTime.setText(UIUtil.fromHtml(chatdatum.getUpdatedAt()));
                if (chatdatum.isProgress()) {
                    chatViewHolder.progressBar.setVisibility(View.VISIBLE);
                } else {
                    chatViewHolder.progressBar.setVisibility(View.GONE);
                }
                if (chatdatum.isIsfailed()) {
                    chatViewHolder.warning.setVisibility(View.VISIBLE);
                } else {
                    chatViewHolder.warning.setVisibility(View.GONE);
                }
            } else {
                chatViewHolder.reciveLayout.setVisibility(View.VISIBLE);
                chatViewHolder.sendLayout.setVisibility(View.GONE);
                chatViewHolder.tvRecieveMessage.setText(UIUtil.fromHtml(chatdatum.getMessage()));
                chatViewHolder.tvRecieveTime.setText(UIUtil.fromHtml(chatdatum.getUpdatedAt()));
            }
        }

        @Override
        public int getItemCount() {
            return ObjectUtil.isEmpty(chatList) ? 0 : chatList.size();
        }

        class ChatViewHolder extends RecyclerView.ViewHolder {

            private TextView tvSendMessage, tvSendTime, tvRecieveMessage, tvRecieveTime;
            private ConstraintLayout sendLayout, reciveLayout;
            private ProgressBar progressBar;
            private ImageView warning;

            private ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSendMessage = itemView.findViewById(R.id.textView209);
                tvSendTime = itemView.findViewById(R.id.textView210);
                tvRecieveMessage = itemView.findViewById(R.id.textView2091);
                tvRecieveTime = itemView.findViewById(R.id.textView2101);
                sendLayout = itemView.findViewById(R.id.sendlayout);
                reciveLayout = itemView.findViewById(R.id.constraintLayout44);
                progressBar = itemView.findViewById(R.id.progressBar5);
                warning = itemView.findViewById(R.id.imageView80);
            }
        }

    }
}
