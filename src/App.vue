<template><Header /> <router-view /></template>
<script setup>
import Header from "./components/Header.vue";
</script>
<style scoped lang = "scss">
header,
router-view {
  display: block;
}
</style>