package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Student;
import com.zeal.softwareengineeringprojectmanage.mapper.StudentMapper;
import com.zeal.softwareengineeringprojectmanage.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImp implements StudentService {
    @Autowired
    StudentMapper studentMapper;

    @Override
    public Student selectByStuId(Integer stuid) {
        Student student = studentMapper.selectByStuId(stuid);
        return student;
    }

    @Override
    public void saveStudent(Student student) {
         studentMapper.insert(student);
    }

    @Override
    public Student selectByPrimaryKey(Integer id) {
        Student student = studentMapper.selectByPrimaryKey(id);
        return student;
    }
}
