package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Blocktask;

import java.util.List;

public interface BlocktaskService {
    List<Blocktask> selectByGroupLeaderId(Integer id);
    List<Blocktask> selectByGroupLeaderIdAndPage(Integer leaderId,Integer start,Integer pageSize);
}
