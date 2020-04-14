package com.zeal.softwareengineeringprojectmanage.controller;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.zeal.softwareengineeringprojectmanage.bean.*;
import com.zeal.softwareengineeringprojectmanage.service.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.nio.charset.MalformedInputException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    @Value("${file.uploadBlockTaskFolder}")
    private String blockTaskPath;
    @Value("${file.uploadBlockTaskResultFolder}")
    private String blockTaskResultPath;
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

        @RequestMapping("/addBlockTopicHtml")
        public String addBlockTopicHtml(Integer stuId,String isSuccess,Model model){
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
                                    MultipartFile file,Model model) throws UnsupportedEncodingException, ParseException {
            if(!file.isEmpty()){
                Blocktask blocktask=new Blocktask();
                blocktask.setBlockname(blockTopicName);
                blocktask.setBlockdescribe(blocktopicdescribe);
                blocktask.setGroupleaderid(groupLeaderId);
                blocktask.setStuid(stuId);
                blocktask.setStagetopicid(stageTopic);
                blocktask.setTopicid(topicId);
                String[] str1 = deadline.split("[T]");
                String[] str2 = releaseTime.split("[T]");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date1 = simpleDateFormat.parse(str1[0]+" "+str1[1]);
                Date date2 = simpleDateFormat.parse(str2[0]+" "+str2[1]);
                blocktask.setDeadline(date1);
                blocktask.setReleasetime(date2);
                File dir = new File(blockTaskPath);
                if(!dir.exists()) {
                    dir.mkdir();
                }
                String path = blockTaskPath + file.getOriginalFilename();
                blocktask.setDownloadlink(path);
                File tempFile = tempFile =  new File(path);
                if(tempFile.exists()){
                    String isSuccess="文件已存在，请重新选择文件";
                    return "redirect:/addBlockTopicHtml?stuId="+groupLeaderId+"&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
                }else {
                    try {
                        FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                    }catch (Exception e){
                        e.printStackTrace();
                        String isSuccess="文件上传失败，请重新尝试";
                        return "redirect:/addBlockTopicHtml?stuId="+groupLeaderId+"&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
                    }
                    int insert = blocktaskService.insert(blocktask);
                    if(insert>0) {
                        String isUpdateSuccess = "成功分配" + insert + "任务";
                        return "redirect:/blockStageTopicHtml?stuId=" + groupLeaderId + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
                    }else {
                        String isSuccess="文件上传失败，请重新尝试";
                        return "redirect:/addBlockTopicHtml?stuId="+groupLeaderId+"&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
                    }
                }
            }else {
                String isSuccess="文件为空，上传失败";
                return "redirect:/addBlockTopicHtml?stuId="+groupLeaderId+"&isSuccess=" + URLEncoder.encode(isSuccess, "UTF-8");
            }
        }

        @RequestMapping("/getBlockTaskDetail")
        public String getBlockTaskDetail(Integer Id,String isSuccess,Model model){
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
                                      MultipartFile file) throws ParseException, UnsupportedEncodingException {
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
                int i = blocktaskService.updateByPrimaryKey(blocktask);
                String isUpdateSuccess = "成功更新" + i + "条任务";
                return "redirect:/blockStageTopicHtml?stuId=" + groupLeaderId + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
            }
            File deleteFile = new File(downloadlink);
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
            File dir = new File(blockTaskPath);
            if(!dir.exists()) {
                dir.mkdir();
            }
            String path = blockTaskPath + file.getOriginalFilename();
            blocktask.setDownloadlink(path);
            File tempFile =  tempFile =  new File(path);
            if(tempFile.exists()){
                String isSuccess="文件已存在，请重新选择！";
                return "redirect:/getBlockTaskDetail?Id="+blockTaskId+"&isSuccess="+URLEncoder.encode(isSuccess,"UTF-8");
            }else {
                try {
                    FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    String isSuccess="文件上传失败！";
                    return "redirect:/getBlockTaskDetail?Id="+blockTaskId+"&isSuccess="+URLEncoder.encode(isSuccess,"UTF-8");
                }
                int i = blocktaskService.updateByPrimaryKey(blocktask);
                String isUpdateSuccess = "成功更新" + i + "条任务";
                return "redirect:/blockStageTopicHtml?stuId=" + groupLeaderId + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
            }
        }

        @RequestMapping("/deleteBlockTask")
        @ResponseBody
        public String deleteBlockTask(HttpServletRequest request, HttpServletResponse response) throws JSONException {
            int id = Integer.parseInt(request.getParameter("id"));
            int i = blocktaskService.deleteByPrimary(id);
            JSONObject object = new JSONObject();
            if(i>0) {
                object.put("code",1);
                String msg="成功删除了"+i+"条选题";
                object.put("msg",msg);
                return object.toString();
            }else {
                object.put("code",-1);
                object.put("msg","删除失败，请重新尝试！！");
                return object.toString();
            }
        }

        @RequestMapping("/yourBlockTask")
        public String yourBlockTask(Integer stuId,Integer page,String isUpdateSuccess, Model model){
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
        public String uploadBlockTask(Integer blockId,Integer stuId,MultipartFile file,Model model) throws UnsupportedEncodingException {
            if(!file.isEmpty()){

                File dir = new File(blockTaskResultPath);
                if(!dir.exists()) {
                    dir.mkdir();
                }
                String path = blockTaskResultPath + file.getOriginalFilename();
                File tempFile = tempFile =  new File(path);
                if(tempFile.exists()){
                   String IsSuccess="文件已存在，请重新上传!";
                   return  "redirect:/uploadBlockTaskFile?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
                }
                else {
                    try {
                        FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                    }catch (Exception e){
                        e.printStackTrace();
                        String IsSuccess="文件上传失败，请重新尝试!";
                        return  "redirect:/uploadBlockTaskFile?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
                    }
                    int i = blocktaskService.updateUploadFile(blockId,path,new Date());
                    if(i>0){
                        String isUpdateSuccess="成功提交";
                        return "redirect:/yourBlockTask?stuId="+stuId+"&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
                    }else {
                        String IsSuccess="提交失败请重新尝试";
                        return  "redirect:/uploadBlockTaskFile?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
                    }
                }
            }else {
                String IsSuccess="文件不能为空";
                return  "redirect:/uploadBlockTaskFile?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
            }
        }

        @RequestMapping("/changeBlockTaskFileHtml")
        public String changeBlockTaskFileHtml(Integer id,Integer stuId,String IsSuccess,Model model){
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
        public String changeBlockTaskFile(Integer blockId,Integer stuId,MultipartFile file,Model model) throws UnsupportedEncodingException {
            if(file.isEmpty()){
                String IsSuccess="文件为空，请重新选择！";
                return "redirect:/changeBlockTaskFileHtml?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
            }else {
                Blocktask blocktask = blocktaskService.selectByPrimaryKey(blockId);
                File f=new File(blocktask.getUploadfile());
                if(f.exists()){
                    f.delete();
                }
                File dir = new File(blockTaskResultPath);
                if(!dir.exists()) {
                    dir.mkdir();
                }
                String path = blockTaskResultPath + file.getOriginalFilename();
                File tempFile =  tempFile =  new File(path);
                if(tempFile.exists()){
                    String IsSuccess="文件已存在，请重新选择！";
                    return "redirect:/changeBlockTaskFileHtml?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
                }else {
                    try {
                        FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String IsSuccess="文件上传失败，请重新尝试！";
                        return "redirect:/changeBlockTaskFileHtml?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
                    }
                    int i = blocktaskService.updateUploadFile(blockId, path, new Date());
                    if(i>0) {
                        String isUpdateSuccess="成功重新提交";
                        return "redirect:/yourBlockTask?stuId="+stuId+"&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
                    }else {
                        String IsSuccess = "重新提交失败，请重新尝试！";
                        return "redirect:/changeBlockTaskFileHtml?id="+blockId+"&stuId="+stuId+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
                    }
                }
            }
        }

        @RequestMapping("/reUploadBlockTaskFileHtml")
        public String reUploadBlockTaskFileHtml(Integer id,Integer stuId,String IsSuccess,Model model){
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
        public String reUploadBlockTaskFile(Integer blockId,Integer stuId,MultipartFile file,Model model) throws UnsupportedEncodingException {
            if (file.isEmpty()) {
                String IsSuccess = "文件为空，请重新选择！";
                return "redirect:/reUploadBlockTaskFileHtml?id=" + blockId + "&stuId=" + stuId + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8");
            } else {
                Blocktask blocktask = blocktaskService.selectByPrimaryKey(blockId);
                File f = new File(blocktask.getUploadfile());
                if (f.exists()) {
                    f.delete();
                }
                File dir = new File(blockTaskResultPath);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                String path = blockTaskResultPath + file.getOriginalFilename();
                File tempFile = tempFile = new File(path);
                if (tempFile.exists()) {
                    String IsSuccess = "文件已存在，请重新选择！";
                    return "redirect:/reUploadBlockTaskFileHtml?id=" + blockId + "&stuId=" + stuId + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8");
                } else {
                    try {
                        FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String IsSuccess = "文件上传失败！";
                        return "redirect:/reUploadBlockTaskFileHtml?id=" + blockId + "&stuId=" + stuId + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8");
                    }
                    int i = blocktaskService.updateIsPassAndUploadAndSubTime(blockId,2,new Date(),path);
                    if (i > 0) {
                        String isUpdateSuccess="成功提交";
                        return "redirect:/yourBlockTask?stuId="+stuId+"&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
                    } else {
                        String IsSuccess = "重新提交失败，请重新尝试！";
                        return "redirect:/reUploadBlockTaskFileHtml?id=" + blockId + "&stuId=" + stuId + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8");
                    }
                }
            }
        }

        @RequestMapping("/getBlockTaskCheckDetail")
        public String getBlockTaskCheckDetail(Integer id,Model model){
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
            int blockId = Integer.parseInt(request.getParameter("blockId"));
            int ispass = Integer.parseInt(request.getParameter("ispass"));
            int score = Integer.parseInt(request.getParameter("score"));
            String suggestion = request.getParameter("suggestion");
            int i = blocktaskService.updateIsPassAndScoreAndSuggestion(blockId, ispass, score, suggestion);
            JSONObject object = new JSONObject();
            if(i>0) {
                object.put("code",1);
                String msg="成功提交了审核信息";
                object.put("msg",msg);
                return object.toString();
            }else {
                object.put("code",-1);
                object.put("msg","审核失败，请重新尝试！！");
                return object.toString();
            }
        }



}

