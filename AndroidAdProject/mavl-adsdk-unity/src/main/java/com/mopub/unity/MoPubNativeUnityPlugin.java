package com.mopub.unity;

import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mopub.mobileads.MoPubNativeAd;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.adsdk.manager.AdSdkManager;
import com.adsdk.manager.NativeLayoutData;

import org.json.JSONObject;


/**
 * Provides an API that bridges the Unity Plugin with the MoPub Native SDK.
 * <p>
 * NOTE: This feature is still in Beta; if interested, please contact support@mopub.com
 */
public class MoPubNativeUnityPlugin extends MoPubUnityPlugin
        implements NativeAd.MoPubNativeEventListener {

    private MoPubNative mMoPubNative;
    private NativeAd mNativeAd;
    private RelativeLayout mLayout;
    private RelativeLayout mNativeAdViewContainer;
    private View mAdView;
    private Point mPosition;
    private Point mSize;
    // 0: Alignment Layout:
    // 1: Position Layout:
    private int mLayoutType = 0;

    /**
     * Creates a {@link MoPubNativeUnityPlugin} for the given ad unit ID.
     *
     * @param adUnitId String for the ad unit ID to use for this native ad.
     */
    public MoPubNativeUnityPlugin(final String adUnitId) {
        super(adUnitId);
    }

    @Override
    public boolean isPluginReady() {
        return mMoPubNative != null;
    }

    /* ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****
     * Native API                                                                              *
     * ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****/

    public void setNativePosition(int x, int y, int w, int h) {
        if (w > 0 && h > 0) {
            mLayoutType = 1;
            mPosition = new Point(x, y);
            mSize = new Point(w, h);
        }
    }

    /**
     * Loads a native ad for the current ad unit ID and with the given keywords.
     */
    public void requestNativeAd(final int alignment) {

        final NativeLayoutData data = AdSdkManager.getInstance().getNativeLayoutData();
        if (data == null) {
            UnityEvent.NativeFail.Emit(mAdUnitId, "Cannot load native ad if do not define the native layout.");
            return;
        }

        runSafelyOnUiThread(new Runnable() {
            public void run() {
                MoPubNativeAd.Builder builder = new MoPubNativeAd.Builder().withAdId(mAdUnitId)
                        .withActivity(getActivity())
                        .fanRenderer(
                                data.getFacebookBinder().getLayoutId(),
                                data.getFacebookBinder().getMainImageId(),
                                data.getFacebookBinder().getIconImageId(),
                                data.getFacebookBinder().getTitleId(),
                                data.getFacebookBinder().getTextId(),
                                data.getFacebookBinder().getCallToActionId(),
                                data.getFacebookBinder().getPrivacyInformationIconImageId())
                        .googleRenderer(
                                data.getGoogleBinder().getLayoutId(),
                                data.getGoogleBinder().getMainImageId(),
                                data.getGoogleBinder().getIconImageId(),
                                data.getGoogleBinder().getTitleId(),
                                data.getGoogleBinder().getTextId(),
                                data.getGoogleBinder().getCallToActionId())
                        .smaatoRenderer(
                                data.getSmaatoBinder().getLayoutId(),
                                data.getSmaatoBinder().getMainImageId(),
                                data.getSmaatoBinder().getIconImageId(),
                                data.getSmaatoBinder().getTitleId(),
                                data.getSmaatoBinder().getTextId(),
                                data.getSmaatoBinder().getCallToActionId(),
                                data.getSmaatoBinder().getPrivacyInformationIconImageId())
                        .staticRenderer(
                                data.getMopubBinder().getLayoutId(),
                                data.getMopubBinder().getMainImageId(),
                                data.getMopubBinder().getIconImageId(),
                                data.getMopubBinder().getTitleId(),
                                data.getMopubBinder().getTextId(),
                                data.getMopubBinder().getCallToActionId(),
                                data.getMopubBinder().getPrivacyInformationIconImageId())
                        .networkListener(new MoPubNative.MoPubNativeNetworkListener() {
                            @Override
                            public void onNativeLoad(NativeAd nativeAd) {
                                nativeAd.setMoPubNativeEventListener(MoPubNativeUnityPlugin.this);
                                prepAlignmentLayout(alignment);
                                mAdView = nativeAd.createAdView(getActivity(), null);
                                mNativeAd = nativeAd;
                                mNativeAd.prepare(mAdView);
                                mNativeAd.renderAdView(mAdView);
//                                int width = (int) getActivity().getResources().getDimension(R.dimen.native_ad_container_layout_width);
//                                int height = (int) getActivity().getResources().getDimension(R.dimen.native_ad_container_layout_height);

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                mAdView.setLayoutParams(layoutParams);
                                mNativeAdViewContainer.removeAllViews();
                                mNativeAdViewContainer.addView(mAdView);
                                UnityEvent.NativeLoad.Emit(mAdUnitId, new JSONObject().toString());
                            }

                            @Override
                            public void onNativeFail(NativeErrorCode nativeErrorCode) {
                                UnityEvent.NativeFail.Emit(mAdUnitId, nativeErrorCode.toString());
                            }
                        });
                mMoPubNative = builder.build();
                if (mMoPubNative != null) {
                    mMoPubNative.makeRequest();
                }
            }
        });
    }

    public boolean isNativeAdLoaded() {
        return mNativeAd != null;
    }

    public void showNativeAd() {

        runSafelyOnUiThread(new Runnable() {
            public void run() {

                if (mLayoutType != 0) {
                    DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();

                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                    View adView = mNativeAdViewContainer.getChildAt(0);
                    mLayout.setGravity(Gravity.TOP | Gravity.START);

                    RelativeLayout.LayoutParams adViewLp = new RelativeLayout.LayoutParams(mSize.x, mSize.y);
                    adView.setLayoutParams(adViewLp);

//                    RelativeLayout.LayoutParams containerLp = new RelativeLayout.LayoutParams(mSize.x + mPosition.x,
//                            mSize.y + +mPosition.y);
//                    mNativeAdViewContainer.setPadding(mPosition.x, mPosition.y, 0, 0);

                    // 可以更好的控制父View布局。
                    RelativeLayout.LayoutParams containerLp = new RelativeLayout.LayoutParams(mSize.x, mSize.y);
                    containerLp.leftMargin = mPosition.x;
                    containerLp.topMargin = mPosition.y;

                    mNativeAdViewContainer.setLayoutParams(containerLp);
                }
                mLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    public void hideNativeAd() {
        runSafelyOnUiThread(new Runnable() {
            public void run() {
                if (mLayout != null) {
                    mLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    public void destroyNativeAd() {
        runSafelyOnUiThread(new Runnable() {
            public void run() {
                if (mNativeAd != null) {
                    mNativeAd.destroy();
                    mNativeAd = null;
                }

                if (mMoPubNative != null) {
                    mMoPubNative.destroy();
                    mMoPubNative = null;
                }

                if (mNativeAdViewContainer != null) {
                    mNativeAdViewContainer.removeAllViews();
                }

                if (mLayout != null) {
                    mLayout.removeAllViews();
                }
            }
        });
    }

    /* ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****
     * MoPubNativeEventListener implementation                                                 *
     * ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****/

    @Override
    public void onImpression(View view) {
        UnityEvent.NativeImpression.Emit(mAdUnitId);
    }

    @Override
    public void onClick(View view) {
        UnityEvent.NativeClick.Emit(mAdUnitId);
    }

    /* ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****
     * Private helpers                                                                         *
     * ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****/

    private void prepAlignmentLayout(int alignment) {
        // create a RelativeLayout and add the ad view to it
        if (mLayout == null) {
            mLayout = new RelativeLayout(getActivity());
        } else {
            // remove the layout if it has a parent
            FrameLayout parentView = (FrameLayout) mLayout.getParent();
            if (parentView != null)
                parentView.removeView(mLayout);
        }
        mLayoutType = 0;
        int gravity = 0;

        switch (alignment) {
            case 0:
                gravity = Gravity.TOP | Gravity.LEFT;
                break;
            case 1:
                gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                break;
            case 2:
                gravity = Gravity.TOP | Gravity.RIGHT;
                break;
            case 3:
                gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
                break;
            case 4:
                gravity = Gravity.BOTTOM | Gravity.LEFT;
                break;
            case 5:
                gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
                break;
            case 6:
                gravity = Gravity.BOTTOM | Gravity.RIGHT;
                break;
        }

        mLayout.setGravity(gravity);

        mNativeAdViewContainer = new RelativeLayout(getActivity());
//
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                getActivity().getResources().getDimensionPixelSize(R.dimen.native_ad_container_layout_width)
//                , getActivity().getResources().getDimensionPixelSize(R.dimen.native_ad_container_layout_height));

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        mLayout.addView(mNativeAdViewContainer, lp);
        getActivity().addContentView(mLayout,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));

        mLayout.setVisibility(RelativeLayout.GONE);
    }
}
