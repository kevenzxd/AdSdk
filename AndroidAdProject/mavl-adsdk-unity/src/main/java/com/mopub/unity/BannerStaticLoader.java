package com.mopub.unity;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import java.util.HashMap;
import java.util.Map;

class BannerStaticLoader implements AdLoader, MoPubView.BannerAdListener {

    Context mContext;
    String mAdUnitId;
    MoPubView mMoPubView;
    FrameLayout mViewContainer;
    FrameLayout.LayoutParams mLayoutParams;
    LocalBannerListener mLocalBannerListener;

    BannerStaticLoader(Context context, String adId) {
        mContext = context;
        mAdUnitId = adId;
    }

    @Override
    public void requestBanner(float width, float height, String keywords, String userDataKeywords) {
        mMoPubView = new MoPubView(mContext);
        mMoPubView.setAdUnitId(mAdUnitId);
        mMoPubView.setKeywords(keywords);
        mMoPubView.setUserDataKeywords(userDataKeywords);
        mMoPubView.loadAd(MoPubView.MoPubAdSize.valueOf((int) height));
        mMoPubView.setBannerAdListener(this);
        mViewContainer.removeAllViews();
        mViewContainer.addView(mMoPubView, mLayoutParams);
    }

    @Override
    public void setLocalBannerListener(LocalBannerListener bannerListener) {
        mLocalBannerListener = bannerListener;
    }

    @Override
    public void hideBanner(boolean shouldHide) {

        if (shouldHide) {
            mMoPubView.setVisibility(View.GONE);
        } else {
            mMoPubView.setLayoutParams(mLayoutParams);
            mMoPubView.setVisibility(View.VISIBLE);
            mViewContainer.setVisibility(LinearLayout.VISIBLE);
        }
    }

    @Override
    public void refreshBannerKeywords(String keywords, String userDataKeywords) {
        mMoPubView.setKeywords(keywords);
        mMoPubView.setUserDataKeywords(userDataKeywords);
    }

    @Override
    public void refreshBanner(String keywords, String userDataKeywords) {
        mMoPubView.setKeywords(keywords);
        mMoPubView.setUserDataKeywords(userDataKeywords);
        mMoPubView.loadAd();
    }

    @Override
    public void destroyBanner() {
        mViewContainer.setVisibility(LinearLayout.GONE);
        mMoPubView.destroy();
        mMoPubView = null;
    }

    @Override
    public void setAutoRefreshEnabled(boolean enabled) {
        mMoPubView.setAutorefreshEnabled(enabled);
    }

    @Override
    public void forceRefresh() {
        mMoPubView.forceRefresh();
    }

    @Override
    public void setViewParameters(FrameLayout viewContainer, FrameLayout.LayoutParams layoutParameter) {
        mViewContainer = viewContainer;
        mLayoutParams = layoutParameter;
    }

    @Override
    public boolean isPluginReady() {
        return mMoPubView != null;
    }

    @Override
    public void setRefreshTime(int second) {

    }

    @Override
    public MoPubView getMoPubView() {
        return mMoPubView;
    }

    @Override
    public void onBannerLoaded(@NonNull MoPubView moPubView) {
        mLocalBannerListener.onBannerLoaded(moPubView);
    }

    @Override
    public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {
        mLocalBannerListener.onBannerFailed(moPubView, moPubErrorCode);
    }

    @Override
    public void onBannerClicked(MoPubView moPubView) {
        mLocalBannerListener.onBannerClicked(moPubView);
    }

    @Override
    public void onBannerExpanded(MoPubView moPubView) {
        mLocalBannerListener.onBannerExpanded(moPubView);
    }

    @Override
    public void onBannerCollapsed(MoPubView moPubView) {
        mLocalBannerListener.onBannerCollapsed(moPubView);
    }
}
