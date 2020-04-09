package com.zeal.softwareengineeringprojectmanage.controller;

import com.zeal.softwareengineeringprojectmanage.bean.*;
import com.zeal.softwareengineeringprojectmanage.service.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    StagetopicService stagetopicService;
    @Autowired
    StagetopicresultService stagetopicresultService;

    @Value("${file.uploadStageTopicResultFolder}")
    private String stageTopicResultPath;

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

    @RequestMapping("/stageTopic")
    public String stageTopic(Integer stuId,Integer page,String isUpdateSuccess,Model model){
        Page p = new Page();
        p.setCurrentPage(page);
        p.setPageSize(5);
        Student student = studentService.selectByPrimaryKey(stuId);
        Teacher teacher = teacherService.selectByPrimayKey(student.getTeaid());
        p.setTotalUsers(stagetopicService.selectByTeaId(student.getTeaid()).size());
        List<Stagetopic> stagetopics = stagetopicService.selectByTeaIdAndPage(student.getTeaid(), (page - 1) * p.getPageSize(), p.getPageSize());
        List<Stagetopicresult> stagetopicresults = stagetopicresultService.selectByTopicId(student.getTopicid());
        model.addAttribute("page",p);
        model.addAttribute("stagetopics",stagetopics);
        model.addAttribute("teacher",teacher);
        model.addAttribute("student",student);
        model.addAttribute("stagetopicresults",stagetopicresults);
        model.addAttribute("isUpdateSuccess",isUpdateSuccess);
        return "student/stageTopic";
    }

    @RequestMapping("/uploadStageTopic")
    public String uploadStageTopic(Integer stageTopicId,Integer topicId,Integer stuId,Model model){
        Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(stageTopicId);
        Topic topic = topicService.selectByPrimaryKey(topicId);
        model.addAttribute("stagetopic",stagetopic);
        model.addAttribute("topic",topic);
        model.addAttribute("stuId",stuId);
        return  "student/uploadStageTopic";
    }

    @RequestMapping("uploadStageFile")
    public String uploadStageFile(Integer topicid, Integer stagetopicid,Integer stuId, MultipartFile file,Model model) throws UnsupportedEncodingException {

        if(!file.isEmpty()){
            Stagetopicresult stagetopicresult=new Stagetopicresult();
            stagetopicresult.setStagetopicid(stagetopicid);
            stagetopicresult.setTopicid(topicid);
            Date date=new Date();
            stagetopicresult.setSubmittime(date);
            File dir = new File(stageTopicResultPath);
            if(!dir.exists()) {
                dir.mkdir();
            }
            String path = stageTopicResultPath + file.getOriginalFilename();
            stagetopicresult.setDownloadlink(path);
            File tempFile = tempFile =  new File(path);
            if(tempFile.exists()){
                model.addAttribute("IsSuccess","文件已存在，请重新上传!");
                Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(stagetopicid);
                Topic topic = topicService.selectByPrimaryKey(topicid);
                model.addAttribute("stagetopic",stagetopic);
                model.addAttribute("topic",topic);
                return  "student/uploadStageTopic";
            }
            else {
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
            }catch (Exception e){
                e.printStackTrace();
                model.addAttribute("IsSuccess","文件上传异常!");
                Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(stagetopicid);
                Topic topic = topicService.selectByPrimaryKey(topicid);
                model.addAttribute("stagetopic",stagetopic);
                model.addAttribute("topic",topic);
                return  "student/uploadStageTopic";
            }
            int insert = stagetopicresultService.insert(stagetopicresult);
            if(insert>0){
                String isUpdateSuccess="成功提交";
                return "redirect:/stageTopic?stuId="+stuId+"&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
            }else {
                model.addAttribute("IsSuccess","提交失败，请重新尝试!");
                Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(stagetopicid);
                Topic topic = topicService.selectByPrimaryKey(topicid);
                model.addAttribute("stagetopic",stagetopic);
                model.addAttribute("topic",topic);
                return  "student/uploadStageTopic";
            }
            }
        }else {
            model.addAttribute("IsSuccess","文件不能为空!");
            Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(stagetopicid);
            Topic topic = topicService.selectByPrimaryKey(topicid);
            model.addAttribute("stagetopic",stagetopic);
            model.addAttribute("topic",topic);
            return  "student/uploadStageTopic";
        }
    }
}
