<template>
  <a-layout >
   <header-view></header-view>
    <a-layout>
      <sider-view></sider-view>
      <a-layout-content
          :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
        <router-view></router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>
<script>
import HeaderView from "@/components/header";
import SiderView from "@/components/sider";
export default {
  components: {SiderView, HeaderView}
}
</script>
<style scoped>
</style>