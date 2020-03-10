package com.zeal.softwareengineeringprojectmanage.controller;

import com.zeal.softwareengineeringprojectmanage.bean.Department;
import com.zeal.softwareengineeringprojectmanage.bean.Employee;
import com.zeal.softwareengineeringprojectmanage.service.DeptService;
import com.zeal.softwareengineeringprojectmanage.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
public class EmployeController {

    @Autowired
    EmpService empService;
    @Autowired
    DeptService deptService;
    //查询所有员工返回列表页面
    @GetMapping("/emps")
    public String list(Model model){
        //thymeleof默认就会拼串
        //classpath:/templates/xxx.html
        Collection<Employee> employees = empService.getAll();
        model.addAttribute("emps",employees);
        return "emp/list";
    }

    @GetMapping("/emp")
    public String toAddPage(Model model){
        //来到添加页面,查出所有部门，在页面中显示
        Collection<Department> departments = deptService.selectAll();
        model.addAttribute("depts",departments);
        return "emp/add";
    }

    //员工添加
    //SpringMVC自动将请求参数和对象的属性进行一一绑定：要求请求参数的名字和javaBean入参对象的属性名一样
    @PostMapping("/emp")
    public String addEmp(Employee employee){

        //来到员工列表页面
        System.out.println("保存员工的信息"+employee);
        //保存员工
        empService.insert(employee);
        //redirect：表示重定向到一个地址 /代表项目路径
        //forward：表示转发到一个地址
        return "redirect:/emps";
    }

    //来到修改页面，查出当前员工，再页面回显
    @GetMapping("/emp/{id}")
    public String toEditPage(@PathVariable("id") Integer id,Model model){
        Employee employee = empService.selectByPrimaryKey(id);
        model.addAttribute("emp",employee);

        //页面要显示所有的部门列表
        Collection<Department> departments = deptService.selectAll();
        model.addAttribute("depts",departments);
        //回到修改页面(add页面是一个修改添加二合一的页面）
        return "emp/add";
    }

    //员工修改
    @PutMapping("/emp")
    public String updataEmployee(Employee employee){
        System.out.println("修改的员工数据"+employee);
        empService.updataEmp(employee);
        return "redirect:/emps";
    }

    //员工删除
    @GetMapping("/emp/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Integer id){
        empService.deleteEmp(id);
        return "redirect:/emps";
    }
}
