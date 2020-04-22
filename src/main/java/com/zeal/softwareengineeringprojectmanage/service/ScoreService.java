package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Score;

import java.util.List;

public interface ScoreService {
    List<Score> selectAll();
    Score selectByPrimaryKey(Integer id);
    int updateReplyScoreAndSugg(Integer id,Integer score,Float finalScore,String suggestion);

}
