package com.hfax.ucard.modules.getui;

import java.io.Serializable;

/**
 * Created by eson on 2017/8/24.
 */

public class PushMessageBean implements Serializable {
    private static final long serialVersionUID = 8721400901274724553L;
    private String title;
    private String body;
    private String PHPushNotificationKeyTabIndex="0";
    private String PHPushNotificationKeyUrl;
    private String PHPushNotificationKeyMessageId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPHPushNotificationKeyTabIndex() {
        return PHPushNotificationKeyTabIndex;
    }

    public void setPHPushNotificationKeyTabIndex(String PHPushNotificationKeyTabIndex) {
        this.PHPushNotificationKeyTabIndex = PHPushNotificationKeyTabIndex;
    }

    public String getPHPushNotificationKeyUrl() {
        return PHPushNotificationKeyUrl;
    }

    public void setPHPushNotificationKeyUrl(String PHPushNotificationKeyUrl) {
        this.PHPushNotificationKeyUrl = PHPushNotificationKeyUrl;
    }

    public String getPHPushNotificationKeyMessageId() {
        return PHPushNotificationKeyMessageId;
    }

    private void setPHPushNotificationKeyMessageId(String PHPushNotificationKeyMessageId) {
        this.PHPushNotificationKeyMessageId = PHPushNotificationKeyMessageId;
    }
}
