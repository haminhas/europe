package boysenberry.europe;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class can be used for writing and retrieving data to the device internal
 * storage by using its static methods.
 * Created by Tamara on 23/11/2015.
 */
public class Data {
    private static Context context;
    private static String data;
    private static String result;

    /**
     * Saves given data to the running device internal storage
     *
     * @param countryName - name of the country to be saved
     * @param data        - data represented as a String
     */
    public static void saveData(Context c, String countryName, String data) {
        context = c;
        Data.data = data;
        writeToFile(countryName);          //calls a private method
    }

    private static void writeToFile(String filename) {
        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
            Log.i("SAVED DATA FOR COUNTRY:", filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the data stored in the device internal storage
     */
    public static String getData(String countryName) {
        try {
            FileInputStream inputStream = context.openFileInput(countryName);
            result = convertStreamToString(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String convertStreamToString(FileInputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sBuild = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sBuild.append(line).append("\n");
        }
        reader.close();
        return sBuild.toString();
    }
}
