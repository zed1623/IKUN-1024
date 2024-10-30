<template>
  <div class="header">
    <div class="logo">
      <h1>DARPC</h1>
    </div>
    <ul class="nav-links">
      <li><router-link to="/">首页</router-link></li>
      <li><router-link to="/about">项目分析</router-link></li>
      <li><router-link to="/about">个人信息</router-link></li>
      <li><router-link to="/about">成员位置</router-link></li>
      <!-- <li>个人分析</li>
      <li>成员位置</li> -->
      <el-input
        v-model="input"
        style="width: 600px"
        placeholder="请输入仓库地址"
        :prefix-icon="Search"
      />
      <!-- <li><router-link to="/contact">Contact</router-link></li> -->
    </ul>
  </div>
</template>

<script setup>
import { Search } from "@element-plus/icons-vue";
import { ref } from "vue";
const input = ref("");
</script>

<style scoped lang="scss">
.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 60px;
  background: linear-gradient(
    to right,
    rgba(161, 71, 118, 1),
    rgba(51, 51, 51, 0)
  );
  color: white;
  padding: 10px 20px;
  margin: 0;
  display: flex;
  align-items: center;
  z-index: 999;
  .logo {
    h1 {
      margin: 0;
    }
  }

  .nav-links {
    list-style: none;
    display: flex;
    gap: 20px;

    li {
      display: inline;

      a {
        color: white;
        text-decoration: none;
        font-weight: bold;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
}
</style>
