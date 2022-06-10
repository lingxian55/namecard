// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import Web3 from "web3";
import axios from "axios";
import config from "./config/config";
import loading from "./config/Loading";
Vue.use(ElementUI)
Vue.config.productionTip = false
Vue.prototype.web3 =new Web3(window.ethereum);
Vue.prototype.axios=axios.create(config)
Vue.prototype.Loading=loading

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
