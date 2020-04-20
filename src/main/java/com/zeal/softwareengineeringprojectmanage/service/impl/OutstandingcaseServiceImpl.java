package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Outstandingcase;
import com.zeal.softwareengineeringprojectmanage.mapper.OutstandingcaseMapper;
import com.zeal.softwareengineeringprojectmanage.service.OutstandingcaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutstandingcaseServiceImpl implements OutstandingcaseService {
    @Autowired
    OutstandingcaseMapper outstandingcaseMapper;
    @Override
    public int insert(Outstandingcase outstandingcase) {
        int insert = outstandingcaseMapper.insert(outstandingcase);
        return insert;
    }

    @Override
    public List<Outstandingcase> selectAll() {
        List<Outstandingcase> outstandingcases = outstandingcaseMapper.selectAll();
        return outstandingcases;
    }

    @Override
    public List<Outstandingcase> selectByTeaId(Integer teaId) {
        List<Outstandingcase> outstandingcases = outstandingcaseMapper.selectByTeaId(teaId);
        return outstandingcases;
    }

    @Override
    public List<Outstandingcase> selectAllAndPage(Integer start, Integer pageSize) {
        List<Outstandingcase> outstandingcases = outstandingcaseMapper.selectAllAndPage(start, pageSize);
        return outstandingcases;
    }

    @Override
    public List<Outstandingcase> selectByTeaIdAndPage(Integer teaId, Integer start, Integer pageSize) {
        List<Outstandingcase> outstandingcases = outstandingcaseMapper.selectByTeaIdAndPage(teaId, start, pageSize);
        return outstandingcases;
    }

    @Override
    public Outstandingcase selectByPrimaryKey(Integer id) {
        Outstandingcase outstandingcase = outstandingcaseMapper.selectByPrimaryKey(id);
        return outstandingcase;
    }

    @Override
    public int updateByPrimaryKey(Outstandingcase outstandingcase) {
        int i = outstandingcaseMapper.updateByPrimaryKey(outstandingcase);
        return i;
    }

    @Override
    public int deleteByPrimaryKey(Integer id) {
        int i = outstandingcaseMapper.deleteByPrimaryKey(id);
        return i;
    }

    @Override
    public List<Outstandingcase> selectByKeyWord(String keyword) {
        List<Outstandingcase> outstandingcases = outstandingcaseMapper.selectByKeyWord(keyword);
        return outstandingcases;
    }

    @Override
    public List<Outstandingcase> selectByKeyWordAndPage(String keyword, Integer start, Integer pageSize) {
        List<Outstandingcase> outstandingcases = outstandingcaseMapper.selectByKeyWordAndPage(keyword, start, pageSize);
        return outstandingcases;
    }

    @Override
    public List<Outstandingcase> selectByKeyWordAndTeaId(String keyword, Integer teaId) {
        List<Outstandingcase> outstandingcases = outstandingcaseMapper.selectByKeyWordAndTeaId(keyword, teaId);
        return outstandingcases;
    }

    @Override
    public List<Outstandingcase> selectByKeyWordAndTeaIdAndPage(String keyword, Integer teaId, Integer start, Integer pageSize) {
        List<Outstandingcase> outstandingcases = outstandingcaseMapper.selectByKeyWordAndTeaIdAndPage(keyword, teaId, start, pageSize);
        return outstandingcases;
    }
}
