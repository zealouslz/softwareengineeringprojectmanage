package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Student;

public interface StudentService {
    Student selectByPrimaryKey(Integer id);
    Student selectByStuId(Integer stuid);
    void saveStudent(Student student);
}
