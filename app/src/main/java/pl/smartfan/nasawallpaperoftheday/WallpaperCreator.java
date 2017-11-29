package pl.smartfan.nasawallpaperoftheday;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class {@link WallpaperCreator} is responsible for processing JSON object.
 */

class WallpaperCreator {

    String[] readJsonStream(InputStreamReader streamReader) throws IOException {
        JsonReader jsonReader = new JsonReader(streamReader);

        String[] stringsToReturn = new String[]{"", "", "", "", ""};

        try {
            jsonReader.beginObject(); //consume the object's opening brace
            while (jsonReader.hasNext()) {
                String fetchedData = jsonReader.nextName();
                switch (fetchedData) {
                    case "copyright":  // Check if desired data is available
                        stringsToReturn[0] = "Copyright:\n" + jsonReader.nextString();
                        Log.v("Result:", stringsToReturn[0]);
                        break;
                    case "explanation":
                        stringsToReturn[1] = jsonReader.nextString();
                        Log.v("Result:", stringsToReturn[1]);
                        break;
                    case "hdurl":
                        stringsToReturn[2] = jsonReader.nextString();
                        Log.v("Result:", stringsToReturn[2]);
                        break;
                    case "title":
                        stringsToReturn[3] = jsonReader.nextString();
                        Log.v("Result:", stringsToReturn[3]);
                        break;
                    case "date":  // TODO: 28.11.2017 add date to explanation
                        stringsToReturn[4] = "Date: " + jsonReader.nextString();
                        Log.v("Result:", stringsToReturn[4]);
                        break;
                    default:
                        jsonReader.skipValue(); // Skip values of other names

                        break;
                }
            }
            jsonReader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringsToReturn;
    }

}
