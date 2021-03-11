package com.mopub.unity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import static com.mopub.unity.MoPubUnityPlugin.runSafelyOnUiThread;

class BannerRefreshLoader implements AdLoader {

    private static final String TAG = "BannerRefreshLoader";
    Context mContext;
    String mAdUnitId;
    MoPubView mMoPubView1;
    MoPubView mMoPubView2;
    FrameLayout mViewContainer;
    FrameLayout.LayoutParams mLayoutParams;
    LocalBannerListener mLocalBannerListener;
    BannerListener[] mBannerListeners = new BannerListener[2];

    LocalHandler mHandler = new LocalHandler();

    private final static int MSG_SHOW_BANNER_VIEW = 0x01;

    private final static int ONE_SECOND = 1000; // 20s验证一次当前view是否在显示广告。
    private final static int REFRESH_TIME = 20 * ONE_SECOND; // 20s验证一次当前view是否在显示广告。

    private int mRefreshTime = REFRESH_TIME;

    volatile int mCurrentShownIndex = 0;
    boolean mBannerShouldShow = true;

    class LocalHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_SHOW_BANNER_VIEW) {

                if (!mBannerShouldShow) {
                    return;
                }

                int targetIndex = msg.arg1;

                // 延迟开始执行之后，banner开始加载。
                mBannerListeners[mCurrentShownIndex - 1].setAdState(AdState.LOADING);
                // 如果30s（假定刷新时间）之后，需要查看另外一个banner状态
                // 三种情况：
                // 1. 初始化情况，哪个广告先回来，直接显示。
                // 2. 如果目标广告与现有显示广告为一个，则直接显示。
                // 3. 如果目标广告与现有显示广告不同，则隐藏现有广告，显示另外一个广告。

                if (!AdState.isLoaded(mBannerListeners[targetIndex - 1].mAdState)) {
                    return;
                }
                showBannerView(targetIndex);
                hideBannerView(getAnotherBannerListenerIndex(targetIndex));
                postToShowAnotherBannerView(getAnotherBannerListenerIndex(targetIndex));

            }
        }
    }

    BannerRefreshLoader(Context context, String adId) {
        mContext = context;
        mAdUnitId = adId;
    }

    @Override
    public void requestBanner(float width, float height, String keywords, String userDataKeywords) {
        mMoPubView1 = buildBannerView(1, width, height, keywords, userDataKeywords);
        mMoPubView2 = buildBannerView(2, width, height, keywords, userDataKeywords);
        mViewContainer.removeAllViews();
        mViewContainer.addView(mMoPubView1, mLayoutParams);
        mViewContainer.addView(mMoPubView2, mLayoutParams);
    }

    private MoPubView buildBannerView(int index, float width, float height, String keywords, String userDataKeywords) {
        MoPubView moPubView = new MoPubView(mContext);
        moPubView.setAdUnitId(mAdUnitId);
        moPubView.setKeywords(keywords);
        moPubView.setUserDataKeywords(userDataKeywords);

        moPubView.loadAd(MoPubView.MoPubAdSize.valueOf((int) height));
        BannerListener bannerListener = new BannerListener(index);
        moPubView.setBannerAdListener(bannerListener);

        mBannerListeners[index - 1] = bannerListener;

        //Force auto refresh banner when loading ad.
        moPubView.setAutorefreshEnabled(true);

        return moPubView;
    }

    @Override
    public void setLocalBannerListener(LocalBannerListener bannerListener) {
        mLocalBannerListener = bannerListener;
    }

    @Override
    public void hideBanner(boolean shouldHide) {
        mBannerShouldShow = !shouldHide;
        if (shouldHide) {
            hideBannerView(1);
            hideBannerView(2);
        } else {

            for (BannerListener bannerListener : mBannerListeners) {
                if (AdState.isShowing(bannerListener.mAdState)) {
                    return;
                }
            }

            for (int i = 0; i < mBannerListeners.length; i++) {
                if (AdState.isLoaded(mBannerListeners[i].mAdState)) {
                    showBannerView(i + 1);
                    hideBannerView(getAnotherBannerListenerIndex(i + 1));
                    postToShowAnotherBannerView(getAnotherBannerListenerIndex(i + 1));
                    return;
                }
            }
        }
    }

    @Override
    public void refreshBannerKeywords(String keywords, String userDataKeywords) {

        mMoPubView1.setKeywords(keywords);
        mMoPubView1.setUserDataKeywords(userDataKeywords);

        mMoPubView2.setKeywords(keywords);
        mMoPubView2.setUserDataKeywords(userDataKeywords);
    }

    @Override
    public void refreshBanner(String keywords, String userDataKeywords) {

        mMoPubView1.setKeywords(keywords);
        mMoPubView1.setUserDataKeywords(userDataKeywords);
        mMoPubView1.loadAd();

        mMoPubView2.setKeywords(keywords);
        mMoPubView2.setUserDataKeywords(userDataKeywords);
        mMoPubView2.loadAd();
    }

    @Override
    public void destroyBanner() {
        mHandler.removeCallbacksAndMessages(null);
        mMoPubView1.destroy();
        mMoPubView1 = null;

        mMoPubView2.destroy();
        mMoPubView2 = null;

    }

    @Override
    public void setAutoRefreshEnabled(boolean enabled) {
        // control the refresh logic in loader.
    }

    @Override
    public void forceRefresh() {
        mMoPubView1.forceRefresh();
        mMoPubView2.forceRefresh();
    }

    @Override
    public void setViewParameters(FrameLayout viewContainer, FrameLayout.LayoutParams layoutParameter) {
        mViewContainer = viewContainer;
        mLayoutParams = layoutParameter;
    }

    @Override
    public boolean isPluginReady() {
        return mMoPubView1 != null;
    }

    @Override
    public void setRefreshTime(int second) {
        mRefreshTime = second * ONE_SECOND;
    }

    @Override
    public MoPubView getMoPubView() {
        // 不确定一改传回那个banner实例。
        return null;
    }

    private class BannerListener implements MoPubView.BannerAdListener {

        final int mIndex;
        volatile int mAdState = AdState.IDLE;

        BannerListener(int index) {
            mIndex = index;
        }

        int getIndex() {
            return mIndex;
        }

        public BannerListener setAdState(int adState) {
            mAdState = adState;
            return this;
        }

        @Override
        public void onBannerLoaded(@NonNull MoPubView moPubView) {
            mLocalBannerListener.onBannerLoaded(moPubView);
            setAdState(AdState.LOADED);
            if (!mBannerShouldShow) {
                return;
            }

            if (mCurrentShownIndex == 0
                    || getIndex() == mCurrentShownIndex) {
                showBannerView(getIndex());
                hideBannerView(getAnotherBannerListenerIndex(getIndex()));
                postToShowAnotherBannerView(getAnotherBannerListenerIndex(getIndex()));
            } else {
                if (AdState.isShowing(mBannerListeners[getAnotherBannerListenerIndex(getIndex()) - 1].mAdState)) {
                    // 如果banner2加载成功，并且，banner1正在显示，则什么都不做，等待第一个banner的显示时间到之后，进行刷新
                    hideBannerView(getIndex());
                } else if (AdState.isFailed(mBannerListeners[getAnotherBannerListenerIndex(getIndex()) - 1].mAdState)
                        || AdState.isLoading(mBannerListeners[getAnotherBannerListenerIndex(getIndex()) - 1].mAdState)) {
                    // 如果banner2加载成功，banner1正在加载第二个广告｜加载失败，显示banner2，并且开始执行延迟方法

                    showBannerView(getIndex());
                    hideBannerView(getAnotherBannerListenerIndex(getIndex()));
                    postToShowAnotherBannerView(getAnotherBannerListenerIndex(getIndex()));
                }
            }
        }

        @Override
        public void onBannerFailed(MoPubView moPubView, MoPubErrorCode moPubErrorCode) {
            setAdState(AdState.FAILED);
            mLocalBannerListener.onBannerFailed(moPubView, moPubErrorCode);
            moPubView.setAutorefreshEnabled(true);
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

    private int getAnotherBannerListenerIndex(int targetIndex) {
        return targetIndex == 1 ? 2 : 1;
    }


    private void showBannerView(final int targetIndex) {
        runSafelyOnUiThread(new Runnable() {
            public void run() {
                if (AdState.isLoaded(mBannerListeners[targetIndex - 1].mAdState)) {
                    mCurrentShownIndex = targetIndex;
                    MoPubView moPubView = targetIndex == 1 ? mMoPubView1 : mMoPubView2;
                    moPubView.setVisibility(View.VISIBLE);
                    moPubView.setAutorefreshEnabled(true);
                    mBannerListeners[targetIndex - 1].setAdState(AdState.SHOWING);
                }
            }
        });
    }

    private void hideBannerView(final int targetIndex) {

        runSafelyOnUiThread(new Runnable() {
            public void run() {
                MoPubView moPubView = targetIndex == 1 ? mMoPubView1 : mMoPubView2;
                moPubView.setVisibility(View.INVISIBLE);
                if (AdState.isLoaded(mBannerListeners[targetIndex - 1].mAdState)) {
                    moPubView.setAutorefreshEnabled(false);
                }
            }
        });
    }

    private void postToShowAnotherBannerView(int targetIndex) {

        mHandler.removeMessages(MSG_SHOW_BANNER_VIEW);

        Message message = new Message();
        message.what = MSG_SHOW_BANNER_VIEW;
        message.arg1 = targetIndex;
        mHandler.sendMessageDelayed(message, mRefreshTime);
    }
}
