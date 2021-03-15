package com.unity.sdk.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.facebook.ads.AdSettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xidongzhang on 2018/8/7 for MopubSdk
 */

public class AppUtils {
    private static final String TAG = "AppUtils";

    // Package private for testing only
    public static Object loadDefaultsFromMetadata(Context context, String keyString) {
        if (context == null) {
            return null;
        }

        ApplicationInfo ai = null;
        try {
            ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }

        if (ai.metaData == null) {
            return null;
        }

        return ai.metaData.get(keyString);
    }

    public static void showFanTestIdDialog(Activity activity) {
        final EditText et = new EditText(activity);
        et.setTextColor(Color.rgb(0, 0, 0));
        et.setBackgroundColor(Color.rgb(255, 255, 255));
        Dialog dialog = new AlertDialog
                .Builder(activity)
                .setView(et)
                .create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                CharSequence cs = et.getText();
                if (cs != null) {
                    Log.d(TAG, "fan id: " + String.valueOf(cs));
                    AdSettings.addTestDevice(String.valueOf(cs));
                }
            }
        });
        dialog.show();
    }

    public static List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                allchildren.add(viewchild);
                Log.d(TAG, "child view: " + viewchild);
                //再次 调用本身（递归）
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }

    public static long getStartTimeInMillisOfDay() {
        Calendar startDate = new GregorianCalendar();
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        return startDate.getTimeInMillis();
    }

    public static void printMapObject(Map<String, Object> map) {
        if (map != null) {
            StringBuilder sb = new StringBuilder();
            Set<String> keys = map.keySet();
            for (String key : keys) {
                sb.append("Key: ");
                sb.append(key);
                sb.append(" Value: ");
                sb.append(map.get(key));
                sb.append("\n");
            }
            Log.d(TAG, "Print Map " + sb.toString());
        } else {
            Log.d(TAG, "No content in this map.");
        }
    }

    public static void printMapString(Map<String, String> map) {
        if (map != null) {
            StringBuilder sb = new StringBuilder();
            Set<String> keys = map.keySet();
            for (String key : keys) {
                sb.append("Key: ");
                sb.append(key);
                sb.append(" Value: ");
                sb.append(map.get(key));
                sb.append("\n");
            }
            Log.d(TAG, "Print Map " + sb.toString());
        } else {
            Log.d(TAG, "No content in this map.");
        }
    }
}
