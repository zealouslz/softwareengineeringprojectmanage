package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Topic;

import java.util.Date;
import java.util.List;

public interface TopicService {
    List<Topic> selectByTeacherId(Integer teacherId);
    List<Topic> selectAll();
    List<Topic> selectByTeaIdAddPage(Integer teacherId,Integer start,Integer pageSize);
    int insert(Topic topic);
    Topic selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(Topic topic);
    int deleteByPrimary(Integer id);
    List<Topic> selectByTeaIdAddChooseDeadline(Integer teacherId);
    List<Topic> selectByTeaIdAddDeadline(Integer teacherId);
    List<Topic> selectByTeaIdAddChooseDeadlineAndPage(Integer teaId,Integer start,Integer pageSize);
    List<Topic> selectByTeaIdAddDeadlineAndPage(Integer teaId,Integer start,Integer pageSize);
    int uploadResult(Integer id,String path,Date submittime);
    int uploadResultAndIspass(Integer id,String path,Date submittime,Integer ispass);
    int auditTopic(Integer id,Integer ispass,String suggestion,Integer score);
}
