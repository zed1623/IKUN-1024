<template>
  <div class="main">
    <el-card style="width: 100%; height: 100%" shadow="hover">
      <div class="topic">开发者贡献</div>
      <div class="content">
        <div ref="chart1" class="percent"></div>
        <div ref="chart2" class="percent"></div>
        <div ref="chart3" class="percent"></div></div
    ></el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import * as echarts from "echarts";

const chart1 = ref(null);
const chart2 = ref(null);
const chart3 = ref(null);
onMounted(() => {
  const myChart1 = echarts.init(chart1.value);
  const myChart2 = echarts.init(chart2.value);
  const myChart3 = echarts.init(chart3.value);

  const option1 = {
    backgroundColor: "#2c343c",
    title: {
      text: "开发者贡献度",
      left: "center",
      top: 20,
      textStyle: {
        color: "#ccc",
      },
    },
    tooltip: {
      trigger: "item",
    },
    visualMap: {
      show: false,
      min: 80,
      max: 600,
      inRange: {
        colorLightness: [0, 1],
      },
    },
    series: [
      {
        name: "Access From",
        type: "pie",
        radius: "55%",
        center: ["50%", "50%"],
        data: [
          { value: 335, name: "user1" },
          { value: 310, name: "user2" },
          { value: 274, name: "user3" },
          { value: 235, name: "user4" },
          { value: 400, name: "user5" },
        ].sort(function (a, b) {
          return a.value - b.value;
        }),
        roseType: "radius",
        label: {
          color: "rgba(255, 255, 255, 0.3)",
        },
        labelLine: {
          lineStyle: {
            color: "rgba(255, 255, 255, 0.3)",
          },
          smooth: 0.2,
          length: 10,
          length2: 20,
        },
        itemStyle: {
          color: "#c23531",
          shadowBlur: 200,
          shadowColor: "rgba(0, 0, 0, 0.5)",
        },
        animationType: "scale",
        animationEasing: "elasticOut",
        animationDelay: function (idx) {
          return Math.random() * 200;
        },
      },
    ],
  };
  const option2 = {
    backgroundColor: "#2c343c",
    title: {
      text: "开发者贡献度",
      left: "center",
      top: 20,
      textStyle: {
        color: "#ccc",
      },
    },
    tooltip: {
      trigger: "item",
    },
    visualMap: {
      show: false,
      min: 80,
      max: 600,
      inRange: {
        colorLightness: [0, 1],
      },
    },
    series: [
      {
        name: "Access From",
        type: "pie",
        radius: "55%",
        center: ["50%", "50%"],
        data: [
          { value: 335, name: "user1" },
          { value: 310, name: "user2" },
          { value: 274, name: "user3" },
          { value: 235, name: "user4" },
          { value: 400, name: "user5" },
        ].sort(function (a, b) {
          return a.value - b.value;
        }),
        roseType: "radius",
        label: {
          color: "rgba(255, 255, 255, 0.3)",
        },
        labelLine: {
          lineStyle: {
            color: "rgba(255, 255, 255, 0.3)",
          },
          smooth: 0.2,
          length: 10,
          length2: 20,
        },
        itemStyle: {
          color: "#c23531",
          shadowBlur: 200,
          shadowColor: "rgba(0, 0, 0, 0.5)",
        },
        animationType: "scale",
        animationEasing: "elasticOut",
        animationDelay: function (idx) {
          return Math.random() * 200;
        },
      },
    ],
  };
  const option3 = {
    backgroundColor: "#2c343c",
    title: {
      text: "开发者贡献度",
      left: "center",
      top: 20,
      textStyle: {
        color: "#ccc",
      },
    },
    tooltip: {
      trigger: "item",
    },
    visualMap: {
      show: false,
      min: 80,
      max: 600,
      inRange: {
        colorLightness: [0, 1],
      },
    },
    series: [
      {
        name: "Access From",
        type: "pie",
        radius: "55%",
        center: ["50%", "50%"],
        data: [
          { value: 335, name: "user1" },
          { value: 310, name: "user2" },
          { value: 274, name: "user3" },
          { value: 235, name: "user4" },
          { value: 400, name: "user5" },
        ].sort(function (a, b) {
          return a.value - b.value;
        }),
        roseType: "radius",
        label: {
          color: "rgba(255, 255, 255, 0.3)",
        },
        labelLine: {
          lineStyle: {
            color: "rgba(255, 255, 255, 0.3)",
          },
          smooth: 0.2,
          length: 10,
          length2: 20,
        },
        itemStyle: {
          color: "#c23531",
          shadowBlur: 200,
          shadowColor: "rgba(0, 0, 0, 0.5)",
        },
        animationType: "scale",
        animationEasing: "elasticOut",
        animationDelay: function (idx) {
          return Math.random() * 200;
        },
      },
    ],
  };
  myChart1.setOption(option1);
  myChart2.setOption(option2);
  myChart3.setOption(option3);
});
</script>

<style scoped lang="scss">
/* 在这里添加你的样式 */
.main {
  .topic {
    text-align: center;
    font-size: 23px;
    background-color: antiquewhite;
  }
  .content {
    display: flex;
    align-items: center;
    justify-content: space-around;
    padding: 30px;
    .percent {
      width: 400px;
      height: 400px;
    }
  }
  ::v-deep .el-card {
    --el-card-padding: 8px;
  }
  height: 500px;

  margin-top: 10px;
}
</style>