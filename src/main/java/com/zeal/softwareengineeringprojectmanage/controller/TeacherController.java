package com.zeal.softwareengineeringprojectmanage.controller;

import com.zeal.softwareengineeringprojectmanage.bean.Page;
import com.zeal.softwareengineeringprojectmanage.bean.Teacher;
import com.zeal.softwareengineeringprojectmanage.bean.Topic;
import com.zeal.softwareengineeringprojectmanage.service.TeacherService;
import com.zeal.softwareengineeringprojectmanage.service.TopicService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class TeacherController {

    @Autowired
    TopicService topicService;
    @Autowired
    TeacherService teacherService;
    @Value("${file.uploadFolder}")
    private String uploadPath; //文件上传的地址
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
            File dir = new File(uploadPath);
            if(!dir.exists()) {
                dir.mkdir();
            }
            String path = uploadPath + file.getOriginalFilename();
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
        File dir = new File(uploadPath);
        if(!dir.exists()) {
            dir.mkdir();
        }
        String path = uploadPath + file.getOriginalFilename();
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

    @RequestMapping("/updateTopic/delete/{id}")
    public String deleteTopicById(@PathVariable("id") Integer id) throws UnsupportedEncodingException {
        Topic topic = topicService.selectByPrimaryKey(id);
        int i = topicService.deleteByPrimary(id);
        String isUpdateSuccess="成功删除了"+i+"条选题";
        return "redirect:/manageTopic?currentUser="+topic.getTeaid()+"&page=1&isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8");
    }
}
