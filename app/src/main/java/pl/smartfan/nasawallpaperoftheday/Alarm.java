package pl.smartfan.nasawallpaperoftheday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Class {@link Alarm} is responsible for periodically running up wallpaper changing task.
 */

public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = null;
        if (pm != null) {
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        }
        if (wl != null) {
            wl.acquire(10 * 60 * 1000L /*10 minutes*/);
        }

        // here what to do when alarm triggers
        //Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show();
        //checking is there internet connection available
        /*if (isNetworkAvailable()) { // TODO: 27.11.2017  it need to leech data from nasa and set new wallpaper
            try {
                nasaLeech(0);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            alertMe((String) getText(R.string.alert_message_no_internet));
        }*/

        if (wl != null) {
            wl.release();
        }
    }

    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        if (am != null) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000L * 60, pi); // Millisec * Second * Minute
        }
    }

    /*public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }*/
}
