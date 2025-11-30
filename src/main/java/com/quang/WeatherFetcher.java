package com.quang;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetcher {

    public static String getWeather(String cityCode) {
        double lat = 0, lon = 0;
        String cityName = "";

        switch (cityCode.toUpperCase()) {
            case "DANANG":
                lat = 16.0471; lon = 108.2068; cityName = "Đà Nẵng";
                break;
            case "HANOI":
                lat = 21.0285; lon = 105.8542; cityName = "Hà Nội";
                break;
            case "HCM":
                lat = 10.8231; lon = 106.6297; cityName = "TP. Hồ Chí Minh";
                break;
            default:
                return "Unknown city";
        }

        try {
            String urlStr =
                    "https://api.open-meteo.com/v1/forecast?" +
                            "latitude=" + lat +
                            "&longitude=" + lon +
                            "&current=temperature_2m,relativehumidity_2m,apparent_temperature,weather_code,wind_speed_10m" +
                            "&hourly=cloud_cover,pressure_msl,uv_index,visibility" +
                            "&daily=sunrise,sunset" +
                            "&timezone=auto";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder raw = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) raw.append(line);
            in.close();

            JsonObject json = JsonParser.parseString(raw.toString()).getAsJsonObject();

            JsonObject current = json.getAsJsonObject("current");
            JsonObject hourly = json.getAsJsonObject("hourly");
            JsonObject daily = json.getAsJsonObject("daily");

            // Lấy chỉ số hiện tại
            double temp = current.get("temperature_2m").getAsDouble();
            double realFeel = current.get("apparent_temperature").getAsDouble();
            double humidity = current.get("relativehumidity_2m").getAsDouble();
            double wind = current.get("wind_speed_10m").getAsDouble();
            int wCode = current.get("weather_code").getAsInt();

            // Hourly
            double cloud = hourly.getAsJsonArray("cloud_cover").get(0).getAsDouble();
            double pressure = hourly.getAsJsonArray("pressure_msl").get(0).getAsDouble();
            double uv = hourly.getAsJsonArray("uv_index").get(0).getAsDouble();
            double visibility = hourly.getAsJsonArray("visibility").get(0).getAsDouble() / 1000.0; // m → km

            // Daily
            String sunrise = daily.getAsJsonArray("sunrise").get(0).getAsString();
            String sunset = daily.getAsJsonArray("sunset").get(0).getAsString();

            return
                    "City: " + cityName + "\n" +
                            "Temperature: " + temp + " °C\n" +
                            "RealFeel: " + realFeel + " °C\n" +
                            "Weather Code: " + wCode + "\n" +
                            "Weather Status: " + decodeWeatherCode(wCode) + "\n" +
                            "Wind Speed: " + wind + " km/h\n" +
                            "Humidity: " + humidity + " %\n" +
                            "Cloud Cover: " + cloud + " %\n" +
                            "Pressure: " + pressure + " hPa\n" +
                            "UV Index: " + uv + "\n" +
                            "Visibility: " + String.format("%.1f km", visibility) + "\n" +
                            "Sunrise: " + sunrise + "\n" +
                            "Sunset: " + sunset + "\n";

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private static String decodeWeatherCode(int code) {
        if (code == 0) return "Clear sky";
        else if (code <= 3) return "Cloudy";
        else if (code <= 55) return "Rainy";
        else if (code <= 65) return "Showers";
        else if (code <= 80) return "Heavy rain";
        else return "Unknown";
    }
}
