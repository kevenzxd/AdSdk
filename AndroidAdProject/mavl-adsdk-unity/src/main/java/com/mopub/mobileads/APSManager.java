package com.mopub.mobileads;

import android.os.Build;
import android.util.Log;

import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.DTBAdCallback;
import com.amazon.device.ads.DTBAdRequest;
import com.amazon.device.ads.DTBAdSize;
import com.amazon.device.ads.MRAIDPolicy;
import com.mopub.common.MoPub;
import com.mopub.common.logging.MoPubLog;
import com.unity.sdk.AdSdkException;
import com.unity.sdk.AdSdkManager;
//import com.unity3d.player.UnityPlayer;

import java.io.IOException;

public class APSManager {

    // Connect public ids.
//    private String AppID = "7dc67bab-6b13-4fe4-a1f8-b93032e856e2";
//    private String RewardID = "dc4cf01c-e8cf-4b33-967f-f0b76aa30f13";
//    private String InterstitialID = "d93e7614-f93e-4bb3-8a07-ef4e00060190";
//    private String BannerID = "aef7ee7d-1229-473f-968a-2bdf745c4db1";
//    private String MrecID = "aaaaa";

    private String AppID = "aps app id";
    private String RewardID = "aps rewarded id";
    private String InterstitialID = "aps interstitial id";
    private String BannerID = "aps banner id";
    private String MrecID = "aps mrec id";

    private boolean Test = false;

    private static APSManager instance;

    private APSManager() {
        initSdk();
    }

    public static APSManager getInstance() {
        if (instance == null) {                         //Single Checked
            synchronized (APSManager.class) {
                if (instance == null) {                 //Double Checked
                    instance = new APSManager();
                }
            }
        }
        return instance;
    }

    public void initSdk() {
//        AdRegistration.getInstance(AppID, UnityPlayer.currentActivity);
        try {
            if (MoPub.getPersonalInformationManager() != null) {
                if (MoPub.getPersonalInformationManager().gdprApplies()) {
                    AdRegistration.setCMPFlavor(AdRegistration.CMPFlavor.MOPUB_CMP);
                }
            }
        } catch (NullPointerException ex) {
            Log.w("APSManager" , "NullPointerException happens ex: "+ex.getMessage());
        }
        //Please remove enableLogging and enableTesting in production mode
        AdRegistration.enableLogging(Test);
        AdRegistration.enableTesting(Test);
        //Optional. Highly recommended for Transparent Ad Marketplace users
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            AdRegistration.useGeoLocation(true);
        } else {
            AdRegistration.useGeoLocation(false);
        }
        //Required call for passing MRAID version
        AdRegistration.setMRAIDPolicy(MRAIDPolicy.MOPUB);
    }

    public void loadBanner(final DTBAdCallback callback) {
        DTBAdRequest adLoader = new DTBAdRequest();
        DTBAdSize mDTBAdSize = new DTBAdSize(320, 50, BannerID);
        adLoader.setSizes(mDTBAdSize);
        adLoader.loadAd(callback);
    }

    public void loadMrec(final DTBAdCallback callback) {
        DTBAdRequest adLoader = new DTBAdRequest();
        DTBAdSize mDTBAdSize = new DTBAdSize(300, 250, MrecID);
        adLoader.setSizes(mDTBAdSize);
        adLoader.loadAd(callback);
    }

    public void loadInterstitial(final DTBAdCallback callback) {
        DTBAdSize mDTBAdSize = new DTBAdSize.DTBInterstitialAdSize(InterstitialID);
        DTBAdRequest adLoader = new DTBAdRequest();
        adLoader.setSizes(mDTBAdSize);
        adLoader.loadAd(callback);
    }

    public void loadReward(final DTBAdCallback callback) {
        DTBAdRequest adLoader = new DTBAdRequest();
        DTBAdSize mDTBAdSize = new DTBAdSize.DTBVideo(640, 390, RewardID);
        adLoader.setSizes(mDTBAdSize);
        adLoader.loadAd(callback);
    }

    public void sendCallBack(String method, String info) {
        try {
            AdSdkManager.getInstance().sendMsgToUnity("APSManager", method, info);
        } catch (AdSdkException e) {
            MoPubLog.log(MoPubLog.SdkLogEvent.ERROR_WITH_THROWABLE,
                    "Exception sending message to Unity", e);
        }
    }
}
