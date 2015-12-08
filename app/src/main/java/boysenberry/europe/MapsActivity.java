package boysenberry.europe;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {


    // TODO add line chart
    // TODO Remove pointer at germany
    // TODO add population string before
    // TODO make information layout invisible when app first starts
    // TODO Country flag should be alligned to the right

    private Connector con;
    private String[] json;

    private GoogleApiClient client;
    private Geocoder geocoder;
    private Marker marker;

    private GoogleMap mMap;
    private RelativeLayout layoutInformation;
    private SeekBar seekBar;
    private ImageView imageCountryFlag;
    private TextView textCountryName;
    private TextView textCountryPopulation;
    private TextView textCountryCapital;
    private TextView textYear;
    private Spinner spinnerCountries;

    private Countries countries;
    private String countryName;

    private final LatLng CENTER = new LatLng(57.75, 18);
    private final int YEAR_END = 2013;
    private final int YEAR_START = 1991;

    ArrayList<String> countryList = new ArrayList<>();

    private PieChart mChart;

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

    }

    public Context getAppContext(){
        return getApplicationContext();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//        mMap.getUiSettings().setMapToolbarEnabled(true);
//        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng germany = new LatLng(52, 13);
        //mMap.animateCamera(CameraUpdateFactory.newLatLng(germany));

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);

        //Sets camera to the centre point in Europe at zoom level 4 so all European countries are shown
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 4));
        mMap.addMarker(new MarkerOptions().position(germany).title("Tap and hold to view infographics for European countries."));
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
        //layoutInformation.setVisibility(View.INVISIBLE);

        textCountryName = (TextView) findViewById(R.id.textCountryName);
        textCountryPopulation = (TextView) findViewById(R.id.textCountryPopulation);
        textCountryCapital = (TextView) findViewById(R.id.textCountryCapital);
        textYear = (TextView) findViewById(R.id.textYear);
        imageCountryFlag = (ImageView) findViewById(R.id.imageCountryFlag);



        spinnerCountries = (Spinner) findViewById(R.id.spinnerCountry);
        //spinnerCountries.addChildrenForA;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countryList);
        spinnerCountries.setAdapter(adapter);
        spinnerCountries.setOnItemSelectedListener(new spinnerListener());

        seekBar = (SeekBar) findViewById(R.id.seekBarYear);
        seekBar.setMax(YEAR_END - YEAR_START);
        seekBar.setOnSeekBarChangeListener(new seekYearChange());
