package com.zeal.softwareengineeringprojectmanage.controller;

import com.zeal.softwareengineeringprojectmanage.bean.*;
import com.zeal.softwareengineeringprojectmanage.service.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    OutstandingcaseService outstandingcaseService;
    @Autowired
    ScoreService scoreService;
    @Autowired
    BlocktaskService blocktaskService;
    @Value("${uploadStageTopicResultFile.location}")
    private String uploadStageTopicResultFilePath; //选题文件上传的地址
    @Value("${uploadStageTopicResultFile.resourceHandler}")
    private String uploadStageTopicResultResourceHandler;
    @Value("${uploadBlockTaskFile.location}")
    private String uploadBlockTaskFilePath; //选题文件上传的地址
    @Value("${uploadBlockTaskFile.resourceHandler}")
    private String uploadBlockTaskFileResourceHandler;
    @Value("${uploadBlockTaskResultFile.location}")
    private String uploadBlockTaskResultFilePath; //选题文件上传的地址
    @Value("${uploadBlockTaskResultFile.resourceHandler}")
    private String uploadBlockTaskResultFileResourceHandler;
    @Value("${uploadTopicResultFile.location}")
    private String uploadTopicResultFilePath; //选题文件上传的地址
    @Value("${uploadTopicResultFile.resourceHandler}")
    private String uploadTopicResultResourceHandler;

    Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping("/choosetopic")
    public String chooseTopic(Integer stuId, Integer page,String noTopicId,Model model){
        logger.info("学生进入选题界面");
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(10);
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
        model.addAttribute("noTopicId",noTopicId);
        model.addAttribute("teacher",teacher);
        model.addAttribute("page",p);
        return "student/chooseTopic";
    }

    @RequestMapping("/yourtopic")
    public String yourTopic(Integer stuId,String isChooseSuccess, Model model) throws UnsupportedEncodingException {
        logger.info("学生进入已选的选题信息界面");
        Student stu = studentService.selectByPrimaryKey(stuId);
        if(stu.getTopicid()==null){
            logger.info("还未选题，跳转到选题信息界面");
            return "redirect:/choosetopic?stuId="+stu.getId()+"&page=1&noTopicId="+URLEncoder.encode("你还没有选题，请先选题！","UTF-8");
        }
        Topic topic = topicService.selectByPrimaryKey(stu.getTopicid());
        if(topic.getResult()==null){
            topic.setResult("");
        }
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
        logger.info(isChooseSuccess);
        return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+URLEncoder.encode(isChooseSuccess,"UTF-8");
    }

    @RequestMapping("/stageTopic")
    public String stageTopic(Integer stuId,Integer page,String isUpdateSuccess,Model model){
        logger.info("学生进入阶段性任务界面");
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
        if(student.getTopicid()==null){
            logger.info("学生还未选题，跳转到选题界面");
           return  "redirect:/yourtopic?stuId="+stuId;
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
    public String uploadStageFile(Integer topicid, Integer stagetopicid,Integer stuId, MultipartFile file,Model model,HttpServletRequest req) throws UnsupportedEncodingException {
        logger.info("学生上传阶段性任务结果");
        if(!file.isEmpty()){
            Stagetopicresult stagetopicresult=new Stagetopicresult();
            stagetopicresult.setStagetopicid(stagetopicid);
            stagetopicresult.setTopicid(topicid);
            Date date=new Date();
            stagetopicresult.setSubmittime(date);
            File dir = new File(uploadStageTopicResultFilePath);
            if(!dir.exists()) {
                dir.mkdir();
            }
            String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
            String fileName = file.getOriginalFilename();
            String fileServerPath = basePath + uploadStageTopicResultResourceHandler.substring(0, uploadStageTopicResultResourceHandler.lastIndexOf("/") + 1) + fileName;
            stagetopicresult.setDownloadlink(fileServerPath);
            File tempFile =  new File(uploadStageTopicResultFilePath,fileName);
            if(tempFile.exists()){
                model.addAttribute("IsSuccess","文件已存在，请重新上传!");
                logger.info("上传失败，阶段性任务结果已存在");
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
                logger.info("阶段性结果文件上传异常");
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
                logger.info("学生成功提交阶段性任务结果");
                return "redirect:/stageTopic?stuId="+stuId+"&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
            }else {
                model.addAttribute("IsSuccess","提交失败，请重新尝试!");
                logger.info("阶段性任务结果提交失败");
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
            logger.info("阶段性结果文件为空，上传失败");
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
        logger.info("学生进入修改阶段性任务结果界面");
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
    public String changeStageFile(Integer stagetopicresultid,Integer stuId,MultipartFile file,HttpServletRequest req) throws UnsupportedEncodingException {
        logger.info("学生重新提交阶段性结果文件");
        if(file.isEmpty()){
            String isSuccess="文件为空，请重新选择！";
            logger.info("阶段性结果文件为空，上传失败");
            return "redirect:/changeStageResultFile?stagetopicresultId="+stagetopicresultid+"&stuId="+stuId+"&isSuccess="+URLEncoder.encode(isSuccess,"UTF-8");
        }else {
            Stagetopicresult stagetopicresult = stagetopicresultService.selectByPrimaryKey(stagetopicresultid);
            String oldFileName=stagetopicresult.getDownloadlink().split("/")[5];
            File f=new File(uploadStageTopicResultFilePath,oldFileName);
            if(f.exists()){
                f.delete();
            }
            File dir = new File(uploadStageTopicResultFilePath);
            if(!dir.exists()) {
                dir.mkdir();
            }
            String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
            String fileName = file.getOriginalFilename();
            String fileServerPath = basePath + uploadStageTopicResultResourceHandler.substring(0, uploadStageTopicResultResourceHandler.lastIndexOf("/") + 1) + fileName;
            Date date=new Date();
            stagetopicresult.setSubmittime(date);
            stagetopicresult.setDownloadlink(fileServerPath);
            File tempFile =  new File(uploadStageTopicResultFilePath,fileName);
            if(tempFile.exists()){
                String isSuccess="文件已存在，请重新选择！";
                logger.info("阶段性结果文件已存在，上传失败");
                return "redirect:/changeStageResultFile?stagetopicresultId="+stagetopicresultid+"&stuId="+stuId+"&isSuccess="+URLEncoder.encode(isSuccess,"UTF-8");
            }else {
                try {
                    FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    String isSuccess = "文件上传失败";
                    logger.info("阶段性结果文件上传失败");
                    return "redirect:/changeStageResultFile?stagetopicresultId="+stagetopicresultid+"&stuId="+stuId+"&isSuccess="+URLEncoder.encode(isSuccess,"UTF-8");
                }
                int i = stagetopicresultService.updateByPrimaryKey(stagetopicresult);
                if(i>0) {
                    String isUpdateSuccess="成功重新提交";
                    logger.info("成功重新提交阶段性结果文件");
                    return "redirect:/stageTopic?stuId="+stuId+"&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
                }else {
                    String isSuccess = "重新提交失败，请重新尝试！";
                    logger.info("阶段性结果文件提交失败");
                    return "redirect:/changeStageResultFile?stagetopicresultId="+stagetopicresultid+"&stuId="+stuId+"&isSuccess="+URLEncoder.encode(isSuccess,"UTF-8");
                }
            }
        }
    }

    @RequestMapping("/reUploadStage")
    public String reUploadStage(Integer stagetopicresultId,Integer stuId,String isSuccess,Model model){
        logger.info("组长进入重新提交阶段性任务界面");
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
    public String reUploadStageFile(Integer stagetopicresultid,Integer stuId,MultipartFile file,HttpServletRequest req) throws UnsupportedEncodingException {
        logger.info("组长重新提交阶段性任务文件");
        if (file.isEmpty()) {
            String isSuccess = "文件为空，请重新选择！";
            logger.info("阶段性任务文件为空");
            return "redirect:/changeStageResultFile?stagetopicresultId=" + stagetopicresultid + "&stuId=" + stuId + "&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
        } else {
            Stagetopicresult stagetopicresult = stagetopicresultService.selectByPrimaryKey(stagetopicresultid);
            String oldFileName=stagetopicresult.getDownloadlink().split("/")[5];
            File f = new File(uploadStageTopicResultFilePath,oldFileName);
            if (f.exists()) {
                f.delete();
            }
            File dir = new File(uploadStageTopicResultFilePath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
            String fileName = file.getOriginalFilename();
            String fileServerPath = basePath + uploadStageTopicResultResourceHandler.substring(0, uploadStageTopicResultResourceHandler.lastIndexOf("/") + 1) + fileName;
            Date date = new Date();
            stagetopicresult.setSubmittime(date);
            stagetopicresult.setDownloadlink(fileServerPath);
            Integer integer = new Integer(2);
            stagetopicresult.setIspass(integer.byteValue());
            File tempFile =  new File(uploadStageTopicResultFilePath,fileName);
            if (tempFile.exists()) {
                String isSuccess = "文件已存在，请重新选择！";
                logger.info("阶段性任务文件已存在");
                return "redirect:/changeStageResultFile?stagetopicresultId=" + stagetopicresultid + "&stuId=" + stuId + "&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
            } else {
                try {
                    FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    String isSuccess = "文件上传失败";
                    logger.info(isSuccess);
                    return "redirect:/changeStageResultFile?stagetopicresultId=" + stagetopicresultid + "&stuId=" + stuId + "&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
                }
                int i = stagetopicresultService.updateByPrimaryKey(stagetopicresult);
                if (i > 0) {
                    String isUpdateSuccess = "成功重新提交";
                    logger.info("成功重新提交阶段性任务");
                    return "redirect:/stageTopic?stuId=" + stuId + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
                } else {
                    String isSuccess = "重新提交失败，请重新尝试！";
                    logger.info("重新提交阶段性任务失败");
                    return "redirect:/changeStageResultFile?stagetopicresultId=" + stagetopicresultid + "&stuId=" + stuId + "&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
                }
            }
        }
    }

        @RequestMapping("/getStageCheckDetail")
        public String getStageCheckDetail(Integer stagetopicresultId,Integer stuId,String isSuccess,Model model){
            logger.info("进入阶段性任务审核详情界面");
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
            logger.info("进入分块任务管理界面");
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

        @RequestMapping("/addBlockTopicHtml")
        public String addBlockTopicHtml(Integer stuId,String isSuccess,Model model){
            logger.info("进入添加任务分块界面");
            Student groupLeader = studentService.selectByPrimaryKey(stuId);
            List<Student> students = studentService.selectByGroupId(groupLeader.getGroupid());
            Topic topic = topicService.selectByPrimaryKey(groupLeader.getTopicid());
            List<Stagetopic> stagetopics = stagetopicService.selectByTeaId(groupLeader.getTeaid());
            model.addAttribute("students",students);
            model.addAttribute("topic",topic);
            model.addAttribute("stagetopics",stagetopics);
            model.addAttribute("groupLeader",groupLeader);
            model.addAttribute("isSuccess",isSuccess);
            return "student/addBlockTopic";
        }
        @RequestMapping("/addBlockTopic")
        public String addBlockTopic(Integer topicId,
                                    Integer stageTopic,
                                    Integer groupLeaderId,
                                    String blockTopicName,
                                    String blocktopicdescribe,
                                    Integer stuId,
                                    String releaseTime,
                                    String deadline,
                                    MultipartFile file,Model model,HttpServletRequest req) throws UnsupportedEncodingException, ParseException {
            logger.info("添加分块任务");
        if(!file.isEmpty()){
                Blocktask blocktask=new Blocktask();
                blocktask.setBlockname(blockTopicName);
                blocktask.setBlockdescribe(blocktopicdescribe);
                blocktask.setGroupleaderid(groupLeaderId);
                blocktask.setStuid(stuId);
                blocktask.setStagetopicid(stageTopic);
                blocktask.setTopicid(topicId);
                blocktask.setScore(0);
                String[] str1 = deadline.split("[T]");
                String[] str2 = releaseTime.split("[T]");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date1 = simpleDateFormat.parse(str1[0]+" "+str1[1]);
                Date date2 = simpleDateFormat.parse(str2[0]+" "+str2[1]);
                blocktask.setDeadline(date1);
                blocktask.setReleasetime(date2);
                File dir = new File(uploadBlockTaskFilePath);
                if(!dir.exists()) {
                    dir.mkdir();
                }
            String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
            String fileName = file.getOriginalFilename();
            String fileServerPath = basePath + uploadBlockTaskFileResourceHandler.substring(0, uploadBlockTaskFileResourceHandler.lastIndexOf("/") + 1) + fileName;
                blocktask.setDownloadlink(fileServerPath);
                File tempFile =   new File(uploadBlockTaskFilePath,fileName);
                if(tempFile.exists()){
                    String isSuccess="文件已存在，请重新选择文件";
                    logger.info("分块任务参考文件已存在");
                    return "redirect:/addBlockTopicHtml?stuId="+groupLeaderId+"&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
                }else {
                    try {
                        FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                    }catch (Exception e){
                        e.printStackTrace();
                        String isSuccess="文件上传失败，请重新尝试";
                        logger.info("分块任务参考文件上传失败");
                        return "redirect:/addBlockTopicHtml?stuId="+groupLeaderId+"&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
                    }
                    int insert = blocktaskService.insert(blocktask);
                    if(insert>0) {
                        String isUpdateSuccess = "成功分配" + insert + "任务";
                        logger.info("成功分配" + insert + "条分块任务");
                        return "redirect:/blockStageTopicHtml?stuId=" + groupLeaderId + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
                    }else {
                        String isSuccess="文件上传失败，请重新尝试";
                        logger.info("分块任务参考文件上传失败");
                        return "redirect:/addBlockTopicHtml?stuId="+groupLeaderId+"&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
                    }
                }
            }else {
                String isSuccess="文件为空，上传失败";
                logger.info("分块任务参考文件为空，上传失败");
                return "redirect:/addBlockTopicHtml?stuId="+groupLeaderId+"&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
            }
        }

        @RequestMapping("/getBlockTaskDetail")
        public String getBlockTaskDetail(Integer Id,String isSuccess,Model model){
            logger.info("进入分块任务详情界面");
            Blocktask blocktask = blocktaskService.selectByPrimaryKey(Id);
            Student groupLeader = studentService.selectByPrimaryKey(blocktask.getGroupleaderid());
            List<Student> students = studentService.selectByGroupId(groupLeader.getGroupid());
            Topic topic = topicService.selectByPrimaryKey(blocktask.getTopicid());
            List<Stagetopic> stagetopics = stagetopicService.selectByTeaId(groupLeader.getTeaid());
            model.addAttribute("blocktask",blocktask);
            model.addAttribute("groupLeader",groupLeader);
            model.addAttribute("students",students);
            model.addAttribute("topic",topic);
            model.addAttribute("stagetopics",stagetopics);
            model.addAttribute("isSuccess",isSuccess);
            return "student/updateBlockTask";
        }

        @RequestMapping("/updateBlockTask")
        public String updateBlockTask(Integer blockTaskId,
                                      Integer stageTopic,
                                      Integer topicId,
                                      String blockTopicName,
                                      String blocktopicdescribe,
                                      Integer stuId,
                                      Integer groupLeaderId,
                                      String releaseTime,
                                      String deadline,
                                      String downloadlink,
                                      MultipartFile file,
                                      HttpServletRequest req) throws ParseException, UnsupportedEncodingException {
            logger.info("更新分块任务信息");
        if(file.isEmpty()){
                Blocktask blocktask=new Blocktask();
                blocktask.setId(blockTaskId);
                blocktask.setStagetopicid(stageTopic);
                blocktask.setBlockname(blockTopicName);
                blocktask.setBlockdescribe(blocktopicdescribe);
                blocktask.setStuid(stuId);
                blocktask.setTopicid(topicId);
                blocktask.setDownloadlink(downloadlink);
                blocktask.setGroupleaderid(groupLeaderId);
                String[] str1 = deadline.split("[T]");
                String[] str2 = releaseTime.split("[T]");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date2 = simpleDateFormat.parse(str1[0]+" "+str1[1]);
                Date date3 = simpleDateFormat.parse(str2[0]+" "+str2[1]);
                blocktask.setDeadline(date2);
                blocktask.setReleasetime(date3);
                blocktaskService.deleteByPrimary(blockTaskId);
                int i = blocktaskService.insert(blocktask);
                String isUpdateSuccess = "成功更新" + i + "条任务";
                logger.info("成功更新"+i+"条分块任务");
                return "redirect:/blockStageTopicHtml?stuId=" + groupLeaderId + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
            }
            String oldFileName=downloadlink.split("/")[5];
            File deleteFile = new File(uploadBlockTaskFilePath,oldFileName);
            if(deleteFile!=null){
                //文件不为空，执行删除
                deleteFile.delete();
            }
            Blocktask blocktask=new Blocktask();
            blocktask.setId(blockTaskId);
            blocktask.setStagetopicid(stageTopic);
            blocktask.setBlockname(blockTopicName);
            blocktask.setBlockdescribe(blocktopicdescribe);
            blocktask.setStuid(stuId);
            blocktask.setTopicid(topicId);
            blocktask.setGroupleaderid(groupLeaderId);
            String[] str1 = deadline.split("[T]");
            String[] str2 = releaseTime.split("[T]");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date2 = simpleDateFormat.parse(str1[0]+" "+str1[1]);
            Date date3 = simpleDateFormat.parse(str2[0]+" "+str2[1]);
            blocktask.setDeadline(date2);
            blocktask.setReleasetime(date3);
            File dir = new File(uploadBlockTaskFilePath);
            if(!dir.exists()) {
                dir.mkdir();
            }
            String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
            String fileName = file.getOriginalFilename();
            String fileServerPath = basePath + uploadBlockTaskFileResourceHandler.substring(0, uploadBlockTaskFileResourceHandler.lastIndexOf("/") + 1) + fileName;
            blocktask.setDownloadlink(fileServerPath);
            File tempFile = new File(uploadBlockTaskFilePath,fileName);
            if(tempFile.exists()){
                String isSuccess="文件已存在，请重新选择！";
                logger.info("分块任务参考文件已存在，上传失败");
                return "redirect:/getBlockTaskDetail?Id="+blockTaskId+"&isSuccess="+URLEncoder.encode(isSuccess,"UTF-8");
            }else {
                try {
                    FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    String isSuccess="文件上传失败！";
                    logger.info("分块任务参考文件上传失败");
                    return "redirect:/getBlockTaskDetail?Id="+blockTaskId+"&isSuccess="+URLEncoder.encode(isSuccess,"UTF-8");
                }
                blocktaskService.deleteByPrimary(blockTaskId);
                int i = blocktaskService.insert(blocktask);
                String isUpdateSuccess = "成功更新" + i + "条任务";
                logger.info("成功更新"+i+"条分块任务");
                return "redirect:/blockStageTopicHtml?stuId=" + groupLeaderId + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
            }
        }

        @RequestMapping("/deleteBlockTask")
        @ResponseBody
        public String deleteBlockTask(HttpServletRequest request, HttpServletResponse response) throws JSONException {
            logger.info("删除分块任务");
            int id = Integer.parseInt(request.getParameter("id"));
            int i = blocktaskService.deleteByPrimary(id);
            JSONObject object = new JSONObject();
            if(i>0) {
                object.put("code",1);
                String msg="成功删除了"+i+"条分块任务";
                logger.info(msg);
                object.put("msg",msg);
                return object.toString();
            }else {
                object.put("code",-1);
                object.put("msg","删除失败，请重新尝试！！");
                logger.info("分块任务删除失败");
                return object.toString();
            }
        }

        @RequestMapping("/yourBlockTask")
        public String yourBlockTask(Integer stuId,Integer page,String isUpdateSuccess, Model model) throws UnsupportedEncodingException {
            logger.info("学生进入自己的分块任务界面");
            Page p=new Page();
            p.setCurrentPage(page);
            p.setPageSize(5);
            p.setTotalUsers(blocktaskService.selectByStuId(stuId).size());
            List<Blocktask> blocktasks = blocktaskService.selectByStuIdAndPage(stuId, (page - 1) * p.getPageSize(), p.getPageSize());
            for (Blocktask blocktask:blocktasks){
                if(blocktask.getUploadfile()==null) {
                    blocktask.setUploadfile("");
                }
            }
            Student student = studentService.selectByPrimaryKey(stuId);
            if(student.getGroupid()==null){
                logger.info("暂未分组，等待老师分组");
                return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+URLEncoder.encode("暂未分组，请等待老师分组","UTF-8");

            }
            Student groupLeader = studentService.selectGroupLeader(student.getGroupid());
            Topic topic = topicService.selectByPrimaryKey(student.getTopicid());
            List<Stagetopic> stagetopics = stagetopicService.selectByTeaId(student.getTeaid());
            model.addAttribute("page",p);
            model.addAttribute("blocktasks",blocktasks);
            model.addAttribute("topic",topic);
            model.addAttribute("stagetopics",stagetopics);
            model.addAttribute("student",student);
            model.addAttribute("groupLeader",groupLeader);
            model.addAttribute("isUpdateSuccess",isUpdateSuccess);
            return "student/yourBlockTask";
        }

        @RequestMapping("/uploadBlockTaskFile")
        public String uploadBlockTaskFile(Integer id,Integer stuId,String IsSuccess,Model model){
            logger.info("进入提交分块任务结果文件界面");
            Blocktask blocktask = blocktaskService.selectByPrimaryKey(id);
            Topic topic = topicService.selectByPrimaryKey(blocktask.getTopicid());
            Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(blocktask.getStagetopicid());
            model.addAttribute("blocktask",blocktask);
            model.addAttribute("topic",topic);
            model.addAttribute("stagetopic",stagetopic);
            model.addAttribute("stuId",stuId);
            model.addAttribute("IsSuccess",IsSuccess);
            return "student/uploadBlockTaskResult";
        }

        @RequestMapping("/uploadBlockTask")
        public String uploadBlockTask(Integer blockId,Integer stuId,MultipartFile file,Model model,HttpServletRequest req) throws UnsupportedEncodingException {
            logger.info("提交分块任务结果文件");
            if(!file.isEmpty()){
                File dir = new File(uploadBlockTaskResultFilePath);
                if(!dir.exists()) {
                    dir.mkdir();
                }
                String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
                String fileName = file.getOriginalFilename();
                String fileServerPath = basePath + uploadBlockTaskResultFileResourceHandler.substring(0, uploadBlockTaskResultFileResourceHandler.lastIndexOf("/") + 1) + fileName;
                File tempFile = new File(uploadBlockTaskResultFilePath,fileName);
                if(tempFile.exists()){
                   String IsSuccess="文件已存在，请重新上传!";
                    logger.info("分块任务结果文件已存在");
                   return  "redirect:/uploadBlockTaskFile?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
                }
                else {
                    try {
                        FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                    }catch (Exception e){
                        e.printStackTrace();
                        String IsSuccess="文件上传失败，请重新尝试!";
                        logger.info("分块结果文件上传失败");
                        return  "redirect:/uploadBlockTaskFile?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
                    }
                    int i = blocktaskService.updateUploadFile(blockId,fileServerPath,new Date());
                    if(i>0){
                        String isUpdateSuccess="成功提交";
                        logger.info("成功上传分块任务结果");
                        return "redirect:/yourBlockTask?stuId="+stuId+"&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
                    }else {
                        String IsSuccess="提交失败请重新尝试";
                        logger.info("分块任务结果提交失败");
                        return  "redirect:/uploadBlockTaskFile?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
                    }
                }
            }else {
                String IsSuccess="文件不能为空";
                logger.info("分块任务结果文件为空");
                return  "redirect:/uploadBlockTaskFile?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
            }
        }

        @RequestMapping("/changeBlockTaskFileHtml")
        public String changeBlockTaskFileHtml(Integer id,Integer stuId,String IsSuccess,Model model,HttpServletRequest req){
            logger.info("进入重新提交分块任务结果界面");
            Blocktask blocktask = blocktaskService.selectByPrimaryKey(id);
            Topic topic = topicService.selectByPrimaryKey(blocktask.getTopicid());
            Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(blocktask.getStagetopicid());
            model.addAttribute("blocktask",blocktask);
            model.addAttribute("topic",topic);
            model.addAttribute("stagetopic",stagetopic);
            model.addAttribute("stuId",stuId);
            model.addAttribute("IsSuccess",IsSuccess);
            return "student/changeBlockTaskFile";
        }

        @RequestMapping("/changeBlockTaskFile")
        public String changeBlockTaskFile(Integer blockId,Integer stuId,MultipartFile file,Model model,HttpServletRequest req) throws UnsupportedEncodingException {
            logger.info("重新提交分块任务结果");
        if(file.isEmpty()){
                String IsSuccess="文件为空，请重新选择！";
            logger.info("分块任务结果文件为空");
                return "redirect:/changeBlockTaskFileHtml?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
            }else {
                Blocktask blocktask = blocktaskService.selectByPrimaryKey(blockId);
                String oldFileName=blocktask.getUploadfile().split("/")[5];
                File f=new File(uploadBlockTaskResultFilePath,oldFileName);
                if(f.exists()){
                    f.delete();
                }
                File dir = new File(uploadBlockTaskResultFilePath);
                if(!dir.exists()) {
                    dir.mkdir();
                }
            String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
            String fileName = file.getOriginalFilename();
            String fileServerPath = basePath + uploadBlockTaskResultFileResourceHandler.substring(0, uploadBlockTaskResultFileResourceHandler.lastIndexOf("/") + 1) + fileName;
                File tempFile = new File(uploadBlockTaskResultFilePath,fileName);
                if(tempFile.exists()){
                    String IsSuccess="文件已存在，请重新选择！";
                    logger.info("分块任务结果文件已存在");
                    return "redirect:/changeBlockTaskFileHtml?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
                }else {
                    try {
                        FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String IsSuccess="文件上传失败，请重新尝试！";
                        logger.info("分块任务结果文件重新提交失败");
                        return "redirect:/changeBlockTaskFileHtml?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
                    }
                    int i = blocktaskService.updateUploadFile(blockId, fileServerPath, new Date());
                    if(i>0) {
                        String isUpdateSuccess="成功重新提交";
                        logger.info("分块任务结果文件成功重新提交");
                        return "redirect:/yourBlockTask?stuId="+stuId+"&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
                    }else {
                        String IsSuccess = "重新提交失败，请重新尝试！";
                        logger.info("分块任务结果文件成功重新提交失败");
                        return "redirect:/changeBlockTaskFileHtml?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
                    }
                }
            }
        }

        @RequestMapping("/reUploadBlockTaskFileHtml")
        public String reUploadBlockTaskFileHtml(Integer id,Integer stuId,String IsSuccess,Model model){
            logger.info("进入重新提交分块任务结果界面");
            Blocktask blocktask = blocktaskService.selectByPrimaryKey(id);
            Topic topic = topicService.selectByPrimaryKey(blocktask.getTopicid());
            Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(blocktask.getStagetopicid());
            model.addAttribute("blocktask",blocktask);
            model.addAttribute("topic",topic);
            model.addAttribute("stagetopic",stagetopic);
            model.addAttribute("stuId",stuId);
            model.addAttribute("IsSuccess",IsSuccess);
            return "student/reUploadBlockTaskFile";
        }
        @RequestMapping("/reUploadBlockTaskFile")
        public String reUploadBlockTaskFile(Integer blockId,Integer stuId,MultipartFile file,Model model,HttpServletRequest req) throws UnsupportedEncodingException {
            logger.info("重新提交分块任务结果文件");
           if (file.isEmpty()) {
                String IsSuccess = "文件为空，请重新选择！";
               logger.info("分块任务结果文件为空，重新提交失败");
                return "redirect:/reUploadBlockTaskFileHtml?id=" + blockId + "&stuId=" + stuId + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8");
            } else {
                Blocktask blocktask = blocktaskService.selectByPrimaryKey(blockId);
                String oldFileName=blocktask.getUploadfile().split("/")[5];
                File f = new File(uploadBlockTaskResultFilePath,oldFileName);
                if (f.exists()) {
                    f.delete();
                }
                File dir = new File(uploadBlockTaskResultFilePath);
                if (!dir.exists()) {
                    dir.mkdir();
                }
               String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
               String fileName = file.getOriginalFilename();
               String fileServerPath = basePath + uploadBlockTaskResultFileResourceHandler.substring(0, uploadBlockTaskResultFileResourceHandler.lastIndexOf("/") + 1) + fileName;
                File tempFile =  new File(uploadBlockTaskResultFilePath,fileName);
                if (tempFile.exists()) {
                    String IsSuccess = "文件已存在，请重新选择！";
                    logger.info("分块任务结果文件已存在，重新提交失败");
                    return "redirect:/reUploadBlockTaskFileHtml?id=" + blockId + "&stuId=" + stuId + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8");
                } else {
                    try {
                        FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String IsSuccess = "文件上传失败！";
                        logger.info("分块任务结果文件重新提交失败");
                        return "redirect:/reUploadBlockTaskFileHtml?id=" + blockId + "&stuId=" + stuId + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8");
                    }
                    int i = blocktaskService.updateIsPassAndUploadAndSubTime(blockId,2,new Date(),fileServerPath);
                    if (i > 0) {
                        String isUpdateSuccess="成功提交";
                        logger.info("分块任务结果文件成功重新提交");
                        return "redirect:/yourBlockTask?stuId="+stuId+"&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
                    } else {
                        String IsSuccess = "重新提交失败，请重新尝试！";
                        logger.info("分块任务结果文件重新提交失败");
                        return "redirect:/reUploadBlockTaskFileHtml?id=" + blockId + "&stuId=" + stuId + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8");
                    }
                }
            }
        }

        @RequestMapping("/getBlockTaskCheckDetail")
        public String getBlockTaskCheckDetail(Integer id,Model model){
            logger.info("获取分块任务审核详情");
            Blocktask blocktask = blocktaskService.selectByPrimaryKey(id);
            Topic topic = topicService.selectByPrimaryKey(blocktask.getTopicid());
            Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(blocktask.getStagetopicid());
            model.addAttribute("blocktask",blocktask);
            model.addAttribute("topic",topic);
            model.addAttribute("stagetopic",stagetopic);
            return "student/getBlockTaskCheckDetail";
        }
        @RequestMapping("/auditBlockTaskHtml")
        public String auditBlockTaskHtml(Integer stuId,Integer page,String isUpdateSuccess,Model model){
            logger.info("进入审核分块任务界面");
            Page p=new Page();
            p.setCurrentPage(page);
            p.setPageSize(5);
            p.setTotalUsers(blocktaskService.selectByGroupLeaderId(stuId).size());
            List<Blocktask> blocktasks = blocktaskService.selectByGroupLeaderIdAndPage(stuId, (page - 1) * p.getPageSize(), p.getPageSize());
            for(Blocktask blocktask:blocktasks){
                if(blocktask.getUploadfile()==null){
                    blocktask.setUploadfile("");
                }
            }
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
            return "student/auditBlockTask";
        }

        @RequestMapping("/auditBlockTask")
        public String auditBlockTask(Integer blockId,Model model){
            logger.info("进入审核分块任务详情界面");
            Blocktask blocktask = blocktaskService.selectByPrimaryKey(blockId);
            Topic topic = topicService.selectByPrimaryKey(blocktask.getTopicid());
            Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(blocktask.getStagetopicid());
            Student student = studentService.selectByPrimaryKey(blocktask.getStuid());
            model.addAttribute("blocktask",blocktask);
            model.addAttribute("topic",topic);
            model.addAttribute("stagetopic",stagetopic);
            model.addAttribute("student",student);
            return "student/auditBlockTaskDetail";
        }

        @RequestMapping("/checkBlockTask")
        @ResponseBody
        public String checkBlockTask(HttpServletRequest request,HttpServletResponse response) throws JSONException {
            logger.info("审核分块任务");
            int blockId = Integer.parseInt(request.getParameter("blockId"));
            int ispass = Integer.parseInt(request.getParameter("ispass"));
            int score = Integer.parseInt(request.getParameter("score"));
            String suggestion = request.getParameter("suggestion");
            int i = blocktaskService.updateIsPassAndScoreAndSuggestion(blockId, ispass, score, suggestion);
            JSONObject object = new JSONObject();
            if(i>0) {
                object.put("code",1);
                String msg="成功提交了审核信息";
                logger.info("成功审核了一条分块任务");
                object.put("msg",msg);
                return object.toString();
            }else {
                object.put("code",-1);
                object.put("msg","审核失败，请重新尝试！！");
                logger.info("分块任务审核失败");
                return object.toString();
            }
        }

        @RequestMapping("/uploadResultHtml")
        public String uploadResultHtml(Integer stuId,Model model){
            logger.info("上传选题结果界面");
            Student student = studentService.selectByPrimaryKey(stuId);
            Topic topic = topicService.selectByPrimaryKey(student.getTopicid());
            Teacher teacher = teacherService.selectByPrimayKey(topic.getTeaid());
            model.addAttribute("student",student);
            model.addAttribute("topic",topic);
            model.addAttribute("teacher",teacher);
            return "student/uploadResult";
        }

        @RequestMapping("/uploadResult")
        public String uploadResult(Integer topicId,Integer stuId,MultipartFile file, Model model,HttpServletRequest req) throws UnsupportedEncodingException {
            logger.info("上传选题完成结果");
            if (file.isEmpty()){
               String isChooseSuccess="文件不能为空，请重新选择！";
                logger.info("选题结果页面为空，上传失败");
               return "redirect:/yourtopic?stuId="+stuId+ URLEncoder.encode(isChooseSuccess, "UTF-8");
            }else {
                File dir = new File(uploadTopicResultFilePath);
                if(!dir.exists()) {
                    dir.mkdir();
                }
                String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
                String fileName = file.getOriginalFilename();
                String fileServerPath = basePath + uploadTopicResultResourceHandler.substring(0, uploadTopicResultResourceHandler.lastIndexOf("/") + 1) + fileName;
                File tempFile =   new File(uploadTopicResultFilePath,fileName);
                if(tempFile.exists()){
                    String isChooseSuccess="文件已存在，请重新上传!";
                    logger.info("选题结果文件已存在，上传失败");
                    return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+URLEncoder.encode(isChooseSuccess, "UTF-8");
                }
                else {
                    try {
                        FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                    }catch (Exception e){
                        e.printStackTrace();
                        String isChooseSuccess="文件上传异常!";
                        logger.info("选题结果文件上传异常");
                        return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+ URLEncoder.encode(isChooseSuccess, "UTF-8");
                    }
                    int i = topicService.uploadResult(topicId,fileServerPath,new Date());
                    if(i>0){
                        String isChooseSuccess="成功提交";
                        logger.info("成功提交选题结果");
                        return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+ URLEncoder.encode(isChooseSuccess, "UTF-8");
                    }else {
                        String isChooseSuccess="提交失败，请重新尝试!";
                        logger.info("选题结果提交失败");
                        return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+ URLEncoder.encode(isChooseSuccess, "UTF-8");
                    }
                }
            }
        }

        @RequestMapping("/reUploadResult")
        public String reUploadResult(Integer topicId,Integer stuId,MultipartFile file, Model model,HttpServletRequest req) throws UnsupportedEncodingException {
            logger.info("重新提交选题结果");
            if (file.isEmpty()){
                String isChooseSuccess="文件不能为空，请重新选择！";
                logger.info("选题结果文件为空，提交失败");
                return "redirect:/yourtopic?stuId="+stuId+ URLEncoder.encode(isChooseSuccess, "UTF-8");
            }else {
                Topic topic = topicService.selectByPrimaryKey(topicId);
                String oldFileName=topic.getResult().split("/")[5];
                File f = new File(uploadTopicResultFilePath,oldFileName);
                if (f.exists()) {
                    f.delete();
                }
                File dir = new File(uploadTopicResultFilePath);
                if(!dir.exists()) {
                    dir.mkdir();
                }
                String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
                String fileName = file.getOriginalFilename();
                String fileServerPath = basePath + uploadTopicResultResourceHandler.substring(0, uploadTopicResultResourceHandler.lastIndexOf("/") + 1) + fileName;
                File tempFile =   new File(uploadTopicResultFilePath,fileName);
                if(tempFile.exists()){
                    String isChooseSuccess="文件已存在，请重新上传!";
                    logger.info("选题结果文件已存在");
                    return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+URLEncoder.encode(isChooseSuccess, "UTF-8");
                }
                else {
                    try {
                        FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                    }catch (Exception e){
                        e.printStackTrace();
                        String isChooseSuccess="文件上传异常!";
                        logger.info("选题结果文件上传失败");
                        return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+ URLEncoder.encode(isChooseSuccess, "UTF-8");
                    }
                    int i = topicService.uploadResultAndIspass(topicId,fileServerPath,new Date(),2);
                    if(i>0){
                        String isChooseSuccess="成功重新提交";
                        logger.info("选题结果文件成功重新提交");
                        return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+ URLEncoder.encode(isChooseSuccess, "UTF-8");
                    }else {
                        String isChooseSuccess="提交失败，请重新尝试!";
                        logger.info("选题结果文件重新提交失败");
                        return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+ URLEncoder.encode(isChooseSuccess, "UTF-8");
                    }
                }
            }
        }

        @RequestMapping("/changeResult")
        public String changeResult(Integer topicId,Integer stuId,MultipartFile file, Model model,HttpServletRequest req) throws UnsupportedEncodingException {
            logger.info("替换选题结果文件");
            if (file.isEmpty()){
                String isChooseSuccess="文件不能为空，请重新选择！";
                logger.info("选题结果文件为空");
                return "redirect:/yourtopic?stuId="+stuId+ "&isChooseSuccess="+URLEncoder.encode(isChooseSuccess, "UTF-8");
            }else {
                Topic topic = topicService.selectByPrimaryKey(topicId);
                String oldFileName=topic.getResult().split("/")[5];
                File f = new File(uploadTopicResultFilePath,oldFileName);
                if (f.exists()) {
                    f.delete();
                }
                File dir = new File(uploadTopicResultFilePath);
                if(!dir.exists()) {
                    dir.mkdir();
                }
                String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
                String fileName = file.getOriginalFilename();
                String fileServerPath = basePath + uploadTopicResultResourceHandler.substring(0, uploadTopicResultResourceHandler.lastIndexOf("/") + 1) + fileName;
                File tempFile =  new File(uploadTopicResultFilePath,fileName);
                if(tempFile.exists()){
                    String isChooseSuccess="文件已存在，请重新上传!";
                    logger.info("选题结果文件已存在，上传失败");
                    return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+URLEncoder.encode(isChooseSuccess, "UTF-8");
                }
                else {
                    try {
                        FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                    }catch (Exception e){
                        e.printStackTrace();
                        String isChooseSuccess="文件上传异常!";
                        logger.info("选题结果文件上传异常");
                        return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+ URLEncoder.encode(isChooseSuccess, "UTF-8");
                    }
                    int i = topicService.uploadResult(topicId,fileServerPath,new Date());
                    if(i>0){
                        String isChooseSuccess="成功修改！";
                        logger.info("成功修改选题结果文件");
                        return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+ URLEncoder.encode(isChooseSuccess, "UTF-8");
                    }else {
                        String isChooseSuccess="提交失败，请重新尝试!";
                        logger.info("选题结果文件上传失败");
                        return "redirect:/yourtopic?stuId="+stuId+"&isChooseSuccess="+ URLEncoder.encode(isChooseSuccess, "UTF-8");
                    }
                }
            }
        }

        @RequestMapping("/selectCaseLib")
        public String selectCaseLib(Integer teaId,Integer page,Integer Type,String Keyword, String isUpdateSuccess,Model model){
            logger.info("学生查询优秀案例库");
            if(Type==1){
                logger.info("学生根据teaid查询优秀案例库");
                List<Outstandingcase> outstandingcasesAll = outstandingcaseService.selectByTeaId(teaId);
                Page p=new Page();
                p.setTotalUsers(outstandingcasesAll.size());
                p.setPageSize(5);
                p.setCurrentPage(page);
                int i=0;
                for (Outstandingcase outstandingcase:outstandingcasesAll){
                    i++;
                }
                List<Outstandingcase> outstandingcases = outstandingcaseService.selectByTeaIdAndPage(teaId, (page - 1) * p.getPageSize(), p.getPageSize());
                List<Teacher> teachers = teacherService.selectAll();
                logger.info("成功查询到"+i+"个案例");
                model.addAttribute("isUpdateSuccess","查询到"+i+"个案例");
                model.addAttribute("page",p);
                model.addAttribute("teachers",teachers);
                model.addAttribute("outstandingcases",outstandingcases);
                model.addAttribute("type",Type);
                model.addAttribute("keyword",Keyword);
                model.addAttribute("teaid",teaId);
                return "student/selectCaseLib";
            }if(Type==2) {
                logger.info("学生根据关键词查询优秀案例库");
                List<Outstandingcase> outstandingcasesAll = outstandingcaseService.selectByKeyWord(Keyword);
                Page p=new Page();
                p.setTotalUsers(outstandingcasesAll.size());
                p.setPageSize(5);
                p.setCurrentPage(page);
                int i=0;
                for (Outstandingcase outstandingcase:outstandingcasesAll){
                    i++;
                }
                List<Outstandingcase> outstandingcases = outstandingcaseService.selectByKeyWordAndPage(Keyword, (page - 1) * p.getPageSize(), p.getPageSize());
                List<Teacher> teachers = teacherService.selectAll();
                model.addAttribute("isUpdateSuccess","查询到"+i+"个案例");
                logger.info("成功查询到"+i+"个案例");
                model.addAttribute("page",p);
                model.addAttribute("teachers",teachers);
                model.addAttribute("outstandingcases",outstandingcases);
                model.addAttribute("type",Type);
                model.addAttribute("keyword",Keyword);
                model.addAttribute("teaid","");
                return "student/selectCaseLib";
            }else {
                logger.info("学生查询所有优秀案例库");
                Page p=new Page();
                p.setTotalUsers(outstandingcaseService.selectAll().size());
                p.setPageSize(5);
                p.setCurrentPage(page);
                List<Outstandingcase> outstandingcases = outstandingcaseService.selectAllAndPage((page - 1) * p.getPageSize(), p.getPageSize());
                List<Teacher> teachers = teacherService.selectAll();
                model.addAttribute("isUpdateSuccess",isUpdateSuccess);
                model.addAttribute("page",p);
                model.addAttribute("teachers",teachers);
                model.addAttribute("outstandingcases",outstandingcases);
                model.addAttribute("type",Type);
                model.addAttribute("keyword",Keyword);
                model.addAttribute("teaid","");
                return "student/selectCaseLib";
            }
        }

        @RequestMapping("/stuGetCaseDetail")
        public String stuGetCaseDetail(Integer id,Model model){
            logger.info("学生进入优秀案例详情界面");
            Outstandingcase outstandingcase = outstandingcaseService.selectByPrimaryKey(id);
            Teacher teacher = teacherService.selectByPrimayKey(outstandingcase.getTeaid());
            model.addAttribute("teacher",teacher);
            model.addAttribute("outstandingcase",outstandingcase);
            return "student/stuCaseDetail";
        }

        @RequestMapping("/yourScore")
        public String yourScore(Integer stuId,Model model){
            logger.info("学生查询成绩");
            Score score = scoreService.selectByPrimaryKey(stuId);
            Student student = studentService.selectByPrimaryKey(stuId);
            Teacher teacher = teacherService.selectByPrimayKey(score.getTeaid());
            Topic topic = topicService.selectByPrimaryKey(score.getTopicid());
            model.addAttribute("score",score);
            model.addAttribute("teacher",teacher);
            model.addAttribute("topic",topic);
            model.addAttribute("student",student);
            return "student/yourScore";
        }

        @RequestMapping("/stuChangePassWordHtml")
        public String stuChangePassWordHtml(Integer stuId,Model model){
            logger.info("学生修改密码界面");
            Student student = studentService.selectByPrimaryKey(stuId);
            model.addAttribute("student",student);
            return "student/stuChangePassword";
        }

    @RequestMapping("/stuChangePassword")
    @ResponseBody
    public String stuChangePassword(HttpServletRequest request,HttpServletResponse response) throws JSONException {
        logger.info("学生修改密码");
        int id = Integer.parseInt(request.getParameter("id"));
        int stuId = Integer.parseInt(request.getParameter("stuId"));
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String rePassword = request.getParameter("rePassword");
        Student student = studentService.selectByPrimaryKey(id);
        JSONObject object = new JSONObject();
        if (student.getPassword().equals(oldPassword)){
            if (newPassword.equals(rePassword)){
                int i = studentService.updatePassword(id, newPassword);
                if(i>0){
                    object.put("code",1);
                    String msg="密码修改成功！请重新登录";
                    logger.info("学生修改密码成功");
                    object.put("msg",msg);
                    return object.toString();
                }else {
                    object.put("code",-1);
                    String msg="修改失败，请重新尝试！";
                    logger.info("学生修改密码失败");
                    object.put("msg",msg);
                    return object.toString();
                }
            }else {
                object.put("code",-1);
                String msg="两次输入密码不一致，请重新输入！";
                logger.info("学生输入的两次密码不一致");
                object.put("msg",msg);
                return object.toString();
            }
        }else {
            object.put("code",-1);
            String msg="输入的旧密码不正确";
            object.put("msg",msg);
            return object.toString();
        }
    }
}

