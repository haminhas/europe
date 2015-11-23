package boysenberry.europe;

import android.content.Context;

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
    private static String data, filename = "europe_data";
    private static String result;

    /**
     * Saves given data to the running device internal storage
     *
     * @param data - data represented as a String
     */
    public static void saveData(Context c, String data) {
        context = c;
        Data.data = data;
        writeToFile();          //calls a private method
    }

    private static void writeToFile() {
        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the data stored in the device internal storage
     */
    public static String getData() {
        try {
            FileInputStream inputStream = context.openFileInput(filename);
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
