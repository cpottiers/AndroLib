package com.cyrilpottiers.androlib.dialog;

import java.util.MissingResourceException;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.cyrilpottiers.androlib.R;

public class SplashScreenDialog extends Dialog {

    private static final int  SHOW_WAITING     = 0;
    private static final int  VERIFY_LOADING   = 1;
    private static final long SPLASH_TIME      = 1000;
    private static final long CHECK_TIME       = 100;

    Boolean                   forceCloseSplash = Boolean.FALSE;
    Boolean                   isOver           = Boolean.FALSE;

    boolean                   hasProgress      = false;

    private Handler           splashHandler    = new Handler() {
                                                   @Override
                                                   public void handleMessage(
                                                           Message msg) {
                                                       switch (msg.what) {
                                                           case SHOW_WAITING: {
                                                               synchronized (SplashScreenDialog.this.forceCloseSplash) {
                                                                   if (!SplashScreenDialog.this.forceCloseSplash) {
                                                                       ProgressBar spin = (ProgressBar) findViewById(android.R.id.progress);
                                                                       if (hasProgress
                                                                           && spin != null)
                                                                           spin.setVisibility(View.VISIBLE);
                                                                   }
                                                               }
                                                           }
                                                               break;
                                                           case VERIFY_LOADING: {
                                                               if (SplashScreenDialog.this.isOver) {
                                                                   SplashScreenDialog.this.dismiss();
                                                               }
                                                               else {
                                                                   if (!SplashScreenDialog.this.forceCloseSplash)
                                                                       splashHandler.sendMessageDelayed(Message.obtain(msg), CHECK_TIME);
                                                               }
                                                           }
                                                               break;
                                                       }
                                                       super.handleMessage(msg);
                                                   }
                                               };

    public SplashScreenDialog(Context context, int resId) {
        super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        constructor(context, resId, Color.BLACK, false);
    }

    public SplashScreenDialog(Context context, int resId, boolean hasProgress) {
        super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        constructor(context, resId, Color.BLACK, hasProgress);
    }

    public SplashScreenDialog(Context context, int resId, int colorId) {
        super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        constructor(context, resId, colorId, false);
    }

    public SplashScreenDialog(Context context, int resId, int colorId,
            boolean hasProgress) {
        super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        constructor(context, resId, colorId, hasProgress);
    }

    private void constructor(Context context, int resId, int colorId,
            boolean hasProgress) {
        if (resId < 0)
            throw new MissingResourceException("resId not present in Bundle", SplashScreenDialog.class.getName(), "resId");

        synchronized (forceCloseSplash) {
            forceCloseSplash = Boolean.FALSE;
        }

        setContentView(R.layout.splashscreenactivitylayout);

        FrameLayout bg = (FrameLayout) findViewById(android.R.id.background);
        bg.setBackgroundColor(colorId);

        ImageView logo = (ImageView) findViewById(android.R.id.icon);
        logo.setImageResource(resId);
        logo.setScaleType(ScaleType.FIT_XY);

        ProgressBar spin = (ProgressBar) findViewById(android.R.id.progress);
        spin.setIndeterminate(true);
        spin.setVisibility(hasProgress?View.INVISIBLE:View.GONE);

        setCancelable(false);
    }

    @Override
    public void onStart() {
        // Show waiting dialog
        Message msg = new Message();
        msg.what = SHOW_WAITING;
        splashHandler.sendMessageDelayed(msg, SPLASH_TIME);

        // we launch the check thread
        msg = new Message();
        msg.what = VERIFY_LOADING;
        splashHandler.sendMessageDelayed(msg, CHECK_TIME);
    }

    @Override
    public void onStop() {
        // hide splash
        synchronized (SplashScreenDialog.this.forceCloseSplash) {
            forceCloseSplash = Boolean.TRUE;
        }
    }

    public void finalize() {
        // hide splash
        synchronized (SplashScreenDialog.this.isOver) {
            isOver = Boolean.TRUE;
        }
    }
}
