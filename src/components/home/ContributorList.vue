<template>
  <div class="main">
    <el-card style="width: 100%; height: 100%" shadow="hover">
      <div class="topic">开发者信息</div>
      <div class="content">
        <el-table
          :data="tableData"
          :default-sort="{ prop: 'TalentRank', order: 'descending' }"
          stripe
          style="width: 100%"
        >
          <el-table-column prop="name" label="用户名" width="180" />
          <el-table-column
            prop="TalentRank"
            label="TalentRank"
            sortable
            width="180"
          />
          <el-table-column prop="name" label="提交次数" width="180" />
          <el-table-column prop="name" label="代码提交量" width="180" />
          <el-table-column prop="name" label="最近一次提交时间" width="180" />
          <el-table-column prop="address" label="位置" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from "vue";

const tableData = [
  {
    TalentRank: "2016-05-03",
    name: "Tom",
    address: "No. 189, Grove St, Los Angeles",
  },
  {
    TalentRank: "2016-05-02",
    name: "Tom",
    address: "No. 189, Grove St, Los Angeles",
  },
  {
    TalentRank: "2016-05-04",
    name: "Tom",
    address: "No. 189, Grove St, Los Angeles",
  },
  {
    TalentRank: "2016-05-01",
    name: "Tom",
    address: "No. 189, Grove St, Los Angeles",
  },
];
</script>

<style scoped lang = "scss">
/* 在这里添加你的样式 */
.main {
  height: 500px;

  margin-top: 10px;
  .topic {
    text-align: center;
    font-size: 23px;
    background-color: antiquewhite;
  }
  ::v-deep .el-card {
    --el-card-padding: 8px;
  }
  .content {
    padding: 10px;
  }
}
h1 {
  color: #42b983;
}
</style>