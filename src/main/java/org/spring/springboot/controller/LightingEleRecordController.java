package org.spring.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.commons.ApiResult;
import org.spring.springboot.domain.LightingVolEleRecord;
import org.spring.springboot.service.LightingEleRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by bysocket on 07/02/2017.
 */
@RestController
public class LightingEleRecordController {

    Logger log = LoggerFactory.getLogger(LightingEleRecordController.class);

    @Autowired
    private LightingEleRecordService lightingEleRecordService;

    @RequestMapping(value = "/api/addLightingVolEleRecord", method = RequestMethod.POST)
    public ApiResult addLightingVolEleRecord( LightingVolEleRecord lightingVolEleRecord) {
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

}
