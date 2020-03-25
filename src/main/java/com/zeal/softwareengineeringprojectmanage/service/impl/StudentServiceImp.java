package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.ChoosedTopic;
import com.zeal.softwareengineeringprojectmanage.bean.Student;
import com.zeal.softwareengineeringprojectmanage.bean.StudentExample;
import com.zeal.softwareengineeringprojectmanage.mapper.StudentMapper;
import com.zeal.softwareengineeringprojectmanage.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    public int saveStudent(Student student) {
        int insert = studentMapper.insert(student);
        return insert;
    }

    @Override
    public Student selectByPrimaryKey(Integer id) {
        Student student = studentMapper.selectByPrimaryKey(id);
        return student;
    }

    @Override
    public List<Student> selectByClazzId(Integer id) {
        List<Student> students = studentMapper.selectByClazzId(id);
        return students;
    }

    @Override
    public int updateByExample(Student student, StudentExample studentExample) {
        int i = studentMapper.updateByExample(student, studentExample);
        return i;
    }

    @Override
    public List<Student> selectByClazzIdLimit(Integer clazzid, Integer start, Integer end) {
        List<Student> students = studentMapper.selectByClazzIdLimit(clazzid,start,end);
        return students;
    }

    @Override
    public int updateByStuId(Integer stuId, String stuName, String password, Integer classId, Integer teaId, Integer isGroupLeader, Integer groupId, Integer topicId) {
        int i = studentMapper.updateByStuId(stuId, stuName, password, classId, teaId, isGroupLeader, groupId, topicId);
        return i;
    }

    @Override
    public int deleteStuByStuId(Integer stuId) {
        int i = studentMapper.deleteByStuId(stuId);
        return i;
    }

    @Override
    public List<Student> selectAll() {
        List<Student> students = studentMapper.selectAll();
        return students;
    }

    @Override
    public List<ChoosedTopic> haveChoosedTopic() {
        List<ChoosedTopic> havechoosedtopic = studentMapper.haveChoosedTopic();
        return havechoosedtopic;
    }

    @Override
    public int updateTopicIdByStuId(Integer topicId, Integer stuId) {
        int i = studentMapper.updateTopicIdByStuId(topicId, stuId);
        return i;
    }
}
