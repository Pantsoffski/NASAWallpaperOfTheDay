package pl.smartfan.nasawallpaperoftheday;

import android.app.WallpaperManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements AsyncResponse {
    private InputStream nasaLeeched = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: 16.11.2017 set wallpaper with leeched photo
        try {
            nasaLeech();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            nasaLeeched = null;
        }
    }

    private void nasaLeech() throws MalformedURLException {
        //URL to send to AsyncTask
        URL url = new URL("https://api.nasa.gov/planetary/apod?api_key=GmIPSectIKdfHDCcnoFZpupFfex71nm9WODSejKu");

        //Instantiate new instance of GetDataAsyncTask class
        GetDataAsyncTask getRequest = new GetDataAsyncTask();

        getRequest.delegate = this;

        //Perform the doInBackground method, passing in our url
        getRequest.execute(url); // TODO: 17.11.2017 try to remove get() and do it only via execute()
    }

    @Override
    public void processFinish(InputStream output) {
        nasaLeeched = output;
        //Log.v("Result:", nasaLeeched);

        WallpaperManager wpm = WallpaperManager.getInstance(this);
        try {
            wpm.setStream(nasaLeeched); // TODO: 20.11.2017 fix NetworkOnMainThreadException 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
