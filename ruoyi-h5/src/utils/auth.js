import Cookies from 'js-cookie'

const TokenKey = 'H5-Token'
const UserInfoKey = 'H5-UserInfo'

// Token 管理
export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

// 用户信息管理
export function getUserInfo() {
  const userInfo = localStorage.getItem(UserInfoKey)
  return userInfo ? JSON.parse(userInfo) : null
}

export function setUserInfo(userInfo) {
  return localStorage.setItem(UserInfoKey, JSON.stringify(userInfo))
}

export function removeUserInfo() {
  return localStorage.removeItem(UserInfoKey)
}

// 清除所有认证信息
export function clearAuth() {
  removeToken()
  removeUserInfo()
}
