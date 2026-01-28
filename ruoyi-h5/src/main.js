// ==================== polyfill 导入 ====================
// 必须在最前面导入，确保兼容老版本浏览器
import 'core-js/stable'
import 'regenerator-runtime/runtime'
// =======================================================

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// 引入移动端适配
import 'amfe-flexible'

// 引入Vant组件库和样式
import Vant from 'vant'
import 'vant/lib/index.css'

// 引入SVG图标
import { SvgIcon } from './assets/icons'

const app = createApp(App)

// 注册全局SVG图标组件
app.component('svg-icon', SvgIcon)

app.use(router)
app.use(store)
app.use(Vant)

app.mount('#app')
