package org.teameos.settings.device;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import org.teameos.settings.device.R;

public class DeviceSettings extends PreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.OnPreferenceClickListener {

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEdit;
    Context mContext;
    Resources res;
    Preference mGetMsl;
    Preference mProvision;
    MslReceiver mslReceiver;
    IntentFilter filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main);
        mContext = (Context) this;
        res = mContext.getResources();
        mPrefs = Prefs.getPrefs(mContext);
        mEdit = Prefs.getEdit();
        mPrefs.registerOnSharedPreferenceChangeListener(this);
        filter = new IntentFilter();
        filter.addAction(res.getString(R.string.msl_action));
        mslReceiver = new MslReceiver();
        mContext.registerReceiver(mslReceiver, filter);
        mGetMsl = (Preference) findPreference("msl_acquire");
        mGetMsl.setOnPreferenceClickListener(this);
        mProvision = (Preference) findPreference("eos_sprint_provision");
        mProvision.setOnPreferenceClickListener(this);

        // remove clear agps for now
        PreferenceScreen ps = this.getPreferenceScreen();
        PreferenceCategory pc = (PreferenceCategory) ps.findPreference("eos_sprint_telephony");
        pc.removePreference((Preference) findPreference("eos_sprint_gpsclrx"));

        updateMslSummary();
    }

    void updateMslSummary() {
        int msl = mPrefs.getInt(res.getString(R.string.msl_key), 0);
        StringBuilder b = new StringBuilder();
        if (msl != 0) {
            b.append(res.getString(R.string.msl_code_prefix));
            b.append(" ")
                    .append(String.valueOf(msl))
                    .append("\n");
        }
        b.append(res.getString(R.string.msl_acquire_summary));
        mGetMsl.setSummary(b.toString());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(res.getString(R.string.msl_key))) {
            int msl = prefs.getInt(key, 0);
            updateMslSummary();
            StringBuilder builder = new StringBuilder();
            builder.append(res.getString(R.string.msl_code_prefix))
                    .append(" ")
                    .append(String.valueOf(msl));
            Toast.makeText(mContext, String.valueOf(builder.toString()), Toast.LENGTH_LONG).show();
        }

    }

    class MslReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(res.getString(R.string.msl_action))) {
                int msl = intent.getIntExtra(res.getString(R.string.msl_key), 0);
                mEdit.putInt(res.getString(R.string.msl_key), msl);
                mEdit.apply();
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        if (pref.equals(mGetMsl)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(res.getString(R.string.msl_instructions))
                    .setMessage(res.getString(R.string.msl_message))
                    .setPositiveButton(res.getString(R.string.msl_continue), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_MAIN)
                                    .setClassName(res.getString(R.string.hidden_menu)
                                            , res.getString(R.string.msl_checker));
                            mContext.startActivity(i);
                        }
                    })
                    .setNegativeButton(res.getString(R.string.msl_cancel), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mContext.unregisterReceiver(mslReceiver);    
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (pref.equals(mProvision)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(res.getString(R.string.provision_title))
                    .setMessage(res.getString(R.string.provision_message))
                    .setPositiveButton(res.getString(R.string.msl_continue), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent();
                            i.setAction(res.getString(R.string.provision_action))
                                    .setClassName(res.getString(R.string.provision_package)
                                            , res.getString(R.string.provision_class));
                            mContext.startActivity(i);
                        }
                    })
                    .setNegativeButton(res.getString(R.string.msl_cancel), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        return false;
    }
}
