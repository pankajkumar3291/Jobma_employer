package com.jobma.employer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.google.android.material.tabs.TabLayout;
import com.jobma.employer.R;
import com.jobma.employer.adapters.ViewPagerAdapter;
import com.jobma.employer.fragments.FragmentApplicants;
import com.jobma.employer.fragments.FragmentOnHold;
import com.jobma.employer.fragments.FragmentPending;
import com.jobma.employer.fragments.FragmentRejected;
import com.jobma.employer.fragments.FragmentSelected;
import com.jobma.employer.util.ObjectUtil;

public class ActivityEvaluateCandidates extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private TabLayout applicant_tabLayout;
    private ViewPager applicant_viewpager;
    private ImageView btnfilter;
    private ImageView btnadd;
    private ImageView btnback;
    private TextView toolbar;

    private FragmentApplicants fragmentApplicants = new FragmentApplicants();
    private FragmentSelected fragmentSelected = new FragmentSelected();
    private FragmentRejected fragmentRejected = new FragmentRejected();
    private FragmentPending fragmentPending = new FragmentPending();
    private FragmentOnHold fragmentOnHold = new FragmentOnHold();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_evaluate_candidates);

        this.initView();
        this.setOnClickListener();

        applicant_tabLayout.setupWithViewPager(applicant_viewpager);
        applicant_tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorDarkSkyBlue));
        applicant_viewpager.setOffscreenPageLimit(5);
        toolbar = findViewById(R.id.toolbarTitle);

        Bundle bundle = new Bundle();

        if (getIntent().getStringExtra("Evaluate").equalsIgnoreCase("Track")) {
            bundle.putString("Evaluate", "Track");
            bundle.putString("jobId", getIntent().getStringExtra("jobId"));
            setupViewPager(applicant_viewpager, "Track");
        } else {
            setupViewPager(applicant_viewpager, "Evaluate");
            bundle.putString("Evaluate", "Evaluate");
            bundle.putString("jobId", getIntent().getStringExtra("jobId"));
            btnadd.setVisibility(View.GONE);
            btnfilter.setVisibility(View.GONE);
            toolbar.setText("Evaluate Candidates");
        }

        fragmentApplicants.setArguments(bundle);
        fragmentSelected.setArguments(bundle);
        fragmentRejected.setArguments(bundle);
        fragmentPending.setArguments(bundle);
        fragmentOnHold.setArguments(bundle);
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());

        this.applicant_tabLayout = findViewById(R.id.applicant_tabLayout);
        this.applicant_viewpager = findViewById(R.id.applicant_view_pagger);
        this.btnadd = findViewById(R.id.btnadd);
        this.btnfilter = findViewById(R.id.floatingActionButton2);
        this.btnback = findViewById(R.id.btnback);
    }

    private void setOnClickListener() {
        this.btnadd.setOnClickListener(this);
        this.btnfilter.setOnClickListener(this);
        this.btnback.setOnClickListener(this);
    }

    private void setupViewPager(ViewPager viewPager, String s) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fragmentApplicants, s.equalsIgnoreCase("Track") ? "Invited" : "Total Applicants");
        adapter.addFragment(fragmentPending, s.equalsIgnoreCase("Track") ? "Applied" : "Pending");
        adapter.addFragment(fragmentSelected, "Selected");
        adapter.addFragment(fragmentOnHold, "On Hold");
        adapter.addFragment(fragmentRejected, "Rejected");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.floatingActionButton2:
                if (!ObjectUtil.isEmpty(this.getIntent().getStringExtra("jobId"))) {
                    startActivity(new Intent(ActivityEvaluateCandidates.this, ActivityInterviewInvitation.class).putExtra("jobId", getIntent().getStringExtra("jobId")));
                }
                break;
            case R.id.btnback:
                finish();
                break;
            case R.id.btnadd:
                this.startActivity(new Intent(ActivityEvaluateCandidates.this, ActivityInvite.class));
                break;
        }
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
}
