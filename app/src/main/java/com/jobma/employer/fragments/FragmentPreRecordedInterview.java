package com.jobma.employer.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityInviteMessage;
import com.jobma.employer.activities.ActivityLiveInterview;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.model.setupInterview.EOCandidatesObject;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.GlobalUtil;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.CREDIT_WALLET;
import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentPreRecordedInterview extends Fragment implements View.OnClickListener {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private String apiKey;
    private View view;
    private SessionSecuredPreferences loginPreferences;
    private String creditValue;
    private RecyclerView recAddCandidate;
    private List<EOCandidatesObject> candidateList = new ArrayList<>();
    private ImageView addImg;
    private Spinner interviewMode;
    private PreInterViewAdater preInterViewAdater;
    private ConstraintLayout addbtnlayout;
    private CheckBox radioButton;
    private static TextInputEditText et_select_date;
    private Button btnNext;
    private int jobId;
    private String allowCandidate = "0";
    private ArrayList<String> nameArray = new ArrayList<>();
    private ArrayList<String> emailArray = new ArrayList<>();
    private ArrayList<String> phoneArray = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_pre_recorded_interview, container, false);

        this.initView();
        this.dataToView();
        this.manageRecyclerview();
        this.setOnClickListener();

        if (!ObjectUtil.isEmpty(this.getArguments().getInt("jobId")))
            this.jobId = getArguments().getInt("jobId");

        return view;
    }

    private void initView() {
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.creditValue = loginPreferences.getString(CREDIT_WALLET, "");
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        recAddCandidate = view.findViewById(R.id.recyclerView2);
        addImg = view.findViewById(R.id.imageView66);
        addbtnlayout = view.findViewById(R.id.constraintLayout29);
        interviewMode = view.findViewById(R.id.spinner8);
        radioButton = view.findViewById(R.id.radioButton9);
        btnNext = view.findViewById(R.id.button16);
        et_select_date = view.findViewById(R.id.et_select_date);
    }

    private void manageRecyclerview() {
        candidateList.add(new EOCandidatesObject("", "", ""));
        preInterViewAdater = new PreInterViewAdater(getContext(), candidateList);
        recAddCandidate.setAdapter(preInterViewAdater);
    }

    private void setOnClickListener() {
        et_select_date.setOnClickListener(this);
        this.btnNext.setOnClickListener(this);
        this.radioButton.setOnClickListener(this);
        this.addImg.setOnClickListener(this);
        this.interviewMode.setOnItemSelectedListener(onJobSelectedListener);
    }

    private void dataToView() {
        ArrayAdapter<String> interviewAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, getResources().getStringArray(R.array.invite));
        interviewAdapter.setDropDownViewResource(R.layout.spinner_item);
        interviewMode.setAdapter(interviewAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.interviewMode.setSelection(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView66:
                candidateList.add(new EOCandidatesObject("", "", ""));
                preInterViewAdater.notifyDataSetChanged();
                if (candidateList.size() == 5)
                    addbtnlayout.setVisibility(View.GONE);
                recAddCandidate.smoothScrollToPosition(candidateList.size() - 1);
                break;
            case R.id.radioButton9:
                if (radioButton.isChecked())
                    allowCandidate = "1";
                else
                    allowCandidate = "0";
                break;
            case R.id.button16:
                getListData();
                break;
            case R.id.et_select_date:
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getActivity().getFragmentManager(), "datePicker");
                break;
        }
    }

    public void getListData() {
        nameArray.clear();
        emailArray.clear();
        phoneArray.clear();

        for (EOCandidatesObject eoCandidatesObject : candidateList) {
            if (ObjectUtil.isEmpty(eoCandidatesObject.getFullName()) || ObjectUtil.isEmpty(eoCandidatesObject.getEmailId()) || ObjectUtil.isEmpty(eoCandidatesObject.getPhoneNumber())) {
                Toast.makeText(getActivity(), R.string.all_fields_required, Toast.LENGTH_SHORT).show();
                return;
            } else if (!GlobalUtil.isValidEmail(eoCandidatesObject.getEmailId().trim())) {
                Toast.makeText(getActivity(), "Please enter valid email", Toast.LENGTH_SHORT).show();
                return;
            } else {
                nameArray.add(eoCandidatesObject.getFullName());
                emailArray.add(eoCandidatesObject.getEmailId());
                phoneArray.add(eoCandidatesObject.getPhoneNumber());
            }
        }
        if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_select_date))) {
            Toast.makeText(getActivity(), "Please select date", Toast.LENGTH_SHORT).show();
        } else {
            Intent intentMessage = new Intent(getActivity(), ActivityInviteMessage.class);
            intentMessage.putExtra("nameArray", nameArray);
            intentMessage.putExtra("emailArray", emailArray);
            intentMessage.putExtra("phoneArray", phoneArray);
            intentMessage.putExtra("selectedDate", ObjectUtil.getTextFromView(et_select_date));
            intentMessage.putExtra("allowCandidate", allowCandidate);
            intentMessage.putExtra("interViewMode", "1");
            intentMessage.putExtra("jobId", jobId);
            startActivity(intentMessage);
        }
    }

    AdapterView.OnItemSelectedListener onJobSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (((String) parent.getItemAtPosition(position)).equalsIgnoreCase("Live Video Interview")) {
                getCreditWallet();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

    };

    private void getCreditWallet() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.getCreditWallet(apiKey).enqueue(new Callback<EOForgetPassword>() {
                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword creditWalletValue = response.body();
                        if (!ObjectUtil.isEmpty(creditWalletValue)) {
                            if (creditWalletValue.getError() == RESPONSE_SUCCESS) {
                                Intent intent = new Intent(getActivity(), ActivityLiveInterview.class);
                                intent.putExtra("jobId", jobId);
                                startActivity(intent);
                            } else {
                                //TODO error 1 show popup, for not credit balance
                                interviewMode.setSelection(0);
                                showNoCreditWalletDialog(creditWalletValue.getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showNoCreditWalletDialog(String dialogMessage) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert);
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
            et_select_date.setClickable(true);
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
                et_select_date.setText(startDate);
            } else {
                Toast toast = Toast.makeText(ApplicationHelper.application(), "Selected date must be greater from current date", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    public void btnVisibility(int i) {
        addbtnlayout.setVisibility(View.VISIBLE);
    }

    //********************************* Adapter Class *********************************************
    class PreInterViewAdater extends RecyclerView.Adapter<PreInterViewAdater.PreInterViewHolder> {

        private Context context;
        private String fullname;
        private String email;
        private String phone;
        private List<EOCandidatesObject> candidateList;

        private PreInterViewAdater(Context context, List<EOCandidatesObject> candidateList) {
            this.context = context;
            this.candidateList = candidateList;
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public PreInterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_pre_recorded_interview, viewGroup, false);
            return new PreInterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PreInterViewHolder preInterViewHolder, final int position) {
            EOCandidatesObject eoCandidatesObject = candidateList.get(position);
            preInterViewHolder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyDataSetChanged();
                    candidateList.remove(position);
                    if (candidateList.size() == 4) {
                        btnVisibility(0);
                    }
                }
            });
            preInterViewHolder.et_full_name.setText(eoCandidatesObject.getFullName());
            preInterViewHolder.et_full_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    fullname = s.toString();
                    eoCandidatesObject.setFullName(fullname);
                }
            });
            preInterViewHolder.et_email.setText(eoCandidatesObject.getEmailId());
            preInterViewHolder.et_email.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    email = s.toString();
                    eoCandidatesObject.setEmailId(email);
                }
            });
            preInterViewHolder.et_phone.setText(eoCandidatesObject.getPhoneNumber());
            preInterViewHolder.et_phone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    phone = s.toString();
                    eoCandidatesObject.setPhoneNumber(phone);
                }
            });

            switch (position) {
                case 0:
                    preInterViewHolder.deleteLayout.setVisibility(View.GONE);
                    preInterViewHolder.tvTitle.setText("First Candidate");
                    break;
                case 1:
                    preInterViewHolder.tvTitle.setText("Second Candidate");
                    preInterViewHolder.deleteLayout.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    preInterViewHolder.tvTitle.setText("Third Candidate");
                    preInterViewHolder.deleteLayout.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    preInterViewHolder.tvTitle.setText("Fourth Candidate");
                    preInterViewHolder.deleteLayout.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    preInterViewHolder.tvTitle.setText("Fifth Candidate");
                    preInterViewHolder.deleteLayout.setVisibility(View.VISIBLE);
                    break;
            }
            if (position == candidateList.size() - 1 && eoCandidatesObject.getValue() == 1) {
//                getListData();
            }
        }

        private void clearForm(ViewGroup group) {
            for (int i = 0, count = group.getChildCount(); i < count; ++i) {
                View view = group.getChildAt(i);
                if (view instanceof EditText) {
                    ((EditText) view).setText("");
                }
                if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                    clearForm((ViewGroup) view);
            }
        }

        @Override
        public int getItemCount() {
            return ObjectUtil.isEmpty(candidateList.size()) ? 0 : candidateList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        private class PreInterViewHolder extends RecyclerView.ViewHolder {
            private TextView tvTitle;
            private ConstraintLayout deleteLayout;
            private ImageView deleteIcon;
            private TextInputEditText et_full_name, et_email, et_phone;

            private PreInterViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.textView162);
                deleteLayout = itemView.findViewById(R.id.delete_layout);
                deleteIcon = itemView.findViewById(R.id.imageView67);
                et_full_name = itemView.findViewById(R.id.et_full_name);
                et_email = itemView.findViewById(R.id.et_email);
                et_phone = itemView.findViewById(R.id.et_phone);
            }
        }
    }
}


