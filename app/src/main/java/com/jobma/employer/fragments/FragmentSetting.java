package com.jobma.employer.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

public class FragmentSetting extends Fragment {

    private View view;
    private ViewPager viewPager1;
    private TextView btnPassword, btnvideo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_setting, container, false);

        this.initView();
        this.setupViewPager(viewPager1);

        return view;
    }

    private void initView() {
        viewPager1 = view.findViewById(R.id.viewpager1);
        btnPassword = view.findViewById(R.id.button4);
        btnvideo = view.findViewById(R.id.button5);
        btnPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager1.setCurrentItem(0);
            }
        });

        btnvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager1.setCurrentItem(1);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {

        SettingViewPagerAdapter adapter = new SettingViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new FragmentChangePassword(), "ONE");
        adapter.addFragment(new FragmentCompanyVideo(), "TWO");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (0 == i) {
                    btnPassword.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bg_btn_selected_dashboard));
                    btnPassword.setTextColor(getResources().getColor(R.color.colorDarkSkyBlue));
                    btnvideo.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bg_btn_unselected_dashboard));
                    btnvideo.setTextColor(getResources().getColor(R.color.colorWhite));
                } else {
                    btnPassword.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bg_btn_unselected_dashboard));
                    btnPassword.setTextColor(getResources().getColor(R.color.colorWhite));
                    btnvideo.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bg_btn_selected_dashboard));
                    btnvideo.setTextColor(getResources().getColor(R.color.colorDarkSkyBlue));
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((ActivityDashboard) getActivity()).checkFragmentVisibility("setting");
    }

    class SettingViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SettingViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
