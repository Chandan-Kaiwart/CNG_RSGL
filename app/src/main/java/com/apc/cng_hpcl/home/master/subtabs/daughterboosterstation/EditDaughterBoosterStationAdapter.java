package com.apc.cng_hpcl.home.master.subtabs.daughterboosterstation;

import android.content.Context;

import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.MGSEquipmentInfoFragment;
import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.MGSGeneralInfoFragment;
import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.MGSInstrumentsInfoFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class EditDaughterBoosterStationAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public EditDaughterBoosterStationAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new EditDBSGeneralInfoFragment();
            case 1:
                return new EditDBSEquipmentInfoFragment();
            case 2:
                return new EditDBSInstrumentsInfoFragment();

            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}