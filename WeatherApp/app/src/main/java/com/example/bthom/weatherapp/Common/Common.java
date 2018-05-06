package com.example.bthom.weatherapp.Common;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by bthom on 4/29/2018.
 */

public class Common {
    private static String API_KEY = "2942e762a1d9e3df6cad51bdf368f522"; //my api key
    private static String API_LINK = "http://api.openweathermap.org/data/2.5/weather"; //link for current weather

    @NonNull
    public static String apiRequest(String lat, String lon){
        StringBuilder sb = new StringBuilder(API_LINK);
        //http://api.openweathermap.org/data/2.5/weather?lat=43.744712652516&lon=-70.45427071478595&appid=2942e762a1d9e3df6cad51bdf368f522
        sb.append(String.format("?lat=%s&lon=%s&APPID=%s&units=imperial",lat,lon,API_KEY));
        System.out.println("SB = " + sb);
        return sb.toString();

    }
    public static String unixTimeStampToDateTime(double unixTimeStamp){

        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long)unixTimeStamp*1000);
        return dateFormat.format(date);

    }
    public static String getImage(String image){
        return String.format("http://openweathermap.org/img/w/%s.png",image);
    }
    public static String getDateNow(){
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM YYYY HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static String getApiKey() {
        return API_KEY;
    }
    public static void setApiKey(String apiKey) {
        API_KEY = apiKey;
    }
    public static String getApiLink() {
        return API_LINK;
    }
    public static void setApiLink(String apiLink) {
        API_LINK = apiLink;
    }


}
