const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,

  // 开发服务器配置
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  },

  // postcss 配置 - 移动端适配
  css: {
    loaderOptions: {
      postcss: {
        postcssOptions: {
          plugins: [
            require('postcss-pxtorem')({
              rootValue: 37.5, // Vant 官方根字体大小是 37.5
              propList: ['*'],
              selectorBlackList: ['.norem'] // 过滤掉.norem-开头的class,不进行rem转换
            })
          ]
        }
      }
    }
  },

  // 生产环境配置
  publicPath: '/',  // 使用绝对路径,避免深层路由资源加载问题
  outputDir: 'dist',
  assetsDir: 'static',
  productionSourceMap: false
})
