<template>
  <div class="profile-page">

    <!-- 内容卡片 -->
    <div class="profile-card">
      <!-- 头像 -->
      <div class="form-row avatar-row" @click="changeAvatar">
        <span class="form-label">头像</span>
        <div class="form-value-wrap">
          <van-image
            round
            width="48"
            height="48"
            :src="displayAvatar"
            fit="cover"
            class="avatar-image"
          />
          <van-icon name="arrow" class="arrow-icon" />
        </div>
      </div>

      <!-- 昵称 -->
      <div class="form-row" @click="editNickname">
        <span class="form-label">昵称</span>
        <div class="form-value-wrap">
          <span class="form-value">{{ userInfo.nickName || '未设置' }}</span>
          <van-icon name="arrow" class="arrow-icon" />
        </div>
      </div>

      <!-- 姓名 -->
      <div class="form-row" @click="editRealName">
        <span class="form-label">姓名</span>
        <div class="form-value-wrap">
          <span class="form-value">{{ userInfo.realName || '未设置' }}</span>
          <van-icon name="arrow" class="arrow-icon" />
        </div>
      </div>

      <!-- 性别 -->
      <div class="form-row" @click="showGenderPicker = true">
        <span class="form-label">性别</span>
        <div class="form-value-wrap">
          <span class="form-value">{{ genderText }}</span>
          <van-icon name="arrow" class="arrow-icon" />
        </div>
      </div>

      <!-- 手机号 -->
      <div class="form-row last-row">
        <span class="form-label">手机号</span>
        <div class="form-value-wrap">
          <span class="form-value">{{ maskedPhone }}</span>
        </div>
      </div>
    </div>

    <!-- ���别选择器 -->
    <van-popup v-model:show="showGenderPicker" position="bottom">
      <van-picker
        :columns="genderOptions"
        @confirm="onGenderConfirm"
        @cancel="showGenderPicker = false"
      />
    </van-popup>

    <!-- 输入弹窗 -->
    <van-popup v-model:show="showInputDialog" position="bottom" :style="{ padding: '30px' }">
      <van-field
        v-model="inputValue"
        :label="inputLabel"
        :placeholder="inputPlaceholder"
        autofocus
        @keyup.enter="confirmInput"
      />
      <div class="input-dialog-buttons">
        <van-button plain type="default" @click="showInputDialog = false">取消</van-button>
        <van-button type="primary" @click="confirmInput">确定</van-button>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast, showLoadingToast, closeToast, showDialog } from 'vant'
import { useUserStore } from '@/store/modules/user'
import { updateUserInfo, uploadAvatar, maskPhone } from '@/api/user'
import { getImageUrl } from '@/utils/image'

const router = useRouter()
const userStore = useUserStore()

// 用户信息
const userInfo = ref({
  nickName: '',
  realName: '',
  sex: '0',
  phonenumber: '',
  avatar: ''
})

// 显示性别选择器
const showGenderPicker = ref(false)

// 显示输入弹窗
const showInputDialog = ref(false)
const inputValue = ref('')
const inputLabel = ref('')
const inputPlaceholder = ref('')
const inputFieldKey = ref('') // 用于记录当前编辑的字段

// 性别选项
const genderOptions = [
  { text: '男', value: '0' },
  { text: '女', value: '1' }
]

// 默认头像
const defaultAvatar = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

// 显示的头像
const displayAvatar = computed(() => {
  const avatar = userInfo.value.avatar
  if (!avatar) return defaultAvatar
  return getImageUrl(avatar)
})

// 性别文本
const genderText = computed(() => {
  const sex = userInfo.value.sex
  if (sex === '0') return '男'
  if (sex === '1') return '女'
  return '未设置'
})

// 脱敏手机号
const maskedPhone = computed(() => {
  return maskPhone(userInfo.value.phonenumber)
})

// 加载用户信息
const loadUserInfo = () => {
  const storedInfo = userStore.userInfo
  if (storedInfo) {
    userInfo.value = {
      nickName: storedInfo.nickName || '',
      realName: storedInfo.realName || '',
      sex: storedInfo.sex || '0',
      phonenumber: storedInfo.phonenumber || '',
      avatar: storedInfo.avatar || ''
    }
  }
}

