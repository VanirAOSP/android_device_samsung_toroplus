package org.teameos.settings.device;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    static SharedPreferences mPrefs;
    static SharedPreferences.Editor mEdit;
    
    public static SharedPreferences getPrefs(Context c) {
        mPrefs = c.getSharedPreferences("msl_prefs", Context.MODE_PRIVATE);
        return mPrefs;        
    }
    
    public static SharedPreferences.Editor getEdit() {
        mEdit = mPrefs.edit();
        return mEdit;
    }
}
