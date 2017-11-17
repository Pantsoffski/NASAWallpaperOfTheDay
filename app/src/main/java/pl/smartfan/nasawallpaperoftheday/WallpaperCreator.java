package pl.smartfan.nasawallpaperoftheday;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class {@link WallpaperCreator} is responsible for processing JSON object.
 */

public class WallpaperCreator {

    private String stringToReturn = "";

    public String readJsonStream(InputStreamReader streamReader) throws IOException {
        JsonReader jsonReader = new JsonReader(streamReader);

        try {
            jsonReader.beginObject(); //consume the object's opening brace
            while (jsonReader.hasNext()) { // TODO: 17.11.2017 add comments
                String fetchedData = jsonReader.nextName();
                if (fetchedData.equals("url")) { // Check if desired data is available
                    while (jsonReader.hasNext()) {
                        stringToReturn = jsonReader.nextString();
                    }
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
