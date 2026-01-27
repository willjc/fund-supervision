import request from '@/utils/request'

// 查询H5用户列表
export function listH5User(query) {
  return request({
    url: '/system/h5userManage/list',
    method: 'get',
    params: query
  })
}

// 查询H5用户详细
export function getH5User(userId) {
  return request({
    url: '/system/h5userManage/' + userId,
    method: 'get'
  })
}

// 新增H5用户
export function addH5User(data) {
  return request({
    url: '/system/h5userManage',
    method: 'post',
    data: data
  })
}

// 修改H5用户
export function updateH5User(data) {
  return request({
    url: '/system/h5userManage/edit',
    method: 'post',
    data: data
  })
}

// 删除H5用户
export function delH5User(userIds) {
  return request({
    url: '/system/h5userManage/' + userIds,
    method: 'delete'
  })
}

// 重置H5用户密码
export function resetH5UserPwd(data) {
  return request({
    url: '/system/h5userManage/resetPwd',
    method: 'post',
    data: data
  })
}
