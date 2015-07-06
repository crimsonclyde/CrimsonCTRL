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
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // Toolbar instead of ActioneBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);
        toolbar.setNavigationIcon(R.drawable.chevron_left);
        getSupportActionBar().setTitle("About");

        /* Header font adjustments */
        TextView crimsonHead = (TextView) findViewById(R.id.crimsonHead);
        Typeface fontFace= Typeface.createFromAsset(getAssets(),"fonts/VeraMono.ttf");

        crimsonHead.setTypeface(fontFace);
        crimsonHead.setGravity(Gravity.CENTER);
        crimsonHead.setTypeface(crimsonHead.getTypeface(), Typeface.BOLD);
        crimsonHead.setTextColor(Color.parseColor("#FFCCCCCC"));
        crimsonHead.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10.f);

        /* TextView font adjustments */
        TextView authorText    = (TextView) findViewById(R.id.authorTextView);
        TextView emailText     = (TextView) findViewById(R.id.emailTextView);
        TextView homeText      = (TextView) findViewById(R.id.homeTextView);
        TextView thankYouText  = (TextView) findViewById(R.id.thankYouTextView);
        TextView copyrightText = (TextView) findViewById(R.id.copyrightTextView);
        authorText.setTypeface(fontFace);
        emailText.setTypeface(fontFace);
        homeText.setTypeface(fontFace);
        thankYouText.setTypeface(fontFace);
        copyrightText.setTypeface(fontFace);

        /* Transform HTML Link*/
        thankYouText.setText(Html.fromHtml(getString(R.string.thankyou_text)));
        thankYouText.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Handle presses on the action bar items
        switch (item.getItemId()) {

            // MainActivity
            case R.id.action_main:
                // Just for debugging
                //Toast.makeText(this, "Starting - Main Activity", Toast.LENGTH_SHORT).show();        // Could be removed, only for debugging reasons

                // Change Activity
                Intent intentMain = new Intent(InfoActivity.this, MainActivity.class);
                startActivity(intentMain);
                break;

            // Temperature Plot
            case R.id.action_temp:
                //Toast.makeText(this, "Starting - Temperature Plot Activity", Toast.LENGTH_SHORT).show();

                // Change Activity
                Intent intentTempPlot = new Intent(InfoActivity.this, TempActivity.class);
                startActivity(intentTempPlot);
                break;

            // Camera Activity
            case R.id.action_cam:
                //Toast.makeText(this, "Starting - Camera Activity", Toast.LENGTH_SHORT).show();     // Could be removed, only for debugging reasons

                // Change Activity
                Intent intentControl = new Intent(InfoActivity.this, MjpegActivity.class);
                startActivity(intentControl);
                break;

            // Settings action
            case R.id.action_settings:
                //Toast.makeText(this, "Starting Settings Activity", Toast.LENGTH_SHORT).show();      // Could be removed, only for debugging reasons

                // Change Activity
                Intent intentSettings = new Intent(InfoActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                break;

            default:
                break;
        }
        return true;
    }
}
