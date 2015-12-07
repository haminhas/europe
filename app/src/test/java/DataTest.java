import android.content.Context;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import boysenberry.europe.Countries;
import boysenberry.europe.Data;
import boysenberry.europe.MapsActivity;

/**
 * JUnit test for the Data class
 * Created by Tamara on 23/11/2015.
 */
public class DataTest extends TestCase {
    MapsActivity activity = new MapsActivity();
    Context context = activity.getAppContext();
    private static final ArrayList<String> names = new ArrayList<>(Arrays.asList("Albania", "Andorra", "Armenia", "Austria",
            "Azerbaijan", "Belgium", "Bulgaria", "Bosnia and Herzegovina", "Belarus", "Switzerland", "Cyprus",
            "Czech Republic", "Germany", "Denmark", "Spain", "Estonia", "Finland", "France", "United Kingdom",
            "Georgia", "Greece", "Croatia", "Hungary", "Ireland", "Iceland", "Italy", "Kosovo", "Liechtenstein",
            "Lithuania", "Luxembourg", "Latvia", "Monaco", "Moldova", "Macedonia, FYR", "Malta", "Montenegro",
            "Netherlands", "Norway", "Poland", "Portugal", "Romania", "Russian Federation", "San Marino", "Serbia",
            "Slovak Republic", "Slovenia", "Sweden", "Turkey", "Ukraine"));

    public void testSaveData() throws Exception {
        for(String country: names){
            Data.saveData(context, country, "Executing Data Test Now!");
        }
        Countries c = Data.getAllData(context);
        String austria = c.getCountry("Austria").getName();
        Assert.assertEquals("Executing Data Test Now!", austria);
    }
}
