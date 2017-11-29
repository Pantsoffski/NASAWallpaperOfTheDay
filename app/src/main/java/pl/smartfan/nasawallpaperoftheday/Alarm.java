package pl.smartfan.nasawallpaperoftheday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.PowerManager;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

/**
 * Class {@link Alarm} is responsible for periodically running up wallpaper changing task.
 */

public class Alarm extends BroadcastReceiver implements AsyncResponse {

    Context context;
    Intent intent;
    Utils utils;
    private Integer randomDatesCounter = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
        utils = new Utils(context);

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = null;
        if (pm != null) {
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        }
        if (wl != null) {
            wl.acquire(10 * 60 * 1000L /*10 minutes*/);
        }

        nasaLeech(0);

        if (wl != null) {
            wl.release();
        }
    }

    private void nasaLeech(int minusDays) {
        //URL to send to AsyncTask with custom date
        URL url = null;
        try {
            Random r = new Random();//to remove
            int randomMinusDays = r.nextInt(1000 - 1) + 1;//to remove
            //change randomMinusDays to minusDays after removal
            url = new URL("https://api.nasa.gov/planetary/apod?date=" + utils.getDateForUrl(randomMinusDays) + "&hd=True&api_key=GmIPSectIKdfHDCcnoFZpupFfex71nm9WODSejKu");

            //class PreferencesSaveGet needs an enclosing instance to be instantiated
            Utils.PreferencesSaveGet prefs = new Utils(null).new PreferencesSaveGet();
            //save url to prefs
            prefs.savePreferences(url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //Instantiate new instance of GetDataAsyncTask class
        GetDataAsyncTask getRequest = new GetDataAsyncTask();

        getRequest.delegate = this;

        if (utils.isNetworkAvailable()) {
            //Perform the doInBackground method, passing in our url
            getRequest.execute(url);
        }
    }

    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        if (am != null) {
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000L * 60 /* 60 * 5*/, pi); // Millis * Second * Minutes * Hours
        }
    }

    @Override
    public void processFinish(Object[] results) {
        if (results != null) { //if there is results
            //Set image as wallpaper
            utils.setWallpaper((Bitmap) results[0]);

            //Fill widget with data
            utils.setWidget((String) results[1]);

            //Show toast
            Toast.makeText(context, R.string.toast_loaded_reloaded, Toast.LENGTH_LONG).show();
        } else { //if there is no results from AsyncTask - get nasa JSON from randomized date
            if (randomDatesCounter < 6) {
                Random r = new Random();
                int randomMinusDays = r.nextInt(1000 - 1) + 1;
                nasaLeech(randomMinusDays);
                randomDatesCounter++;
            } else {
                //Show toast
                Toast.makeText(context, R.string.alert_message_no_data, Toast.LENGTH_LONG).show();
            }
        }

    /*public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }*/
    }
}
