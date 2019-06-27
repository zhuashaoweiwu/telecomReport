package org.spring.springboot.service.impl;

import com.alibaba.fastjson.serializer.SerializerFeature;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.listener.SimpleHttpServer;
import org.spring.springboot.mapper.LightingVolEleRecordMapper;
import org.spring.springboot.domain.LightingVolEleRecord;
import org.spring.springboot.service.LightingEleRecordService;
import org.spring.springboot.utils.Constant;
import org.spring.springboot.utils.ConstantKey;
import org.spring.springboot.utils.HttpsUtil;
import org.spring.springboot.utils.JsonUtil;
import org.spring.springboot.utils.PubMethod;
import org.spring.springboot.utils.StreamClosedHttpResponse;
import org.spring.springboot.utils.TableNameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LightingEleRecordServiceImpl implements LightingEleRecordService {

    @Autowired
    private LightingVolEleRecordMapper lightingVolEleRecordMapper;

    Logger log = LoggerFactory.getLogger(LightingEleRecordServiceImpl.class);

    /**
     * http  时间紧就tm一天,直接new对象,待有时间改回池化,主动回收链接.
     */

    @Override
    public int addLightingVolEleRecord(LightingVolEleRecord lightingVolEleRecord) {
        Date now = new Date();
        lightingVolEleRecord.setGmtUpdated(now);
        lightingVolEleRecord.setGmtCreated(now);
        return this.lightingVolEleRecordMapper.addLightingVolEleRecord(lightingVolEleRecord, TableNameUtil.getTableNameByDate(SimpleHttpServer.LIGHTING_VOL_ELE_RECORD_TABLENAMEROOT, new Date()));
    }

    @Override
    public String RegisterDirectConnectedDevice(String imei) {
        HttpsUtil httpsUtil = new HttpsUtil();
        try {
            httpsUtil.initSSLConfigForTwoWay();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "";
        }
        StreamClosedHttpResponse responseRegisterDirectConnectedDevice =
                httpsUtil.doPostJsonGetStatusLine(Constant.REGISTER_DIRECT_CONNECTED_DEVICE,
                        ConstantKey.getHeader(httpsUtil), ConstantKey.getParamReg(imei.replaceAll("^(0+)", "")));

        System.out.println("RegisterDirectConnectedDevice, response content:");
        System.out.println(responseRegisterDirectConnectedDevice.getStatusLine());
        System.out.println(responseRegisterDirectConnectedDevice.getContent());
        log.warn("RegisterDirectConnectedDevice, response content:");
        log.warn("statusCode : " + responseRegisterDirectConnectedDevice.getStatusLine().getStatusCode());
        log.warn(responseRegisterDirectConnectedDevice.getContent());
        System.out.println();
        Map<String, String> data = JsonUtil.jsonString2SimpleObj(responseRegisterDirectConnectedDevice.getContent(), Map.class);

        return data.get("deviceId");
    }

    @Override
    public Boolean Dimming(String deviceId, Integer percent) {
        if (PubMethod.isEmpty(deviceId) || PubMethod.isEmpty(percent)) {
            log.error("调光失败 参数为空 :  deviceId" + deviceId + "  percent :" + percent);
            return Boolean.FALSE;
        }
        Map<String, Object> DimmingParas = new HashMap<>();
        DimmingParas.put("Dimming", percent);

        Map<String, Object> paramCommand = new HashMap<>();
        paramCommand.put("serviceId", ConstantKey.SERVICEID);
        paramCommand.put("method", ConstantKey.method.DIMMING);
        paramCommand.put("paras", DimmingParas);

        Map<String, Object> paramCreateDeviceCommand = new HashMap<>();
        paramCreateDeviceCommand.put("deviceId", deviceId);
        paramCreateDeviceCommand.put("command", paramCommand);
        paramCreateDeviceCommand.put("expireTime", ConstantKey.TIMEOUT);
        paramCreateDeviceCommand.put("maxRetransmit", ConstantKey.MAXRETRANSMIT);

        HttpsUtil httpsUtil = new HttpsUtil();
        try {
            httpsUtil.initSSLConfigForTwoWay();

            HttpResponse responseCreateDeviceCommand = httpsUtil.doPostJson(Constant.CREATE_DEVICE_CMD, ConstantKey.getHeader(httpsUtil),
                    com.alibaba.fastjson.JSON.toJSONString(paramCreateDeviceCommand, SerializerFeature.DisableCircularReferenceDetect));
            String responseBody = httpsUtil.getHttpResponseBody(responseCreateDeviceCommand);

            System.out.println("CreateDeviceCommand, response content:");
            System.out.println(responseCreateDeviceCommand.getStatusLine());
            System.out.println(responseBody);
            System.out.println();
            log.info("调光 CreateDeviceCommand, response content:");
            log.info("status " + responseCreateDeviceCommand.getStatusLine().getStatusCode());
            log.info(responseBody);
            return Boolean.TRUE;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("调光失败!");
            log.error(e.getMessage());

        }
        return Boolean.FALSE;
    }


    private void workMode() {

    }

    @Override
    public Boolean CreateBatchTask(List<String> deviceIds, List<String> dimmings) {
        if (dimmings.size() > 20) {
            log.error("任务失败   任务过长 ");
            return Boolean.FALSE;
        }
        /**等待硬件改版*/
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                HttpsUtil httpsUtil = new HttpsUtil();
                try {
                    httpsUtil.initSSLConfigForTwoWay();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Map<String, Object> paras = new HashMap<>();
                paras.put("WorkMode", 0);

                Map<String, Object> command = new HashMap<>();
                command.put("serviceId", ConstantKey.SERVICEID);
                command.put("method", ConstantKey.method.WORK_MODE);
                command.put("paras", paras);

                Map<String, Object> paramBody_DeviceCmd = new HashMap<>();
                paramBody_DeviceCmd.put("type", ConstantKey.CREATE_TYPE);
                paramBody_DeviceCmd.put("deviceList", deviceIds);
                paramBody_DeviceCmd.put("command", command);
                paramBody_DeviceCmd.put("maxRetransmit", ConstantKey.MAXRETRANSMIT);

                Map<String, Object> paramDeviceCmdTask = new HashMap<>();
                paramDeviceCmdTask.put("appId", Constant.APPID);
                paramDeviceCmdTask.put("timeout", ConstantKey.TIMEOUT60);
                paramDeviceCmdTask.put("taskName", UUID.randomUUID().toString().replaceAll("-", ""));
                paramDeviceCmdTask.put("taskType", ConstantKey.TASKTYPE_DEVICECMD);
                paramDeviceCmdTask.put("param", paramBody_DeviceCmd);
                /**设置自动模式*/
                StreamClosedHttpResponse responseDeviceCmdTask = httpsUtil.doPostJsonGetStatusLine(
                        Constant.CREATE_BATCH_TASK, ConstantKey.getHeader(httpsUtil),
                        com.alibaba.fastjson.JSON.toJSONString(paramDeviceCmdTask, SerializerFeature.DisableCircularReferenceDetect));
                System.out.println("CreateBatchCmdTask, response content:");
                System.out.println(responseDeviceCmdTask.getStatusLine());
                System.out.println(responseDeviceCmdTask.getContent());
                System.out.println();
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                paras.remove("WorkMode");
                command.put("method", ConstantKey.method.TIME_STRATEGY);
                for (String dimming : dimmings) {
                    paras.put("TimeStrategy", dimming);
                    paramDeviceCmdTask.put("taskName", UUID.randomUUID().toString().replaceAll("-", ""));
                    responseDeviceCmdTask = httpsUtil.doPostJsonGetStatusLine(
                            Constant.CREATE_BATCH_TASK, ConstantKey.getHeader(httpsUtil),
                            com.alibaba.fastjson.JSON.toJSONString(paramDeviceCmdTask, SerializerFeature.DisableCircularReferenceDetect));

                    System.out.println("CreateBatchCmdTask, response content:");
                    System.out.println(responseDeviceCmdTask.getStatusLine());
                    System.out.println(responseDeviceCmdTask.getContent());
                    System.out.println();
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }});

        return Boolean.TRUE;
    }

    @Override
    public Boolean RemoveDirectConnectedDevice(List<String> deviceIds) {
        HttpsUtil httpsUtil = new HttpsUtil();
        try {
            httpsUtil.initSSLConfigForTwoWay();
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }


        StreamClosedHttpResponse streamClosedHttpResponse = null;
        for (String deviceId : deviceIds) {
            try {
                streamClosedHttpResponse = httpsUtil.doDeleteWithParasGetStatusLine(Constant.DELETE_DIRECT_CONNECTED_DEVICE + "/" + deviceId
                        , null, ConstantKey.getHeader(httpsUtil));
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.getMessage());
                return Boolean.FALSE;
            }
            System.out.println("DeleteDirectConnectedDevice, response content:");
            System.out.println(streamClosedHttpResponse.getStatusLine());
            System.out.println(streamClosedHttpResponse.getContent());
            System.out.println();
        }
        return Boolean.TRUE;
    }
}
