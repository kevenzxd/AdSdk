package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.mopub.common.LifecycleListener;
import com.mopub.common.Preconditions;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.util.Views;

import java.util.Collections;
import java.util.Map;

import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.CLICKED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.CUSTOM;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_ATTEMPTED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_FAILED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.LOAD_SUCCESS;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.SHOW_ATTEMPTED;
import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.SHOW_SUCCESS;
import static com.mopub.mobileads.GooglePlayServicesAdapterConfiguration.forwardNpaIfSet;
import static com.mopub.mobileads.MoPubErrorCode.NETWORK_NO_FILL;
import static com.mopub.mobileads.MoPubErrorCode.NO_FILL;

public class GooglePlayServicesBanner extends BaseAd {
    /*
     * These keys are intended for MoPub internal use. Do not modify.
     */
    public static final String AD_UNIT_ID_KEY = "adUnitID";
    public static final String CONTENT_URL_KEY = "contentUrl";
    public static final String TAG_FOR_CHILD_DIRECTED_KEY = "tagForChildDirectedTreatment";
    public static final String TAG_FOR_UNDER_AGE_OF_CONSENT_KEY = "tagForUnderAgeOfConsent";
    public static final String TEST_DEVICES_KEY = "testDevices";

    private static final String ADAPTER_NAME = GooglePlayServicesBanner.class.getSimpleName();
    @Nullable
    private AdView mGoogleAdView;
    @Nullable
    private String mAdUnitId;
    private Integer adWidth;
    private Integer adHeight;

    @Override
    protected void load(@NonNull final Context context, @NonNull final AdData adData) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(adData);

        adWidth = adData.getAdWidth();
        adHeight = adData.getAdHeight();
        final Map<String, String> extras = adData.getExtras();

        mAdUnitId = extras.get(AD_UNIT_ID_KEY);

        mGoogleAdView = new AdView(context);
        mGoogleAdView.setAdListener(new AdViewListener());
        mGoogleAdView.setAdUnitId(mAdUnitId);

        final AdSize adSize = (adWidth == null || adHeight == null || adWidth <= 0 || adHeight <= 0)
                ? null
                : new AdSize(adWidth, adHeight);

