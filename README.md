# Gittalent Backend

**项目简介**
Gittalent 是一个用于分析 GitHub 项目及其贡献者的后端系统，旨在评估项目的重要性及开发者的技术能力。该项目提供 API 接口来获取、存储、分析 GitHub 数据，并生成详细的分析报告。

**主要功能**
获取并存储 GitHub 项目数据
获取并存储 GitHub 开发者数据
根据国家筛选开发者
计算开发者的 TalentRank（技术能力评价分数）
导出开发者数据到 Excel 文件
分析开发者的贡献和活跃度

**技术栈**
语言: Java
框架: Spring Boot, MyBatis
数据库: MySQL
其他库: Apache POI (Excel 操作), GitHub API 集成
工具: Maven, Git

**项目结构**
csharp

```
src
├── main
│   ├── java
│   │   └── com.ljh
|   |       ├── config          # 配置类
│   │       ├── controller      # 控制器层
│   │       ├── service         # 服务层接口和实现
│   │       ├── mapper          # MyBatis Mapper 接口
│   │       ├── pojo            # 实体类和 DTO
│   │       ├── handler         # 全局异常处理类
|   |       └── utils           # 工具类
│   └── resources
│       ├── mapper              # MyBatis 映射文件
│       ├── application.yml     # 配置文件
```

联系方式
作者: 小殇
Email: chainljh@gmail.com
GitHub: xiaoshang-1
