package com.jobma.employer.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jobma.employer.R;
import com.jobma.employer.adapters.ViewPagerAdapter;
import com.jobma.employer.fragments.FragmentApplicantCounts;
import com.jobma.employer.fragments.FragmentOnHoldCounts;
import com.jobma.employer.fragments.FragmentPendingCounts;
import com.jobma.employer.fragments.FragmentRejectedCounts;
import com.jobma.employer.fragments.FragmentSelectedCounts;
import com.jobma.employer.model.dashboard.EOInterviewCountsData;
import com.jobma.employer.util.ObjectUtil;

public class ActivityApplicantsReport extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivBackBtn;
    private TabLayout tabLayout;
    private TextView pageTitle;
    private ViewPager viewPager;
    private String[] tabTitle = {"Applicants", "Selected", "Rejected", "Pending", "On Hold"};
    private String[] pageTitleList = {"Applicants List", "Selected List", "Rejected List", "Pending List", "On Hold List"};
    private EOInterviewCountsData interviewCountsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_applicants_report);

        if (!ObjectUtil.isEmpty(this.getIntent().getSerializableExtra("countValues"))) {
            this.interviewCountsData = (EOInterviewCountsData) this.getIntent().getSerializableExtra("countValues");
        }

        this.initView();
        this.setOnClickListener();
    }

    private void initView() {
        this.ivBackBtn = this.findViewById(R.id.ivBackBtn);
        this.tabLayout = this.findViewById(R.id.tabLayout);
        this.pageTitle = this.findViewById(R.id.textView243);
        this.viewPager = this.findViewById(R.id.viewPager);

        this.viewPager.setOffscreenPageLimit(5);

        this.setupViewPager(this.viewPager);
        this.tabLayout.setupWithViewPager(viewPager);

        try {
            setupTabIcons();
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position, false);
                pageTitle.setText(pageTitleList[position]);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });

    }

    private void setOnClickListener() {
        this.ivBackBtn.setOnClickListener(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentApplicantCounts(), "Applicants");
        adapter.addFragment(new FragmentSelectedCounts(), "Selected");
        adapter.addFragment(new FragmentRejectedCounts(), "Rejected");
        adapter.addFragment(new FragmentPendingCounts(), "Pending");
        adapter.addFragment(new FragmentOnHoldCounts(), "On Hold");

        viewPager.setAdapter(adapter);
    }

    private View prepareTabView(int position) {
        View view = getLayoutInflater().inflate(R.layout.custom_tab, null);
        TextView tv_title = view.findViewById(R.id.tv_title);
        TextView tv_count = view.findViewById(R.id.tv_count);

        tv_title.setText(tabTitle[position]);

        //TODO logic for show counts on tab layout
        if (tabTitle[position].equalsIgnoreCase("Applicants")) {
            tv_count.setText(String.valueOf(interviewCountsData.getApplication()));
        } else if (tabTitle[position].equalsIgnoreCase("Selected")) {
            tv_count.setText(String.valueOf(interviewCountsData.getSelected()));
        } else if (tabTitle[position].equalsIgnoreCase("Rejected")) {
            tv_count.setText(String.valueOf(interviewCountsData.getRejected()));
        } else if (tabTitle[position].equalsIgnoreCase("Pending")) {
            tv_count.setText(String.valueOf(interviewCountsData.getPending()));
        } else if (tabTitle[position].equalsIgnoreCase("On Hold")) {
            tv_count.setText(String.valueOf(interviewCountsData.getOnHold()));
        }

        return view;
    }

    private void setupTabIcons() {
        for (int i = 0; i < tabTitle.length; i++) {
            tabLayout.getTabAt(i).setCustomView(prepareTabView(i));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivBackBtn) {
            this.finish();
        }
    }

}
