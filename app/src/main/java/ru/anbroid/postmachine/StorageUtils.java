package ru.anbroid.postmachine;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class StorageUtils
{
    public static boolean checkStoragePermission(Activity activityCompat)
    {
        boolean result = true;

        if (android.os.Build.VERSION.SDK_INT < 33 && ContextCompat.checkSelfPermission(activityCompat,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activityCompat,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

            result = false;
        }

        return result;
    }
}
