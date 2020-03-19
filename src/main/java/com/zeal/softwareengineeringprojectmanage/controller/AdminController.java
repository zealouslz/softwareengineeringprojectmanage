package com.zeal.softwareengineeringprojectmanage.controller;

import com.sun.deploy.net.HttpResponse;
import com.zeal.softwareengineeringprojectmanage.bean.*;
import com.zeal.softwareengineeringprojectmanage.service.ClazzService;
import com.zeal.softwareengineeringprojectmanage.service.ImportService;
import com.zeal.softwareengineeringprojectmanage.service.StudentService;
import com.zeal.softwareengineeringprojectmanage.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    StudentService studentService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    ClazzService clazzService;
    @Autowired
    private ImportService importService;


    @RequestMapping("/addSingleStudent")
    public String importStudent(Student student, HttpSession session, RedirectAttributes redirectAttributes){
            try {
                studentService.saveStudent(student);
                redirectAttributes.addFlashAttribute("addIsSuccess","学生添加成功");
                return "redirect:/studentImport";
            }catch (Exception e){
                redirectAttributes.addFlashAttribute("addIsSuccess","学生添加失败");
                return "redirect:/studentImport";
            }
    }
    @RequestMapping("/addSingleTeacher")
    public String importTeacher(Teacher teacher, HttpSession session, RedirectAttributes redirectAttributes){
        try {
            teacherService.insert(teacher);
            redirectAttributes.addFlashAttribute("addIsSuccess","教师添加成功");
            return "redirect:/teacherImport";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("addIsSuccess","教师添加失败");
            return "redirect:/teacherImport";
        }
    }

    @RequestMapping("/studentImport")
    public String ToStudentImport(Model model,HttpSession session){
        List<Teacher> teachers = teacherService.selectAll();
        List<Clazz> clazzes = clazzService.selectAll();
        model.addAttribute("teachers",teachers);
        model.addAttribute("clazzes",clazzes);
        return "admin/studentImport";
    }
    @RequestMapping("/teacherImport")
    public String ToTeacherImport(Model model,HttpSession session){
        return "admin/teacherImport";
    }
    @RequestMapping(value="/upload",method= RequestMethod.POST)
    public String  uploadExcel(HttpServletRequest request,HttpSession session,RedirectAttributes redirectAttributes) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        InputStream inputStream =null;
        List<List<Object>> list = null;
        int insert=0;
        MultipartFile file = multipartRequest.getFile("filename");
        if(file.isEmpty()){
            redirectAttributes.addFlashAttribute("IsSuccess", "文件不能为空");
            return "redirect:/studentImport";
        }
        inputStream = file.getInputStream();
        list = importService.getBankListByExcel(inputStream,file.getOriginalFilename());
        inputStream.close();
//连接数据库部分
        for (int i = 0; i < list.size(); i++) {
            List<Object> lo = list.get(i);
            Student student=new Student();
            student.setStuid((int)(Double.parseDouble(String.valueOf(lo.get(1)))));
            student.setStuname(String.valueOf(lo.get(2)));
            student.setPassword(String.valueOf(lo.get(3)));
            student.setClassid((int)(Double.parseDouble(String.valueOf(lo.get(4)))));
            student.setTeaid((int)(Double.parseDouble(String.valueOf(lo.get(5)))));
             insert += studentService.saveStudent(student);
            //调用mapper中的insert方法
        }
        redirectAttributes.addFlashAttribute("IsSuccess", "成功插入"+insert+"条数据");
        return "redirect:/studentImport";
    }
    @RequestMapping(value="/uploadTea",method= RequestMethod.POST)
    public String  uploadTeaExcel(HttpServletRequest request,HttpSession session,RedirectAttributes redirectAttributes) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        InputStream inputStream =null;
        List<List<Object>> list = null;
        int insert=0;
        MultipartFile file = multipartRequest.getFile("filename");
        if(file.isEmpty()){
            redirectAttributes.addFlashAttribute("IsSuccess", "文件不能为空");
            return "redirect:/teacherImport";
        }
        inputStream = file.getInputStream();
        list = importService.getBankListByExcel(inputStream,file.getOriginalFilename());
        inputStream.close();
