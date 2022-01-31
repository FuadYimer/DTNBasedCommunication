package org.disrupted.ibits.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.disrupted.ibits.database.DatabaseFactory;
import org.disrupted.ibits.network.NetworkCoordinator;
import org.disrupted.ibits.util.RumblePreferences;

/**
 * @author
 */
public class StartOnBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if(!RumblePreferences.startOnBoot(context))
                return;

            if(DatabaseFactory.getContactDatabase(context).getLocalContact() != null) {
                Intent startIntent = new Intent(context, NetworkCoordinator.class);
                startIntent.setAction(NetworkCoordinator.ACTION_START_FOREGROUND);
                context.startService(startIntent);
            }
        }
    }
}