package com.example.my_application;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

public class JSONParser {

    private static String extractDate(String isoDateTime) {
        return isoDateTime.split("T")[0];
    }

    public static List<Weather> parseWeatherData(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray times = jsonObject.getJSONObject("hourly").getJSONArray("time");
            JSONArray temperatures = jsonObject.getJSONObject("hourly").getJSONArray("temperature_2m");
            JSONArray humidityValues = jsonObject.getJSONObject("hourly").getJSONArray("relative_humidity_2m");  // Corrected key name

            JSONArray rainValues = jsonObject.getJSONObject("hourly").getJSONArray("rain");  // Corrected key name

            Map<String, List<Double>> temperatureMap = groupTemperaturesByDate(times, temperatures);
            Map<String, List<Double>> humidityMap = groupValuesByDate(times, humidityValues);

            Map<String, List<Double>> rainMap = groupValuesByDate(times, rainValues);

            return calculateAverageAndCreateWeather(temperatureMap, humidityMap, rainMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static Map<String, List<Double>> groupTemperaturesByDate(JSONArray times, JSONArray temperatures) {
        return null;
    }



    private static Map<String, List<Double>> groupValuesByDate(JSONArray times, JSONArray values) {
        Map<String, List<Double>> valueMap = new HashMap<>();

        IntStream.range(0, times.length()).forEach(i -> {
            String dateTime = null;
            try {
                dateTime = times.getString(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String date = extractDate(dateTime);
            double value = 0;
            try {
                value = values.getDouble(i);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            valueMap.computeIfAbsent(date, k -> new ArrayList<>()).add(value);
        });

        return valueMap;
    }

    private static List<Weather> calculateAverageAndCreateWeather(
            Map<String, List<Double>> temperatureMap,
            Map<String, List<Double>> humidityMap,
            Map<String, List<Double>> rainMap) {

        SimpleDateFormat isoFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        List<Weather> weatherDays = new ArrayList<>();

        if (temperatureMap != null && humidityMap != null && rainMap != null) {
            temperatureMap.forEach((date, dailyTemperatures) -> {
                double averageTemperature = dailyTemperatures.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double averageHumidity = calculateAverage(humidityMap.get(date));
                double totalRain = calculateTotal(rainMap.get(date));

                try {
                    Date dateObject = isoFormatter.parse(date);
                    weatherDays.add(new Weather(dateObject, averageTemperature, averageHumidity, totalRain));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        return weatherDays;
    }

    private static double calculateAverage(List<Double> values) {
        return values != null ? values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0) : 0.0;
    }

    private static double calculateTotal(List<Double> values) {
        return values != null ? values.stream().mapToDouble(Double::doubleValue).sum() : 0.0;
    }
}
