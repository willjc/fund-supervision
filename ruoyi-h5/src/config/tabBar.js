export const TAB_BAR_ITEMS = Object.freeze([
  Object.freeze({ id: 'home', routeName: 'Home', label: '首页', icon: 'wap-home-o' }),
  Object.freeze({ id: 'institution', routeName: 'Institution', label: '机构', icon: 'shop-o' }),
  Object.freeze({ id: 'order', routeName: 'Order', label: '订单', icon: 'orders-o' }),
  Object.freeze({ id: 'user', routeName: 'User', label: '我的', icon: 'user-o' })
])

export const TAB_BAR_ROUTE_NAMES = new Set(TAB_BAR_ITEMS.map((item) => item.routeName))
