package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Outstandingcase;

import java.util.List;

public interface OutstandingcaseService {
    int insert(Outstandingcase outstandingcase);
    List<Outstandingcase> selectAll();
    List<Outstandingcase> selectAllAndPage(Integer start,Integer pageSize);
    List<Outstandingcase> selectByTeaId(Integer teaId);
    List<Outstandingcase> selectByTeaIdAndPage(Integer teaId,Integer start,Integer pageSize);
    Outstandingcase selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(Outstandingcase outstandingcase);
    int deleteByPrimaryKey(Integer id);
    List<Outstandingcase> selectByKeyWord(String keyword);
    List<Outstandingcase> selectByKeyWordAndTeaId(String keyword,Integer teaId);
    List<Outstandingcase> selectByKeyWordAndPage(String keyword,Integer start,Integer pageSize);
    List<Outstandingcase> selectByKeyWordAndTeaIdAndPage(String keyword,Integer teaId,Integer start,Integer pageSize);
}
