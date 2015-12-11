package boysenberry.europe;


        import android.test.ActivityInstrumentationTestCase2;
        import android.test.UiThreadTest;
        import android.widget.TextView;

        import com.google.android.gms.maps.model.LatLng;

public class MapsActivityTest extends ActivityInstrumentationTestCase2<MapsActivity> {
    public MapsActivityTest() {
        super(MapsActivity.class);
    }

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

        for (int i=0; i<3; i++) {
            String actual = country[i];
            LatLng coordinates = new LatLng(degree1[i], degree2[i]);
            mapsActivity.onMapClick(coordinates);

            String result = textCountryName.getText().toString();

            assertEquals(actual, result);
        }
    }

    @UiThreadTest
    public void testgetCountyName(){
        MapsActivity mapsActivity = getActivity();

        final TextView textCountryName = (TextView) mapsActivity.findViewById(R.id.textCountryName);

        for (int i=0; i<3; i++) {
            LatLng coordinates = new LatLng(degree1[i], degree2[i]);
            mapsActivity.onMapClick(coordinates);

            String countryName;
            mapsActivity.getCountryName(degree1[i], degree2[i]);

            countryName = textCountryName.getText().toString();

            assertEquals(country[i], countryName);
        }
    }

   /*
    public void createChartTest(){
        MapsActivity mapsActivity = getActivity();

        String [] categories = {"Male", "Female"};

        RelativeLayout chart1 = (RelativeLayout)mapsActivity.findViewById(R.id.chart1);

        final TextView textCountryPopulation = (TextView) mapsActivity.findViewById(R.id.textCountryPopulation);

        testActivity.onMapClick(germany);

        String population = textCountryPopulation.getText().toString();

        String femaleLabourPercentage = countries.getCountry("Germany").getLabour("2013");
        double femaleLabour = Double.parseDouble(femaleLabourPercentage);

        double maleLabour = 100 - femaleLabour;
        double[] labour = {maleLabour, femaleLabour};

        mapsActivity.createChart(labour,chart1,"");

        assertEquals(,);
    }*/

    @UiThreadTest
    public void testPopulation(){

        MapsActivity mapsActivity = getActivity();

        Countries countries;

        for (int i=0; i<3; i++) {
            LatLng coordinates = new LatLng(degree1[i], degree2[i]);
            mapsActivity.onMapClick(coordinates);

            String actualycountry = country[i];

            mapsActivity.updateInformationLayout(actualycountry, 2013);

            TextView textCountryPopulation = (TextView) mapsActivity.findViewById(R.id.textCountryPopulation);
            //String countries = "Germany";

            String germanPop = mapsActivity.getCountries().getCountry(country[i]).getPopulation("2013");
            String actualGermanPop = textCountryPopulation.getText().toString();

            assertEquals(actualGermanPop, germanPop);
        }
    }
}
