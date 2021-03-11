package com.adsdk.manager;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NativeLayoutData {
    ViewBinder mMopubBinder;
    ViewBinder mGoogleBinder;
    ViewBinder mFacebookBinder;
    ViewBinder mSmaatoBinder;

    public NativeLayoutData(ViewBinder mopubBinder, ViewBinder googleBinder, ViewBinder facebookBinder, ViewBinder smaatoBinder) {
        mMopubBinder = mopubBinder;
        mGoogleBinder = googleBinder;
        mFacebookBinder = facebookBinder;
        mSmaatoBinder = smaatoBinder;
    }

    public ViewBinder getMopubBinder() {
        return mMopubBinder;
    }

    public ViewBinder getGoogleBinder() {
        return mGoogleBinder;
    }

    public ViewBinder getFacebookBinder() {
        return mFacebookBinder;
    }

    public ViewBinder getSmaatoBinder() {
        return mSmaatoBinder;
    }

    public static class ViewBinder {
        final int layoutId;
        final int titleId;
        final int textId;
        final int callToActionId;
        final int mainImageId;
        final int iconImageId;
        final int privacyInformationIconImageId;

        ViewBinder(int layoutId, int titleId,
                   int textId, int callToActionId,
                   int mainImageId, int iconImageId,
                   int privacyInformationIconImageId) {
            this.layoutId = layoutId;
            this.titleId = titleId;
            this.textId = textId;
            this.callToActionId = callToActionId;
            this.mainImageId = mainImageId;
            this.iconImageId = iconImageId;
            this.privacyInformationIconImageId = privacyInformationIconImageId;
        }

        public int getLayoutId() {
            return layoutId;
        }

        public int getTitleId() {
            return titleId;
        }

        public int getTextId() {
            return textId;
        }

        public int getCallToActionId() {
            return callToActionId;
        }

        public int getMainImageId() {
            return mainImageId;
        }

        public int getIconImageId() {
            return iconImageId;
        }

        public int getPrivacyInformationIconImageId() {
            return privacyInformationIconImageId;
        }
    }


    public final static class NativeViewBuilder {
        private final int layoutId;
        private int titleId;
        private int textId;
        private int callToActionId;
        private int mainImageId;
        private int iconImageId;
        private int privacyInformationIconImageId;

        @NonNull
        private Map<String, Integer> extras = Collections.emptyMap();

        public NativeViewBuilder(int layoutId) {
            this.layoutId = layoutId;
            this.extras = new HashMap();
        }

        @NonNull
        public final NativeViewBuilder titleId(int titleId) {
            this.titleId = titleId;
            return this;
        }

        @NonNull
        public final NativeViewBuilder textId(int textId) {
            this.textId = textId;
            return this;
        }

        @NonNull
        public final NativeViewBuilder callToActionId(int callToActionId) {
            this.callToActionId = callToActionId;
            return this;
        }

        @NonNull
        public final NativeViewBuilder mainImageId(int mediaLayoutId) {
            this.mainImageId = mediaLayoutId;
            return this;
        }

        @NonNull
        public final NativeViewBuilder iconImageId(int iconImageId) {
            this.iconImageId = iconImageId;
            return this;
        }

        @NonNull
        public final NativeViewBuilder adChoice(int adChoiceLayoutId) {
            this.privacyInformationIconImageId = adChoiceLayoutId;
            return this;
        }

//        @NonNull
//        public final NativeViewBuilder addExtras(Map<String, Integer> resourceIds) {
//            this.extras = new HashMap(resourceIds);
//            return this;
//        }
//
//        @NonNull
//        public final NativeViewBuilder addExtra(String key, int resourceId) {
//            this.extras.put(key, resourceId);
//            return this;
//        }

        @NonNull
        public final ViewBinder build() {
            return new ViewBinder(layoutId, titleId, textId, callToActionId, mainImageId, iconImageId, privacyInformationIconImageId);
        }
    }

}
