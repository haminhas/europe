package boysenberry.europe;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {

    // TODO check if Kosovo is working
    // TODO add flags to res folder
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

        seekBar = (SeekBar) findViewById(R.id.seekBarYear);
        seekBar.setMax(YEAR_END - YEAR_START);
        seekBar.setOnSeekBarChangeListener(new seekYearChange());
        seekBar.setVisibility(View.INVISIBLE);

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

    @Override
    public void onMapLongClick(LatLng latLng) {

//        // removes previously placed marker.
//        if (marker != null) {
//            marker.remove();
//        }// for testing purposes place marker
//        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(countryName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
//
//        // Getting country name
//        getCountryName(latLng.latitude, latLng.longitude);
//
//        textCountryName.setText(countryName.toUpperCase());
//        textCountryPopulation.setText(countries.getCountry(countryName).getPopulation("2013").toString());
//        textCountryCapital.setText(countries.getCountry(countryName).getCapital());
//        textYear.setText("2013");
//        seekBar.setProgress(2013);
//
//        createDougnutCharts("2013");
//
//        layoutInformation.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // Getting country name
        getCountryName(latLng.latitude, latLng.longitude);

        textCountryName.setText(countryName.toUpperCase());
        try {
            textCountryPopulation.setText(countries.getCountry(countryName).getPopulation("2013").toString());
            textCountryCapital.setText(countries.getCountry(countryName).getCapital());
        } catch(NullPointerException e) {

        }
        seekBar.setVisibility(View.VISIBLE);
        textYear.setText("2013");
        seekBar.setProgress(2013);

        createDougnutCharts("2013");
        //setFlag();

        layoutInformation.setVisibility(View.VISIBLE);
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
     * Creates multiple charts for the information layout.
     * Method always updates immediately and if a chart is not drawn then a message
     * is shown instead to warn the user about the missing data from the world bank.
     *
     * @param year creates charts for year specified
     */
    private void createDougnutCharts(String year) {

        TextView errorMessage = new TextView(this);

        // Creats a chart to show labour ratios between female and male
        RelativeLayout chart1 = (RelativeLayout) findViewById(R.id.chart1);
        try {
            String femaleLabourPercentage = countries.getCountry(countryName).getLabour(year);
            double femaleLabour = Double.parseDouble(femaleLabourPercentage);

            double maleLabour = 100 - femaleLabour;
            double[] labour = {maleLabour, femaleLabour};
            chart1.removeAllViews();
            createChart(labour, chart1, "Ratio of female to male labour force.");
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            //addErrorMessage(1, chart1);
            chart1.removeAllViews();
            errorMessage.setText("Chart 1 doesn't have data for this year.");
            chart1.addView(errorMessage);
        }

        // Chart to show percentage of labour force which is female and male
        RelativeLayout chart2 = (RelativeLayout) findViewById(R.id.chart2);
        try {
            String femaleEducationPercentage = countries.getCountry(countryName).getEducation(year);
            double femaleEducation = Double.parseDouble(femaleEducationPercentage);

            double maleEducation = 100 - femaleEducation;
            double[] education = {maleEducation, femaleEducation};
            chart2.removeAllViews();
            createChart(education, chart2, "Ratio of female to male education.");
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            //addErrorMessage(2, chart2);
            chart2.removeAllViews();
            errorMessage.setText("Chart 2 doesn't have data for this year.");
            chart2.addView(errorMessage);

        }

        // Chart to show percentage of labour force which is female and male
        RelativeLayout chart3 = (RelativeLayout) findViewById(R.id.chart3);
        try {
            String femaleEmploymentPercentage = countries.getCountry(countryName).getPercentageFemale(year);
            double femaleEmployment = Double.parseDouble(femaleEmploymentPercentage);

            double maleEmployment = 100 - femaleEmployment;
            double[] employment = {maleEmployment, femaleEmployment};
            chart3.removeAllViews();
            createChart(employment, chart3, "Ratio of female to male labour force.");
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();

            chart3.removeAllViews();
            errorMessage.setText("Chart 3 doesn't have data for this year.");
            chart3.addView(errorMessage);
        }

    }

    /**
     * Creates individual doughnut charts for the information layout.
     *
     * @param population an array of ratios for different series
     * @param layout     a certain layout in which the chart will be drawn on
     * @param title      a strin which has the title for the chart
     */
    public void createChart(double[] population, RelativeLayout layout, String title) {

        int[] colors = new int[]{Color.rgb(255, 26, 25), Color.rgb(6, 95, 247)};
        String[] categories = new String[]{"Male", "Female"};

        GraphicalView mChartView;
        MultipleCategorySeries mSeries = new MultipleCategorySeries("");
        DefaultRenderer mRenderer = new DefaultRenderer();

        mSeries.add(categories, population);

        for (int i = 0; i < population.length; i++) {
            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
            seriesRenderer.setColor(colors[i]);
            seriesRenderer.setDisplayBoundingPoints(true);
            mRenderer.addSeriesRenderer(seriesRenderer);
        }

        // Defining how the doughnut chart should look.
        mRenderer.setPanEnabled(false);
        mRenderer.setAntialiasing(true);
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setShowLabels(false);
        mRenderer.setDisplayValues(true);
        mRenderer.setShowLegend(false);
        mRenderer.setZoomEnabled(false);
        mRenderer.setBackgroundColor(Color.TRANSPARENT);
        //mRenderer.setScale(0.5f);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setChartTitle(title);
        mRenderer.setChartTitleTextSize(20);

        mChartView = ChartFactory.getDoughnutChartView(this, mSeries, mRenderer);
        mChartView.repaint();
        layout.addView(mChartView);
    }

    // Delete this method.
    private void addErrorMessage(int index, RelativeLayout chart) {
        TextView errorMessage = new TextView(this);

        if (index == 1) {
            chart.removeAllViews();
            errorMessage.setText("Chart 1 doesn't have data for this year.");
            chart.addView(errorMessage);
        }
        if (index == 2) {
            chart.removeAllViews();
            errorMessage.setText("Chart 2 doesn't have data for this year.");
            chart.addView(errorMessage);
        }
        if (index == 3) {
            chart.removeAllViews();
            errorMessage.setText("Chart 3 doesn't have data for this year.");
            chart.addView(errorMessage);
        }
    }

    public void closeButton(View view) {
        //layoutInformation.setVisibility(View.INVISIBLE);

        String country = "unitedkingdom";

        textCountryName.setText(country);
//        textCountryPopulation.setText(countries.getCountry(country).getPopulation("2013").toString());
//        textCountryCapital.setText(countries.getCountry(country).getCapital());
//        textYear.setText("2013");
//        seekBar.setProgress(2013);
//
//        createDougnutCharts("2013");
//        setFlag(country);
    }

    /**
     * Check for changes on seek bar. When the user uses it the information layout
     * is updated accordingly.
     */
    private class seekYearChange implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int year = YEAR_START + progress;

            createDougnutCharts(year + "");
            textYear.setText(year + "");
            textCountryPopulation.setText(countries.getCountry(countryName).getPopulation(year + ""));

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    }

}
