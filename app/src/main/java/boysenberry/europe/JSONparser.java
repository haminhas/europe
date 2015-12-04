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
    private Connector con;
    private String[] json;

    public JSONparser (Context context, String[] urls) {
        json = new String[6];
        this.context = context;
        con = new Connector(this.context);
            //String temp  = con.doInBackground(s);
            try{
                json = con.execute(urls).get();
            } catch (InterruptedException | ExecutionException e){
                e.printStackTrace();
            }

        Country(json[0]);
        Data(json[1], "first");
        Data(json[2], "second");
        Data(json[3], "third");
        Data(json[4], "forth");
        Data(json[5], "fifth");

        saveData();
    }

    private void Country(String s) {
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

            } // End Loop
        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        }
    }

    private void Data(String s, String number) {
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

                for (Country j : c.getList()) {
                    if (j.getID().equals(id)) {
                        if (number.equals("first")) {
                            j.addFemalePercentage(temp);
                        } else if (number.equals("second")) {
                            j.addPopulation(temp);
                        } else if (number.equals("third")) {
                            j.addFemalePopulation(temp);
                        } else if (number.equals("fourth")) {
                            j.addEducation(temp);
                        } else if (number.equals("fifth")) {
                            j.addlabour(temp);

                        }
                    }
                }

            } // End Loop

        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        }
    }

    public Countries getCountries(){
        return c;
    }

    private void saveData(){
        for (Country co : c.getList())
            Data.saveData(context, co.getName(), co.toString());
    }

}
