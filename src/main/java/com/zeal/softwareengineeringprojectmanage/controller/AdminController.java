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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    ScoreService scoreService;
    @Autowired
    BlocktaskService blocktaskService;
    @Autowired
    StagetopicService stagetopicService;
    @Autowired
    TopicService topicService;
    @Value("${uploadOutstandingCaseFile.location}")
    private String uploadOutstandingCaseFilePath; //选题文件上传的地址
    @Value("${uploadOutstandingCaseFile.resourceHandler}")
    private String uploadOutstandingCaseFileResourceHandler;
    @Value("${uploadTopicFile.location}")
    private String uploadTopicFilePath; //选题文件上传的地址
    @Value("${uploadTopicFile.resourceHandler}")
    private String uploadTopicResourceHandler;
    @Autowired
    OutstandingcaseService outstandingcaseService;

    Logger logger = LoggerFactory.getLogger(getClass());
    @RequestMapping("/addSingleStudent")
    public String importStudent(Student student, HttpSession session, RedirectAttributes redirectAttributes){
        logger.info("单独添加学生信息");
        List<Student> students = studentService.selectAll();
        for(Student stu:students){
            if(stu.getStuid().intValue()==student.getStuid().intValue()){
                redirectAttributes.addFlashAttribute("addIsSuccess","此学号已存在，请重新添加");
                logger.info("学号已存在，添加失败");
                return "redirect:/studentImport";
            }
        }
        try {
                studentService.saveStudent(student);
                redirectAttributes.addFlashAttribute("addIsSuccess","学生添加成功");
                logger.info("成功添加一条学生信息");
                return "redirect:/studentImport";
            }catch (Exception e){
                redirectAttributes.addFlashAttribute("addIsSuccess","学生添加失败");
                logger.info("学生添加异常");
                return "redirect:/studentImport";
            }
    }
    @RequestMapping("/addSingleTeacher")
    public String importTeacher(Teacher teacher, HttpSession session, RedirectAttributes redirectAttributes){
        logger.info("单独添加一条教师信息");
        List<Teacher> teachers = teacherService.selectAll();
        for(Teacher tea:teachers){
            if(tea.getTeaid().intValue()==teacher.getTeaid().intValue()||tea.getTeaname().equals(teacher.getTeaname())){
                redirectAttributes.addFlashAttribute("addIsSuccess","该教师已存在！");
                logger.info("添加失败，教师已存在");
                return "redirect:/teacherImport";
            }
        }
        try {
            teacherService.insert(teacher);
            redirectAttributes.addFlashAttribute("addIsSuccess","教师添加成功");
            logger.info("成功添加一条教师信息");
            return "redirect:/teacherImport";
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("addIsSuccess","教师添加失败");
            logger.info("单独添加教师失败");
            return "redirect:/teacherImport";
        }
    }

    @RequestMapping("/studentImport")
    public String ToStudentImport(Model model,HttpSession session){
        logger.info("进入批量导入学生信息界面");
        List<Teacher> teachers = teacherService.selectAll();
        List<Clazz> clazzes = clazzService.selectAll();
        model.addAttribute("teachers",teachers);
        model.addAttribute("clazzes",clazzes);
        return "admin/studentImport";
    }
    @RequestMapping("/teacherImport")
    public String ToTeacherImport(Model model,HttpSession session){
        logger.info("进入批量导入教师信息界面");
        return "admin/teacherImport";
    }
    @RequestMapping(value="/upload",method= RequestMethod.POST)
    public String  uploadExcel(HttpServletRequest request,HttpSession session,RedirectAttributes redirectAttributes) throws Exception {
        logger.info("上传学生信息文件");
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        InputStream inputStream =null;
        List<List<Object>> list = null;
        int insert=0;
        MultipartFile file = multipartRequest.getFile("filename");
        if(file.isEmpty()){
            redirectAttributes.addFlashAttribute("IsSuccess", "文件不能为空");
            logger.info("上传学生信息文件为空");
            return "redirect:/studentImport";
        }
        try {
            inputStream = file.getInputStream();
            list = importService.getBankListByExcel(inputStream,file.getOriginalFilename());
            inputStream.close();
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("IsSuccess","文件格式错误请重新上传，必须是.xls或者.xlsx");
            logger.info("学生信息文件格式错误");
            return "redirect:/studentImport";
        }
//连接数据库部分
        for (int i = 0; i < list.size(); i++) {
            List<Object> lo = list.get(i);
            Student student=new Student();
            student.setStuid((int)(Double.parseDouble(String.valueOf(lo.get(1)))));
            student.setStuname(String.valueOf(lo.get(2)));
            student.setPassword(String.valueOf(lo.get(3)));
            student.setClassid((int)(Double.parseDouble(String.valueOf(lo.get(4)))));
            student.setTeaid((int)(Double.parseDouble(String.valueOf(lo.get(5)))));
            try {
                insert += studentService.saveStudent(student);
            }
             catch (Exception e){
                 redirectAttributes.addFlashAttribute("IsSuccess", "插入失败，请检查表格格式或者班级id和教师id是否存在");
                 logger.info("导入失败，表中对应教师id和班级id不存在");
                 return "redirect:/studentImport";
             }
            //调用mapper中的insert方法
        }
        redirectAttributes.addFlashAttribute("IsSuccess", "成功插入"+insert+"条数据");
        logger.info("成功插入"+insert+"条学生信息");
        return "redirect:/studentImport";
    }
    @RequestMapping(value="/uploadTea",method= RequestMethod.POST)
    public String  uploadTeaExcel(HttpServletRequest request,HttpSession session,RedirectAttributes redirectAttributes) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        logger.info("导入教师信息");
        InputStream inputStream =null;
        List<List<Object>> list = null;
        int insert=0;
        MultipartFile file = multipartRequest.getFile("filename");
        if(file.isEmpty()){
            redirectAttributes.addFlashAttribute("IsSuccess", "文件不能为空");
            logger.info("导入失败，教师信息表为空");
            return "redirect:/teacherImport";
        }
        try {
            inputStream = file.getInputStream();
            list = importService.getBankListByExcel(inputStream,file.getOriginalFilename());
            inputStream.close();
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("IsSuccess","文件格式错误请重新上传，必须是.xls或者.xlsx");
            logger.info("导入失败，教师信息表格式错误");
            return "redirect:/teacherImport";
        }
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
        logger.info("成功插入"+insert+"条教师信息");
        return "redirect:/teacherImport";
    }
    @RequestMapping("/manageStu")
    public String manageStu(Integer type,Integer page,String classId,String stuId,String isUpdateSuccess, Model model){
        logger.info("进入管理学生界面");
        List<Clazz> clazzes = clazzService.selectAll();
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(8);
        List<Student> students = null;
        List<Score> scores = scoreService.selectAll();
        List<Teacher> teachers = teacherService.selectAll();
        List<Topic> topics = topicService.selectAll();
        if(type==1){
            logger.info("按classId查询学生");
            List<Student> studentAll = studentService.selectByClazzId(Integer.parseInt(classId));
            p.setTotalUsers(studentAll.size());
            isUpdateSuccess="成功查询到"+studentAll.size()+"个学生";
            logger.info(isUpdateSuccess);
            students=studentService.selectByClazzIdLimit(Integer.parseInt(classId),(page - 1) * p.getPageSize(), p.getPageSize());
        }else if (type==2){
            logger.info("按学号查询学生");
            Student studentAll = studentService.selectByStuId(Integer.parseInt(stuId));
            if (studentAll==null){
                isUpdateSuccess="该学生不存在，请重新输入";
                logger.info(isUpdateSuccess);
                p.setTotalUsers(0);
            }else {
                isUpdateSuccess="成功查询到一个学生";
                logger.info(isUpdateSuccess);
                p.setTotalUsers(1);
                List<Student> studentAll1=new ArrayList<>();
                studentAll1.add(studentAll);
                students=studentAll1;
            }
        }else if(type==0){
            logger.info("查询所有学生信息");
            List<Student> studentAll = studentService.selectAll();
            p.setTotalUsers(studentAll.size());
            students = studentService.selectAllAndPage((page - 1) * p.getPageSize(), p.getPageSize());
        }
        model.addAttribute("page",p);
        model.addAttribute("students",students);
        model.addAttribute("isUpdateSuccess",isUpdateSuccess);
        model.addAttribute("classId",classId);
        model.addAttribute("type",type);
        model.addAttribute("stuId",stuId);
        model.addAttribute("clazzes",clazzes);
        model.addAttribute("scores",scores);
        model.addAttribute("teachers",teachers);
        model.addAttribute("topics",topics);
        return "admin/manageStu";
    }

    @RequestMapping("/getStuDetail")
    public String updateStu(Integer id,Integer type,String classId,String stuId,Model model){
        logger.info("进入学生信息详情页面");
        Student student = studentService.selectByStuId(id);
        model.addAttribute("student",student);
        List<Clazz> clazzes = clazzService.selectAll();
        model.addAttribute("clazzes",clazzes);
        List<Teacher> teachers = teacherService.selectAll();
        model.addAttribute("teachers",teachers);
        model.addAttribute("type",type);
        model.addAttribute("classId",classId);
        model.addAttribute("stuId",stuId);
        return "admin/updateStu";
    }
    @RequestMapping("/getTeaDetail")
    public String updateTea(Integer teaId,Model model){
        logger.info("进入教师信息详情页面");
        Teacher teacher = teacherService.selectByPrimayKey(teaId);
        model.addAttribute("teacher",teacher);
        return "admin/updateTea";
    }
    @RequestMapping("/updateStuByStuId")
    public String updateStuByStuId(Student student,Integer type,String classId,String stuId,Model model) throws UnsupportedEncodingException {
        logger.info("根据学生id更新学生信息");
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
        logger.info("成功修改"+student.getId()+"的信息");
        return "redirect:/manageStu?classId="+classId+"&isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8")+"&page=1&type="+type+"&stuId="+stuId;
    }
    @RequestMapping("/updateTeaById")
    @ResponseBody
    public String updateTeaById(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, JSONException {
        logger.info("根据教师id更新教师信息");
        int id = Integer.parseInt(request.getParameter("id"));
        String teaname = request.getParameter("teaname");
        String password = request.getParameter("password");
        int teaid = Integer.parseInt(request.getParameter("teaid"));
        Teacher teacher=new Teacher();
        teacher.setId(id);
        teacher.setTeaid(teaid);
        teacher.setTeaname(teaname);
        teacher.setPassword(password);
        TeacherExample teacherExample=new TeacherExample();
        TeacherExample.Criteria criteria = teacherExample.createCriteria();
        criteria.andIdEqualTo(teacher.getId());
        int i = teacherService.updateByExample(teacher,teacherExample);
        JSONObject object=new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="成功修改id为"+id+"的教师信息";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="修改失败，请重新尝试！";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }
    }
    @RequestMapping("/deleteStu")
    @ResponseBody
    public String deleteStuByStuId(HttpServletRequest request) throws UnsupportedEncodingException, JSONException {
        int id = Integer.parseInt(request.getParameter("id"));
        int i = studentService.deleteStuByStuId(id);
        logger.info("删除id为"+id+"的学生信息");
        JSONObject object=new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="成功删除学号为"+id+"的学生";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="删除失败，请重新尝试！";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }
    }
    @RequestMapping("/deleteTea")
    @ResponseBody
    public String deleteTeaById(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, JSONException {
        int id = Integer.parseInt(request.getParameter("id"));
        logger.info("删除id为"+id+"的教师信息");
        int i = teacherService.deleteByPrimaryKey(id);
        JSONObject object=new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="成功删除id为"+id+"的教师";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="删除失败，请重新尝试！";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }
    }

    @RequestMapping("/manageTea")
    public String findTeaByPage(int page,String isUpdateSuccess,Model model){
        logger.info("进入管理教师信息页面");
        Page p=new Page();
        p.setTotalUsers(teacherService.selectAll().size());
        p.setCurrentPage(page);
        List<Teacher> teachers = teacherService.selectByPage((page - 1) * p.getPageSize(), p.getPageSize());
        model.addAttribute("teachers", teachers);
        model.addAttribute("isUpdateSuccess",isUpdateSuccess);
        model.addAttribute("page", p);
        return "admin/manageTea";
    }

    @RequestMapping("/manageClass")
    public String ManageClass(int page,String isUpdateSuccess ,Model model ){
        logger.info("进入管理班级信息页面");
        Page p=new Page();
        p.setTotalUsers(clazzService.selectAll().size());
        p.setCurrentPage(page);
        List<Clazz> clazzes = clazzService.selectByPage((page - 1) * p.getPageSize(), p.getPageSize());
        model.addAttribute("clazzes",clazzes);
        model.addAttribute("page", p);
        model.addAttribute("isUpdateSuccess",isUpdateSuccess);
        return "admin/manageClass";
    }

    @RequestMapping("/addClass")
    public String addClass(Model model,String SingleSuccess,HttpSession session){
        logger.info("进入添加班级界面");
        model.addAttribute("SingleSuccess",SingleSuccess);
        return "admin/addClass";
    }

    @RequestMapping("/addSingleClass")
    public String addSingleClass(Clazz clazz) throws UnsupportedEncodingException {
        logger.info("单独添加一条班级信息");
        List<Clazz> clazzes = clazzService.selectAll();
        for (Clazz clz:clazzes){
            if(clz.getClassname().equals(clazz.getClassname())){
                int insert = clazzService.insert(clazz);
                String isUpdateSuccess="该班级已存在！";
                logger.info(isUpdateSuccess);
                return "redirect:/addClass?SingleSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8")+"&page=1";
            }
        }
        int insert = clazzService.insert(clazz);
        String isUpdateSuccess="成功添加"+insert+"条数据";
        logger.info("成功添加"+insert+"条班级信息");
        return "redirect:/manageClass?isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8")+"&page=1";
    }

    @RequestMapping(value="/classUpload",method= RequestMethod.POST)
    public String  uploadClassExcel(HttpServletRequest request,HttpSession session,RedirectAttributes redirectAttributes,Model model) throws  Exception{
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        logger.info("导入班级信息文件");
        InputStream inputStream =null;
        List<List<Object>> list = null;
        int insert=0;
        MultipartFile file = multipartRequest.getFile("filename");
        if(file.isEmpty()){
            logger.info("班级信息文件为空，导入失败");
            redirectAttributes.addFlashAttribute("IsSuccess", "文件不能为空");
            return "redirect:/addClass";
        }
        try {
            list = importService.getBankListByExcel(inputStream,file.getOriginalFilename());
            inputStream = file.getInputStream();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("IsSuccess","文件格式错误请重新上传，必须是.xls或者.xlsx");
            logger.info("班级信息文件格式错误");
            return "redirect:/addClass";
        }

//连接数据库部分
        for (int i = 0; i < list.size(); i++) {
            List<Object> lo = list.get(i);
            Clazz clazz=new Clazz();
            clazz.setClassname(String.valueOf(lo.get(1)));
            clazz.setCharge(String.valueOf(lo.get(2)));
            insert += clazzService.insert(clazz);
            //调用mapper中的insert方法
        }
        String isUpdateSuccess="成功添加"+insert+"条数据";
        logger.info("成功添加"+insert+"条班级信息");
        return "redirect:/manageClass?isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8")+"&page=1";
    }

    @RequestMapping("/getClassDetail")
    public String updateClass( Integer classId,Model model){
        logger.info("进入班级信息详情界面");
        Clazz clazz = clazzService.selectByPrimaryKey(classId);
        model.addAttribute("clazz",clazz);
        List<Teacher> teachers = teacherService.selectAll();
        model.addAttribute("teachers",teachers);
        return "admin/updateClass";
    }

    @RequestMapping("/updateClassById")
    @ResponseBody
    public String updateClassById(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, JSONException {
        logger.info("根据班级id更新班级信息");
        int id = Integer.parseInt(request.getParameter("id"));
        String classname = request.getParameter("classname");
        String charge = request.getParameter("charge");
        Clazz clazz =new Clazz();
        clazz.setId(id);
        clazz.setCharge(charge);
        clazz.setClassname(classname);
        int i = clazzService.updateByPrimaryKey(clazz);
        JSONObject object=new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="成功修改id为"+id+"的班级信息";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="修改失败，请重新尝试！";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }
    }

    @RequestMapping("/deleteClass")
    @ResponseBody
    public String deleteClassById(HttpServletResponse response,HttpServletRequest request) throws UnsupportedEncodingException, JSONException {
        int id = Integer.parseInt(request.getParameter("id"));
        logger.info("删除班级id为"+id+"的信息");
        int i = clazzService.deleteByClassId(id);
        JSONObject object=new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="成功删除id为"+id+"的班级信息";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="删除失败，请重新尝试！";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }
    }

    @RequestMapping("/adminManageCase")
    public String adminManageCase(Integer teaId,Integer page,Integer Type,String Keyword, String isUpdateSuccess,Model model){
        logger.info("进入管理员管理优秀案例界面");
        if(Type==1){
            logger.info("按教师id查询优秀案例信息");
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
            model.addAttribute("isUpdateSuccess","查询到"+i+"个案例");
            logger.info("查询到"+i+"条优秀案例信息");
            model.addAttribute("page",p);
            model.addAttribute("teachers",teachers);
            model.addAttribute("outstandingcases",outstandingcases);
            model.addAttribute("type",Type);
            model.addAttribute("keyword",Keyword);
            model.addAttribute("teaid",teaId);
            return "admin/adminManageCaseLib";
        }if(Type==2) {
            logger.info("按关键词查询案例信息");
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
            logger.info("查询到"+i+"条优秀案例信息");
            model.addAttribute("page",p);
            model.addAttribute("teachers",teachers);
            model.addAttribute("outstandingcases",outstandingcases);
            model.addAttribute("type",Type);
            model.addAttribute("keyword",Keyword);
            model.addAttribute("teaid","");
            return "admin/adminManageCaseLib";
        }else {
            logger.info("查询所有优秀案例信息");
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
            return "admin/adminManageCaseLib";
        }

    }
    @RequestMapping("/adminGetCaseDetail")
    public String adminGetCaseDetail(Integer id,String IsSuccess,String Keyword,Integer Type,Integer teaId,Model model){
        logger.info("管理员进入优秀案例详情界面");
        Outstandingcase outstandingcase = outstandingcaseService.selectByPrimaryKey(id);
        List<Teacher> teachers = teacherService.selectAll();
        model.addAttribute("outstandingcase",outstandingcase);
        model.addAttribute("IsSuccess",IsSuccess);
        model.addAttribute("keyword",Keyword);
        model.addAttribute("teachers",teachers);
        model.addAttribute("type",Type);
        model.addAttribute("teaid",teaId);
        return "admin/adminUpdateCase";
    }

    @RequestMapping("/adminUpdateCaseById")
    public String adminUpdateCaseById(Integer id,
                                      String casename,
                                      String casedescribe,
                                      String downloadlink,
                                      String technology,
                                      String groupmember,
                                      String score,
                                      String suggestion,
                                      Integer teaId,
                                      Integer teaid,
                                      String Keyword,
                                      Integer Type,
                                      MultipartFile file,Model model,HttpServletRequest req) throws UnsupportedEncodingException {

        logger.info("管理员更新优秀案例信息");
        if(file.isEmpty()){
            Outstandingcase outstandingcase =new Outstandingcase();
            outstandingcase.setId(id);
            outstandingcase.setCasename(casename);
            outstandingcase.setCasedescribe(casedescribe);
            outstandingcase.setDownloadlink(downloadlink);
            outstandingcase.setSubmittime(new Date());
            outstandingcase.setScore(score);
            outstandingcase.setSuggestion(suggestion);
            outstandingcase.setGroupmember(groupmember);
            outstandingcase.setTechnology(technology);
            outstandingcase.setTeaid(teaid);
            int i = outstandingcaseService.updateByPrimaryKey(outstandingcase);
            String isUpdateSuccess="成功更新"+i+"条案例";
            logger.info("管理员成功更新"+i+"条优秀案例信息");
            if(teaId==null) {
                return "redirect:/adminManageCase?teaId="+ "&Type=" + Type + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8") + "&Keyword=" + Keyword;
            }else {
                return "redirect:/adminManageCase?teaId="+teaId+ "&Type=" + Type + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8") + "&Keyword=" + Keyword;
            }
        }
        String oldFilename=downloadlink.split("/")[5];
        File deleteFile = new File(uploadOutstandingCaseFilePath,oldFilename);
        if(deleteFile!=null){
            //文件不为空，执行删除
            deleteFile.delete();
        }
        Outstandingcase outstandingcase =new Outstandingcase();
        outstandingcase.setId(id);
        outstandingcase.setCasename(casename);
        outstandingcase.setCasedescribe(casedescribe);
        outstandingcase.setSubmittime(new Date());
        outstandingcase.setScore(score);
        outstandingcase.setSuggestion(suggestion);
        outstandingcase.setGroupmember(groupmember);
        outstandingcase.setTechnology(technology);
        outstandingcase.setTeaid(teaid);
        File dir = new File(uploadOutstandingCaseFilePath);
        if(!dir.exists()) {
            dir.mkdir();
        }
        String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
        String fileName = file.getOriginalFilename();
        String fileServerPath = basePath + uploadOutstandingCaseFileResourceHandler.substring(0, uploadOutstandingCaseFileResourceHandler.lastIndexOf("/") + 1) + fileName;
        outstandingcase.setDownloadlink(fileServerPath);
        File tempFile = null;
        try {
            tempFile =  new File(uploadOutstandingCaseFilePath,fileName);
            FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
        }catch (Exception e){
            e.printStackTrace();
            String IsSuccess="文件上传失败";
            logger.info("优秀案例信息文件上传失败");
            return "redirect:/adminGetCaseDetail?id="+id+"&Type="+Type+"&IsSuccess="+URLEncoder.encode(IsSuccess, "UTF-8")+"&Keyword="+Keyword+"&teaId="+teaId;
        }
        int i = outstandingcaseService.updateByPrimaryKey(outstandingcase);
        String isUpdateSuccess="成功更新"+i+"条案例";
        logger.info("成功更新"+i+"条优秀案例信息");
        return "redirect:/adminManageCase?teaId=" + teaId +"&Type="+Type+ "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8")+"&Keyword="+Keyword;
    }

    @RequestMapping("/adminDeleteCase")
    @ResponseBody
    public String adminDeleteCase(HttpServletRequest request) throws JSONException {
        logger.info("管理员删除优秀案例信息");
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
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }
    }

    @RequestMapping("/adminManageTopic")
    public  String adminManageTopic(Integer page,Integer teaId,Integer Type,String isUpdateSuccess,Model model){
        logger.info("管理员进入管理选题界面");
       if(Type==1) {
           logger.info("根据teaId查询选题信息");
           List<Topic> topicsAll= topicService.selectByTeacherId(teaId);
           Page p=new Page();
           p.setCurrentPage(page);
           p.setPageSize(5);
           p.setTotalUsers(topicsAll.size());
           int i=0;
           for(Topic topic:topicsAll){
               i++;
           }
           List<Topic> topics = topicService.selectByTeaIdAddPage(teaId,(page - 1) * p.getPageSize(), p.getPageSize());
           model.addAttribute("topics",topics);
           model.addAttribute("page",p);
           model.addAttribute("isUpdateSuccess","查询到"+i+"条选题");
           logger.info("查询到"+i+"条选题");
           List<Teacher> teachers = teacherService.selectAll();
           model.addAttribute("teachers",teachers);
           if (teaId==null){
               model.addAttribute("teaid","");
           }else {
           model.addAttribute("teaid",teaId);
           }
           model.addAttribute("type",Type);
           return "admin/adminManageTopic";
       }else{
           logger.info("查询所有选题信息");
           Page p = new Page();
           p.setCurrentPage(page);
           p.setPageSize(5);
           p.setTotalUsers(topicService.selectAll().size());
           List<Topic> topics = topicService.selectAllAndPage((page - 1) * p.getPageSize(), p.getPageSize());
           model.addAttribute("topics", topics);
           model.addAttribute("page", p);
           model.addAttribute("isUpdateSuccess", isUpdateSuccess);
           List<Teacher> teachers = teacherService.selectAll();
           model.addAttribute("teachers", teachers);
           if (teaId==null){
               model.addAttribute("teaid","");
           }else {
               model.addAttribute("teaid",teaId);
           }
           model.addAttribute("type",Type);
           return "admin/adminManageTopic";
       }
    }

    @RequestMapping("/adminGetTopicDetail")
    public String adminGetTopicDetail(Integer topicId,Integer teaId,Integer Type,String IsSuccess,Model model){
        logger.info("管理员进入选题详情界面");
        Topic topic = topicService.selectByPrimaryKey(topicId);
        model.addAttribute("topic",topic);
        Teacher teacher = teacherService.selectByPrimayKey(topic.getTeaid());
        model.addAttribute("teacher",teacher);
        model.addAttribute("IsSuccess",IsSuccess);
        model.addAttribute("teaid",teaId);
        model.addAttribute("type",Type);
        return "admin/adminUpdateTopic";
    }

    @RequestMapping("/adminUpdateTopicById")
    public String adminUpdateTopicById(@RequestParam("id")Integer id,
                                       @RequestParam("topicname")String topicname,
                                       @RequestParam("topicdescribe") String topicdescribe,
                                       Integer teaId,
                                       Integer Type,
                                       @RequestParam("teaid") Integer teaid,
                                       @RequestParam("deadline")String deadline,
                                       @RequestParam("choosedeadline")String choosedeadline,
                                       @RequestParam("maxsize") Integer maxsize,
                                       @RequestParam("downloadlink") String downloadlink,
                                       @RequestParam("file") MultipartFile file, HttpServletRequest req, Model model) throws ParseException, UnsupportedEncodingException {
        logger.info("管理员根据选题id更新选题信息");
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
            logger.info(isUpdateSuccess);
            if(teaId==null){
                return "redirect:/adminManageTopic?page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8") + "&teaId=" + "&Type=" + Type;
            }else {
                return "redirect:/adminManageTopic?page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8") + "&teaId=" + teaId + "&Type=" + Type;
            }
        }
        String oldFileName=downloadlink.split("/")[5];
        File deleteFile = new File(uploadTopicFilePath,oldFileName);
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
        String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
        String fileName = file.getOriginalFilename();
        String fileServerPath = basePath + uploadTopicResourceHandler.substring(0, uploadTopicResourceHandler.lastIndexOf("/") + 1) + fileName;
        topic.setDownloadlink(fileServerPath);
        File tempFile = new File(uploadTopicFilePath,fileName);
        if(tempFile.exists()){
            String IsSuccess="文件已存在，请重新选择！";
            if (teaId==null){
                return "redirect:/adminGetTopicDetail?topicId=" + id + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8") + "&teaId="  + "&Type=" + Type;
            }else {
                return "redirect:/adminGetTopicDetail?topicId=" + id + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8") + "&teaId=" + teaId + "&Type=" + Type;
            }
        }else {
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
            } catch (Exception e) {
                e.printStackTrace();
                String IsSuccess = "文件上传失败";
                logger.info("上传选题参考文件失败");
                if (teaId==null){
                    return "redirect:/adminGetTopicDetail?topicId=" + id + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8") + "&teaId="  + "&Type=" + Type;
                }else {
                    return "redirect:/adminGetTopicDetail?topicId=" + id + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8") + "&teaId=" + teaId + "&Type=" + Type;
                }
            }
            int i = topicService.updateByPrimaryKey(topic);
            String isUpdateSuccess = "成功更新" + i + "条选题";
            logger.info(isUpdateSuccess);
            if(teaId==null){
                return "redirect:/adminManageTopic?page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8") + "&teaId=" + "&Type=" + Type;
            }else {
                return "redirect:/adminManageTopic?page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8") + "&teaId=" + teaId + "&Type=" + Type;
            }
        }
    }

    @RequestMapping("/adminDeleteTopic")
    @ResponseBody
    public String adminDeleteTopic(HttpServletRequest request, HttpServletResponse response) throws JSONException {
        logger.info("管理员删除选题");
        int id = Integer.parseInt(request.getParameter("id"));
        int i = topicService.deleteByPrimary(id);
        JSONObject object = new JSONObject();
        if(i>0) {
            object.put("code",1);
            String msg="成功删除了"+i+"条选题";
            logger.info(msg);
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            object.put("msg","删除失败，请重新尝试！！");
            logger.info("管理员删除选题信息失败");
            return object.toString();
        }
    }
    @RequestMapping("/adminManageScore")
    public String adminManageScore(String teaId,Integer page,String classId, String stuId,String isUpdateSuccess,Integer type,Model model){
        logger.info("管理员进入管理成绩界面");
        List<Clazz> clazzes = clazzService.selectAll();
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(8);
        List<Student> students = null;
        List<Score> scores = scoreService.selectAll();
        List<Teacher> teachers = teacherService.selectAll();
        List<Topic> topics = topicService.selectAll();
        if(type==0) {
            logger.info("根据teaId查询学生成绩");
            List<Student> studentAll = studentService.selectByTeaId(Integer.parseInt(teaId));
            p.setTotalUsers(studentAll.size());
            isUpdateSuccess="成功查询到"+studentAll.size()+"个学生";
            logger.info(isUpdateSuccess);
            students = studentService.selectByTeaIdAndPage(Integer.parseInt(teaId), (page - 1) * p.getPageSize(), p.getPageSize());
        }else if(type==1){
            logger.info("根据classid查询学生成绩");
            List<Student> studentAll = studentService.selectByClazzId(Integer.parseInt(classId));
            p.setTotalUsers(studentAll.size());
            isUpdateSuccess="成功查询到"+studentAll.size()+"个学生";
            logger.info(isUpdateSuccess);
            students=studentService.selectByClazzIdLimit(Integer.parseInt(classId),(page - 1) * p.getPageSize(), p.getPageSize());
        }else if (type==3){
            logger.info("根据stuId查询学生成绩");
            Student studentAll = studentService.selectByStuId(Integer.parseInt(stuId));
            if (studentAll==null){
                isUpdateSuccess="该学生不存在，请重新输入";
                logger.info(isUpdateSuccess);
                p.setTotalUsers(0);
            }else {
                isUpdateSuccess="成功查询到一个学生";
                logger.info(isUpdateSuccess);
                p.setTotalUsers(1);
                List<Student> studentAll1=new ArrayList<>();
                studentAll1.add(studentAll);
                students=studentAll1;
            }
        }else if(type==4){
            logger.info("查询所有学生成绩");
            List<Student> studentAll = studentService.selectAll();
            p.setTotalUsers(studentAll.size());
            students = studentService.selectAllAndPage((page - 1) * p.getPageSize(), p.getPageSize());
        }
        model.addAttribute("page",p);
        model.addAttribute("students",students);
        model.addAttribute("isUpdateSuccess",isUpdateSuccess);
        model.addAttribute("teaId",teaId);
        model.addAttribute("classId",classId);
        model.addAttribute("type",type);
        model.addAttribute("stuId",stuId);
        model.addAttribute("clazzes",clazzes);
        model.addAttribute("scores",scores);
        model.addAttribute("teachers",teachers);
        model.addAttribute("topics",topics);
        return "admin/adminManageScore";
    }

    @RequestMapping("/adminUpdateScoreHtml")
    public String adminUpdateScore(Integer id,String teaId,String classId, String stuId,Integer type,Model model ){
        logger.info("管理员进入更新学生成绩界面");
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
        return "admin/adminUpdateScore";
    }
    @RequestMapping("/adminBlockDetail")
    public String blockDetail(Integer stuId,Model model){
        List<Blocktask> blocktasks = blocktaskService.selectByStuId(stuId);
        Student student = studentService.selectByPrimaryKey(stuId);
        Topic topic = topicService.selectByPrimaryKey(student.getTopicid());
        List<Stagetopic> stagetopics = stagetopicService.selectAll();
        model.addAttribute("blocktasks",blocktasks);
        model.addAttribute("student",student);
        model.addAttribute("topic",topic);
        model.addAttribute("stagetopics",stagetopics);
        return "admin/adminBlockDetail";
    }
}
