package com.jobma.employer.adapters;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityLiveVideoInterview;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.dashboard.EOOverviewData;
import com.jobma.employer.model.setupInterview.EOTimeZoneData;
import com.jobma.employer.model.setupInterview.EOTimeZoneList;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class OverViewRecyclerviewAdapter extends RecyclerView.Adapter<OverViewRecyclerviewAdapter.OverviewViewHolder> {

    private ArrayList<EOOverviewData> overviewDataList;
    private Context context;
    private boolean isvisible;
    private ArrayList<EOTimeZoneData> timeZoneDataList = new ArrayList<>();
    private String selectedTimeZone;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    public OverViewRecyclerviewAdapter(Context context, ArrayList<EOOverviewData> overviewDataList) {
        this.context = context;
        this.overviewDataList = overviewDataList;

        this.progress = new GlobalProgressDialog(context);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.getTimeZone();
    }

    @NonNull
    @Override
    public OverviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_overview, viewGroup, false);
        return new OverviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OverviewViewHolder holder, final int position) {
        float dialogRadius = UIUtil.getDimension(R.dimen._3sdp);
        UIUtil.setBackgroundRound(holder.dialogCorner, R.color.dialog_rounded_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        UIUtil.setBackgroundRound(holder.tv_rescheduled, R.color.colorDarkSkyBlue, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        EOOverviewData overviewData = this.overviewDataList.get(position);

        holder.tvname.setText(overviewData.getPitcherName());
        if (!ObjectUtil.isEmpty(overviewData.getJobmaInvitationDate())) {
            String[] splitStr = overviewData.getJobmaInvitationDate().split("\\s+");
            holder.tvtime.setText(splitStr[1].concat(" ").concat(splitStr[2]));
        }

        holder.tvinterview.setText(overviewData.getInterviewMode());
        holder.tvlocation.setText(overviewData.getInvitationTimezone());
        holder.tvAppliedFor.setText(overviewData.getJobName());
        loadImages(overviewData.getPitcherPhoto(), holder.profileImage);
        holder.tvInterviewToken.setText(ObjectUtil.isEmpty(overviewData.getJobmaInterviewToken()) ? "Interview token" : overviewData.getJobmaInterviewToken());

        holder.dotImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO uncomment the code for live interview
                if (isvisible) {
                    holder.dialoglayout.setVisibility(View.GONE);
                    isvisible = false;
                } else {
                    holder.dialoglayout.setVisibility(View.VISIBLE);
                    isvisible = true;
                }
            }
        });

        //TODO from here we are showing or hiding reschedule or 3 dots image
        if (!ObjectUtil.isEmpty(overviewData.getJobmaInterviewStatus())) {
            if (overviewData.getJobmaInterviewStatus().equalsIgnoreCase("1")) {
                holder.dotImage.setVisibility(View.VISIBLE);
                holder.tv_rescheduled.setVisibility(View.GONE);
            } else {
                holder.dotImage.setVisibility(View.GONE);
                holder.tv_rescheduled.setVisibility(View.VISIBLE);
            }

//            if (overviewData.getJobmaInterviewStatus().equalsIgnoreCase("1")) {
//                holder.dotImage.setVisibility(View.GONE);
//                holder.tv_rescheduled.setVisibility(View.VISIBLE);
//            } else {
//                holder.dotImage.setVisibility(View.VISIBLE);
//                holder.tv_rescheduled.setVisibility(View.GONE);
//            }

        }

        holder.tv_rescheduled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogReschedule();
            }
        });

        holder.tvTokenCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyTokenToClipboard(overviewData.getJobmaInterviewToken());
            }
        });

        holder.tvGoToLiveInterview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.dialoglayout.setVisibility(View.GONE);
                Intent liveInterview = new Intent(context, ActivityLiveVideoInterview.class);
                liveInterview.putExtra("invitedId", overviewData.getId());
                context.startActivity(liveInterview);
            }
        });

    }

    private static EditText selectDate;
    private EditText selectStartTime, selectEndTime;

    private void dialogReschedule() {
        final Dialog dialog = new Dialog(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_reschedule);

        float dialogRadius = UIUtil.getDimension(R.dimen._2sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.colorWhite, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        Button btn_cancel = dialog.findViewById(R.id.button26);
        Button btn_reschedule = dialog.findViewById(R.id.button25);
        Spinner spinnerTimeZone = dialog.findViewById(R.id.spinnerTimeZone);
        selectDate = dialog.findViewById(R.id.editText20);
        selectStartTime = dialog.findViewById(R.id.textInputLayout20);
        selectEndTime = dialog.findViewById(R.id.textInputLayout21);
        EditText editTextAddMessage = dialog.findViewById(R.id.editText21);
        dialog.show();

        //TODO set data into timezone spinner
        setDataInSpinner(spinnerTimeZone);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(((AppCompatActivity) context).getFragmentManager(), "datePicker");
            }
        });

        selectStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker(0);
            }
        });

        selectEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker(1);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        btn_reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidAllFields()) {
                    rescheduleInterviewApi();
                }
            }
        });
    }

    //TODO this api will reschedule to live interview
    private void rescheduleInterviewApi() {

    }

    private boolean isValidAllFields() {
        String errorMsg = null;

        if (selectedTimeZone.equalsIgnoreCase("Select time zone")) {
            errorMsg = "Please select time zone";
        } else if (ObjectUtil.isEmptyStr(selectDate.getText().toString())) {
            errorMsg = "Please select date";
        } else if (ObjectUtil.isEmptyStr(selectStartTime.getText().toString())) {
            errorMsg = "Please select start time";
        } else if (ObjectUtil.isEmptyStr(selectEndTime.getText().toString())) {
            errorMsg = "Please select end time";
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getTimeZone() {
        progress.showProgressBar();
        apiInterface.getTimeZone().enqueue(new Callback<EOTimeZoneList>() {
            @Override
            public void onResponse(Call<EOTimeZoneList> call, Response<EOTimeZoneList> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EOTimeZoneList eoTimeZoneList = response.body();
                    if (!ObjectUtil.isEmpty(eoTimeZoneList)) {
                        if (eoTimeZoneList.getError() == RESPONSE_SUCCESS) {
                            timeZoneDataList = (ArrayList<EOTimeZoneData>) eoTimeZoneList.getData();
                        } else {
                            Toast.makeText(context, "" + eoTimeZoneList.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EOTimeZoneList> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(context, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setDataInSpinner(Spinner spinnerTimeZone) {
        ArrayList<String> timeZoneList = new ArrayList<>();
        if (!ObjectUtil.isEmpty(timeZoneDataList)) {
            for (EOTimeZoneData timeZoneData : timeZoneDataList) {
                timeZoneList.add(timeZoneData.getKey());
            }
        }
        SpinnerAdapter arrayAdapter = new SpinnerAdapter(context, R.layout.spinner_item);
        arrayAdapter.addAll(timeZoneList);
        arrayAdapter.add("Select time zone");
        spinnerTimeZone.setAdapter(arrayAdapter);
        spinnerTimeZone.setSelection(arrayAdapter.getCount());
        spinnerTimeZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedTimeZone = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
            selectDate.setClickable(true);
            String startDate = String.valueOf(year).concat("-").concat(String.valueOf(month + 1)).concat("-").concat(String.valueOf(dayOfMonth));

            Date date = null;
            Date SystemDate = null;
            long millis = System.currentTimeMillis();
            java.sql.Date sysDate = new java.sql.Date(millis);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = simpleDateFormat.parse(startDate);
                SystemDate = simpleDateFormat.parse(sysDate.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (date.before(SystemDate)) {
                Toast toast = Toast.makeText(ApplicationHelper.application(), "You can not schedule interview in back date", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            } else if (date.equals(SystemDate)) {
                selectDate.setText(startDate);
            } else {
                selectDate.setText(startDate);
            }
        }
    }

    private Date date1 = null;

    private void openTimePicker(final int value) {
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (value == 0) {
                    selectStartTime.setText(selectedHour + ":" + (selectedMinute < 10 ? "0" + selectedMinute : selectedMinute));
                    selectEndTime.setText("");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    try {
                        date1 = simpleDateFormat.parse(selectedHour + ":" + selectedMinute);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    String temp;
                    if (TextUtils.isEmpty(selectStartTime.getText().toString())) {
                        Toast.makeText(context, "please select start time first", Toast.LENGTH_SHORT).show();
                    } else {

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        Date endDate = null;
                        try {
                            endDate = simpleDateFormat.parse(selectedHour + ":" + selectedMinute);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        long difference = endDate.getTime() - date1.getTime();
                        if (difference < 0) {
                            Date dateMax = null;
                            Date dateMin = null;
                            try {
                                dateMax = simpleDateFormat.parse("24:00");
                                dateMin = simpleDateFormat.parse("00:00");
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            difference = (dateMax.getTime() - date1.getTime()) + (endDate.getTime() - dateMin.getTime());
                        }
                        int days = (int) (difference / (1000 * 60 * 60 * 24));
                        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
                        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);

                        if ((hours == 2 && min == 0) || (hours < 2 && min <= 60)) {
                            selectEndTime.setText(selectedHour + ":" + (selectedMinute < 10 ? "0" + selectedMinute : selectedMinute));
                        } else {
                            Toast.makeText(context, "Maximum interview hours will not exceed from 2 hours", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }, hour, minute, false); //Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void copyTokenToClipboard(String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied text", Toast.LENGTH_SHORT).show();
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get()
                .load(imagePath)
                .fit()
                .error(R.drawable.ic_profile)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(this.overviewDataList) ? 0 : overviewDataList.size();
    }

    private class SpinnerAdapter extends ArrayAdapter<String> {

        private SpinnerAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            int count = super.getCount();
            return count > 0 ? count - 1 : count;
        }
    }

    public class OverviewViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        ImageView dotImage;
        ConstraintLayout dialoglayout, dialogCorner;
        TextView tvname, tvtime, tvinterview, tvlocation, tvAppliedFor, tvInterviewToken, tvTokenCopy, tvGoToLiveInterview, tv_rescheduled;

        private OverviewViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.circleImageView3);
            tvname = itemView.findViewById(R.id.textView97);
            tvtime = itemView.findViewById(R.id.textView98);
            tvinterview = itemView.findViewById(R.id.textView99);
            tvlocation = itemView.findViewById(R.id.textView100);
            tvAppliedFor = itemView.findViewById(R.id.textView102);
            dotImage = itemView.findViewById(R.id.imageView44);
            dialoglayout = itemView.findViewById(R.id.constraintLayout13);
            dialogCorner = itemView.findViewById(R.id.constraintLayout14);
            tvInterviewToken = itemView.findViewById(R.id.textView113);
            tvTokenCopy = itemView.findViewById(R.id.textView114);
            tvGoToLiveInterview = itemView.findViewById(R.id.textView112);
            tv_rescheduled = itemView.findViewById(R.id.tv_rescheduled);
        }
    }

}
