package boysenberry.europe;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private TextView mTextView;
    private Geocoder geocoder;
    private Marker marker;
    private RelativeLayout rlInfo;

    LatLng center = new LatLng(57.75, 18);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this, Locale.getDefault());
        mTextView = (TextView)findViewById(R.id.mTextView);

        rlInfo = (RelativeLayout)findViewById(R.id.rlInfo);
        rlInfo.setVisibility(View.INVISIBLE);

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
                new LatLng(35,-25),
                new LatLng(70, 45)
        );

        LatLngBounds visibleBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        if (!europeBounds.contains(visibleBounds.getCenter())){
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
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        android.location.Address address = addresses.get(0);

        String countryName = "";
        if (address != null) {
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++){
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

    }

    public void closeInfo(View view) {
        rlInfo.setVisibility(View.INVISIBLE);
        marker.remove();
    }
}

