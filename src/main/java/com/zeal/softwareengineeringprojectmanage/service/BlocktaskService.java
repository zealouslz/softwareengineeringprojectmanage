package com.zeal.softwareengineeringprojectmanage.service;

import com.zeal.softwareengineeringprojectmanage.bean.Blocktask;

import java.util.Date;
import java.util.List;

public interface BlocktaskService {
    List<Blocktask> selectByGroupLeaderId(Integer id);
    List<Blocktask> selectByGroupLeaderIdAndPage(Integer leaderId,Integer start,Integer pageSize);
    int insert(Blocktask blocktask);
    Blocktask selectByPrimaryKey(Integer Id);
    int updateByPrimaryKey(Blocktask blocktask);
    int deleteByPrimary(Integer id);
    List<Blocktask> selectByStuIdAndPage(Integer stuId,Integer start,Integer pageSize);
    List<Blocktask> selectByStuId(Integer stuId);
    int updateUploadFile(Integer id, String path, Date date);
    int updateIsPassAndScoreAndSuggestion(Integer id,Integer ispass,Integer score,String suggestion);
    int updateIsPassAndUploadAndSubTime(Integer id,Integer ispass,Date submittime,String uploadFile);
}
