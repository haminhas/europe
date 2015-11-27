package boysenberry.europe;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class can be used for writing and retrieving data to the device internal
 * storage by using its static methods.
 * Created by Tamara on 23/11/2015.
 */
public class Data {
    private static Context context;
    private static String data;
    private static String result;
    private static final ArrayList<String> names = new ArrayList<>(Arrays.asList("Albania", "Andorra", "Armenia", "Austria",
            "Azerbaijan", "Belgium", "Bulgaria", "Bosnia and Herzegovina", "Belarus", "Switzerland", "Cyprus",
            "Czech Republic", "Germany", "Denmark", "Spain", "Estonia", "Finland", "France", "United Kingdom",
            "Georgia", "Greece", "Croatia", "Hungary", "Ireland", "Iceland", "Italy", "Kosovo", "Liechtenstein",
            "Lithuania", "Luxembourg", "Latvia", "Monaco", "Moldova", "Macedonia, FYR", "Malta", "Montenegro",
            "Netherlands", "Norway", "Poland", "Portugal", "Romania", "Russian Federation", "San Marino", "Serbia",
            "Slovak Republic", "Slovenia", "Sweden", "Turkey", "Ukraine"));

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
     * Retrieves the data for a given country stored in the device internal storage
     */
    private static String getData(Context context, String countryName) {
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

    /**
     * Reads data from internal storage, and returns a collection of countries.
     *
     * @param context Context
     * @return Countries
     */
    public static Countries getAllData(Context context) {
        Countries c = new Countries();

        for (String country : names) {  //for each country
            String data = Data.getData(context, country);
            ArrayList<String> lines = new ArrayList<>(Arrays.asList(data.split(System.getProperty("line.separator"))));

            String ID = lines.get(0);
            String name = lines.get(1);
            String capital = lines.get(2);
            Country co = new Country(ID, name, capital);

            for (int i = 3; i < lines.size(); i++) {
                if (i < 27)
                    co.addPopulation(lines.get(i));
                else if (i < 51)
                    co.addFemalePopulation(lines.get(i));
                else
                    co.addFemalePercentage(lines.get(i));
            }
            c.add(co);
        }
        return c;
    }
}
