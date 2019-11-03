package com.inspirado.kuber;

import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 *
 */

/**
 * @author navnit
 */
public class Util implements Serializable {

    static Hashtable paymentOptions;
    static Hashtable statusTable;

    static{
        paymentOptions = new Hashtable();
        paymentOptions.put("1","Bank A/C Transfer");
        paymentOptions.put("2","PhonePe");
        paymentOptions.put("3","Paytm");
        paymentOptions.put("4","GPay");
        paymentOptions.put("5","BHIM");

        statusTable = new Hashtable<>();
        statusTable.put(1,"Awaiting Acceptance");
        statusTable.put(2,"Accepted");
        statusTable.put(3,"Transferred");
        statusTable.put(4,"Closed");
        statusTable.put(5,"Escalated");
        statusTable.put(10,"Cancelled");
        statusTable.put(11,"Expired");

    }

    public static void  updateSharedPref(SharedPreferences pref, JSONObject response ){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user", response.toString());
        editor.commit();
    }

    public static String getGMTDateTime(String localDateTime, TimeZone localTimeZone) {
        DateFormat dfIS = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ROOT);
        dfIS.setTimeZone(localTimeZone);
        DateFormat dfUtc = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
        dfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return dfUtc.format(dfIS.parse(localDateTime));
        } catch (ParseException e) {
            return null;              // invalid input
        }
    }

    public static String getLocalDateTime(String gmtDateTime, TimeZone localTimeZone) {
        DateFormat dfLocal = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ROOT);
        dfLocal.setTimeZone(localTimeZone);
        DateFormat dfUtc = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
        dfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return dfLocal.format(dfUtc.parse(gmtDateTime));

        } catch (ParseException e) {
            return null;              // invalid input
        }
    }

    public static String getCurrentLocalDate(TimeZone localTimeZone) {
        DateFormat dfLocal = new SimpleDateFormat("dd-MM-yyyy", Locale.ROOT);
        dfLocal.setTimeZone(localTimeZone);
        try {
            return dfLocal.format(new Date());
        } catch (Exception e) {
            return null;              // invalid input
        }
    }

    public static String getCurrentLocalTime(TimeZone localTimeZone) {
        DateFormat dfLocal = new SimpleDateFormat("HH:mm", Locale.ROOT);
        dfLocal.setTimeZone(localTimeZone);
        try {
            return dfLocal.format(new Date());
        } catch (Exception e) {
            return null;              // invalid input
        }
    }

    public static String getCurrentGMTTime(long offsetSec) {
        DateFormat dfUtc = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
        dfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.add(Calendar.SECOND, (int)offsetSec);
        Date date = calendar.getTime();
        try {
            return dfUtc.format(date);
        } catch (Exception e) {
            return null;              // invalid input
        }
    }

    public static long getTimeDiff(String date1, String date2, TimeZone timeZone) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
        dateFormat.setTimeZone(timeZone);
        try {
            Date dt1 = (date1 == null) ? new Date() : dateFormat.parse(date1);
            Date dt2 = (date2 == null) ? new Date() : dateFormat.parse(date2);
            return TimeUnit.MILLISECONDS.toSeconds(dt1.getTime() - dt2.getTime());
        } catch (Exception e) {
            return 0;              // invalid input
        }
    }

    public static List getPaymentOptionsDetails(String optionList){
        ArrayList list = new ArrayList();
        StringTokenizer tokenizer =  new StringTokenizer(optionList,",");
        while (tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
            String value = paymentOptions.get(token)+"";
            list.add(value);
        }
        return list;
    }

    public static String getPaymentOptionCode(String optionDetail){
        Enumeration e = paymentOptions.keys();
        while(e.hasMoreElements()){
            String key = e.nextElement()+"";
            if(paymentOptions.get(key).toString().equalsIgnoreCase(optionDetail)){
                return key;
            }
        }
        return "";
    }
    public static String getStatusDetail(int optionCode) {
        return statusTable.get(optionCode)+"";
    }

}
