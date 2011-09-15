package com.cyrilpottiers.androlib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public final class Log {
    private final static int MAX_LENGTH = (4 * 1024 - 512);
    private static boolean   LOGGING    = true;

    public static void print(int type, String tag, String message) {
        switch (type) {
            case android.util.Log.DEBUG:
                android.util.Log.d(tag, message);
                break;
            case android.util.Log.ERROR:
                android.util.Log.e(tag, message);
                break;
            case android.util.Log.INFO:
                android.util.Log.i(tag, message);
                break;
            case android.util.Log.VERBOSE:
                android.util.Log.v(tag, message);
                break;
            case android.util.Log.WARN:
                android.util.Log.w(tag, message);
                break;
        }
    }

    public static void compute(int type, String tag, String message) {
        if (!LOGGING) return;

        // Split message
        int i = 0;
        StringBuilder sb = null;
        String prefix = "";
        while (true) {
            sb = new StringBuilder();
            if (message.length() - i < MAX_LENGTH) {
                print(type, tag, sb.append(prefix).append(message.substring(i)).toString());
                return;
            }
            else {
                print(type, tag, sb.append(prefix).append(message.substring(i, i + MAX_LENGTH)).toString());
                prefix = "      ";
                i += MAX_LENGTH;
            }
        }
    }

    public static void initialize(Context context) {
        // See if we're a debug or a release build
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (packageInfo.signatures.length > 0) {
                String signature = new String(packageInfo.signatures[0].toByteArray());
                LOGGING = signature.contains("Android Debug");
            }
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static boolean isDebugging() {
        return LOGGING;
    }

    public static void d(String tag, String message) {
        compute(android.util.Log.DEBUG, tag, message);
    }

    public static void e(String tag, String message) {
        compute(android.util.Log.ERROR, tag, message);
    }

    public static void i(String tag, String message) {
        compute(android.util.Log.INFO, tag, message);
    }

    public static void v(String tag, String message) {
        compute(android.util.Log.VERBOSE, tag, message);
    }

    public static void w(String tag, String message) {
        compute(android.util.Log.WARN, tag, message);
    }

}
