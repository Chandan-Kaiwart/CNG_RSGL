package com.apc.cng_hpcl.home.registration.subtabs;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class RegTabAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public RegTabAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OrganizationRegFragment();
            case 1:
                return new EmployeeRegFragment();

            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}