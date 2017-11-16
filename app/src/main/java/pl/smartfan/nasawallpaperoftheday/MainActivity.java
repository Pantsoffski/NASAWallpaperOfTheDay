package pl.smartfan.nasawallpaperoftheday;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String nasaLeeched = nasaLeech(); // TODO: 16.11.2017 set wallpaper with leeched photo

        Log.v("Result:", nasaLeeched); //tested and it works
    }

    private String nasaLeech() {
        //String to place our result in
        String result;

        //Instantiate new instance of our class
        GetDataAsyncTask getRequest = new GetDataAsyncTask();

        //Perform the doInBackground method, passing in our url
        try {
            result = getRequest.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            result = "";
        }

        return result;
    }
}
