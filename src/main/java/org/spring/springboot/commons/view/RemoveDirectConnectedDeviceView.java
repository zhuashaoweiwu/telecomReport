package org.spring.springboot.commons.view;

import java.util.List;

public class RemoveDirectConnectedDeviceView {

    private List<String> deviceIds;

    private List<String> dimmings;

    public List<String> getDimmings() {
        return dimmings;
    }

    public void setDimmings(List<String> dimmings) {
        this.dimmings = dimmings;
    }

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }
}
