package pl.smartfan.nasawallpaperoftheday;

import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.RemoteViews;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Class with tools to set and verify some things.
 */

public class Utils {

    private Context context;

    Utils(Context context) {
        this.context = context;
    }

    //Setting widget data
    void setWidget(String textForWidget) {
        //Prepare text for widget (cut it at 200)
        textForWidget = textForWidget.substring(0, 200) + "...";

        //Start and fill widget with leeched text
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        ComponentName thisWidget = new ComponentName(context, AppWidget.class);
        remoteViews.setTextViewText(R.id.appWidgetText, textForWidget);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    void setWallpaper(Bitmap bitmap) {
        if (bitmap != null) { //if there is results
            WallpaperManager wpm = WallpaperManager.getInstance(context);
            try {
                wpm.setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //method responsible for receiving date in YYYY-MM-DD format
    String getDateForUrl(int minusDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -minusDays); //for test: minus 50 days from current date
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(calendar.getTime());
    }

    //method responsible for detecting internet availability
    boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class PreferencesSaveGet {
        SharedPreferences preferences = context.getSharedPreferences("pl.smartfan.nasawallpaperoftheday", Context.MODE_PRIVATE);

        //save last downloaded explanation, date, title and url
        void savePreferences(String url, String explanation, String title, String copyright, String date) {
            preferences.edit().putString("url", url).putString("explanation", explanation).putString("title", title).putString("copyright", copyright).putString("date", date).apply();
        }

        //get preferences
        String[] getPreferences() {
            String[] stringsToReturn = new String[]{
                    preferences.getString("url", ""),
                    preferences.getString("explanation", ""),
                    preferences.getString("title", ""),
                    preferences.getString("copyright", ""),
                    preferences.getString("date", "")
            };
            return stringsToReturn;
        }
    }
}
