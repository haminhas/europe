package boysenberry.europe;


import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;
import android.widget.TextView;
import org.junit.Test;

import com.google.android.gms.maps.model.LatLng;

public class MapsActivityTest extends ActivityInstrumentationTestCase2<MapsActivity> {
    public MapsActivityTest() {
        super(MapsActivity.class);
    }

    @Test
    public void testMapsActivityExists(){
        MapsActivity mapsActivity = getActivity();
        assertNotNull(mapsActivity);
    }

    private String country [] = {"Germany","France", "Spain"};
    private int degree1 [] = {52,48,40};
    private int degree2 [] = {13,02,03};

    @UiThreadTest
    public void testClickCountry(){
        MapsActivity mapsActivity = getActivity();

        TextView textCountryName = (TextView) mapsActivity.findViewById(R.id.textCountryName);

        for (int i=0; i<2; i++) {
            String actual = country[i];
            LatLng coordinates = new LatLng(degree1[i], degree2[i]);
            mapsActivity.onMapClick(coordinates);

            String result = textCountryName.getText().toString();

            assertEquals(actual, result);
        }
    }

    @UiThreadTest
    public void testGetCountryName(){
        MapsActivity mapsActivity = getActivity();

        final TextView textCountryName = (TextView) mapsActivity.findViewById(R.id.textCountryName);

        for (int i=0; i<2; i++) {
            LatLng coordinates = new LatLng(degree1[i], degree2[i]);
            mapsActivity.onMapClick(coordinates);

            String countryName;
            mapsActivity.getCountryName(degree1[i], degree2[i]);

            countryName = textCountryName.getText().toString();

            assertEquals("Country name shown is incorrect.",country[i], countryName);
        }
    }

    @UiThreadTest
    public void testPopulation(){

        MapsActivity mapsActivity = getActivity();

        Countries countries;

        for (int i=0; i<2; i++) {
            LatLng coordinates = new LatLng(degree1[i], degree2[i]);
            mapsActivity.onMapClick(coordinates);

            String actualycountry = country[i];

            mapsActivity.updateInformationLayout(actualycountry, 2013);

            TextView textCountryPopulation = (TextView) mapsActivity.findViewById(R.id.textCountryPopulation);
            //String countries = "Germany";

            String germanPop = mapsActivity.getCountries().getCountry(country[i]).getPopulation("2013");
            String actualGermanPop = textCountryPopulation.getText().toString();

            assertEquals("The population show isnt't equal to the actual population.",actualGermanPop, germanPop);
        }
    }
}
