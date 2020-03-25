package com.zeal.softwareengineeringprojectmanage;

import com.zeal.softwareengineeringprojectmanage.bean.*;
import com.zeal.softwareengineeringprojectmanage.service.AdminService;
import com.zeal.softwareengineeringprojectmanage.service.StudentService;
import com.zeal.softwareengineeringprojectmanage.service.TeacherService;
import com.zeal.softwareengineeringprojectmanage.service.TopicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class SoftwareengineeringprojectmanageApplicationTests {

    @Autowired
    StudentService studentService;
    @Autowired
    AdminService adminService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    TopicService topicService;
    @Test
    void contextLoads() {
//        Admin admin = adminService.selectByAdminId(12);
//        System.out.println(admin);
//        Student student = studentService.selectByStuId(2016123297);
//        System.out.println(student);
        List<Topic> topics = topicService.selectByTeaIdAddChooseDeadline(1);
        System.out.println(topics.toString());

    }

}
