package id.co.motion.autorun;

import android.app.LauncherActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootedBroadcastReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, LauncherService.class));
    }

}