//        seekBar.setVisibility(View.INVISIBLE);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void checkNetwork() {
        if (isNetworkConnected(this)) {
            String country = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR?per_page=100&format=json";
            String female = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SL.EMP.TOTL.SP.FE.ZS?format=json&date=1990:2013&per_page=10000";
            String population = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SP.POP.TOTL?format=json&date=1990%3A2013&per_page=10000";
            String fpop = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SP.POP.TOTL.FE.ZS?format=json&date=1990%3A2013&per_page=10000";
            String education = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SL.TLF.TERT.FE.ZS?format=json&date=1990:2013&per_page=10000";
            String labour = "http://api.worldbank.org/countries/ALB;AND;ARM;AUT;AZE;BLR;BEL;BIH;BGR;HRV;CYP;CZE;DNK;EST;FIN;FRA;GEO;DEU;GRC;HUN;ISL;IRL;ITA;KAZ;KSV;LVA;LIE;LTU;LUX;MKD;MLT;MCO;MDA;MNE;NLD;NOR;POL;PRT;ROU;RUS;SMR;SRB;SVK;SVN;ESP;SWE;CHE;TUR;UKR;GBR/indicators/SL.TLF.TOTL.FE.ZS?format=json&date=1990:2013&per_page=10000";

            String[] ar = {country, female, population, fpop, education, labour};
            con = new Connector();
            json = new String[6];
            try {
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

            p.saveData(this.getApplicationContext());
            countries = p.getCountries();
        } else {
            try {
                countries = Data.getAllData(this.getApplicationContext());
                // TODO remove google maps and set info layout to take up 70% of screen space
                RelativeLayout layoutSpinner = (RelativeLayout)findViewById(R.id.layoutSpinner);
                layoutSpinner.setHorizontalGravity(50);
                layoutInformation.setHorizontalGravity(50);
            } catch (RuntimeException e) {
                Log.i("Runtime Exception", "Please connect to the internet");
            }
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
                new LatLng(35, -25),
                new LatLng(70, 45)
        );

        LatLngBounds visibleBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        if (!europeBounds.contains(visibleBounds.getCenter())) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 4));
        }
    }


    /**
     * Method is used to get a countries name using the latitude and longitude. Method doesn't return
     * anything but updates the countryName variable which is important for other features of the app.
     * <p/>
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
            Log.d("MainActivity", "Got the country name.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MainActivity", e.toString());
        }

        Address address;

        try{
            address = addresses.get(0);
            if (address != null) {
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    countryName = address.getCountryName();
                }
            }
        }catch(IndexOutOfBoundsException e) {
            Toast.makeText(getApplicationContext(), "Click on a country you fool", Toast.LENGTH_SHORT).show();
            countryName = "United Kingdom";
        }

        if (countryName.equals("Macedonia (FYROM)")) {
            countryName = "Macedonia, FYR";
        } else if (countryName.equals("Kosova (Kosovo)")) {
            countryName = "Kosovo";
        } else if (countryName.equals("Slovakia")) {
            countryName = "Slovak Republic";
        } else if (countryName.equals("Russia")) {
            countryName = "Russian Federation";
        }

    }

    /**
     * Method checks which country has been clicked and then gets the flag for that
     * country and sets it as a resource for imageCountryFlag.
     *
     * @param country String value with the country name
     */
    public void setFlag(String country) {

//  Countries
//    "Albania", "Andorra", "Armenia", "Austria",
//    "Azerbaijan", "Belgium", "Bulgaria", "Bosnia and Herzegovina", "Belarus", "Switzerland", "Cyprus",
//    "Czech Republic", "Germany", "Denmark", "Spain", "Estonia", "Finland", "France", "United Kingdom",
//    "Georgia", "Greece", "Croatia", "Hungary", "Ireland", "Iceland", "Italy", "Kosovo", "Liechtenstein",
//    "Lithuania", "Luxembourg", "Latvia", "Monaco", "Moldova", "Macedonia, FYR", "Malta", "Montenegro",
//    "Netherlands", "Norway", "Poland", "Portugal", "Romania", "Russian Federation", "San Marino", "Serbia",
//    "Slovak Republic", "Slovenia", "Sweden", "Turkey", "Ukraine"};

        if(country.equals("Bosnia and Herzegovina")) {
            country = "bosniaandherzegovina";
        }
        else if(country.equals("Czech Republic")) {
            country = "czechrepublic";
        }
        else if(country.equals("United Kingdom")) {
            country = "unitedkingdom";
        }
        else if(country.equals("Macedonia, FYR")) {
            country = "macedoniafyr";
        }
        else if(country.equals("Russian Federation")) {
            country = "russia";
        }
        else if(country.equals("San Marino")) {
            country = "sanmarino";
        }
        else if(country.equals("Slovak Republic")) {
            country = "slovak";
        }

        String flag = country.toLowerCase();
        imageCountryFlag.setImageResource(getResources().getIdentifier(flag, "drawable", getPackageName()));
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
            textYear.setText(year + "");
            textCountryPopulation.setText(countries.getCountry(countryName).getPopulation(year + ""));
        }

        @Override   public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override   public void onStopTrackingTouch(SeekBar seekBar) {}

    }

    private class spinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            countryName = parent.getItemAtPosition(position).toString();
            updateInformationLayout(countryName, YEAR_END);
            setFlag(countryName);

            //textCountryName.setText(countryName);


            // Showing selected spinner item
            //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }
        public void onNothingSelected(AdapterView<?> arg0) {}

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

        getCountryName(latLng.latitude, latLng.longitude);
        updateInformationLayout(countryName, YEAR_END);

        layoutInformation.setVisibility(View.VISIBLE);

    }

    private void updateInformationLayout(String country, int year) {

        textCountryName.setText(country);
        try {
            textCountryPopulation.setText(countries.getCountry(country).getPopulation(year + "").toString());
            textCountryCapital.setText(countries.getCountry(country).getCapital());
        } catch(NullPointerException e) {

        }
        textYear.setText(year + "");
        String yearChart  = year + "";
        // TODO pie chart
        //createDougnutCharts(yearChart);
        seekBar.setVisibility(View.VISIBLE);

        createAllCharts(yearChart);
    }

    private void createAllCharts(String year) {

        TextView errorMessage = new TextView(this);

        try {
            mChart = (PieChart) findViewById(R.id.mChart1);
            String femaleLabourPercentage = countries.getCountry(countryName).getLabour(year);
            float femaleLabour = Float.parseFloat(femaleLabourPercentage);
            float maleLabour = 100 - femaleLabour;
            float[] labour = {maleLabour, femaleLabour};

            ArrayList<Entry> yData = new ArrayList<>();
            for (int i = 0; i < labour.length; i++) {
                yData.add(new Entry(labour[i], i));
            }

            createPieChart(mChart, "Female Labour", yData);
            mChart.invalidate();
        } catch (NumberFormatException e) {
            mChart.removeAllViews();
            errorMessage.setText("Chart 1 doesn't have data for this year.");
            //mChart.addView(errorMessage);
        }

        try {
            mChart = (PieChart) findViewById(R.id.mChart2);
            String femaleEducationPercentage = countries.getCountry(countryName).getEducation(year);
            float femaleEducation = Float.parseFloat(femaleEducationPercentage);
            float maleEducation = 100 - femaleEducation;
            float[] education = {maleEducation, femaleEducation};

            ArrayList<Entry> yData = new ArrayList<>();
            for (int i = 0; i < education.length; i++) {
                yData.add(new Entry(education[i], i));
            }

            createPieChart(mChart, "Female Education", yData);
            mChart.invalidate();
        } catch (NumberFormatException e) {
            mChart.removeAllViews();
            errorMessage.setText("Chart 2 doesn't have data for this year.");
            //mChart.addView(errorMessage);
        }

        try {
            mChart = (PieChart) findViewById(R.id.mChart3);
            String femaleEmploymentPercentage = countries.getCountry(countryName).getPercentageFemale(year);
            float femaleEmployment = Float.parseFloat(femaleEmploymentPercentage);
            float maleEmployment = 100 - femaleEmployment;
            float[] employment = {maleEmployment, femaleEmployment};

            ArrayList<Entry> yData = new ArrayList<Entry>();
            for(int i = 0; i < employment.length; i++) {
                yData.add(new Entry(employment[i], i));
            }

            createPieChart(mChart, "Female Employment", yData);
            mChart.invalidate();



        } catch (NumberFormatException e) {
            mChart.removeAllViews();
            errorMessage.setText("Chart 3 doesn't have data for this year.");
            //mChart.addView(errorMessage);
        }
    }


    private void createPieChart(PieChart chart, String title, ArrayList<Entry> yValue) {

        ArrayList<String> xValue = new ArrayList<>();
        xValue.add("Male");
        xValue.add("Female");

        mChart = new PieChart(this);
        chart.addView(mChart);

        chart.setBackgroundColor(Color.WHITE);

        mChart.setUsePercentValues(true);
        mChart.setDescription(title);
        mChart.getLegend().setEnabled(false);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(20);
        mChart.setTransparentCircleRadius(10);

        mChart.setExtraOffsets(5, 10, 5, 5);
        mChart.setCenterText("Hello");
        mChart.setNoDataText("00");

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);

        mChart.animateXY(2000, 2000);

        // TODO work this shit out
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

    private void addDataToChart(ArrayList<Entry> yValue, ArrayList<String> xValue, String title) {

        PieDataSet dataSet = new PieDataSet(yValue, title);
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        colors.add(ColorTemplate.getHoloBlue());
        colors.add(Color.RED);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(xValue, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        mChart.setData(data);

        mChart.highlightValue(null);
        mChart.invalidate();

    }

}