package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Blocktask;
import com.zeal.softwareengineeringprojectmanage.bean.BlocktaskExample;
import com.zeal.softwareengineeringprojectmanage.mapper.BlocktaskMapper;
import com.zeal.softwareengineeringprojectmanage.service.BlocktaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BlocktaskServiceImpl implements BlocktaskService {
    @Autowired
    BlocktaskMapper blocktaskMapper;
    @Override
    public List<Blocktask> selectByGroupLeaderId(Integer id) {
        BlocktaskExample blocktaskExample=new BlocktaskExample();
        BlocktaskExample.Criteria criteria = blocktaskExample.createCriteria();
        criteria.andGroupleaderidEqualTo(id);
        List<Blocktask> blocktasks = blocktaskMapper.selectByExample(blocktaskExample);
        return blocktasks;
    }

    @Override
    public List<Blocktask> selectByGroupLeaderIdAndPage(Integer leaderId, Integer start, Integer pageSize) {
        List<Blocktask> blocktasks = blocktaskMapper.selectByGroupLeaderIdAndPage(leaderId, start, pageSize);
        return blocktasks;
    }

    @Override
    public int insert(Blocktask blocktask) {
        int insert = blocktaskMapper.insert(blocktask);
        return insert;
    }

    @Override
    public Blocktask selectByPrimaryKey(Integer Id) {
        Blocktask blocktask = blocktaskMapper.selectByPrimaryKey(Id);
        return blocktask;
    }

    @Override
    public int updateByPrimaryKey(Blocktask blocktask) {
        int i = blocktaskMapper.updateByPrimaryKey(blocktask);
        return i;
    }

    @Override
    public int deleteByPrimary(Integer id) {
        int i = blocktaskMapper.deleteByPrimaryKey(id);
        return i;
    }

    @Override
    public List<Blocktask> selectByStuIdAndPage(Integer stuId, Integer start, Integer pageSize) {
        List<Blocktask> blocktasks = blocktaskMapper.selectByStuIdAndPage(stuId, start, pageSize);
        return blocktasks;
    }

    @Override
    public List<Blocktask> selectByStuId(Integer stuId) {
        BlocktaskExample blocktaskExample=new BlocktaskExample();
        BlocktaskExample.Criteria criteria = blocktaskExample.createCriteria();
        criteria.andStuidEqualTo(stuId);
        List<Blocktask> blocktasks = blocktaskMapper.selectByExample(blocktaskExample);
        return blocktasks;
    }

    @Override
    public int updateUploadFile(Integer id, String path, Date date) {
        int i = blocktaskMapper.updateUploadFile(path, date, id);
        return i;
    }

    @Override
    public int updateIsPassAndScoreAndSuggestion(Integer id, Integer ispass, Integer score, String suggestion) {
        int i = blocktaskMapper.updateIsPassAndScoreAndSuggestion(id, ispass, score, suggestion);
        return i;
    }

    @Override
    public int updateIsPassAndUploadAndSubTime(Integer id, Integer ispass, Date submittime, String uploadFile) {
        int i = blocktaskMapper.updateIsPassAndUploadAndSubTime(id, ispass, uploadFile, submittime);
        return  i;
    }
}
