package com.zeal.softwareengineeringprojectmanage.controller;

import com.zeal.softwareengineeringprojectmanage.bean.Department;
import com.zeal.softwareengineeringprojectmanage.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

@RestController
public class DeptController {
    @Autowired
    DeptService deptService;

    @RequestMapping("/selectDept/{id}")
    public Department getDepartment(@PathVariable("id") Integer id){
        return deptService.selectByPrimaryKey(id);
    }

    @RequestMapping("/insertDept")
    public String insertDept(Department department){
        deptService.insert(department);
        return "success";

    }
    @GetMapping("/updataDept")
    public Department updataDept(Department department){
        Department department1 = deptService.updataDept(department);
        return department1;
    }

    @GetMapping("/deleteDept")
    public String deleteDept(Integer id){
        deptService.deletDept(id);
        return "success";
    }
}
