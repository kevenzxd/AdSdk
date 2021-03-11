package com.mopub.unity;

import android.widget.FrameLayout;

import com.mopub.mobileads.MoPubView;

interface AdLoader {

    void requestBanner(final float width, final float height,
                       final String keywords, final String userDataKeywords);

    void setLocalBannerListener(LocalBannerListener bannerListener);

    void hideBanner(boolean shouldHide);

    void refreshBannerKeywords(String keywords, String userDataKeywords);

    void refreshBanner(String keywords, String userDataKeywords);

    void destroyBanner();

    void setAutoRefreshEnabled(boolean enabled);

    void forceRefresh();

    void setViewParameters(FrameLayout viewContainer, FrameLayout.LayoutParams layoutParameter);

    boolean isPluginReady();

    void setRefreshTime(int second);

    MoPubView getMoPubView();
}
