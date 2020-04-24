package com.zeal.softwareengineeringprojectmanage.controller;


import com.zeal.softwareengineeringprojectmanage.bean.*;
import com.zeal.softwareengineeringprojectmanage.service.*;
import com.zeal.softwareengineeringprojectmanage.util.ExcelUtil;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
public class TeacherController {

    @Autowired
    TopicService topicService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    StudentService studentService;
    @Autowired
    StagetopicService stagetopicService;
    @Autowired
    ScoreService scoreService;
    @Autowired
    BlocktaskService blocktaskService;
    @Autowired
    StagetopicresultService stagetopicresultService;
    @Autowired
    OutstandingcaseService outstandingcaseService;
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    ClazzService clazzService;
    @Value("${file.uploadTopicFolder}")
    private String uploadTopicFilePath; //选题文件上传的地址
    @Value("${file.uploadStageTopicFolder}")
    private String uploadStageTopicFilePath; //选题文件上传的地址
    @Value("${file.uploadOutstandingCaseFolder}")
    private String outstandingCasePath;
    @RequestMapping("/manageTopic")
    public String manageTopic(Integer currentUser,Integer page,String isUpdateSuccess, Model model){
        logger.info("教师进入管理选题界面");
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(5);
        p.setTotalUsers(topicService.selectByTeacherId(currentUser).size());
        List<Topic> topics = topicService.selectByTeaIdAddPage(currentUser,(page - 1) * p.getPageSize(), p.getPageSize());
        model.addAttribute("topics",topics);
        model.addAttribute("page",p);
        model.addAttribute("isUpdateSuccess",isUpdateSuccess);
        List<Teacher> teachers = teacherService.selectAll();
        model.addAttribute("teachers",teachers);
        model.addAttribute("currentUser",currentUser);
        return "teacher/manageTopic";
    }

    @RequestMapping("/addTopic")
    public String addTopic(Integer teaId,Model model){
        logger.info("教师进入添加选题界面");
        Teacher teacher = teacherService.selectByPrimayKey(teaId);
        model.addAttribute("teacher",teacher);
        return "teacher/addTopic";
    }

