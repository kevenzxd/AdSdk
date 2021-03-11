package com.mopub.mobileads;

import androidx.annotation.NonNull;

import com.amazon.device.ads.AdError;
import com.amazon.device.ads.DTBAdCallback;
import com.amazon.device.ads.DTBAdResponse;

public class APSInterstitialLoader {

    public APSInterstitialLoader() {

    }

    public static void load() throws Exception {
        APSManager.getInstance().loadInterstitial(new DTBAdCallback() {
            @Override
            public void onFailure(@NonNull AdError adError) {
//                UnityPlayer.UnitySendMessage("APSManager", "onInterstitialFailure", adError.getMessage());
                APSManager.getInstance().sendCallBack("onInterstitialFailure", adError.getMessage());
            }

            @Override
            public void onSuccess(@NonNull DTBAdResponse dtbAdResponse) {
//                String keywords = dtbAdResponse.getMoPubKeywords();
//                UnityPlayer.UnitySendMessage("APSManager", "onInterstitialSuccess", keywords);
                APSManager.getInstance().sendCallBack("onInterstitialSuccess", dtbAdResponse.getMoPubKeywords());
            }
        });
    }
}
