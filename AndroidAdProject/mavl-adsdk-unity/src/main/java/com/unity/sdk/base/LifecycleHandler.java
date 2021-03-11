package com.unity.sdk.base;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

public class LifecycleHandler extends Handler implements FullLifecycleObserver {

    private static final String TAG = "LifecycleHandler";
    private LifecycleOwner mLifecycleOwner;

    public LifecycleHandler(LifecycleOwner lifecycleOwner) {
        mLifecycleOwner = lifecycleOwner;
        addObserver();
    }

    public LifecycleHandler(LifecycleOwner lifecycleOwner, Callback callback) {
        super(callback);
        mLifecycleOwner = lifecycleOwner;
        addObserver();
    }

    public LifecycleHandler(LifecycleOwner lifecycleOwner, Looper looper) {
        super(looper);
        mLifecycleOwner = lifecycleOwner;
        addObserver();
    }

    public LifecycleHandler(LifecycleOwner lifecycleOwner, Looper looper, Callback callback) {
        super(looper, callback);
        mLifecycleOwner = lifecycleOwner;
        addObserver();
    }

    private void addObserver() {
        if (mLifecycleOwner != null) {
            mLifecycleOwner.getLifecycle().addObserver(new FullLifecycleObserverAdapter(mLifecycleOwner, this));
        }
    }

    @Override
    public void onCreate(LifecycleOwner owner) {
        Log.i(TAG, "onCreate: owner = " + owner);
    }

    @Override
    public void onStart(LifecycleOwner owner) {
        Log.i(TAG, "onStart: owner = " + owner);
    }

    @Override
    public void onResume(LifecycleOwner owner) {
        Log.i(TAG, "onResume: owner = " + owner);
    }

    @Override
    public void onPause(LifecycleOwner owner) {
        Log.i(TAG, "onPause: owner = " + owner);
    }

    @Override
    public void onStop(LifecycleOwner owner) {
        Log.i(TAG, "onStop: owner = " + owner);
    }

    @Override
    public void onDestroy(LifecycleOwner owner) {
        Log.i(TAG, "onDestroy: owner = " + owner);
        removeCallbacksAndMessages(null);
    }
}
