package com.zeal.softwareengineeringprojectmanage.controller;

import com.zeal.softwareengineeringprojectmanage.bean.*;
import com.zeal.softwareengineeringprojectmanage.service.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.event.ListDataEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    ClazzService clazzService;
    @Value("${file.uploadTopicFolder}")
    private String uploadTopicFilePath; //选题文件上传的地址
    @Value("${file.uploadStageTopicFolder}")
    private String uploadStageTopicFilePath; //选题文件上传的地址
    @RequestMapping("/manageTopic")
    public String manageTopic(Integer currentUser,Integer page,String isUpdateSuccess, Model model){
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
            File tempFile = null;
            try {
                tempFile =  new File(path);
                FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
            }catch (Exception e){
                e.printStackTrace();
                model.addAttribute("IsSuccess","文件上传失败");
                Teacher teacher = teacherService.selectByPrimayKey(topic.getTeaid());
                model.addAttribute("teacher",teacher);
                return "teacher/addTopic";
            }
            int insert = topicService.insert(topic);
            String isUpdateSuccess="成功发布"+insert+"选题";
            return "redirect:/manageTopic?currentUser="+teaid+"&page=1&isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8");
        }else{
            model.addAttribute("IsSuccess","文件为空，上传失败");
            Teacher teacher = teacherService.selectByPrimayKey(teaid);
            model.addAttribute("teacher",teacher);
            return "teacher/addTopic";
        }
    }

    @RequestMapping("/getTopicDetail/{id}")
    public String getTopicDetail(@PathVariable("id") Integer topicId,
                                 @RequestParam("IsSuccess") String IsSuccess,
                                 Model model){
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
        File tempFile = null;
        try {
            tempFile =  new File(path);
            FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
        }catch (Exception e){
            e.printStackTrace();
            String IsSuccess="文件上传失败";
            return "redirect:/getTopicDetail/"+id+"?IsSuccess="+URLEncoder.encode(IsSuccess,"UTF-8");
        }
        int i = topicService.updateByPrimaryKey(topic);
        String isUpdateSuccess="成功更新"+i+"条选题";
        return "redirect:/manageTopic?currentUser="+teaid+"&page=1&isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8");
    }

    @RequestMapping("/updateTopic/delete/")
    @ResponseBody
    public String deleteTopicById(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, JSONException {
        int id = Integer.parseInt(request.getParameter("id"));
        int i = topicService.deleteByPrimary(id);
        JSONObject object = new JSONObject();
        if(i>0) {
            object.put("code",1);
            String msg="成功删除了"+i+"条选题";
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            object.put("msg","修改失败，请重新尝试！！");
            return object.toString();
        }
    }

    @RequestMapping("/viewChooseDetail")
    public String viewChoooseDetail(Integer currentUser,Integer page,Model model){
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

    @RequestMapping("/confirmGroup/{id}")
    public String confirmGroup(@PathVariable("id") Integer topicId,Model model){
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
        int id = Integer.parseInt(request.getParameter("id"));
        int groupId = Integer.parseInt(request.getParameter("groupId"));
        List<Student> students1 = studentService.selectByGroupId(groupId);
        JSONObject object=new JSONObject();
        if(students1.size()!=0){
            String msg = "组号"+groupId+"已被分配，请重新输入";   //设置Json对象的属性
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
                object.put("code",1);
                object.put("msg",msg);
                return object.toString();

            } else {
                String msg = "分组失败，请重新尝试";   //设置Json对象的属性
                object.put("code",-1);
                object.put("msg",msg);
                return object.toString();
            }
        }
    }

    @RequestMapping(value = "/stopTopic")
    @ResponseBody
    public String stopTopic(HttpServletRequest request,HttpServletResponse response) throws JSONException, IOException {
        int topicId = Integer.parseInt(request.getParameter("topicId"));
        int i = topicService.deleteByPrimary(topicId);
        JSONObject object = new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="id为"+topicId+"的选题已停开";
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg = "停开失败，请重新尝试";   //设置Json对象的属性
            object.put("msg",msg);
            return object.toString();
        }
    }

    @RequestMapping("/manageGroupAndTopic")
    public String manageGroupAndTopic(Integer currentUser,Integer page,Model model){
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
        List<Student> students = studentService.selectByStuIdAndTeaId(stuId,currentUser);
        if (students.size()==0)
        {
            model.addAttribute("isSelectSuccess","您输入的学号不存在，或者指导教师不是你");
        }
        model.addAttribute("students",students);
        Teacher teacher = teacherService.selectByPrimayKey(currentUser);
        model.addAttribute("teacher",teacher);
        List<Clazz> clazzes = clazzService.selectAll();
        model.addAttribute("clazzes",clazzes);
        return "teacher/manageGroupAndTopic";
    }

    @RequestMapping("/getStuGroupAndTopicDetail/{stuid}")
    public String updateStu(@PathVariable("stuid") Integer stuid,Model model){
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
            return object.toString();
        }else {
        int i = studentService.updateGroupAndTopicByStuId(stuid, isgroupleader, groupid, topicid);
        Student student = studentService.selectByStuId(stuid);
        if (i>0){
            object.put("code",1);
            String msg="成功将"+student.getStuname()+"的组号改为"+student.getGroupid()+";选题号为"+student.getTopicid();
            object.put("msg",msg);
            return object.toString();
        }
          else {
            object.put("code",-1);
            object.put("msg","修改失败，请重新尝试！！");
            return object.toString();
        }
    }
    }

    @RequestMapping("/manageStageTask")
    public String manageStageTask(Integer currentUser,Integer page,String isUpdateSuccess,Model model){
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
            File tempFile = null;
            try {
                tempFile =  new File(path);
                FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
            }catch (Exception e){
                e.printStackTrace();
                model.addAttribute("IsSuccess","文件上传失败");
                Teacher teacher = teacherService.selectByPrimayKey(teaid);
                model.addAttribute("teacher",teacher);
                return "teacher/addStageTopic";
            }
            int insert = stagetopicService.insert(stagetopic);
            if(insert>0) {
                String isUpdateSuccess = "成功发布" + insert + "选题";
                return "redirect:/manageStageTask?currentUser=" + teaid + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
            }else {
                model.addAttribute("IsSuccess","阶段性任务发布失败，请重试");
                Teacher teacher = teacherService.selectByPrimayKey(teaid);
                model.addAttribute("teacher",teacher);
                return "teacher/addStageTopic";
            }
        }else {
            model.addAttribute("IsSuccess", "文件为空，上传失败");
            Teacher teacher = teacherService.selectByPrimayKey(teaid);
            model.addAttribute("teacher", teacher);
            return "teacher/addStageTopic";
        }
    }

    @RequestMapping("/getStageTopicDetail/{id}")
    public String getStageTopicDetail(@PathVariable("id") Integer id,Model model){
        Stagetopic stagetopic = stagetopicService.selectByPrimaryKey(id);
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
            String isUpdateSuccess="成功更新"+i+"条选题";
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
            model.addAttribute("IsSuccess",IsSuccess);
            Stagetopic stagetopic1 = stagetopicService.selectByPrimaryKey(id);
            Teacher teacher = teacherService.selectByPrimayKey(stagetopic1.getTeaid());
            model.addAttribute("stagetopic",stagetopic1);
            model.addAttribute("teacher",teacher);
            return "teacher/updateStageTopic";
        }
        int i = stagetopicService.updateByPrimaryKey(stagetopic);
        String isUpdateSuccess="成功更新"+i+"条选题";
        return "redirect:/manageStageTask?currentUser=" + teaid + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8");
    }

    @RequestMapping("/deleteStageTopic")
    @ResponseBody
    public String deleteStageTopic(HttpServletRequest request,HttpServletResponse response) throws JSONException {
        int id = Integer.parseInt(request.getParameter("id"));
        int i = stagetopicService.deleteByPrimaryKey(id);
        JSONObject object = new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="成功删除id为"+id+"的阶段性任务";
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            object.put("msg","修改失败，请重新尝试！！");
            return object.toString();
        }
    }
}
