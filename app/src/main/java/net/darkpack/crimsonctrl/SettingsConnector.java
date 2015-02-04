package net.darkpack.crimsonctrl;

/**
 * _________        .__
 * \_   ___ \_______|__| _____   __________   ____
 * /    \  \/\_  __ \  |/     \ /  ___/  _ \ /    \
 * \     \____|  | \/  |  | |  \\___ (  <_> )   |  \
 *  \______  /|__|  |__|__|_|  /____  >____/|___|  /
 *         \/                \/     \/           \/
 * Project: CrimsonCTRL
 * File: SettingsConnector.java
 *
 * Author : CrimsonClyde
 * E-Mail : clyde_at_darkpack.net
 * Thx    : 4nt1g, Seelenfaenger, Bonnie
 * Use at your own risk! Keep Mordor tidy
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingsConnector {

    public static final String PREF_NAME = "CTRL_SETTINGS";
    public static final int MODE = Context.MODE_PRIVATE;

    public static final String COREID = "COREID";
    public static final String ACCESSTOKEN = "ACCESSTOKEN";
    public static final String CAMURL = "CAMURL";
    public static final String CTRLPIN = "CTRLPIN";
    public static final String CTRLVALUE = "CTRLVALUE";
    public static final String CAMUSER = "CAMUSER";
    public static final String CAMPASS = "CAMPASS";



    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();

    }

    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }


    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    public static Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }


}
