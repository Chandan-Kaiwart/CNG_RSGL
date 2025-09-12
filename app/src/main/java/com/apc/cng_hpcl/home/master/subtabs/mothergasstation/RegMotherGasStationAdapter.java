package com.apc.cng_hpcl.home.master.subtabs.mothergasstation;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class RegMotherGasStationAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public RegMotherGasStationAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MGSGeneralInfoFragment();
            case 1:
                return new MGSEquipmentInfoFragment();
            case 2:
                return new MGSInstrumentsInfoFragment();

            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}