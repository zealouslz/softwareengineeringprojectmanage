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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class StudentController {
    @Autowired
    TopicService topicService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    StudentService studentService;

    @RequestMapping("/choosetopic")
    public String chooseTopic(Integer stuId, Integer page,Model model){
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(5);
        Student student = studentService.selectByPrimaryKey(stuId);
        List<Topic> topicsAll = topicService.selectByTeaIdAddChooseDeadline(student.getTeaid());
        p.setTotalUsers(topicsAll.size());
        List<Topic> topics = topicService.selectByTeaIdAddChooseDeadlineAndPage(student.getTeaid(), (page - 1) * p.getPageSize(), p.getPageSize());
        List<Integer> allCanChoosedTopicId=new ArrayList<>();
        List<Integer> allStuHaveChoosedTopicId= new ArrayList<>();
        for(Topic topic:topics){
            allCanChoosedTopicId.add(topic.getId());
        }
        List<ChoosedTopic> choosedTopics = studentService.haveChoosedTopic();
        for(ChoosedTopic choosedTopic:choosedTopics){
            allStuHaveChoosedTopicId.add(choosedTopic.getTopicId());
        }

        for (Integer i:allCanChoosedTopicId){
            if(!allStuHaveChoosedTopicId.contains(i)){
                ChoosedTopic choosedTopic = new ChoosedTopic();
                choosedTopic.setTopicId(i);
                choosedTopic.setCount(0);
                choosedTopics.add(choosedTopic);
            }
        }
        Teacher teacher = teacherService.selectByPrimayKey(student.getTeaid());
        model.addAttribute("topics",topics);
        model.addAttribute("choosedTopics",choosedTopics);
        model.addAttribute("teacher",teacher);
        model.addAttribute("page",p);
        return "student/chooseTopic";
    }

    @RequestMapping("/yourtopic")
    public String yourTopic(Integer stuId,String isChooseSuccess, Model model){
        Student stu = studentService.selectByPrimaryKey(stuId);
        if(stu.getTopicid()==null){
            return "redirect:/choosetopic?stuId="+stu.getId()+"&page=1";
        }
        Topic topic = topicService.selectByPrimaryKey(stu.getTopicid());
        Teacher teacher = teacherService.selectByPrimayKey(stu.getTeaid());
        model.addAttribute("topic",topic);
        model.addAttribute("teacher",teacher);
        model.addAttribute("student",stu);
        model.addAttribute("isChooseSuccess",isChooseSuccess);
        return "student/haveChoosedTopic";
    }

    @RequestMapping("/confirmChooseTopic")
    public String confirmChooseTopic(Integer topicId, Integer stuId) throws UnsupportedEncodingException {
        int i = studentService.updateTopicIdByStuId(topicId, stuId);
        Topic topic = topicService.selectByPrimaryKey(topicId);
        String isChooseSuccess="成功选择"+topic.getTopicname()+"!题号："+topicId;
        return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+URLEncoder.encode(isChooseSuccess,"UTF-8");
    }
}
