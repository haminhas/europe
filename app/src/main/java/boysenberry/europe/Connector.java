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
public class Connector extends AsyncTask<String, Void, String> {
    private Context context;
    private Countries c = new Countries();

    public Connector(Context context) {
        this.context = context;
        String country = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR?per_page=100&format=json";
        String female = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SL.EMP.TOTL.SP.FE.ZS?format=json&date=1990:2013&per_page=10000";
        String population = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SP.POP.TOTL?format=json&date=1990%3A2013&per_page=10000";
        String fpop = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SP.POP.TOTL.FE.ZS?format=json&date=1990%3A2013&per_page=10000";
        String education = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SL.TLF.TERT.FE.ZS?format=json&date=1990:2013&per_page=10000";
        String labour = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SL.TLF.TOTL.FE.ZS?format=json&date=1990:2013&per_page=10000";
        execute(country, female, population, fpop, education, labour);
    }

    @Override
    protected String doInBackground(String... urls) {
        StringBuffer buffer = new StringBuffer();
        String[] strings = new String[6];
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
                strings[i] = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.i("test", "ID");
        json(strings[0]);
        Log.i("test", "female");
        getData(strings[1], "first");
        Log.i("test", "population");
        getData(strings[2], "second");
        Log.i("test", "fpop");
        getData(strings[3], "third");
        Log.i("test", "eduaction");
        getData(strings[4], "fourth");
        Log.i("test", "labour");
        getData(strings[5], "fifth");

        //Save data for all countries into separate files
        for (Country co : c.getList())
            Data.saveData(context, co.getName(), co.toString());
        return (buffer.toString());
    }

    private void json(String s) {
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

    private void getData(String s, String number) {
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
                if (number.equals("first")) {
                    for (Country j : c.getList()) {
                        if (j.getID().equals(id)) {
                            j.addFemalePercentage(temp);
                        }
                    }
                } else if (number.equals("second")) {
                    for (Country j : c.getList()) {
                        if (j.getID().equals(id)) {
                            j.addPopulation(temp);
                        }
                    }
                } else if (number.equals("third")) {
                    for (Country j : c.getList()) {
                        if (j.getID().equals(id)) {
                            j.addFemalePercentage(temp);
                        }
                    }
                } else if (number.equals("fourth")) {
                    for (Country j : c.getList()) {
                        if (j.getID().equals(id)) {
                            j.addEducation(temp);
                            //Log.i("test", id + "," + temp);
                        }
                    }
                } else if (number.equals("fifth")) {
                    for (Country j : c.getList()) {
                        if (j.getID().equals(id)) {
                            j.addlabour(temp);
                            Log.i("test", id + "," + temp);
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
}
