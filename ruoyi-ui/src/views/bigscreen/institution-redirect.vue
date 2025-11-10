<template>
  <div></div>
</template>

<script>
export default {
  name: 'InstitutionRedirect',
  created() {
    // 直接跳转到独立的机构分布大屏页面
    const url = process.env.NODE_ENV === 'production'
      ? '/screen/institution-distribution.html'
      : 'http://localhost:8080/screen/institution-distribution.html';

    window.location.href = url;
  }
}
</script>