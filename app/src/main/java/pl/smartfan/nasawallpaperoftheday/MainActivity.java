package pl.smartfan.nasawallpaperoftheday;

import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    TextView explanation, title, copyright;
    Button btn;
    ConstraintLayout layout;
    ProgressBar progressBar;
    CharSequence explanationText, titleText, copyrightText;

    // TODO: 25.11.2017 change wallpaper everyday
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.mainLayout);
        progressBar = findViewById(R.id.progressBar);
        btn = findViewById(R.id.button);

        try {
            nasaLeech();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //set onClickListener on button
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                //Show popup window with explanation, title and credits when button is clicked
                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popUpWindowLayout = null;
                if (inflater != null) {
                    popUpWindowLayout = inflater.inflate(R.layout.popup_window, null);
                }
                final PopupWindow window = new PopupWindow(popUpWindowLayout, layout.getWidth() - 50, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);

                if (popUpWindowLayout != null) {
                    explanation = popUpWindowLayout.findViewById(R.id.explanation);
                    title = popUpWindowLayout.findViewById(R.id.title);
                    copyright = popUpWindowLayout.findViewById(R.id.copyright);
                }

                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setAnimationStyle(R.style.Animation);

                //set touch listener for window
                window.setTouchInterceptor(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //view.performClick();
                        window.dismiss();
                        btn.setVisibility(View.VISIBLE);
                        return false;
                    }
                });

                window.showAtLocation(layout, Gravity.CENTER, 0, -25);

                //If explanationText contains text
                if (explanationText.length() != 0) {
                    //Fill MainActivity with photo & explanation & title & credits
                    explanation.setText(explanationText);
                    title.setText(titleText);
                    copyright.setText(copyrightText);
                }

                btn.setVisibility(View.INVISIBLE);
            }
        });
    }

    //method responsible for data leeching from NASA servers
    private void nasaLeech() throws MalformedURLException {
        //URL to send to AsyncTask
        URL url = new URL("https://api.nasa.gov/planetary/apod?api_key=GmIPSectIKdfHDCcnoFZpupFfex71nm9WODSejKu");

        //Instantiate new instance of GetDataAsyncTask class
        GetDataAsyncTask getRequest = new GetDataAsyncTask();

        getRequest.delegate = this;

        //Perform the doInBackground method, passing in our url
        getRequest.execute(url);
    }

    //method fired after AsyncTask finished
    @Override
    public void processFinish(Object[] results) {

        //Set image as wallpaper
        WallpaperManager wpm = WallpaperManager.getInstance(this);
        try {
            wpm.setBitmap((Bitmap) results[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Prepare text for widget (cut it)
        String textForWidget = (String) results[1];
        textForWidget = textForWidget.substring(0, 200) + "...";

        //Start and fill widget with leeched text
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.app_widget);
        ComponentName thisWidget = new ComponentName(this, AppWidget.class);
        remoteViews.setTextViewText(R.id.appWidgetText, textForWidget);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

        //Show toast message that wallpaper was loaded or reloaded
        Toast.makeText(this, R.string.toast_loaded_reloaded, Toast.LENGTH_LONG).show();

        Bitmap srcBmp = (Bitmap) results[0];
        Bitmap dstBmp;

        //Bitmap cropping
        if (srcBmp.getWidth() >= srcBmp.getHeight()) {

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        } else {

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }

        //Create drawable from cropped Bitmap
        Drawable drawable = new BitmapDrawable(getResources(), dstBmp);

        //Set drawable as wallpaper
        layout.setBackground(drawable);

        //Set leeched text to variables (used in showPopUpWindow method)
        explanationText = (CharSequence) results[1];
        titleText = (CharSequence) results[2];
        copyrightText = (CharSequence) results[3];

        //make button visible
        btn.setVisibility(View.VISIBLE);

        //make progress circle invisible
        progressBar.setVisibility(View.INVISIBLE);

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
