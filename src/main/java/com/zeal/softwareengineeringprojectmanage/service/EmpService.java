package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Employee;

import java.util.Collection;

public interface EmpService {
    Collection<Employee> getAll();
     void insert(Employee employee);
     Employee selectByPrimaryKey(Integer id);
     void updataEmp(Employee employee);
     void deleteEmp(Integer id);
}
