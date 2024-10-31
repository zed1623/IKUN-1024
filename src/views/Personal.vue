<template>
  <div class="contributor-details-page">
    <!-- 个人信息区 -->
    <el-card class="personal-info" shadow="hover">
      <img src="" alt="头像" class="avatar" />
      <h2>用户名</h2>
      <p>简介信息</p>
      <p>国家 | 公司 | 加入时间</p>
    </el-card>

    <!-- 贡献统计概览 -->
    <el-card class="stats-overview" shadow="hover">
      <div class="flex">
        <div class="stat-card">总提交次数: 120</div>
        <div class="stat-card">代码行数增量: 3400</div>
        <div class="stat-card">代码行数减量: 2200</div>
        <div class="stat-card">修改文件数: 80</div>
      </div>
    </el-card>

    <!-- 提交记录列表 -->
    <el-card class="commit-records" shadow="hover">
      <h2>提交记录</h2>
      <ul>
        <li>提交标题 - 提交时间 - 提交哈希值 - 代码行增删数</li>
        <!-- 其他记录 -->
      </ul>
    </el-card>

    <!-- 活动时间线 -->
    <el-card class="activity-timeline" shadow="hover">
      <h2>活跃时间线</h2>
      <!-- 图表：提交频率、活跃周期 -->
    </el-card>

    <!-- 代码变更分析 -->
    <el-card class="code-change-analysis" shadow="hover">
      <h2>代码变更分析</h2>
      <!-- 图表：增删行数、修改文件分布 -->
    </el-card>

    <!-- 筛选和导出按钮 -->
    <el-card class="interactive-tools" shadow="hover">
      <div class="button-group">
        <el-button type="primary">日期筛选</el-button>
        <el-button type="primary">文件类型筛选</el-button>
        <el-button type="primary">导出数据</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { ElButton, ElCard } from "element-plus";

// 定义响应式数据
const message = ref("个人信息");
</script>

<style scoped lang="scss">
.contributor-details-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 900px;
  margin: 0 auto;
  font-family: Arial, sans-serif;

  .el-card {
    padding: 15px;
    border-radius: 8px;
  }

  .personal-info {
    display: flex;
    align-items: center;
    gap: 15px;

    .avatar {
      width: 60px;
      height: 60px;
      border-radius: 50%;
    }
  }

  .stats-overview {
    .flex {
      display: flex;
      gap: 10px;
    }
    .stat-card {
      flex: 1;
      background-color: #e0f7fa;
      padding: 10px;
      text-align: center;
      border-radius: 5px;
      font-weight: bold;
    }
  }

  .commit-records,
  .activity-timeline,
  .code-change-analysis {
    background-color: #f7f7f7;
    padding: 15px;
    border-radius: 5px;

    h2 {
      font-size: 1.2em;
      color: #333;
    }
  }

  .interactive-tools {
    display: flex;
    gap: 10px;
    justify-content: flex-start;

    .el-button {
      padding: 8px 15px;
    }
  }
}
</style>
