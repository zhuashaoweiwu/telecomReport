package org.spring.springboot.utils;

import org.spring.springboot.commons.AccessToken;

import java.util.HashMap;
import java.util.Map;

public class ConstantKey {

    public static final String MANUFACTURERID = "nnlighting";
    public static final String MANUFACTURERNAME = "nnlighting";
    public static final String MODEL = "SLController";
    public static final String PROTOCOLTYPE = "CoAP";
    public static final String DEVICETYPE = "Bulb";
    public static final Integer TIMEOUT = 0;
    public static final Integer TIMEOUT60 = 60;
    public static final Integer MAXRETRANSMIT = 3;

    public static final String SERVICEID = "DeviceParameters";
    public static final String TASKTYPE_DEVICECMD = "DeviceCmd";
    public static final String CREATE_TYPE = "DeviceList";





   public interface method {
        public static String DIMMING = "Dimming";
        public static String TIME_STRATEGY = "TimeStrategy";
        public static String WORK_MODE = "WorkMode";
    }


    private static Map<String, Object> paramDeviceInfo = new HashMap<String, Object>() {{
        put("manufacturerId", MANUFACTURERID);
        put("manufacturerName", MANUFACTURERNAME);
        put("deviceType", DEVICETYPE);
        put("deviceName", DEVICETYPE);
        put("model", MODEL);
        put("protocolType", PROTOCOLTYPE);
    }};


    public static String getParamReg(String nodeId) {
        Map<String, Object> paramReg = new HashMap<String, Object>() {{
            put("verifyCode", nodeId);
            put("nodeId", nodeId);
            put("deviceInfo", paramDeviceInfo);
            put("timeout", TIMEOUT);
        }};
        return JsonUtil.jsonObj2Sting(paramReg);
    }

    public static Map<String, String> getHeader(HttpsUtil httpsUtil) {
        Map<String, String> hander = new HashMap<String, String>() {{
            put(Constant.HEADER_APP_KEY, Constant.APPID);
            put(Constant.HEADER_APP_AUTH, "Bearer" + " " + AccessToken.getInstance().getAccessToken(httpsUtil));
        }};
        return hander;
    }


}
