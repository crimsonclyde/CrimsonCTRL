package net.darkpack.crimsonctrl;

/**
 * _________        .__
 * \_   ___ \_______|__| _____   __________   ____
 * /    \  \/\_  __ \  |/     \ /  ___/  _ \ /    \
 * \     \____|  | \/  |  | |  \\___ (  <_> )   |  \
 *  \______  /|__|  |__|__|_|  /____  >____/|___|  /
 *         \/                \/     \/           \/
 * Project: CrimsonCTRL
 * File: SettingsActivity.java
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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class SettingsActivity extends Activity {
    EditText coreid, accesstoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        init();
    }

    private void init() {
        coreid = (EditText) findViewById(R.id.edit_core_id);
        accesstoken = (EditText) findViewById(R.id.edit_access_token);
        readSettings();
    }

    public void save(View view) {
        String coreIdText = coreid.getText().toString();
        String accessTokenText = accesstoken.getText().toString();

        if (coreIdText != null)
            SettingsConnector.writeString(this, SettingsConnector.COREID, coreIdText);
        if (accessTokenText != null)
            SettingsConnector.writeString(this, SettingsConnector.ACCESSTOKEN, accessTokenText);
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();

    }

    public void reset(View view) {
		/* A better way to delete all is:
		 * PreferenceConnector.getEditor(this).clear().commit();
		 */
        SettingsConnector.getEditor(this).remove(SettingsConnector.COREID).commit();
        SettingsConnector.getEditor(this).remove(SettingsConnector.ACCESSTOKEN).commit();
        readSettings();
    }

    /*
     * Read the data refer to saved person and visualize them into Edittexts
     */
    private void readSettings() {
        coreid.setText(SettingsConnector.readString(this, SettingsConnector.COREID, null));
        accesstoken.setText(SettingsConnector.readString(this, SettingsConnector.ACCESSTOKEN, null));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);

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
                Intent intentMain = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intentMain);
                break;

            // SCL Control
            case R.id.action_control:
                Toast.makeText(this, "Starting - Control Activity", Toast.LENGTH_SHORT).show();     // Could be removed, only for debugging reasons

                // Change Activity
                Intent intentControl = new Intent(SettingsActivity.this, ControlActivity.class);
                startActivity(intentControl);
                break;

            // Info action
            case R.id.action_settings:
                Toast.makeText(this, "Starting - Info Activity", Toast.LENGTH_SHORT).show();        // Could be removed, only for debugging reasons

                // Change Activity
                Intent intentInfo = new Intent(SettingsActivity.this, InfoActivity.class);
                startActivity(intentInfo);
                break;

            default:
                break;
        }
        return true;

    }

}
