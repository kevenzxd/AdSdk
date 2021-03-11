package com.mopub.unity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adsdk.R;
import com.google.android.gms.ads.AdSize;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import com.mopub.mobileads.MoPubView.MoPubAdSize;

/**
 * Provides an API that bridges the Unity Plugin with the MoPub Banner SDK.
 */

public class MoPubBannerUnityPlugin extends MoPubUnityPlugin implements LocalBannerListener {
    private FrameLayout mLayout;
    private int mBannerHeight;
    private int mAlignment = 5;
    private Point mPosition;
    // 0: Alignment Layout:
    // 1: Position Layout:
    private int mLayoutType = 0;

    private DisplayMetrics mDisplayMetrics;
    private AdLoader mAdLoader;

    /**
     * Creates a {@link MoPubBannerUnityPlugin} for the given ad unit ID.
     *
     * @param adUnitId String for the ad unit ID to use for this banner.
     */
    public MoPubBannerUnityPlugin(final String adUnitId) {
        super(adUnitId);
        mDisplayMetrics = getScreenInfo();
    }

    @Override
    public boolean isPluginReady() {
        return mAdLoader.isPluginReady();
    }

    /* ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****
     * Banners API                                                                             *
     * ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****/

    /**
     * Requests, loads and shows a banner with the given alignment and size for the current ad unit
     * ID, if it doesn't exist already.
     * <p>
     * Valid alignment values are:
     * 0 - top left
     * 1 - top center
     * 2 - top right
     * 3 - center
     * 4 - bottom left
     * 5 - bottom center
     * 6 - bottom right
     *
     * @param width            float for the maximum width, in dp, for the ad
     * @param height           float for the maximum height, in dp, for the ad
     * @param alignment        int for the desired alignment for the requested banner.
     * @param keywords         String with comma-separated key:value pairs of non-PII keywords.
     * @param userDataKeywords String with comma-separated key:value pairs of PII keywords.
     */
    public void requestBanner(final float width, final float height, final int alignment,
                              final String keywords, final String userDataKeywords) {

        if (height == 250) {
            mAdLoader = AdLoaderFactory.createStaticLoader(getActivity(), mAdUnitId);
        } else {
            mAdLoader = AdLoaderFactory.createRefreshLoader(getActivity(), mAdUnitId);
        }

        if (alreadyRequested())
            return;

        runSafelyOnUiThread(new Runnable() {
            public void run() {
                mBannerHeight = (int) height;
                prepLayout(alignment);

                mAdLoader.setViewParameters(mLayout, getViewLayoutParameters());
                mAdLoader.requestBanner(height, width, keywords, userDataKeywords);
                mAdLoader.setLocalBannerListener(MoPubBannerUnityPlugin.this);
                getActivity().addContentView(mLayout,
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));
            }
        });
    }

    private DisplayMetrics getScreenInfo() {

        DisplayMetrics outMetrics2 = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics2);
        return outMetrics2;
    }

    private int getViewHeight(float height) {
        if (height == 50) {
            float widthPixels = mDisplayMetrics.widthPixels;
            float density = mDisplayMetrics.density;

            int adWidth = (int) (widthPixels / density);

            // Step 3 - Get adaptive ad size and return for setting on the ad view.
            AdSize tempSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getActivity(), adWidth);
            return (int) (tempSize.getHeight() * density);
        } else {
            return (int) getActivity().getResources().getDimension(R.dimen.ad_sdk_banner_250_view_height);
        }
    }

    public void setBannerPosition(int x, int y) {
        mLayoutType = 1;
        mPosition = new Point(x, y);
        if (mAdLoader != null) {
            mAdLoader.setViewParameters(mLayout, getViewLayoutParameters());
        }
    }

    public void setBannerRefreshTime(int second) {
        if (mAdLoader != null) {
            mAdLoader.setRefreshTime(second);
        }
    }

    /**
     * Shows or hides the current banner.
     *
     * @param shouldHide hides the banner if true; shows the banner if false.
     */
    public void hideBanner(final boolean shouldHide) {
        if (!isPluginReady())
            return;

        runSafelyOnUiThread(new Runnable() {
            public void run() {
                mAdLoader.hideBanner(shouldHide);
            }
        });
    }

    /**
     * Sets the given keywords for the current banner and then reloads it. Personally
     * Identifiable Information (PII) should ONLY be passed via
     * {@link #refreshBanner(String)}
     *
     * @param keywords String with comma-separated key:value pairs of non-PII keywords.
     */
    public void refreshBannerKeywords(final String keywords, @Nullable final String userDataKeywords) {
        if (!isPluginReady())
            return;

        runSafelyOnUiThread(new Runnable() {
            public void run() {
                mAdLoader.refreshBannerKeywords(keywords, userDataKeywords);
            }
        });
    }

    /**
     * Sets the given keywords for the current banner and then reloads it. Personally
     * Identifiable Information (PII) should ONLY be passed via
     * {@link #refreshBanner(String)}
     *
     * @param keywords String with comma-separated key:value pairs of non-PII keywords.
     */
    public void refreshBanner(final String keywords) {
        refreshBanner(keywords, null);
    }

    /**
     * Sets the given keywords for the current banner and then reloads it. Personally
     * Identifiable Information (PII) should ONLY be present in the userDataKeywords field.
     *
     * @param keywords         String with comma-separated key:value pairs of non-PII keywords.
     * @param userDataKeywords String with comma-separated key:value pairs of PII keywords.
     */
    public void refreshBanner(final String keywords, @Nullable final String userDataKeywords) {
        if (!isPluginReady())
            return;

        runSafelyOnUiThread(new Runnable() {
            public void run() {
                mAdLoader.refreshBanner(keywords, userDataKeywords);
            }
        });
    }

    /**
     * Removes the current banner from the view and destroys it.
     */
    public void destroyBanner() {
        if (!isPluginReady() || mLayout == null)
            return;

        runSafelyOnUiThread(new Runnable() {
            public void run() {
                mLayout.removeAllViews();
                mAdLoader.destroyBanner();

            }
        });
    }


    public void setAutorefreshEnabled(boolean enabled) {
        Log.d(TAG , "debugMP setAutorefreshEnabled "+enabled);
        if (isPluginReady())
            mAdLoader.setAutoRefreshEnabled(enabled);
    }


    public void forceRefresh() {
        if (isPluginReady())
            mAdLoader.forceRefresh();
    }


    /* ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****
     * BannerAdListener implementation                                                         *
     * ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****/

    @Override
    public void onBannerLoaded(MoPubView banner) {
        UnityEvent.AdLoaded.Emit(mAdUnitId, String.valueOf(banner.getAdWidth()),
                String.valueOf(banner.getAdHeight()));
    }

    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        UnityEvent.AdFailed.Emit(mAdUnitId, errorCode.toString());
    }

    @Override
    public void onBannerClicked(MoPubView banner) {
        UnityEvent.AdClicked.Emit(mAdUnitId);
    }

    @Override
    public void onBannerExpanded(MoPubView banner) {
        UnityEvent.AdExpanded.Emit(mAdUnitId);
    }

    @Override
    public void onBannerCollapsed(MoPubView banner) {
        UnityEvent.AdCollapsed.Emit(mAdUnitId);
    }

    /* ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****
     * Private helpers                                                                         *
     * ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** ***** *****/

    private void prepLayout(int alignment) {
        // create a RelativeLayout and add the ad view to it
        if (mLayout == null) {
            mLayout = new FrameLayout(getActivity());
            mAlignment = alignment;
        } else {
            // remove the layout if it has a parent
            FrameLayout parentView = (FrameLayout) mLayout.getParent();
            if (parentView != null)
                parentView.removeView(mLayout);
        }

        mLayoutType = 0;

        int targetY = 0;
        int targetX = 0;

        Point bannerSize;
        if (mBannerHeight == 250) {
            bannerSize = new Point(
                    (int) getActivity().getResources().getDimension(R.dimen.ad_sdk_banner_300_view_width),
                    (int) getActivity().getResources().getDimension(R.dimen.ad_sdk_banner_250_view_height)
            );
            targetX = (mDisplayMetrics.widthPixels - bannerSize.x) / 2;
        } else {
            bannerSize = new Point(getScreenInfo().widthPixels, getViewHeight(50));
        }

        switch (mAlignment) {
            case 0:
            case 1:
            case 2:
                // no need compute the location.
                break;
            case 3:
                targetY = (getScreenHeight(getActivity()) - bannerSize.y) / 2;
                break;
            case 4:
            case 5:
            case 6:
                targetY = getScreenHeight(getActivity()) - bannerSize.y;
                break;
        }
        setBannerPosition(targetX, targetY);
    }

    private FrameLayout.LayoutParams getViewLayoutParameters() {
        Point bannerSize;
        if (mBannerHeight == 250) {
            bannerSize = new Point(
                    (int) getActivity().getResources().getDimension(R.dimen.ad_sdk_banner_300_view_width),
                    (int) getActivity().getResources().getDimension(R.dimen.ad_sdk_banner_250_view_height)
            );
        } else {
            bannerSize = new Point(getScreenInfo().widthPixels, getViewHeight(50));
        }

        FrameLayout.LayoutParams containerLp = new FrameLayout.LayoutParams(bannerSize.x, bannerSize.y);
        containerLp.leftMargin = mPosition.x;
        containerLp.topMargin = mPosition.y;
        return containerLp;
    }

    // Redundant method for better readability.
    private boolean alreadyRequested() {
        return isPluginReady();
    }

    private boolean isNavigationBarShow(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        Point realSize = new Point();
        display.getSize(size);
        display.getRealSize(realSize);
        return realSize.y!=size.y;
    }

    private int getNavigationBarHeight(Activity activity) {
        if (!isNavigationBarShow(activity)){
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    private int getScreenHeight(Activity activity) {
        return mDisplayMetrics.heightPixels + getNavigationBarHeight(activity);
    }

}
