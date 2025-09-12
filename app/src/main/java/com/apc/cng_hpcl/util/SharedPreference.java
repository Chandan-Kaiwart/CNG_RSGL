package com.apc.cng_hpcl.util;

import android.content.Context;
import android.content.SharedPreferences;
public class SharedPreference {
    Context context;

    public SharedPreference(Context contex){
        this.context = context;
    }

//    public void saveMgsDetails(String lcv, String mgs_id, String dbs_id) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("MGSDetails", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("LCV", lcv);
//        editor.putString("MGSID", mgs_id);
//        editor.putString("DBSID", dbs_id);
//        editor.commit();
//    }

//    public String getMgsDetails() {
//
////        SharedPreferences sharedPreferences = context.getSharedPreferences("MGSDetails", Context.MODE_APPEND);
//        return sharedPreferences.getString("LCV", "");
//    }

}
