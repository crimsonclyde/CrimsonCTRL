package net.darkpack.crimsonctrl;

/**
 * _________        .__
 * \_   ___ \_______|__| _____   __________   ____
 * /    \  \/\_  __ \  |/     \ /  ___/  _ \ /    \
 * \     \____|  | \/  |  | |  \\___ (  <_> )   |  \
 *  \______  /|__|  |__|__|_|  /____  >____/|___|  /
 *         \/                \/     \/           \/
 * Project: CrimsonCTRL
 * File: MainActivity.java
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
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;





public class MainActivity extends Activity {
    // Set actvity name as debug tag
    public static final String TAG = HttpsClient.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // HttpsClient Class
    class HttpsClient extends AsyncTask<String, Void, String> {
        private Exception exception;
        public String doInBackground(String... urls) {

            try {
                Log.d(TAG, "*******************    Open Connection    *****************************");
                URL url = new URL(urls[0]);
                Log.d(TAG, "Received URL:  " + url);

                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                Log.d(TAG, "Con Status: " + con);

                InputStream in = con.getInputStream();
                Log.d(TAG, "GetInputStream:  " + in);

                Log.d(TAG, "*******************    String Builder     *****************************");
                String line = null;

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                Data data = new Data();

                while ((line = br.readLine()) != null) {
                    if (line.contains("event")) {
                        //do nothing since the event tag is of no interest
                        Log.d(TAG, "Loop: no needed content detected, try once again...");
                        continue;
                    }
                    if (line.contains("data: ")) {
                        //convert to JSON (stripping the beginning "data: "
                        JSONObject jObject = new JSONObject(line.substring(6));
                        String json_data = (String) jObject.get("data");
                        //convert again
                        jObject = new JSONObject(json_data);

                        //reading photocell
                        if (jObject.has("Photocell")) {
                            data.setPhoto(jObject.getInt("Photocell"));
                        }

                        //reading temp
                        if (jObject.has("Temperature")) {
                            data.setTemp(jObject.getInt("Temperature"));
                        }
                        //reading wifi signal strength (RSSI)
                        if (jObject.has("RSSI")) {
                            data.setWifi(jObject.getInt("RSSI"));
                        }

                        //reading StoneCircleLight (SCL)
                        if (jObject.has("SCL")) {
                            data.setSCL(jObject.getInt("SCL"));
                        }

                        //reading date
                        if (jObject.has("Day")) {
                            //build date
                            data.setDate(jObject.getInt("Year") + "-" + jObject.getInt("Month") + "-" + jObject.getInt("Day"));
                        }
                        //reading time
                        if (jObject.has("Hours")) {
                            //build time
                            data.setTime(jObject.getInt("Hours") + ":" + jObject.getInt("Minutes") + ":" + jObject.getInt("Seconds"));
                        }
                    }
                    //check if we have all needed data
                    if (data.isReady()) {
                        //exit endless connection
                        Log.d(TAG, "*******************    Data converted     *****************************");
                        Log.d(TAG, "data:  " + data);
                        Log.d(TAG, "Datestamp test:  " + data.getDatestamp());
                        break;
                    }
                }


                // Creation of finalized containers for UI usage
                final String gDate = data.getDate();
                final String gTime = data.getTime();
                final String gWifi = data.getWifi() + " dB";            // Needs to be converted to a string
                final String gTemp = data.getTemp() + "° Celsius";      // Needs to be converted to a string
                final String gPhoto = data.getPhoto() + "";             // Needs to be converted to a string
                final int gScl = data.getScl();                         // Needs to be converted to a string


                // To access the findViewById we need this to runOnUiThread
                runOnUiThread(new Runnable(){
                    public void run() {
                        Log.d(TAG, "*******************    Run UI Thread     *****************************");
                        Log.d(TAG, "gDate:   " + gDate);
                        Log.d(TAG, "gTime:   " + gTime);
                        Log.d(TAG, "gWifi:   " + gWifi);
                        Log.d(TAG, "gTemp:   " + gTemp);
                        Log.d(TAG, "gPhoto:  " + gPhoto);
                        Log.d(TAG, "gScl:    " + gScl);

                        // Assign and declare
                        final TextView updateDate  = (TextView) findViewById(R.id.dataDateTextView);
                        final TextView updateTime  = (TextView) findViewById(R.id.dataTimeTextView);
                        final TextView updateWifi  = (TextView) findViewById(R.id.dataWifiTextView);
                        final TextView updateTemp  = (TextView) findViewById(R.id.dataTempTextView);
                        final TextView updatePhoto = (TextView) findViewById(R.id.dataPhotoTextView);
                        final TextView updateScl   = (TextView) findViewById(R.id.dataSclTextView);




                        // Update the TextViews
                        Log.d(TAG, "*******************    Update TextView       *****************************");
                        updateDate.setText(gDate);
                        updateTime.setText(gTime);
                        updateWifi.setText(gWifi);
                        updateTemp.setText(gTemp);
                        updatePhoto.setText(gPhoto);
                        if (gScl >= 1) {
                            updateScl.setText("Off");
                        }
                        if (gScl == 0) {
                            updateScl.setText("On");
                        }
                    }

                });
                // Closing the stream
                Log.d(TAG, "*******************  Stream closed, exiting     *****************************");
                br.close();
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
            return null; }



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

    // Handle presses on the action bar items
    switch (item.getItemId()) {

        // Refresh action
        case R.id.action_refresh:
            Toast.makeText(this, "RELOAD DATA", Toast.LENGTH_SHORT).show();
            loadCrimsonCoreData();
            break;

        // SCL Control
        case R.id.action_control:
            Toast.makeText(this, "Starting - Control Activity", Toast.LENGTH_SHORT).show();         // Could be removed, only for debugging reasons

            // Change Activity
            Intent intentControl = new Intent(MainActivity.this, ControlActivity.class);
            startActivity(intentControl);
            break;

        // Settings action
        case R.id.action_settings:
            Toast.makeText(this, "Starting - Settings Activity", Toast.LENGTH_SHORT).show();        // Could be removed, only for debugging reasons

            // Change Activity
            Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intentSettings);
            break;

        // Info Screen
        case R.id.action_info:
            // Just for debugging
            Toast.makeText(this, "Starting - Info Activity", Toast.LENGTH_LONG).show();             // Could be removed, only for debugging reasons

            // Change Activity
            Intent intentInfo = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(intentInfo);
            break;

        default:
            break;
        }
        return true;


    }



    /*
     * Read the data from shared preferences
     * Requesting the value stored from the CrimsonCore on the spark-cloud
    */
    private void loadCrimsonCoreData() {
        // Get CoreID + AccessToken from shared preferences
        String coreid = SettingsConnector.readString(this, SettingsConnector.COREID, null);
        String accesstoken = SettingsConnector.readString(this, SettingsConnector.ACCESSTOKEN, null);
        Log.d(TAG, "CoreID:        " + coreid);
        Log.d(TAG, "AccessToken:   " + accesstoken);
        // Get values from spark-cloud
        new HttpsClient().execute("https://api.spark.io/v1/devices/"+coreid+"/events/?access_token="+accesstoken);

    }



}