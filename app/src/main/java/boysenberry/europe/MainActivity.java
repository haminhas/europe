package boysenberry.europe;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private String[] names = {"Albania","Andorra","Armenia","Austria","Azerbaijan","Belgium","Bulgaria",
            "Bosnia and Herzegovina","Belarus","Switzerland","Cyprus","Czech Republic","Germany","Denmark",
            "Spain","Estonia","Finland","France","United Kingdom","Georgia","Greece","Croatia","Hungary",
            "Ireland","Iceland","Italy","Kosovo","Liechtenstein","Lithuania","Luxembourg","Latvia","Monaco",
            "Moldova","Macedonia, FYR","Malta","Montenegro","Netherlands","Norway","Poland","Portugal",
            "Romania","Russian Federation","San Marino","Serbia","Slovak Republic","Slovenia","Sweden",
            "Turkey","Ukraine"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isNetworkConnected(this)){
            Connector c = new Connector(this.getApplicationContext());
        } else {
            for (String s:names){
                Log.i("TESTING GET DATA:",Data.getData(s));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkConnected(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            // There are no active networks.
            return false;
        }
        //boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return true;
    }

}
