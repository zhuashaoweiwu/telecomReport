package org.spring.springboot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.listener.SimpleHttpServer;
import org.spring.springboot.mapper.LightingVolEleRecordMapper;
import org.spring.springboot.domain.LightingVolEleRecord;
import org.spring.springboot.service.LightingEleRecordService;
import org.spring.springboot.utils.TableNameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LightingEleRecordServiceImpl implements LightingEleRecordService {

    @Autowired
    private LightingVolEleRecordMapper lightingVolEleRecordMapper;
    Logger log = LoggerFactory.getLogger(LightingEleRecordServiceImpl.class);

    @Override
    public int addLightingVolEleRecord(LightingVolEleRecord lightingVolEleRecord)  {
        Date now = new Date();
        lightingVolEleRecord.setGmtUpdated(now);
        lightingVolEleRecord.setGmtCreated(now);
        return this.lightingVolEleRecordMapper.addLightingVolEleRecord(lightingVolEleRecord, TableNameUtil.getTableNameByDate(SimpleHttpServer.LIGHTING_VOL_ELE_RECORD_TABLENAMEROOT, new Date()));
    }
}
