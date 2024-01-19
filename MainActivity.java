package com.example.my_application;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView[] weatherViews = new TextView[7];
    private final String WEATHER_API_URL = "https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&hourly=temperature_2m,relative_humidity_2m,rain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeTextViews();
        new FetchWeatherData().execute(WEATHER_API_URL);
    }

    private void initializeTextViews() {
        int[] textViewIds = {
                R.id.tvDay1, R.id.tvDay2, R.id.tvDay3,
                R.id.tvDay4, R.id.tvDay5, R.id.tvDay6, R.id.tvDay7
        };

        for (int i = 0; i < weatherViews.length; i++) {
            weatherViews[i] = findViewById(textViewIds[i]);
        }
    }

    private class FetchWeatherData extends AsyncTask<String, Void, List<Weather>> {

        @Override
        protected List<Weather> doInBackground(String... urls) {
            String jsonResponse = "";

            try {
                URL url = new URL(urls[0]);
                Log.d("MainActivity", "URL: " + url.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                Log.d("MainActivity", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    jsonResponse = readResponse(connection);
                    Log.d("MainActivity", "JSON Response: " + jsonResponse);
                    return JSONParser.parseWeatherData(jsonResponse);
                }

                Log.e("MainActivity", "Non-OK Response received");
            } catch (Exception e) {
                Log.e("MainActivity", "Error in network operation", e);
                return null;
            }

            return JSONParser.parseWeatherData(jsonResponse);
        }

        private String readResponse(HttpURLConnection connection) throws Exception {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            reader.close();
            return builder.toString();
        }

        @Override
        protected void onPostExecute(List<Weather> result) {
            try {
                if (result != null && !result.isEmpty()) {
                    Log.d("MainActivity", "Received weather data: " + result.size() + " items");
                    updateUI(result);
                    Log.d("MainActivity", "Weather data updated in UI");
                } else {
                    Log.e("MainActivity", "Failed to fetch or parse weather data");
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Error updating UI", e);
            }
        }
        private void updateUI(List<Weather> result) {
            runOnUiThread(() -> {
                for (int i = 0; i < weatherViews.length && i < result.size(); i++) {
                    weatherViews[i].setText(result.get(i).getFormattedDate() + ": Avg Temp " + result.get(i).getAverageTemperature() + "°F");
                }
            });
        }

    }
}
