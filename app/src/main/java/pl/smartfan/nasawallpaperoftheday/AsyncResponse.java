package pl.smartfan.nasawallpaperoftheday;

/**
 * Interface {@link AsyncResponse} is responsible for handling response from AsyncTask.
 */

public interface AsyncResponse<T> {
    void processFinish(T... results);
}
