import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
// 引入 Element Plus 和样式
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
// 引入路由
import router from './router';

const app = createApp(App);  // 创建 app 实例
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
app.use(ElementPlus);        // 使用 Element Plus 插件
app.use(router);
app.mount('#app');           // 挂载应用
