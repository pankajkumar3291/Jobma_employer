package com.jobma.employer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityInterviewKit;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.model.mcq_question_request.EOMCQuestionRequest;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.UIUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class DialogMultipleChoice extends Dialog {

    private TextView tvMore;
    private Context context;
    private MultiChoiceAdatper multiChoiceAdatper;
    private List<MultichoiceModel> choiceFieldList = new ArrayList<>();
    private RecyclerView recMultiChoise;
    private Button cancelbtn, btnSubmit;
    private EditText etTitle;
    private GlobalProgressDialog progress;
    private boolean ansIsChecked = false;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    public DialogMultipleChoice(Context context) {
        super(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progress = new GlobalProgressDialog(context);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        setContentView(R.layout.dialog_choice);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(this.findViewById(R.id.mainLayout), R.color.colorWhite, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        recMultiChoise = findViewById(R.id.recyclerView3);
        etTitle = findViewById(R.id.ques_title);
        btnSubmit = findViewById(R.id.button25);
        cancelbtn = findViewById(R.id.button26);

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etTitle.getText().toString())) {
                    etTitle.setError("Can't be empty");
                } else {
                    String allquestions = "";
                    String allAnswers = "";

                    int sameoption = 0;

                    for (MultichoiceModel multichoiceModel : choiceFieldList) {

                        for (int i = 0; i < choiceFieldList.size(); i++) {
                            if (multichoiceModel.getOption().equalsIgnoreCase(choiceFieldList.get(i).getOption())) {
                                sameoption++;
                            }
                            if (TextUtils.isEmpty(multichoiceModel.getOption())) {
                                Toast.makeText(getContext(), "please all visible option is required", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if (sameoption > choiceFieldList.size()) {
                            Toast.makeText(getContext(), "Options should not be same", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (multichoiceModel.ischecked) {
                            ansIsChecked = true;
                        }
                        if (allquestions.equalsIgnoreCase("") && allAnswers.equalsIgnoreCase("")) {
                            allquestions = multichoiceModel.getOption();
                            allAnswers = multichoiceModel.getAnsTypes();
                        } else {
                            if (!allquestions.equalsIgnoreCase("") && !TextUtils.isEmpty(multichoiceModel.getOption())) {
                                allquestions = allquestions + "," + multichoiceModel.getOption();
                            }
                            allAnswers = allAnswers + "," + multichoiceModel.getAnsTypes();
                        }
                    }

                    if (ansIsChecked) {
                        if (!TextUtils.isEmpty(allquestions) && allquestions.contains(",") && !TextUtils.isEmpty(apiKey)) {
                            progress.showProgressBar();
                            Map<String, Object> jsonParams = new ArrayMap<>();
                            jsonParams.put("qtype", "2");
                            jsonParams.put("qcontent", etTitle.getText().toString());
                            jsonParams.put("option", allquestions);
                            jsonParams.put("answer", allAnswers);
                            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());
                            try {
                                apiInterface.addInterviewQuestions(apiKey, body).enqueue(new Callback<EOMCQuestionRequest>() {
                                    @Override
                                    public void onResponse(Call<EOMCQuestionRequest> call, Response<EOMCQuestionRequest> response) {
                                        progress.hideProgressBar();

                                        if (response.body() != null) {
                                            if (response.body().getError() == 0 && response.body().getData() != null) {
                                                ((ActivityInterviewKit) context).addMultiQuestion(response.body().getData().getQid(), response.body().getData().getQcontent(), response.body().getData().getQtype());
                                                Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                                dismiss();
                                            } else {
                                                Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<EOMCQuestionRequest> call, Throwable t) {
                                        progress.hideProgressBar();
                                        Toast.makeText(getContext(), t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                progress.hideProgressBar();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getContext(), "please all visible option is required", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "please mention correct Answer", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        recMultiChoise.setLayoutManager(new LinearLayoutManager(context));
        choiceFieldList.add(new MultichoiceModel("", "0"));
        choiceFieldList.add(new MultichoiceModel("", "0"));
        multiChoiceAdatper = new MultiChoiceAdatper(context, choiceFieldList);
        recMultiChoise.setAdapter(multiChoiceAdatper);
        findidhere();
    }

    private void findidhere() {
        tvMore = findViewById(R.id.textView214);
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceFieldList.add(new MultichoiceModel("", "0"));
                multiChoiceAdatper.notifyDataSetChanged();
            }
        });
    }

    protected void checkview() {
        if (choiceFieldList.size() < 5)
            tvMore.setVisibility(View.VISIBLE);
        else
            tvMore.setVisibility(View.GONE);
    }

    @Override
    public void show() {
        super.show();
    }

    //todo ===================================== Adapter class =====================================
    class MultiChoiceAdatper extends RecyclerView.Adapter<MultiChoiceAdatper.MultiChoiceViewHolder> {

        private Context context;
        private List<MultichoiceModel> ansrsList;
        private String option;

        public MultiChoiceAdatper(Context context, List<MultichoiceModel> ansrsList) {
            this.context = context;
            this.ansrsList = ansrsList;
        }

        @NonNull
        @Override
        public MultiChoiceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_multiple_choice, viewGroup, false);
            return new MultiChoiceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MultiChoiceViewHolder multiChoiceViewHolder, int position) {
            MultichoiceModel multichoiceModel = ansrsList.get(position);

            multiChoiceViewHolder.delimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choiceFieldList.remove(position);
                    notifyDataSetChanged();
                }
            });

            if (multichoiceModel.ischecked)
                multiChoiceViewHolder.checkBox.setChecked(true);
            else
                multiChoiceViewHolder.checkBox.setChecked(false);

            multiChoiceViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < ansrsList.size(); i++) {
                        if (i == position) {
                            ansrsList.get(i).setIschecked(true);
                            ansrsList.get(i).setAnsTypes("1");
                        } else {
                            ansrsList.get(i).setIschecked(false);
                            ansrsList.get(i).setAnsTypes("0");
                        }
                    }
                    notifyDataSetChanged();
                }
            });

            multiChoiceViewHolder.etOption.setText(multichoiceModel.getOption());
            multiChoiceViewHolder.etOption.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    option = s.toString();
                    ansrsList.get(position).setOption(option);
                }
            });

            if (position == ansrsList.size() - 1) {
                checkview();
            }

            switch (position) {
                case 0:
                    multiChoiceViewHolder.delimg.setVisibility(View.INVISIBLE);
                    multiChoiceViewHolder.etOption.setHint("Option 1");
                    break;
                case 1:
                    multiChoiceViewHolder.delimg.setVisibility(View.INVISIBLE);
                    multiChoiceViewHolder.etOption.setHint("Option 2");
                    break;
                case 2:
                    multiChoiceViewHolder.delimg.setVisibility(View.VISIBLE);
                    multiChoiceViewHolder.etOption.setHint("Option 3");
                    break;
                case 3:
                    multiChoiceViewHolder.delimg.setVisibility(View.VISIBLE);
                    multiChoiceViewHolder.etOption.setHint("Option 4");
                    break;
                case 4:
                    multiChoiceViewHolder.delimg.setVisibility(View.VISIBLE);
                    multiChoiceViewHolder.etOption.setHint("Option 5");
                    break;
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
            return ansrsList.size();
        }

        class MultiChoiceViewHolder extends RecyclerView.ViewHolder {

            private EditText etOption;
            private CheckBox checkBox;
            private ImageView delimg;

            public MultiChoiceViewHolder(@NonNull View itemView) {
                super(itemView);

                etOption = itemView.findViewById(R.id.textView203);
                delimg = itemView.findViewById(R.id.imageView83);
                checkBox = itemView.findViewById(R.id.checkBox2);
            }
        }
    }

    class MultichoiceModel {

        private boolean ischecked;
        private String option;
        private String ansTypes;

        public String getAnsTypes() {
            return ansTypes;
        }

        public void setAnsTypes(String ansTypes) {
            this.ansTypes = ansTypes;
        }

        public MultichoiceModel(String option, String ansTypes) {
            this.option = option;
            this.ansTypes = ansTypes;
        }

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        public boolean isIschecked() {
            return ischecked;
        }

        public void setIschecked(boolean ischecked) {
            this.ischecked = ischecked;
        }
    }

}
