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
    path: '/search',
    name: 'Search',
    component: () => import('@/views/search/index.vue'),
    meta: { title: '搜索', requireAuth: false }
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
    path: '/order/confirm/:institutionId',
    name: 'OrderConfirm',
    component: () => import('@/views/order/confirm.vue'),
    meta: { title: '确认订单', requireAuth: false }
  },
  {
    path: '/payment/cashier/:orderId',
    name: 'PaymentCashier',
    component: () => import('@/views/payment/cashier.vue'),
    meta: { title: '支付收银台', requireAuth: false }
  },
  {
    path: '/payment/success',
    name: 'PaymentSuccess',
    component: () => import('@/views/payment/success.vue'),
    meta: { title: '支付成功', requireAuth: false }
  },
  {
    path: '/notice/list',
    name: 'NoticeList',
    component: () => import('@/views/notice/list.vue'),
    meta: { title: '通知公告', requireAuth: false }
  },
  {
    path: '/notice/detail/:id',
    name: 'NoticeDetail',
    component: () => import('@/views/notice/detail.vue'),
    meta: { title: '通知详情', requireAuth: false }
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
    path: '/appointment/booking/:institutionId',
    name: 'AppointmentBooking',
    component: () => import('@/views/appointment/booking.vue'),
    meta: { title: '预约参观', requireAuth: false }
  },
  {
    path: '/appointment/success',
    name: 'AppointmentSuccess',
    component: () => import('@/views/appointment/success.vue'),
    meta: { title: '预约成功', requireAuth: false }
  },
  {
    path: '/appointment/detail/:id',
    name: 'AppointmentDetail',
    component: () => import('@/views/appointment/detail.vue'),
    meta: { title: '预约详情', requireAuth: false }
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('@/views/user/index.vue'),
    meta: { title: '我的', requireAuth: true }
  },
  // 个人资料
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('@/views/user/profile.vue'),
    meta: { title: '个人资料', requireAuth: false }
  },
  // 老人信息
  {
    path: '/user/elder',
    name: 'UserElder',
    component: () => import('@/views/user/elder/index.vue'),
    meta: { title: '老人信息', requireAuth: false }
  },
  {
    path: '/user/elder/form',
    name: 'UserElderForm',
    component: () => import('@/views/user/elder/form.vue'),
    meta: { title: '新增老人信息', requireAuth: false }
  },
  // 我的费用
  {
    path: '/user/expense',
    name: 'UserExpense',
    component: () => import('@/views/user/expense/index.vue'),
    meta: { title: '我的费用', requireAuth: false }
  },
  // 退款管理
  {
    path: '/user/expense/refund/apply',
    name: 'RefundApply',
    component: () => import('@/views/user/expense/refund/apply.vue'),
    meta: { title: '申请退款', requireAuth: false }
  },
  {
    path: '/user/expense/refund/review/:id',
    name: 'RefundReview',
    component: () => import('@/views/user/expense/refund/review.vue'),
    meta: { title: '退款审核', requireAuth: false }
  },
  {
    path: '/user/expense/refund/list',
    name: 'RefundList',
    component: () => import('@/views/user/expense/refund/list.vue'),
    meta: { title: '退款记录', requireAuth: false }
  },
  // 待办事项
  {
    path: '/user/todo',
    name: 'UserTodo',
    component: () => import('@/views/user/todo/index.vue'),
    meta: { title: '待办事项', requireAuth: false }
  },
  // 我的评价
  {
    path: '/user/evaluation',
    name: 'UserEvaluation',
    component: () => import('@/views/user/evaluation/index.vue'),
    meta: { title: '我的评价', requireAuth: false }
  },
  {
    path: '/user/evaluation/form',
    name: 'UserEvaluationForm',
    component: () => import('@/views/user/evaluation/form.vue'),
    meta: { title: '写评价', requireAuth: false }
  },
  // 评价提交页面
  {
    path: '/review/submit/:orderId',
    name: 'ReviewSubmit',
    component: () => import('@/views/review/submit.vue'),
    meta: { title: '发表评价', requireAuth: true }
  },
    // 我的收藏
  {
    path: '/user/collection',
    name: 'UserCollection',
    component: () => import('@/views/user/collection/index.vue'),
    meta: { title: '我的收藏', requireAuth: false }
  },
  // 我要投诉
  {
    path: '/user/complaint',
    name: 'UserComplaint',
    component: () => import('@/views/user/complaint/index.vue'),
    meta: { title: '我要投诉', requireAuth: false }
  },
  {
    path: '/user/complaint/form',
    name: 'UserComplaintForm',
    component: () => import('@/views/user/complaint/form.vue'),
    meta: { title: '新增投诉', requireAuth: false }
  },
  // 我的订单 (保留与原路由兼容)
  {
    path: '/user/order',
    name: 'UserOrder',
    component: () => import('@/views/order/index.vue'),
    meta: { title: '我的订单', requireAuth: false }
  },
  // 我的预约
  {
    path: '/user/appointment',
    name: 'UserAppointment',
    component: () => import('@/views/user/appointment.vue'),
    meta: { title: '我的预约', requireAuth: false }
  },
  // 以下是旧路由,保留兼容性
  {
    path: '/user/fee',
    name: 'UserFee',
    component: () => import('@/views/user/fee.vue'),
    meta: { title: '我的费用', requireAuth: false }
  },
  {
    path: '/user/review',
    name: 'UserReview',
    component: () => import('@/views/user/review.vue'),
    meta: { title: '我的评价', requireAuth: false }
  },
  {
    path: '/user/favorite',
    name: 'UserFavorite',
    component: () => import('@/views/user/favorite.vue'),
    meta: { title: '我的收藏', requireAuth: false }
  },
  {
    path: '/freetrial/apply/:institutionId',
    name: 'FreeTrialApply',
    component: () => import('@/views/freetrial/apply.vue'),
    meta: { title: '免费入住', requireAuth: false }
  },
  {
    path: '/freetrial/success',
    name: 'FreeTrialSuccess',
    component: () => import('@/views/freetrial/success.vue'),
    meta: { title: '预约入住', requireAuth: false }
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
