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
        String s = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR?per_page=100&format=json";
        String zz = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SL.EMP.TOTL.SP.FE.ZS?format=json&date=1990:2013&per_page=10000";
        execute(s,zz);
    }
    @Override
    protected String doInBackground(String... urls){
        StringBuffer buffer;
        buffer = new StringBuffer();
        for(int i = 0;i<2;i++) {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (first) {
                json(buffer.toString());
                first = false;
            } else {
                getFemaleWork(buffer.toString());
            }
        }
        return (buffer.toString());
    }

    private void json(String s){
        try {
            JSONArray jsArray = new JSONArray(s);
            JSONObject jsObject = jsArray.getJSONObject(0);
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

    private void getFemaleWork(String s){
        try {
            JSONArray jsArray = new JSONArray(s);
            JSONObject jsObject = jsArray.getJSONObject(0);
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
                for(Country j:c.getList()){
                    if(j.getID().equals(id)){
                        j.addFemalePercentage(temp);
                    }
                }
                //Log.i("test",id+","+temp);

            } // End Loop

        } catch (JSONException e) {
            Log.e("JSONException", "Error: " + e.toString());
        }
    }
}
