<template>
  <div></div>
</template>

<script>
export default {
  name: 'FundSupervisionRedirect',
  created() {
    // 直接跳转到独立的资金监管大屏页面
    const url = process.env.NODE_ENV === 'production'
      ? '/screen/fund-supervision.html'
      : 'http://localhost:8080/screen/fund-supervision.html';

    window.location.href = url;
  }
}
</script>