    @RequestMapping("/addSingleTopic")
    public String addSingleTopic(@RequestParam("topicname")String topicname,
                                 @RequestParam("topicdescribe") String topicdescribe,
                                 @RequestParam("teaid") Integer teaid,
                                 @RequestParam("deadline")String deadline,
                                 @RequestParam("choosedeadline")String choosedeadline,
                                 @RequestParam("maxsize") Integer maxsize,
                                 @RequestParam("file") MultipartFile file, HttpServletRequest req, Model model) throws ParseException, UnsupportedEncodingException {
        logger.info("教师添加一条选题信息");
        if(!file.isEmpty()){
            Topic topic =new Topic();
            topic.setTopicname(topicname);
            topic.setTopicdescribe(topicdescribe);
            topic.setTeaid(teaid);
            String[] str1 = deadline.split("[T]");
            String[] str2 = choosedeadline.split("[T]");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date1 = simpleDateFormat.parse(str1[0]+" "+str1[1]);
            Date date2 = simpleDateFormat.parse(str2[0]+" "+str2[1]);
            topic.setDeadline(date1);
            topic.setChoosedeadline(date2);
            topic.setMaxsize(maxsize);
            Date date=new Date();
            File dir = new File(uploadTopicFilePath);
            if(!dir.exists()) {
                dir.mkdir();
            }
            String path = uploadTopicFilePath + file.getOriginalFilename();
            topic.setDownloadlink(path);
            topic.setReleasetime(date);
            File tempFile = tempFile =  new File(path);
            if(tempFile.exists()){
                model.addAttribute("IsSuccess","文件已存在，请重新选择");
                logger.info("选题参考文件上传失败，文件已存在");
                Teacher teacher = teacherService.selectByPrimayKey(topic.getTeaid());
                model.addAttribute("teacher",teacher);
                return "teacher/addTopic";
            }
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
            }catch (Exception e){
                e.printStackTrace();
                model.addAttribute("IsSuccess","文件上传失败");
                logger.info("选题参考文件上传失败");
                Teacher teacher = teacherService.selectByPrimayKey(topic.getTeaid());
                model.addAttribute("teacher",teacher);
                return "teacher/addTopic";
            }
            int insert = topicService.insert(topic);
            String isUpdateSuccess="成功发布"+insert+"选题";
            logger.info("教师"+isUpdateSuccess);
            return "redirect:/manageTopic?currentUser="+teaid+"&page=1&isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8");
        }else{
            model.addAttribute("IsSuccess","文件为空，上传失败");
            logger.info("参考文档为空，上传失败");
            Teacher teacher = teacherService.selectByPrimayKey(teaid);
            model.addAttribute("teacher",teacher);
            return "teacher/addTopic";
        }
    }

    @RequestMapping("/getTopicDetail")
    public String getTopicDetail( Integer topicId, String IsSuccess, Model model){
        logger.info("教师进入更新选题信息界面");
        Topic topic = topicService.selectByPrimaryKey(topicId);
        model.addAttribute("topic",topic);
        Teacher teacher = teacherService.selectByPrimayKey(topic.getTeaid());
        model.addAttribute("teacher",teacher);
        model.addAttribute("IsSuccess",IsSuccess);
        return "teacher/updateTopic";
    }

    @RequestMapping("/updateTopicById")
    public String updateTopicById(@RequestParam("id")Integer id,
                                  @RequestParam("topicname")String topicname,
                                  @RequestParam("topicdescribe") String topicdescribe,
                                  @RequestParam("teaid") Integer teaid,
                                  @RequestParam("deadline")String deadline,
                                  @RequestParam("choosedeadline")String choosedeadline,
                                  @RequestParam("maxsize") Integer maxsize,
                                  @RequestParam("downloadlink") String downloadlink,
                                  @RequestParam("file") MultipartFile file, HttpServletRequest req, Model model) throws ParseException, UnsupportedEncodingException {
        logger.info("教师根据id更新选题信息");
        if(file.isEmpty()){
            Topic topic =new Topic();
            topic.setId(id);
            topic.setTopicname(topicname);
            topic.setTopicdescribe(topicdescribe);
            topic.setTeaid(teaid);
            Date date1=new Date();
            topic.setReleasetime(date1);
            String[] str1 = deadline.split("[T]");
            String[] str2 = choosedeadline.split("[T]");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date2 = simpleDateFormat.parse(str1[0]+" "+str1[1]);
            Date date3 = simpleDateFormat.parse(str2[0]+" "+str2[1]);
            topic.setDeadline(date2);
            topic.setChoosedeadline(date3);
            topic.setMaxsize(maxsize);
            topic.setDownloadlink(downloadlink);
            int i = topicService.updateByPrimaryKey(topic);
            String isUpdateSuccess="成功更新"+i+"条选题";
            logger.info("教师"+isUpdateSuccess);
            return "redirect:/manageTopic?currentUser="+teaid+"&page=1&isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8");
        }
        File deleteFile = new File(downloadlink);
        if(deleteFile!=null){
            //文件不为空，执行删除
            deleteFile.delete();
        }
        Topic topic =new Topic();
        topic.setId(id);
        topic.setTopicname(topicname);
        topic.setTopicdescribe(topicdescribe);
        topic.setTeaid(teaid);
        Date date1=new Date();
        topic.setReleasetime(date1);
        String[] str = deadline.split("[T]");
        String[] str2 = choosedeadline.split("[T]");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = simpleDateFormat.parse(str[0]+" "+str[1]);
        Date date3 = simpleDateFormat.parse(str2[0]+" "+str2[1]);
        topic.setChoosedeadline(date3);
        topic.setDeadline(date);
        topic.setMaxsize(maxsize);
        File dir = new File(uploadTopicFilePath);
        if(!dir.exists()) {
            dir.mkdir();
        }
        String path = uploadTopicFilePath + file.getOriginalFilename();
        topic.setDownloadlink(path);
        File tempFile =  tempFile =  new File(path);
        if(tempFile.exists()){
            String IsSuccess="文件已存在，请重新选择！";
            logger.info("选题参考文档已存在，上传失败");
            return "redirect:/getTopicDetail?topicId="+id+"&IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
        }else {
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
            } catch (Exception e) {
                e.printStackTrace();
                String IsSuccess = "文件上传失败";
                logger.info(IsSuccess);
                return "redirect:/getTopicDetail?topicId=" + id + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8");
            }
            int i = topicService.updateByPrimaryKey(topic);
            String isUpdateSuccess = "成功更新" + i + "条选题";
            logger.info("教师"+isUpdateSuccess);
            return "redirect:/manageTopic?currentUser=" + teaid + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
        }
    }

    @RequestMapping("/updateTopic/delete/")
    @ResponseBody
    public String deleteTopicById(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, JSONException {
        logger.info("教师删除一条选题");
        int id = Integer.parseInt(request.getParameter("id"));
        int i = topicService.deleteByPrimary(id);
        JSONObject object = new JSONObject();
        if(i>0) {
            object.put("code",1);
            String msg="成功删除了"+i+"条选题";
            logger.info("教师"+msg);
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            object.put("msg","删除失败，请重新尝试！！");
            logger.info("教师删除选题失败");
            return object.toString();
        }
    }

    @RequestMapping("/viewChooseDetail")
    public String viewChoooseDetail(Integer currentUser,Integer page,Model model){
        logger.info("教师进入学生选题情况界面");
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(5);
        p.setTotalUsers(topicService.selectByTeaIdAddDeadline(currentUser).size());
        List<Topic> topics = topicService.selectByTeaIdAddDeadlineAndPage(currentUser,(page - 1) * p.getPageSize(), p.getPageSize());
        List<Student> students = studentService.selectByTeaId(currentUser);
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
        model.addAttribute("topics",topics);
        model.addAttribute("page",p);
        model.addAttribute("students",students);
        model.addAttribute("choosedTopics",choosedTopics);
        model.addAttribute("currentUser",currentUser);
        return "teacher/viewChooseDetail";
    }

    @RequestMapping("/confirmGroup")
    public String confirmGroup(Integer topicId,Model model){
        logger.info("教师进入分组界面");
        List<Student> students = studentService.selectByTopicId(topicId);
        model.addAttribute("students",students);
        Topic topic = topicService.selectByPrimaryKey(topicId);
        model.addAttribute("topic",topic);
        StringBuilder  confirmMsg=new StringBuilder();
        for(Student student:students){
            confirmMsg.append(student.getStuname());
            confirmMsg.append("、");
        }
        if (confirmMsg.length() > 0) {
            model.addAttribute("confirmMsg",confirmMsg.toString().substring(0,confirmMsg.length()-1));
        }
        else {
            model.addAttribute("confirmMsg",confirmMsg.toString());
        }
        return "teacher/confirmGroup";
    }

    @RequestMapping(value = "/setStuGroup")
    @ResponseBody
    public String setStuGroup(HttpServletRequest request,HttpServletResponse response) throws JSONException, IOException {
        logger.info("教师给学生分组");
        int id = Integer.parseInt(request.getParameter("id"));
        int groupId = Integer.parseInt(request.getParameter("groupId"));
        List<Student> students1 = studentService.selectByGroupId(groupId);
        JSONObject object=new JSONObject();
        if(students1.size()!=0){
            String msg = "组号"+groupId+"已被分配，请重新输入";   //设置Json对象的属性
            logger.info("分组失败"+msg);
            object.put("code",-1);
            object.put("msg",msg);
            return object.toString();
        }else {
            int groupLeader = Integer.parseInt(request.getParameter("groupLeader"));
            Student student1 = studentService.selectByPrimaryKey(groupLeader);
            int updateGroupId = studentService.updateGroupIdByTopicId(id, groupId);
            int updateGroupLeader = studentService.updateGroupLeader(groupLeader);
            List<Student> students = studentService.selectByTopicId(id);
            StringBuilder confirmMsg = new StringBuilder();
            for (Student student : students) {
                confirmMsg.append(student.getStuname());
                confirmMsg.append("、");
            }
            //创建Json对象
            if (updateGroupId > 0 & updateGroupLeader > 0) {
                String msg = "成功将" + confirmMsg.toString().substring(0, confirmMsg.length() - 1) + "分为第" + groupId + "组；组长为" + student1.getStuname();   //设置Json对象的属性
                logger.info(msg);
                object.put("code",1);
                object.put("msg",msg);
                return object.toString();

            } else {
                String msg = "分组失败，请重新尝试";   //设置Json对象的属性
                logger.info(msg);
                object.put("code",-1);
                object.put("msg",msg);
                return object.toString();
            }
        }
    }

    @RequestMapping(value = "/stopTopic")
    @ResponseBody
    public String stopTopic(HttpServletRequest request,HttpServletResponse response) throws JSONException, IOException {
        logger.info("教师停开选题");
        int topicId = Integer.parseInt(request.getParameter("topicId"));
        int i = topicService.deleteByPrimary(topicId);
        JSONObject object = new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="id为"+topicId+"的选题已停开";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg = "停开失败，请重新尝试";   //设置Json对象的属性
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }
    }

    @RequestMapping("/manageGroupAndTopic")
    public String manageGroupAndTopic(Integer currentUser,Integer page,Model model){
        logger.info("教师进入管理学生选题和分组界面");
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(10);
        p.setTotalUsers(studentService.selectByTeaId(currentUser).size());
        List<Student> students = studentService.selectByTeaIdAndPage(currentUser, (page - 1) * p.getPageSize(), p.getPageSize());
        List<Clazz> clazzes = clazzService.selectAll();
        Teacher teacher = teacherService.selectByPrimayKey(currentUser);
        model.addAttribute("teacher",teacher);
        model.addAttribute("clazzes",clazzes);
        model.addAttribute("page",p);
        model.addAttribute("students",students);
        return "teacher/manageGroupAndTopic";
    }

    @RequestMapping("/selectStudentByGroup")
    public String selectStudentByGroup(Integer groupId,Integer currentUser,Model model){
        logger.info("教师按组号查询学生分组及选题情况");
        List<Student> students = studentService.selectByTeaIdAndGroupId(currentUser, groupId);
        if (students.size()==0)
        {
            model.addAttribute("isSelectSuccess","您输入的组号不存在");
        }
        model.addAttribute("students",students);
        Teacher teacher = teacherService.selectByPrimayKey(currentUser);
        model.addAttribute("teacher",teacher);
        List<Clazz> clazzes = clazzService.selectAll();
        model.addAttribute("clazzes",clazzes);
        return "teacher/manageGroupAndTopic";

    }

    @RequestMapping("/selectStudentByStuId")
    public String selectStudentByStuId(Integer stuId,Integer currentUser,Model model){
        logger.info("教师通过stuId查询学生选题分组情况");
        List<Student> students = studentService.selectByStuIdAndTeaId(stuId,currentUser);
        if (students.size()==0)
        {
            model.addAttribute("isSelectSuccess","您输入的学号不存在，或者指导教师不是你");
            logger.info("输入的学号不存在，或者指导教师不是该教师");
        }
        model.addAttribute("students",students);
        Teacher teacher = teacherService.selectByPrimayKey(currentUser);
        model.addAttribute("teacher",teacher);
        List<Clazz> clazzes = clazzService.selectAll();
        model.addAttribute("clazzes",clazzes);
        return "teacher/manageGroupAndTopic";
    }

    @RequestMapping("/getStuGroupAndTopicDetail")
    public String updateStu(Integer stuid,Model model){
        logger.info("教师获取学生分组和选题详情");
        Student student = studentService.selectByStuId(stuid);
        model.addAttribute("student",student);
        List<Clazz> clazzes = clazzService.selectAll();
        model.addAttribute("clazzes",clazzes);
        Teacher teacher = teacherService.selectByPrimayKey(student.getTeaid());
        model.addAttribute("teacher",teacher);
        return "teacher/updateStuGroupAndTopic";
    }

    @RequestMapping("/updateStuGroupAndTopicByStuId")
    @ResponseBody
    public String updateStuGroupAndTopicByStuId(HttpServletRequest request,HttpServletResponse response) throws JSONException {
        logger.info("教师更新学生分组和选题");
        int stuid = Integer.parseInt(request.getParameter("stuid"));
        int isgroupleader = Integer.parseInt(request.getParameter("isgroupleader"));
        int groupid = Integer.parseInt(request.getParameter("groupid"));
        int topicid = Integer.parseInt(request.getParameter("topicid"));
        List<Student> students = studentService.selectByGroupId(groupid);
        Topic topic = topicService.selectByPrimaryKey(topicid);
        JSONObject object=new JSONObject();
        if(students.size()==0||topic==null){
            object.put("code",-1);
            object.put("msg","您所填写的组号或选题号不存在，请重新输入！！");
            logger.info("您所填写的组号或选题号不存在");
            return object.toString();
        }else {
        int i = studentService.updateGroupAndTopicByStuId(stuid, isgroupleader, groupid, topicid);
        Student student = studentService.selectByStuId(stuid);
        if (i>0){
            object.put("code",1);
            String msg="成功将"+student.getStuname()+"的组号改为"+student.getGroupid()+";选题号为"+student.getTopicid();
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }
          else {
            object.put("code",-1);
            object.put("msg","修改失败，您所填写的组号或选题号不存在，请重新尝试！！");
            logger.info("您所填写的组号或选题号不存在");
            return object.toString();
        }
    }
    }

    @RequestMapping("/manageStageTask")
    public String manageStageTask(Integer currentUser,Integer page,String isUpdateSuccess,Model model){
        logger.info("教师进入阶段性任务界面");
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(10);
        p.setTotalUsers(stagetopicService.selectByTeaId(currentUser).size());
        Teacher teacher = teacherService.selectByPrimayKey(currentUser);
        List<Stagetopic> stagetopics = stagetopicService.selectByTeaIdAndPage(currentUser, (page - 1) * p.getPageSize(), p.getPageSize());
        model.addAttribute("page",p);
        model.addAttribute("stagetopics",stagetopics);
        model.addAttribute("teacher",teacher);
        model.addAttribute("isUpdateSuccess",isUpdateSuccess);
        return "teacher/manageStageTask";
    }

    @RequestMapping("/addStageTopic")
    public String addStageTopic(Integer teaId,Model model){
        logger.info("教师进入添加阶段性任务界面");
        Teacher teacher = teacherService.selectByPrimayKey(teaId);
        model.addAttribute("teacher",teacher);
        return "teacher/addStageTopic";
    }

    @RequestMapping("/addSingleStageTopic")
    public String addSingleStageTopic(@RequestParam("name")String name,
                                      @RequestParam("describe") String describe,
                                      @RequestParam("teaid") Integer teaid,
                                      @RequestParam("deadline")String deadline,
                                      @RequestParam("releaseTime")String releaseTime,
                                      @RequestParam("file") MultipartFile file,Model model) throws ParseException, UnsupportedEncodingException {
        logger.info("教师添加一条阶段性任务");
        if(!file.isEmpty()){
            Stagetopic stagetopic =new Stagetopic();
            stagetopic.setStagename(name);
            stagetopic.setStagedescribe(describe);
            stagetopic.setTeaid(teaid);
            String[] str1 = deadline.split("[T]");
            String[] str2 = releaseTime.split("[T]");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date1 = simpleDateFormat.parse(str1[0]+" "+str1[1]);
            Date date2 = simpleDateFormat.parse(str2[0]+" "+str2[1]);
            stagetopic.setDeadline(date1);
            stagetopic.setReleasetime(date2);
            File dir = new File(uploadStageTopicFilePath);
            if(!dir.exists()) {
                dir.mkdir();
            }
            String path = uploadStageTopicFilePath + file.getOriginalFilename();
            stagetopic.setDownloadlink(path);
            File tempFile = tempFile =  new File(path);
            if(tempFile.exists()){
                model.addAttribute("IsSuccess","文件已存在，请重新选择文件");
                logger.info("上传的阶段性参考文件已存在");
                Teacher teacher = teacherService.selectByPrimayKey(teaid);
                model.addAttribute("teacher",teacher);
                return "teacher/addStageTopic";
            }else {
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
            }catch (Exception e){
                e.printStackTrace();
                model.addAttribute("IsSuccess","文件上传失败");
                logger.info("文件上传失败");
                Teacher teacher = teacherService.selectByPrimayKey(teaid);
                model.addAttribute("teacher",teacher);
                return "teacher/addStageTopic";
            }
            int insert = stagetopicService.insert(stagetopic);
            if(insert>0) {
                String isUpdateSuccess = "成功发布" + insert + "阶段性任务";
                logger.info(isUpdateSuccess);
                return "redirect:/manageStageTask?currentUser=" + teaid + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
            }else {
                model.addAttribute("IsSuccess","阶段性任务发布失败，请重试");
                logger.info("阶段性任务发布失败");
                Teacher teacher = teacherService.selectByPrimayKey(teaid);
                model.addAttribute("teacher",teacher);
                return "teacher/addStageTopic";
            }
            }
        }else {
            model.addAttribute("IsSuccess", "文件为空，上传失败");
            logger.info("阶段性任务文件为空，上传失败");
            Teacher teacher = teacherService.selectByPrimayKey(teaid);
            model.addAttribute("teacher", teacher);
            return "teacher/addStageTopic";
        }
    }

    @RequestMapping("/getStageTopicDetail")
    public String getStageTopicDetail( Integer stagetopicId,Model model){
        logger.info("教师进入阶段性任务详情界面");
        Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(stagetopicId);
        Teacher teacher = teacherService.selectByPrimayKey(stagetopic.getTeaid());
        model.addAttribute("stagetopic",stagetopic);
        model.addAttribute("teacher",teacher);
        return "teacher/updateStageTopic";
    }

    @RequestMapping("/updateStageTopicById")
    public String updateStageTopicById(@RequestParam("id")Integer id,
                                      @RequestParam("name")String name,
                                      @RequestParam("describe") String describe,
                                      @RequestParam("teaid") Integer teaid,
                                      @RequestParam("deadline")String deadline,
                                      @RequestParam("releaseTime")String releaseTime,
                                      @RequestParam("downloadlink") String downloadlink,
                                      @RequestParam("file") MultipartFile file, HttpServletRequest req, Model model) throws ParseException, UnsupportedEncodingException {
        logger.info("教师通过id更新阶段性任务信息");
        if(file.isEmpty()){
            Stagetopic stagetopic = new Stagetopic();
            stagetopic.setId(id);
            stagetopic.setStagename(name);
            stagetopic.setStagedescribe(describe);
            stagetopic.setTeaid(teaid);
            String[] str1 = deadline.split("[T]");
            String[] str2 = releaseTime.split("[T]");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date2 = simpleDateFormat.parse(str1[0]+" "+str1[1]);
            Date date3 = simpleDateFormat.parse(str2[0]+" "+str2[1]);
            stagetopic.setDeadline(date2);
            stagetopic.setReleasetime(date3);
            stagetopic.setDownloadlink(downloadlink);
            int i = stagetopicService.updateByPrimaryKey(stagetopic);
            String isUpdateSuccess="成功更新"+i+"条阶段性任务";
            logger.info(isUpdateSuccess);
            return "redirect:/manageStageTask?currentUser=" + teaid + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
        }
        File deleteFile = new File(downloadlink);
        if(deleteFile!=null){
            //文件不为空，执行删除
            deleteFile.delete();
        }
        Stagetopic stagetopic = new Stagetopic();
        stagetopic.setId(id);
        stagetopic.setStagename(name);
        stagetopic.setStagedescribe(describe);
        stagetopic.setTeaid(teaid);
        String[] str = deadline.split("[T]");
        String[] str2 = releaseTime.split("[T]");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = simpleDateFormat.parse(str[0]+" "+str[1]);
        Date date3 = simpleDateFormat.parse(str2[0]+" "+str2[1]);
        stagetopic.setReleasetime(date3);
        stagetopic.setDeadline(date);
        File dir = new File(uploadStageTopicFilePath);
        if(!dir.exists()) {
            dir.mkdir();
        }
        String path = uploadStageTopicFilePath + file.getOriginalFilename();
        stagetopic.setDownloadlink(path);
        File tempFile = null;
        try {
            tempFile =  new File(path);
            FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
        }catch (Exception e){
            e.printStackTrace();
            String IsSuccess="文件上传失败";
            logger.info("阶段性任务参考文件上传失败");
            model.addAttribute("IsSuccess",IsSuccess);
            Stagetopic stagetopic1 = stagetopicService.selectByPrimaryKey(id);
            Teacher teacher = teacherService.selectByPrimayKey(stagetopic1.getTeaid());
            model.addAttribute("stagetopic",stagetopic1);
            model.addAttribute("teacher",teacher);
            return "teacher/updateStageTopic";
        }
        int i = stagetopicService.updateByPrimaryKey(stagetopic);
        String isUpdateSuccess="成功更新"+i+"条阶段性任务";
        logger.info(isUpdateSuccess);
        return "redirect:/manageStageTask?currentUser=" + teaid + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
    }

    @RequestMapping("/deleteStageTopic")
    @ResponseBody
    public String deleteStageTopic(HttpServletRequest request,HttpServletResponse response) throws JSONException {
        logger.info("教师删除阶段性任务");
        int id = Integer.parseInt(request.getParameter("id"));
        int i = stagetopicService.deleteByPrimaryKey(id);
        JSONObject object = new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="成功删除id为"+id+"的阶段性任务";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            object.put("msg","修改失败，请重新尝试！！");
            logger.info("阶段性任务修改失败");
            return object.toString();
        }
    }

    @RequestMapping("/auditStageTopic")
    public String auditStageTopic(Integer currentUser,Integer page,Model model) throws UnsupportedEncodingException {
        logger.info("教师进入审核阶段性任务界面");
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(5);

        List<Stagetopic> stagetopics = stagetopicService.selectByTeaId(currentUser);
        if (stagetopics.size()==0){
            return "redirect:/manageStageTask?currentUser="+currentUser+"&isUpdateSuccess=" + URLEncoder.encode("你还没有发布阶段性任务","UTF-8")+"&page=1";
        }
        List<Integer> stagetopicIds=new ArrayList<>();
        for (Stagetopic stagetopic:stagetopics){
            stagetopicIds.add(stagetopic.getId());
        }
        List<Stagetopicresult> stagetopicresultsAll = stagetopicresultService.selectByStageTopicIds(stagetopicIds);
        p.setTotalUsers(stagetopicresultsAll.size());
        List<Stagetopicresult> stagetopicresults = stagetopicresultService.selectByStageTopicIdsAndPage(stagetopicIds, (page - 1) * p.getPageSize(), p.getPageSize());
        model.addAttribute("stagetopicresults",stagetopicresults);
        List<Topic> topics = topicService.selectByTeacherId(currentUser);
        model.addAttribute("topics",topics);
        model.addAttribute("stagetopics",stagetopics);
        model.addAttribute("page",p);
        model.addAttribute("currentUser",currentUser);
        return "teacher/auditStageTopic";
    }

    @RequestMapping("/auditStage")
    public String auditStage(Integer stagetopicresultId,Model model){
        logger.info("教师进入审核阶段性任务详情界面");
        Stagetopicresult stagetopicresult = stagetopicresultService.selectByPrimaryKey(stagetopicresultId);
        Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(stagetopicresult.getStagetopicid());
        Topic topic = topicService.selectByPrimaryKey(stagetopicresult.getTopicid());
        model.addAttribute("stagetopicresult",stagetopicresult);
        model.addAttribute("stagetopic",stagetopic);
        model.addAttribute("topic",topic);
        model.addAttribute("currentUser",topic.getTeaid());
        return "teacher/auditStageDetail";
    }

    @RequestMapping("/auditStageDetail")
    @ResponseBody
    public String auditStageDetail(HttpServletRequest request,HttpServletResponse response) throws JSONException {
        logger.info("教师审核阶段性任务");
        int id = Integer.parseInt(request.getParameter("id"));
        int ispass = Integer.parseInt(request.getParameter("ispass"));
        String suggestion = request.getParameter("suggestion");
        int i = stagetopicresultService.updateByIdAndIsPassAndSugg(id, ispass, suggestion);
       JSONObject jsonObject= new JSONObject();
        if(i>0){
            jsonObject.put("code",1);
            jsonObject.put("msg","成功审核id为"+id+"的任务");
            logger.info("成功审核id为"+id+"的任务");
            return jsonObject.toString();
        }else {
            jsonObject.put("code",-1);
            jsonObject.put("msg","审核失败，请重新尝试！！");
            logger.info("审核失败，请重新尝试！！");
            return jsonObject.toString();
        }
    }

    @RequestMapping("/auditTopicHtml")
    public String auditTopicHtml(Integer currentUser,Integer page,String isUpdateSuccess,Model model){
        logger.info("教师进入审核选题界面");
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(5);
        p.setTotalUsers(topicService.selectByTeacherId(currentUser).size());
        List<Topic> topics = topicService.selectByTeaIdAddPage(currentUser,(page - 1) * p.getPageSize(), p.getPageSize());
        for (Topic topic:topics){
            if(topic.getResult()==null){
                topic.setResult("");
            }
        }
        model.addAttribute("topics",topics);
        model.addAttribute("page",p);
        model.addAttribute("isUpdateSuccess",isUpdateSuccess);
        List<Teacher> teachers = teacherService.selectAll();
        model.addAttribute("teachers",teachers);
        model.addAttribute("currentUser",currentUser);
        return "teacher/auditTopic";
    }

    @RequestMapping("/auditTopic")
    public String auditTopic(Integer topicId,Model model){
        logger.info("教师进入审核选题详情界面");
        Topic topic = topicService.selectByPrimaryKey(topicId);
        List<Student> students = studentService.selectByTopicId(topicId);
        StringBuilder groupMemeber=new StringBuilder();
        for (Student student:students){
            groupMemeber.append(student.getStuname());
            groupMemeber.append("、");
        }
        model.addAttribute("topic",topic);
        model.addAttribute("groupMemeber",groupMemeber.toString().substring(0,(groupMemeber.length()-1)));
        return "teacher/auditTopicDetail";
    }

    @RequestMapping("/auditTopicDetail")
    @ResponseBody
    public String auditTopicDetail(HttpServletResponse response,HttpServletRequest request) throws JSONException {
        logger.info("教师审核选题");
        int id = Integer.parseInt(request.getParameter("id"));
        int ispass = Integer.parseInt(request.getParameter("ispass"));
        String suggestion = request.getParameter("suggestion");
        int score = Integer.parseInt(request.getParameter("score"));
        int i = topicService.auditTopic(id, ispass, suggestion, score);
        JSONObject object=new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="成功审核id为"+id+"的选题";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="审核失败";
            logger.info("教师审核选题失败");
            object.put("msg",msg);
            return object.toString();
        }
    }

    @RequestMapping("/putIntoCaseLibHtml")
    public String putIntoCaseLibHtml(Integer topicId,Model model){
        logger.info("教师进入存入优秀案例库界面");
        Topic topic = topicService.selectByPrimaryKey(topicId);
        List<Student> students = studentService.selectByTopicId(topicId);
        StringBuilder groupMemeber=new StringBuilder();
        for (Student student:students){
            groupMemeber.append(student.getStuname());
            groupMemeber.append("、");
        }
        model.addAttribute("topic",topic);
        model.addAttribute("groupMemeber",groupMemeber.toString().substring(0,(groupMemeber.length()-1)));
        return "teacher/putIntoCaseLib";
    }

    @RequestMapping("putIntoCaseLib")
    @ResponseBody
    public String putIntoCaseLib(HttpServletRequest request,HttpServletResponse response) throws JSONException {
        logger.info("教师将选题存入优秀案例库");
        int topicId = Integer.parseInt(request.getParameter("id"));
        String technology = request.getParameter("technology");
        String group = request.getParameter("group");
        System.out.println(group);
        Topic topic = topicService.selectByPrimaryKey(topicId);
        Outstandingcase outstandingcase=new Outstandingcase();
        outstandingcase.setCasename(topic.getTopicname());
        outstandingcase.setCasedescribe(topic.getTopicdescribe());
        outstandingcase.setDownloadlink(topic.getResult());
        outstandingcase.setGroupmember(group);
        outstandingcase.setTechnology(technology);
        outstandingcase.setSubmittime(new Date());
        outstandingcase.setTeaid(topic.getTeaid());
        outstandingcase.setScore(topic.getScore().toString());
        outstandingcase.setSuggestion(topic.getSuggestion());
        int insert = outstandingcaseService.insert(outstandingcase);
        JSONObject object=new JSONObject();
        if(insert>0){
            object.put("code",1);
            String msg="成功将题号为"+topicId+"的选题设为优秀案例";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="设置失败，请重新尝试";
            logger.info("教师将选题设置为优秀案例失败");
            object.put("msg",msg);
            return object.toString();
        }
    }

    @RequestMapping("/manageCaseLib")
    public String manageCaseLib(Integer currentUser,Integer page,Integer Type,String Keyword, String isUpdateSuccess,Model model){
        logger.info("教师进入管理优秀案例库界面");
        if(Type==1){
            logger.info("通过teaid查询优秀案例库");
        Page p=new Page();
        p.setTotalUsers(outstandingcaseService.selectByTeaId(currentUser).size());
        p.setPageSize(5);
        p.setCurrentPage(page);
        List<Outstandingcase> outstandingcases = outstandingcaseService.selectByTeaIdAndPage(currentUser, (page - 1) * p.getPageSize(), p.getPageSize());
        List<Teacher> teachers = teacherService.selectAll();
        model.addAttribute("isUpdateSuccess",isUpdateSuccess);
        model.addAttribute("page",p);
        model.addAttribute("teachers",teachers);
        model.addAttribute("outstandingcases",outstandingcases);
        model.addAttribute("currentUser",currentUser);
        model.addAttribute("type",Type);
        model.addAttribute("keyword",Keyword);
        return "teacher/manageCaseLib";
        }if(Type==2) {
            logger.info("教师通过关键词查询优秀案例库");
            List<Outstandingcase> outstandingcasesAll = outstandingcaseService.selectByKeyWordAndTeaId(Keyword, currentUser);
            Page p=new Page();
            p.setTotalUsers(outstandingcasesAll.size());
            p.setPageSize(5);
            p.setCurrentPage(page);
            int i=0;
            for (Outstandingcase outstandingcase:outstandingcasesAll){
                i++;
            }
            List<Outstandingcase> outstandingcases = outstandingcaseService.selectByKeyWordAndTeaIdAndPage(Keyword,currentUser, (page - 1) * p.getPageSize(), p.getPageSize());
            List<Teacher> teachers = teacherService.selectAll();
            model.addAttribute("isUpdateSuccess","查询到"+i+"个案例");
            logger.info("通过关键词查询到"+i+"个案例");
            model.addAttribute("page",p);
            model.addAttribute("teachers",teachers);
            model.addAttribute("outstandingcases",outstandingcases);
            model.addAttribute("currentUser",currentUser);
            model.addAttribute("type",Type);
            model.addAttribute("keyword",Keyword);
            return "teacher/manageCaseLib";
        }else {
            logger.info("教师查询所有优秀案例库");
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
            model.addAttribute("currentUser",currentUser);
            model.addAttribute("type",Type);
            model.addAttribute("keyword",Keyword);
            return "teacher/manageCaseLib";
        }
    }

    @RequestMapping("/getCaseDetail")
    public String getCaseDetail(Integer id,String IsSuccess,String Keyword,Integer Type,Model model){
        logger.info("教师进入优秀案例详情界面");
        Outstandingcase outstandingcase = outstandingcaseService.selectByPrimaryKey(id);
        model.addAttribute("outstandingcase",outstandingcase);
        model.addAttribute("IsSuccess",IsSuccess);
        model.addAttribute("keyword",Keyword);
        model.addAttribute("type",Type);
        return "teacher/updateCase";
    }

    @RequestMapping("/updateCaseById")
    public String updateCaseById(Integer id,
                                 String casename,
                                 String casedescribe,
                                 String downloadlink,
                                 String technology,
                                 String groupmember,
                                 String score,
                                 String suggestion,
                                 Integer teaId,
                                 String Keyword,
                                 Integer Type,
                                 MultipartFile file,Model model) throws UnsupportedEncodingException {
        logger.info("教师通过id更新优秀案例库");
        if(file.isEmpty()){
            Outstandingcase outstandingcase =new Outstandingcase();
            outstandingcase.setId(id);
            outstandingcase.setCasename(casename);
            outstandingcase.setCasedescribe(casedescribe);
            outstandingcase.setDownloadlink(downloadlink);
            outstandingcase.setTeaid(teaId);
            outstandingcase.setSubmittime(new Date());
            outstandingcase.setScore(score);
            outstandingcase.setSuggestion(suggestion);
            outstandingcase.setGroupmember(groupmember);
            outstandingcase.setTechnology(technology);
            int i = outstandingcaseService.updateByPrimaryKey(outstandingcase);
            String isUpdateSuccess="成功更新"+i+"条案例";
            logger.info(isUpdateSuccess);
            return "redirect:/manageCaseLib?currentUser=" + teaId +"&Type="+Type+ "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8")+"&Keyword="+Keyword;
        }
        File deleteFile = new File(downloadlink);
        if(deleteFile!=null){
            //文件不为空，执行删除
            deleteFile.delete();
        }
        Outstandingcase outstandingcase =new Outstandingcase();
        outstandingcase.setId(id);
        outstandingcase.setCasename(casename);
        outstandingcase.setCasedescribe(casedescribe);
        outstandingcase.setTeaid(teaId);
        outstandingcase.setSubmittime(new Date());
        outstandingcase.setScore(score);
        outstandingcase.setSuggestion(suggestion);
        outstandingcase.setGroupmember(groupmember);
        outstandingcase.setTechnology(technology);
        File dir = new File(outstandingCasePath);
        if(!dir.exists()) {
            dir.mkdir();
        }
        String path = outstandingCasePath + file.getOriginalFilename();
        outstandingcase.setDownloadlink(path);
        File tempFile = null;
        try {
            tempFile =  new File(path);
            FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
        }catch (Exception e){
            e.printStackTrace();
            String IsSuccess="文件上传失败";
            logger.info("优秀案例参考文件上传失败");
            return "redirect:/getCaseDetail?id="+id+"&Type="+Type+"&IsSuccess="+URLEncoder.encode(IsSuccess, "UTF-8")+"&Keyword="+Keyword;
        }
        int i = outstandingcaseService.updateByPrimaryKey(outstandingcase);
        String isUpdateSuccess="成功更新"+i+"条案例";
        logger.info(isUpdateSuccess);
        return "redirect:/manageCaseLib?currentUser=" + teaId +"&Type="+Type+ "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8")+"&Keyword="+Keyword;
    }

    @RequestMapping("/deleteCase")
    @ResponseBody
    public String deleteCase(HttpServletRequest request) throws JSONException {
        logger.info("教师删除从优秀案例库中删除一条记录");
        int id = Integer.parseInt(request.getParameter("id"));
        int i = outstandingcaseService.deleteByPrimaryKey(id);
        JSONObject object=new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="成功删除题id为"+id+"的优秀案例";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="删除失败，请重新尝试！";
            logger.info("删除优秀案例失败");
            object.put("msg",msg);
            return object.toString();
        }
    }

    @RequestMapping("/onlyGetCaseDetail")
    public String onlyGetCaseDetail(Integer id,Model model){
        logger.info("进入优秀案例详情界面");
        Outstandingcase outstandingcase = outstandingcaseService.selectByPrimaryKey(id);
        Teacher teacher = teacherService.selectByPrimayKey(outstandingcase.getTeaid());
        model.addAttribute("teacher",teacher);
        model.addAttribute("outstandingcase",outstandingcase);
        return "teacher/caseDetail";
    }

    @RequestMapping("/importReplyScoreHtml")
    public String importReplyScoreHtml(Integer currentUser,Integer page,String classId, String stuId,String isUpdateSuccess,Integer type,Model model){
        logger.info("教师进入管理成绩页面");
        List<Clazz> clazzes = clazzService.selectAll();
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(8);
        List<Student> students = null;
        List<Score> scores = scoreService.selectAll();
        List<Teacher> teachers = teacherService.selectAll();
        List<Topic> topics = topicService.selectAll();
        if(type==0) {
            logger.info("通过教师id查询学生成绩");
            List<Student> studentAll = studentService.selectByTeaId(currentUser);
            p.setTotalUsers(studentAll.size());
            isUpdateSuccess="成功查询到"+studentAll.size()+"个学生";
            logger.info(isUpdateSuccess);
             students = studentService.selectByTeaIdAndPage(currentUser, (page - 1) * p.getPageSize(), p.getPageSize());
        }else if(type==1){
            logger.info("通过班级id查询学生成绩");
            List<Student> studentAll = studentService.selectByClazzId(Integer.parseInt(classId));
            p.setTotalUsers(studentAll.size());
            isUpdateSuccess="成功查询到"+studentAll.size()+"个学生";
            logger.info(isUpdateSuccess);
            students=studentService.selectByClazzIdLimit(Integer.parseInt(classId),(page - 1) * p.getPageSize(), p.getPageSize());
        }else {
            logger.info("通过学生学号查询学生成绩");
            Student studentAll = studentService.selectByStuId(Integer.parseInt(stuId));
            if (studentAll==null){
                isUpdateSuccess="该学生不存在，请重新输入";
                logger.info("学号所对应的学生不存在");
                p.setTotalUsers(0);
            }else {
                isUpdateSuccess="成功查询到一个学生";
                logger.info(isUpdateSuccess);
                p.setTotalUsers(1);
                List<Student> studentAll1=new ArrayList<>();
                studentAll1.add(studentAll);
                students=studentAll1;
            }
        }
        model.addAttribute("page",p);
        model.addAttribute("students",students);
        model.addAttribute("isUpdateSuccess",isUpdateSuccess);
        model.addAttribute("currentUser",currentUser);
        if(classId==null){
            model.addAttribute("classId","");
        }
        model.addAttribute("classId",classId);
        model.addAttribute("type",type);
        if (stuId==null){
            model.addAttribute("stuId","");
        }
        model.addAttribute("stuId",stuId);
        model.addAttribute("clazzes",clazzes);
        model.addAttribute("scores",scores);
        model.addAttribute("teachers",teachers);
        model.addAttribute("topics",topics);
        return "teacher/importReplyScore";
    }

    @RequestMapping("/importReplyScore")
    public String importReplyScore(Integer id,Integer teaId,String classId, String stuId,Integer type,Model model){
        logger.info("教师进入评分界面");
        Score score = scoreService.selectByPrimaryKey(id);
        Student student = studentService.selectByPrimaryKey(score.getId());
        Topic topic = topicService.selectByPrimaryKey(score.getTopicid());
        model.addAttribute("topic",topic);
        model.addAttribute("teaId",teaId);
        model.addAttribute("classId",classId);
        model.addAttribute("stuId",stuId);
        model.addAttribute("type",type);
        model.addAttribute("score",score);
        model.addAttribute("student",student);
        return "teacher/giveMarkAndSuggestion";
    }

    @RequestMapping("/giveMarkAndSuggestion")
    @ResponseBody
    public String giveMarkAndSuggestion(HttpServletRequest request,HttpServletResponse response) throws JSONException {
        logger.info("教师进行评分和写评语");
        String suggestion = request.getParameter("suggestion");
        int score = Integer.parseInt(request.getParameter("score"));
        int id = Integer.parseInt(request.getParameter("id"));
        Score oldScore = scoreService.selectByPrimaryKey(id);
        JSONObject object=new JSONObject();
        if(oldScore.getBlocktime()==0 ||oldScore.getGropleaderscore()==0||oldScore.getAttendancescore()==0){
            object.put("code",-1);
            String msg="组长或者报告还没评分，请等待评分之后再操作";
            logger.info("组长或者报告还没评分");
            object.put("msg",msg);
            return object.toString();
        }else {
            Double finalScore = (oldScore.getGropleaderscore()/oldScore.getBlocktime())*0.2+oldScore.getAttendancescore()*0.4+score*0.4;
            int i = scoreService.updateReplyScoreAndSugg(id, score, finalScore.floatValue(), suggestion);
            if(i>0){
                object.put("code",1);
                String msg="成功给学号为"+oldScore.getStuid()+"的同学评分";
                logger.info(msg);
                object.put("msg",msg);
                return object.toString();
            }else {
                object.put("code",-1);
                String msg="评分失败，请重新尝试！";
                logger.info(msg);
                object.put("msg",msg);
                return object.toString();
            }
        }
    }
    @RequestMapping("/onlyGetScoreDetail")
    public String onlyGetScoreDetail(Integer id,Model model){
        logger.info("教师查看评分详情");
        Score score = scoreService.selectByPrimaryKey(id);
        Student student = studentService.selectByPrimaryKey(id);
        Topic topic = topicService.selectByPrimaryKey(score.getTopicid());
        Teacher teacher = teacherService.selectByPrimayKey(student.getTeaid());
        model.addAttribute("score",score);
        model.addAttribute("student",student);
        model.addAttribute("topic",topic);
        model.addAttribute("teacher",teacher);
        return "teacher/scoreDetail";
    }
    @RequestMapping("/blockDetail")
    public String blockDetail(Integer stuId,Model model){
        logger.info("教师查看学生完成任务详情");
        List<Blocktask> blocktasks = blocktaskService.selectByStuId(stuId);
        Student student = studentService.selectByPrimaryKey(stuId);
        Topic topic = topicService.selectByPrimaryKey(student.getTopicid());
        List<Stagetopic> stagetopics = stagetopicService.selectAll();
        model.addAttribute("blocktasks",blocktasks);
        model.addAttribute("student",student);
        model.addAttribute("topic",topic);
        model.addAttribute("stagetopics",stagetopics);
        return "teacher/blockDetail";
    }
    /**
     * 根据条件将数据导出为Excel
     * 如果需要浏览器发送请求时即下载Excel，就不能用ajax进行传输，所以这里用GET方式进行提交
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/exportExcel")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) {
        logger.info("导出班级成绩");
        Integer classId = Integer.parseInt(request.getParameter("classId"));
        List<Student> students = studentService.selectByClazzId(classId);
        List<Score> scores = scoreService.selectAll();
        List<Teacher> teachers = teacherService.selectAll();
        List<Clazz> clazzes = clazzService.selectAll();
        List<Topic> topics = topicService.selectAll();
        String[] title = {"ID", "学号", "姓名", "班级", "指导教师", "选题名", "组长评分","完成任务次数","报告得分","答辩得分","最终得分"};
        String filename =clazzService.selectByPrimaryKey(classId).getClassname()+".xls";
        String sheetName = "sheet1";
        String[][] content = new String[students.size()][11];
        int i=0;
        try {
                for (Student student : students) {
                    for (Score score : scores) {
                        if (student.getId().intValue() == score.getId().intValue()) {
                            content[i][0] = String.valueOf(student.getId());
                            content[i][1] = String.valueOf(student.getStuid());
                            content[i][2] = String.valueOf(student.getStuname());
                            for(Clazz clazz:clazzes) {
                                if(clazz.getId().intValue()==student.getClassid().intValue()){
                                content[i][3] = clazz.getClassname();
                                }
                            }
                            for(Teacher teacher:teachers){
                                if(teacher.getId().intValue()==student.getTeaid()) {
                                    content[i][4] = teacher.getTeaname();
                                }
                            }
                            for (Topic topic:topics) {
                                if (topic.getId().intValue()==student.getTopicid()) {
                                    content[i][5] = String.valueOf(topic.getTopicname());
                                }
                            }
                            if(score.getBlocktime()==0){
                                content[i][6] = String.valueOf(0);
                            }else {
                                content[i][6] = String.valueOf(score.getGropleaderscore() / score.getBlocktime());
                            }
                            content[i][7] = String.valueOf(score.getBlocktime());
                            content[i][8] = String.valueOf(score.getAttendancescore());
                            content[i][9] = String.valueOf(score.getReplyscore());
                            content[i][10] = String.valueOf(score.getFinalscore());
                        }
                    }
                    i=i+1;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);
        try {
            // 响应到客户端
            this.setResponseHeader(response, filename);
            OutputStream os = response.getOutputStream();
            wb.write(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 向客户端发送响应流方法
     *
     * @param response
     * @param fileName
     */
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @RequestMapping("/teaChangePassWordHtml")
    public String teaChangePassWordHtml(Integer currentUser,Model model){
        logger.info("教师进入修改密码界面");
        Teacher teacher = teacherService.selectByPrimayKey(currentUser);
        model.addAttribute("teacher",teacher);
        return "teacher/teaChangePassword";
    }

    @RequestMapping("/teaChangePassword")
    @ResponseBody
    public String teaChangePassword(HttpServletRequest request,HttpServletResponse response) throws JSONException {
        logger.info("教师修改密码");
        int id = Integer.parseInt(request.getParameter("id"));
        int teaId = Integer.parseInt(request.getParameter("teaId"));
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String rePassword = request.getParameter("rePassword");
        Teacher teacher = teacherService.selectByPrimayKey(id);
        JSONObject object = new JSONObject();
        if (teacher.getPassword().equals(oldPassword)){
            if (newPassword.equals(rePassword)){
                int i = teacherService.updatePassword(id, newPassword);
                if(i>0){
                    object.put("code",1);
                    String msg="密码修改成功！请重新登录";
                    logger.info("教师修改密码成功");
                    object.put("msg",msg);
                    return object.toString();
                }else {
                    object.put("code",-1);
                    String msg="修改失败，请重新尝试！";
                    logger.info("教师修改密码失败");
                    object.put("msg",msg);
                    return object.toString();
                }
            }else {
                object.put("code",-1);
                String msg="两次输入密码不一致，请重新输入！";
                logger.info("教师修改密码失败");
                object.put("两次输入密码不一致",msg);
                return object.toString();
            }
        }else {
            object.put("code",-1);
            String msg="原密码输入不正确，请重新输入！";
            logger.info("原密码输入不正确");
            object.put("msg",msg);
            return object.toString();
        }
    }
}
