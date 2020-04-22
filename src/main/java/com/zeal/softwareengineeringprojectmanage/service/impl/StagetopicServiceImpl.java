package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Stagetopic;
import com.zeal.softwareengineeringprojectmanage.bean.StagetopicExample;
import com.zeal.softwareengineeringprojectmanage.mapper.StagetopicMapper;
import com.zeal.softwareengineeringprojectmanage.service.StagetopicService;
import org.apache.poi.ss.formula.functions.Now;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class StagetopicServiceImpl implements StagetopicService {
    @Autowired
    StagetopicMapper stagetopicMapper;
    @Override
    public List<Stagetopic> selectByTeaIdAndPage(Integer teaId, Integer start, Integer pageSize) {
        List<Stagetopic> stagetopics = stagetopicMapper.selectByTeaIdAndPage(teaId, start, pageSize);
        return stagetopics;
    }

    @Override
    public List<Stagetopic> selectByTeaId(Integer teaId) {
        StagetopicExample stagetopicExample=new StagetopicExample();
        StagetopicExample.Criteria criteria = stagetopicExample.createCriteria();
        criteria.andTeaidEqualTo(teaId);
        Date date=new Date();
        criteria.andDeadlineGreaterThan(date);
        List<Stagetopic> stagetopics = stagetopicMapper.selectByExample(stagetopicExample);
        return stagetopics;
    }

    @Override
    public int insert(Stagetopic stagetopic) {
        int insert = stagetopicMapper.insert(stagetopic);
        return insert;
    }

    @Override
    public Stagetopic selectByPrimaryKey(Integer id) {
        Stagetopic stagetopic = stagetopicMapper.selectByPrimaryKey(id);
        return stagetopic;
    }

    @Override
    public int updateByPrimaryKey(Stagetopic stagetopic) {
        int i = stagetopicMapper.updateByPrimaryKey(stagetopic);
        return i;
    }

    @Override
    public int deleteByPrimaryKey(Integer id) {
        int i = stagetopicMapper.deleteByPrimaryKey(id);
        return i;
    }

    @Override
    public List<Stagetopic> selectAll() {
        List<Stagetopic> stagetopics = stagetopicMapper.selectAll();
        return stagetopics;
    }
}
