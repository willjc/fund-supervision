import request from '@/utils/request'

// 查询入驻申请列表
export function listInstitutionApply(query) {
  // 临时模拟数据，避免404错误
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        msg: '查询成功',
        rows: [
          {
            applyId: 1,
            institutionName: '康乐养老院',
            registeredCapital: 500,
            street: '朝阳区',
            creditCode: '91110000123456789A',
            recordNumber: 'BJ20230001',
            contactPerson: '张三',
            contactPhone: '13800138000',
            establishDate: '2020-01-15',
            organizer: '民政局',
            status: '1', // 已入驻
            applyTime: '2023-12-01 10:00:00',
            supervisionBalance: 150000.00
          },
          {
            applyId: 2,
            institutionName: '夕阳红养老中心',
            registeredCapital: 800,
            street: '海淀区',
            creditCode: '91110000987654321B',
            recordNumber: 'BJ20230002',
            contactPerson: '李四',
            contactPhone: '13900139000',
            establishDate: '2019-06-20',
            organizer: '社会公益组织',
            status: '0', // 待审批
            applyTime: '2023-12-05 14:30:00',
            supervisionBalance: 280000.00
          },
          {
            applyId: 3,
            institutionName: '幸福晚年护理院',
            registeredCapital: 300,
            street: '西城区',
            creditCode: '91110000567891234C',
            recordNumber: 'BJ20230003',
            contactPerson: '王五',
            contactPhone: '13700137000',
            establishDate: '2021-03-10',
            organizer: '个人投资',
            status: '1', // 已入驻
            applyTime: '2023-11-20 09:15:00',
            supervisionBalance: 120000.00
          }
        ],
        total: 3
      });
    }, 500);
  });

  // 正式接口（待后端实现后启用）
  // return request({
  //   url: '/pension/institution/apply/list',
  //   method: 'get',
  //   params: query
  // })
}

// 查询入驻申请详细
export function getInstitutionApply(applyId) {
  // 临时模拟数据
  return new Promise((resolve) => {
    setTimeout(() => {
      const mockData = {
        1: {
          applyId: 1,
          institutionName: '康乐养老院',
          registeredCapital: 500,
          registeredAddress: '北京市朝阳区建国路88号',
          street: '朝阳区',
          creditCode: '91110000123456789A',
          recordNumber: 'BJ20230001',
          contactPerson: '张三',
          contactPhone: '13800138000',
          establishDate: '2020-01-15',
          actualAddress: '北京市朝阳区建国路88号',
          organizer: '民政局',
          responsibleName: '赵六',
          responsibleIdCard: '110101199001011234',
          responsibleAddress: '北京市朝阳区幸福小区',
          responsiblePhone: '13500135000',
          institutionType: 'nursing_home',
          bedCount: 200,
          feeRange: '3000-8000元/月',
          fixedAssets: 1000,
          supervisionAccount: '6222021234567890123',
          basicAccount: '6222021234567890124',
          status: '1',
          applyTime: '2023-12-01 10:00:00',
          businessLicense: '/profile/upload/2023/12/01/business_license.jpg',
          approvalCertificate: '/profile/upload/2023/12/01/approval_cert.jpg',
          supervisionAgreement: '/profile/upload/2023/12/01/supervision_agreement.jpg',
          supervisionBalance: 150000.00
        }
      };

      resolve({
        code: 200,
        msg: '查询成功',
        data: mockData[applyId] || mockData[1]
      });
    }, 300);
  });

  // 正式接口（待后端实现后启用）
  // return request({
  //   url: '/pension/institution/apply/' + applyId,
  //   method: 'get'
  // })
}

// 获取申请统计信息
export function getApplyStatistics() {
  // 临时模拟数据
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        msg: '查询成功',
        data: {
          totalCount: 15,
          pendingCount: 3,
          approvedCount: 10,
          rejectedCount: 2
        }
      });
    }, 200);
  });

  // 正式接口（待后端实现后启用）
  // return request({
  //   url: '/pension/institution/apply/statistics',
  //   method: 'get'
  // })
}

// 申请解除监管
export function releaseSupervision(applyId, data) {
  return request({
    url: '/pension/institution/apply/releaseSupervision/' + applyId,
    method: 'post',
    data: data
  })
}

// 批量申请解除监管
export function batchReleaseSupervision(applyIds) {
  return request({
    url: '/pension/institution/apply/batchReleaseSupervision',
    method: 'post',
    data: applyIds
  })
}

// 撤回申请
export function withdrawApply(applyId) {
  return request({
    url: '/pension/institution/apply/withdraw/' + applyId,
    method: 'post'
  })
}

// 导出入驻申请列表
export function exportInstitutionApply(query) {
  return request({
    url: '/pension/institution/apply/export',
    method: 'post',
    params: query
  })
}