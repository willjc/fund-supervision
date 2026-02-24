import { createRouter, createWebHistory } from 'vue-router'
import { getToken, setToken, setUserInfo } from '@/utils/auth'
import { useUserStore } from '@/store/modules/user'
import { showToast } from 'vant'
import { autoLogin } from '@/utils/zhb'

// 标记是否正在登录中，避免重复触发
let isLoggingIn = false

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/home/index.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/search/index.vue'),
    meta: { title: '搜索' }
  },
  {
    path: '/institution',
    name: 'Institution',
    component: () => import('@/views/institution/index.vue'),
    meta: { title: '养老机构' }
  },
  {
    path: '/institution/detail/:id',
    name: 'InstitutionDetail',
    component: () => import('@/views/institution/detail.vue'),
    meta: { title: '机构详情' }
  },
  {
    path: '/institution/:id/images',
    name: 'InstitutionImages',
    component: () => import('@/views/institution/images.vue'),
    meta: { title: '全部图片' }
  },
  {
    path: '/institution/:id/vr',
    name: 'InstitutionVR',
    component: () => import('@/views/institution/vr.vue'),
    meta: { title: 'VR看房' }
  },
  {
    path: '/deposit',
    name: 'Deposit',
    component: () => import('@/views/deposit/index.vue'),
    meta: { title: '押金管理' }
  },
  {
    path: '/deposit/balance',
    name: 'DepositBalance',
    component: () => import('@/views/deposit/balance.vue'),
    meta: { title: '押金余额' }
  },
  {
    path: '/deposit/apply-list',
    name: 'DepositApplyList',
    component: () => import('@/views/deposit/applyList.vue'),
    meta: { title: '申请列表' }
  },
  {
    path: '/deposit/apply-detail/:id',
    name: 'DepositApplyDetail',
    component: () => import('@/views/deposit/applyDetail.vue'),
    meta: { title: '申请详情' }
  },
  // 资金划拨审批
  {
    path: '/transfer/apply-detail/:id',
    name: 'TransferApplyDetail',
    component: () => import('@/views/transfer/applyDetail.vue'),
    meta: { title: '划拨申请详情' }
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('@/views/order/index.vue'),
    meta: { title: '我的订单' }
  },
  {
    path: '/order/detail/:id',
    name: 'OrderDetail',
    component: () => import('@/views/order/detail.vue'),
    meta: { title: '订单详情' }
  },
  {
    path: '/order/confirm/:institutionId',
    name: 'OrderConfirm',
    component: () => import('@/views/order/confirm.vue'),
    meta: { title: '确认订单' }
  },
  {
    path: '/payment/cashier/:orderId',
    name: 'PaymentCashier',
    component: () => import('@/views/payment/cashier.vue'),
    meta: { title: '支付收银台' }
  },
  {
    path: '/payment/success',
    name: 'PaymentSuccess',
    component: () => import('@/views/payment/success.vue'),
    meta: { title: '支付成功' }
  },
  {
    path: '/notice/list',
    name: 'NoticeList',
    component: () => import('@/views/notice/list.vue'),
    meta: { title: '通知公告' }
  },
  {
    path: '/notice/detail/:id',
    name: 'NoticeDetail',
    component: () => import('@/views/notice/detail.vue'),
    meta: { title: '通知详情' }
  },
  {
    path: '/todo/list',
    name: 'TodoList',
    component: () => import('@/views/todo/list.vue'),
    meta: { title: '待办事项' }
  },
  {
    path: '/elder/list',
    name: 'ElderList',
    component: () => import('@/views/elder/index.vue'),
    meta: { title: '老人信息' }
  },
  {
    path: '/fee/query',
    name: 'FeeQuery',
    component: () => import('@/views/fee/query.vue'),
    meta: { title: '费用查询' }
  },
  {
    path: '/appointment/booking/:institutionId',
    name: 'AppointmentBooking',
    component: () => import('@/views/appointment/booking.vue'),
    meta: { title: '预约参观' }
  },
  {
    path: '/appointment/success',
    name: 'AppointmentSuccess',
    component: () => import('@/views/appointment/success.vue'),
    meta: { title: '预约成功' }
  },
  {
    path: '/appointment/detail/:id',
    name: 'AppointmentDetail',
    component: () => import('@/views/appointment/detail.vue'),
    meta: { title: '预约详情' }
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('@/views/user/index.vue'),
    meta: { title: '我的' }
  },
  // 个人资料
  {
    path: '/user/profile',
    name: 'UserProfile',
    component: () => import('@/views/user/profile.vue'),
    meta: { title: '个人资料' }
  },
  // 老人信息
  {
    path: '/user/elder',
    name: 'UserElder',
    component: () => import('@/views/user/elder/index.vue'),
    meta: { title: '老人信息' }
  },
  {
    path: '/user/elder/form',
    name: 'UserElderForm',
    component: () => import('@/views/user/elder/form.vue'),
    meta: { title: '新增老人信息' }
  },
  // 我的费用
  {
    path: '/user/expense',
    name: 'UserExpense',
    component: () => import('@/views/user/expense/index.vue'),
    meta: { title: '我的费用' }
  },
  // 退款管理
  {
    path: '/user/refund/apply',
    name: 'RefundApply',
    component: () => import('@/views/user/refund/apply.vue'),
    meta: { title: '申请退款' }
  },
  {
    path: '/user/expense/refund/review/:id',
    name: 'RefundReview',
    component: () => import('@/views/user/expense/refund/review.vue'),
    meta: { title: '退款审核' }
  },
  {
    path: '/user/expense/refund/list',
    name: 'RefundList',
    component: () => import('@/views/user/expense/refund/list.vue'),
    meta: { title: '退款记录' }
  },
  // 待办事项
  {
    path: '/user/todo',
    name: 'UserTodo',
    component: () => import('@/views/user/todo/index.vue'),
    meta: { title: '待办事项' }
  },
  // 我的评价
  {
    path: '/user/evaluation',
    name: 'UserEvaluation',
    component: () => import('@/views/user/evaluation/index.vue'),
    meta: { title: '我的评价' }
  },
  {
    path: '/user/evaluation/form',
    name: 'UserEvaluationForm',
    component: () => import('@/views/user/evaluation/form.vue'),
    meta: { title: '写评价' }
  },
  // 评价提交页面
  {
    path: '/review/submit/:orderId',
    name: 'ReviewSubmit',
    component: () => import('@/views/review/submit.vue'),
    meta: { title: '发表评价' }
  },
  // 我的收藏
  {
    path: '/user/collection',
    name: 'UserCollection',
    component: () => import('@/views/user/collection/index.vue'),
    meta: { title: '我的收藏' }
  },
  // 我要投诉
  {
    path: '/user/complaint',
    name: 'UserComplaint',
    component: () => import('@/views/user/complaint/index.vue'),
    meta: { title: '我要投诉' }
  },
  {
    path: '/user/complaint/form',
    name: 'UserComplaintForm',
    component: () => import('@/views/user/complaint/form.vue'),
    meta: { title: '新增投诉' }
  },
  // 我的订单 (保留与原路由兼容)
  {
    path: '/user/order',
    name: 'UserOrder',
    component: () => import('@/views/order/index.vue'),
    meta: { title: '我的订单' }
  },
  // 我的预约
  {
    path: '/user/appointment',
    name: 'UserAppointment',
    component: () => import('@/views/user/appointment.vue'),
    meta: { title: '我的预约' }
  },
  // 以下是旧路由,保留兼容性
  {
    path: '/user/fee',
    name: 'UserFee',
    component: () => import('@/views/user/fee.vue'),
    meta: { title: '我的费用' }
  },
  {
    path: '/user/review',
    name: 'UserReview',
    component: () => import('@/views/user/review.vue'),
    meta: { title: '我的评价' }
  },
  {
    path: '/user/favorite',
    name: 'UserFavorite',
    component: () => import('@/views/user/favorite.vue'),
    meta: { title: '我的收藏' }
  },
  {
    path: '/freetrial/apply/:institutionId',
    name: 'FreeTrialApply',
    component: () => import('@/views/freetrial/apply.vue'),
    meta: { title: '免费入住' }
  },
  {
    path: '/freetrial/success',
    name: 'FreeTrialSuccess',
    component: () => import('@/views/freetrial/success.vue'),
    meta: { title: '预约入住' }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫 - 全局自动登录
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || 'H5移动端'

  // 检查是否有token
  const token = getToken()

  if (token) {
    // 已登录，直接放行
    next()
  } else {
    // 没有token，需要登录
    if (isLoggingIn) {
      // 正在登录中，等待
      next()
      return
    }

    // 标记正在登录
    isLoggingIn = true

    // 显示全屏登录Loading
    if (window.setAppLoggingIn) {
      window.setAppLoggingIn(true)
    }

    try {
      // 调用郑好办自动登录
      const res = await autoLogin()

      if (res.code === 200 && res.data) {
        // 保存token和用户信息
        setToken(res.data.token)
        setUserInfo(res.data.user)

        // 更新store
        const userStore = useUserStore()
        userStore.setToken(res.data.token)
        userStore.setUser(res.data.user)
        if (res.data.elders) {
          userStore.setElders(res.data.elders)
        }

        // 登录成功，关闭loading，继续跳转
        if (window.setAppLoggingIn) {
          window.setAppLoggingIn(false)
        }
        isLoggingIn = false
        next()
      } else {
        throw new Error(res.msg || '登录失败')
      }
    } catch (error) {
      console.error('自动登录失败:', error)

      // 关闭loading
      if (window.setAppLoggingIn) {
        window.setAppLoggingIn(false)
      }

      // 显示错误提示
      showToast({
        message: error.message || '登录失败，请重试',
        duration: 2000
      })

      isLoggingIn = false

      // 登录失败，保持在当前页面或跳转到首页
      // 不跳转到登录页，因为已经没有登录页了
      if (to.path !== '/home') {
        next({ path: '/home' })
      } else {
        next()
      }
    }
  }
})

export default router
