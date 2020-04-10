package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Blocktask;
import com.zeal.softwareengineeringprojectmanage.bean.BlocktaskExample;
import com.zeal.softwareengineeringprojectmanage.mapper.BlocktaskMapper;
import com.zeal.softwareengineeringprojectmanage.service.BlocktaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
