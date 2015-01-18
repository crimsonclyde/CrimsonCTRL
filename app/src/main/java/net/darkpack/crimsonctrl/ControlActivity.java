package net.darkpack.crimsonctrl;

/**
 * _________        .__
 * \_   ___ \_______|__| _____   __________   ____
 * /    \  \/\_  __ \  |/     \ /  ___/  _ \ /    \
 * \     \____|  | \/  |  | |  \\___ (  <_> )   |  \
 *  \______  /|__|  |__|__|_|  /____  >____/|___|  /
 *         \/                \/     \/           \/
 * Project: CrimsonCTRL
 * File: ControlActivity.java
 *
 * Author : CrimsonClyde
 * E-Mail : clyde_at_darkpack.net
 * Thx    : 4nt1g, Seelenfaenger, Bonnie
 * Use at your own risk! Keep Mordor tidy
 */

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;



public class ControlActivity extends Activity{
    // Set Activity name as debug tag
    public static final String TAG = ControlActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        //Declare and assign
        final Button showSclOnButton   = (Button) findViewById(R.id.sclButtonOn);
        final Button showSclOffButton  = (Button) findViewById(R.id.sclButtonOff);

        View.OnClickListener listenerOn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostClient().execute("LOW");
                Toast.makeText(ControlActivity.this, "StoneCircleLight ON", Toast.LENGTH_SHORT).show();
            }
        };
        showSclOnButton.setOnClickListener(listenerOn);

        View.OnClickListener listenerOff = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostClient().execute("HIGH");
                Toast.makeText(ControlActivity.this, "StoneCircleLight OFF", Toast.LENGTH_SHORT).show();
            }
        };
        showSclOffButton.setOnClickListener(listenerOff);



    }

    // We must do this as a background task, elsewhere our app crashes
    class PostClient extends AsyncTask<String, Void, String> {
        public String doInBackground(String... IO) {

            // Predefine variables
            String io = new String(IO[0]);
            URL url;

            try {
                // Fetch CoreID + AccessToken from SharedPreferences and constuct url and param string
                Log.d(TAG, "*******************   Fetch Results    *****************************");
                String coreid = SettingsConnector.readString(ControlActivity.this, SettingsConnector.COREID, null);
                String accesstoken = SettingsConnector.readString(ControlActivity.this, SettingsConnector.ACCESSTOKEN, null);
                Log.d(TAG, "CoreID:       " + coreid);
                Log.d(TAG, "AccessToken:  " + accesstoken);
                Log.d(TAG, "*******************   Building URL     *****************************");
                url = new URL("https://api.spark.io/v1/devices/"+coreid+"/SCL/");
                Log.d(TAG, "URL:          " + url);
                Log.d(TAG, "*******************   Building Params  *****************************");
                String param = "access_token="+accesstoken+"&params=d3,"+io;
                Log.d(TAG, "param:" + param);


                Log.d(TAG, "*****************   Opening Connection  ****************************");
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                con.setReadTimeout(7000);
                con.setConnectTimeout(7000);
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setInstanceFollowRedirects(false);
                con.setRequestMethod("POST");
                con.setFixedLengthStreamingMode(param.getBytes().length);
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Send
                PrintWriter out = new PrintWriter(con.getOutputStream());
                out.print(param);
                out.close();

                con.connect();

                BufferedReader in = null;
                if (con.getResponseCode() != 200) {
                    in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    Log.d(TAG, "!=200: " + in);
                } else {
                    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    Log.d(TAG, "POST request send successful: " + in);
                };


            } catch (Exception e) {
                Log.d(TAG, "Exception");
                e.printStackTrace();
                return null;
            }
        // Set null and we´e good to go
        return null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Handle presses on the action bar items
        switch (item.getItemId()) {

            // Main Activity
            case R.id.action_main:
                Toast.makeText(this, "Starting - Main Activity", Toast.LENGTH_SHORT).show();        // Could be removed, only for debugging reasons

                // Change Activity
                Intent intentMain = new Intent(ControlActivity.this, MainActivity.class);
                startActivity(intentMain);
                break;

            // Settings action
            case R.id.action_settings:
                Toast.makeText(this, "Starting - Settings Activity", Toast.LENGTH_SHORT).show();    // Could be removed, only for debugging reasons

                // Change Activity
                Intent intentSettings = new Intent(ControlActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                break;

            // Info Activity
            case R.id.action_info:
                // Just for debugging
                Toast.makeText(this, "Starting - Info Activity", Toast.LENGTH_SHORT).show();        // Could be removed, only for debugging reasons

                // Change Activity
                Intent intentInfo = new Intent(ControlActivity.this, InfoActivity.class);
                startActivity(intentInfo);
                break;


            default:
                break;
        }
        return true;
    }

}
