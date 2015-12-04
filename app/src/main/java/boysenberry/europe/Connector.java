package boysenberry.europe;

import android.content.Context;
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
 * Class which collects all data from the world bank
 */
public class Connector extends AsyncTask<String, Void, String[]> {
    private Context context;

    public Connector(Context context) {
        this.context = context;
    }

    @Override
    protected String[] doInBackground(String... urls) {
        String[] json = new String[6];
        StringBuffer buffer;
        for (int i = 0; i < 6; i++) {
            buffer = new StringBuffer();
            try {
                URL url = new URL(urls[i]);
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
                json[i] = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Save data for all countries into separate files
        return json;
    }


}