// 更换头像
const changeAvatar = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (!file) return

    // 检查文件大小 (限制2MB)
    if (file.size > 2 * 1024 * 1024) {
      showToast('图片大小不能超过2MB')
      return
    }

    showLoadingToast({
      message: '上传中...',
      forbidClick: true,
      duration: 0
    })

    try {
      const response = await uploadAvatar(file)
      closeToast()

      if (response.code === 200) {
        // 安全获取头像URL，兼容直接返回URL或data.avatar格式
        const avatarUrl = response.data?.avatar || response.data || response.avatar
        if (avatarUrl) {
          userInfo.value.avatar = avatarUrl
          // 更新 store
          userStore.updateUserInfo({ avatar: avatarUrl })
          showSuccessToast('头像上传成功')
        } else {
          showToast('上传成功，但未返回头像地址')
        }
      } else {
        showToast(response.msg || '上传失败')
      }
    } catch (error) {
      closeToast()
      console.error('头像上传失败:', error)
      showToast('上传失败，请重试')
    }
  }
  input.click()
}

// 编辑昵称
const editNickname = () => {
  inputLabel.value = '昵称'
  inputPlaceholder.value = '请输入昵称'
  inputValue.value = userInfo.value.nickName || ''
  inputFieldKey.value = 'nickName'
  showInputDialog.value = true
}

// 编辑姓名
const editRealName = () => {
  inputLabel.value = '姓名'
  inputPlaceholder.value = '请输入真实姓名'
  inputValue.value = userInfo.value.realName || ''
  inputFieldKey.value = 'realName'
  showInputDialog.value = true
}

// 确认输入
const confirmInput = () => {
  showInputDialog.value = false
  const value = inputValue.value.trim()
  if (value) {
    const data = inputFieldKey.value === 'nickName' ? { nickName: value } : { realName: value }
    saveUserInfo(data)
  } else {
    showToast('请输入内容')
  }
}

// 性别确认
const onGenderConfirm = ({ selectedOptions }) => {
  const gender = selectedOptions[0].value
  showGenderPicker.value = false
  saveUserInfo({ sex: gender })
}

// 保存用户信息
const saveUserInfo = async (data) => {
  showLoadingToast({
    message: '保存中...',
    forbidClick: true,
    duration: 0
  })

  try {
    const response = await updateUserInfo(data)
    closeToast()

    if (response.code === 200) {
      // 更新本地数据
      userInfo.value = { ...userInfo.value, ...data }
      // 更新 store
      userStore.updateUserInfo(data)
      showSuccessToast('保存成功')
    } else {
      showToast(response.msg || '保存失败')
    }
  } catch (error) {
    closeToast()
    console.error('保存失败:', error)
    showToast('保存失败，请重试')
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background-color: #f5f6fc;
}

/* 顶部导航栏 */
:deep(.van-nav-bar) {
  background: linear-gradient(180deg, #0f73ff 0%, #4fc7ff 100%);
}

:deep(.van-nav-bar__title) {
  color: #fff;
}

:deep(.van-nav-bar .van-icon) {
  color: #fff;
}

:deep(.van-nav-bar__left) {
  padding: 0 16px;
}

/* 内容卡片 */
.profile-card {
  background: #fff;
  margin: 12px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.form-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.2s ease;
}

.form-row:last-child {
  border-bottom: none;
}

.form-row:active {
  background: #f8f8f8;
}

.form-row.avatar-row {
  padding: 12px 16px;
}

.form-label {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.form-value-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-value {
  font-size: 14px;
  color: #999;
}

.avatar-image {
  flex-shrink: 0;
}

.arrow-icon {
  font-size: 14px;
  color: #c8c9cc;
}

/* 输入弹窗样式 */
.input-dialog-buttons {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-top: 20px;
}

.input-dialog-buttons .van-button {
  flex: 1;
}
</style>
