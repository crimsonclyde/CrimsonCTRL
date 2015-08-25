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
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = SettingsActivity.class.getSimpleName();
    EditText mCoreId, mAccessToken, mCamUrl, mCtrlPin, mCtrlValue, mCamUser, mCamPass, mTempServiceUrl, mWebsocketUrl, mWebsocketPort;
    ImageButton mShowAccessToken, mShowCamPass, mScanQrCode;
    protected String mActivityTitle = "Settings";
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    String[] mDrawerListItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();

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
                        Intent intentCam = new Intent(SettingsActivity.this, MjpegActivity.class);
                        startActivity(intentCam);
                        break;

                    case 2:
                        // Change Activity
                        Intent intentTempPlot = new Intent(SettingsActivity.this, TempActivity.class);
                        startActivity(intentTempPlot);
                        break;

                    case 3:
                        // Change Activity
                        Intent intentSettings = new Intent(SettingsActivity.this, SettingsActivity.class);
                        startActivity(intentSettings);
                        break;

                    case 4:
                        // Change Activity
                        Intent intentInfo = new Intent(SettingsActivity.this, InfoActivity.class);
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
        getSupportActionBar().setTitle("Settings");
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
                    mAccessToken.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mAccessToken.setTransformationMethod(PasswordTransformationMethod.getInstance());
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
                    mCamPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                } else {
                    mCamPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
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
                        System.out.println("WebService URL:             " + seperateScanResult[7]);
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

                        // 7 TemperatureServiceUrl
                        if ( seperateScanResult[7].contains("TemperatureServiceUrl") ) {
                            String tempServiceUrlSplit[] = seperateScanResult[7].split("::");
                            String mTempServiceUrlSplitted = tempServiceUrlSplit[1];
                            String mTempServiceUrlConverted = mTempServiceUrlSplitted.replace("&amp;","&");
                            mTempServiceUrl.setText(mTempServiceUrlConverted);

                        }

                        // 8 WebsocketUrl
                        if ( seperateScanResult[8].contains("WebSocketUrl") ) {
                            String webSocketUrlSplit[] = seperateScanResult[8].split("::");
                            System.out.println("WebSocket URL :    " + webSocketUrlSplit[1]);
                            mWebsocketUrl.setText(webSocketUrlSplit[1]);
                        }

                        // WebsocketPort
                        if ( seperateScanResult[9].contains("WebSocketPort") ) {
                            String webSocketPortSplit[] = seperateScanResult[9].split("::");
                            System.out.println("WebSocket Port:    " + webSocketPortSplit[1]);
                            mWebsocketPort.setText(webSocketPortSplit[1]);
                        }



                    }
                });


            } else {
                Toast.makeText(this, "Fail converting results", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void init() {
        mCoreId         = (EditText) findViewById(R.id.edit_core_id);
        mAccessToken    = (EditText) findViewById(R.id.edit_access_token);
        mCamUrl         = (EditText) findViewById(R.id.edit_cam_url);
        mCtrlPin        = (EditText) findViewById(R.id.edit_ctrl_pin);
        mCtrlValue      = (EditText) findViewById(R.id.edit_ctrl_value);
        mCamUser        = (EditText) findViewById(R.id.edit_cam_user);
        mCamPass        = (EditText) findViewById(R.id.edit_cam_pass);
        mTempServiceUrl = (EditText) findViewById(R.id.edit_temp_service);
        mWebsocketUrl   = (EditText) findViewById(R.id.edit_websocket_url);
        mWebsocketPort  = (EditText) findViewById(R.id.edit_websocket_port);
        readSettings();
    }

    public void save(View view) {
        String coreIdText       = mCoreId.getText().toString();
        String accessTokenText  = mAccessToken.getText().toString();
        String camUrlText       = mCamUrl.getText().toString();
        String ctrlPinText      = mCtrlPin.getText().toString();
        String ctrlValueText    = mCtrlValue.getText().toString();
        String camUserText      = mCamUser.getText().toString();
        String camPassText      = mCamPass.getText().toString();
        String tempServiceUrl   = mTempServiceUrl.getText().toString();
        String websocketurl     = mWebsocketUrl.getText().toString();
        String websocketport    = mWebsocketPort.getText().toString();


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
        if (tempServiceUrl != null)
            SettingsConnector.writeString(this, SettingsConnector.TEMPSERVICEURL, tempServiceUrl);
        if (websocketurl != null)
            SettingsConnector.writeString(this, SettingsConnector.WEBSOCKETURL, websocketurl );
        if (websocketport != null)
            SettingsConnector.writeString(this, SettingsConnector.WEBSOCKETPORT, websocketport);

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
        mTempServiceUrl.setText(SettingsConnector.readString(this, SettingsConnector.TEMPSERVICEURL, null));
        mWebsocketUrl.setText(SettingsConnector.readString(this, SettingsConnector.WEBSOCKETURL, null));
        mWebsocketPort.setText(SettingsConnector.readString(this, SettingsConnector.WEBSOCKETPORT, null));
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


        // Toolbar Navigation Drawer
        switch (item.getItemId()){
            case android.R.id.home: {
                if (mDrawerLayout.isDrawerOpen(mDrawerList)){
                    mDrawerLayout.closeDrawer(mDrawerList);
                } else {
                    mDrawerLayout.openDrawer(mDrawerList);
                }
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }

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
            SettingsConnector.getEditor(SettingsActivity.this).remove(SettingsConnector.TEMPSERVICEURL).commit();
            SettingsConnector.getEditor(SettingsActivity.this).remove(SettingsConnector.WEBSOCKETURL).commit();
            SettingsConnector.getEditor(SettingsActivity.this).remove(SettingsConnector.WEBSOCKETPORT).commit();
            readSettings();
        }
    }


}