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
 * Author : CrimsonClyde
 * E-Mail : clyde at darkpack.net
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;


/* ****************************************************************************************** */

public class MainActivity extends AppCompatActivity {
    // Initiate Variables and Defaults
    public static final String TAG = HttpsClient.class.getSimpleName();
    protected final static int mRefreshTimeout = 60;
    protected String mStoredTimestamp;
    protected String mCurrentTimestamp;
    protected ProgressBar mProgressBar;
    protected Button scButton;
    public String socketOutput = "s0";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Toolbar instead of ActioneBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);
        //toolbar.setNavigationIcon(R.drawable.chevron_left);
        getSupportActionBar().setTitle("Control");


        /* Header font adjustments */
        TextView crimsonHead = (TextView) findViewById(R.id.crimsonHead);
        Typeface fontFace= Typeface.createFromAsset(getAssets(),"fonts/VeraMono.ttf");

        crimsonHead.setTypeface(fontFace);
        crimsonHead.setGravity(Gravity.CENTER);
        crimsonHead.setTypeface(crimsonHead.getTypeface(), Typeface.BOLD);
        crimsonHead.setTextColor(Color.parseColor("#FFCCCCCC"));
        crimsonHead.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10.f);

        // Assign and declare the ProgressBar
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Initiate the ButtonListener (LongClick)
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

            // Temperature Plot
            case R.id.action_temp:
                //Toast.makeText(this, "Starting - Temperature Plot Activity", Toast.LENGTH_SHORT).show();

                // Change Activity
                Intent intentTempPlot = new Intent(MainActivity.this, TempActivity.class);
                startActivity(intentTempPlot);
                break;

            // Settings action
            case R.id.action_settings:
                //Toast.makeText(this, "Starting - Settings Activity", Toast.LENGTH_SHORT).show();

                // Change Activity
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                break;

            // Test Screen:
            //case R.id.action_test:
            //    Intent intentTest = new Intent(MainActivity.this, TestActivity.class);
            //    startActivity(intentTest);
            //    break;


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
            Log.d(TAG, "*****************    Summoning the Construct    **********************");

            // Set current timestamp (-60 seconds)
            mCurrentTimestamp =  String.valueOf((System.currentTimeMillis() / 1000) - mRefreshTimeout);
            final int cTime = Integer.parseInt(mCurrentTimestamp);

            // Get stored timestamp
            mStoredTimestamp   = SettingsConnector.readString(this, SettingsConnector.TIMESTAMP, null);
            String coreid      = SettingsConnector.readString(this, SettingsConnector.COREID, null);
            String accesstoken = SettingsConnector.readString(this, SettingsConnector.ACCESSTOKEN, null);
            String scl         = SettingsConnector.readString(this, SettingsConnector.SCL, null);
            String socketurl   = SettingsConnector.readString(this, SettingsConnector.WEBSOCKETURL, null);
            String socketport  = SettingsConnector.readString(this, SettingsConnector.WEBSOCKETPORT, null);

            // First run fix: If mStoredTime is empty set it to 05.11.1984 23:23:23
            if ( mStoredTimestamp == null) { mStoredTimestamp = "468545003"; }

            Log.d(TAG, "mStoredTimestamp:  " + mStoredTimestamp);
            Log.d(TAG, "coreid:            " + coreid);
            Log.d(TAG, "accesstoken:       " + accesstoken);
            Log.d(TAG, "scl:               " + scl);

            if (accesstoken == null || coreid == null || socketurl == null || socketport == null) {
                Log.d(TAG, "CoreID, AccessToken, SocketURL or SocketPort not set, starting SettingsActivity");
                Log.d(TAG, "CoreID:       " + coreid );
                Log.d(TAG, "AccessToken:  " + accesstoken);
                Log.d(TAG, "SocketURL:    " + socketurl);
                Log.d(TAG, "SocketPort:   " + socketport);
                Toast.makeText(MainActivity.this, "Core+Socket must be set ", Toast.LENGTH_LONG).show();
                Intent initSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(initSettings);
            } else if (scl == null) {
                // SCL Value is empty, write /default 1 (off)
                Log.d(TAG, "SCL value not set!");
                // Set initial default value (1=off)
                SettingsConnector.writeString(this, SettingsConnector.SCL, "1");
                // Restart MainActivity
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
                String coreid      = SettingsConnector.readString(MainActivity.this, SettingsConnector.COREID, null);
                String accesstoken = SettingsConnector.readString(MainActivity.this, SettingsConnector.ACCESSTOKEN, null);

                // Execute TODO: Remove hardcoded function name
                new SclCtrl().execute("https://api.spark.io/v1/devices/"+coreid+"/events/pMisc/?access_token="+accesstoken);

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
        new HttpsClient().execute("https://api.spark.io/v1/devices/" + coreid + "/events/?access_token=" + accesstoken);
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

        //public String ipAddress;
        //public int socketPort = 5000;
        public InetAddress getIpFromHostname;


        public String doInBackground(String... urls) {

            try {
                Log.d(TAG, "**********************************************************************");
                Log.d(TAG, "******************    HttpsUrlConnection    **************************");
                URL url = new URL(urls[0]);
                Log.d(TAG, "Received URL:    " + url);
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
                Log.d(TAG, "Con Status:      " + con);
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

                /*
                 * Global CrimsonHome Server Information System
                 *
                 * TODO: Parse output store in shared prefs and update views
                 */
                Log.d(TAG, "**********************************************************************");
                Log.d(TAG, "****************  CrimsonHome Server Info     ************************");

                // Retrieve URL + Port from SharedPreferences
                String websocketurl = SettingsConnector.readString(MainActivity.this, SettingsConnector.WEBSOCKETURL, null);
                String websocketport = SettingsConnector.readString(MainActivity.this, SettingsConnector.WEBSOCKETPORT, null);

                if (websocketurl != null && websocketport != null) {
                    // Convert websocketport to an integer
                    int websocketportInt = Integer.parseInt(websocketport);

                    // 1. Resolve IP from URL
                    try {
                        getIpFromHostname = InetAddress.getByName(websocketurl);
                        Log.d(TAG, "WebSocket - GetIpFromHost: " + getIpFromHostname.getHostAddress());
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    String ipAddress = getIpFromHostname.getHostAddress();


                    // 2. Prepare the socket connection
                    try {
                        // Create Socket instance
                        Socket socket = new Socket(ipAddress, websocketportInt);
                        // Get input buffer
                        BufferedReader websocketReader = new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                        socketOutput = websocketReader.readLine();
                        br.close();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    // 3. Debug info
                    if (socketOutput.contains("s1")) {
                        Log.d(TAG, "WebSocket - Online " + socketOutput);
                    } else if (socketOutput.contains("s0")) {
                        Log.d(TAG, "WebSocket - Offline " + socketOutput);
                    } else {
                        Log.d(TAG, "WebSocket: Error! " + socketOutput);
                    }

                    // 4. Save result into shared preferences
                    SettingsConnector.writeString(MainActivity.this, SettingsConnector.WEBSOCKETOUTPUT, socketOutput);
                }


                // Read - now update the UI
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
                final TextView updateServer = (TextView)  findViewById(R.id.textCrimsonHomeState);
                final TextView serviceApache = (TextView) findViewById(R.id.textCrimsonHomeApache);
                final TextView uptimeServer = (TextView) findViewById(R.id.textCrimsonHomeUptime);
                final TextView uptimeDetails = (TextView) findViewById(R.id.crimsonHomeUptimeTextView);


                // Retrieve data from shared preferences
                Log.d(TAG, "************    Retrieve data from Shared Preferences     ************");
                String photo     = SettingsConnector.readString(MainActivity.this, SettingsConnector.PHOTO, null);
                String temp      = SettingsConnector.readString(MainActivity.this, SettingsConnector.TEMP, null);
                String scl       = SettingsConnector.readString(MainActivity.this, SettingsConnector.SCL, null);
                String websocket = SettingsConnector.readString(MainActivity.this, SettingsConnector.WEBSOCKETOUTPUT, null);
                mStoredTimestamp = SettingsConnector.readString(MainActivity.this, SettingsConnector.TIMESTAMP, null);



                Log.d(TAG, "SharedPreferences - photo:      " + photo);
                Log.d(TAG, "SharedPreferences - temp:       " + temp);
                Log.d(TAG, "SharedPreferences - scl:        " + scl);
                Log.d(TAG, "SharedPreferences - websocket:  " + websocket);
                Log.d(TAG, "SharedPreferences - timestamp:  " + mStoredTimestamp);


                // Update the TextViews
                Log.d(TAG, "*****************    Update TextView       ***************************");
                // Temperature State
                if      (temp != null)  { updateTemp.setText(temp); }
                else                    { updateTemp.setText("ERROR"); }

                // Photoresistor State
                if      (photo != null) { updatePhoto.setText(photo); }
                else                    { updatePhoto.setText("ERROR"); }

                // StoneCircleLight
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

                if (websocket.contains("s1") ) {
                    // CrimsonHome Server Information Split
                    String delim1 = "[;]";
                    String[] tokens = websocket.split(delim1);
                    // Debug
                    Log.d(TAG, "FirstSplit: " + tokens[1]);
                    Log.d(TAG, "FirstSplit: " + tokens[2]);
                    Log.d(TAG, "FirstSplit: " + tokens[3]);

                    // CrimsonHome Server
                    if (websocket.contains("ServerUp,s1;")) {
                        updateServer.setText("Up");
                    } else {
                        updateServer.setText("Down");
                    }

                    // CrimsonHome Uptime
                    // We need to split the uptime multiple time
                    // First split the parts by ,
                    String delimUptime = "[,]";
                    String[] uptimeSplit = tokens[2].split(delimUptime);
                    Log.d(TAG, "Uptime Split: " + uptimeSplit[0]);
                    Log.d(TAG, "Uptime Split: " + uptimeSplit[1]);
                    Log.d(TAG, "Uptime Split: " + uptimeSplit[2]);
                    Log.d(TAG, "Uptime Split: " + uptimeSplit[3]);

                    // Second split the uptime by " "
                    String delimUptime2 = "[ ]";
                    String[] uptimeSplit2 = uptimeSplit[1].split(delimUptime2);

                    // Debug
                    Log.d(TAG, "Uptime Split2-0     :" + uptimeSplit2[0]);
                    Log.d(TAG, "Uptime Split2-1     :" + uptimeSplit2[1]);
                    Log.d(TAG, "Uptime Split2-2     :" + uptimeSplit2[2]);
                    Log.d(TAG, "Uptime Split2-3     :" + uptimeSplit2[3]);
                    Log.d(TAG, "Uptime Split2-4     :" + uptimeSplit2[4]);
                    Log.d(TAG, "Uptime Split-Length :" + uptimeSplit2.length);


                    // Splitting the uptime
                    int uptimeSplitLength = uptimeSplit2.length;
                    String uptimeSplit3[] = null;
                    String delimUptime3 = "[:]";

                    // In case server is up only minutes
                    if (uptimeSplit2[4].contains("min")) {
                        Log.d(TAG, "Uptime Split2-3 Hours:   " + uptimeSplit2[3]);

                        // Update the textviews
                        if (uptimeSplit2[2].contains("up")) {
                            uptimeServer.setText("Up");
                            uptimeDetails.setTextSize(16.0f);
                            uptimeDetails.setText("CrimsonHome" + "\n" + "Uptime: " +
                                    uptimeSplit2[3].replaceAll("\\s", "") + "minutes" +
                                    "\n" +
                                    "Current time: " + uptimeSplit2[1]);
                        } else {
                            uptimeServer.setText("Down");
                        }
                    }

                    // Days + Hours + Minutes
                    else if (uptimeSplit2[4].contains("day") || uptimeSplit2[4].contains("days")) {
                        uptimeSplit3 = uptimeSplit[2].split(delimUptime3);
                        Log.d(TAG, "UptimeSplit3-0 Hours:    " + uptimeSplit3[0]);
                        Log.d(TAG, "UptimeSplit3-1 Minutes:  " + uptimeSplit3[1]);

                        // Update the textviews
                        if (uptimeSplit2[2].contains("up")) {
                            uptimeServer.setText("Up");
                            uptimeDetails.setTextSize(16.0f);

                            uptimeDetails.setText("CrimsonHome" + "\n" + "Uptime: " +
                                    uptimeSplit2[3].replaceAll("\\s", "") + uptimeSplit2[4].replaceAll("\\s", "") + " " +
                                    uptimeSplit3[0].replaceAll("\\s", "") + "hours " +
                                    uptimeSplit3[1].replaceAll("\\s", "") + "minutes" +
                                    "\n" +
                                    "Current time: " + uptimeSplit2[1]);
                        } else {
                            uptimeServer.setText("Down");

                        }

                    }

                    // Hours + Minutes
                    else if (uptimeSplit2[4].contains(":")) {
                        uptimeSplit3 = uptimeSplit2[4].split(delimUptime3);
                        Log.d(TAG, "Uptime Split4-0:   " + uptimeSplit3[0]);
                        Log.d(TAG, "Uptime Split4-1:   " + uptimeSplit3[1]);

                        // Update the textviews
                        if (uptimeSplit2[2].contains("up")) {
                            uptimeServer.setText("Up");
                            uptimeDetails.setTextSize(16.0f);
                            uptimeDetails.setText("CrimsonHome" + "\n" + "Uptime: " +
                                    uptimeSplit3[0].replaceAll("\\s", "") + "hours " +
                                    uptimeSplit3[1].replaceAll("\\s", "") + "minutes" +
                                    "\n" +
                                    "Current time: " + uptimeSplit2[1]);
                        } else {
                            uptimeServer.setText("Down");
                        }

                    }


                    // CrimsonHome Service Apache
                    if (websocket.contains("Apache,Apache2 is running")) {
                        serviceApache.setText("Up");
                    } else {
                        serviceApache.setText("Down");
                    }

                    // Set the variable back off
                    //websocket = "s0";
                }

                // Hide prorgressbar
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

            Log.d(TAG, "********************************************************************");
            Log.d(TAG, "***************    1st step: Reload data        ********************");

            // To access the findViewById we need this to runOnUiThread
            runOnUiThread(new Runnable() {
                public void run() {
                    mProgressBar.setVisibility(View.VISIBLE);

                    // reload
                    loadCrimsonCoreData();

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    // Fetch value from SharedPreferences
                    String scl       = SettingsConnector.readString(MainActivity.this, SettingsConnector.SCL, null);
                    Log.d(TAG, "Current SCL value: " + scl);


                    if (scl.equals("1")) {
                        // Send LOW to set relay ON
                        Toast.makeText(MainActivity.this, "Switched ON", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Set pin to low");
                        new PostClient().execute("LOW");
                    } else if (scl.equals("0")) {
                        // Send HIGH to set relay LOW
                        Toast.makeText(MainActivity.this, "Switched OFF", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Set pin to high");
                        new PostClient().execute("HIGH");
                    }

                }
            });



            return null;
        }
    }




    /* ****************************************************************************************** */
    /*
    * (2) Restful Function
    *
    * POST value to the function initialized by CrimsonCore
    */
    class PostClient extends AsyncTask<String, Void, String> {
        public String doInBackground(String... IO) {
            mProgressBar.setVisibility(View.VISIBLE);

            // Predefine variables
            final String io = new String(IO[0]);
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
                    Log.d(TAG, "Post request failed (Statuscode != 200): " + in);
                } else {
                    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    Log.d(TAG, "POST request send successful:            " + in);

                    // Update TextViews
                    runOnUiThread(new Runnable() {
                        public void run () {

                            // Declare & Assign the TextView
                            final TextView updateScl = (TextView) findViewById(R.id.textStoneCircle);

                            if (io.equals("HIGH")) {
                                // High = OFF
                                Log.d(TAG, "Update TextView to OFF.");
                                updateScl.setText("OFF");
                            } else if (io.equals("LOW")) {
                                // LOW = ON
                                updateScl.setText("ON");
                                Log.d(TAG, "Update TextView to ON.");
                            }

                        }
                    });
                }
                mProgressBar.setVisibility(View.INVISIBLE);

            } catch (Exception e) {
                Log.d(TAG, "Exception");
                e.printStackTrace();
                return null;
            }
            Log.d(TAG, "*****************************EOF************************************");
            // Set null and we´re good to go
            return null;
        }
    }

}