module.exports = {
  presets: [
    ['@vue/cli-plugin-babel/preset', {
      useBuiltIns: 'entry',
      corejs: 3,
      targets: {
        // 兼容到 iOS 8 和 Android 4.4
        ios: '8',
        android: '4.4',
        // 也支持 Chrome 较老版本
        chrome: '50'
      }
    }]
  ],
  plugins: []
}
