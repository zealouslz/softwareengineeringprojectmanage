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
    @Autowired
    BlocktaskService blocktaskService;
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
        List<Stagetopic> stagetopics1 = stagetopicService.selectByTeaId(student.getTeaid());
        p.setTotalUsers(stagetopics1.size());
        List<Stagetopic> stagetopics = stagetopicService.selectByTeaIdAndPage(student.getTeaid(), (page - 1) * p.getPageSize(), p.getPageSize());
        List<Integer> stagetopicsIds =new ArrayList<>();
        for(Stagetopic stagetopic:stagetopics1){
            stagetopicsIds.add(stagetopic.getId());
        }
        List<Stagetopicresult> stagetopicresults = stagetopicresultService.selectByTopicId(student.getTopicid());
        List<Integer> resultstagetopicsIds=new ArrayList<>();
        for (Stagetopicresult stagetopicresult:stagetopicresults){
            resultstagetopicsIds.add(stagetopicresult.getStagetopicid());
        }
        for (Integer id:stagetopicsIds){
            if(!resultstagetopicsIds.contains(id)){
                Stagetopicresult stagetopicresult = new Stagetopicresult();
                stagetopicresult.setStagetopicid(id);
                stagetopicresult.setTopicid(student.getTopicid());
                stagetopicresult.setIspass(null);
                stagetopicresult.setDownloadlink("");
                stagetopicresults.add(stagetopicresult);
            }
        }
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
                model.addAttribute("stuId",stuId);
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
                model.addAttribute("stuId",stuId);
                return  "student/uploadStageTopic";
            }
            int insert = stagetopicresultService.insert(stagetopicresult);
            if(insert>0){
                String isUpdateSuccess="成功提交";
                System.out.println(stuId);
                return "redirect:/stageTopic?stuId="+stuId+"&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
            }else {
                model.addAttribute("IsSuccess","提交失败，请重新尝试!");
                Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(stagetopicid);
                Topic topic = topicService.selectByPrimaryKey(topicid);
                model.addAttribute("stagetopic",stagetopic);
                model.addAttribute("topic",topic);
                model.addAttribute("stuId",stuId);
                return  "student/uploadStageTopic";
            }
            }
        }else {
            model.addAttribute("IsSuccess","文件不能为空!");
            Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(stagetopicid);
            Topic topic = topicService.selectByPrimaryKey(topicid);
            model.addAttribute("stagetopic",stagetopic);
            model.addAttribute("topic",topic);
            model.addAttribute("stuId",stuId);
            return  "student/uploadStageTopic";
        }
    }

    @RequestMapping("/changeStageResultFile")
    public String changeStageResultFile(Integer stagetopicresultId,Integer stuId,String isSuccess,Model model){
        Stagetopicresult stagetopicresult = stagetopicresultService.selectByPrimaryKey(stagetopicresultId);
        Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(stagetopicresult.getStagetopicid());
        Topic topic = topicService.selectByPrimaryKey(stagetopicresult.getTopicid());
        model.addAttribute("stagetopicresult",stagetopicresult);
        model.addAttribute("stagetopic",stagetopic);
        model.addAttribute("topic",topic);
        model.addAttribute("isSuccess",isSuccess);
        model.addAttribute("stuId",stuId);
        return "student/changeStageResult";
    }

    @RequestMapping("/changeStageFile")
    public String changeStageFile(Integer stagetopicresultid,Integer stuId,MultipartFile file) throws UnsupportedEncodingException {
        if(file.isEmpty()){
            String isSuccess="文件为空，请重新选择！";
            return "redirect:/changeStageResultFile?stagetopicresultId="+stagetopicresultid+"&stuId="+stuId+"&isSuccess="+URLEncoder.encode(isSuccess,"UTF-8");
        }else {
            Stagetopicresult stagetopicresult = stagetopicresultService.selectByPrimaryKey(stagetopicresultid);
            File f=new File(stagetopicresult.getDownloadlink());
            if(f.exists()){
                f.delete();
            }
            File dir = new File(stageTopicResultPath);
            if(!dir.exists()) {
                dir.mkdir();
            }
            String path = stageTopicResultPath + file.getOriginalFilename();
            Date date=new Date();
            stagetopicresult.setSubmittime(date);
            stagetopicresult.setDownloadlink(path);
            File tempFile =  tempFile =  new File(path);
            if(tempFile.exists()){
                String isSuccess="文件已存在，请重新选择！";
                return "redirect:/changeStageResultFile?stagetopicresultId="+stagetopicresultid+"&stuId="+stuId+"&isSuccess="+URLEncoder.encode(isSuccess,"UTF-8");
            }else {
                try {
                    FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    String isSuccess = "文件上传失败";
                    return "redirect:/changeStageResultFile?stagetopicresultId="+stagetopicresultid+"&stuId="+stuId+"&isSuccess="+URLEncoder.encode(isSuccess,"UTF-8");
                }
                int i = stagetopicresultService.updateByPrimaryKey(stagetopicresult);
                if(i>0) {
                    String isUpdateSuccess="成功重新提交";
                    return "redirect:/stageTopic?stuId="+stuId+"&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
                }else {
                    String isSuccess = "重新提交失败，请重新尝试！";
                    return "redirect:/changeStageResultFile?stagetopicresultId="+stagetopicresultid+"&stuId="+stuId+"&isSuccess="+URLEncoder.encode(isSuccess,"UTF-8");
                }
            }
        }
    }

    @RequestMapping("/reUploadStage")
    public String reUploadStage(Integer stagetopicresultId,Integer stuId,String isSuccess,Model model){
        Stagetopicresult stagetopicresult = stagetopicresultService.selectByPrimaryKey(stagetopicresultId);
        Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(stagetopicresult.getStagetopicid());
        Topic topic = topicService.selectByPrimaryKey(stagetopicresult.getTopicid());
        model.addAttribute("stagetopicresult",stagetopicresult);
        model.addAttribute("stagetopic",stagetopic);
        model.addAttribute("topic",topic);
        model.addAttribute("isSuccess",isSuccess);
        model.addAttribute("stuId",stuId);
        return "student/reUploadStage";
    }

    @RequestMapping("/reUploadStageFile")
    public String reUploadStageFile(Integer stagetopicresultid,Integer stuId,MultipartFile file) throws UnsupportedEncodingException {
        if (file.isEmpty()) {
            String isSuccess = "文件为空，请重新选择！";
            return "redirect:/changeStageResultFile?stagetopicresultId=" + stagetopicresultid + "&stuId=" + stuId + "&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
        } else {
            Stagetopicresult stagetopicresult = stagetopicresultService.selectByPrimaryKey(stagetopicresultid);
            File f = new File(stagetopicresult.getDownloadlink());
            if (f.exists()) {
                f.delete();
            }
            File dir = new File(stageTopicResultPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String path = stageTopicResultPath + file.getOriginalFilename();
            Date date = new Date();
            stagetopicresult.setSubmittime(date);
            stagetopicresult.setDownloadlink(path);
            Integer integer = new Integer(2);
            stagetopicresult.setIspass(integer.byteValue());
            File tempFile = tempFile = new File(path);
            if (tempFile.exists()) {
                String isSuccess = "文件已存在，请重新选择！";
                return "redirect:/changeStageResultFile?stagetopicresultId=" + stagetopicresultid + "&stuId=" + stuId + "&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
            } else {
                try {
                    FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    String isSuccess = "文件上传失败";
                    return "redirect:/changeStageResultFile?stagetopicresultId=" + stagetopicresultid + "&stuId=" + stuId + "&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
                }
                int i = stagetopicresultService.updateByPrimaryKey(stagetopicresult);
                if (i > 0) {
                    String isUpdateSuccess = "成功重新提交";
                    return "redirect:/stageTopic?stuId=" + stuId + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
                } else {
                    String isSuccess = "重新提交失败，请重新尝试！";
                    return "redirect:/changeStageResultFile?stagetopicresultId=" + stagetopicresultid + "&stuId=" + stuId + "&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
                }
            }
        }
    }

        @RequestMapping("/getStageCheckDetail")
        public String getStageCheckDetail(Integer stagetopicresultId,Integer stuId,String isSuccess,Model model){
            Stagetopicresult stagetopicresult = stagetopicresultService.selectByPrimaryKey(stagetopicresultId);
            Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(stagetopicresult.getStagetopicid());
            Topic topic = topicService.selectByPrimaryKey(stagetopicresult.getTopicid());
            model.addAttribute("stagetopicresult",stagetopicresult);
            model.addAttribute("stagetopic",stagetopic);
            model.addAttribute("topic",topic);
            return "student/getStageCheckDetail";
        }

        @RequestMapping("/blockStageTopicHtml")
        public String blockStageTopicHtml(Integer stuId,Integer page,String isUpdateSuccess,Model model){
            Page p=new Page();
            p.setCurrentPage(page);
            p.setPageSize(5);
            p.setTotalUsers(blocktaskService.selectByGroupLeaderId(stuId).size());
            List<Blocktask> blocktasks = blocktaskService.selectByGroupLeaderIdAndPage(stuId, (page - 1) * p.getPageSize(), p.getPageSize());
            Student groupLeader = studentService.selectByPrimaryKey(stuId);
            Topic topic = topicService.selectByPrimaryKey(groupLeader.getTopicid());
            List<Stagetopic> stagetopics = stagetopicService.selectByTeaId(groupLeader.getTeaid());
            List<Student> students = studentService.selectByGroupId(groupLeader.getGroupid());
            model.addAttribute("blocktasks",blocktasks);
            model.addAttribute("page",p);
            model.addAttribute("groupLeader",groupLeader);
            model.addAttribute("topic",topic);
            model.addAttribute("stagetopics",stagetopics);
            model.addAttribute("students",students);
            model.addAttribute("isUpdateSuccess",isUpdateSuccess);
            return "student/blockStageTopic";
        }

}

