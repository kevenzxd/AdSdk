package com.adsdk.unity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import com.unity.sdk.AdSdkManager;
import com.unity.sdk.UnityInterface;
import com.unity.sdk.ad.NativeLayoutData;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initAdSDK();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    protected AdSdkManager mAdSdkManager;

    private void initAdSDK() {
        // Important Add for AdSdk.
        mAdSdkManager = AdSdkManager.getInstance();
        mAdSdkManager.init(this, new UnityInterface() {
            @Override
            public void sendMessageToUnity(String s, String s1, String s2) {
                Log.d(TAG, "sendMessageToUnity "
                        +  " Class:" + s
                        +  " Method:" + s
                        +  " Param:" + s
                );
//                UnityPlayer.UnitySendMessage(s, s1, s2);
            }
        });
        buildNativeLayout();

        // Open Debug Log
        mAdSdkManager.enableDebugMode();
    }

    // Add for adsdk. Native sample code.
    private void buildNativeLayout() {
//        NativeLayoutData.ViewBinder mopubBinder =
//                new NativeLayoutData.NativeViewBuilder(R.layout.ad_sdk_mopub_unity_native_ad_view)
//                        .mainImageId(R.id.ad_image)
//                        .iconImageId(R.id.ad_icon)
//                        .textId(R.id.ad_text)
//                        .titleId(R.id.ad_title)
//                        .callToActionId(R.id.call_to_action)
//                        .adChoice(R.id.ad_choice).build();
//        NativeLayoutData.ViewBinder googleBinder =
//                new NativeLayoutData.NativeViewBuilder(R.layout.ad_sdk_google_unity_native_ad_view)
//                        .mainImageId(R.id.ad_image)
//                        .iconImageId(R.id.ad_icon)
//                        .textId(R.id.ad_text)
//                        .titleId(R.id.ad_title)
//                        .callToActionId(R.id.call_to_action).build();
//        NativeLayoutData.ViewBinder facebookBinder =
//                new NativeLayoutData.NativeViewBuilder(R.layout.ad_sdk_fan_unity_native_ad_view)
//                        .mainImageId(R.id.ad_image)
//                        .iconImageId(R.id.ad_icon)
//                        .textId(R.id.ad_text)
//                        .titleId(R.id.ad_title)
//                        .callToActionId(R.id.call_to_action)
//                        .adChoice(R.id.ad_choice).build();
//        NativeLayoutData.ViewBinder smaatoBinder =
//                new NativeLayoutData.NativeViewBuilder(R.layout.ad_sdk_smaato_unity_native_ad_view)
//                        .mainImageId(R.id.ad_image)
//                        .iconImageId(R.id.ad_icon)
//                        .textId(R.id.ad_text)
//                        .titleId(R.id.ad_title)
//                        .callToActionId(R.id.call_to_action)
//                        .adChoice(R.id.ad_choice).build();
//        mAdSdkManager.setupNativeAdViewLayout(mopubBinder, googleBinder, facebookBinder, smaatoBinder);
    }
}