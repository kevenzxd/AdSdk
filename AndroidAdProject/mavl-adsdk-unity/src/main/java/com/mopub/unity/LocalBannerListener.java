package com.mopub.unity;

import androidx.annotation.NonNull;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

interface LocalBannerListener {
    void onBannerLoaded(@NonNull MoPubView moPubView);

    void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode);

    void onBannerClicked(MoPubView moPubView);

    void onBannerExpanded(MoPubView moPubView);

    void onBannerCollapsed(MoPubView moPubView);
}
