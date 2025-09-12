package com.apc.cng_hpcl.home.master.subtabs.daughterboosterstation;

import android.content.Context;

import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.MGSEquipmentInfoFragment;
import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.MGSGeneralInfoFragment;
import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.MGSInstrumentsInfoFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class RegDaughterBoosterStationAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public RegDaughterBoosterStationAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RegDBSGeneralInfoFragment();
            case 1:
                return new RegDBSEquipmentInfoFragment();
            case 2:
                return new RegDBSInstrumentsInfoFragment();

            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}