package id.co.motion.autorun;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Iterator;

public class LauncherService extends Service {
    @Override
    public IBinder onBind(Intent p2) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new AsyncTask<Service, Void, Service>() {
            protected Service doInBackground(Service... param) {
                Service svc = param[0];
                Context ctx = svc.getApplicationContext();
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
                HashSet cur_data = (HashSet) pref.getStringSet(MainActivity.CONF_DATA, null);
                if (cur_data != null) {
                    PackageManager pm = svc.getPackageManager();
                    HashSet err_data = null;
                    Iterator itt = cur_data.iterator();
                    while (itt.hasNext()) {
                        String pkg_name = (String) itt.next();
                        try {
                            Intent itn = pm.getLaunchIntentForPackage(pkg_name);
                            if (itn != null) {
                                svc.startActivity(itn);
                                synchronized (this) {
                                    wait(3000);
                                }
                            } else {
                                throw new ActivityNotFoundException();
                            }
                        } catch (ActivityNotFoundException exc) {
                            if (err_data == null) err_data = new HashSet();
                            err_data.add(pkg_name);
                        } catch (InterruptedException exc) {
                        }
                    }

                    if (err_data != null) {
                        HashSet upd_data = new HashSet(cur_data);
                        upd_data.removeAll(err_data);
                        pref.edit().putStringSet(MainActivity.CONF_DATA, upd_data).apply();
                        cur_data = upd_data;
                    }
                }
                return svc;
            }

            protected void onPostExecute(Service svc) {
                svc.stopSelf();
            }
        }.execute(this);

        return START_STICKY;
    }
}