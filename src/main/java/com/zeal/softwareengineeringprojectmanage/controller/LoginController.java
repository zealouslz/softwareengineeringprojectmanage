package com.zeal.softwareengineeringprojectmanage.controller;

import com.zeal.softwareengineeringprojectmanage.bean.Admin;
import com.zeal.softwareengineeringprojectmanage.bean.Department;
import com.zeal.softwareengineeringprojectmanage.bean.Student;
import com.zeal.softwareengineeringprojectmanage.bean.Teacher;
import com.zeal.softwareengineeringprojectmanage.service.AdminService;
import com.zeal.softwareengineeringprojectmanage.service.StudentService;
import com.zeal.softwareengineeringprojectmanage.service.TeacherService;
import com.zeal.softwareengineeringprojectmanage.util.CreateValidateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    StudentService studentService;
    @Autowired
    AdminService adminService;
    @Autowired
    TeacherService teacherService;
    @RequestMapping (value = "/user/login")
    public String login(@RequestParam("username") Integer userid,
                        @RequestParam("password") String password,
                        @RequestParam("verification") String verification,
                        @RequestParam("identity") String identity,
                        HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(true);
        String code = (String) session.getAttribute("code");
            if (!code.equals(verification)) {

                redirectAttributes.addFlashAttribute("verificationError","验证码错误");
                return "redirect:/";
            } else if ("administrator".equals(identity)) {
                Admin admin = adminService.selectByAdminId(userid);
                if (admin == null) {
                    redirectAttributes.addFlashAttribute("error","该管理员账户不存在");
                    return "redirect:/";
                } else {
                    if (admin.getPassword().equals(password)) {
                        session.setAttribute("loginUser", "管理员：" + admin.getUserid());
                        return "redirect:/adminmain.html";
                    } else {
                        redirectAttributes.addFlashAttribute("error", "管理员密码错误");
                        return "redirect:/";
                    }
                }

            } else if ("teacher".equals(identity)) {
                Teacher teacher = teacherService.selectByTeaId(userid);
                if (teacher == null) {
                    redirectAttributes.addFlashAttribute("error", "该教师账户不存在");
                    return "redirect:/";
                } else {
                    if (teacher.getPassword().equals(password)) {
                        session.setAttribute("loginUser", teacher.getTeaname());
                        session.setAttribute("userId",teacher.getId());
                        return "redirect:/teachermain.html";
                    } else {
                        redirectAttributes.addFlashAttribute("error", "教师密码错误");
                        return "redirect:/";
                    }
                }

            } else if ("student".equals(identity)) {
                Student student = studentService.selectByStuId(userid);
                if (student == null) {
                    redirectAttributes.addFlashAttribute("error", "该学生账户不存在");
                    return "redirect:/";
                } else {
                    if (student.getPassword().equals(password)) {
                        session.setAttribute("loginUser", student.getStuname());
                        session.setAttribute("stuId",student.getId());
                        return "redirect:/studentmain.html";
                    } else {
                        redirectAttributes.addFlashAttribute("error", "学生密码错误");
                        return "redirect:/";
                    }
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "用户密码错误");
                return "redirect:/";
            }

    }


    //验证码
    @RequestMapping(value = "/getCode")
    public void getValidateCode(HttpServletResponse response,HttpServletRequest request) throws IOException {

        //创建输出流
        OutputStream outputStream = response.getOutputStream();
        //获取session
        HttpSession session = request.getSession();
        //获取验证码
        CreateValidateCode createValidateCode = new CreateValidateCode();
        String generateVerifyCode = createValidateCode.getString();
        //将验证码存入session，做登录验证
        session.setAttribute("code", generateVerifyCode);
        System.out.println(generateVerifyCode);
        //获取验证码图片
        BufferedImage image = createValidateCode.getImage();
        ImageIO.write(image, "png", outputStream);
        //关流
        outputStream.flush();
        outputStream.close();
    }

    }
