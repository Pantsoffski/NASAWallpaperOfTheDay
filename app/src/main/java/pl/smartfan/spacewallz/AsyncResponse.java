package pl.smartfan.spacewallz;

/**
 * Interface {@link AsyncResponse} is responsible for handling response from AsyncTask.
 */

public interface AsyncResponse {
    void processFinish(Object[] results);
}
