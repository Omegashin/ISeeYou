package com.bdcorps.cyclops;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent;

        // try the new Jelly Bean direct android wallpaper chooser first
        try
        {
            ComponentName component = new ComponentName(getPackageName(), getPackageName() + ".CyclopsWallpaperService");
            intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, component);
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e3)
        {
            // try the generic android wallpaper chooser next
            try
            {
                intent = new Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
                startActivity(intent);
            } catch (android.content.ActivityNotFoundException e2)
            {
                // that failed, let's try the nook intent
                try
                {
                    intent = new Intent();
                    intent.setAction("com.bn.nook.CHANGE_WALLPAPER");
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException e)
                {
                    // everything failed, let's notify the user
                    Toast.makeText(MainActivity.this, "Wallpaper chooser not found", Toast.LENGTH_SHORT).show();
                }
            }
        }
        finish();
    }
}
