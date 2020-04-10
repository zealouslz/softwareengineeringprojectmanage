package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Stagetopicresult;

import java.util.List;

public interface StagetopicresultService {
    List<Stagetopicresult> selectByTopicId(Integer topicId);
    int insert(Stagetopicresult stagetopicresult);
    Stagetopicresult selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(Stagetopicresult stagetopicresult);
    List<Stagetopicresult> selectByStageTopicIds(List<Integer> stageTopicIds);
    List<Stagetopicresult> selectByStageTopicIdsAndPage(List<Integer> stageTopicIds,Integer start,Integer pageSize);
    int updateByIdAndIsPassAndSugg(Integer id,Integer ispass,String suggestion);
}