//连接数据库部分
        for (int i = 0; i < list.size(); i++) {
            List<Object> lo = list.get(i);
            Teacher teacher=new Teacher();
            teacher.setTeaid((int)(Double.parseDouble(String.valueOf(lo.get(1)))));
            teacher.setTeaname(String.valueOf(lo.get(2)));
            teacher.setPassword(String.valueOf(lo.get(3)));
            insert += teacherService.insert(teacher);
            //调用mapper中的insert方法
        }
        redirectAttributes.addFlashAttribute("IsSuccess", "成功插入"+insert+"条数据");
        return "redirect:/teacherImport";
    }
    @RequestMapping("/manageStu")
    public String manageStu(Model model){
        List<Clazz> clazzes = clazzService.selectAll();
        model.addAttribute("clazzes",clazzes);
        return "admin/manageStu";
    }

    @RequestMapping("/getStuDetail/{stuid}")
    public String updateStu(@PathVariable("stuid") Integer stuid,Model model){
        Student student = studentService.selectByStuId(stuid);
        model.addAttribute("student",student);
        List<Clazz> clazzes = clazzService.selectAll();
        model.addAttribute("clazzes",clazzes);
        List<Teacher> teachers = teacherService.selectAll();
        model.addAttribute("teachers",teachers);
        return "admin/updateStu";
    }
    @RequestMapping("/getTeaDetail/{id}")
    public String updateTea(@PathVariable("id") Integer id,Model model){
        Teacher teacher = teacherService.selectByPrimayKey(id);
        model.addAttribute("teacher",teacher);
        return "admin/updateTea";
    }
    @RequestMapping("/updateStuByStuId")
    public String updateStuByStuId(Student student,Model model) throws UnsupportedEncodingException {
        Integer stuid = student.getStuid();
        String stuname = student.getStuname();
        String password = student.getPassword();
        Integer classid = student.getClassid();
        Integer teaid = student.getTeaid();
        int isgroupleader = (int)student.getIsgroupleader();
        Integer groupid = student.getGroupid();
        Integer topicid = student.getTopicid();
        int i = studentService.updateByStuId(stuid,stuname,password,classid,teaid,isgroupleader,groupid,topicid);
        String isUpdateSuccess="成功修改了"+i+"条数据";
        return "redirect:/student/page?classid="+classid+"&isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8")+"&page=1";
    }
    @RequestMapping("/updateTeaById")
    public String updateTeaById(Teacher teacher,Model model) throws UnsupportedEncodingException {
        TeacherExample teacherExample=new TeacherExample();
        TeacherExample.Criteria criteria = teacherExample.createCriteria();
        criteria.andIdEqualTo(teacher.getId());
        int i = teacherService.updateByExample(teacher,teacherExample);
        String isUpdateSuccess="成功修改了"+i+"条数据";
        return "redirect:/teacher/page?&isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8")+"&page=1";
    }
    @RequestMapping("/upDateStu/delete/{stuid}")
    public String deleteStuByStuId(@PathVariable("stuid") Integer stuId,
                                   Model model) throws UnsupportedEncodingException {
        Student student = studentService.selectByStuId(stuId);
        Integer classid = student.getClassid();
        int i = studentService.deleteStuByStuId(stuId);
        String isUpdateSuccess="成功删除了"+i+"条数据";
        return "redirect:/student/page?classid="+classid+ "&isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8")+"&page=1";
    }
    @RequestMapping("/upDateTea/delete/{id}")
    public String deleteTeaById(@PathVariable("id") Integer Id,
                                   Model model) throws UnsupportedEncodingException {
        int i = teacherService.deleteByPrimaryKey(Id);
        String isUpdateSuccess="成功删除了"+i+"条数据";
        return "redirect:/teacher/page?isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8")+"&page=1";
    }

    @RequestMapping(value = "/student/page")
    public String findStuByPage(String classid,int page,String isUpdateSuccess, Model model) {
            List<Clazz> clazzes = clazzService.selectAll();
            model.addAttribute("clazzes",clazzes);
            List<Teacher> teachers = teacherService.selectAll();
            model.addAttribute("teachers",teachers);
            Page p=new Page();
            p.setTotalUsers(studentService.selectByClazzId(Integer.parseInt(classid)).size());
            p.setCurrentPage(page);
            List<Student> list = studentService.selectByClazzIdLimit(Integer.parseInt(classid),(page - 1) * p.getPageSize(), p.getPageSize());
//            查询结果是list集合
            model.addAttribute("list", list);
            model.addAttribute("page", p);
            model.addAttribute("clazzid",classid);
            model.addAttribute("isUpdateSuccess",isUpdateSuccess);
            return "admin/manageStu";
    }

    @RequestMapping("/teacher/page")
    public String findTeaByPage(int page,String isUpdateSuccess,Model model){
        Page p=new Page();
        p.setTotalUsers(teacherService.selectAll().size());
        p.setCurrentPage(page);
        List<Teacher> teachers = teacherService.selectByPage((page - 1) * p.getPageSize(), p.getPageSize());
        model.addAttribute("teachers", teachers);
        model.addAttribute("isUpdateSuccess",isUpdateSuccess);
        model.addAttribute("page", p);
        return "admin/manageTea";
    }
}
