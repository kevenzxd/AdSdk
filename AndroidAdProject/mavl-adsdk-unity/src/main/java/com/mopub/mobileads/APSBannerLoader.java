package com.mopub.mobileads;

import androidx.annotation.NonNull;

import com.amazon.device.ads.AdError;
import com.amazon.device.ads.DTBAdCallback;
import com.amazon.device.ads.DTBAdResponse;

public class APSBannerLoader {
    public APSBannerLoader() {

    }

    public static void load() throws Exception {
        APSManager.getInstance().loadBanner(new DTBAdCallback() {
            @Override
            public void onFailure(@NonNull AdError adError) {
//                UnityPlayer.UnitySendMessage("APSManager", "onBannerFailure", adError.getMessage());
                APSManager.getInstance().sendCallBack("onBannerFailure", adError.getMessage());
            }

            @Override
            public void onSuccess(@NonNull DTBAdResponse dtbAdResponse) {
//                String keywords = dtbAdResponse.getMoPubKeywords();
//                UnityPlayer.UnitySendMessage("APSManager", "onBannerSuccess", keywords);
                APSManager.getInstance().sendCallBack("onBannerSuccess", dtbAdResponse.getMoPubKeywords());
            }
        });
    }
}
