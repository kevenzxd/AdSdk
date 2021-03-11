package com.mopub.mobileads;

import androidx.annotation.NonNull;

import com.amazon.device.ads.AdError;
import com.amazon.device.ads.DTBAdCallback;
import com.amazon.device.ads.DTBAdResponse;

public class APSRewardLoader {


    public APSRewardLoader(){

    }

    public static void load() throws Exception {
        APSManager.getInstance().loadReward(new DTBAdCallback() {
            @Override
            public void onFailure(@NonNull AdError adError) {
//                UnityPlayer.UnitySendMessage("APSManager", "onRewardFailure", adError.getMessage());
                APSManager.getInstance().sendCallBack("onRewardFailure", adError.getMessage());
            }

            @Override
            public void onSuccess(@NonNull DTBAdResponse dtbAdResponse) {
//                String keywords = dtbAdResponse.getMoPubKeywords();
//                UnityPlayer.UnitySendMessage("APSManager", "onRewardSuccess", keywords);
                APSManager.getInstance().sendCallBack("onRewardFailure", dtbAdResponse.getMoPubKeywords());
            }
        });
    }
}
