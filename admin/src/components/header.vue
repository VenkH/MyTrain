<template>
  <a-layout-header class="header" id="components-layout-demo-top-side-2">
    <div class="logo" />
    <div style="float:right; color: white">
        欢迎使用管理控台
    </div>
    <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <coffee-outlined/>&nbsp; 欢迎
        </router-link>
      </a-menu-item>
      <a-menu-item key="/about">
        <router-link to="/about">
          <user-outlined/>&nbsp;关于
        </router-link>
      </a-menu-item>
      <a-menu-item key="/station">
        <router-link to="/station">
          <user-outlined/>&nbsp;车站管理
        </router-link>
      </a-menu-item>
      <a-menu-item key="/train">
        <router-link to="/train">
          <user-outlined/>&nbsp;火车管理
        </router-link>
      </a-menu-item>
      <a-menu-item key="/train-station">
        <router-link to="/train-station">
          <user-outlined/>&nbsp;火车车站
        </router-link>
      </a-menu-item>
      <a-menu-item key="/train-carriage">
        <router-link to="/train-carriage">
          <user-outlined/>&nbsp;火车车厢
        </router-link>
      </a-menu-item>
      <a-menu-item key="/train-seat">
        <router-link to="/train-seat">
          <user-outlined/>&nbsp;火车座位
        </router-link>
      </a-menu-item>
    </a-menu>
  </a-layout-header>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import router from '@/router'

export default defineComponent({
  name: "the-header-view",
  setup() {
    const selectedKeys = ref([]);

    watch(() => router.currentRoute.value.path, (newValue) => {
      console.log('watch', newValue);
      selectedKeys.value = [];
      selectedKeys.value.push(newValue);
    }, {immediate: true});
    return {
      selectedKeys
    };
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
#components-layout-demo-top-side-2 .logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  background: rgba(255, 255, 255, 0.3);
}

.ant-row-rtl #components-layout-demo-top-side-2 .logo {
  float: right;
  margin: 16px 0 16px 24px;
}
</style>
