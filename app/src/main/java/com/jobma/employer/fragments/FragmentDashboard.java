package com.jobma.employer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityDashboard;

public class FragmentDashboard extends Fragment implements View.OnClickListener {

    private View view;
    private ViewPager viewPager;
    private TextView btnOverview, btnRecentApplicant;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        this.initView();
        this.setOnClickListner();
        this.setupViewPager(viewPager);

        return view;
    }

    private void initView() {
        this.viewPager = view.findViewById(R.id.viewpager);
        this.btnOverview = view.findViewById(R.id.button4);
        this.btnRecentApplicant = view.findViewById(R.id.button5);
    }

    private void setOnClickListner() {
        btnOverview.setOnClickListener(this);
        btnRecentApplicant.setOnClickListener(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        final DashboardViewPagerAdapter adapter = new DashboardViewPagerAdapter(getContext(), getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    btnOverview.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_btn_selected_dashboard));
                    btnOverview.setTextColor(getResources().getColor(R.color.colorDarkSkyBlue));
                    btnRecentApplicant.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_btn_unselected_dashboard));
                    btnRecentApplicant.setTextColor(getResources().getColor(R.color.colorWhite));
                } else {
                    btnOverview.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bg_btn_unselected_dashboard));
                    btnOverview.setTextColor(getResources().getColor(R.color.colorWhite));
                    btnRecentApplicant.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bg_btn_selected_dashboard));
                    btnRecentApplicant.setTextColor(getResources().getColor(R.color.colorDarkSkyBlue));
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button4:
                this.changeFragments(0);
                break;
            case R.id.button5:
                this.changeFragments(1);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((ActivityDashboard) getActivity()).checkFragmentVisibility("Dashboard");
    }

    private void changeFragments(int position) {
        if (position == 0 && viewPager.getCurrentItem() == 1)
            viewPager.setCurrentItem(0, true);
        else if (position == 1 && viewPager.getCurrentItem() == 0)
            viewPager.setCurrentItem(1, true);
    }

    //TODO viewpager adapter for replacing the pages
    public class DashboardViewPagerAdapter extends FragmentPagerAdapter {

        private Context context;

        private DashboardViewPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FragmentOverview();
                case 1:
                    return new FragmentRecentApplicant();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}
