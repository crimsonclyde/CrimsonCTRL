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
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


/**
 * A straightforward example of using AndroidPlot to plot some data.
 */
public class TempActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = InfoActivity.class.getSimpleName();
    protected String mActivityTitle = "Temperature";
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    String[] mDrawerListItems;
    private XYPlot plot;
    String result = "";
    JSONObject jObject = null;
    JSONArray jArray = null;
    ArrayList tempArrayList = new ArrayList();
    Number[] series1Numbers;
    XYSeries series1;
    LineAndPointFormatter series1Format;
    String tempserviceurl;
    Spinner tempSpin;
    String tempTime;
    // TODO: New Spinner for result amount
    String tempAmount  = "10";   // Default value
    String format      = "json"; // Default value


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // fun little snippet that prevents users from taking screenshots
        // on ICS+ devices :-)
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
        //        WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_temp);

        // Assign Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Toolbar Navigation Drawer
        mDrawerLayout       = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerList         = (ListView) findViewById(R.id.drawer_list);
        mDrawerListItems    = getResources().getStringArray(R.array.drawer_items);

        // Remove the current Activity from ArrayList
        Log.d(TAG, "List of all activities    :" + Arrays.toString(mDrawerListItems));
        List<String> list = new ArrayList<String>(Arrays.asList(mDrawerListItems));
        list.removeAll(Arrays.asList(mActivityTitle));
        mDrawerListItems = list.toArray(new String[0]);
        Log.d(TAG, "Current activity removed  :" + Arrays.toString(mDrawerListItems));

        // Create the Adapter
        mDrawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mDrawerListItems));

        // Summoning onClickListener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int editPosition = position + 1;
                //Toast.makeText(MainActivity.this, "You selected item " + editPosition, Toast.LENGTH_SHORT).show();
                switch (editPosition) {
                    case 1:
                        // Change Activity
                        Intent intentControl = new Intent(TempActivity.this, MainActivity.class);
                        startActivity(intentControl);
                        break;

                    case 2:
                        // Change Activity
                        Intent intentCam = new Intent(TempActivity.this, MjpegActivity.class);
                        startActivity(intentCam);
                        break;

                    case 3:
                        // Change Activity
                        Intent intentTemperature = new Intent(TempActivity.this, SettingsActivity.class);
                        startActivity(intentTemperature);
                        break;

                    case 4:
                        // Change Activity
                        Intent intentInfo = new Intent(TempActivity.this, InfoActivity.class);
                        startActivity(intentInfo);
                        break;

                    default:
                        break;
                }
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
                invalidateOptionsMenu();
                syncState();
            }
            public void onDrawerOpened(View v){
                super.onDrawerOpened(v);
                invalidateOptionsMenu();
                syncState();

            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Initiate Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Temperature Trend");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        /* Header font adjustments */
        TextView crimsonHead = (TextView) findViewById(R.id.crimsonHead);
        Typeface fontFace= Typeface.createFromAsset(getAssets(),"fonts/VeraMono.ttf");

        crimsonHead.setTypeface(fontFace);
        crimsonHead.setGravity(Gravity.CENTER);
        crimsonHead.setTypeface(crimsonHead.getTypeface(), Typeface.BOLD);
        crimsonHead.setTextColor(Color.parseColor("#DBDBDB"));
        crimsonHead.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10.f);

        /* Temperature Time Spinner */
        tempSpin = (Spinner) findViewById(R.id.temp_time_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.temptime_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        tempSpin.setAdapter(adapter);
        // OnSelect Listener
        tempSpin.setOnItemSelectedListener(this);



        /* Get values from SharedPreferences */
        tempserviceurl = SettingsConnector.readString(TempActivity.this, SettingsConnector.TEMPSERVICEURL, null);
        Log.d(TAG, "tempserviceurl:     " + tempserviceurl);

        // Check if WebSerice URL is set
        if ( tempserviceurl == null ) {
            Log.d(TAG, "Temperature Service URL NOT set: Starting Settings Activity");
            Intent initSettings = new Intent(TempActivity.this, SettingsActivity.class);
            startActivity(initSettings);

        } else {
            //Run the pull request
            //Log.d(TAG, "Temperature Service URL set: Initiate WebServiceClient");
            //new WebServiceClient().execute(tempserviceurl);
        }


    }

    /* Spinner Temperature Action */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        tempSpin.getItemAtPosition(pos);
        Log.d(TAG, "Spinner selected time:    " + pos);

        switch (pos) {
            case 0:
                tempTime = "8";
                break;
            case 1:
                tempTime = "10";
                clearPlot();
                break;
            case 2:
                tempTime = "12";
                clearPlot();
                break;
            case 3:
                tempTime = "14";
                clearPlot();
                break;
            case 4:
                tempTime = "16";
                clearPlot();
                break;
            case 5:
                tempTime = "18";
                clearPlot();
                break;
            case 6:
                tempTime = "20";
                clearPlot();
                break;
            case 7:
                tempTime = "22";
                clearPlot();
                break;
            default:
                tempTime = "8";
                break;
        }

        new WebServiceClient().execute(tempserviceurl);
        Log.d(TAG, "TempTime:                    " + tempTime);
        Log.d(TAG, "Temperature Service URL set: Initiate WebServiceClient");





    }

    public void clearPlot() {
        // Clear the graph
        plot.clear();
        plot.redraw();
        // Clear the ArrayList (failing results in adding new values to the array)
        tempArrayList.clear();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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

        // Toolbar Navigation Drawer
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    mDrawerLayout.openDrawer(mDrawerList);
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }


    }



    /* ******************************************************************************************
     *
     * Encapsulate the Framework from UI - Thread
     *
     * Connect to the CrimsonHome WebService and pull the temperature data
     * Converting data to an array
     */
    class WebServiceClient extends AsyncTask<String, Void, Number[]> {
        private Exception exception;


        public Number[] doInBackground(String... urls) {
            Log.d(TAG, "**********************************************************************");
            Log.d(TAG, "*****        _____                                 _            ******");
            Log.d(TAG, "*****       |   __|___ ___ _____ ___ _ _ _ ___ ___| |_          ******");
            Log.d(TAG, "*****       |   __|  _| .'|     | -_| | | | . |  _| '_|         ******");
            Log.d(TAG, "*****       |__|  |_| |__,|_|_|_|___|_____|___|_| |_,_|         ******");
            Log.d(TAG, "**********************************************************************");
            Log.d(TAG, "*****************    Summoning the Construct    **********************");
            if (isNetworkAvailable()) {
                try {
                    Log.d(TAG, "**********************************************************************");
                    Log.d(TAG, "******************    HttpsUrlConnection    **************************");
                    // Building the URL
                    URL url = new URL(urls[0]);
                    String s_time = URLEncoder.encode(tempTime, "UTF-8");
                    String s_amount = URLEncoder.encode(tempAmount, "UTF-8");
                    String s_url = url + "&num=" + s_amount + "&time=" + s_time + "&format=" + format;
                    URL finalUrl = new URL(s_url);


                    Log.d(TAG, "Received URL:    " + finalUrl);
                    HttpsURLConnection con = (HttpsURLConnection) finalUrl.openConnection();
                    Log.d(TAG, "Con Status:      " + con);
                    InputStream in = con.getInputStream();
                    Log.d(TAG, "GetInputStream:  " + in);

                    Log.d(TAG, "*******************    Buffered Reader    *****************************");
                    String line = null;

                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                in, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        in.close();
                        result = sb.toString();
                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }


                    Log.d(TAG, "*******************    Parse JSON         *****************************");

                    // Selecting the Array Node
                    try {
                        jObject = new JSONObject(result);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing data " + e.toString());
                    }
                    Log.d(TAG, "jObject:         " + jObject);
                    Log.d(TAG, "jObject Length:  " + jObject.length());


                    if (jObject != null) {
                        try {
                            // Select the Object Node
                            jArray = jObject.getJSONArray("jArrayNode");
                            Log.d(TAG, "jArray:         " + jArray);
                            Log.d(TAG, "jArray Length:  " + jArray.length());


                            // Now we are able to loop through the array
                            Log.d(TAG, "*** Entering Loop ***");
                            for (int i = 0; i < jArray.length(); i++) {
                                Log.d(TAG, i + "");

                                // Dive into JSON Object "jObject"
                                JSONObject jObjectNode = jArray.getJSONObject(i);
                                Log.d(TAG, "JObjectNode:    " + jObjectNode);

                                // Deep-dive into single JSON Object
                                JSONObject object = jObjectNode.getJSONObject("jObject");
                                Log.d(TAG, "Object:         " + object);


                                // Extract the data
                                Log.d(TAG, "************************    Values    ******************************** ");
                                int json_id = i;
                                Log.d(TAG, json_id + "");

                                String id = object.getString("id");
                                Log.d(TAG, "id:                " + id);

                                String year = object.getString("year");
                                Log.d(TAG, "year:              " + year);

                                String month = object.getString("month");
                                Log.d(TAG, "month:             " + month);

                                String day = object.getString("day");
                                Log.d(TAG, "day:               " + day);

                                String time = object.getString("time");
                                Log.d(TAG, "time:              " + time);

                                String temp = object.getString("temp");
                                Log.d(TAG, "temp:              " + temp);

                                float floatTemp = Float.parseFloat(temp);
                                Log.d(TAG, "floatTemp          " + floatTemp);

                                Log.d(TAG, "***********************************************************************");


                                // Add items to ArrayList
                                tempArrayList.add(floatTemp);

                            }

                        } catch (Exception e) {
                            this.exception = e;
                        }
                    }
                } catch (Exception e) {
                    this.exception = e;
                }
                Log.d(TAG, "*******************    Building Array     *****************************");
                Log.d(TAG, "***********************************************************************");
                Log.d(TAG, "tempArrayList:     " + tempArrayList.toString());
                Log.d(TAG, "tempArraySize:     " + tempArrayList.size());


                Number[] finalTemp = new Number[tempArrayList.size()];
                tempArrayList.toArray(finalTemp);
                Log.d(TAG, "finalTemp Array;   " + finalTemp.toString());


                return finalTemp;
            } else {
            Toast.makeText(TempActivity.this, "Network not available", Toast.LENGTH_LONG).show();

            }
        return null;
        }





        /* ******************************************************************************************
         *
         * Running if we have the data
         *
         * Waiting for the background job to finish
         * Creating the AndroidPlot and inserting the results
         */
        @Override
        protected void onPostExecute (final Number[] finalTemp) {
            Toast.makeText(TempActivity.this, "Background Job finished", Toast.LENGTH_SHORT).show();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // initialize our XYPlot reference:
                    plot = (XYPlot) findViewById(R.id.weatherPlot);

                    // TODO: build dynamic based array to protect different array sizes
                    // while (int i, i < finalTemp.length )  { array ++ }
                     Number[] series2Days = {1,2,3,4,5,6,7,8,9,10};

                    //Number [] series2Days = new int[finalTemp.length];


                    series1Numbers = finalTemp;



                    // Turn the above arrays into XYSeries':
                    series1 = new SimpleXYSeries(
                            Arrays.asList(series2Days),             // X-Axis
                            Arrays.asList(series1Numbers),          // Y-Axis
                            "Temperature");                         // Title


                    // Create a formatter to use for drawing a series using LineAndPointRenderer
                    // and configure it from xml:
                    series1Format = new LineAndPointFormatter();
                    series1Format.setPointLabelFormatter(new PointLabelFormatter());
                    series1Format.configure(getApplicationContext(),
                            R.xml.line_point_formatter_with_plf1);

                    // add a new series' to the xyplot:
                    plot.addSeries(series1, series1Format);


                    // reduce the number of range labels
                    plot.setTicksPerRangeLabel(3);
                    plot.getGraphWidget().setDomainLabelOrientation(-45);

                    // Design


                    plot.setBackgroundColor(Color.WHITE);
                    plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, series2Days.length);
                    plot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
                    plot.setPlotMargins(0, 0, 0, 0);
                    plot.setPlotPadding(0, 0, 0, 0);
                    plot.setGridPadding(0, 0, 0, 0);
                    plot.setBackgroundColor(Color.argb(00, 00, 00, 00));


                    plot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
                    plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);

                    plot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
                    plot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

                    plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
                    plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
                    plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);



                    //Remove legend
                    plot.getLayoutManager().remove(plot.getLegendWidget());
                    plot.getLayoutManager().remove(plot.getDomainLabelWidget());
                    plot.getLayoutManager().remove(plot.getRangeLabelWidget());
                    plot.getLayoutManager().remove(plot.getTitleWidget());



                    plot.setMarkupEnabled(false);
                    plot.redraw();


                }
            });

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


    }
}
