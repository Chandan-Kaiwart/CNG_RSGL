package com.apc.cng_hpcl.home.master.subtabs.lcv;

import android.content.Context;

import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.MGSEquipmentInfoFragment;
import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.MGSGeneralInfoFragment;
import com.apc.cng_hpcl.home.master.subtabs.mothergasstation.MGSInstrumentsInfoFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class EditLcvAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public EditLcvAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new EditLcvGeneralInfoFragment();
            case 1:
                return new EditLcvCascadeInfoFragment();


            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}