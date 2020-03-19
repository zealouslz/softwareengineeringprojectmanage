package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Teacher;
import com.zeal.softwareengineeringprojectmanage.bean.TeacherExample;

import java.util.List;

public interface TeacherService {
    Teacher selectByTeaId(Integer id);
    List<Teacher> selectAll();
    int insert(Teacher teacher);
    Teacher selectByPrimayKey(Integer id);
    int updateByExample(Teacher teacher, TeacherExample teacherExample);
    int deleteByPrimaryKey(Integer id);
    List<Teacher> selectByPage(int start,int perPageSize);
}
