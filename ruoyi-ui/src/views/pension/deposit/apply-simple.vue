<template>
  <div class="app-container">
    <h1>入住人押金使用申请</h1>
    <p>页面功能正在开发中...</p>

    <el-button type="primary" @click="testAPI">测试API</el-button>

    <div v-if="testData">
      <h3>测试数据：</h3>
      <pre>{{ testData }}</pre>
    </div>
  </div>
</template>

<script>
import { listResident } from "@/api/elder/resident";

export default {
  name: "PensionDepositApply",
  data() {
    return {
      testData: null
    };
  },
  created() {
    console.log("页面已加载");
  },
  methods: {
    testAPI() {
      console.log("测试API调用");
      listResident({}).then(response => {
        console.log("API响应:", response);
        this.testData = response;
      }).catch(error => {
        console.error("API错误:", error);
        this.testData = { error: error.message };
      });
    }
  }
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>