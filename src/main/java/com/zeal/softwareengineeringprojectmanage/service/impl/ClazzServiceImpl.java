package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Clazz;
import com.zeal.softwareengineeringprojectmanage.bean.ClazzExample;
import com.zeal.softwareengineeringprojectmanage.mapper.ClazzMapper;
import com.zeal.softwareengineeringprojectmanage.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClazzServiceImpl implements ClazzService {
    @Autowired
    ClazzMapper clazzMapper;
    @Override
    public List<Clazz> selectAll() {
        List<Clazz> clazzes = clazzMapper.selectAll();
        return clazzes;
    }

    @Override
    public List<Clazz> selectByPage(Integer start,Integer pageSize) {
        List<Clazz> clazzes = clazzMapper.selectByPage(start, pageSize);
        return clazzes;
    }

    @Override
    public int insert(Clazz clazz) {
        int insert = clazzMapper.insert(clazz);
        return insert;
    }

    @Override
    public Clazz selectByPrimaryKey(Integer id) {
        Clazz clazz = clazzMapper.selectByPrimaryKey(id);
        return  clazz;
    }

    @Override
    public int updateByPrimaryKey(Clazz clazz) {
        ClazzExample clazzExample=new ClazzExample();
        ClazzExample.Criteria criteria = clazzExample.createCriteria();
        criteria.andIdEqualTo(clazz.getId());
        int i = clazzMapper.updateByExample(clazz, clazzExample);
        return i;
    }

    @Override
    public int deleteByClassId(Integer id) {
        int i = clazzMapper.deleteClassById(id);
        return i;
    }
}
