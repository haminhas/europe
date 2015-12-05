import android.os.Handler;

import junit.framework.Assert;

import org.junit.Test;

import java.util.concurrent.ExecutionException;

import boysenberry.europe.Connector;

/**
 * Created by Hassan on 05/12/2015.
 */
public class ConnectorTest {
    Connector c = new Connector();

    @Test
    public void testBackground(){
        String z = "[{\"page\":1,\"pages\":1,\"per_page\":\"10000\",\"total\":1},[{\"indicator\":{\"id\":\"SL.EMP.TOTL.SP.FE.ZS\",\"value\":\"Employment to population ratio, 15+, female (%) (modeled ILO estimate)\"},\"country\":{\"id\":\"GB\",\"value\":\"United Kingdom\"},\"value\":null,\"decimal\":\"0\",\"date\":\"1990\"}]]";
        String s[] = c.r("http://api.worldbank.org/countries/GBR/indicators/SL.EMP.TOTL.SP.FE.ZS?format=json&date=1990&per_page=10000");
        Assert.assertNotNull(s);
        Assert.assertEquals(s[0],z);

        String z1 = "[{\"page\":1,\"pages\":1,\"per_page\":\"10000\",\"total\":1},[{\"indicator\":{\"id\":\"SL.EMP.TOTL.SP.FE.ZS\",\"value\":\"Employment to population ratio, 15+, female (%) (modeled ILO estimate)\"},\"country\":{\"id\":\"GB\",\"value\":\"United Kingdom\"},\"value\":\"51.4000015258789\",\"decimal\":\"0\",\"date\":\"2000\"}]]";
        String s1[] = c.r("http://api.worldbank.org/countries/GBR/indicators/SL.EMP.TOTL.SP.FE.ZS?format=json&date=2000&per_page=10000");
        Assert.assertNotNull(s1);
        Assert.assertEquals(s1[0],z1);
    }
}
