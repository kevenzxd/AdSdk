package com.unity.sdk.ad;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.lifecycle.LifecycleOwner;

import com.adsdk.R;
import com.unity.sdk.base.BasePresenter;

public class AdPresenter extends BasePresenter {

    private final static int S_ANIMATION_DURATION = (int) (1.0f * 1000);// 1s
    private static final String TAG = "AdPresenter";

    private Activity mActivity;

    private FrameLayout mBaseView;
    private FrameLayout mFrameLayout;
//    private LottieAnimationView mLottieAnimationView;

    public AdPresenter(Activity activity, FrameLayout baseView, LifecycleOwner lifecycleOwner) {
        super(lifecycleOwner);
        mActivity = activity;
        mBaseView = baseView;
        buildBaseAdView();
    }

    private Activity getActivity() {
        return mActivity;
    }

    @Override
    public void onCreate(LifecycleOwner owner) {
        super.onCreate(owner);
    }

    @Override
    public void onResume(LifecycleOwner owner) {
        super.onResume(owner);
    }

    @Override
    public void onPause(LifecycleOwner owner) {
        super.onPause(owner);
    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
        super.onDestroy(owner);
    }

    private void buildBaseAdView() {
        mFrameLayout = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.ad_sdk_loading_view, null);
//        mLottieAnimationView = mFrameLayout.findViewById(R.id.lottie_animation_view);
    }

    public void showLoadingView() {

        if (mFrameLayout.getParent() != null) {
            ((ViewGroup) mFrameLayout.getParent()).removeView(mFrameLayout);
        }
        mBaseView.addView(mFrameLayout);
//        mLottieAnimationView.playAnimation();
    }

    public void hideLoadingView() {
        try {
//            mLottieAnimationView.pauseAnimation();
            mBaseView.removeView(mFrameLayout);
        } catch (Exception ex) {
            Log.w(TAG , "Cannot hide loading view.");
        }
    }

    public int getAnimationShownTime() {
        return S_ANIMATION_DURATION;
    }
}
