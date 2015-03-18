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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class SettingsActivity extends Activity {
    public static final String TAG = SettingsActivity.class.getSimpleName();
    EditText mCoreId, mAccessToken, mCamUrl, mCtrlPin, mCtrlValue, mCamUser, mCamPass;
    ImageButton mShowAccessToken, mShowCamPass, mScanQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();

        /* Header font adjustments */
        TextView crimsonHead = (TextView) findViewById(R.id.crimsonHead);
        Typeface fontFace= Typeface.createFromAsset(getAssets(),"fonts/VeraMono.ttf");
        crimsonHead.setTypeface(fontFace);
        crimsonHead.setGravity(Gravity.CENTER);

        /* Suppress opening virtual keyboard */
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        /* ZXing Scanner Button */
        mScanQrCode = (ImageButton) findViewById(R.id.qrcodeImageButton);
        mScanQrCode.setOnClickListener(new View.OnClickListener() {
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


        /* Visibility Button Core Access Token */
        mShowAccessToken = (ImageButton) findViewById(R.id.accesstokenImageButton);
        mShowAccessToken.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.selector));
        mShowAccessToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                button.setSelected(!button.isSelected());

                if (button.isSelected()) {
                    mAccessToken.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    mAccessToken.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });


        /* Visibility Button Camera Password*/
        mShowCamPass = (ImageButton) findViewById(R.id.campassImageButton);
        mShowCamPass.setImageDrawable(getBaseContext().getResources().getDrawable(R.drawable.selector));
        mShowCamPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                button.setSelected(!button.isSelected());

                if (button.isSelected()) {
                    mCamPass.setTransformationMethod(PasswordTransformationMethod.getInstance());

                } else {
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
                Toast.makeText(this, "Scan successful", Toast.LENGTH_SHORT).show();
                System.out.println("Result:            " + result);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // First split the scan result
                        String[] seperateScanResult = contents.split("\\r?\\n");

                        // Second, some debug info
                        for ( String c : seperateScanResult )
                            System.out.println(c);

                        System.out.println("SeparateScanResult Length:  " + seperateScanResult.length);
                        System.out.println("CoreID 1:                   " + seperateScanResult[0]);
                        System.out.println("Cam Password 7:             " + seperateScanResult[6]);
                        // Third split the array into value & content

                        // 0 CoreID
                        if ( seperateScanResult[0].contains("CoreId") ) {
                            String coreSplit[] = seperateScanResult[0].split("::");
                            System.out.println("Update CoreID:       " + coreSplit[1]);
                            mCoreId.setText(coreSplit[1]);
                        }
                        // 1 AccessToken
                        if ( seperateScanResult[1].contains("AccessToken") ) {
                            String accesstokenSplit[] = seperateScanResult[1].split("::");
                            System.out.println("Update AccessToken:  " + accesstokenSplit[1]);
                            mAccessToken.setText(accesstokenSplit[1]);
                        }

                        // 2 ControlPin
                        if ( seperateScanResult[2].contains("ControlPin") ) {
                            String controlpinSplit[] = seperateScanResult[2].split("::");
                            System.out.println("Update ControlPin:   " + controlpinSplit[1]);
                            mCtrlPin.setText(controlpinSplit[1]);
                        }

                        // 3 Function Name
                        if ( seperateScanResult[3].contains("FunctionName") ) {
                            String functionnameSplit[] = seperateScanResult[3].split("::");
                            System.out.println("Update FunctionName: " + functionnameSplit[1]);
                            mCtrlValue.setText(functionnameSplit[1]);
                        }

                        // 4 Camera URL
                        if ( seperateScanResult[4].contains("CamURL") ) {
                            String camurlSplit[] = seperateScanResult[4].split("::");
                            System.out.println("Update CamURL:      " + camurlSplit[1]);
                            mCamUrl.setText(camurlSplit[1]);
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
        mCoreId = (EditText) findViewById(R.id.edit_core_id);
        mAccessToken = (EditText) findViewById(R.id.edit_access_token);
        mCamUrl = (EditText) findViewById(R.id.edit_cam_url);
        mCtrlPin = (EditText) findViewById(R.id.edit_ctrl_pin);
        mCtrlValue = (EditText) findViewById(R.id.edit_ctrl_value);
        mCamUser = (EditText) findViewById(R.id.edit_cam_user);
        mCamPass = (EditText) findViewById(R.id.edit_cam_pass);
        readSettings();
    }

    public void save(View view) {
        String coreIdText = mCoreId.getText().toString();
        String accessTokenText = mAccessToken.getText().toString();
        String camUrlText = mCamUrl.getText().toString();
        String ctrlPinText = mCtrlPin.getText().toString();
        String ctrlValueText = mCtrlValue.getText().toString();
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
        Toast.makeText(this, "Successfully saved", Toast.LENGTH_SHORT).show();

    }

    public void reset(View view) {
		/* Show Dialog before wiping data
		*  TODO: Update deprecated showDialog(int)
		*/
        showDialog(DIALOG_ALERT);

    }

    /*
     * Read the data refer to saved person and visualize them into Edittexts
     */
    private void readSettings() {
        mCoreId.setText(SettingsConnector.readString(this, SettingsConnector.COREID, null));
        mAccessToken.setText(SettingsConnector.readString(this, SettingsConnector.ACCESSTOKEN, null));
        mCamUrl.setText(SettingsConnector.readString(this, SettingsConnector.CAMURL, null));
        mCtrlPin.setText(SettingsConnector.readString(this, SettingsConnector.CTRLPIN, null));
        mCtrlValue.setText(SettingsConnector.readString(this, SettingsConnector.CTRLVALUE, null));
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


    // constant for identifying the dialog
    private static final int DIALOG_ALERT = 10;


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_ALERT:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Clear all settings data?");
                builder.setCancelable(true);
                builder.setPositiveButton("Clear", new OkOnClickListener());
                builder.setNegativeButton("Cancel", new CancelOnClickListener());
                AlertDialog dialog = builder.create();
                dialog.show();
        }
        return super.onCreateDialog(id);
    }

    private final class CancelOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "Canceled");
        }
    }

    private final class OkOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            SettingsConnector.getEditor(SettingsActivity.this).remove(SettingsConnector.COREID).commit();
            SettingsConnector.getEditor(SettingsActivity.this).remove(SettingsConnector.ACCESSTOKEN).commit();
            SettingsConnector.getEditor(SettingsActivity.this).remove(SettingsConnector.CAMURL).commit();
            SettingsConnector.getEditor(SettingsActivity.this).remove(SettingsConnector.CTRLPIN).commit();
            SettingsConnector.getEditor(SettingsActivity.this).remove(SettingsConnector.CTRLVALUE).commit();
            SettingsConnector.getEditor(SettingsActivity.this).remove(SettingsConnector.CAMUSER).commit();
            SettingsConnector.getEditor(SettingsActivity.this).remove(SettingsConnector.CAMPASS).commit();
            readSettings();
        }
    }


}