package org.disrupted.ibits.app;

import android.util.Log;



import org.disrupted.ibits.util.RumblePreferences;

import de.greenrobot.event.EventBus;

/**
 * @author
 */
public class EventLogger {

    public static final String TAG = "EventLogger";

    private static EventLogger logger;

    private EventLogger() {
    }

    public static EventLogger getInstance() {
        if (logger == null)
            logger = new EventLogger();
        return logger;
    }

    public void init() {
        if(RumblePreferences.isLogcatDebugEnabled(RumbleApplication.getContext())) {
            logger.start();
        } else {
            logger.stop();
        }
    }

    private void start() {
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this,10);
    }

    private void stop() {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
    
    public void onEvent(RumbleEvent event) {
        Log.d(TAG, "---> "+event.getClass().getSimpleName()+" : "+event.shortDescription());
    }

}
