package pl.smartfan.nasawallpaperoftheday;

import android.graphics.Bitmap;

/**
 * Interface {@link AsyncResponse} is responsible for handling response from AsyncTask.
 */

public interface AsyncResponse {
    void processFinish(Bitmap output);
}
