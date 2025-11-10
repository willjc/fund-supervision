import request from '@/utils/request'

// 查询入住人列表
export function listResident(query) {
  return new Promise((resolve) => {
    setTimeout(() => {
      const mockData = [
        {
          residentId: 1,
          elderName: '张三',
          gender: '1',
          age: 75,
          idCard: '110101194901011234',
          phone: '13800138001',
          bedInfo: 'A101-01',
          checkInStatus: '1',
          careLevel: '2',
          serviceBalance: 5680.50,
          depositBalance: 10000.00,
          memberBalance: 2000.00,
          checkInDate: '2024-01-15',
          emergencyContact: '李四(13900139001)',
          address: '北京市朝阳区建国路88号',
          monthlyFee: 3500.00
        },
        {
          residentId: 2,
          elderName: '李四',
          gender: '2',
          age: 82,
          idCard: '110101194201012345',
          phone: '13800138002',
          bedInfo: 'A101-02',
          checkInStatus: '1',
          careLevel: '3',
          serviceBalance: 1250.80,
          depositBalance: 10000.00,
          memberBalance: 500.00,
          checkInDate: '2024-02-20',
          emergencyContact: '王五(13900139002)',
          address: '北京市海淀区中关村大街1号',
          monthlyFee: 4200.00
        },
        {
          residentId: 3,
          elderName: '王五',
          gender: '1',
          age: 68,
          idCard: '110101195601011234',
          phone: '13800138003',
          bedInfo: 'B201-01',
          checkInStatus: '1',
          careLevel: '1',
          serviceBalance: 8900.00,
          depositBalance: 8000.00,
          memberBalance: 3500.00,
          checkInDate: '2024-03-10',
          emergencyContact: '赵六(13900139003)',
          address: '北京市西城区金融街25号',
          monthlyFee: 2800.00
        },
        {
          residentId: 4,
          elderName: '赵六',
          gender: '1',
          age: 79,
          idCard: '110101194501011234',
          phone: '13800138004',
          bedInfo: 'B201-02',
          checkInStatus: '3',
          careLevel: '2',
          serviceBalance: 450.30,
          depositBalance: 10000.00,
          memberBalance: 0.00,
          checkInDate: '2023-12-05',
          emergencyContact: '孙七(13900139004)',
          address: '北京市东城区王府井大街255号',
          monthlyFee: 3200.00
        },
        {
          residentId: 5,
          elderName: '孙七',
          gender: '2',
          age: 85,
          idCard: '110101193901011234',
          phone: '13800138005',
          bedInfo: 'C301-01',
          checkInStatus: '2',
          careLevel: '3',
          serviceBalance: 0.00,
          depositBalance: 0.00,
          memberBalance: 800.00,
          checkInDate: '2023-10-15',
          emergencyContact: '周八(13900139005)',
          address: '北京市丰台区南三环西路16号',
          monthlyFee: 4500.00
        }
      ];

      // 简单的过滤逻辑
      let filteredData = mockData;
      if (query.elderName) {
        filteredData = filteredData.filter(item => item.elderName.includes(query.elderName));
      }
      if (query.gender) {
        filteredData = filteredData.filter(item => item.gender === query.gender);
      }
      if (query.roomNumber) {
        filteredData = filteredData.filter(item => item.bedInfo.includes(query.roomNumber));
      }
      if (query.checkInStatus) {
        filteredData = filteredData.filter(item => item.checkInStatus === query.checkInStatus);
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

// 查询入住人详细
export function getResident(residentId) {
  return new Promise((resolve) => {
    setTimeout(() => {
      const mockData = {
        residentId: residentId,
        elderName: '张三',
        gender: '1',
        age: 75,
        idCard: '110101194901011234',
        phone: '13800138001',
        bedInfo: 'A101-01',
        checkInStatus: '1',
        careLevel: '2',
        serviceBalance: 5680.50,
        depositBalance: 10000.00,
        memberBalance: 2000.00,
        checkInDate: '2024-01-15',
        emergencyContact: '李四(13900139001)',
        address: '北京市朝阳区建国路88号',
        monthlyFee: 3500.00,
        emergencyName: '李四',
        emergencyPhone: '13900139001',
        emergencyRelation: '子女',
        healthStatus: '健康状况良好，高血压控制稳定',
        specialNeeds: '需要定期测量血压，饮食清淡',
        photoPath: '/upload/elder/photo/001.jpg',
        createTime: '2024-01-15 09:00:00',
        updateTime: '2024-11-04 10:30:00',
        remark: '性格开朗，喜欢下棋'
      };
      resolve({
        code: 200,
        msg: '查询成功',
        data: mockData
      });
    }, 200);
  });
}

// 删除入住人
export function delResident(residentId) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        msg: '删除成功'
      });
    }, 300);
  });
}

// 入住人续费
export function renewResident(data) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        msg: '续费成功',
        data: {
          transactionId: 'TXN' + Date.now(),
          amount: data.amount,
          paymentMethod: data.paymentMethod,
          renewType: data.renewType
        }
      });
    }, 500);
  });
}

// 入住人退费
export function refundResident(data) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        msg: '退费成功',
        data: {
          refundId: 'REF' + Date.now(),
          amount: data.amount,
          refundType: data.refundType,
          reason: data.reason
        }
      });
    }, 500);
  });
}

// 押金使用申请
export function applyDepositUse(data) {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        msg: '申请提交成功',
        data: {
          applyId: 'DEP' + Date.now(),
          amount: data.amount,
          purpose: data.purpose,
          status: '待审批'
        }
      });
    }, 500);
  });
}

// 导出入住人
export function exportResident(query) {
  return request({
    url: '/elder/resident/export',
    method: 'get',
    params: query
  })
}

// 下载导入模板
export function downloadTemplate() {
  return request({
    url: '/elder/resident/template',
    method: 'get'
  })
}

// 批量导入入住人
export function importResident(data) {
  return request({
    url: '/elder/resident/import',
    method: 'post',
    data: data
  })
}