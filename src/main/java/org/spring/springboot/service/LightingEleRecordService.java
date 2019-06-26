package org.spring.springboot.service;

import org.spring.springboot.domain.LightingVolEleRecord;

import java.util.List;

public interface LightingEleRecordService {

    int addLightingVolEleRecord(LightingVolEleRecord lightingVolEleRecord);

    public  String RegisterDirectConnectedDevice(String imei);

    public  Boolean Dimming(String deviceId,Integer percent);

    public  Boolean CreateBatchTask(List<String> deviceIds,List<String> dimmings);

    public Boolean  RemoveDirectConnectedDevice(String deviceId);
}
