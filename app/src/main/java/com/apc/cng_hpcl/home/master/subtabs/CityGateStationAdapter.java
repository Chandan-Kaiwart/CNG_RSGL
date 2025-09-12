package com.apc.cng_hpcl.home.master.subtabs;

import android.content.Context;

import com.apc.cng_hpcl.home.registration.subtabs.EmployeeRegFragment;
import com.apc.cng_hpcl.home.registration.subtabs.OrganizationRegFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class CityGateStationAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public CityGateStationAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RegCityGateStation();
            case 1:
                return new EditCityGateStation();

            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}