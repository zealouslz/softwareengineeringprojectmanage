package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Clazz;
import com.zeal.softwareengineeringprojectmanage.bean.ClazzExample;

import java.util.List;

public interface  ClazzService {
    List<Clazz> selectAll();
    List<Clazz> selectByPage(Integer start,Integer pageSize);
    int insert(Clazz clazz);
    Clazz selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(Clazz clazz);
    int deleteByClassId(Integer id);
}
