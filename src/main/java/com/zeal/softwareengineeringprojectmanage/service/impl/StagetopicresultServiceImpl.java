package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Stagetopicresult;
import com.zeal.softwareengineeringprojectmanage.bean.StagetopicresultExample;
import com.zeal.softwareengineeringprojectmanage.mapper.StagetopicresultMapper;
import com.zeal.softwareengineeringprojectmanage.service.StagetopicresultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class StagetopicresultServiceImpl implements StagetopicresultService {
    @Autowired
    StagetopicresultMapper stagetopicresultMapper;
    @Override
    public List<Stagetopicresult> selectByTopicId(Integer topicId) {
        StagetopicresultExample stagetopicresultExample =new StagetopicresultExample();
        StagetopicresultExample.Criteria criteria = stagetopicresultExample.createCriteria();
        criteria.andTopicidEqualTo(topicId);
        List<Stagetopicresult> stagetopicresults = stagetopicresultMapper.selectByExample(stagetopicresultExample);
        return stagetopicresults;
    }

    @Override
    public int insert(Stagetopicresult stagetopicresult) {
        int insert = stagetopicresultMapper.insert(stagetopicresult);
        return insert;
    }

    @Override
    public Stagetopicresult selectByPrimaryKey(Integer id) {
        Stagetopicresult stagetopicresult = stagetopicresultMapper.selectByPrimaryKey(id);
        return stagetopicresult;
    }

    @Override
    public int updateByPrimaryKey(Stagetopicresult stagetopicresult) {
        int i = stagetopicresultMapper.updateByPrimaryKey(stagetopicresult);
        return i;
    }

    @Override
    public List<Stagetopicresult> selectByStageTopicIds(List<Integer> stageTopicIds) {
        StagetopicresultExample stagetopicresultExample=new StagetopicresultExample();
        StagetopicresultExample.Criteria criteria = stagetopicresultExample.createCriteria();
        criteria.andStagetopicidIn(stageTopicIds);
        List<Stagetopicresult> stagetopicresults = stagetopicresultMapper.selectByExample(stagetopicresultExample);
        return stagetopicresults;
    }

    @Override
    public List<Stagetopicresult> selectByStageTopicIdsAndPage(List<Integer> stageTopicIds, Integer start, Integer pageSize) {
        List<Stagetopicresult> stagetopicresults = stagetopicresultMapper.selectByStageTopicIdsAndPage(stageTopicIds, start, pageSize);
        return stagetopicresults;
    }

    @Override
    public int updateByIdAndIsPassAndSugg(Integer id, Integer ispass, String suggestion) {

        int i = stagetopicresultMapper.updateByIdAndIsPassAndSugg(id, ispass.byteValue(), suggestion);
        return i;

    }
}
