<template>
  <div class="notice-page">
    <van-nav-bar title="通知公告" fixed placeholder left-arrow @click-left="onBack" />

    <!-- 通知列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <div v-if="noticeList.length > 0">
          <div
            v-for="notice in noticeList"
            :key="notice.noticeId"
            class="notice-card"
            @click="goToDetail(notice.noticeId)"
          >
            <div class="notice-header">
              <div class="notice-title">{{ notice.noticeTitle }}</div>
              <van-tag v-if="!notice.isRead" type="danger" size="small">未读</van-tag>
            </div>

            <div class="notice-content">
              {{ notice.noticeContent }}
            </div>

            <div class="notice-footer">
              <div class="notice-type">
                <van-icon name="label-o" />
                <span>{{ getNoticeTypeText(notice.noticeType) }}</span>
              </div>
              <div class="notice-time">
                <van-icon name="clock-o" />
                <span>{{ formatDate(notice.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <van-empty v-else description="暂无通知" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import dayjs from 'dayjs'

const router = useRouter()

// 列表状态
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 通知列表
const noticeList = ref([])

// 分页参数
const pageNum = ref(1)
const pageSize = ref(10)

// 模拟通知数据
const mockNotices = [
  {
    noticeId: 1,
    noticeTitle: '关于2025年春节放假通知',
    noticeContent: '根据国家法定节假日安排,我院春节放假时间为2025年1月28日至2月4日,共8天。期间工作人员将轮班值守,确保老人日常照料不受影响。',
    noticeType: '1',
    isRead: false,
    createTime: '2025-01-14 09:00:00'
  },
  {
    noticeId: 2,
    noticeTitle: '本月费用已结算',
    noticeContent: '尊敬的家属,您好!本月费用已结算完成,请查收账单明细。如有疑问请联系财务部门。',
    noticeType: '2',
    isRead: false,
    createTime: '2025-01-13 15:30:00'
  },
  {
    noticeId: 3,
    noticeTitle: '健康体检通知',
    noticeContent: '为确保老人身体健康,我院将于本月20日-25日组织老人进行年度健康体检,请各位家属知悉。',
    noticeType: '1',
    isRead: true,
    createTime: '2025-01-12 10:15:00'
  },
  {
    noticeId: 4,
    noticeTitle: '活动通知:新春茶话会',
    noticeContent: '为庆祝新春佳节,我院将于1月20日下午2点在一楼活动室举办新春茶话会,欢迎家属参加。',
    noticeType: '3',
    isRead: true,
    createTime: '2025-01-10 14:20:00'
  },
  {
    noticeId: 5,
    noticeTitle: '膳食调整通知',
    noticeContent: '根据季节变化和营养需求,本周起调整老人膳食方案,增加冬季温补食材,详情请咨询营养师。',
    noticeType: '1',
    isRead: true,
    createTime: '2025-01-08 11:00:00'
  },
  {
    noticeId: 6,
    noticeTitle: '探视时间调整通知',
    noticeContent: '为保障老人休息质量,自本月起探视时间调整为每天9:00-11:30和14:00-17:00,敬请配合。',
    noticeType: '1',
    isRead: true,
    createTime: '2025-01-06 16:45:00'
  },
  {
    noticeId: 7,
    noticeTitle: '账户余额提醒',
    noticeContent: '尊敬的家属,您的账户余额不足1000元,请及时充值以确保服务正常进行。',
    noticeType: '2',
    isRead: true,
    createTime: '2025-01-05 09:30:00'
  },
  {
    noticeId: 8,
    noticeTitle: '消防安全演练通知',
    noticeContent: '为提高全员消防安全意识,我院将于1月18日上午10点进行消防安全演练,届时会有警报声,请勿惊慌。',
    noticeType: '1',
    isRead: true,
    createTime: '2025-01-03 13:15:00'
  }
]

// 返回上一页
const onBack = () => {
  router.back()
}

// 重置列表
const resetList = () => {
  noticeList.value = []
  pageNum.value = 1
  finished.value = false
}

// 下拉刷新
const onRefresh = () => {
  resetList()
  onLoad()
  refreshing.value = false
}

// 加载通知列表 (使用模拟数据)
const onLoad = async () => {
  try {
    loading.value = true

    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 500))

    // 分页处理
    const startIndex = (pageNum.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    const list = mockNotices.slice(startIndex, endIndex)

    if (list.length === 0) {
      finished.value = true
    } else {
      noticeList.value = [...noticeList.value, ...list]
      pageNum.value++

      // 如果返回数据少于pageSize或已到达最后,说明没有更多了
      if (list.length < pageSize.value || endIndex >= mockNotices.length) {
        finished.value = true
      }
    }
  } catch (error) {
    console.error('获取通知列表失败:', error)
    showToast('获取通知列表失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 查看详情
const goToDetail = (noticeId) => {
  showToast('通知详情页面开发中')
  // TODO: 跳转到通知详情页
  // router.push({
  //   name: 'NoticeDetail',
  //   params: { id: noticeId }
  // })
}

// 获取通知类型文本
const getNoticeTypeText = (type) => {
  const typeMap = {
    '1': '系统通知',
    '2': '费用通知',
    '3': '活动通知'
  }
  return typeMap[type] || '其他'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('MM-DD HH:mm')
}

// 页面加载时获取通知列表
onMounted(() => {
  // 自动加载通知列表
})
</script>

<style scoped>
.notice-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.notice-card {
  background-color: #fff;
  margin: 12px;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 8px;
}

.notice-title {
  flex: 1;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
}

.notice-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.notice-type,
.notice-time {
  display: flex;
  align-items: center;
  gap: 4px;
}

.notice-type .van-icon,
.notice-time .van-icon {
  font-size: 14px;
}
</style>
