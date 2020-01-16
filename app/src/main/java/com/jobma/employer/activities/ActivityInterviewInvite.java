package com.jobma.employer.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.jobma.employer.R;
import com.jobma.employer.fragments.FragmentPreRecordedInterview;
import com.jobma.employer.util.ObjectUtil;

import java.lang.ref.WeakReference;

public class ActivityInterviewInvite extends AppCompatActivity {

    private ImageView btnback;
    private int jobId;
    public static WeakReference<ActivityInterviewInvite> activityPreRecorded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_interview);

        activityPreRecorded = new WeakReference<>(this);

        if (!ObjectUtil.isEmpty(this.getIntent().getIntExtra("jobId", 0)))
            jobId = this.getIntent().getIntExtra("jobId", 0);

        this.initView();
    }

    private void initView() {
        this.btnback = this.findViewById(R.id.btnback);

        this.btnback.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ActivityInterviewInvite.this.finish();
                                            }
                                        }
        );

        FragmentPreRecordedInterview recordedInterview = new FragmentPreRecordedInterview();
        Bundle bundle = new Bundle();
        bundle.putInt("jobId", jobId);
        recordedInterview.setArguments(bundle);
        this.getSupportFragmentManager().beginTransaction().replace(R.id.invite_container, recordedInterview).commit();

    }

}
