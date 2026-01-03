package com.example.daily.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    @Cacheable(value = "weather", key = "'todaySeoul'")
    public String getTodayWeather() {
        log.info("📢 외부 날씨 API 호출 중...");

        String url = "https://api.openweathermap.org/data/2.5/weather?q=Seoul&appid=" + apiKey + "&units=metric";

        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONObject jsonObj = new JSONObject(response);
            return jsonObj.getJSONArray("weather").getJSONObject(0).getString("main");
        } catch (Exception e) {
            log.error("❌ 날씨 API 호출 실패: {}", e.getMessage());
            return "Unknown";
        }
    }
}
