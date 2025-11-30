package com.quang;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetcher {

    public static String getWeather(String cityCode) {
        double lat = 0;
        double lon = 0;
        String cityName = "";

        switch (cityCode.toUpperCase()) {
            case "DANANG":
                lat = 16.0471;
                lon = 108.2068;
                cityName = "Đà Nẵng";
                break;
            case "HANOI":
                lat = 21.0285;
                lon = 105.8542;
                cityName = "Hà Nội";
                break;
            case "HCM":
                lat = 10.8231;
                lon = 106.6297;
                cityName = "TP. Hồ Chí Minh";
                break;
            default:
                return "Unknown city";
        }

        try {
            String urlStr = "https://api.open-meteo.com/v1/forecast?latitude=" +
                    lat + "&longitude=" + lon + "&current_weather=true";

            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder content = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                content.append(line);
            }

            in.close();

            JsonObject json = JsonParser.parseString(content.toString()).getAsJsonObject();
            JsonObject current = json.getAsJsonObject("current_weather");

            double temp = current.get("temperature").getAsDouble();
            double wind = current.get("windspeed").getAsDouble();
            double code = current.get("weathercode").getAsDouble();

            return  "City: " + cityName + "\n" +
                    "Temperature: " + temp + "°C\n" +
                    "Wind speed: " + wind + " km/h\n" +
                    "Weather code: " + code + "\n";

        } catch (Exception e) {
            return "Error fetching weather.";
        }
    }
}
