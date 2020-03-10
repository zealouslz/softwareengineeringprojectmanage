package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Admin;


public interface AdminService {
    Admin selectByAdminId(Integer id);
}
