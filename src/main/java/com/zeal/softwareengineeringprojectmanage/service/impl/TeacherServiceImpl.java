package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Teacher;
import com.zeal.softwareengineeringprojectmanage.bean.TeacherExample;
import com.zeal.softwareengineeringprojectmanage.mapper.TeacherMapper;
import com.zeal.softwareengineeringprojectmanage.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    TeacherMapper teacherMapper;

    @Override
    public List<Teacher> selectAll() {
        List<Teacher> teachers = teacherMapper.selectAll();
        return teachers;
    }

    @Override
    public Teacher selectByTeaId(Integer id) {
        Teacher teacher = teacherMapper.selectByTeaId(id);
        return teacher;
    }

    @Override
    public int updateByExample(Teacher teacher, TeacherExample teacherExample) {
        int i = teacherMapper.updateByExample(teacher, teacherExample);
        return i;
    }

    @Override
    public Teacher selectByPrimayKey(Integer id) {
        Teacher teacher = teacherMapper.selectByPrimaryKey(id);
        return teacher;
    }

    @Override
    public int insert(Teacher teacher) {
        int insert = teacherMapper.insert(teacher);
        return insert;
    }

    @Override
    public int deleteByPrimaryKey(Integer id) {
        int i = teacherMapper.deleteByPrimaryKey(id);
        return i;
    }

    @Override
    public List<Teacher> selectByPage(int start, int perPageSize) {
        List<Teacher> teachers = teacherMapper.selectByPage(start, perPageSize);
        return teachers;
    }

    @Override
    public int updatePassword(Integer id,String password) {
        int i = teacherMapper.updatePassword(id, password);
        return i;
    }
}
