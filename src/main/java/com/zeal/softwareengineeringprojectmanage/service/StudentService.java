package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.ChoosedTopic;
import com.zeal.softwareengineeringprojectmanage.bean.Student;
import com.zeal.softwareengineeringprojectmanage.bean.StudentExample;
import org.omg.PortableInterceptor.INACTIVE;


import java.util.List;
import java.util.Map;

public interface StudentService {
    Student selectByPrimaryKey(Integer id);
    Student selectByStuId(Integer stuid);
    int saveStudent(Student student);
    List<Student> selectByClazzId(Integer id);
    List<Student> selectByClazzIdLimit(Integer id,Integer start,Integer end);
    int updateByExample(Student student, StudentExample studentExample);
    int updateByStuId(Integer stuId,String stuName,String password,Integer classId,Integer teaId,Integer isGroupLeader,Integer groupId,Integer topicId);
    int deleteStuByStuId(Integer stuId);
    List<Student> selectAll();
    List<ChoosedTopic> haveChoosedTopic();
    int updateTopicIdByStuId(Integer topicId,Integer stuId);
    List<Student> selectByTeaId(Integer teaId);
    List<Student> selectByTopicId(Integer topicId);
    int updateGroupIdByTopicId(Integer topicId,Integer groupId);
    int updateGroupLeader(Integer id);
    List<Student> selectByGroupId(Integer groupId);
    List<Student> selectByTeaIdAndPage(Integer teaId,Integer start,Integer pageSize);
    List<Student> selectByTeaIdAndGroupId(Integer teaId,Integer groupId);
    List<Student> selectByStuIdAndTeaId(Integer stuId,Integer teaId);
    int updateGroupAndTopicByStuId(Integer stuId,Integer isgroupLeader,Integer groupId,Integer topicId);
}
