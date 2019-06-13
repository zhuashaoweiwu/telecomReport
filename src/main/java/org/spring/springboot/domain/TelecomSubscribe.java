package org.spring.springboot.domain;

import java.util.Date;

public class TelecomSubscribe {

    public TelecomSubscribe (){}

    public TelecomSubscribe (String subscribeId,String callbackUrl,String serviceNotifyType){
            this.subscribeId = subscribeId;
            this.callbackUrl = callbackUrl;
            this.serviceNotifyType = serviceNotifyType;
            Date date = new Date();
            this.gmtCreated = date;
            this.gmtUpdated = date;
    }

    private Long id;

    private String subscribeId;

    private String callbackUrl;

    private String serviceNotifyType;

    private Date gmtCreated;

    private Date gmtUpdated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(String subscribeId) {
        this.subscribeId = subscribeId == null ? null : subscribeId.trim();
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl == null ? null : callbackUrl.trim();
    }

    public String getServiceNotifyType() {
        return serviceNotifyType;
    }

    public void setServiceNotifyType(String serviceNotifyType) {
        this.serviceNotifyType = serviceNotifyType == null ? null : serviceNotifyType.trim();
    }

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtUpdated() {
        return gmtUpdated;
    }

    public void setGmtUpdated(Date gmtUpdated) {
        this.gmtUpdated = gmtUpdated;
    }
}