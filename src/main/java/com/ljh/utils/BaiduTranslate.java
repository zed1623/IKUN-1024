package com.ljh.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.net.URLEncoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@Component
public class BaiduTranslate {
    // 百度翻译API的配置信息
    private static final String BAIDU_TRANSLATE_API_URL = "http://api.fanyi.baidu.com/api/trans/vip/translate";
    private static final String APP_ID = "20230427001657555"; // 百度翻译API的APP ID
    private static final String SECURITY_KEY = "4aXzUMywqBP9ur1e1zCG"; // 百度翻译API的密钥

    /**
     * 调用百度翻译 API 将国家名称翻译为中文
     */
    public String translateCountryToChinese(String country) {
        try {
            // 构造翻译API的参数
            String salt = String.valueOf(System.currentTimeMillis());
            String sign = md5(APP_ID + country + salt + SECURITY_KEY); // 签名生成
            String url = BAIDU_TRANSLATE_API_URL + "?q=" + URLEncoder.encode(country, "UTF-8")
                    + "&from=en&to=zh"
                    + "&appid=" + APP_ID
                    + "&salt=" + salt
                    + "&sign=" + sign;

            // 发送HTTP请求
            URL translateUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) translateUrl.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // 解析JSON返回结果
            JSONObject response = new JSONObject(content.toString());
            if (response.has("trans_result")) {
                JSONArray resultArray = response.getJSONArray("trans_result");
                JSONObject result = resultArray.getJSONObject(0);
                return result.getString("dst"); // 获取翻译后的国家名称
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return country;  // 出错时返回原文
    }

    /**
     * MD5加密生成签名
     */
    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
