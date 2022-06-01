package com.example.solar_alarm.sunrise_sunset_http;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class HttpRequests {
    private final HttpURLConnection httpUrlConnection;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public HttpRequests(SunriseSunsetRequest sunriseSunsetRequest) throws IOException
    {
        String query = getParamsString(sunriseSunsetRequest);
        URL url = new URL("https://api.sunrise-sunset.org/json" + query);

        httpUrlConnection = (HttpURLConnection)url.openConnection();
        httpUrlConnection.setRequestMethod("GET");
    }

    public SunriseSunsetResponse GetSolarData(SunriseSunsetRequest sunriseSunsetRequest) throws IOException
    {
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setConnectTimeout(5000);
        httpUrlConnection.setReadTimeout(5000);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();

        while ((inputLine = bufferedReader.readLine()) != null) {
            content.append(inputLine);
        }

        Gson gson = new Gson();
        SunriseSunsetResponse sunriseSunsetResponse = gson.fromJson(content.toString(), SunriseSunsetResponse.class);
        bufferedReader.close();

        sunriseSunsetResponse.request = sunriseSunsetRequest;

        return sunriseSunsetResponse;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getParamsString(SunriseSunsetRequest sunriseSunsetRequest) throws UnsupportedEncodingException
    {
        //https://api.sunrise-sunset.org/json?date=2021-5-10&lat=40.67441&lng=-73.43162&formatted=0
        return String.format("?date=%s-%s-%s&lat=%s&lng=%s&formatted=0",
                             URLEncoder.encode(String.valueOf(sunriseSunsetRequest.RequestDate.getYear()),             "UTF-8"),
                             URLEncoder.encode(String.valueOf(sunriseSunsetRequest.RequestDate.getMonth().getValue()), "UTF-8"),
                             URLEncoder.encode(String.valueOf(sunriseSunsetRequest.RequestDate.getDayOfMonth()),       "UTF-8"),
                             URLEncoder.encode(String.valueOf(sunriseSunsetRequest.Latitude),                          "UTF-8"),
                             URLEncoder.encode(String.valueOf(sunriseSunsetRequest.Longitude),                         "UTF-8"));
    }
}
