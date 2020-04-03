package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Topic;
import com.zeal.softwareengineeringprojectmanage.mapper.TopicMapper;
import com.zeal.softwareengineeringprojectmanage.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    TopicMapper topicMapper;
    @Override
    public List<Topic> selectByTeacherId(Integer teacherId) {
        List<Topic> topics = topicMapper.selectByTeacherId(teacherId);
        return topics;
    }

    @Override
    public List<Topic> selectAll() {
        List<Topic> topics = topicMapper.selectAll();
        return topics;
    }

    @Override
    public List<Topic> selectByTeaIdAddPage(Integer teacherId, Integer start, Integer pageSize) {
        List<Topic> topics = topicMapper.selectByTeaIdAddPage(teacherId, start, pageSize);
        return topics;
    }

    @Override
    public int insert(Topic topic) {
        int insert = topicMapper.insert(topic);
        return insert;
    }

    @Override
    public Topic selectByPrimaryKey(Integer id) {
        Topic topic = topicMapper.selectByPrimaryKey(id);
        return topic;
    }

    @Override
    public int updateByPrimaryKey(Topic topic) {
        int i = topicMapper.updateByPrimaryKey(topic);
        return i;
    }

    @Override
    public int deleteByPrimary(Integer id) {
        int i = topicMapper.deleteByPrimaryKey(id);
        return i;
    }

    @Override
    public List<Topic> selectByTeaIdAddChooseDeadline(Integer teacherId) {
        List<Topic> topics = topicMapper.selectByTeaIdAddChooseDeadline(teacherId);
        return topics;
    }

    @Override
    public List<Topic> selectByTeaIdAddDeadline(Integer teacherId) {
        List<Topic> topics = topicMapper.selectByTeaIdAddDeadline(teacherId);
        return topics;
    }


    @Override
    public List<Topic> selectByTeaIdAddChooseDeadlineAndPage(Integer teaId, Integer start, Integer pageSize) {
        List<Topic> topics = topicMapper.selectByTeaIdAddChooseDeadlineAndPage(teaId, start, pageSize);
        return topics;
    }

    @Override
    public List<Topic> selectByTeaIdAddDeadlineAndPage(Integer teaId, Integer start, Integer pageSize) {
        List<Topic> topics = topicMapper.selectByTeaIdAddDeadlineAndPage(teaId, start, pageSize);
        return topics;
    }
}
