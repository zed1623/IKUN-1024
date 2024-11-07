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
          <el-table-column prop="login" label="账号" width="180" />
          <el-table-column
            prop="talentRank"
            label="TalentRank"
            sortable
            width="180"
          />
          <el-table-column prop="number" label="提交次数" width="180" />
          <el-table-column
            prop="totalAdditions"
            label="代码提交量"
            width="180"
          />
          <el-table-column
            prop="updatedAt"
            label="最近一次提交时间"
            width="180"
          />
          <el-table-column prop="nation" label="地区" />
          <el-table-column prop="nation" label="地区置信度" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { defineProps, ref, watch } from "vue";

const props = defineProps({
  data: {
    type: [Object, Array],
    required: true,
  },
});

const tableData = ref([]);

// 监听 props.data 的变化并更新 tableData
watch(
  () => props.data,
  (newValue) => {
    if (newValue && newValue.length > 0) {
      console.log("data has been updated in child component:", newValue);
      // 在这里处理数据更新后的逻辑
      tableData.value = newValue;
    }
  },
  { immediate: true, deep: true }
);
</script>

<style scoped lang = "scss">
/* 在这里添加你的样式 */
.main {
  height: 500px;

  margin-top: 10px;
  .topic {
    text-align: center;
    font-size: 23px;
    /* background-color: antiquewhite; */
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