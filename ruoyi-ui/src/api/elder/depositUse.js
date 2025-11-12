import request from '@/utils/request'

// 查询押金使用申请列表
export function listDepositUse(query) {
  return request({
    url: '/pension/deposit/apply/list',
    method: 'get',
    params: query
  })
}

// 查询押金使用申请列表(mock数据 - 备用)
export function listDepositUseMock(query) {
  return new Promise((resolve) => {
    setTimeout(() => {
      const mockData = [
        {
          id: 1,
          applyNo: 'DEP202501001',
          residentId: 1,
          elderName: '张三',
          bedInfo: 'A101-01',
          amount: 5000.00,
          purpose: '医疗费用',
          urgencyLevel: '紧急',
          status: '待审批',
          paymentStatus: '未拨付',
          expectedUseDate: '2025-01-10',
          reason: '因突发心脏问题需要住院治疗，申请使用押金支付医疗费用',
          description: '需要支付住院押金、手术费用等相关医疗费用',
          remark: '情况紧急，家属已确认同意',
          confirmName: '李四',
          confirmRelation: '子女',
          confirmPhone: '13900139001',
          confirmMethod: '现场确认',
          confirmComment: '同意父亲使用押金支付医疗费用，情况属实',
          signature: '李四',
          createTime: '2025-01-05 09:30:00',
          approver: null,
          approveTime: null,
          approveRemark: null,
          attachments: [
            { name: '医疗证明.pdf', url: '/upload/medical/证明1.pdf' },
            { name: '费用清单.pdf', url: '/upload/medical/清单1.pdf' }
          ]
        },
        {
          id: 2,
          applyNo: 'DEP202501002',
          residentId: 2,
          elderName: '李四',
          bedInfo: 'A101-02',
          amount: 2000.00,
          purpose: '个人物品购买',
          urgencyLevel: '一般',
          status: '已通过',
          paymentStatus: '已拨付',
          expectedUseDate: '2025-01-08',
          reason: '需要购买助听器和日常用品',
          description: '助听器费用1500元，日常用品费用500元',
          remark: '已由家属确认',
          confirmName: '王五',
          confirmRelation: '子女',
          confirmPhone: '13900139002',
          confirmMethod: '电话确认',
          confirmComment: '同意母亲购买助听器等物品',
          signature: '王五',
          createTime: '2025-01-03 14:20:00',
          approver: '监管员张三',
          approveTime: '2025-01-04 10:15:00',
          approveRemark: '申请合理，同意使用押金',
          attachments: [
            { name: '物品清单.xlsx', url: '/upload/items/清单1.xlsx' }
          ],
          paymentAmount: 2000.00,
          paymentTime: '2025-01-04 14:30:00',
          paymentMethod: '现金',
          paymentOperator: '财务员李四'
        },
        {
          id: 3,
          applyNo: 'DEP202501003',
          residentId: 3,
          elderName: '王五',
          bedInfo: 'B201-01',
          amount: 8000.00,
          purpose: '特殊护理服务',
          urgencyLevel: '非常紧急',
          status: '审批中',
          paymentStatus: '未拨付',
          expectedUseDate: '2025-01-06',
          reason: '需要24小时专业护理服务',
          description: '由于老人身体状况突然恶化，需要聘请专业护工提供24小时护理服务',
          remark: '医生建议需要专业护理',
          confirmName: '赵六',
          confirmRelation: '配偶',
          confirmPhone: '13900139003',
          confirmMethod: '视频确认',
          confirmComment: '丈夫确实需要专业护理，同意申请',
          signature: '赵六',
          createTime: '2025-01-05 16:45:00',
          approver: null,
          approveTime: null,
          approveRemark: null,
          attachments: [
            { name: '医生建议.pdf', url: '/upload/medical/建议1.pdf' },
            { name: '护理方案.pdf', url: '/upload/nursing/方案1.pdf' }
          ]
        },
        {
          id: 4,
          applyNo: 'DEP202501004',
          residentId: 1,
          elderName: '张三',
          bedInfo: 'A101-01',
          amount: 1500.00,
          purpose: '其他用途',
          urgencyLevel: '一般',
          status: '已驳回',
          paymentStatus: '未拨付',
          expectedUseDate: '2025-01-12',
          reason: '购买营养保健品',
          description: '需要购买一些营养保健品补充身体',
          remark: '老人自己要求',
          confirmName: '张三',
          confirmRelation: '本人',
          confirmPhone: '13800138001',
          confirmMethod: '现场确认',
          confirmComment: '本人确实需要购买营养品',
          signature: '张三',
          createTime: '2025-01-02 11:20:00',
          approver: '监管员李四',
          approveTime: '2025-01-03 09:45:00',
          approveRemark: '营养保健品不在押金使用范围内，申请驳回',
          attachments: []
        },
        {
          id: 5,
          applyNo: 'DEP202501005',
          residentId: 4,
          elderName: '赵六',
          bedInfo: 'B201-02',
          amount: 3000.00,
          purpose: '医疗费用',
          urgencyLevel: '紧急',
          status: '已撤回',
          paymentStatus: '未拨付',
          expectedUseDate: '2025-01-07',
          reason: '需要购买药品',
          description: '长期服用的药品需要购买',
          remark: '家属后来决定用医保支付',
          confirmName: '孙七',
          confirmRelation: '子女',
          confirmPhone: '13900139004',
          confirmMethod: '电话确认',
          confirmComment: '后来决定用医保支付，撤回申请',
          signature: '孙七',
          createTime: '2025-01-01 15:30:00',
          approver: null,
          approveTime: null,
          approveRemark: null,
          attachments: []
        }
      ];

      // 简单的过滤逻辑
      let filteredData = mockData;
      if (query.elderName) {
        filteredData = filteredData.filter(item => item.elderName.includes(query.elderName));
      }
      if (query.purpose) {
        filteredData = filteredData.filter(item => item.purpose === query.purpose);
      }
      if (query.urgencyLevel) {
        filteredData = filteredData.filter(item => item.urgencyLevel === query.urgencyLevel);
      }
      if (query.status) {
        filteredData = filteredData.filter(item => item.status === query.status);
      }
      if (query.paymentStatus) {
        filteredData = filteredData.filter(item => item.paymentStatus === query.paymentStatus);
      }

      const pageNum = query.pageNum || 1;
      const pageSize = query.pageSize || 10;
      const start = (pageNum - 1) * pageSize;
      const end = start + pageSize;
      const pageData = filteredData.slice(start, end);

      resolve({
        code: 200,
        msg: '查询成功',
        rows: pageData,
        total: filteredData.length
      });
    }, 300);
  });
}

