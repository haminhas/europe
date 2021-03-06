package boysenberry.europe;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    // TODO add line chart
    // TODO Remove pointer at germany

    private Geocoder geocoder;
    private GoogleMap mMap;
    private RelativeLayout layoutInformation;
    private SeekBar seekBar;
    private Spinner spinnerCountries;
    private ImageView imageCountryFlag;
    private TextView textCountryName;
    private TextView textCountryPopulation;
    private TextView textCountryCapital;
    private TextView textYear;
    private TextView textRatio;

    private Countries countries;
    private String countryName;
    private String prevValidCountryName;

    private final LatLng CENTER = new LatLng(54, 15);
    private final int YEAR_END = 2013;
    private final int YEAR_START = 1991;

    ArrayList<String> countryList = new ArrayList<>();

    private PieChart mChart;

    public Countries getCountries(){
        return countries;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        countryList = Data.getCountries();

        checkNetwork();
        startUp();

        Toast.makeText(getApplicationContext(), "Select a country from the menu, or just click on the map", Toast.LENGTH_LONG).show();
    }

    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng germany = new LatLng(52, 13);

        mMap.setOnMapClickListener(this);

        //Sets camera to the centre point in Europe at zoom level 4 so all European countries are shown
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                boundsCheck();
            }
        });

    }

    /**
     * Used to set up key components of the MapsActivity.
     */
    private void startUp() {

        geocoder = new Geocoder(this, Locale.getDefault());
        layoutInformation = (RelativeLayout) findViewById(R.id.layoutInformation);
        //layoutInformation.setAlpha(0.95f);
        layoutInformation.setVisibility(View.INVISIBLE);

        textCountryName = (TextView) findViewById(R.id.textCountryName);
        textCountryPopulation = (TextView) findViewById(R.id.textCountryPopulation);
        textCountryCapital = (TextView) findViewById(R.id.textCountryCapital);
        textYear = (TextView) findViewById(R.id.textYear);
        TextView population = (TextView) findViewById(R.id.population);
        imageCountryFlag = (ImageView) findViewById(R.id.imageCountryFlag);
        textRatio = (TextView) findViewById(R.id.ratio);

        textCountryName.setTextColor(Color.WHITE);
        textCountryPopulation.setTextColor(Color.WHITE);
        textCountryCapital.setTextColor(Color.WHITE);
        textYear.setTextColor(Color.WHITE);
        population.setTextColor(Color.WHITE);
        textRatio.setTextColor(Color.WHITE);


        spinnerCountries = (Spinner) findViewById(R.id.spinnerCountry);
        spinnerCountries.setDropDownHorizontalOffset(5);

        ArrayList<String> tempArr = countryList;
        tempArr.add(0, "Select a country");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tempArr);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCountries.setAdapter(adapter);
        spinnerCountries.setOnItemSelectedListener(new spinnerListener());

        seekBar = (SeekBar) findViewById(R.id.seekBarYear);
        seekBar.setMax(YEAR_END - YEAR_START);
        seekBar.setProgress(2013);
        seekBar.setOnSeekBarChangeListener(new seekYearChange());

        GoogleApiClient client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void checkNetwork() {
        // first check if there is an internet connection
        if (isNetworkConnected(this)) {
            //if there is internet connection check whether the data has already been saved
            if (Data.isNull(this.getApplicationContext())) {
                // if this is the first time the app has been loaded up fetch the data from the World Bank Data set
                String country = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR?per_page=100&format=json";
                String parliaments = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SG.GEN.PARL.ZS?format=json&date=1990:2013&per_page=10000";
                String population = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SP.POP.TOTL?format=json&date=1990%3A2013&per_page=10000";
                String fpop = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SP.POP.TOTL.FE.ZS?format=json&date=1990%3A2013&per_page=10000";
                String education = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SL.TLF.TERT.FE.ZS?format=json&date=1990:2013&per_page=10000";
                String labour = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SL.TLF.TOTL.FE.ZS?format=json&date=1990:2013&per_page=10000";

                String[] ar = {country, parliaments, population, fpop, education, labour};
                Connector con = new Connector();
                String[] json = new String[6];
                try {
                    //get the JSON string format from the URL's provided
                    json = con.execute(ar).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                JSONparser p = new JSONparser();

                p.Country(json[0]);
                p.Data(json[1], "first");
                p.Data(json[2], "second");
                p.Data(json[3], "third");
                p.Data(json[4], "fourth");
                p.Data(json[5], "fifth");

                // Save the data to local memory
                p.saveData(this.getApplicationContext());
                countries = p.getCountries();

            } else {
                // If this is not the first time the app has been loaded and there in an internet connetion
                // then get the Data from the local storage
                data();
            }
        } else {
            // if there is no internet connection then get the data fro the local storage
            // if there is no data then give a message to connect to the internet
            data();
        }
    }

    private void data() {
        try {
            countries = Data.getAllData(this.getApplicationContext());
            RelativeLayout layoutSpinner = (RelativeLayout) findViewById(R.id.layoutSpinner);
            layoutSpinner.setHorizontalGravity(50);
            layoutInformation.setHorizontalGravity(50);
        } catch (RuntimeException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Please connect to the internet and try again", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkConnected(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            // There are no active networks.
            return false;
        }
        //boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return true;
    }

    public void boundsCheck() {
        LatLngBounds europeBounds = new LatLngBounds(
                new LatLng(35, -10),
                new LatLng(70, 70)
        );

        LatLngBounds visibleBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        if (!europeBounds.contains(visibleBounds.getCenter())) {
            setMapCenter();
        }
    }

    private void setMapCenter() {
        LatLng CENTER = new LatLng(53, 32);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 4));
    }

    /**
     * Method is used to get a countries name using the latitude and longitude. Method doesn't return
     * anything but updates the countryName variable which is important for other features of the app.
     * <p>
     * Method is also used to change some of the countries name because of variations between the Google
     * and WorldDataBank name for countries.
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     */
    public void getCountryName(double latitude, double longitude) {
        countryName = "";

        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address;

        address = addresses.get(0);
        if (address != null) {
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                countryName = address.getCountryName();
            }
        }

        switch (countryName) {
            case "Macedonia (FYROM)":
                countryName = "Macedonia, FYR";
                break;
            case "Kosova (Kosovo)":
                countryName = "Kosovo";
                break;
            case "Slovakia":
                countryName = "Slovak Republic";
                break;
            case "Russia":
                countryName = "Russian Federation";
                break;
        }

    }

    /**
     * Method checks which country has been clicked and then gets the flag for that
     * country and sets it as a resource for imageCountryFlag.
     *
     * @param country String value with the country name
     */
    public void setFlag(String country) {

        switch (country) {
            case "Bosnia and Herzegovina":
                country = "bosniaandherzegovina";
                break;
            case "Czech Republic":
                country = "czechrepublic";
                break;
            case "United Kingdom":
                country = "unitedkingdom";
                break;
            case "Macedonia, FYR":
                country = "macedoniafyr";
                break;
            case "Russian Federation":
                country = "russia";
                break;
            case "San Marino":
                country = "sanmarino";
                break;
            case "Slovak Republic":
                country = "slovak";
                break;
        }

        String flag = country.toLowerCase();
        imageCountryFlag.setImageResource(getResources().getIdentifier(flag, "drawable", getPackageName()));
        imageCountryFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    /**
     * Check for changes on seek bar. When the user uses it the information layout
     * is updated accordingly.
     */
    private class seekYearChange implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int year = YEAR_START + progress;

            // TODO pie chart
            //createDougnutCharts(year + "");
            updateInformationLayout(countryName, year);
            textYear.setText(year + ":");
            textCountryPopulation.setText(countries.getCountry(countryName).getPopulation(year + ""));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    }

    private class spinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            try {
                if (position != 0) {
                    countryName = parent.getItemAtPosition(position).toString();
                    updateInformationLayout(countryName, YEAR_END);
                    setFlag(countryName);
                    seekBar.setProgress(2013);

                    if (layoutInformation.getVisibility() == View.INVISIBLE) {
                        layoutInformation.setVisibility(View.VISIBLE);
                        setMapCenter();
                    }
                }
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), "Please connect to the internet and try again or Re-open the App", Toast.LENGTH_LONG).show();
            }

        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    /**
     * Method is used to get the latitude and longitude when a user clicks on the map.
     *
     * @param latLng used to work out which country is being clicked on by the user.
     */
    @Override
    public void onMapClick(LatLng latLng) {
        try {
            getCountryName(latLng.latitude, latLng.longitude);
            updateInformationLayout(countryName, YEAR_END);
            setFlag(countryName);
            seekBar.setProgress(2013);
            spinnerCountries.setSelection(countryList.indexOf(countryName));
            if (layoutInformation.getVisibility() == View.INVISIBLE) {
                layoutInformation.setVisibility(View.VISIBLE);
                setMapCenter();
            }
            prevValidCountryName = countryName;
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), countryName + " is not an European country", Toast.LENGTH_SHORT).show();
            if (prevValidCountryName != null) {
                countryName = prevValidCountryName;
                updateInformationLayout(countryName, YEAR_END);
            }
            seekBar.setProgress(2013);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(getApplicationContext(), "Please click on a country", Toast.LENGTH_SHORT).show();
            countryName = prevValidCountryName;
        }

    }

    /**
     * Method is used to to update information such as country name, country population, country capital,
     * ratios of male to female population.
     *
     * @param country parameter which is required to update the name of the country and various other widgets
     * @param year    parameter required to show information for a particular year.
     */
    public void updateInformationLayout(String country, int year) {

        textCountryName.setText(country);
        try {
            textCountryPopulation.setText(countries.getCountry(country).getPopulation(year + ""));
            textCountryCapital.setText(countries.getCountry(country).getCapital());
            String ratioFemalePercentage = countries.getCountry(country).getFemalePopulation(year + "");
            Float ratioFemale = Float.parseFloat(ratioFemalePercentage);
            Float ratioMale = 100 - ratioFemale;
            String display = (int) Math.ceil(ratioMale) + " : " + (int) Math.ceil(ratioFemale);
            textRatio.setText(display);
        } catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
        }
        textYear.setText(year + ":");
        seekBar.setVisibility(View.VISIBLE);

        String yearChart = year + "";
        createAllCharts(year + "");
    }

    /**
     * Method is used to create all charts for the information layout.
     *
     * @param year charts are created depending on the year passed through
     */
    private void createAllCharts(String year) {

        LinearLayout chart1 = (LinearLayout) findViewById(R.id.chart1Ratio);

        try {
            mChart = (PieChart) findViewById(R.id.mChart1);
            mChart.removeAllViewsInLayout();
            String femaleLabourPercentage = countries.getCountry(countryName).getLabour(year);
            float femaleLabour = Float.parseFloat(femaleLabourPercentage);
            float maleLabour = 100 - femaleLabour;
            float[] labour = {maleLabour, femaleLabour};

            ArrayList<Entry> yData = new ArrayList<>();
            for (int i = 0; i < labour.length; i++) {
                yData.add(new Entry(labour[i], i));
            }

            ArrayList<String> xValue = new ArrayList<>();
            xValue.add("Male");
            xValue.add("Female");

            createPieChart(mChart, "Breakdown of gender employment (% out of whole population).", yData, xValue, year);


            chart1.removeAllViews();
            addRatio(femaleLabour, maleLabour, chart1);

            mChart.setNoDataText("");
            mChart.setNoDataTextDescription("");


        } catch (NumberFormatException e) {
            mChart.removeAllViews();
            chart1.removeAllViews();
            mChart.setNoDataText("Data not available.");
            mChart.setNoDataTextDescription("");

        }

        LinearLayout chart2 = (LinearLayout) findViewById(R.id.chart2Ratio);
        try {
            mChart = (PieChart) findViewById(R.id.mChart2);
            mChart.removeAllViewsInLayout();
            String femaleEducationPercentage = countries.getCountry(countryName).getEducation(year);
            float femaleEducation = Float.parseFloat(femaleEducationPercentage);
            float femaleNotEducation = 100 - femaleEducation;
            float[] education = {femaleNotEducation, femaleEducation};

            ArrayList<Entry> yData = new ArrayList<>();
            for (int i = 0; i < education.length; i++) {
                yData.add(new Entry(education[i], i));
            }

            ArrayList<String> xValue = new ArrayList<>();
            xValue.add("Without education");
            xValue.add("With education");

            createPieChart(mChart, "Percentage of female in workforce with tertiary education", yData, xValue, year);

            chart2.removeAllViews();
            for (int i = 0; i < ((int) Math.ceil(femaleEducation) / 10); i++) {
                ImageView image = new ImageView(this);
                image.setImageResource(getResources().getIdentifier("femaleeducated", "drawable", getPackageName()));
                chart2.addView(image);
            }
            for (int i = 0; i < ((int) Math.ceil(femaleNotEducation) / 10); i++) {
                ImageView image = new ImageView(this);
                image.setImageResource(getResources().getIdentifier("female", "drawable", getPackageName()));
                chart2.addView(image);
            }
            //addRatio(femaleEducation, femaleNotEducation, chart2);


        } catch (NumberFormatException e) {
            mChart.removeAllViews();
            chart2.removeAllViews();
            mChart.setNoDataText("Data not available.");
            mChart.setNoDataTextDescription("");
        }

        LinearLayout chart3 = (LinearLayout) findViewById(R.id.chart3Ratio);
        try {
            mChart = (PieChart) findViewById(R.id.mChart3);
            mChart.removeAllViewsInLayout();
            mChart.setNoDataText("");
            mChart.setNoDataTextDescription("");
            String femaleparliaments = countries.getCountry(countryName).getparliaments(year);
            float femaleEmployment = Float.parseFloat(femaleparliaments);
            float maleEmployment = 100 - femaleEmployment;
            float[] employment = {maleEmployment, femaleEmployment};

            ArrayList<Entry> yData = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                yData.add(new Entry(employment[i], i));
            }

            ArrayList<String> xValue = new ArrayList<>();
            xValue.add("Male");
            xValue.add("Female");

            createPieChart(mChart, "Proportion of seats held in Parliament by gender", yData, xValue, year);

            chart3.removeAllViews();
            addRatio(femaleEmployment, maleEmployment, chart3);


        } catch (NumberFormatException e) {
            mChart.removeAllViews();
            chart3.removeAllViews();
            mChart.setNoDataText("Data not available.");
            mChart.setNoDataTextDescription("");

        }

    }

    private void addRatio(float female, float male, LinearLayout chart) {
        chart.removeAllViews();
        for (int i = 0; i < ((int) Math.ceil(female) / 10); i++) {
            ImageView image = new ImageView(this);
            image.setImageResource(getResources().getIdentifier("female", "drawable", getPackageName()));
            chart.addView(image);
        }
        for (int i = 0; i < ((int) Math.ceil(male) / 10); i++) {
            ImageView image = new ImageView(this);
            image.setImageResource(getResources().getIdentifier("male", "drawable", getPackageName()));
            chart.addView(image);
        }
    }

    /**
     * Method is used to create individual pie charts for the information layout. In this method
     * all of the properties are described.
     *
     * @param chart  layout to which the chart should be drawn on
     * @param title  title for the pie chart
     * @param yValue set of data for the y axis
     */
    private void createPieChart(PieChart chart, String title, ArrayList<Entry> yValue, ArrayList<String> xValue, String year) {

        mChart = new PieChart(this);
        chart.addView(mChart);

        chart.setBackgroundColor(Color.TRANSPARENT);

        mChart.setNoDataText("");
        mChart.setNoDataTextDescription("");

        mChart.setDescription("");

        mChart.setUsePercentValues(true);

        mChart.setCenterText(year);


        TextView textTitle = new TextView(this);
        textTitle.setText(title);
        textTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        textTitle.setTextColor(Color.BLACK);
        mChart.addView(textTitle);

        mChart.getLegend().setEnabled(false);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(20);
        mChart.setHoleColor(Color.rgb(248, 248, 242));
        mChart.setTransparentCircleRadius(5);

        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setRotationAngle(10);
        mChart.setRotationEnabled(true);

        //  EaseInExpo
//        mChart.animateXY(2000, 2000);
        mChart.animateY(2000, Easing.EasingOption.Linear);
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {

            }

            @Override
            public void onNothingSelected() {

            }
        });

        addDataToChart(yValue, xValue, title);

    }

    /**
     * This method is used to add data to the charts which are created in createPieChart method.
     *
     * @param yValue contains the data for the y axis
     * @param title  string with the title of the pie chart.
     */
    private void addDataToChart(ArrayList<Entry> yValue, ArrayList<String> xValue, String title) {

        PieDataSet dataSet = new PieDataSet(yValue, title);
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(10);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(255, 174, 93));
        colors.add(Color.rgb(248, 222, 189));

        dataSet.setColors(colors);

        PieData data = new PieData(xValue, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(13f);

        data.setValueTextColor(Color.rgb(111, 54, 98));
        data.setValueTextColor(Color.rgb(198, 61, 15));

        mChart.setData(data);

        mChart.highlightValue(null);
        mChart.invalidate();

    }

}