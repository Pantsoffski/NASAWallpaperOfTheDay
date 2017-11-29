package pl.smartfan.nasawallpaperoftheday;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class {@link GetDataAsyncTask} is a AsyncTask to leech JSON data from NASA.
 */

public class GetDataAsyncTask extends AsyncTask<URL, Void, Object[]> {

    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 30000;
    private static final int CONNECTION_TIMEOUT = 30000;
    AsyncResponse delegate = null;

    @Override
    protected Object[] doInBackground(URL... urls) {

        Object[] results = new Object[5];
        InputStreamReader streamReader;

        try {
            //Create a URL object holding our url
            URL myUrl = urls[0];

            //Create a connection
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();

            //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //Connect to our url
            connection.connect();

            //Create a new InputStreamReader
            streamReader = new InputStreamReader(connection.getInputStream());

            //Create WallpaperCreator object
            WallpaperCreator wallpaperCreator = new WallpaperCreator();

            //Get InputStream (wallpaper image) from desired URL
            String[] stringsFromWallpaperCreator = wallpaperCreator.readJsonStream(streamReader);
            InputStream inputStream = new URL(stringsFromWallpaperCreator[2]).openStream();

            //Decode stream to Bitmap
            results[0] = BitmapFactory.decodeStream(inputStream); //image
            results[1] = stringsFromWallpaperCreator[1]; //explanation
            results[2] = stringsFromWallpaperCreator[3]; //title
            results[3] = stringsFromWallpaperCreator[0]; //copyright
            results[4] = stringsFromWallpaperCreator[4]; //date

        } catch (IOException e) {
            e.printStackTrace();
            results = null;
        }

        return results;
    }

    @Override
    protected void onPostExecute(Object[] results) {
        //Call interface
        delegate.processFinish(results);
    }
}
