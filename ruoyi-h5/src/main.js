import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// 引入移动端适配
import 'amfe-flexible'

// 引入Vant组件库和样式
import Vant from 'vant'
import 'vant/lib/index.css'

const app = createApp(App)

app.use(router)
app.use(store)
app.use(Vant)

app.mount('#app')
