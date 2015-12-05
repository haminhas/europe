import android.util.Log;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import boysenberry.europe.Connector;
import boysenberry.europe.JSONparser;

/**
 * Created by Hassan on 05/12/2015.
 */
public class JSONparserTest extends TestCase {
    JSONparser p = new JSONparser();

    @Test
    public void testCountry() {
        String s= "[{\"page\":1,\"pages\":1,\"per_page\":\"100\",\"total\":1},[{\"id\":\"GBR\",\"iso2Code\":\"GB\",\"name\":\"United Kingdom\"," +
                "\"region\":{\"id\":\"ECS\",\"value\":\"Europe & Central Asia (all income levels)\"},\"adminregion\":{\"id\":\"\",\"value\":\"\"}," +
                "\"incomeLevel\":{\"id\":\"OEC\",\"value\":\"High income: OECD\"},\"lendingType\":{\"id\":\"LNX\",\"value\":\"Not classified\"}," +
                "\"capitalCity\":\"London\",\"longitude\":\"-0.126236\",\"latitude\":\"51.5002\"}]]";
        Assert.assertEquals(p.Country(s),"GB United Kingdom London");
    }

    public void testData(){
        String s = "[{\"page\":1,\"pages\":1,\"per_page\":\"10000\",\"total\":1},[{\"indicator\":{\"id\":\"SL.EMP.TOTL.SP.FE.ZS\",\"value\":\"Employment to population ratio, 15+, female (%) (modeled ILO estimate)\"},\"country\":{\"id\":\"GB\",\"value\":\"United Kingdom\"},\"value\":\"51.7999992370605\",\"decimal\":\"0\",\"date\":\"2013\"}]]";
        Assert.assertEquals(p.Data(s,""),"51.7999992370605,2013");
    }
}
