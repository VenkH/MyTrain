import { createStore } from 'vuex'

const MEMBER = "MEMBER";

export default createStore({
  state: {
    member:window.SessionStorage.get(MEMBER) || {}
  },
  // 数值单位转换
  getters: {
  },
  // 同步
  mutations: {
    setMember(state, _member) {
      state.member = _member;
      window.SessionStorage.set(MEMBER, _member);
    }

  },
  // 异步
  actions: {
  },
  // 可以再细分多个模块，每个模块都拥有自己的state、getters、mutations、actions
  modules: {
  }
})
