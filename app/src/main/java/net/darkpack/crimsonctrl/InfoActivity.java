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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class InfoActivity extends AppCompatActivity {

    // Declare variables & set some defaults
    public static final String TAG = InfoActivity.class.getSimpleName();
    protected String mActivityTitle = "About";
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    String[] mDrawerListItems;
    ImageButton expandCard2, expandCard3, expandCard4;
    TextView holdCard2, holdCard3, holdCard4;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

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
                        Intent intentControl = new Intent(InfoActivity.this, MainActivity.class);
                        startActivity(intentControl);
                        break;

                    case 2:
                        // Change Activity
                        Intent intentCam = new Intent(InfoActivity.this, MjpegActivity.class);
                        startActivity(intentCam);
                        break;

                    case 3:
                        // Change Activity
                        Intent intentTemperature = new Intent(InfoActivity.this, TempActivity.class);
                        startActivity(intentTemperature);
                        break;

                    case 4:
                        // Change Activity
                        Intent intentInfo = new Intent(InfoActivity.this, SettingsActivity.class);
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
        getSupportActionBar().setTitle("About");
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


        /*
         * OnClickListener for collapse and expand CardView
         */


        // Card2 - Application Info
        expandCard2 = (ImageButton) findViewById(R.id.buttonCard2);
        holdCard2 = (TextView) findViewById(R.id.holderAbout);
        final int COLLAPSED = 0;
        final int EXPANDED = 1;

        // Set defaults
        holdCard2.setText(R.string.about_app_text_collapsed);
        holdCard2.setTag(COLLAPSED);

        // OnClicklistener including conditional statement
        expandCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int) holdCard2.getTag();

                if (i == EXPANDED) {
                    holdCard2.setText(R.string.about_app_text_collapsed);
                    holdCard2.setTag(COLLAPSED);

                } else {
                    holdCard2.setText(R.string.about_app_text_expanded);
                    holdCard2.setTag(EXPANDED);
                }

            }
        });

        // Card3 - Application Info
        expandCard3 = (ImageButton) findViewById(R.id.buttonCard3);
        holdCard3 = (TextView) findViewById(R.id.holderThx);
        final int COLLAPSED3 = 0;
        final int EXPANDED3 = 1;

        // Set defaults
        holdCard3.setText(R.string.about_thx_text_collapsed);
        holdCard3.setTag(COLLAPSED);

        // OnClicklistener including conditional statement
        expandCard3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int)holdCard3.getTag();

                if (i == EXPANDED3 ) {
                    holdCard3.setText(R.string.about_thx_text_collapsed);
                    holdCard3.setTag(COLLAPSED3);

                } else {
                    holdCard3.setText(R.string.about_thx_text_expanded);
                    holdCard3.setTag(EXPANDED3);
                }

            }
        });

        // Card4 - Legal
        expandCard4 = (ImageButton) findViewById(R.id.buttonCard4);
        holdCard4 = (TextView) findViewById(R.id.holderLegal);
        final int COLLAPSED4 = 0;
        final int EXPANDED4 = 1;

        // Set defaults
        holdCard4.setText(R.string.about_legal_text_collapsed);
        holdCard4.setTag(COLLAPSED);

        // OnClicklistener including conditional statement
        expandCard4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = (int)holdCard4.getTag();

                if (i == EXPANDED4 ) {
                    holdCard4.setText(R.string.about_legal_text_collapsed);
                    holdCard4.setTag(COLLAPSED4);

                } else {
                    holdCard4.setText(R.string.about_legal_text_expanded);
                    holdCard4.setTag(EXPANDED4);
                }

            }
        });


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
}
