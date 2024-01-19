package com.example.my_application;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Weather {
    private Date date;
    private double averageTemperature;
    private double averageHumidity;

    private double totalRain;
    public Weather(Date date, double averageTemperature, double averageHumidity, double totalRain) {
        this.date = date;
        this.averageTemperature = averageTemperature;
        this.averageHumidity = averageHumidity;

        this.totalRain = totalRain;
    }

    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(date);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }

    public double getAverageHumidity() {
        return averageHumidity;
    }


    public double getTotalRain() {
        return totalRain;
    }
    public void setAverageTemperature(double averageTemperature) {
        this.averageTemperature = averageTemperature;
    }
}
