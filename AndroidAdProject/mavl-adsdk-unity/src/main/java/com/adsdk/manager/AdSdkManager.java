package com.adsdk.manager;

import android.app.Activity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.ironsource.mediationsdk.IronSource;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.logging.MoPubLogger;
import com.ogury.sdk.Ogury;
import com.ogury.sdk.OguryConfiguration;
import com.unity.sdk.ad.AdPresenter;
import com.unity.sdk.base.FullLifecycleObserverAdapter;
import com.unity.sdk.base.LifecycleHandler;

public class AdSdkManager implements LifecycleOwner {

    private static final String TAG = "AdSdkManager";

    private Activity mActivity;
    private UnityInterface mUnityInterface;

    private LifecycleRegistry mLifecycleRegistry;
    private FullLifecycleObserverAdapter mFullLifecycleObserverAdapter;
    private AdPresenter mAdPresenter;
    private LifecycleHandler mLifecycleHandler;
    private NativeLayoutData mNativeLayoutData;

    private static class AdSdkManagerHolder {
        private static final AdSdkManager INSTANCE = new AdSdkManager();
    }

    public static final AdSdkManager getInstance() {
        return AdSdkManagerHolder.INSTANCE;
    }

    /**
     *
     * 初始化 AD SDK Manager。
     *
     * @param activity：主Activity
     * @param unityInterface：SDK与Unity交互接口，实现从Android向Unity传输消息
     */
    public void init(Activity activity, UnityInterface unityInterface) {

        mActivity = activity;
        mUnityInterface = unityInterface;

        // Add it for ad life cycle.
        mLifecycleRegistry = new LifecycleRegistry(this);

        mLifecycleHandler = new LifecycleHandler(this);

        FrameLayout sdkViewContainer = new FrameLayout(mActivity);

        mAdPresenter = new AdPresenter(mActivity, sdkViewContainer, this);
        mFullLifecycleObserverAdapter = new FullLifecycleObserverAdapter(this, mAdPresenter);

        mLifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);

        mActivity.addContentView(sdkViewContainer,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));

        initOgury();
    }

    public void enableDebugMode() {
        MoPubLog.setLogLevel(MoPubLog.LogLevel.DEBUG);
        MoPubLog.addLogger(new MoPubLogger() {
            @Override
            public void log(@Nullable String s, @Nullable String s1, @Nullable String s2, @Nullable String s3) {
                Log.e(TAG, "debugMP log: "
                        + " className: " + s
                        + " methodName: " + s1
                        + " identifier: " + s2
                        + " message: " + s3
                );
            }
        });
    }

    public void setupNativeAdViewLayout(NativeLayoutData.ViewBinder mopubBinder,
                                        NativeLayoutData.ViewBinder googleBinder,
                                        NativeLayoutData.ViewBinder facebookBinder,
                                        NativeLayoutData.ViewBinder smaatoBinder) {
        mNativeLayoutData = new NativeLayoutData(mopubBinder, googleBinder, facebookBinder, smaatoBinder);
    }

    public Activity getActivity() {
        return mActivity;
    }

    public NativeLayoutData getNativeLayoutData() {
        return mNativeLayoutData;
    }

    // Quit Unity
    public void onDestroy() {
        if (mLifecycleRegistry == null || mLifecycleHandler == null) {
            return;
        }
        mLifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
        // Add it for ad life cycle.
        getLifecycle().removeObserver(mFullLifecycleObserverAdapter);
        mLifecycleHandler.removeCallbacksAndMessages(null);
    }

    public void onStart() {
        if (mLifecycleRegistry == null) {
            return;
        }
        mLifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
    }

    public void onResume() {
        if (mLifecycleRegistry == null || mActivity == null) {
            return;
        }
        IronSource.onResume(mActivity);
        mLifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
    }

    public void onPause() {
        if (mLifecycleRegistry == null || mLifecycleHandler == null || mActivity == null) {
            return;
        }
        IronSource.onPause(mActivity);
        mAdPresenter.hideLoadingView();
        mLifecycleHandler.removeCallbacksAndMessages(null);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

    public void showLoadingUI() {
//        mUnityPlayer.pause();
        mAdPresenter.showLoadingView();
    }

    public void hideLoadingUI() {
//        mUnityPlayer.resume();
        mAdPresenter.hideLoadingView();
    }

    public void postDelay(Runnable action, int delayTime) {
        if (mAdPresenter == null || mLifecycleHandler == null) {
            return;
        }
        int delayTimeC = delayTime;
        if (delayTime == 0) {
            delayTimeC = mAdPresenter.getAnimationShownTime();
        }
        mLifecycleHandler.postDelayed(action, delayTimeC);
    }

    public void sendMsgToUnity(String className, String method, String extraMsg) throws AdSdkException {
        if (mUnityInterface != null) {
            mUnityInterface.sendMessageToUnity(className, method, extraMsg);
        } else {
            throw new AdSdkException("Initial Ad SDK Firstly.");
        }
    }

    private void initOgury() {
        OguryConfiguration.Builder oguryConfigurationBuilder = new OguryConfiguration.Builder(mActivity, "OGY-XXXXXXXXXXXX");
        Ogury.start(oguryConfigurationBuilder.build());
    }
}
