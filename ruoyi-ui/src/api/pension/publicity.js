import request from '@/utils/request'

// 获取机构公示信息
export function getPublicityInfo() {
  return request({
    url: '/pension/publicity/info',
    method: 'get'
  })
}

// 保存机构公示信息
export function savePublicityInfo(data) {
  return request({
    url: '/pension/publicity/save',
    method: 'post',
    data: data
  })
}

// 上传公示图片
export function uploadPicture(fileUrl) {
  return request({
    url: '/pension/publicity/upload-picture',
    method: 'post',
    data: { file: fileUrl }
  })
}
