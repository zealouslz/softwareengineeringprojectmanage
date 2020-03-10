package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Employee;
import com.zeal.softwareengineeringprojectmanage.mapper.EmployeeMapper;
import com.zeal.softwareengineeringprojectmanage.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
@Service
public class EmpServiceImpl implements EmpService {
    @Autowired
    EmployeeMapper employeeMapper;

    @Cacheable(value = "emp")
    @Override
    public Collection<Employee> getAll() {
        Collection<Employee> employees = employeeMapper.selectAll();
        return employees;
    }

    @Override
    public void insert(Employee employee) {
        employeeMapper.insert(employee);
    }
    @Cacheable(value = "emp")
    @Override
    public Employee selectByPrimaryKey(Integer id) {
        Employee employee = employeeMapper.selectByPrimaryKey(id);
        return employee;
    }

    @Override
    public void deleteEmp(Integer id) {
        employeeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updataEmp(Employee employee) {
        employeeMapper.updateByPrimaryKey(employee);
    }
}
