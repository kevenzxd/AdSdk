package com.mopub.mobileads;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.mopub.nativeads.FacebookAdRenderer;
import com.mopub.nativeads.GooglePlayServicesViewBinder;
import com.mopub.nativeads.MediaViewBinder;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.ViewBinder;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaocong on 16/6/17.
 */
public class MoPubNativeAd {

    public static class Builder {
        WeakReference<Context> mContext;
        MoPubNative.MoPubNativeNetworkListener listener;
        Map<String, Object> mLocalExtras = new HashMap<>();
        String adId;

        ViewBinder.Builder staticBuilder;
        FacebookAdRenderer.FacebookViewBinder.Builder fanBuilder;
        GooglePlayServicesViewBinder.Builder googleBuilder;
        MediaViewBinder.Builder smaatoBuilder;

        public Builder() {
        }

        public Builder withExtra(@NonNull String key, Object value) {
            mLocalExtras.put(key, value);
            return this;
        }

        public Builder withActivity(@NonNull Context context) {
            this.mContext = new WeakReference<>(context);
            return this;
        }

        public Builder withAdId(@NonNull String id) {
            adId = id;
            return this;
        }

        public Builder networkListener(@NonNull MoPubNative.MoPubNativeNetworkListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder staticRenderer(@LayoutRes int layout,
                                    @IdRes int mainImageId,
                                    @IdRes int iconImageId,
                                    @IdRes int titleId,
                                    @IdRes int bodyId,
                                    @IdRes int callToActionId,
                                    @IdRes int adChoiceId) {
            staticBuilder = new ViewBinder.Builder(layout);
            staticBuilder.mainImageId(mainImageId);
            staticBuilder.iconImageId(iconImageId);
            staticBuilder.titleId(titleId);
            staticBuilder.textId(bodyId);
            staticBuilder.callToActionId(callToActionId);
            staticBuilder.privacyInformationIconImageId(adChoiceId);
            return this;
        }

        public Builder fanRenderer(@LayoutRes int layout,
                                      @IdRes int mainImageId,
                                      @IdRes int iconImageId,
                                      @IdRes int titleId,
                                      @IdRes int bodyId,
                                      @IdRes int callToActionId,
                                      @IdRes int adChoiceId) {
            fanBuilder = new FacebookAdRenderer.FacebookViewBinder.Builder(layout);
            fanBuilder.adChoicesRelativeLayoutId(adChoiceId);
            fanBuilder.adIconViewId(iconImageId);
            fanBuilder.callToActionId(callToActionId);
            fanBuilder.mediaViewId(mainImageId);
            fanBuilder.titleId(titleId);
            fanBuilder.textId(bodyId);
            if (mainImageId == 0) {
                mLocalExtras.put("native_banner",true);
            }
            return this;
        }

        public Builder googleRenderer(@LayoutRes int layout,
                                      @IdRes int mainImageId,
                                      @IdRes int iconImageId,
                                      @IdRes int titleId,
                                      @IdRes int bodyId,
                                      @IdRes int callToActionId) {
            googleBuilder = new GooglePlayServicesViewBinder.Builder(layout);
            googleBuilder.callToActionId(callToActionId);
            googleBuilder.iconImageId(iconImageId);
            googleBuilder.mediaLayoutId(mainImageId);
            googleBuilder.textId(bodyId);
            googleBuilder.titleId(titleId);

            return this;
        }


        public Builder smaatoRenderer(@LayoutRes int layout,
                                      @IdRes int mainImageId,
                                      @IdRes int iconImageId,
                                      @IdRes int titleId,
                                      @IdRes int bodyId,
                                      @IdRes int callToActionId,
                                      @IdRes int adChoiceId) {
            smaatoBuilder = new MediaViewBinder.Builder(layout);
            smaatoBuilder.mediaLayoutId(mainImageId);
            smaatoBuilder.iconImageId(iconImageId);
            smaatoBuilder.titleId(titleId);
            smaatoBuilder.textId(bodyId);
            smaatoBuilder.callToActionId(callToActionId);
            smaatoBuilder.privacyInformationIconImageId(adChoiceId);
            return this;
        }

        public MoPubNative build() {

            if (staticBuilder == null || mContext == null || TextUtils.isEmpty(adId) || listener == null)
                return null;

            Context context = mContext.get();
            if (context == null) {
                return null;
            }


            MoPubNative moPubNative = new MoPubNative(context, adId, listener);

            if (fanBuilder != null) {
                moPubNative.registerAdRenderer(NativeRenderFactory.getFactory().getRender(NativeRenderFactory.ENUM_RENDER.FAN_ST, fanBuilder.build()));
            }

            if (googleBuilder != null) {
                moPubNative.registerAdRenderer(NativeRenderFactory.getFactory().getRender(NativeRenderFactory.ENUM_RENDER.GOOGLE_CONTENT, googleBuilder.build()));
            }

            if (smaatoBuilder != null) {
                moPubNative.registerAdRenderer(NativeRenderFactory.getFactory().getRender(NativeRenderFactory.ENUM_RENDER.SMAATO, smaatoBuilder.build()));
            }

            moPubNative.registerAdRenderer(NativeRenderFactory.getFactory().getRender(NativeRenderFactory.ENUM_RENDER.MOPUB_ST, staticBuilder.build()));

            moPubNative.setLocalExtras(mLocalExtras);

            return moPubNative;
        }
    }

}
