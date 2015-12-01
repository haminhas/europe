package boysenberry.europe;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
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
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private Countries countries;

    private GoogleMap mMap;
    private TextView mTextView;
    private Geocoder geocoder;
    private Marker marker;
    private RelativeLayout rlInfo;

    private TableLayout tlChart;

    private String [] people = {"male", "female", "alien"};
    private double [] pop = {3, 4, 5};

    LatLng center = new LatLng(57.75, 18);
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this, Locale.getDefault());
        mTextView = (TextView) findViewById(R.id.mTextView);

        rlInfo = (RelativeLayout) findViewById(R.id.rlInfo);
        rlInfo.setVisibility(View.INVISIBLE);

        tlChart = (TableLayout) findViewById(R.id.chartslayout);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    // Map API
    // https://developers.google.com/android/reference/com/google/android/gms/maps/package-summary
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng germany = new LatLng(52, 13);
        //mMap.animateCamera(CameraUpdateFactory.newLatLng(germany));

        //Sets camera to the centre point in Europe at zoom level 4 so all European countries are shown
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 4));

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);

        mMap.addMarker(new MarkerOptions().position(germany).title("Tap and hold to view infographics for European countries."));

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                boundsCheck();
            }
        });
    }

    //Checks if the centre point of the camera is outside of the Europe bound, and if so resets the camera back to Europe.
    public void boundsCheck() {

        LatLngBounds europeBounds = new LatLngBounds(
                new LatLng(35, -25),
                new LatLng(70, 45)
        );

        LatLngBounds visibleBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        if (!europeBounds.contains(visibleBounds.getCenter())) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 4));
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;
        mTextView.setText("tapped, point = " + latitude + " lng" + longitude);

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = addresses.get(0);

        String countryName = "";
        if (address != null) {
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                countryName = address.getCountryName();
            }
        }

        // Removes previously placed marker.
        if (marker != null) {
            marker.remove();
        }

        //place marker where user just clicked
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(countryName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        // changes the text view on top of the map
        mTextView.setText("tapped!, point = " + latLng + "Country " + countryName);
        rlInfo.setVisibility(View.VISIBLE);

        //System.out.prinln(countries.getList().get(0).getPercentageFemale());


        for(int i = 0; i < countries.getList().size(); i++) {

        }

        createChart(people, pop);

    }

    // A method that create the chart.
    // categries eg male,female. Population 2.3, 4.5.
    public void createChart(String[] categories, double[] population) {

        // Color of each Pie Chart Sections
        int[] colors = {Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.RED,
                Color.YELLOW, Color.GRAY};

        // Instantiating CategorySeries to plot Pie Chart
        CategorySeries distributionSeries = new CategorySeries("");
        for (int i = 0; i < population.length; i++) {
            // Adding a slice with its values and name to the Pie Chart
            distributionSeries.add(categories[i], population[i]);
        }

        // Instantiating a renderer for the Pie Chart
        DefaultRenderer defaultRenderer = new DefaultRenderer();
        for (int i = 0; i < population.length; i++) {
            SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();
            seriesRenderer.setColor(colors[i]);
            seriesRenderer.setDisplayBoundingPoints(false);
            // Adding a renderer for a slice
            defaultRenderer.addSeriesRenderer(seriesRenderer);
        }


        //defaultRenderer.setChartTitle(title);
        //defaultRenderer.setChartTitleTextSize(50);
        // disables moving the graph
        defaultRenderer.setPanEnabled(false);
        //disables the key
        defaultRenderer.setShowLegend(false);
        //defaultRenderer.setLegendHeight(1);
        defaultRenderer.setScale(0.5f);
        defaultRenderer.setLabelsTextSize(25);
        defaultRenderer.setLabelsColor(Color.BLACK);

        // creates pie chart with dataset and Renderer
        GraphicalView mChartView = ChartFactory.getPieChartView(this, distributionSeries, defaultRenderer);

        mChartView.repaint();

        //Change rlInfo
        tlChart.addView(mChartView);
    }

    public void closeInfo(View view) {
        rlInfo.setVisibility(View.INVISIBLE);
        marker.remove();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://boysenberry.europe/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://boysenberry.europe/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

