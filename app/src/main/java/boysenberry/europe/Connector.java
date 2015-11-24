package boysenberry.europe;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Hassan on 23/11/2015.
 */
public class Connector extends AsyncTask<String, Void, String> {
    private String result;
    private ArrayList<String> alldata;

    public Connector (){
        alldata = new ArrayList<String>();
        result = null;
        execute("http://api.worldbank.org/country?per_page=100&region=EUU&format=json");
    }
    @Override
    protected String doInBackground(String... urls){
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine = in.readLine();
            while (inputLine != null) {
                buffer.append(inputLine);
                inputLine = in.readLine();
            }
            in.close();
            connection.disconnect();
        } catch (IOException e){
            e.printStackTrace();
        }
        json(buffer.toString());
        result = buffer.toString();
        return(buffer.toString());
    }

    private void json(String s){
        try {
            JSONArray jsArray = new JSONArray(s);
            JSONObject jsObject = jsArray.getJSONObject(0);
            JSONArray jsData = jsArray.getJSONArray(1);

            for(int i=0; i < jsData.length() ; i++) {

                JSONObject jObject = jsData.getJSONObject(i);
                String temp;

                String name = jObject.getString("name");
                String capital = jObject.getString("capitalCity");
                String latitude = jObject.getString("latitude");
                String longitude = jObject.getString("longitude");
                temp = name +","+capital+","+ latitude+","+ longitude;
                alldata.add(temp);
                Log.i("tag",temp);

            } // End Loop
        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        }
    }

    public ArrayList<String> getData(){
        return alldata;
    }



}