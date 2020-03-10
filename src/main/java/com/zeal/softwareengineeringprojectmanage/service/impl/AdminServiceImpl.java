package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Admin;
import com.zeal.softwareengineeringprojectmanage.mapper.AdminMapper;
import com.zeal.softwareengineeringprojectmanage.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminMapper adminMapper;
    @Override
    public Admin selectByAdminId(Integer id) {
        Admin admin = adminMapper.selectByAdminId(id);
        return admin;
    }
}
