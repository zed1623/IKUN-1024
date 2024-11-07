package com.ljh.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ljh.exception.BaseException;
import com.ljh.mapper.DeveloperMapper;
import com.ljh.mapper.ProjectMapper;
import com.ljh.pojo.dto.DeveloperPageQueryDTO;
import com.ljh.pojo.entity.Developer;
import com.ljh.pojo.entity.Project;
import com.ljh.result.PageResult;
import com.ljh.service.DeveloperService;
import com.ljh.utils.BaiduTranslate;
import com.sun.rowset.internal.Row;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class DeveloperServiceImpl implements DeveloperService {

    private static final String GITHUB_USER_API_URL = "https://api.github.com/users/";
    private static final String GITHUB_EVENTS_API_URL = "https://api.github.com/users/%s/events/public";
    private static final String GITHUB_REPOS_API_URL = "https://api.github.com/repos/%s/%s/stats/contributors";
    private static final String token = "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";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/";
    private static final String GITHUB_CONTRIBUTORS_API_URL = "https://api.github.com/repos/";


    private static final String GEO_NAMES_USERNAME = "xiaoshang";
    private static final String GEO_NAMES_API_URL = "http://api.geonames.org/searchJSON?q=";

    @Autowired
    private DeveloperMapper developerMapper;
    @Autowired
    private BaiduTranslate baiduTranslate;

    @Autowired
    private ProjectMapper projectMapper; // 项目 Mapper

    @Override
    public void getDeveloper() {
        // 获取最新的前10个开发者
        List<String> developers = developerMapper.getTop10Developers();

        // 使用 ExecutorService 来处理并发请求
        ExecutorService executorService = Executors.newFixedThreadPool(10); // 根据需要调整线程池大小
        List<Future<?>> futures = new ArrayList<>();

        // 获取项目信息（同一个项目的多个开发者，只需获取一次）
        String login = developers.get(0);
        Developer developer1 = developerMapper.seletProjectUrl(login);
        String repoUrl = developer1.getProjectUrl();
        Project project = projectMapper.getProjectsForDeveloper(repoUrl);
        String owner = project.getOwnerLogin();
        String repo = project.getName();

        for (String username : developers) {
            futures.add(executorService.submit(() -> {
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

                        // 设置 TalentRank
                        developer.setTalentRank(calculateTalentRank(username));

                        // 设置国家信息并缓存结果
                        String location = userInfo.optString("location", "N/A");
                        String nation = guessNationFromLocation(location);
                        developer.setNation(nation);
                        developer.setNationConfidence(!nation.equals("N/A") ? 0.9f : 0.0f);

                        developer.setCreatedAt(userInfo.optString("created_at"));
                        developer.setUpdatedAt(userInfo.optString("updated_at"));

                        // 获取代码统计信息
                        int[] codeStats = getCodeStatsForUser(owner, repo, username);
                        developer.setTotalAdditions(codeStats[0]);
                        developer.setTotalDeletions(codeStats[1]);

                        // 设置提交次数信息
                        Developer info = getGitHubContributorsInfo(owner, repo, username);
                        developer.setNumber(info.getNumber());

                        // 更新国家信息
                        if (nation.equals("N/A")) {
                            String timezone = getUserTimezoneFromEvents(developer.getName());
                            String continent = guessContinentFromTimezone(timezone);
                            if (!continent.equals("Unknown Timezone")) {
                                developer.setNation(continent);
                                developer.setNationConfidence(0.3f);
                            }
                        }

                        // 获取开发者领域信息
                        developer.setField(analyzeDeveloperField(developer.getName()));

                        // 批量更新数据库
                        developerMapper.updateDeveloperByLogin(developer);
                    }

                } catch (Exception e) {
                    System.out.println("Error processing developer " + username + ": " + e.getMessage());
                }
            }));
        }

        // 等待所有任务完成
        futures.forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // 关闭线程池
        executorService.shutdown();
    }

    /**
     * 根据用户名查找用户信息
     *
     * @param login 用户的登录名
     * @return
     */
    @Override
    public Developer getLoginDeveloper(String login) {
        try {
            return developerMapper.findDeveloperByLogin(login); // 调用MyBatis映射的方法
        } catch (Exception e) {
            throw new BaseException("根据用户名查找用户信息失败:" + e.getMessage());
        }
    }

    /**
     * 用户分页查询
     *
     * @param developerPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DeveloperPageQueryDTO developerPageQueryDTO) {
        PageHelper.startPage(developerPageQueryDTO.getPage(),developerPageQueryDTO.getPageSize());
        //下一条sql进行分页，自动加入limit关键字分页
        Page<Developer> page = developerMapper.pageQuery(developerPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 导出用户信息为excel表格
     *
     * @param response
     */
    @Override
    public void exportDevelopersToExcel(HttpServletResponse response) {
        try {
            // 获取用户数据
            List<Developer> developers = developerMapper.getAllDevelopers();

            // 创建 Excel 工作簿和 Sheet
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Developers");

            // 创建表头
            String[] headers = {"开发者全名", "GitHub用户名", "电子邮箱", "项目url", "博客地址", "地区", "技术能力评价分数"};
            XSSFFont font = workbook.createFont();
            XSSFCellStyle headerStyle = workbook.createCellStyle();
            font.setBold(true); // 设置字体加粗
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                // 可选择添加单元格样式，例如加粗、居中等
                cell.setCellStyle(headerStyle);
            }

            // 填充数据
            int rowNum = 1;
            for (Developer developer : developers) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(developer.getLogin());
                row.createCell(1).setCellValue(developer.getName());
                row.createCell(2).setCellValue((developer.getEmail()));
                row.createCell(3).setCellValue(developer.getProfileUrl());
                row.createCell(4).setCellValue(developer.getBio());
                row.createCell(5).setCellValue(developer.getNation());
//                row.createCell(6).setCellValue(developer.getTalentRank());
            }
            // 设置 HTTP 响应
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=developers.xlsx");

            // 输出流写入响应
