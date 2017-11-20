package pl.smartfan.nasawallpaperoftheday;

import java.io.InputStream;

/**
 * Interface {@link AsyncResponse} is responsible for handling response from AsyncTask.
 */

public interface AsyncResponse {
    void processFinish(InputStream output);
}
