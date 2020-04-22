package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Score;
import com.zeal.softwareengineeringprojectmanage.bean.ScoreExample;
import com.zeal.softwareengineeringprojectmanage.mapper.ScoreMapper;
import com.zeal.softwareengineeringprojectmanage.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ScoreServiceImpl implements ScoreService {
    @Autowired
    ScoreMapper scoreMapper;
    @Override
    public List<Score> selectAll() {
        List<Score> scores = scoreMapper.selectAll();
        return scores;
    }

    @Override
    public Score selectByPrimaryKey(Integer id) {
        Score score = scoreMapper.selectByPrimaryKey(id);
        return score;
    }

    @Override
    public int updateReplyScoreAndSugg(Integer id, Integer score, Float finalScore, String suggestion) {
        int i = scoreMapper.updateReplyScoreAndSugg(id, suggestion, score, finalScore);
        return i;
    }
}
