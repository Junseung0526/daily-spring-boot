package com.example.daily.service;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    @Value("${weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getTodayWeather() {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=Seoul&appid=" + apiKey + "&units=metric";

        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject jsonObj = new JSONObject(response);

            return jsonObj.getJSONArray("weather").getJSONObject(0).getString("main");
        } catch (Exception e) {
            // API 호출 실패 시 기본값
            return "Unknown";
        }
    }

}
