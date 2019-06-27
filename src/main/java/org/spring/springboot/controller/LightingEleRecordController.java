package org.spring.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.commons.ApiResult;
import org.spring.springboot.commons.ResultCode;
import org.spring.springboot.commons.view.RemoveDirectConnectedDeviceView;
import org.spring.springboot.domain.LightingVolEleRecord;
import org.spring.springboot.service.LightingEleRecordService;
import org.spring.springboot.utils.PubMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LightingEleRecordController {

    Logger log = LoggerFactory.getLogger(LightingEleRecordController.class);

    @Autowired
    private LightingEleRecordService lightingEleRecordService;

    @RequestMapping(value = "/api/addLightingVolEleRecord", method = RequestMethod.POST)
    public ApiResult addLightingVolEleRecord(LightingVolEleRecord lightingVolEleRecord) {
        try {
            lightingEleRecordService.addLightingVolEleRecord(lightingVolEleRecord);
            return new ApiResult.Builder<>().success().build();
        } catch (Exception e) {
            log.error("增加电信上报数据信息失败.");
            e.printStackTrace();
            log.error(e.getMessage());
            return new ApiResult.Builder<>().failure().build();
        }
    }

    @RequestMapping(value = "/api/RegisterDirectConnectedDevice", method = RequestMethod.POST)
    public ApiResult RegisterDirectConnectedDevice(String imei) {
        try {
            String device = lightingEleRecordService.RegisterDirectConnectedDevice(imei);
            return new ApiResult.Builder<>().success(device).build();
        } catch (Exception e) {
            log.error("增加设备 error.");
            e.printStackTrace();
            log.error(e.getMessage());
            return new ApiResult.Builder<>().failure().build();
        }
    }

    @RequestMapping(value = "/api/Dimming", method = RequestMethod.POST)
    public ApiResult Dimming(String deviceId, Integer percent) {
        try {
            return new ApiResult.Builder<>().success(lightingEleRecordService.Dimming(deviceId, percent)).build();
        } catch (Exception e) {
            log.error("调光 Controll error.");
            e.printStackTrace();
            log.error(e.getMessage());
            return new ApiResult.Builder<>().failure().build();
        }
    }


    @RequestMapping(value = "/api/CreateBatchTask", method = RequestMethod.POST)
    public ApiResult CreateBatchTask(@RequestBody RemoveDirectConnectedDeviceView removeDirectConnectedDeviceView) {
        try {
            if (PubMethod.isEmpty(removeDirectConnectedDeviceView) || PubMethod.isEmpty(removeDirectConnectedDeviceView.getDeviceIds())
                    || PubMethod.isEmpty(removeDirectConnectedDeviceView.getDimmings())) {
                return new ApiResult.Builder<>().failure(ResultCode.PARAM_ERROR).build();
            }

            return new ApiResult.Builder<>().success(lightingEleRecordService.CreateBatchTask(removeDirectConnectedDeviceView.getDeviceIds(),
                    removeDirectConnectedDeviceView.getDimmings())).build();
        } catch (Exception e) {
            log.error("任务 Controll error.");
            e.printStackTrace();
            log.error(e.getMessage());
            return new ApiResult.Builder<>().failure().build();
        }
    }


    @RequestMapping(value = "/api/RemoveDirectConnectedDevice", method = RequestMethod.POST)
    public ApiResult RemoveDirectConnectedDevice(@RequestBody RemoveDirectConnectedDeviceView removeDirectConnectedDeviceView) {
        try {//  if request.getHeader("User-Agent").equals("Netease0.1") pass
            if (PubMethod.isEmpty(removeDirectConnectedDeviceView.getDeviceIds()))
                return new ApiResult.Builder<>().failure().build();
            return new ApiResult.Builder<>().success(lightingEleRecordService.RemoveDirectConnectedDevice(removeDirectConnectedDeviceView.getDeviceIds())).build();
        } catch (Exception e) {
            log.error("删除 Controll error.");
            e.printStackTrace();
            log.error(e.getMessage());
            return new ApiResult.Builder<>().failure().build();
        }
    }


}
