package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Teacher;
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
}
