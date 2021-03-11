package com.mopub.mobileads;

import androidx.annotation.NonNull;

import com.amazon.device.ads.AdError;
import com.amazon.device.ads.DTBAdCallback;
import com.amazon.device.ads.DTBAdResponse;

public class APSMrecLoader {
    public APSMrecLoader() {

    }

    public static void load() throws Exception {
        APSManager.getInstance().loadMrec(new DTBAdCallback() {

            @Override
            public void onFailure(@NonNull AdError adError) {
//                UnityPlayer.UnitySendMessage("APSManager", "onMrecFailure", adError.getMessage());
                APSManager.getInstance().sendCallBack("onMrecFailure", adError.getMessage());
            }

            @Override
            public void onSuccess(@NonNull DTBAdResponse dtbAdResponse) {
//                String keywords = dtbAdResponse.getMoPubKeywords();
//                UnityPlayer.UnitySendMessage("APSManager", "onMrecSuccess", keywords);
                APSManager.getInstance().sendCallBack("onMrecSuccess", dtbAdResponse.getMoPubKeywords());
            }
        });
    }
}
