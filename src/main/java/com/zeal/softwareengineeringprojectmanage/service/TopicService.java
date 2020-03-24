package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Topic;

import java.util.List;

public interface TopicService {
    List<Topic> selectByTeacherId(Integer teacherId);
    List<Topic> selectAll();
    List<Topic> selectByTeaIdAddPage(Integer teacherId,Integer start,Integer pageSize);
    int insert(Topic topic);
    Topic selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(Topic topic);
    int deleteByPrimary(Integer id);
}
