<template>
  <div class="vant-icon-body">
    <el-input v-model="name" class="icon-search" clearable placeholder="请输入Vant图标名称" @clear="filterIcons" @input="filterIcons">
      <i slot="suffix" class="el-icon-search el-input__icon" />
    </el-input>
    <div class="icon-list">
      <div class="list-container">
        <div v-for="(item, index) in filteredIconList" class="icon-item-wrapper" :key="index" @click="selectedIcon(item)">
          <div :class="['icon-item', { active: activeIcon === item }]">
            <div :class="['vant-icon', `van-icon-${item}`]" />
            <span>{{ item }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'VantIconSelect',
  props: {
    activeIcon: {
      type: String
    }
  },
  data() {
    return {
      name: '',
      iconList: [
        // 基础图标
        'home-o', 'search', 'friends-o', 'setting-o', 'question-o', 'phone-o', 'lock', 'unlock',
        'location-o', 'like-o', 'star-o', 'postcard', 'photo-o', 'play-circle-o', 'pause-circle-o',
        'stop-circle-o', 'replay', 'pass', 'fail', 'minus', 'plus', 'delete', 'edit',

        // 箭头图标
        'arrow', 'arrow-left', 'arrow-up', 'arrow-down', 'arrow-right',
        'arrow-left', 'arrow-up', 'arrow-down', 'arrow-right',
        'double-arrow-left', 'double-arrow-up', 'double-arrow-down', 'double-arrow-right',
        'chevron-left', 'chevron-up', 'chevron-down', 'chevron-right',

        // 媒体图标
        'music-o', 'video-o', 'photograph', 'pause', 'play', 'stop',
        'revoke', 'success', 'fail', 'plus', 'minus', 'cross', 'check',

        // 商务图标
        'shop-o', 'cart-o', 'shopping-cart-o', 'friends', 'friend-o', 'manager-o',
        'gold-coin-o', 'gem-o', 'gift-o', 'gift-card-o', 'fire-o', 'bulb-o',
        'label-o', 'bookmark-o', 'service-o', 'bag-o', 'qr', 'scan',

        // 生活服务图标
        'hospital-o', 'first-aid-o', 'stethoscope-o', 'thermometer-o',
        'medication', 'record', 'health', 'medical', 'clinic', 'ward',
        'ambulance', 'wheelchair', 'crutches', 'oxygen', 'injection',

        // 设施图标
        'bathroom-o', 'shower-o', 'bathtub-o', 'toilet-o',
        'wifi-o', 'network', 'signal', 'airplay', 'bluetooth-o', 'share',
        'phone-o', 'envelop-o', 'location-o', 'guide-o', 'bookmark-o',
        'like-o', 'star-o', 'thumb-circle-o', 'good-job-o',

        // 建筑设施
        'home', 'home-o', 'office-building', 'shop', 'store',
        'hotel-o', 'bed', 'sofa', 'table', 'chair',
        'tv-o', 'fridge-o', 'washing-o', 'computer-o',

        // 交通工具
        'car-o', 'logistics', 'bicycle', 'motorbike', 'truck', 'taxi', 'bus',
        'subway', 'train', 'airplane', 'ship-o', 'boat-o',

        // 服务类图标
        'service-o', 'room-service', 'concierge', 'bell', 'clock-o', 'calendar-o',
        'description', 'notes-o', 'file-o', 'folder-o', 'folder-open-o',
        'qr-invalid', 'shield-o', 'info-o', 'warning-o', 'error',

        // 界面操作
        'apps-o', 'grid', 'list-switch', 'more-o', 'more',
        'wap-home-o', 'wap-nav', 'wap-apps-o', 'wap-aim',
        'bars', 'bulleted-list', 'numbered-list',

        // 文件类
        'description', 'notes-o', 'file-o', 'folder-o',
        'newspaper-o', 'label-o', 'bookmark-o', 'tag-o',

        // 评价反馈
        'star', 'star-o', 'like', 'like-o', 'thumb-circle-o',
        'good-job-o', 'smile', 'smile-o', 'cry', 'cry-o',

        // 其他常用
        'add', 'add-square', 'add-circle', 'subtract', 'subtract-square', 'subtract-circle',
        'multiply', 'divide', 'equal', 'not-equal',
        'percent', 'yuan', 'dollar', 'currency',

        // 设备相关
        'phone', 'phone-o', 'desktop-o', 'laptop-o', 'tablet-o',
        'camera-o', 'video-o', 'music-o', 'speaker-o',
        'volume-o', 'volume', 'battery', 'flashlight'
      ]
    }
  },
  computed: {
    filteredIconList() {
      if (!this.name) return this.iconList;
      return this.iconList.filter(item => item.includes(this.name));
    }
  },
  methods: {
    filterIcons() {
      // 由 computed属性处理
    },
    selectedIcon(name) {
      this.$emit('selected', name)
      document.body.click()
    },
    reset() {
      this.name = ''
    }
  }
}
</script>

<style lang="scss" scoped>
.vant-icon-body {
  width: 100%;
  padding: 10px;

  .icon-search {
    margin-bottom: 10px;
  }

  .icon-list {
    height: 200px;
    overflow-y: auto;

    .list-container {
      display: flex;
      flex-wrap: wrap;

      .icon-item-wrapper {
        width: calc(10% - 10px);
        margin: 5px;
        text-align: center;
        cursor: pointer;

        .icon-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 8px 4px;
          border: 1px solid #e6e6e6;
          border-radius: 4px;
          transition: all 0.3s;

          &:hover {
            border-color: #409eff;
            background-color: #f5f7fa;
          }

          &.active {
            border-color: #409eff;
            background-color: #409eff;
            color: #fff;
          }

          .vant-icon {
            font-size: 20px;
            margin-bottom: 4px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
          }

          span {
            font-size: 12px;
            line-height: 1.2;
            word-break: break-all;
          }
        }
      }
    }
  }
}
</style>