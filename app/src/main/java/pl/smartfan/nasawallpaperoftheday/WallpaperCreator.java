package pl.smartfan.nasawallpaperoftheday;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class {@link WallpaperCreator} is responsible for processing JSON object.
 */

public class WallpaperCreator {

    private String[] stringToReturn = new String[4];

    public String[] readJsonStream(InputStreamReader streamReader) throws IOException {
        JsonReader jsonReader = new JsonReader(streamReader);

        try {
            jsonReader.beginObject(); //consume the object's opening brace
            while (jsonReader.hasNext()) {
                String fetchedData = jsonReader.nextName();
                if (fetchedData.equals("copyright")) { // Check if desired data is available
                    stringToReturn[0] = "Copyright:\n" + jsonReader.nextString();
                    Log.v("Result:", stringToReturn[0]);
                } else if (fetchedData.equals("explanation")) {
                    stringToReturn[1] = jsonReader.nextString();
                    Log.v("Result:", stringToReturn[1]);
                } else if (fetchedData.equals("hdurl")) {
                    stringToReturn[2] = jsonReader.nextString();
                    Log.v("Result:", stringToReturn[2]);
                } else if (fetchedData.equals("title")) {
                    stringToReturn[3] = jsonReader.nextString();
                    Log.v("Result:", stringToReturn[3]);
                } else {
                    jsonReader.skipValue(); // Skip values of other names
                }
            }
            jsonReader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringToReturn;
    }

}