//            try (OutputStream outputStream = response.getOutputStream()) {
//                workbook.write(outputStream);
//            }
           FileOutputStream out =   new FileOutputStream(new File("D:\\Developers.xlsx"));
            workbook.write(out);
            out.close();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除所有用户信息
     * @param projectUrl
     */
    @Override
    public void deleteDeveloper(String projectUrl) {
        if(projectUrl.isEmpty()){
            throw  new BaseException("projectUrl为空");
        }
        developerMapper.deleteDeveloper(projectUrl); // 执行删除所有用户信息的数据库操作
    }

    /**
     * 根据nation查询用户信息
     *
     * @param nation
     * @return
     */
    @Override
    public List<Developer> getDevelopersByNation(String nation) {
        if(nation.isEmpty()){
            throw new BaseException("nation为空");
        }
        List<Developer> developerList = developerMapper.findDevelopersByNation(nation);
        return developerList;
    }

    /**
     * 根据领域搜索开发者并按 TalentRank 排序，可选使用 Nation 作为筛选条件
     *
     * @param field
     * @param nation
     * @return
     */
    @Override
    public List<Developer> searchDevelopersByFieldAndNation(String field, String nation) {
        List<Developer> developerList = developerMapper.findDevelopersByFieldAndNation(field, nation);
        return developerList;
    }

    /**
     * 根据项目地址查询用户信息
     *
     * @param projectUrl
     * @return
     */
    @Override
    public List<Developer> getUrlDeveloper(String projectUrl) {
        List<Developer> developerList = developerMapper.selectUrlDeveloper(projectUrl);
        return developerList;
    }

    /**
     * 根据 GitHub API 获取用户信息
     * @param urlString
     * @return
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
            throw new BaseException("根据 GitHub API 获取用户信息失败："+ e.getMessage());
        }
        return null;
    }

    /**
     * 分析贡献者的活跃度
     *
     * @param username 用户名
     * @return 活跃度分析结果
     */
    public Map<String, Object> analyzeContributorActivity(String username) {
        Map<String, Object> activityReport = new HashMap<>();
        try {
            String eventsUrl = String.format(GITHUB_EVENTS_API_URL, username);
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
                Map<Integer, Integer> hourFrequency = new HashMap<>();
                int totalEvents = events.length();
                LocalDateTime latestEventTime = null;
                int commitEvents = 0;
                int totalCommits = 0;

                for (int i = 0; i < events.length(); i++) {
                    JSONObject event = events.getJSONObject(i);
                    String type = event.getString("type");
//                    System.out.println("Event type: " + type);

                    if (type.equals("PushEvent")) {
                        commitEvents++;
                        // 提取 PushEvent 中的提交数量
                        JSONObject payload = event.getJSONObject("payload");
                        if (payload.has("commits")) {
                            JSONArray commitsArray = payload.getJSONArray("commits");
                            totalCommits += commitsArray.length(); // 统计提交数量
                        }
                    }

                    String createdAt = event.getString("created_at");
                    LocalDateTime eventTime = LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME);
                    int hour = eventTime.getHour();
                    hourFrequency.put(hour, hourFrequency.getOrDefault(hour, 0) + 1);

                    if (latestEventTime == null || eventTime.isAfter(latestEventTime)) {
                        latestEventTime = eventTime;
                    }
                }

                // 计算活跃时间
                int peakHour = hourFrequency.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(-1);

                activityReport.put("总事件数：", totalEvents);
                activityReport.put("提交事件数：", commitEvents);
                activityReport.put("添加总提交数：", totalCommits); // 添加总提交数
                activityReport.put("高峰时段：", peakHour);
                activityReport.put("最新活动时间：", latestEventTime);
                activityReport.put("每小时活动分布：", hourFrequency);

                System.out.println("活跃度分析结果: " + activityReport);
            } else {
                System.out.println("获取贡献者活动信息失败，HTTP 响应码：" + responseCode);
                connection.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return activityReport;
    }

    /**
     * 根据 location 通过 GeoNames API 获取国家名称，并将其翻译为中文
     * @param location
     * @return
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
     * @param username
     * @return
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
            throw new BaseException("获取指定用户活动事件失败：" + e.getMessage());
        }
        return "Unknown Timezone";
    }

    /**
     * 分析用户事件时间推断时区
     * @param events
     * @return
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
     * @param timezone
     * @return
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

    /**
     * 根据编程语言推测国家
     * @param username
     * @return
     */
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
            throw new BaseException("根据编程语言推测国家失败：" + e.getMessage());
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

    /**
     * 根据 GitHub 用户名分析开发者的技术领域
     * @param username GitHub 用户名
     * @return 推测的技术领域
     */
    public String analyzeDeveloperField(String username) {
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

                // 解析项目信息，分析主语言和项目描述
                JSONArray repos = new JSONArray(content.toString());
                Map<String, Integer> languageFrequency = new HashMap<>();
                Map<String, Integer> fieldFrequency = new HashMap<>();

                for (int i = 0; i < repos.length(); i++) {
                    JSONObject repo = repos.getJSONObject(i);
                    String language = repo.optString("language", "N/A");
                    String description = repo.optString("description", "").toLowerCase();

                    // 统计编程语言频率
                    if (!language.equals("N/A")) {
                        languageFrequency.put(language, languageFrequency.getOrDefault(language, 0) + 1);
                    }

                    // 基于项目描述推测领域
                    String fieldFromDescription = guessFieldFromDescription(description);
                    if (!fieldFromDescription.equals("未知领域")) {
                        fieldFrequency.put(fieldFromDescription, fieldFrequency.getOrDefault(fieldFromDescription, 0) + 1);
                    }
                }

                // 找出使用最多的语言
                String dominantLanguage = languageFrequency.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("N/A");

                // 根据编程语言推测技术领域
                String fieldFromLanguage = guessFieldFromLanguage(dominantLanguage);

                // 找出频率最高的领域
                String mostCommonField = fieldFrequency.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("未知领域");

                // 返回更准确的领域，优先级：项目描述 > 编程语言
                return !mostCommonField.equals("未知领域") ? mostCommonField : fieldFromLanguage;

            }
        } catch (Exception e) {
            throw new BaseException("分析开发者技术领域失败：" + e.getMessage());
        }
        return "未知领域";
    }

    /**
     * 根据项目描述推测技术领域
     * @param description 项目描述
     * @return 技术领域
     */
    private String guessFieldFromDescription(String description) {
        if (description.contains("web") || description.contains("frontend") || description.contains("website")) {
            return "Web 开发";
        } else if (description.contains("data") || description.contains("ml") || description.contains("ai")) {
            return "数据科学/机器学习";
        } else if (description.contains("app") || description.contains("android") || description.contains("ios")) {
            return "移动应用开发";
        } else if (description.contains("game") || description.contains("unity") || description.contains("unreal")) {
            return "游戏开发";
        } else if (description.contains("embedded") || description.contains("firmware") || description.contains("system")) {
            return "系统开发/嵌入式开发";
        }
        return "未知领域";
    }

    /**
     * 根据编程语言推测技术领域
     * @param language 编程语言
     * @return 技术领域
     */
    private String guessFieldFromLanguage(String language) {
        switch (language.toLowerCase()) {
            case "javascript":
            case "html":
            case "css":
                return "Web 开发";
            case "python":
                return "数据科学/机器学习";
            case "java":
                return "企业级应用/Android 开发";
            case "c#":
                return "游戏开发/企业应用";
            case "swift":
                return "iOS 开发";
            case "kotlin":
                return "Android 开发";
            case "r":
                return "数据分析";
            case "php":
                return "Web 开发";
            case "c++":
            case "c":
                return "系统开发/嵌入式开发";
            case "ruby":
                return "Web 开发";
            case "go":
                return "云计算/后端开发";
            case "typescript":
                return "前端/全栈开发";
            default:
                return "通用开发";
        }
    }



    /**
     * 计算开发者的 TalentRank
     * @param username
     * @return
     */
    private float calculateTalentRank(String username) {
        float talentRank = 0.0f;
        String url = developerMapper.findContributorToDatabase(username);
        Project project = projectMapper.getProjectsForDeveloper(url); // 获取该开发者参与的项目

        double importanceScore = project.getImportanceScore();
        int contributionScore = getContributionScore(username, project.getName(), project.getOwnerLogin());
        System.out.println("contribution score: " + contributionScore);

        // 使用公式计算 TalentRank
        double rawScore = importanceScore * contributionScore;

        // 假设我们使用一个固定的最大值进行缩放，例如 maxScore = 1000.0；
        // 如果你有动态的最大值，可以通过分析历史数据或全局计算得到。
        double maxScore = 1000.0;  // 根据数据情况调整此值

        // 将 rawScore 缩放到 1 到 100 的范围
        if (rawScore > 0) {
            talentRank = (float) ((rawScore / maxScore) * 100);
        }

        // 确保结果在 1 到 100 之间，最小值为 1
        talentRank = Math.max(1.0f, Math.min(100.0f, talentRank));

        return talentRank;
    }


    /**
     * 获取开发者对指定项目的贡献分数
     * @param username
     * @param repoName
     * @param owner
     * @return
     */
    private int getContributionScore(String username, String repoName, String owner) {
        int contributions = 0;

        try {
            String repoUrl = String.format(GITHUB_REPOS_API_URL, owner, repoName);
            URL url = new URL(repoUrl);

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

                // 获取贡献信息
                JSONArray contributors = new JSONArray(content.toString());
                for (int i = 0; i < contributors.length(); i++) {
                    JSONObject contributor = contributors.getJSONObject(i);
                    if (contributor.getJSONObject("author").getString("login").equals(username)) {
                        contributions = contributor.getInt("total");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new BaseException("获取开发者对指定项目的贡献分数：" + e.getMessage());
        }

        return contributions;
    }

    /**
     * 获取指定用户对指定项目的代码总量
     * @param owner
     * @param repo
     * @param username
     * @return
     */
    public int[] getCodeStatsForUser(String owner, String repo, String username) {
        int totalAdditions = 0;
        int totalDeletions = 0;
        try {
            String commitsUrl = GITHUB_API_URL + owner + "/" + repo + "/commits?author=" + username;
            URL url = new URL(commitsUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
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

                JSONArray commits = new JSONArray(content.toString());

                for (int i = 0; i < commits.length(); i++) {
                    String sha = commits.getJSONObject(i).getString("sha");
                    String commitDetailsUrl = GITHUB_API_URL + owner + "/" + repo + "/commits/" + sha;

                    URL detailsUrl = new URL(commitDetailsUrl);
                    HttpURLConnection detailsConnection = (HttpURLConnection) detailsUrl.openConnection();
                    detailsConnection.setRequestMethod("GET");
                    detailsConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                    detailsConnection.setRequestProperty("Authorization", "token " + token);

                    if (detailsConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader detailsIn = new BufferedReader(new InputStreamReader(detailsConnection.getInputStream()));
                        StringBuilder detailsContent = new StringBuilder();
                        String detailsLine;

                        while ((detailsLine = detailsIn.readLine()) != null) {
                            detailsContent.append(detailsLine);
                        }
                        detailsIn.close();

                        JSONObject commitDetails = new JSONObject(detailsContent.toString());
                        if (commitDetails.has("stats")) {
                            JSONObject stats = commitDetails.getJSONObject("stats");
                            totalAdditions += stats.getInt("additions");
                            totalDeletions += stats.getInt("deletions");
                        }
                    }
                    detailsConnection.disconnect();
                }
            } else {
                System.out.println("Failed to get commit records. HTTP Response Code: " + responseCode);
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new int[]{totalAdditions, totalDeletions};
    }



    /**
     * 获取指定用户对该项目的贡献次数
     * @param owner 项目所有者
     * @param repo 项目名称
     * @param username 指定用户的用户名
     * @return 指定用户的贡献信息
     */
    public Developer getGitHubContributorsInfo(String owner, String repo, String username) {
        Developer userContribution = new Developer();
        try {
            String urlString = GITHUB_CONTRIBUTORS_API_URL + owner + "/" + repo + "/contributors";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestProperty("Authorization", "token " + token);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();

                JSONArray contributorsArray = new JSONArray(content.toString());
                for (int i = 0; i < contributorsArray.length(); i++) {
                    JSONObject contributor = contributorsArray.getJSONObject(i);
                    if (contributor.getString("login").equals(username)) {
                        System.out.println("用户名: " + contributor.getString("login"));
                        System.out.println("用户 ID: " + contributor.getInt("id"));
                        System.out.println("贡献次数: " + contributor.getInt("contributions"));
                        System.out.println("GitHub 主页: " + contributor.getString("html_url"));
                        System.out.println("头像链接: " + contributor.getString("avatar_url"));
                        System.out.println("-----------------------------");

                        // 将指定用户的信息添加到返回的 Developer 对象中
                        userContribution.setLogin(contributor.getString("login"));
                        userContribution.setId(contributor.getInt("id"));
                        userContribution.setNumber(contributor.getInt("contributions"));
                        userContribution.setProfileUrl(contributor.getString("html_url"));
                        userContribution.setAvatarUrl(contributor.getString("avatar_url"));
                        break;
                    }
                }
            } else {
                System.out.println("Failed to get contributors info. HTTP Response Code: " + responseCode);
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userContribution;
    }


}
