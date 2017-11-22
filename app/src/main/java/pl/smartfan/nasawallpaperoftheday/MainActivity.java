package pl.smartfan.nasawallpaperoftheday;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    private Bitmap nasaLeeched = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        getRequest.execute(url);
    }

    @Override
    public void processFinish(Object[] results) {
        nasaLeeched = (Bitmap) results[0]; // TODO: 22.11.2017 modify AsyncTask and AsyncResponse interface to get explanation and title (beside hdurl) from JSON

        WallpaperManager wpm = WallpaperManager.getInstance(this);
        try {
            wpm.setBitmap(nasaLeeched);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //this is for future functions (change wallpaper with instant crop option)
        /*Intent intent = new Intent(WallpaperManager.ACTION_CROP_AND_SET_WALLPAPER);
        Uri myImageUro = Uri.
        wpm.getCropAndSetWallpaperIntent(getImageUri(this, nasaLeeched));*/
    }

    //this is for future functions (change wallpaper with instant crop option)
    /*public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }*/
}
