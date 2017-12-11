package pl.smartfan.spacewallz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import java.net.MalformedURLException;
import java.net.URL;

// TODO: 30.11.2017 add icons, comments 
public class MainActivity extends AppCompatActivity implements AsyncResponse {

    URL url;
    Bitmap srcBmp;
    PhotoView photoView;
    TextView explanation, title, copyright, date;
    Button btnExplanation, btnReload, xButtonWindow, xButtonFullscreen;
    ImageButton btnFullscreen;
    ConstraintLayout layout;
    ProgressBar progressBar;
    CharSequence explanationText, titleText, copyrightText, dateText;
    Alarm alarm;
    Utils utils;
    //class PreferencesSaveGet needs an enclosing instance to be instantiated
    Utils.PreferencesSaveGet prefs;
    private Integer randomDatesCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarm = new Alarm();
        utils = new Utils(this);
        prefs = new Utils(this).new PreferencesSaveGet();

        layout = findViewById(R.id.mainLayout);
        progressBar = findViewById(R.id.progressBar);
        btnExplanation = findViewById(R.id.button_explanation);
        btnReload = findViewById(R.id.button_refresh);
        btnFullscreen = findViewById(R.id.button_fullscreen);
        xButtonFullscreen = findViewById(R.id.x_button_fullscreen);
        photoView = findViewById(R.id.photoView);

        nasaLeech(0);

        //set onClickListener on btnFullscreen
        btnFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                btnFullscreen.setVisibility(View.INVISIBLE);
                btnExplanation.setVisibility(View.INVISIBLE);
                btnReload.setVisibility(View.INVISIBLE);

                photoView.setVisibility(View.VISIBLE);
                xButtonFullscreen.setVisibility(View.VISIBLE);
                if (srcBmp != null) {
                    Drawable drawable = new BitmapDrawable(getResources(), srcBmp);
                    photoView.setImageDrawable(drawable);
                }
            }
        });

        //set onClickListener on xButtonFullscreen
        xButtonFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFullscreen.setVisibility(View.VISIBLE);
                btnExplanation.setVisibility(View.VISIBLE);
                btnReload.setVisibility(View.VISIBLE);

                photoView.setVisibility(View.INVISIBLE);
                xButtonFullscreen.setVisibility(View.INVISIBLE);
            }
        });

        //set onClickListener on btnExplanation
        btnExplanation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                btnReload.setVisibility(View.INVISIBLE);
                btnExplanation.setVisibility(View.INVISIBLE);
                btnFullscreen.setVisibility(View.INVISIBLE);

                //Show popup window with explanation, title and credits when button is clicked
                LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popUpWindowLayout = null;
                if (inflater != null) {
                    popUpWindowLayout = inflater.inflate(R.layout.popup_window, (ViewGroup) findViewById(R.id.mainLayout), false);
                }
                final PopupWindow window = new PopupWindow(popUpWindowLayout, layout.getWidth() - 50, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);

                if (popUpWindowLayout != null) {
                    explanation = popUpWindowLayout.findViewById(R.id.explanation);
                    title = popUpWindowLayout.findViewById(R.id.title);
                    copyright = popUpWindowLayout.findViewById(R.id.copyright);
                    date = popUpWindowLayout.findViewById(R.id.date);
                    xButtonWindow = popUpWindowLayout.findViewById(R.id.xButtonWindow);
                }

                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setAnimationStyle(R.style.Animation);

                //set onClickListener for popup window X button
                xButtonWindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        window.dismiss();
                        btnExplanation.setVisibility(View.VISIBLE);
                        btnReload.setVisibility(View.VISIBLE);
                        btnFullscreen.setVisibility(View.VISIBLE);
                    }
                });

                window.showAtLocation(layout, Gravity.CENTER, 0, -25);

                //If explanationText contains text
                if (explanationText.length() != 0) {
                    //Fill MainActivity with photo & explanation & title & credits
                    explanation.setText(explanationText);
                    title.setText(titleText);
                    copyright.setText(copyrightText);
                    date.setText(dateText);
                }
            }
        });

        //set onClickListener on btnReload
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nasaLeech(0);
            }
        });
    }

    //method responsible for data leeching from NASA servers
    void nasaLeech(int minusDays) {
        //checking is there internet connection available
        if (utils.isNetworkAvailable()) {
            try {
                //make progress circle visible
                progressBar.setVisibility(View.VISIBLE);

                //get data and set it in this Activity

                //URL to send to AsyncTask with custom date
                url = new URL("https://api.nasa.gov/planetary/apod?date=" + utils.getDateForUrl(minusDays) + "&hd=True&api_key=GmIPSectIKdfHDCcnoFZpupFfex71nm9WODSejKu");

                //Instantiate new instance of GetDataAsyncTask class
                GetDataAsyncTask getRequest = new GetDataAsyncTask(prefs, utils.getWallpaper());

                getRequest.delegate = this;

                //Perform the doInBackground method, passing in url
                getRequest.execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            alertMe((String) getText(R.string.alert_message_no_internet));
        }
    }

    //method fired after AsyncTask finished
    @Override
    public void processFinish(Object[] results) {
        if (results != null) { //if there is results

            //Set image as wallpaper
            utils.setWallpaper((Bitmap) results[0]);

            //Fill widget with data
            utils.setWidget((String) results[1]);

            //Show toast message that wallpaper was loaded or reloaded
            Toast.makeText(this, R.string.toast_loaded_reloaded, Toast.LENGTH_LONG).show();

            srcBmp = (Bitmap) results[0];
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
            dateText = (CharSequence) results[4];

            //make buttons visible
            btnExplanation.setVisibility(View.VISIBLE);
            btnFullscreen.setVisibility(View.VISIBLE);

            //make progress circle invisible
            progressBar.setVisibility(View.INVISIBLE);

            //save url to prefs
            prefs.savePreferences(url.toString(), (String) results[1], (String) results[2], (String) results[3], (String) results[4]);
        } else { //if there is no results from AsyncTask - show alert dialog
            if (randomDatesCounter < 10) {
                //for future random date getter
                //Random r = new Random();
                //int randomMinusDays = r.nextInt(1000 - 1) + 1;

                nasaLeech(randomDatesCounter + 1); // get latest working date
                randomDatesCounter++;
            } else { //if randomizing dates doesn't work (tried 5 times), show Alert Dialog
                alertMe((String) getText(R.string.alert_message_no_data));
            }
        }
    }

    //method responsible for Alert Dialog
    void alertMe(String message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(R.string.alert_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0); //exit app
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        alarm.setAlarm(this.getApplicationContext());
    }
}
