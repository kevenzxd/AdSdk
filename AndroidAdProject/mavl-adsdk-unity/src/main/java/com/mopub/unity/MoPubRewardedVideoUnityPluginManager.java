package com.mopub.unity;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.mopub.common.MoPubReward;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;

import java.util.Set;

import static com.mopub.common.logging.MoPubLog.AdLogEvent;

/**
 * Singleton class to handle Rewarded Video events, as this is the one ad format to follow a
 * singleton pattern and we need to manage these events outside of the plugin lifecycle.
 */
public class MoPubRewardedVideoUnityPluginManager implements MoPubRewardedVideoListener {
    private static String TAG = "MoPub";
    private static volatile MoPubRewardedVideoUnityPluginManager sInstance;

    private MoPubRewardedVideoUnityPluginManager() {}

    static MoPubRewardedVideoUnityPluginManager getInstance() {
        if (sInstance == null) {
            synchronized (MoPubRewardedVideoUnityPluginManager.class) {
                if (sInstance == null) {
                    sInstance = new MoPubRewardedVideoUnityPluginManager();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onRewardedVideoLoadSuccess(@NonNull final String adUnitId) {
        MoPubUnityPlugin.UnityEvent.RewardedVideoLoaded.Emit(adUnitId);
    }

    @Override
    public void onRewardedVideoLoadFailure(@NonNull final String adUnitId,
                                           @NonNull final MoPubErrorCode errorCode) {
        /*Activity getActivity = getActivity();
        if (getActivity instanceof LocalActivity) {
            LocalActivity activity = (LocalActivity)getActivity();
            activity.hideLoadingUI();
        }*/
        if (errorCode == MoPubErrorCode.EXPIRED)
            MoPubUnityPlugin.UnityEvent.RewardedVideoExpired.Emit(adUnitId);
        else
            MoPubUnityPlugin.UnityEvent.RewardedVideoFailed.Emit(adUnitId, errorCode.toString());
    }

    @Override
    public void onRewardedVideoStarted(@NonNull final String adUnitId) {
        // disable loading ui logic.
        /**
        Activity getActivity = getActivity();
        if (getActivity instanceof LocalActivity) {
            LocalActivity activity = (LocalActivity)getActivity();
            activity.hideLoadingUI();
        }
        */
        MoPubUnityPlugin.UnityEvent.RewardedVideoShown.Emit(adUnitId);
    }

    @Override
    public void onRewardedVideoClicked(@NonNull final String adUnitId) {
        MoPubUnityPlugin.UnityEvent.RewardedVideoClicked.Emit(adUnitId);
    }

    @Override
    public void onRewardedVideoPlaybackError(@NonNull final String adUnitId,
                                             @NonNull final MoPubErrorCode errorCode) {
        // disable loading ui logic.
        /**
        Activity getActivity = getActivity();
        if (getActivity instanceof LocalActivity) {
            LocalActivity activity = (LocalActivity)getActivity();
            activity.hideLoadingUI();
        }
        */
        MoPubUnityPlugin.UnityEvent.RewardedVideoFailedToPlay.Emit(adUnitId, errorCode.toString());
    }

    @Override
    public void onRewardedVideoClosed(@NonNull final String adUnitId) {
        MoPubUnityPlugin.UnityEvent.RewardedVideoClosed.Emit(adUnitId);
    }

    @Override
    public void onRewardedVideoCompleted(@NonNull final Set<String> adUnitIds,
                                         @NonNull final MoPubReward reward) {
        if (adUnitIds.size() == 0 || reward == null) {
            MoPubLog.log(AdLogEvent.CUSTOM,
                    "Rewarded ad completed without ad unit ID and/or reward.");
            return;
        }

        for (String adUnitId : adUnitIds) {
            MoPubUnityPlugin.UnityEvent.RewardedVideoReceivedReward.Emit(adUnitId,
                    reward.getLabel(), String.valueOf(reward.getAmount()));
        }
    }
}
