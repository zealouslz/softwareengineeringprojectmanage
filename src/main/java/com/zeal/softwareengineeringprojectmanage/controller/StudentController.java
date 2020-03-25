package com.zeal.softwareengineeringprojectmanage.controller;

import com.zeal.softwareengineeringprojectmanage.bean.*;
import com.zeal.softwareengineeringprojectmanage.service.StudentService;
import com.zeal.softwareengineeringprojectmanage.service.TeacherService;
import com.zeal.softwareengineeringprojectmanage.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class StudentController {
    @Autowired
    TopicService topicService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    StudentService studentService;

    @RequestMapping("/choosetopic")
    public String chooseTopic(Integer stuId, Integer page,String isSuccess,Model model){
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(5);
        Student student = studentService.selectByPrimaryKey(stuId);
        List<Topic> topicsAll = topicService.selectByTeaIdAddChooseDeadline(student.getTeaid());
        p.setTotalUsers(topicsAll.size());
        List<Topic> topics = topicService.selectByTeaIdAddChooseDeadlineAndPage(student.getTeaid(), (page - 1) * p.getPageSize(), p.getPageSize());
        List<ChoosedTopic> choosedTopics = studentService.haveChoosedTopic();
        Teacher teacher = teacherService.selectByPrimayKey(student.getTeaid());
        model.addAttribute("topics",topics);
        model.addAttribute("choosedTopics",choosedTopics);
        model.addAttribute("teacher",teacher);
        model.addAttribute("page",p);
        model.addAttribute("isSuccess",isSuccess);
        return "student/chooseTopic";
    }

    @RequestMapping("/confirmChooseTopic")
    public String confirmChooseTopic(Integer topicId,Integer stuId,Model model){
        int i = studentService.updateTopicIdByStuId(topicId, stuId);
        Topic topic = topicService.selectByPrimaryKey(topicId);
        model.addAttribute("isChooseSuccess","成功选择"+topic.getTopicname()+"!题号："+topicId);
        return null;
    }
}
