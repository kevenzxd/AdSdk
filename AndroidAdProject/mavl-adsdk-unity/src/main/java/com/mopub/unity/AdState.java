package com.mopub.unity;

class AdState {
    public final static int IDLE        = 0;
    public final static int LOADING     = 1;
    public final static int LOADED      = 2;
    public final static int FAILED      = 3;
    public final static int SHOWING     = 4;
    public final static int HIDE        = 5;
    public final static int DESTROY     = 6;

    public static boolean isLoaded(int state) {
        return state == LOADED;
    }

    public static boolean isShowing(int state) {
        return state == SHOWING;
    }

    public static boolean isFailed(int state) {
        return state == FAILED;
    }

    public static boolean isLoading(int state) {
        return state == FAILED;
    }
}
