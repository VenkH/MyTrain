<template>
  <a-layout >
   <header-view></header-view>
    <a-layout>
      <sider-view></sider-view>
      <a-layout style="padding: 0 24px 24px">
        <a-breadcrumb style="margin: 16px 0">
          <a-breadcrumb-item>Home</a-breadcrumb-item>
          <a-breadcrumb-item>List</a-breadcrumb-item>
          <a-breadcrumb-item>App</a-breadcrumb-item>
        </a-breadcrumb>
        <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
        >
          Content
        </a-layout-content>
      </a-layout>
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

.site-layout-background {
  background: #fff;
}
</style>