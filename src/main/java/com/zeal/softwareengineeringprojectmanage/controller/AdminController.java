package com.zeal.softwareengineeringprojectmanage.controller;

import com.sun.deploy.net.HttpResponse;
import com.zeal.softwareengineeringprojectmanage.bean.Clazz;
import com.zeal.softwareengineeringprojectmanage.bean.Student;
import com.zeal.softwareengineeringprojectmanage.bean.Teacher;
import com.zeal.softwareengineeringprojectmanage.service.ClazzService;
import com.zeal.softwareengineeringprojectmanage.service.ImportService;
import com.zeal.softwareengineeringprojectmanage.service.StudentService;
import com.zeal.softwareengineeringprojectmanage.service.TeacherService;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    public String importStudent(Student student, HttpSession session){
            try {
                studentService.saveStudent(student);
                session.setAttribute("addIsSuccess","学生添加成功");
                return "redirect:/studentImport";
            }catch (Exception e){
                session.setAttribute("addIsSuccess","学生添加失败");
                return "redirect:/studentImport";
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

    @RequestMapping("/fileUpload")
    public String fileUpload(@RequestParam("studentFile") MultipartFile file,HttpSession session){
        if (file.isEmpty()) {
            session.setAttribute("IsSuccess","上传失败");
            return "redirect:/studentImport";
        }
        String fileName = file.getOriginalFilename();
        if(!fileName.contains(".xlsx"))
        {
            session.setAttribute("IsSuccess","格式不是.xlsx,请重新上传");
            return "redirect:/studentImport";
        }else {
            String filePath = "D:\\graduation project\\softwareengineeringprojectmanage\\src\\main\\resources\\uploadfile\\";
            File dest = new File(filePath + fileName);
            try {
                file.transferTo(dest);
                session.setAttribute("IsSuccess", "上传成功");
                return "redirect:/studentImport";
            } catch (IOException e) {
                session.setAttribute("IsSuccess", "上传失败");
                return "redirect:/studentImport";
            }
        }
    }

    @RequestMapping(value="/upload",method= RequestMethod.POST)
    public String  uploadExcel(HttpServletRequest request,HttpSession session) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        InputStream inputStream =null;
        List<List<Object>> list = null;
        MultipartFile file = multipartRequest.getFile("filename");
        if(file.isEmpty()){
            session.setAttribute("IsSuccess", "文件不能为空");
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
            studentService.saveStudent(student);
            //调用mapper中的insert方法
        }
        session.setAttribute("IsSuccess", "上传成功");
        return "redirect:/studentImport";
    }

}
