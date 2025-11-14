import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/home/index.vue'),
    meta: { title: '首页', requireAuth: false }
  },
  {
    path: '/institution',
    name: 'Institution',
    component: () => import('@/views/institution/index.vue'),
    meta: { title: '养老机构', requireAuth: false }
  },
  {
    path: '/institution/detail/:id',
    name: 'InstitutionDetail',
    component: () => import('@/views/institution/detail.vue'),
    meta: { title: '机构详情', requireAuth: false }
  },
  {
    path: '/deposit',
    name: 'Deposit',
    component: () => import('@/views/deposit/index.vue'),
    meta: { title: '押金管理', requireAuth: true }
  },
  {
    path: '/deposit/balance',
    name: 'DepositBalance',
    component: () => import('@/views/deposit/balance.vue'),
    meta: { title: '押金余额', requireAuth: true }
  },
  {
    path: '/deposit/apply-list',
    name: 'DepositApplyList',
    component: () => import('@/views/deposit/applyList.vue'),
    meta: { title: '申请列表', requireAuth: true }
  },
  {
    path: '/deposit/apply-detail/:id',
    name: 'DepositApplyDetail',
    component: () => import('@/views/deposit/applyDetail.vue'),
    meta: { title: '申请详情', requireAuth: true }
  },
  {
    path: '/deposit/approve/:id',
    name: 'DepositApprove',
    component: () => import('@/views/deposit/approve.vue'),
    meta: { title: '押金审批', requireAuth: true }
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('@/views/order/index.vue'),
    meta: { title: '我的订单', requireAuth: false }
  },
  {
    path: '/order/detail/:id',
    name: 'OrderDetail',
    component: () => import('@/views/order/detail.vue'),
    meta: { title: '订单详情', requireAuth: false }
  },
  {
    path: '/notice/list',
    name: 'NoticeList',
    component: () => import('@/views/notice/list.vue'),
    meta: { title: '通知公告', requireAuth: false }
  },
  {
    path: '/todo/list',
    name: 'TodoList',
    component: () => import('@/views/todo/list.vue'),
    meta: { title: '待办事项', requireAuth: false }
  },
  {
    path: '/elder/list',
    name: 'ElderList',
    component: () => import('@/views/elder/index.vue'),
    meta: { title: '老人信息', requireAuth: false }
  },
  {
    path: '/fee/query',
    name: 'FeeQuery',
    component: () => import('@/views/fee/query.vue'),
    meta: { title: '费用查询', requireAuth: false }
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('@/views/user/index.vue'),
    meta: { title: '我的', requireAuth: false }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/user/login.vue'),
    meta: { title: '登录', requireAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || 'H5移动端'

  // 判断是否需要登录
  if (to.meta.requireAuth) {
    const token = getToken()
    if (token) {
      next()
    } else {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    }
  } else {
    next()
  }
})

export default router
