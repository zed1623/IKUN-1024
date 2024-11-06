<template>
  <div class="homePage">
    <div class="projectInformation">
      <ProjectOverview class="flexItem" :data="result.data" />
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
import { ref, onMounted, computed } from "vue";
import ProjectOverview from "@/components/home/ProjectOverview.vue";
import QuickStats from "@/components/home/QuickStats.vue";
import ContributorCharts from "@/components/home/ContributorCharts.vue";
import ContributorList from "@/components/home/ContributorList.vue";
import { analyseUrl, showUrl } from "@/api/project/project";
import { useRepoStore } from "@/stores/repoStore";

const result = ref({});

const store = useRepoStore();
const repoUrl = computed(() => store.repoUrl);

async function fetchAndShowUrl(data) {
  try {
    console.log("Starting analyseUrl request...");
    // 调用 analyseUrl 请求
    console.log("data", data);

    const response = await analyseUrl(data);
    console.log("analyseUrl response received:", response);

    if (response.data.code === 200) {
      console.log("analyseUrl succeeded with status 200");

      // 调用 showUrl 请求
      const showResponse = await showUrl(data);
      console.log("showUrl response:", showResponse.data);
      result.value = showResponse.data; // 将响应数据赋值
      console.log("result:", result.value.data);
    }
  } catch (error) {
    console.error("Error fetching data:", error);
  }
}
// 在组件挂载时调用
onMounted(() => {
  const requestData = {
    url: repoUrl.value,
  };
  fetchAndShowUrl(requestData);
});
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

