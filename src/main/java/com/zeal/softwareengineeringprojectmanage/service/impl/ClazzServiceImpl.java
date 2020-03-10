package com.zeal.softwareengineeringprojectmanage.service.impl;

import com.zeal.softwareengineeringprojectmanage.bean.Clazz;
import com.zeal.softwareengineeringprojectmanage.mapper.ClazzMapper;
import com.zeal.softwareengineeringprojectmanage.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClazzServiceImpl implements ClazzService {
    @Autowired
    ClazzMapper clazzMapper;
    @Override
    public List<Clazz> selectAll() {
        List<Clazz> clazzes = clazzMapper.selectAll();
        return clazzes;
    }
}
