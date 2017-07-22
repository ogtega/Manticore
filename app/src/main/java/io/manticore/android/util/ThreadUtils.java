package io.manticore.android.util;

public class ThreadUtils {
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    public static int getCores() {
        return NUMBER_OF_CORES;
    }
}
