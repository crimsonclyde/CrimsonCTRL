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
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
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
import javax.net.ssl.HttpsURLConnection;


/**
 * A straightforward example of using AndroidPlot to plot some data.
 */
public class TempActivity extends Activity implements AdapterView.OnItemSelectedListener {

    public static final String TAG = TempActivity.class.getSimpleName();
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
    String tempTime    = "8";    // Default value
    // TODO: New Spinner for result amount
    String tempAmount  = "10";   // Default value
    String format      = "json"; // Default value


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // fun little snippet that prevents users from taking screenshots
        // on ICS+ devices :-)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_temp);

        /* Header font adjustments */
        TextView crimsonHead = (TextView) findViewById(R.id.crimsonHead);
        Typeface fontFace = Typeface.createFromAsset(getAssets(), "fonts/VeraMono.ttf");
        crimsonHead.setTypeface(fontFace);
        crimsonHead.setGravity(Gravity.CENTER);

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
            Toast.makeText(TempActivity.this, "TempService URL empty", Toast.LENGTH_LONG).show();
            Intent initSettings = new Intent(TempActivity.this, SettingsActivity.class);
            startActivity(initSettings);

        } else {
            // Run the pull request
            //Toast.makeText(TempActivity.this, "TempService URL OK", Toast.LENGTH_LONG).show();
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

        Log.d(TAG, "TempTime:       " + tempTime);

        new WebServiceClient().execute(tempserviceurl);



    }

    public void clearPlot() {
        // Clear up the Graph
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

        // Handle presses on the action bar items
        switch (item.getItemId()) {


            // Cam Action
            case R.id.action_main:
                //Toast.makeText(this, "Starting - Camera Activity", Toast.LENGTH_SHORT).show();

                // Main Activity
                Intent intentMain = new Intent(TempActivity.this, MainActivity.class);
                startActivity(intentMain);
                break;

            // Camera Activity
            case R.id.action_cam:
                //Toast.makeText(this, "Starting - Temperature Graph Activity", Toast.LENGTH_SHORT).show();

                // Change Activity
                Intent intentCam = new Intent(TempActivity.this, MjpegActivity.class);
                startActivity(intentCam);
                break;

            // Settings action
            case R.id.action_settings:
                //Toast.makeText(this, "Starting - Settings Activity", Toast.LENGTH_SHORT).show();

                // Change Activity
                Intent intentSettings = new Intent(TempActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                break;

            // Info Screen
            case R.id.action_info:
                // Just for debugging
                //Toast.makeText(this, "Starting - Info Activity", Toast.LENGTH_LONG).show();

                // Change Activity
                Intent intentInfo = new Intent(TempActivity.this, InfoActivity.class);
                startActivity(intentInfo);
                break;

            default:
                break;
        }
        return true;


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

            try {
                Log.d(TAG, "**********************************************************************");
                Log.d(TAG, "******************    HttpsUrlConnection    **************************");
                // Building the URL
                URL url = new URL(urls[0]);
                String s_time   = URLEncoder.encode(tempTime, "UTF-8");
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
                Log.d(TAG, "jObject:         " + jObject );
                Log.d(TAG, "jObject Length:  " + jObject.length());



                if ( jObject != null ) {
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

                            String id   = object.getString("id");
                            Log.d(TAG, "id:                " + id);

                            String year = object.getString("year");
                            Log.d(TAG, "year:              " + year);

                            String month = object.getString("month");
                            Log.d(TAG, "month:             " + month);

                            String day   = object.getString("day");
                            Log.d(TAG, "day:               " + day);

                            String time  = object.getString("time");
                            Log.d(TAG, "time:              " + time);

                            String temp = object.getString("temp");
                            Log.d(TAG, "temp:              " + temp);

                            float floatTemp  = Float.parseFloat(temp);
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


            return finalTemp; }



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

                    series1Numbers = finalTemp;
                    //series1Numbers = new Number[]{6, 5, 4, 3, 2, 1};


                    // Turn the above arrays into XYSeries':
                    series1 = new SimpleXYSeries(Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");

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

                    plot.redraw();


                }
            });

        }
    }
}
