<template>
  <div></div>
</template>

<script>
export default {
  name: 'WarningMonitorRedirect',
  created() {
    // 直接跳转到独立的预警监控大屏页面
    const url = process.env.NODE_ENV === 'production'
      ? '/screen/warning-monitor.html'
      : 'http://localhost:8080/screen/warning-monitor.html';

    window.location.href = url;
  }
}
</script>