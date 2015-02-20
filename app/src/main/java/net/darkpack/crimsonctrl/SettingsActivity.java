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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class SettingsActivity extends Activity {
    EditText coreid, accesstoken, camurl, ctrlpin, ctrlvalue, mCamUser, mCamPass;
    CheckBox mCheckBoxAccessToken, mCheckBoxCamPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();


        Button btn = (Button) findViewById(R.id.scan_qrcode);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(SettingsActivity.this);
                integrator.addExtra("SCAN_WIDTH", 640);
                integrator.addExtra("SCAN_HEIGHT", 480);
                integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
                //customize the prompt message before scanning
                integrator.addExtra("PROMPT_MESSAGE", "Scanner Start!");
                integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
            }
        });


        mCheckBoxAccessToken = (CheckBox) findViewById(R.id.checkBoxAccessToken);
        mCheckBoxCamPass = (CheckBox) findViewById(R.id.checkBoxCamPass);


        // Listener to show AccessToken
        mCheckBoxAccessToken.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    // Show Access Token
                    accesstoken.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // Hide Access Token
                    accesstoken.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }

            }
        });

        // Listener to show Camera Password
        mCheckBoxCamPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    // Show Access Token
                    mCamPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // Hide Access Token
                    mCamPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            final String contents = result.getContents();
            if (contents != null) {
                Toast.makeText(this, "Scan successfull", Toast.LENGTH_SHORT).show();
                System.out.println("Result:            " + result);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // First split the scan result
                        String[] seperateScanResult = contents.split("\\r?\\n");

                        // Second, some debug info
                        for ( String c : seperateScanResult )
                            System.out.println(c);

                        System.out.println("SeperateScanResult Length:  " + seperateScanResult.length);
                        System.out.println("CoreID 1:                   " + seperateScanResult[0]);
                        System.out.println("Cam Password 7:             " + seperateScanResult[6]);
                        // Third split the array into value & content

                        // 0 CoreID
                        if ( seperateScanResult[0].contains("CoreId") ) {
                            String coreSplit[] = seperateScanResult[0].split("::");
                            System.out.println("Update CoreID:       " + coreSplit[1]);
                            coreid.setText(coreSplit[1]);
                        }
                        // 1 AccessToken
                        if ( seperateScanResult[1].contains("AccessToken") ) {
                            String accesstokenSplit[] = seperateScanResult[1].split("::");
                            System.out.println("Update AccessToken:  " + accesstokenSplit[1]);
                            accesstoken.setText(accesstokenSplit[1]);
                        }

                        // 2 ControlPin
                        if ( seperateScanResult[2].contains("ControlPin") ) {
                            String controlpinSplit[] = seperateScanResult[2].split("::");
                            System.out.println("Update ControlPin:   " + controlpinSplit[1]);
                            ctrlpin.setText(controlpinSplit[1]);
                        }

                        // 3 Function Name
                        if ( seperateScanResult[3].contains("FunctionName") ) {
                            String functionnameSplit[] = seperateScanResult[3].split("::");
                            System.out.println("Update FunctionName: " + functionnameSplit[1]);
                            ctrlvalue.setText(functionnameSplit[1]);
                        }

                        // 4 Camera URL
                        if ( seperateScanResult[4].contains("CamURL") ) {
                            String camurlSplit[] = seperateScanResult[4].split("::");
                            System.out.println("Update CamURL:      " + camurlSplit[1]);
                            camurl.setText(camurlSplit[1]);
                        }

                        // 5 Camera Auth User
                        if ( seperateScanResult[5].contains("CamUser") ) {
                            String camusrSplit[] = seperateScanResult[5].split("::");
                            System.out.println("Update CamPass:    " + camusrSplit[1]);
                            mCamUser.setText(camusrSplit[1]);
                        }

                        // 6 Camera Password
                        if ( seperateScanResult[6].contains("CamPassword") ) {
                            String accesstokenSplit[] = seperateScanResult[6].split("::");
                            System.out.println(accesstokenSplit[1]);
                            mCamPass.setText(accesstokenSplit[1]);
                        }

                    }
                });



            } else {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void init() {
        coreid = (EditText) findViewById(R.id.edit_core_id);
        accesstoken = (EditText) findViewById(R.id.edit_access_token);
        camurl = (EditText) findViewById(R.id.edit_cam_url);
        ctrlpin = (EditText) findViewById(R.id.edit_ctrl_pin);
        ctrlvalue = (EditText) findViewById(R.id.edit_ctrl_value);
        mCamUser = (EditText) findViewById(R.id.edit_cam_user);
        mCamPass = (EditText) findViewById(R.id.edit_cam_pass);
        readSettings();
    }

    public void save(View view) {
        String coreIdText = coreid.getText().toString();
        String accessTokenText = accesstoken.getText().toString();
        String camUrlText = camurl.getText().toString();
        String ctrlPinText = ctrlpin.getText().toString();
        String ctrlValueText = ctrlvalue.getText().toString();
        String camUserText = mCamUser.getText().toString();
        String camPassText = mCamPass.getText().toString();

        if (coreIdText != null)
            SettingsConnector.writeString(this, SettingsConnector.COREID, coreIdText);
        if (accessTokenText != null)
            SettingsConnector.writeString(this, SettingsConnector.ACCESSTOKEN, accessTokenText);
        if (camUrlText != null)
            SettingsConnector.writeString(this, SettingsConnector.CAMURL, camUrlText);
        if (ctrlPinText != null)
            SettingsConnector.writeString(this, SettingsConnector.CTRLPIN, ctrlPinText);
        if (ctrlValueText != null)
            SettingsConnector.writeString(this, SettingsConnector.CTRLVALUE, ctrlValueText);
        if (camUserText != null)
            SettingsConnector.writeString(this, SettingsConnector.CAMUSER, camUserText);
        if (camPassText != null)
            SettingsConnector.writeString(this, SettingsConnector.CAMPASS, camPassText);

        // User Feedback
        Toast.makeText(this, "SAVED", Toast.LENGTH_SHORT).show();

    }

    public void reset(View view) {
		/* A better way to delete all is:
		 * PreferenceConnector.getEditor(this).clear().commit();
		 */
        SettingsConnector.getEditor(this).remove(SettingsConnector.COREID).commit();
        SettingsConnector.getEditor(this).remove(SettingsConnector.ACCESSTOKEN).commit();
        SettingsConnector.getEditor(this).remove(SettingsConnector.CAMURL).commit();
        SettingsConnector.getEditor(this).remove(SettingsConnector.CTRLPIN).commit();
        SettingsConnector.getEditor(this).remove(SettingsConnector.CTRLVALUE).commit();
        SettingsConnector.getEditor(this).remove(SettingsConnector.CAMUSER).commit();
        SettingsConnector.getEditor(this).remove(SettingsConnector.CAMPASS).commit();
        readSettings();

        // User Feedback
        Toast.makeText(this, "RESET DATA", Toast.LENGTH_SHORT).show();
    }

    /*
     * Read the data refer to saved person and visualize them into Edittexts
     */
    private void readSettings() {
        coreid.setText(SettingsConnector.readString(this, SettingsConnector.COREID, null));
        accesstoken.setText(SettingsConnector.readString(this, SettingsConnector.ACCESSTOKEN, null));
        camurl.setText(SettingsConnector.readString(this, SettingsConnector.CAMURL, null));
        ctrlpin.setText(SettingsConnector.readString(this, SettingsConnector.CTRLPIN, null));
        ctrlvalue.setText(SettingsConnector.readString(this, SettingsConnector.CTRLVALUE, null));
        mCamUser.setText(SettingsConnector.readString(this, SettingsConnector.CAMUSER, null));
        mCamPass.setText(SettingsConnector.readString(this, SettingsConnector.CAMPASS, null));
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

            // Camera Activity
            case R.id.action_cam:
                Toast.makeText(this, "Starting - Camera Activity", Toast.LENGTH_SHORT).show();     // Could be removed, only for debugging reasons

                // Change Activity
                Intent intentControl = new Intent(SettingsActivity.this, MjpegActivity.class);
                startActivity(intentControl);
                break;

            // Info action
            case R.id.action_info:
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
