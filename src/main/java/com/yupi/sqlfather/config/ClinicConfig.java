package com.rongda.system.upms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties(prefix = "clinic")
public class ClinicConfig {

    private String HSP_CODE;
    /**
     * 请求url前缀
     */
    private String url;
    /**
     * 病患绑定接口
     */
    private String bindingCHTID;
    /**
     * 取排班表接口
     */
    private String getScheduleList;
    /**
     * 短信发送接口
     */
    private String sendText;

}