// 查询押金使用申请详细
export function getDepositUse(id) {
  return request({
    url: '/pension/deposit/apply/' + id,
    method: 'get'
  })
}

// 查询押金使用申请详细(mock数据 - 备用)
export function getDepositUseMock(id) {
  return new Promise((resolve) => {
    setTimeout(() => {
      const mockData = {
        id: id,
        applyNo: 'DEP202501001',
        residentId: 1,
        elderName: '张三',
        bedInfo: 'A101-01',
        amount: 5000.00,
        purpose: '医疗费用',
        urgencyLevel: '紧急',
        status: '待审批',
        paymentStatus: '未拨付',
        expectedUseDate: '2025-01-10',
        reason: '因突发心脏问题需要住院治疗，申请使用押金支付医疗费用。医生建议立即住院治疗，费用预计在5000元左右。',
        description: '主要包括：1. 住院押金 2000元 2. 手术费用 2500元 3. 药品费用 500元',
        remark: '情况紧急，家属已确认同意，希望能尽快审批',
        confirmName: '李四',
        confirmRelation: '子女',
        confirmPhone: '13900139001',
        confirmMethod: '现场确认',
        confirmComment: '父亲突然心脏不适，医生建议立即住院。作为子女，我同意父亲使用押金支付相关医疗费用。情况属实，特此确认。',
        signature: '李四',
        createTime: '2025-01-05 09:30:00',
        approver: null,
        approveTime: null,
        approveRemark: null,
        attachments: [
          { name: '医疗诊断证明.pdf', url: '/upload/medical/诊断证明.pdf' },
          { name: '住院通知书.pdf', url: '/upload/medical/住院通知.pdf' },
          { name: '费用估算单.pdf', url: '/upload/medical/费用估算.pdf' }
        ]
      };
      resolve({
        code: 200,
        msg: '查询成功',
        data: mockData
      });
    }, 200);
  });
}

// 新增押金使用申请
export function addDepositUse(data) {
  return request({
    url: '/pension/deposit/apply',
    method: 'post',
    data: data
  })
}

// 修改押金使用申请
export function updateDepositUse(data) {
  return request({
    url: '/pension/deposit/apply',
    method: 'put',
    data: data
  })
}

// 提交押金使用申请(从草稿到待家属审批)
export function submitDepositUse(applyId) {
  return request({
    url: '/pension/deposit/apply/submit/' + applyId,
    method: 'put'
  })
}

// 撤回押金使用申请
export function withdrawDepositUse(applyId) {
  return request({
    url: '/pension/deposit/apply/withdraw/' + applyId,
    method: 'put'
  })
}

// 家属审批押金使用申请
export function familyApproveDepositUse(applyId, data) {
  return request({
    url: '/pension/deposit/apply/familyApprove/' + applyId,
    method: 'put',
    data: data
  })
}

// 监管部门审批押金使用申请
export function supervisionApproveDepositUse(applyId, data) {
  return request({
    url: '/pension/deposit/apply/supervisionApprove/' + applyId,
    method: 'put',
    data: data
  })
}

// 删除押金使用申请
export function delDepositUse(applyId) {
  return request({
    url: '/pension/deposit/apply/' + applyId,
    method: 'delete'
  })
}

// 押金拨付
export function paymentDepositUse(data) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        msg: '拨付成功',
        data: {
          paymentId: 'PAY' + Date.now(),
          paymentTime: new Date().toLocaleString()
        }
      });
    }, 500);
  });
}

// 导出押金使用申请
export function exportDepositUse(query) {
  return request({
    url: '/elder/deposit/export',
    method: 'get',
    params: query
  })
}