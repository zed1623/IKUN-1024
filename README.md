# Gittalent Backend

**项目简介**
Gittalent 是一个用于分析 GitHub 项目及其贡献者的后端系统，旨在评估项目的重要性以及开发者的技术能力。系统通过集成 GitHub API，提供一系列丰富的 API 接口，用于获取、存储和分析数据，生成详细的项目和开发者报告。

**主要功能**

- 获取并存储 GitHub 项目数据
- 获取并存储 GitHub 开发者数据
- 根据国家筛选和筛选开发者，并根据技术能力（TalentRank）排序
- 计算并评估开发者的 TalentRank（技术能力评分）
- 导出开发者数据到 Excel 文件
- 分析开发者的贡献活动和活跃度

**技术栈**

- **语言**: Java
- **框架**: Spring Boot, MyBatis
- **数据库**: MySQL
- **其他库**: Apache POI (用于操作 Excel), GitHub API 集成
- **工具**: Maven, Git

**项目结构**

```
src
├── main
│   ├── java
│   │   └── com.ljh
│   │       ├── config          # 配置类
│   │       ├── controller      # 控制器层，处理 API 请求
│   │       ├── service         # 服务层接口和实现类，封装业务逻辑
│   │       ├── mapper          # MyBatis Mapper 接口，负责数据库操作
│   │       ├── pojo            # 实体类和数据传输对象 (DTO)
│   │       ├── handler         # 全局异常处理类
│   │       └── utils           # 工具类
│   └── resources
│       ├── mapper              # MyBatis 映射文件，定义 SQL 查询和映射
│       ├── application.yml     # Spring Boot 应用程序配置文件
```

**联系方式**

- **作者**: 小殇
- **Email**: 2710581308@qq.com
- **GitHub**: [xiaoshang-1](https://github.com/xiaoshang-1)

如需帮助或更多信息，请随时联系作者。

