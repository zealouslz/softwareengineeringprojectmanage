package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Department;

import java.util.List;

public interface DeptService {
    Department selectByPrimaryKey(Integer id);
    void insert(Department department);
    Department updataDept(Department department);
    void deletDept(Integer id);
}
