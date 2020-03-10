package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Teacher;

import java.util.List;

public interface TeacherService {
    Teacher selectByTeaId(Integer id);
    List<Teacher> selectAll();
}
