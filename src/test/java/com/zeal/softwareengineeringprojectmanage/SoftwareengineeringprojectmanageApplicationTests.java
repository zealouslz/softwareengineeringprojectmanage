package com.zeal.softwareengineeringprojectmanage;

import com.zeal.softwareengineeringprojectmanage.bean.Admin;
import com.zeal.softwareengineeringprojectmanage.bean.Student;
import com.zeal.softwareengineeringprojectmanage.bean.Teacher;
import com.zeal.softwareengineeringprojectmanage.service.AdminService;
import com.zeal.softwareengineeringprojectmanage.service.StudentService;
import com.zeal.softwareengineeringprojectmanage.service.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SoftwareengineeringprojectmanageApplicationTests {

    @Autowired
    StudentService studentService;
    @Autowired
    AdminService adminService;
    @Autowired
    TeacherService teacherService;
    @Test
    void contextLoads() {
//        Admin admin = adminService.selectByAdminId(12);
//        System.out.println(admin);
//        Student student = studentService.selectByStuId(2016123297);
//        System.out.println(student);
        Teacher teacher = teacherService.selectByTeaId(52);
        if (teacher==null){
            System.out.println(teacher+"不存在");
        }
        System.out.println(teacher);
    }

}
