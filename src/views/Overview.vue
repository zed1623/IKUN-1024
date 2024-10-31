<template>
  <div class="homePage">
    <div class="projectInformation">
      <ProjectOverview class="flexItem" />
      <!-- 项目概览区组件 -->
      <QuickStats class="flexItem" />
      <!-- 快速统计组件 -->
    </div>

    <ContributorCharts />
    <!-- 贡献者分析图表组件 -->
    <ContributorList />
    <!-- 贡献者列表组件 -->
  </div>
</template>

<script setup>
import { ref } from "vue";
import ProjectOverview from "@/components/home/ProjectOverview.vue";
import QuickStats from "@/components/home/QuickStats.vue";
import ContributorCharts from "@/components/home/ContributorCharts.vue";
import ContributorList from "@/components/home/ContributorList.vue";

// 定义响应式数据
const message = ref("Hello, Vue 3!");

// 在这里编写你的 Composition API 代码
</script>

<style scoped lang = "scss">
.homePage {
  padding: 10px;
  .projectInformation {
    display: flex;
    gap: 20px;
    justify-content: space-between; // 子元素之间均匀分布
    align-items: center; // 垂直方向居中对齐

    .flexItem {
      flex: 1;
      height: 400px;
    }
  }
}
</style>