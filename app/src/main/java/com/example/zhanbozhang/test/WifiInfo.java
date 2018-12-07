package com.example.zhanbozhang.test;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;

/**
 * Created by zhanbo.zhang on 2018/6/15.
 */

public class WifiInfo {

    public static final int STATE_CONNECTING = 1;

    public static final int STATE_CONNECTED = 2;

    public static final int STATE_STATIC = 3;

    public static final int WIFICIPHER_NOPASS = 1;
    public static final int WIFICIPHER_WEP = 2;
    public static final int WIFICIPHER_WPA = 3;
    public static final int WIFICIPHER_PSK = 4;
    public static final int WIFICIPHER_EAP = 5;
    /*public static final Creator<WifiInfo> CREATOR = new Creator<WifiInfo>() {
        @Override
        public WifiInfo createFromParcel(Parcel in) {
            return new WifiInfo(in);
        }

        @Override
        public WifiInfo[] newArray(int size) {
            return new WifiInfo[size];
        }
    };*/
    private String BSSID;
    private String wifiName;
    private String wifiSummary;
    private boolean isLocked;
    private int signal;
    private int level;
    private int status = STATE_STATIC;
    private int type;

    public static WifiInfo create(WifiConfiguration configuration, android.net.wifi.WifiInfo info) {
        WifiInfo result = new WifiInfo();

        result.BSSID = TextUtils.isEmpty(configuration.BSSID) ? info.getSSID() : configuration.BSSID;
        result.wifiName = configuration.SSID;
        result.level = info.getRssi();
        result.isLocked = result.isLocked(configuration);
        result.signal = result.calculateCount(result.level);
        result.type = result.getType(configuration);

        return result;
    }

    private int getType(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return WIFICIPHER_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return WIFICIPHER_EAP;
        }
        return (config.wepKeys[0] != null) ? WIFICIPHER_WEP : WIFICIPHER_NOPASS;
    }

    private boolean isLocked(WifiConfiguration config) {
        return getType(config) != WIFICIPHER_NOPASS;
    }

    public WifiInfo() {
    }

    public WifiInfo(ScanResult value) {
        this.BSSID = value.BSSID;
        this.wifiName = value.SSID;
        this.level = value.level;
        this.isLocked = isLocked(value);
        this.signal = calculateCount(value.level);
        this.type = getType(value.capabilities);
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getWifiSummary() {
        return wifiSummary;
    }

    public void setWifiSummary(String wifiSummary) {
        this.wifiSummary = wifiSummary;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public int getSignal() {
        return signal;
    }

    public int getLevel() {
        return level;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBSSID() {
        return BSSID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int calculateCount(int number) {
        int count;

        number = (number < -95) ? -95 : number;
        number = (number > -35) ? -35 : number;

        float scale = (float) (number + 95) / 60;
        count = (int) (scale * 4);
        return count;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    private boolean isLocked(ScanResult result) {
        if (result.capabilities == null)
            return false;

        String capabilities = result.capabilities.trim();
        if (capabilities != null && (capabilities.equals("") || capabilities.equals("[ESS]"))) {
            return false;
        }
        return true;
    }

    public int getType(String capabilities) {
        if (capabilities.toLowerCase().contains("wpa")) {
            return WIFICIPHER_WPA;
        } else if (capabilities.toLowerCase().contains("wep")) {
            return WIFICIPHER_WEP;
        } else {
            return WIFICIPHER_NOPASS;
        }
    }

    /*@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(STATE_CONNECTING);
        dest.writeInt(STATE_CONNECTED);
        dest.writeInt(STATE_STATIC);

        dest.writeString(BSSID);
        dest.writeString(wifiName);
        dest.writeString(wifiSummary);

        dest.writeByte((byte) (isLocked ? 1 : 0));
        dest.writeInt(signal);
        dest.writeInt(level);
        dest.writeInt(status);
    }*/
}
