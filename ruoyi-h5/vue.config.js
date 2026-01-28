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
      },
      // 代理图片访问路径，解决404问题
      '/profile': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },

  // webpack 配置
  configureWebpack: {
    resolve: {
      fallback: {
        // 避免加载不必要的模块
        'path': false,
        'fs': false
      }
    }
  },

  // 链式 webpack 配置
  chainWebpack: config => {
    // 移除 preload 插件，避免加载问题
    config.plugins.delete('preload')
    config.plugins.delete('prefetch')
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
