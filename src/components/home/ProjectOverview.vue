<template>
  <div>
    <el-card style="width: 100%; height: 100%" shadow="hover"
      ><div class="topic">项目总览</div>
      <div class="content">
        <div class="basic">
          <div class="show">
            <div class="name">项目名称：</div>
            <div class="data">XxxxxxxXxxX</div>
          </div>
          <div class="show">
            <div class="name">简介：</div>
            <div class="data">XxxxxxxXxxX</div>
          </div>
          <div class="show">
            <div class="name">star：</div>
            <div class="data">XxxxxxxXxxX</div>
          </div>
          <div class="show">
            <div class="name">forks：</div>
            <div class="data">XxxxxxxXxxX</div>
          </div>
          <div class="show">
            <div class="name">issues：</div>
            <div class="data">XxxxxxxXxxX</div>
          </div>
        </div>
        <div class="summary">
          <div class="show">
            <div class="name">开发者数量:</div>
            <div class="data">XxxxxxxXxxX</div>
          </div>
          <div class="show">
            <div class="name">总提交次数：</div>
            <div class="data">XxxxxxxXxxX</div>
          </div>
          <div class="show">
            <div class="name">总代码行数：</div>
            <div class="data">XxxxxxxXxxX</div>
          </div>
          <div class="show">
            <div class="name">最近更新：</div>
            <div class="data">XxxxxxxXxxX</div>
          </div>
        </div>
      </div></el-card
    >
  </div>
</template>

<script setup>
import { ref } from "vue";
</script>

<style scoped lang = "scss">
.topic {
  text-align: center;
  font-size: 23px;
  background-color: antiquewhite;
}
.content {
  .basic,
  .summary {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    padding: 10px;
  }
}
.show {
  /* background-color: deeppink; */
  width: 360px;
  height: 36px;
  display: flex;
  gap: 6px;
  .name {
    line-height: 36px;
    color: rgb(87, 21, 122);
  }
  .data {
    line-height: 36px;
    color: #4a4a8b;
  }
}
::v-deep .el-card {
  --el-card-padding: 8px;
}
</style>