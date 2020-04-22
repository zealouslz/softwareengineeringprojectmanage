package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Stagetopic;

import java.util.List;

public interface StagetopicService {
    List<Stagetopic> selectByTeaIdAndPage(Integer teaId,Integer start,Integer pageSize);
    List<Stagetopic> selectByTeaId(Integer teaId);
    int insert(Stagetopic stagetopic);
    Stagetopic selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(Stagetopic stagetopic);
    int deleteByPrimaryKey(Integer id);
    List<Stagetopic> selectAll();
}
