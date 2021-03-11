package com.mopub.mobileads;

import com.mopub.common.logging.MoPubLog;
import com.mopub.common.util.Reflection;
import com.mopub.nativeads.FacebookAdRenderer;
import com.mopub.nativeads.GooglePlayServicesAdRenderer;
import com.mopub.nativeads.GooglePlayServicesViewBinder;
import com.mopub.nativeads.MoPubAdRenderer;
import com.mopub.nativeads.ViewBinder;

/**
 * Created by xidongzhang on 2018/5/21.
 */

public class NativeRenderFactory {

    private static final String MOPUB_ST_CLASS = "com.mopub.nativeads.MoPubStaticNativeAdRenderer";
    private static final String FAN_ST_CLASS = "com.mopub.nativeads.FacebookAdRenderer";
    private static final String GOOGLE_CONTENT_CLASS = "com.mopub.nativeads.GooglePlayServicesAdRenderer";

    private static final String FLURRY_ST_CLASS = "com.mopub.nativeads.FlurryStaticNativeAdRenderer";
    private static final String FLURRY_MEDIA_CLASS = "com.mopub.nativeads.FlurryNativeAdRenderer";
    private static final String VERIZON_CLASS = "com.mopub.nativeads.VerizonNativeAdRenderer";
    private static final String CRITEO_CLASS = "com.criteo.mediation.mopub.advancednative.CriteoNativeEventRenderer";

    public enum ENUM_RENDER {
        MOPUB_ST, MOPUB_MEDIA, FAN_ST, FAN_MEDIA, GOOGLE_INSTALL, GOOGLE_CONTENT, FLURRY_ST, FLURRY_MEDIA, VERIZON, CRITEO, SMAATO
    }

    private static NativeRenderFactory mFactory = new NativeRenderFactory();

    public static NativeRenderFactory getFactory() {
        return mFactory;
    }

    public MoPubAdRenderer getRender(ENUM_RENDER render, Object binder) {
        MoPubAdRenderer renderer = null;
        switch (render) {
            case MOPUB_ST:
                renderer = createMopubSt((ViewBinder)binder);
                break;
            case FAN_ST:
                renderer = createFANSt((FacebookAdRenderer.FacebookViewBinder)binder);
                break;
            case FAN_MEDIA:
                break;
            case GOOGLE_INSTALL:
                break;
            case GOOGLE_CONTENT:
                renderer = createGoogleSt((GooglePlayServicesViewBinder)binder);
                break;
            case FLURRY_ST:
                renderer = createFlurrySt((ViewBinder)binder);
                break;
            case FLURRY_MEDIA:
                renderer = createFlurryMedia((ViewBinder)binder);
                break;
            case VERIZON:
                renderer = createVerizon((ViewBinder)binder);
                break;
            case CRITEO:
                renderer = createCriteo((ViewBinder)binder);
                break;
            default:

                break;
        }
        return renderer;
    }

    private MoPubAdRenderer createMopubSt(ViewBinder binder) {
        return createBase(MOPUB_ST_CLASS, binder);
    }

    private MoPubAdRenderer createFANSt(FacebookAdRenderer.FacebookViewBinder binder) {
        return createFacebookRender(FAN_ST_CLASS, binder);
    }

    private MoPubAdRenderer createGoogleSt(GooglePlayServicesViewBinder binder) {
        return createGoogleRender(GOOGLE_CONTENT_CLASS, binder);
    }
    private MoPubAdRenderer createFlurrySt(ViewBinder binder) {
        return createBase(FLURRY_ST_CLASS, binder);
    }

    private MoPubAdRenderer createFlurryMedia(ViewBinder binder) {
        return createBase(FLURRY_MEDIA_CLASS, binder);
    }

    private MoPubAdRenderer createVerizon(ViewBinder binder) {
        return createBase(VERIZON_CLASS, binder);
    }

    private MoPubAdRenderer createCriteo(ViewBinder binder) {
        return createBase(CRITEO_CLASS, binder);
    }

    private MoPubAdRenderer createGoogleRender(final String className, GooglePlayServicesViewBinder binder) {
        return new GooglePlayServicesAdRenderer(binder);
    }

    private MoPubAdRenderer createFacebookRender(final String className, FacebookAdRenderer.FacebookViewBinder binder) {
        return new FacebookAdRenderer(binder);
    }

    private MoPubAdRenderer createBase(final String className, ViewBinder binder) {
        MoPubAdRenderer renderer = null;
        if (Reflection.classFound(className)) {
            try {
                renderer = (MoPubAdRenderer) Reflection.instantiateClassWithConstructor(
                        className, Object.class,
                        new Class[] {ViewBinder.class},
                        new Object[] {binder});
            } catch (Exception e) {
                MoPubLog.w("Error loading custom event", e);
            }
        } else {
            MoPubLog.i("Could not load custom event -- missing "+className+" module");
        }
        return renderer;
    }
}
