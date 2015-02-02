package net.darkpack.crimsonctrl;

/**
 * _________        .__
 * \_   ___ \_______|__| _____   __________   ____
 * /    \  \/\_  __ \  |/     \ /  ___/  _ \ /    \
 * \     \____|  | \/  |  | |  \\___ (  <_> )   |  \
 *  \______  /|__|  |__|__|_|  /____  >____/|___|  /
 *         \/                \/     \/           \/
 *
 * Project: CrimsonCTRL
 *
 *
 * Author : CrimsonClyde
 * E-Mail : clyde_AT_darkpack.net
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import static net.darkpack.crimsonctrl.SettingsConnector.getEditor;


public class MainActivity extends Activity {
    // Set actvity name as debug tag
    public static final String TAG = HttpsClient.class.getSimpleName();
    protected ProgressBar mProgressBar;
    Button scButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scButtonListener();

        // Progress Bar
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (isNetworkAvailable()) {

            mProgressBar.setVisibility(View.VISIBLE);
            loadCrimsonCoreData();
        } else {
            Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG).show();
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

    // Handle presses on the action bar items
    switch (item.getItemId()) {

        // Refresh action
        case R.id.action_refresh:
            Toast.makeText(this, "RELOAD DATA", Toast.LENGTH_SHORT).show();
            loadCrimsonCoreData();
            break;

        // Cam Action
        case R.id.action_cam:
            Toast.makeText(this, "Starting - Camera Activity", Toast.LENGTH_SHORT).show();         // Could be removed, only for debugging reasons

            // Change Activity
            Intent intentCam = new Intent(MainActivity.this, MjpegActivity.class);
            startActivity(intentCam);
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
    * IsNetworkAvailable
    *
    * Check if Network is up and running
    */
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }



    /*
    * Create OnClickListener
    *
    * Create listener for the refresh function
    */
    public void scButtonListener() {

        scButton = (Button) findViewById(R.id.scButtonSelector);

        scButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Get CoreID + AccessToken from shared preferences
                String coreid = SettingsConnector.readString(MainActivity.this, SettingsConnector.COREID, null);
                String accesstoken = SettingsConnector.readString(MainActivity.this, SettingsConnector.ACCESSTOKEN, null);
                // Execute
                new SclCtrl().execute("https://api.spark.io/v1/devices/"+coreid+"/events/?access_token="+accesstoken);

                return false;
            }
        });

    }



    /*
    * Initiate HttpClient
    *
    * Retrieve CoreID & AccessToken from SharedPreferences
    * Execute HttpClient
    */
    private void loadCrimsonCoreData() {
        // Get CoreID + AccessToken from shared preferences
        String coreid = SettingsConnector.readString(this, SettingsConnector.COREID, null);
        String accesstoken = SettingsConnector.readString(this, SettingsConnector.ACCESSTOKEN, null);
        Log.d(TAG, "************    Retrieve data from Shared Preferences     ************");
        Log.d(TAG, "CoreID:        " + coreid);
        Log.d(TAG, "AccessToken:   " + accesstoken);

        Log.d(TAG, "*********************    Execute HttpsClient     *********************");
        new HttpsClient().execute("https://api.spark.io/v1/devices/"+coreid+"/events/?access_token="+accesstoken);
    }


    /*
    * Restful Events from Spark-Cloud
    *
    * GET values stored in the cloud via httpsurlconnection
    * and save 'em in Androids SharedPreferences
    */
    class HttpsClient extends AsyncTask<String, Void, String> {
        public static final String PREFS_NAME = "TEMP_SETTINGS";
        public static final String mPHOTO = "PHOTO";
        public static final String mTEMP = "TEMP";
        public static final String mSCL = "SCL";
        private Exception exception;


        public String doInBackground(String... urls) {

            try {
                Log.d(TAG, "**********************************************************************");
                Log.d(TAG, "******************    HttpsUrlConnection    **************************");
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
                            data.setTemp(jObject.getString("Temperature"));
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
                        Log.d(TAG, "*********************    Data received    ****************************");
                        Log.d(TAG, "data:  " + data);
                        Log.d(TAG, "Datestamp test:  " + data.getDatestamp());
                        break;
                    }
                }

                // Now we create finalized containers for further usage
                Log.d(TAG, "**********************************************************************");
                Log.d(TAG, "*********************    Data received    ****************************");
                Log.d(TAG, "gDate:   " + data.getDate());
                Log.d(TAG, "gTime:   " + data.getTime());
                Log.d(TAG, "gWifi:   " + data.getWifi().toString());
                Log.d(TAG, "gTemp:   " + data.getTemp());
                Log.d(TAG, "gPhoto:  " + data.getPhoto().toString());
                Log.d(TAG, "gScl:    " + data.getScl().toString());



                // Let´s put the results into the shared preferences
                // TODO: Time+Date query if stored data <1Minute use data from shared preferences
                Log.d(TAG, "***********    Store Data in Shared Preferences     ******************");
                final SharedPreferences[] sharedValues = {MainActivity.this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)};
                SharedPreferences.Editor editor = sharedValues[0].edit();
                editor.clear();
                editor.putString("PHOTO", data.getPhoto().toString());
                editor.putString("TEMP", data.getTemp());
                editor.putString("SCL", data.getScl().toString());
                editor.apply();


                // To access the findViewById we need this to runOnUiThread
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "*******************    Run UI Thread     *****************************");
                        // Assign & declare TextViews
                        final TextView updateTemp = (TextView) findViewById(R.id.textTemperature);
                        final TextView updatePhoto = (TextView) findViewById(R.id.textResistance);
                        final TextView updateScl = (TextView) findViewById(R.id.textStoneCircle);


                        // Retrieve data from shared preferences
                        Log.d(TAG, "************    Retrieve data from Shared Preferences     ************");
                        sharedValues[0] = MainActivity.this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        final String photo = sharedValues[0].getString("PHOTO", "");
                        final String temp = sharedValues[0].getString("TEMP", "");
                        final int scl = Integer.parseInt(sharedValues[0].getString("SCL", ""));
                        Log.d(TAG, "photo var in shared pref: " + photo);
                        Log.d(TAG, "temp var in shared pref: " + temp);
                        Log.d(TAG, "scl var in shared pref: " + scl);


                        // Update the TextViews
                        Log.d(TAG, "*****************    Update TextView       ***************************");
                        if      (temp != null)  { updateTemp.setText(temp); }
                        else                    { updateTemp.setText("ERROR"); }

                        if      (photo != null) { updatePhoto.setText(photo); }
                        else                    { updatePhoto.setText("ERROR"); }

                        if      (scl == 1)  { updateScl.setText("Off"); }
                        else if (scl == 0)  { updateScl.setText("On");  }
                        else                { updateScl.setText("ERROR"); }
                    }

                });

                Log.d(TAG, "****************  Closing stream, exiting     ************************");

                // Closing the stream
                br.close();
                mProgressBar.setVisibility(View.INVISIBLE);
                Log.d(TAG, "**********************************************************************");
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
            return null; }
    }


    /*
    * Read value from spark-cloud
    * Initiate ON/OFF function
    */
    class SclCtrl extends AsyncTask<String, Void, String> {
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


                BufferedReader br1 = new BufferedReader(new InputStreamReader(con.getInputStream()));
                Data data = new Data();

                while ((line = br1.readLine()) != null) {
                    if (line.contains("event")) {
                        //do nothing since the event tag is of no interest
                        Log.d(TAG, "Loop: no needed content detected, try once again...");
                        Log.d(TAG, line + "");
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
                            data.setTemp(jObject.getString("Temperature"));
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
                        break;
                    }
                }

                final int gScl = data.getScl();                         // Needs to be converted to a string


                // To access the findViewById we need this to runOnUiThread
                runOnUiThread(new Runnable(){
                    public void run() {
                        final TextView updateScl   = (TextView) findViewById(R.id.textStoneCircle);


                        if (gScl >= 1) {
                            // Send LOW to set relay ON
                            Toast.makeText(MainActivity.this, "Switched ON", Toast.LENGTH_SHORT).show();
                            new PostClient().execute("LOW");
                            final TextView updateScl1   = (TextView) findViewById(R.id.textStoneCircle);
                            updateScl1.setText("ON");

                        }
                        if (gScl == 0) {
                            // Send HIGH to set relay LOW
                            Toast.makeText(MainActivity.this, "Switched OFF", Toast.LENGTH_SHORT).show();
                            new PostClient().execute("HIGH");
                            final TextView updateScl2   = (TextView) findViewById(R.id.textStoneCircle);
                            updateScl2.setText("OFF");


                        }
                    }

                });
                // Closing the stream
                Log.d(TAG, "*******************  Stream closed, exiting     *****************************");
                br1.close();
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
            return null; }



    }

    /*
    * Restful Function
    *
    * POST value to the function initialized by CrimsonCore
    */
    class PostClient extends AsyncTask<String, Void, String> {
        public String doInBackground(String... IO) {

            // Predefine variables
            String io = new String(IO[0]);
            URL url;

            try {
                // Fetch CoreID + AccessToken from SharedPreferences and construct url and param string
                Log.d(TAG, "********************************************************************");
                Log.d(TAG, "**********   Retrieve value from SharedPreferences   ***************");
                String coreid = SettingsConnector.readString(MainActivity.this, SettingsConnector.COREID, null);
                String accesstoken = SettingsConnector.readString(MainActivity.this, SettingsConnector.ACCESSTOKEN, null);
                String ctrlpin = SettingsConnector.readString(MainActivity.this, SettingsConnector.CTRLPIN, null);
                String ctrlvalue = SettingsConnector.readString(MainActivity.this, SettingsConnector.CTRLVALUE, null);
                Log.d(TAG, "CoreID:       " + coreid);
                Log.d(TAG, "AccessToken:  " + accesstoken);
                Log.d(TAG, "CtrlPin:      " + ctrlpin);
                Log.d(TAG, "CtrlValue:    " + ctrlvalue);
                Log.d(TAG, "*******************   Building URL     *****************************");
                url = new URL("https://api.spark.io/v1/devices/"+coreid+"/"+ctrlvalue+"/");
                Log.d(TAG, "URL:          " + url);
                Log.d(TAG, "*******************   Building Params  *****************************");
                String param = "access_token="+accesstoken+"&params="+ctrlpin+","+io;
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
                Log.d(TAG; "Con:       " + con);

                Log.d(TAG, "********************   Send params   ****************************");
                // Send specified parameters to the api (registered function must be set by CrimsonCore)
                PrintWriter out = new PrintWriter(con.getOutputStream());
                out.print(param);
                out.close();
                con.connect();

                Log.d(TAG, "*****************   Buffered Reader  ***************************");
                // Retrieve information if connection was successful or not
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
            // Set null and we´re good to go
            return null;
        }
    }

}