package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Stagetopicresult;

import java.util.List;

public interface StagetopicresultService {
    List<Stagetopicresult> selectByTopicId(Integer topicId);
    int insert(Stagetopicresult stagetopicresult);
}
