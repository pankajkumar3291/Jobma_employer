package com.jobma.employer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityFilter;
import com.jobma.employer.adapters.ViewPagerAdapter;

public class FragmentApplicantReport extends Fragment implements View.OnClickListener {

    private TabLayout applicant_tabLayout;
    private ViewPager applicant_viewpager;
    private View view;
    private FloatingActionButton btnfilter;
    private Bundle bundle;

    private FragmentApplicants fragmentApplicants = new FragmentApplicants();
    private FragmentSelected fragmentSelected = new FragmentSelected();
    private FragmentRejected fragmentRejected = new FragmentRejected();
    private FragmentPending fragmentPending = new FragmentPending();
    private FragmentOnHold fragmentOnHold = new FragmentOnHold();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_applicant_report, container, false);

        findidhere();

        applicant_tabLayout.setupWithViewPager(applicant_viewpager);
        applicant_tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorDarkSkyBlue));

        if (getArguments() != null) {
            if (getArguments().containsKey("fromApplicant")) {
                bundle = new Bundle();
                bundle.putString("fromTrack", "track");
                setupViewPager(applicant_viewpager, "Track");
            }
        } else {
            setupViewPager(applicant_viewpager, "");
        }

        return view;
    }

    private void findidhere() {
        applicant_tabLayout = view.findViewById(R.id.applicant_tabLayout);
        applicant_viewpager = view.findViewById(R.id.applicant_view_pagger);
        btnfilter = view.findViewById(R.id.floatingActionButton2);
        btnfilter.setOnClickListener(this);
    }

    private void setupViewPager(ViewPager viewPager, String s) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(fragmentApplicants, s.equalsIgnoreCase("Track") ? "Invited" : "Applicants");
        adapter.addFragment(fragmentSelected, "Selected");
        adapter.addFragment(fragmentRejected, "Rejected");
        adapter.addFragment(fragmentPending, "Pending");
        adapter.addFragment(fragmentOnHold, "On Hold");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButton2:
                startActivity(new Intent(getContext(), ActivityFilter.class));
                break;
        }
    }
}
