package io.manticore.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import static io.manticore.android.App.getAppContext;

public class TimeUtils {
    private static final String PREF_SCANNER_TIMEOUT = "pref_scanner_timeout";

    private static SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getAppContext());
    }

    public static double getScannerTimeout() {
        switch (getSharedPreferences().getString(PREF_SCANNER_TIMEOUT, "0")) {
            case "0":
                return 0.25;
            case "1":
                return 0.50;
            case "2":
                return 0.75;
            case "3":
                return 1.00;
        }

        return 0.75;
    }
}
