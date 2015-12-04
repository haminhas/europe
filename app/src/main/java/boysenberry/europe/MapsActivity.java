package boysenberry.europe;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener {

    private Countries countries;

    private GoogleMap mMap;
    private RelativeLayout layoutInformation;
    private TextView textCountryName;
    private TextView textCountryPopulation;
    private SeekBar seekBar;

    private GoogleApiClient client;
    private Geocoder geocoder;
    private Marker marker;

    private final LatLng CENTER = new LatLng(57.75, 18);
    private final int YEAR_END = 2013;
    private final int YEAR_START = 1990;
    private String countryName;

    // CHARTS
    private static int[] COLORS = new int[] {Color.BLUE, Color.RED};
    private static double[] VALUES =new double[] { 10, 11};
    private static String[] NAME_LIST = new String[] {"Male", "Female"};


    public void createChart(String[] categories, double[] population, RelativeLayout layout) {

        GraphicalView mChartView;
        MultipleCategorySeries mSeries = new MultipleCategorySeries("");
        DefaultRenderer mRenderer = new DefaultRenderer();

        mSeries.add(categories, population);

        for(int i = 0; i < population.length; i++) {
            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
            seriesRenderer.setColor(COLORS[i]);
            seriesRenderer.setDisplayBoundingPoints(false);
            mRenderer.addSeriesRenderer(seriesRenderer);
        }

        mRenderer.setPanEnabled(false);
        mRenderer.setAntialiasing(true);
        mRenderer.setZoomButtonsVisible(true);

        mRenderer.setDisplayValues(true);

        mRenderer.setShowLegend(false);
        mRenderer.setZoomEnabled(false);
        mRenderer.setBackgroundColor(Color.MAGENTA);
        //mRenderer.setScale(0.5f);
        mRenderer.setLabelsTextSize(15);
        //mRenderer.


        mRenderer.setLabelsColor(Color.BLACK);

        //mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
        mChartView = ChartFactory.getDoughnutChartView(this, mSeries, mRenderer);


        mChartView.repaint();
        layout.addView(mChartView);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkNetwork();
        startUp();
    }

    private void startUp() {

        geocoder = new Geocoder(this, Locale.getDefault());
        layoutInformation = (RelativeLayout) findViewById(R.id.layoutInformation);
        //layoutInformation.setVisibility(View.INVISIBLE);
        layoutInformation.setAlpha(0.95f);

        textCountryName = (TextView) findViewById(R.id.textCountryName);
        textCountryPopulation = (TextView) findViewById(R.id.textCountryPopulation);

        seekBar = (SeekBar)findViewById(R.id.seekBarYear);
        seekBar.setMax(YEAR_END - YEAR_START);
        seekBar.setOnSeekBarChangeListener(new seekYearChange());


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void checkNetwork() {
        if (isNetworkConnected(this)) {
            Connector c = new Connector(this.getApplicationContext());
            countries = c.getCountries();
        } else {
            try {
                countries = Data.getAllData(this.getApplicationContext());
            } catch (RuntimeException e) {
                Log.i("Runtime Exception", "Please connect to the internet");
            }
        }
    }

    private boolean isNetworkConnected(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            // There are no active networks.
            return false;
        }
        //boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return true;
    }


    // Maps API
    // https://developers.google.com/android/reference/com/google/android/gms/maps/package-summary

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng germany = new LatLng(52, 13);
        //mMap.animateCamera(CameraUpdateFactory.newLatLng(germany));

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMapClickListener(this);

        //Sets camera to the centre point in Europe at zoom level 4 so all European countries are shown
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 4));
//        mMap.addMarker(new MarkerOptions().position(germany).title("Tap and hold to view infographics for European countries."));
//        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition cameraPosition) {
//                boundsCheck();
//            }
//        });

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

        // removes previously placed marker.
        if (marker != null) {
            marker.remove();
        }
        // for testing purposes place marker
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(countryName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        // Getting country name
        getCountryName(latLng.latitude, latLng.longitude);
        textCountryName.setText(countryName);
        textCountryPopulation.setText(countries.getCountry(countryName).getPopulation("2012").toString());

//        textCountryName.setText("Germany");
        //textCountryPopulation.setText("1,000,000");

        RelativeLayout chart1 = (RelativeLayout)findViewById(R.id.chart1);

        // Creating chart
        createChart(NAME_LIST, VALUES, chart1);

        double[] VALUES =new double[] { 100, 400};
        String[] NAME_LIST = new String[] {"MMale", "FFemale"};

        RelativeLayout chart2 = (RelativeLayout)findViewById(R.id.chart2);

        createChart(NAME_LIST, VALUES, chart2);

        layoutInformation.setVisibility(View.VISIBLE);
    }

    public void getCountryName(double latitude, double longitude) {
        countryName = "";

        Log.d("MainActivity", "WHY WONT YOU WORK>!");
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Log.d("MainActivity", "Got the country name.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MainActivity", e.toString());
        }

        Address address = addresses.get(0);

        if (address != null) {
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++){
                countryName = address.getCountryName();
            }
        }
        if(countryName.equals("Macedonia (FYROM)")) {
            countryName = "Macedonia, FYR";
        }
        else if(countryName.equals("Kosova (Kosovo)")) {
            countryName = "Kosovo";
        }
        else if(countryName.equals("Slovakia")) {
            countryName = "Slovak Republic";
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    private class seekYearChange implements SeekBar.OnSeekBarChangeListener {

        // for testing purposes only
        TextView text = (TextView)findViewById(R.id.textView);

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            text.setText(Integer.toString(YEAR_START + progress));
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar) {}

    }

    public void closeButton(View view) {
        //layoutInformation.setVisibility(View.INVISIBLE);
    }

}
