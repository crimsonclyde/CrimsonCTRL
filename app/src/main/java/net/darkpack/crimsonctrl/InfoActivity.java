package net.darkpack.crimsonctrl;

/**
 * _________        .__
 * \_   ___ \_______|__| _____   __________   ____
 * /    \  \/\_  __ \  |/     \ /  ___/  _ \ /    \
 * \     \____|  | \/  |  | |  \\___ (  <_> )   |  \
 *  \______  /|__|  |__|__|_|  /____  >____/|___|  /
 *         \/                \/     \/           \/
 * Project: CrimsonCTRL
 * File: InfoActivity.java
 *
 * Author : CrimsonClyde
 * E-Mail : clyde_at_darkpack.net
 * Thx    : 4nt1g, Seelenfaenger, Bonnie
 * Use at your own risk! Keep Mordor tidy
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

public class InfoActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);


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
                Toast.makeText(this, "Starting - Main Activity", Toast.LENGTH_SHORT).show();        // Could be removed, only for debugging reasons

                // Change Activity
                Intent intentMain = new Intent(InfoActivity.this, MainActivity.class);
                startActivity(intentMain);
                break;

            // SCL Control
            case R.id.action_control:
                Toast.makeText(this, "Starting - Control Activity", Toast.LENGTH_SHORT).show();     // Could be removed, only for debugging reasons

                // Change Activity
                Intent intentControl = new Intent(InfoActivity.this, ControlActivity.class);
                startActivity(intentControl);
                break;

            // Settings action
            case R.id.action_settings:
                Toast.makeText(this, "Starting Settings Activity", Toast.LENGTH_SHORT).show();      // Could be removed, only for debugging reasons

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
