package com.mopub.unity;

import android.content.Context;

class AdLoaderFactory {
    protected static AdLoaderFactory instance = new AdLoaderFactory();

    public static AdLoader createRefreshLoader(Context context, String adId) {
        return instance.internalCreateBanner(context, adId);
    }

    public static AdLoader createStaticLoader(Context context, String adId) {
        return instance.internalCreateMrec(context, adId);
    }

    protected AdLoader internalCreateBanner(Context context, String adId) {
        return new BannerRefreshLoader(context, adId);
    }

    protected AdLoader internalCreateMrec(Context context, String adId) {
        return new BannerStaticLoader(context, adId);
    }
}
