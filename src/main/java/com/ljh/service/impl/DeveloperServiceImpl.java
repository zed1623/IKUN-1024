package com.ljh.service.impl;

import com.ljh.mapper.DeveloperMapper;
import com.ljh.pojo.entity.Developer;
import com.ljh.service.DeveloperService;
import com.ljh.utils.BaiduTranslate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeveloperServiceImpl implements DeveloperService {

    private static final String GITHUB_USER_API_URL = "https://api.github.com/users/";
    private static final String GITHUB_EVENTS_API_URL = "https://api.github.com/users/%s/events/public";
    private static final String token = "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";

    private static final String GEO_NAMES_USERNAME = "xiaoshang";
    private static final String GEO_NAMES_API_URL = "http://api.geonames.org/searchJSON?q=";

    @Autowired
    private DeveloperMapper developerMapper;
    @Autowired
    private BaiduTranslate baiduTranslate;

    @Override
    public void getDeveloper() {
        List<String> developers = developerMapper.getTop10Developers();

        for (String username : developers) {
            try {
                // 1. 获取 GitHub 用户信息
                String githubApiUrl = GITHUB_USER_API_URL + username;
                JSONObject userInfo = getGitHubUserInfo(githubApiUrl);
                if (userInfo != null) {
                    Developer developer = new Developer();
                    developer.setLogin(userInfo.optString("login", "N/A"));
                    developer.setName(userInfo.optString("name", "N/A"));
                    developer.setEmail(userInfo.optString("email", "N/A"));
                    developer.setAvatarUrl(userInfo.optString("avatar_url", "N/A"));
                    developer.setBlogUrl(userInfo.optString("blog", "N/A"));
                    developer.setProfileUrl(userInfo.optString("html_url", "N/A"));
                    developer.setBio(userInfo.optString("bio", "N/A"));

                    // 2. 获取并设置国家信息
                    String location = userInfo.optString("location", "N/A");
                    String nation = guessNationFromLocation(location);
                    developer.setNation(nation);
                    developer.setNationConfidence(!nation.equals("N/A") ? 0.9f : 0.0f);

                    // 3. 设置其他字段
                    developer.setCreatedAt(userInfo.optString("created_at"));
                    developer.setUpdatedAt(userInfo.optString("updated_at"));


                    // 如果 nation 是 "N/A"，通过时区推断大洲并更新 nation 字段
                    if (nation.equals("N/A")) {
                        String timezone = getUserTimezoneFromEvents(developer.getName());
                        String continent = guessContinentFromTimezone(timezone);  // 根据时区推断大洲
                        String s = guessNationFromLanguages(developer.getName());
                        if (!continent.equals("Unknown Timezone")) {
                            developer.setNation(continent);  // 用推测的大洲替换 N/A
                            developer.setNationConfidence(0.3f);  // 设定较低的置信度
                        }

                        if(!s.equals("N/A")) {
                            developer.setNation(s);
                            developer.setNationConfidence(0.3f);
                        }
                    }


                    // 4. 更新数据库中的开发者信息
                    developerMapper.updateDeveloperByLogin(developer);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据 GitHub API 获取用户信息
     */
    private JSONObject getGitHubUserInfo(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "token " + token);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                return new JSONObject(content.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据 location 通过 GeoNames API 获取国家名称，并将其翻译为中文
     */
    private String guessNationFromLocation(String location) {
        if (location == null || location.equals("N/A")) {
            return "N/A";
        }
        try {
            String urlString = GEO_NAMES_API_URL + location + "&maxRows=1&username=" + GEO_NAMES_USERNAME;
            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            JSONObject response = new JSONObject(content.toString());
            if (response.getJSONArray("geonames").length() > 0) {
                String countryName = response.getJSONArray("geonames").getJSONObject(0).getString("countryName");
                // 调用翻译API将国家名称翻译成中文
                return baiduTranslate.translateCountryToChinese(countryName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    /**
     * 获取用户活动事件，并推断时区
     */
    private String getUserTimezoneFromEvents(String username) {
        try {
            String eventsUrl = "https://api.github.com/users/" + username + "/events";
            URL url = new URL(eventsUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "token " + token);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();

                // 解析事件信息
                JSONArray events = new JSONArray(content.toString());
                return analyzeEventsForTimezone(events);  // 调用分析方法推测时区
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Timezone";
    }

    /**
     * 分析用户事件时间推断时区
     */
    private String analyzeEventsForTimezone(JSONArray events) {
        Map<Integer, Integer> hourFrequency = new HashMap<>();

        // 统计每个小时的事件发生次数
        for (int i = 0; i < events.length(); i++) {
            JSONObject event = events.getJSONObject(i);
            String createdAt = event.getString("created_at");
            LocalDateTime eventTime = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME);
            int hour = eventTime.getHour();

            hourFrequency.put(hour, hourFrequency.getOrDefault(hour, 0) + 1);
        }

        // 找到活动最多的小时
        int peakHour = hourFrequency.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);

        // 推测时区 (根据你的业务逻辑，比如假设用户在活动高峰期的本地时间是 9-18 点)
        if (peakHour != -1) {
            int guessedTimezoneOffset = peakHour - 12;  // 假设用户主要在 12:00 本地时间活跃
            return "UTC" + (guessedTimezoneOffset >= 0 ? "+" : "") + guessedTimezoneOffset;
        }

        return "Unknown Timezone";
    }


    /**
     * 根据时区推测大洲
     */
    private String guessContinentFromTimezone(String timezone) {
        switch (timezone) {
            case "UTC-8":
            case "UTC-7":
            case "UTC-6":
                return "北美洲";  // 北美洲
            case "UTC+4":
            case "UTC+3":
                return "亚洲";  // 亚洲（西部和中亚地区）
            case "UTC+1":
            case "UTC+2":
                return "欧洲";  // 欧洲
            case "UTC+10":
            case "UTC+11":
                return "大洋洲";  // 大洋洲
            case "UTC-3":
                return "南美洲";  // 南美洲
            default:
                return "Unknown Timezone";  // 无法推测时区
        }
    }

    private String guessNationFromLanguages(String username) {
        try {
            String reposUrl = String.format("https://api.github.com/users/%s/repos", username);
            URL url = new URL(reposUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "token " + token);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();

                // 解析项目信息，分析主语言
                JSONArray repos = new JSONArray(content.toString());
                Map<String, Integer> languageFrequency = new HashMap<>();

                for (int i = 0; i < repos.length(); i++) {
                    JSONObject repo = repos.getJSONObject(i);
                    String language = repo.optString("language", "N/A");
                    if (!language.equals("N/A")) {
                        languageFrequency.put(language, languageFrequency.getOrDefault(language, 0) + 1);
                    }
                }

                // 找出使用最多的语言
                String dominantLanguage = languageFrequency.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("N/A");

                // 根据编程语言推测国家
                return guessNationFromLanguage(dominantLanguage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    private String guessNationFromLanguage(String language) {
        switch (language.toLowerCase()) {
            case "ruby":
                return "日本";
            case "python":
            case "java":
                return "全球";
            case "go":
                return "United States";
            case "rust":
                return "United States";
            case "swift":
                return "欧洲";
            case "php":
                return "欧洲";
            default:
                return "N/A";
        }
    }


}
