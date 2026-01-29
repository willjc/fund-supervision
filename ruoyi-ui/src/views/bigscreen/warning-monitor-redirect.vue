<template>
  <div></div>
</template>

<script>
export default {
  name: 'WarningMonitorRedirect',
  created() {
    // 直接跳转到独立的预警监控大屏页面
    // 使用相对路径，避免硬编码域名导致跨环境问题
    const url = '/screen/warning-monitor.html';
    window.location.href = url;
  }
}
</script>