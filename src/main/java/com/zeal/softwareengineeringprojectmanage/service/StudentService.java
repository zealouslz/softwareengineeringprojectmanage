package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Student;
import com.zeal.softwareengineeringprojectmanage.bean.StudentExample;

import java.util.List;

public interface StudentService {
    Student selectByPrimaryKey(Integer id);
    Student selectByStuId(Integer stuid);
    int saveStudent(Student student);
    List<Student> selectByClazzId(Integer id);
    List<Student> selectByClazzIdLimit(Integer id,Integer start,Integer end);
    int updateByExample(Student student, StudentExample studentExample);
    int updateByStuId(Integer stuId,String stuName,String password,Integer classId,Integer teaId,Integer isGroupLeader,Integer groupId,Integer topicId);
    int deleteStuByStuId(Integer stuId);
}
