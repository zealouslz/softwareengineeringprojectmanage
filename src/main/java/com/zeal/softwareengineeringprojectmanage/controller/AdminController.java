package com.zeal.softwareengineeringprojectmanage.controller;

import com.sun.deploy.net.HttpResponse;
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
    @Value("${file.uploadOutstandingCaseFolder}")
    private String outstandingCasePath;
    @Value("${file.uploadTopicFolder}")
    private String uploadTopicFilePath; //选题文件上传的地址
    @Autowired
    OutstandingcaseService outstandingcaseService;


    @RequestMapping("/addSingleStudent")
    public String importStudent(Student student, HttpSession session, RedirectAttributes redirectAttributes){
        List<Student> students = studentService.selectAll();
        for(Student stu:students){
            if(stu.getStuid().intValue()==student.getStuid().intValue()){
                redirectAttributes.addFlashAttribute("addIsSuccess","此学号已存在，请重新添加");
                return "redirect:/studentImport";
            }
        }
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
        List<Teacher> teachers = teacherService.selectAll();
        for(Teacher tea:teachers){
            if(tea.getTeaid().intValue()==teacher.getTeaid().intValue()||tea.getTeaname().equals(teacher.getTeaname())){
                redirectAttributes.addFlashAttribute("addIsSuccess","该教师已存在！");
                return "redirect:/teacherImport";
            }
        }
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
        try {
            inputStream = file.getInputStream();
            list = importService.getBankListByExcel(inputStream,file.getOriginalFilename());
            inputStream.close();
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("IsSuccess","文件格式错误请重新上传，必须是.xls或者.xlsx");
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
                 return "redirect:/studentImport";
             }
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
        try {
            inputStream = file.getInputStream();
            list = importService.getBankListByExcel(inputStream,file.getOriginalFilename());
            inputStream.close();
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("IsSuccess","文件格式错误请重新上传，必须是.xls或者.xlsx");
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
        return "redirect:/teacherImport";
    }
    @RequestMapping("/manageStu")
    public String manageStu(Integer type,Integer page,String classId,String stuId,String isUpdateSuccess, Model model){
        List<Clazz> clazzes = clazzService.selectAll();
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(8);
        List<Student> students = null;
        List<Score> scores = scoreService.selectAll();
        List<Teacher> teachers = teacherService.selectAll();
        List<Topic> topics = topicService.selectAll();
        if(type==1){
            List<Student> studentAll = studentService.selectByClazzId(Integer.parseInt(classId));
            p.setTotalUsers(studentAll.size());
            isUpdateSuccess="成功查询到"+studentAll.size()+"个学生";
            students=studentService.selectByClazzIdLimit(Integer.parseInt(classId),(page - 1) * p.getPageSize(), p.getPageSize());
        }else if (type==2){
            Student studentAll = studentService.selectByStuId(Integer.parseInt(stuId));
            if (studentAll==null){
                isUpdateSuccess="该学生不存在，请重新输入";
                p.setTotalUsers(0);
            }else {
                isUpdateSuccess="成功查询到一个学生";
                p.setTotalUsers(1);
                List<Student> studentAll1=new ArrayList<>();
                studentAll1.add(studentAll);
                students=studentAll1;
            }
        }else if(type==0){
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
        Teacher teacher = teacherService.selectByPrimayKey(teaId);
        model.addAttribute("teacher",teacher);
        return "admin/updateTea";
    }
    @RequestMapping("/updateStuByStuId")
    public String updateStuByStuId(Student student,Integer type,String classId,String stuId,Model model) throws UnsupportedEncodingException {
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
        return "redirect:/manageStu?classId="+classId+"&isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8")+"&page=1&type="+type+"&stuId="+stuId;
    }
    @RequestMapping("/updateTeaById")
    @ResponseBody
    public String updateTeaById(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, JSONException {
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
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="修改失败，请重新尝试！";
            object.put("msg",msg);
            return object.toString();
        }
    }
    @RequestMapping("/deleteStu")
    @ResponseBody
    public String deleteStuByStuId(HttpServletRequest request) throws UnsupportedEncodingException, JSONException {
        int id = Integer.parseInt(request.getParameter("id"));
        int i = studentService.deleteStuByStuId(id);
        JSONObject object=new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="成功删除学号为"+id+"的学生";
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="删除失败，请重新尝试！";
            object.put("msg",msg);
            return object.toString();
        }
    }
    @RequestMapping("/deleteTea")
    @ResponseBody
    public String deleteTeaById(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, JSONException {

        int id = Integer.parseInt(request.getParameter("id"));
        int i = teacherService.deleteByPrimaryKey(id);
        JSONObject object=new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="成功删除id为"+id+"的教师";
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="删除失败，请重新尝试！";
            object.put("msg",msg);
            return object.toString();
        }
    }

    @RequestMapping("/manageTea")
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

    @RequestMapping("/manageClass")
    public String ManageClass(int page,String isUpdateSuccess ,Model model ){
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
        model.addAttribute("SingleSuccess",SingleSuccess);
        return "admin/addClass";
    }

    @RequestMapping("/addSingleClass")
    public String addSingleClass(Clazz clazz) throws UnsupportedEncodingException {
        List<Clazz> clazzes = clazzService.selectAll();
        for (Clazz clz:clazzes){
            if(clz.getClassname().equals(clazz.getClassname())){
                int insert = clazzService.insert(clazz);
                String isUpdateSuccess="该班级已存在！";
                return "redirect:/addClass?SingleSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8")+"&page=1";
            }
        }
        int insert = clazzService.insert(clazz);
        String isUpdateSuccess="成功添加"+insert+"条数据";
        return "redirect:/manageClass?isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8")+"&page=1";
    }

    @RequestMapping(value="/classUpload",method= RequestMethod.POST)
    public String  uploadClassExcel(HttpServletRequest request,HttpSession session,RedirectAttributes redirectAttributes,Model model) throws  Exception{
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

        InputStream inputStream =null;
        List<List<Object>> list = null;
        int insert=0;
        MultipartFile file = multipartRequest.getFile("filename");
        if(file.isEmpty()){
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
        return "redirect:/manageClass?isUpdateSuccess="+URLEncoder.encode(isUpdateSuccess,"UTF-8")+"&page=1";
    }

    @RequestMapping("/getClassDetail")
    public String updateClass( Integer classId,Model model){
        Clazz clazz = clazzService.selectByPrimaryKey(classId);
        model.addAttribute("clazz",clazz);
        List<Teacher> teachers = teacherService.selectAll();
        model.addAttribute("teachers",teachers);
        return "admin/updateClass";
    }

    @RequestMapping("/updateClassById")
    @ResponseBody
    public String updateClassById(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, JSONException {
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
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="修改失败，请重新尝试！";
            object.put("msg",msg);
            return object.toString();
        }
    }

    @RequestMapping("/deleteClass")
    @ResponseBody
    public String deleteClassById(HttpServletResponse response,HttpServletRequest request) throws UnsupportedEncodingException, JSONException {
        int id = Integer.parseInt(request.getParameter("id"));
        int i = clazzService.deleteByClassId(id);
        JSONObject object=new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="成功删除id为"+id+"的班级信息";
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="删除失败，请重新尝试！";
            object.put("msg",msg);
            return object.toString();
        }
    }

    @RequestMapping("/adminManageCase")
    public String adminManageCase(Integer teaId,Integer page,Integer Type,String Keyword, String isUpdateSuccess,Model model){
        if(Type==1){
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
            model.addAttribute("page",p);
            model.addAttribute("teachers",teachers);
            model.addAttribute("outstandingcases",outstandingcases);
            model.addAttribute("type",Type);
            model.addAttribute("keyword",Keyword);
            model.addAttribute("teaid",teaId);
            return "admin/adminManageCaseLib";
        }if(Type==2) {
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
            model.addAttribute("page",p);
            model.addAttribute("teachers",teachers);
            model.addAttribute("outstandingcases",outstandingcases);
            model.addAttribute("type",Type);
            model.addAttribute("keyword",Keyword);
            model.addAttribute("teaid","");
            return "admin/adminManageCaseLib";
        }else {
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
                                      MultipartFile file,Model model) throws UnsupportedEncodingException {
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
            if(teaId==null) {
                return "redirect:/adminManageCase?teaId="+ "&Type=" + Type + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8") + "&Keyword=" + Keyword;
            }else {
                return "redirect:/adminManageCase?teaId="+teaId+ "&Type=" + Type + "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8") + "&Keyword=" + Keyword;
            }
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
        outstandingcase.setSubmittime(new Date());
        outstandingcase.setScore(score);
        outstandingcase.setSuggestion(suggestion);
        outstandingcase.setGroupmember(groupmember);
        outstandingcase.setTechnology(technology);
        outstandingcase.setTeaid(teaid);
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
            return "redirect:/adminGetCaseDetail?id="+id+"&Type="+Type+"&IsSuccess="+URLEncoder.encode(IsSuccess, "UTF-8")+"&Keyword="+Keyword+"&teaId="+teaId;
        }
        int i = outstandingcaseService.updateByPrimaryKey(outstandingcase);
        String isUpdateSuccess="成功更新"+i+"条案例";
        return "redirect:/adminManageCase?teaId=" + teaId +"&Type="+Type+ "&page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8")+"&Keyword="+Keyword;
    }

    @RequestMapping("/adminDeleteCase")
    @ResponseBody
    public String adminDeleteCase(HttpServletRequest request) throws JSONException {
        int id = Integer.parseInt(request.getParameter("id"));
        int i = outstandingcaseService.deleteByPrimaryKey(id);
        JSONObject object=new JSONObject();
        if(i>0){
            object.put("code",1);
            String msg="成功删除题id为"+id+"的优秀案例";
            object.put("msg",msg);
            return object.toString();
        }else {
            object.put("code",-1);
            String msg="删除失败，请重新尝试！";
            object.put("msg",msg);
            return object.toString();
        }
    }

    @RequestMapping("/adminManageTopic")
    public  String adminManageTopic(Integer page,Integer teaId,Integer Type,String isUpdateSuccess,Model model){
       if(Type==1) {
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
            if(teaId==null){
                return "redirect:/adminManageTopic?page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8") + "&teaId=" + "&Type=" + Type;
            }else {
                return "redirect:/adminManageTopic?page=1&isUpdateSuccess=" + URLEncoder.encode(isUpdateSuccess, "UTF-8") + "&teaId=" + teaId + "&Type=" + Type;
            }
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
                if (teaId==null){
                    return "redirect:/adminGetTopicDetail?topicId=" + id + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8") + "&teaId="  + "&Type=" + Type;
                }else {
                    return "redirect:/adminGetTopicDetail?topicId=" + id + "&IsSuccess=" + URLEncoder.encode(IsSuccess, "UTF-8") + "&teaId=" + teaId + "&Type=" + Type;
                }
            }
            int i = topicService.updateByPrimaryKey(topic);
            String isUpdateSuccess = "成功更新" + i + "条选题";
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
            object.put("msg","删除失败，请重新尝试！！");
            return object.toString();
        }
    }
    @RequestMapping("/adminManageScore")
    public String adminManageScore(String teaId,Integer page,String classId, String stuId,String isUpdateSuccess,Integer type,Model model){
        List<Clazz> clazzes = clazzService.selectAll();
        Page p=new Page();
        p.setCurrentPage(page);
        p.setPageSize(8);
        List<Student> students = null;
        List<Score> scores = scoreService.selectAll();
        List<Teacher> teachers = teacherService.selectAll();
        List<Topic> topics = topicService.selectAll();
        if(type==0) {
            List<Student> studentAll = studentService.selectByTeaId(Integer.parseInt(teaId));
            p.setTotalUsers(studentAll.size());
            isUpdateSuccess="成功查询到"+studentAll.size()+"个学生";
            students = studentService.selectByTeaIdAndPage(Integer.parseInt(teaId), (page - 1) * p.getPageSize(), p.getPageSize());
        }else if(type==1){
            List<Student> studentAll = studentService.selectByClazzId(Integer.parseInt(classId));
            p.setTotalUsers(studentAll.size());
            isUpdateSuccess="成功查询到"+studentAll.size()+"个学生";
            students=studentService.selectByClazzIdLimit(Integer.parseInt(classId),(page - 1) * p.getPageSize(), p.getPageSize());
        }else if (type==3){
            Student studentAll = studentService.selectByStuId(Integer.parseInt(stuId));
            if (studentAll==null){
                isUpdateSuccess="该学生不存在，请重新输入";
                p.setTotalUsers(0);
            }else {
                isUpdateSuccess="成功查询到一个学生";
                p.setTotalUsers(1);
                List<Student> studentAll1=new ArrayList<>();
                studentAll1.add(studentAll);
                students=studentAll1;
            }
        }else if(type==4){
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
