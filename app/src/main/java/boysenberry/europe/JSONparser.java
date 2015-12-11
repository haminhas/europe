package boysenberry.europe;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hassan on 04/12/2015.
 */
public class JSONparser {
    private Context context;
    private Countries c = new Countries();

    /**
     * Initialises all the country objects are stores the country name, ID, and Capital city
     *
     * @param  s - JSON format string
     * @return String
     */
    public String Country(String s) {
        String temp = "";
        try {
            JSONArray jsArray = new JSONArray(s);
            JSONArray jsData = jsArray.getJSONArray(1);

            for (int i = 0; i < jsData.length(); i++) {

                JSONObject jObject = jsData.getJSONObject(i);
                String id = jObject.getString("iso2Code");
                String name = jObject.getString("name");
                String capital = jObject.getString("capitalCity");
                Country t = new Country(id, name, capital);
                c.add(t);
                temp = id +" "+ name +" "+ capital;

            } // End Loop
        } catch (JSONException | NullPointerException e) {
            Log.e("", "Error: " + e.toString());
        }
        return temp;
    }

    /**
     * Adds data to correct country objects
     *
     * @param  s - JSON format
     *         number - used to decide which where the data should be saved
     * @return String
     */
    public String[] Data(String s, String number) {
        ArrayList<String> t = new ArrayList<>();
        try {
            JSONArray jsArray = new JSONArray(s);
            JSONArray jsData = jsArray.getJSONArray(1);

            for (int i = 0; i < jsData.length(); i++) {

                JSONObject jObject = jsData.getJSONObject(i);

                String temp;
                String idArray = "[" + jObject.getString("country") + "]";
                JSONArray getIDs = new JSONArray(idArray);
                JSONObject jtemp = getIDs.getJSONObject(0);
                String id = jtemp.getString("id");
                String value = jObject.getString("value");
                String date = jObject.getString("date");

                temp = value + "," + date;
                t.add(temp);
                //loops through all the countries until it finds the correct country which matches the ID
                for (Country j : c.getList()) {
                    if (j.getID().equals(id)) {
                        // then it adds the data to the an array which is the country class based
                        // on the value that is passed to the method
                        if (number.equals("first")) {
                            j.addparliaments(temp);
                            break;
                        } else if (number.equals("second")) {
                            j.addPopulation(temp);
                            break;
                        } else if (number.equals("third")) {
                            j.addFemalePopulation(temp);
                            break;
                        } else if (number.equals("fourth")) {
                            j.addEducation(temp);
                            break;
                        } else if (number.equals("fifth")) {
                            j.addlabour(temp);
                            break;

                        }
                    }
                }

            } // End Loop

        } catch (JSONException | NullPointerException e) {
            Log.e("", "Error: " + e.toString());
        }
        String[]  j = t.toArray(new String[t.size()]);
        return j;
    }

    public Countries getCountries(){
        return c;
    }


    /**
     * Saves all the data to local memory
     */
    public void saveData(Context context){
        this.context = context;

        for (Country co : c.getList())
            Data.saveData(context, co.getName(), co.toString());
    }



}


