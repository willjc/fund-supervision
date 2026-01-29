<template>
  <div></div>
</template>

<script>
export default {
  name: 'InstitutionRedirect',
  created() {
    // 直接跳转到独立的机构分布大屏页面
    // 使用相对路径，避免硬编码域名导致跨环境问题
    const url = '/screen/institution-distribution.html';
    window.location.href = url;
  }
}
</script>