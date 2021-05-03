package com.example.solar_alarm.sunrise_sunset_http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class HttpRequests {
    private HttpURLConnection httpUrlConnection;

    public HttpRequests() throws IOException
    {
        URL url = new URL("https://api.sunrise-sunset.org/json");
        httpUrlConnection = (HttpURLConnection)url.openConnection();
        httpUrlConnection.setRequestMethod("GET");
    }

    public SunriseSunsetResponse GetSolarData(SunriseSunsetRequest sunriseSunsetRequest) throws IOException
    {
        SunriseSunsetResponse sunriseSunsetResponse = new SunriseSunsetResponse();

        httpUrlConnection.setDoOutput(true);
        DataOutputStream dataOutputStream = new DataOutputStream(httpUrlConnection.getOutputStream());
        dataOutputStream.writeBytes(getParamsString(sunriseSunsetRequest));
        dataOutputStream.flush();
        dataOutputStream.close();

        httpUrlConnection.setConnectTimeout(5000);
        httpUrlConnection.setReadTimeout(5000);

        int status = httpUrlConnection.getResponseCode();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = bufferedReader.readLine()) != null) {
            content.append(inputLine);
        }
        bufferedReader.close();

        return sunriseSunsetResponse;
    }

    public static String getParamsString(SunriseSunsetRequest sunriseSunsetRequest) throws UnsupportedEncodingException
    {
        return String.format("?date=%s;lat=%s;lon=%s;formatted=%s;",
                             URLEncoder.encode(sunriseSunsetRequest.getDate(), "UTF-8"),
                             URLEncoder.encode(sunriseSunsetRequest.getDate(), "UTF-8"),
                             URLEncoder.encode(String.valueOf(sunriseSunsetRequest.getLatitude()), "UTF-8"),
                             URLEncoder.encode(String.valueOf(sunriseSunsetRequest.getLongitude()), "UTF-8"));
    }
}
