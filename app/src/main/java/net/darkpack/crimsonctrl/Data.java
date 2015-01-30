package net.darkpack.crimsonctrl;

/**
 * _________        .__
 * \_   ___ \_______|__| _____   __________   ____
 * /    \  \/\_  __ \  |/     \ /  ___/  _ \ /    \
 * \     \____|  | \/  |  | |  \\___ (  <_> )   |  \
 *  \______  /|__|  |__|__|_|  /____  >____/|___|  /
 *         \/                \/     \/           \/
 * Project: CrimsonCTRL
 * File: Data.java
 *
 * Author : CrimsonClyde
 * E-Mail : clyde_at_darkpack.net
 * Thx    : 4nt1g, Seelenfaenger, Bonnie
 * Use at your own risk! Keep Mordor tidy
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Data {
    private String date = null;
    private String time = null;
    private Integer wifi = null;
    private Integer photo = null;
    private String temp = null;
    private Integer scl = null;

    public boolean isReady() {
        return (date != null && time != null && photo != null && temp != null && wifi != null);
    }

    // Data
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    // Time
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    // RSSI Signal strength
    public Integer getWifi() { return wifi;}
    public void setWifi(Integer wifi) {this.wifi = wifi; }

    // Temperature
    public String getTemp() {
        return temp;
    }
    public void setTemp(String temp) {
        this.temp = temp;
    }

    // Photocell Value
    public Integer getPhoto() {
        return photo;
    }
    public void setPhoto(Integer photo) {
        this.photo = photo;
    }

    // StoneCircleLight
    public Integer getScl() { return scl;}
    public void setSCL(Integer scl) {this.scl = scl; }

    public Date getDatestamp() throws ParseException{
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(date + " " + time);
    }

    @Override
    public String toString() {
        return "Data [date=" + date + ", time=" + time + ", photo=" + photo
                + ", temp=" + temp + "]";
    }
}
