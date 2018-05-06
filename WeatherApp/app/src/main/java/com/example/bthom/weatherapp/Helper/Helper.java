package com.example.bthom.weatherapp.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by bthom on 4/29/2018.
 * Helper Class
 */

public class Helper {
    static String stream = null;

     public Helper(){
     }

     public String getHTTPData(String urlString){
         try {
             URL url = new URL(urlString);
             HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
             System.out.println("URL CONNECTION RESPONSE= " + httpURLConnection.getResponseCode());
             if(httpURLConnection.getResponseCode() == 200){ //200 == ok
                 BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                 StringBuilder sb = new StringBuilder();
                 String line;
                 while( (line = br.readLine()) != null){
                     sb.append(line);
                     stream = sb.toString();
                     httpURLConnection.disconnect();
                 }
             }
         } catch (MalformedURLException e) {
             e.getMessage();
         } catch (IOException e) {
             e.printStackTrace();
         }
      return stream;

     }


}