        final AdSize targetAdSize = getAdSize(context, adSize);
        if (targetAdSize != null) {
            mGoogleAdView.setAdSize(targetAdSize);
        } else {
            MoPubLog.log(getAdNetworkId(), LOAD_FAILED, ADAPTER_NAME,
                    NETWORK_NO_FILL.getIntCode(),
                    NETWORK_NO_FILL);

            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(NETWORK_NO_FILL);
            }
            return;
        }

        final AdRequest.Builder builder = new AdRequest.Builder();
        builder.setRequestAgent("MoPub");

        // Publishers may append a content URL by passing it to the MoPubView.setLocalExtras() call.
        final String contentUrl = extras.get(CONTENT_URL_KEY);

        if (!TextUtils.isEmpty(contentUrl)) {
            builder.setContentUrl(contentUrl);
        }

        forwardNpaIfSet(builder);

        final RequestConfiguration.Builder requestConfigurationBuilder = new RequestConfiguration.Builder();

        // Publishers may request for test ads by passing test device IDs to the MoPubView.setLocalExtras() call.
        final String testDeviceId = extras.get(TEST_DEVICES_KEY);

        if (!TextUtils.isEmpty(testDeviceId)) {
            requestConfigurationBuilder.setTestDeviceIds(Collections.singletonList(testDeviceId));
        }

        // Publishers may want to indicate that their content is child-directed and forward this
        // information to Google.
        final String childDirected = extras.get(TAG_FOR_CHILD_DIRECTED_KEY);

        if (childDirected != null) {
            if (Boolean.parseBoolean(childDirected)) {
                requestConfigurationBuilder.setTagForChildDirectedTreatment(TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE);
            } else {
                requestConfigurationBuilder.setTagForChildDirectedTreatment(TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE);
            }
        } else {
            requestConfigurationBuilder.setTagForChildDirectedTreatment(TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED);
        }

        // Publishers may want to mark their requests to receive treatment for users in the
        // European Economic Area (EEA) under the age of consent.
        final String underAgeOfConsent = extras.get(TAG_FOR_UNDER_AGE_OF_CONSENT_KEY);

        if (underAgeOfConsent != null) {
            if (Boolean.parseBoolean(underAgeOfConsent)) {
                requestConfigurationBuilder.setTagForUnderAgeOfConsent(TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE);
            } else {
                requestConfigurationBuilder.setTagForUnderAgeOfConsent(TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE);
            }
        } else {
            requestConfigurationBuilder.setTagForUnderAgeOfConsent(TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED);
        }

        final RequestConfiguration requestConfiguration = requestConfigurationBuilder.build();
        MobileAds.setRequestConfiguration(requestConfiguration);

        final AdRequest adRequest = builder.build();
        mGoogleAdView.loadAd(adRequest);

        MoPubLog.log(getAdNetworkId(), LOAD_ATTEMPTED, ADAPTER_NAME);
    }

    @Nullable
    @Override
    protected View getAdView() {
        return mGoogleAdView;
    }

    @Override
    protected void onInvalidate() {
        Views.removeFromParent(mGoogleAdView);

        if (mGoogleAdView != null) {
            mGoogleAdView.setAdListener(null);
            mGoogleAdView.destroy();
        }
    }

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    @NonNull
    public String getAdNetworkId() {
        return mAdUnitId == null ? "" : mAdUnitId;
    }

    @Override
    protected boolean checkAndInitializeSdk(@NonNull final Activity launcherActivity,
                                            @NonNull final AdData adData) {
        return false;
    }

    private class AdViewListener extends AdListener {
        @Override
        public void onAdClosed() {
        }

        @Override
        public void onAdFailedToLoad(LoadAdError loadAdError) {
            MoPubLog.log(getAdNetworkId(), LOAD_FAILED, ADAPTER_NAME,
                    getMoPubErrorCode(loadAdError.getCode()).getIntCode(),
                    getMoPubErrorCode(loadAdError.getCode()));
            MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "Failed to load Google " +
                    "banners with message: " + loadAdError.getMessage() + ". Caused by: " +
                    loadAdError.getCause());

            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(getMoPubErrorCode(loadAdError.getCode()));
            }
        }

        @Override
        public void onAdLoaded() {
            final int receivedWidth = mGoogleAdView.getAdSize().getWidth();
            final int receivedHeight = mGoogleAdView.getAdSize().getHeight();

//            if (receivedWidth > adWidth || receivedHeight > adHeight) {
//                MoPubLog.log(getAdNetworkId(), LOAD_FAILED, ADAPTER_NAME,
//                        NETWORK_NO_FILL.getIntCode(),
//                        NETWORK_NO_FILL);
//                MoPubLog.log(getAdNetworkId(), CUSTOM, ADAPTER_NAME, "Google served an ad but" +
//                        " it was invalidated because its size of " + receivedWidth + " x " + receivedHeight +
//                        " exceeds the publisher-specified size of " + adWidth + " x " + adHeight);
//
//                if (mLoadListener != null) {
//                    mLoadListener.onAdLoadFailed(getMoPubErrorCode(NETWORK_NO_FILL.getIntCode()));
//                }
//            } else {
                MoPubLog.log(getAdNetworkId(), LOAD_SUCCESS, ADAPTER_NAME);
                MoPubLog.log(getAdNetworkId(), SHOW_ATTEMPTED, ADAPTER_NAME);
                MoPubLog.log(getAdNetworkId(), SHOW_SUCCESS, ADAPTER_NAME);

                if (mLoadListener != null) {
                    mLoadListener.onAdLoaded();
                }
//            }
        }

        @Override
        public void onAdOpened() {
            MoPubLog.log(getAdNetworkId(), CLICKED, ADAPTER_NAME);

            if (mInteractionListener != null) {
                mInteractionListener.onAdClicked();
            }
        }

        /**
         * Converts a given Google Mobile Ads SDK error code into {@link MoPubErrorCode}.
         *
         * @param error Google Mobile Ads SDK error code.
         * @return an equivalent MoPub SDK error code for the given Google Mobile Ads SDK error
         * code.
         */
        private MoPubErrorCode getMoPubErrorCode(int error) {
            switch (error) {
                case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                    return MoPubErrorCode.INTERNAL_ERROR;
                case AdRequest.ERROR_CODE_INVALID_REQUEST:
                    return MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR;
                case AdRequest.ERROR_CODE_NETWORK_ERROR:
                    return MoPubErrorCode.NO_CONNECTION;
                case AdRequest.ERROR_CODE_NO_FILL:
                    return NO_FILL;
                default:
                    return MoPubErrorCode.UNSPECIFIED;
            }
        }
    }

    private AdSize getAdSize(Context context, AdSize adSize) {
        if ((adSize.getHeight() == 50
                || adSize == AdSize.FULL_BANNER
                || adSize == AdSize.LEADERBOARD)
                && (context instanceof Activity)) {
            // Step 2 - Determine the screen width (less decorations) to use for the ad width.
            Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            float widthPixels = outMetrics.widthPixels;
            float density = outMetrics.density;

            int adWidth = (int) (widthPixels / density);

            // Step 3 - Get adaptive ad size and return for setting on the ad view.
            AdSize tempSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
            return tempSize;
        } else {
            return adSize;
        }
    }
}
