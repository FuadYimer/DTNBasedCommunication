
package org.disrupted.ibits.util;

import org.disrupted.ibits.app.RumbleApplication;

/**
 * @author
 */
public class Log {

    public static void d(String tag, String message) {
        if(RumblePreferences.isLogcatDebugEnabled(RumbleApplication.getContext()))
            android.util.Log.d(tag,message);
    }

    public static void d(String tag, String message, Throwable t) {
        if(RumblePreferences.isLogcatDebugEnabled(RumbleApplication.getContext()))
            android.util.Log.d(tag,message,t);
    }

    public static void e(String tag, String message) {
        if(RumblePreferences.isLogcatDebugEnabled(RumbleApplication.getContext()))
            android.util.Log.e(tag,message);
    }

    public static void e(String tag, String message, Throwable t) {
        if(RumblePreferences.isLogcatDebugEnabled(RumbleApplication.getContext()))
            android.util.Log.e(tag,message,t);
    }
}
