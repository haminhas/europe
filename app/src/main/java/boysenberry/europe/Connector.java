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

/**
 * Created by Hassan on 23/11/2015.
 */
public class Connector extends AsyncTask<String, Void, String> {
    private String result;

    public Connector (){
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
        result = buffer.toString();

        try {
            JSONArray jsArray = new JSONArray(result);
            JSONObject jsObject = jsArray.getJSONObject(0);
            JSONArray jsData = jsArray.getJSONArray(1);

            for(int i=0; i < jsData.length() ; i++) {

                JSONObject jObject = jsData.getJSONObject(i);

                String name = jObject.getString("name");
                Log.i("tag",name);

            } // End Loop
        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        }

        return(buffer.toString());
    }

}
