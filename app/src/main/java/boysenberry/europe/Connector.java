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
    private Countries c = new Countries();
    private boolean first;

    public Connector (){

        first = true;
        String country = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR?per_page=100&format=json";
        String female = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SL.EMP.TOTL.SP.FE.ZS?format=json&date=1990:2013&per_page=10000";
        String population = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SP.POP.TOTL?format=json&date=1990%3A2013&per_page=10000";
        execute(country,female,population);
    }
    @Override
    protected String doInBackground(String... urls){
        StringBuffer buffer = new StringBuffer();
        String[] strings = new String[3];
        for(int i = 0;i<3;i++) {
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
                strings[i] = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.i("test","ID");
        json(strings[0]);
        Log.i("test","female");
        getData(strings[1]);
        Log.i("test","population");
        getData(strings[2]);

        return (buffer.toString());
    }

    private void json(String s){
        try {
            JSONArray jsArray = new JSONArray(s);
            JSONArray jsData = jsArray.getJSONArray(1);

            for(int i=0; i < jsData.length() ; i++) {

                JSONObject jObject = jsData.getJSONObject(i);
                String temp;
                String id = jObject.getString("iso2Code");
                String name = jObject.getString("name");
                String capital = jObject.getString("capitalCity");
                Country t =  new Country(id,name,capital);
                c.add(t);
                temp = name +","+capital;
                //Log.i("tag",id);

            } // End Loop
        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        }
    }

    private void getData(String s){
        try {
            JSONArray jsArray = new JSONArray(s);
            JSONArray jsData = jsArray.getJSONArray(1);

            for(int i=0; i < jsData.length() ; i++) {

                JSONObject jObject = jsData.getJSONObject(i);

                String temp;
                String idArray = "["+jObject.getString("country")+"]";
                JSONArray getIDs = new JSONArray(idArray);
                JSONObject jtemp = getIDs.getJSONObject(0);
                String id = jtemp.getString("id");
                String value = jObject.getString("value");
                String date = jObject.getString("date");

                temp = value+","+ date;
                if (first) {
                    for (Country j : c.getList()) {
                        if (j.getID().equals(id)) {
                            j.addFemalePercentage(temp);
                        }
                    }
                    first = false;
                } else {
                    for (Country j : c.getList()) {
                        if (j.getID().equals(id)) {
                            j.addPopulation(temp);
                        }
                    }
                }
                Log.i("test",id+","+temp);

            } // End Loop

        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        }
    }
}
