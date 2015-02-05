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


/* ****************************************************************************************** */

public class MainActivity extends Activity {
    // Initiate Variables and Defaults
    public static final String TAG = HttpsClient.class.getSimpleName();
    protected static final String PREF_NAME = "CTRL_SETTINGS";
    protected final static int mRefreshTimeout = 60;
    protected String mStoredTimestamp;
    protected String mCurrentTimestamp;
    protected ProgressBar mProgressBar;
    protected Button scButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign and declare the ProgressBar
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Initiate the ButtonListener
        scButtonListener();

        // Calling the construct
        theFramework();

    }


    /* ****************************************************************************************** */
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
            //Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
            theFramework();
            break;

        // Cam Action
        case R.id.action_cam:
            //Toast.makeText(this, "Starting - Camera Activity", Toast.LENGTH_SHORT).show();

            // Change Activity
            Intent intentCam = new Intent(MainActivity.this, MjpegActivity.class);
            startActivity(intentCam);
            break;

        // Settings action
        case R.id.action_settings:
            //Toast.makeText(this, "Starting - Settings Activity", Toast.LENGTH_SHORT).show();

            // Change Activity
            Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intentSettings);
            break;

        // Info Screen
        case R.id.action_info:
            // Just for debugging
            //Toast.makeText(this, "Starting - Info Activity", Toast.LENGTH_LONG).show();

            // Change Activity
            Intent intentInfo = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(intentInfo);
            break;

        default:
            break;
        }
        return true;


    }

    /* ******************************************************************************************
     *
     * theFramework
     *
     *  I am the Architect. I created CrimsonCTRL. I have been waiting for you. You have many
     *  questions and though the process has altered your consciousness, you remain irrevocably human.
     *  Ergo, some of this code you will understand, some of them you will not. Concurrently,
     *  while your first question may be the most pertinent, you may or may not realize,
     *  it is also the most irrelevant.
     *  This function is the sum of a remainder of an unbalanced equation inherent to the programming
     *  of CrimsonCTRL.
     *
     *  1. Check network coverage
     *  2. Compare current/stored timestamp
     *  3. Do refresh or don´t
     */

    public void theFramework() {
        // Netowork availability check
        if (isNetworkAvailable()) {

            Log.d(TAG, "**********************************************************************");
            Log.d(TAG, "*****        _____                                 _            ******");
            Log.d(TAG, "*****       |   __|___ ___ _____ ___ _ _ _ ___ ___| |_          ******");
            Log.d(TAG, "*****       |   __|  _| .'|     | -_| | | | . |  _| '_|         ******");
            Log.d(TAG, "*****       |__|  |_| |__,|_|_|_|___|_____|___|_| |_,_|         ******");
            Log.d(TAG, "**********************************************************************");
            Log.d(TAG, "*********************    TimeStamp Check    **************************");

            // Set current timestamp (-60 seconds)
            mCurrentTimestamp =  String.valueOf((System.currentTimeMillis() / 1000) - mRefreshTimeout);
            final int cTime = Integer.parseInt(mCurrentTimestamp);

            // Get stored timestamp
            mStoredTimestamp   = SettingsConnector.readString(this, SettingsConnector.TIMESTAMP, null);
            String coreid      = SettingsConnector.readString(this, SettingsConnector.COREID, null);
            String accesstoken = SettingsConnector.readString(this, SettingsConnector.ACCESSTOKEN, null);
            String scl         = SettingsConnector.readString(this, SettingsConnector.SCL, null);

             // Initial bugfix for the first run
            if ( mStoredTimestamp == null) { mStoredTimestamp = "468545003"; }

            Log.d(TAG, "mStoredTimestamp:  " + mStoredTimestamp);
            Log.d(TAG, "coreid:            " + coreid);
            Log.d(TAG, "accesstoken:       " + accesstoken);
            Log.d(TAG, "scl:               " + scl);


            if (accesstoken == null || coreid == null) {
                Log.d(TAG, "CoreID/AccessToken is empty - starting SettingsActivity: " + coreid + accesstoken );
                Intent initSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(initSettings);
            } else if (scl == null) {
                // When their is no spoon, we set a spoon
                Log.d(TAG, "SCL empty");
                // Set initial default value (1=off)
                SettingsConnector.writeString(this, SettingsConnector.SCL, "1");
                Log.d(TAG, "SCL applied, restart activity");
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } else {
                final int sTime = Integer.parseInt(mStoredTimestamp);

                // Debug
                Log.d(TAG, "StoredTimestamp:  " + sTime);
                Log.d(TAG, "CurrentTimestamp: " + cTime);

                // Decide if update is necessary
                if (cTime > sTime) {
                    // Values stored are too old, do the refresh boogie
                    Toast.makeText(this, "RELOAD DATA", Toast.LENGTH_LONG).show();
                    mProgressBar.setVisibility(View.VISIBLE);
                    loadCrimsonCoreData();

                } else {
                    // Values stored are not older than 60 secs, do nothing
                    mProgressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "No update needed.", Toast.LENGTH_LONG).show();
                    int pastTime = sTime - cTime;
                    Log.d(TAG, "Data age in SharedPreferences:  " + pastTime);
                    updateViews();
                }
            }

            } else {
                Toast.makeText(this, "Network is unavailable!", Toast.LENGTH_LONG).show();
        }
    }




    /* ******************************************************************************************
     *
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


    /* ******************************************************************************************
     *
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


    /* ******************************************************************************************
     *
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



    /* ******************************************************************************************
     *
     * Restful Events from Spark-Cloud
     *
     * GET values stored in the cloud via httpsurlconnection
     * and save 'em in Androids SharedPreferences
     */
    class HttpsClient extends AsyncTask<String, Void, String> {
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
                Log.d(TAG, "***********    Store Data in Shared Preferences     ******************");
                Log.d(TAG, "gDate:   " + data.getDate());
                Log.d(TAG, "gTime:   " + data.getTime());
                Log.d(TAG, "gWifi:   " + data.getWifi().toString());
                Log.d(TAG, "gTemp:   " + data.getTemp());
                Log.d(TAG, "gPhoto:  " + data.getPhoto().toString());
                Log.d(TAG, "gScl:    " + data.getScl().toString());


                SettingsConnector.writeString(MainActivity.this, SettingsConnector.PHOTO, data.getPhoto().toString());
                SettingsConnector.writeString(MainActivity.this, SettingsConnector.TEMP, data.getTemp());
                SettingsConnector.writeString(MainActivity.this, SettingsConnector.SCL, data.getScl().toString());

                // Save current timestamp
                Long timestamp = System.currentTimeMillis()/1000;
                SettingsConnector.writeString(MainActivity.this, SettingsConnector.TIMESTAMP, timestamp.toString());

                Log.d(TAG, "Data stored in SharedPreferences");

                Log.d(TAG, "****************  Closing stream, exiting     ************************");


                // Closing the stream
                br.close();
                updateViews();
                Log.d(TAG, "**********************************************************************");
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
            return null; }
    }

    /* ******************************************************************************************
     *
     * Update TextViews
     */

    public void updateViews() {
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
                String photo     = SettingsConnector.readString(MainActivity.this, SettingsConnector.PHOTO, null);
                String temp      = SettingsConnector.readString(MainActivity.this, SettingsConnector.TEMP, null);
                String scl       = SettingsConnector.readString(MainActivity.this, SettingsConnector.SCL, null);
                mStoredTimestamp = SettingsConnector.readString(MainActivity.this, SettingsConnector.TIMESTAMP, null);


                Log.d(TAG, "SharedPreferences - photo:      " + photo);
                Log.d(TAG, "SharedPreferences - temp:       " + temp);
                Log.d(TAG, "SharedPreferences - scl:        " + scl);
                Log.d(TAG, "SharedPreferences - timestamp:  " + mStoredTimestamp);


                // Update the TextViews
                Log.d(TAG, "*****************    Update TextView       ***************************");
                if      (temp != null)  { updateTemp.setText(temp); }
                else                    { updateTemp.setText("ERROR"); }

                if      (photo != null) { updatePhoto.setText(photo); }
                else                    { updatePhoto.setText("ERROR"); }

                switch (scl) {
                    case "1":
                        updateScl.setText("Off");
                        break;
                    case "0":
                        updateScl.setText("On");
                        break;
                    default:
                        updateScl.setText("ERROR");
                }

                mProgressBar.setVisibility(View.INVISIBLE);
            }

        });
    }

    /* ****************************************************************************************** */
    /*
    *  (1)PreCheck
    *  Read SCL value from the spark cloud to get current state
    *
    *  Maybe deprecated when time based storing is finally
    *  implemented
    */
    class SclCtrl extends AsyncTask<String, Void, String> {
        private Exception exception;
        public String doInBackground(String... urls) {

            try {
                Log.d(TAG, "********************************************************************");
                Log.d(TAG, "********    1st step: Check if StoneCirle is on/off    *************");
                URL url = new URL(urls[0]);
                Log.d(TAG, "Received URL:  " + url);

                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                Log.d(TAG, "Con Status: " + con);

                InputStream in = con.getInputStream();
                Log.d(TAG, "GetInputStream:  " + in);

                Log.d(TAG, "*******************    String Builder     *****************************");
                String line = null;


                BufferedReader br1 = new BufferedReader(new InputStreamReader(con.getInputStream()));
                final Data data = new Data();

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

                        //reading StoneCircleLight (SCL)
                        if (jObject.has("SCL")) {
                            data.setSCL(jObject.getInt("SCL"));
                        }

                    }
                    //check if we have all needed data
                    if (data.isReady()) {
                        break;
                    }
                }



                // To access the findViewById we need this to runOnUiThread
                runOnUiThread(new Runnable(){
                    public void run() {
                        final TextView updateScl   = (TextView) findViewById(R.id.textStoneCircle);


                        if (data.getScl() == 1) {
                            // Send LOW to set relay ON
                            Toast.makeText(MainActivity.this, "Switched ON", Toast.LENGTH_SHORT).show();
                            new PostClient().execute("LOW");
                            final TextView updateScl1   = (TextView) findViewById(R.id.textStoneCircle);
                            updateScl1.setText("ON");

                        }
                        if (data.getScl() == 0) {
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


    /* ****************************************************************************************** */
    /*
    * (2) Restful Function
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
                Log.d(TAG, "***********    2st step: Send params to the Core    ****************");
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
                Log.d(TAG, "Con:       " + con);

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
                }


            } catch (Exception e) {
                Log.d(TAG, "Exception");
                e.printStackTrace();
                return null;
            }
            Log.d(TAG, "********************************************************************");
            // Set null and we´re good to go
            return null;
        }
    }

}