package com.bdcorps.cyclops;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.LinearLayout;

public class LiveWallpaperServiceSettings extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.wallpaper_settings);

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(
                this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
                this);
        super.onDestroy();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {


    }

    public void changeAssets(View view){
        SharedPreferences  sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("eye_design_pref", getResources().getResourceEntryName(view.getId()));
        editor.apply();

        String design_pref = sharedPreferences.getString("eye_design_pref", "Automatic");

        if (!design_pref.equals("Automatic")) {

            iSeeYouWallpaperService.setAssetNameWithPref(design_pref);
            iSeeYouWallpaperService.isManualSet=false;
        }

        LinearLayout linearLayout=(LinearLayout) findViewById(R.id.root);
        Snackbar.make(linearLayout,"Eye design changed to "+getResources().getResourceEntryName(view.getId()), Snackbar.LENGTH_LONG).show();



    }
}