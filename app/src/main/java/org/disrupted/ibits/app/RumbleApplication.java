
package org.disrupted.ibits.app;

import android.app.Application;
import android.content.Context;

import org.disrupted.ibits.database.DatabaseFactory;
import org.disrupted.ibits.database.statistics.StatisticManager;
import org.disrupted.ibits.database.CacheManager;

/**
 * @author L
 */
public class RumbleApplication extends Application{

    public static String BUILD_VERSION = "FOUCAULT";
    public static String BUILD_NUMBER = "1.0";

    private static RumbleApplication instance;

    public RumbleApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EventLogger.getInstance().init();
        DatabaseFactory.getInstance(this);
        CacheManager.getInstance().start();
        StatisticManager.getInstance().start();
    }

    public static Context getContext() {
        return instance;
    }

    public static RumbleApplication getApplication() {
        return instance;
    }
}
