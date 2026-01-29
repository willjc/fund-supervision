<template>
  <div></div>
</template>

<script>
export default {
  name: 'FundSupervisionRedirect',
  created() {
    // 直接跳转到独立的资金监管大屏页面
    // 使用相对路径，避免硬编码域名导致跨环境问题
    const url = '/screen/fund-supervision.html';
    window.location.href = url;
  }
}
</script>