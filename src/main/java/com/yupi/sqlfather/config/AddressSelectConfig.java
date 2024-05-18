package com.rongda.system.upms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description :地址选择
 * @Author jhy
 * @Date 2020/11/24 4:37 下午
 */
@Data
@Component
@ConfigurationProperties(prefix = "adressip")
public class AddressSelectConfig {

    /** 域名地址 */
    private String url;

    /**
     * 微信回调接口地址
     */
    private String wxCallbackUrl;
    /**
     * 患者默认头像
     */
    private String userImg;
    /**
     * 医生默认头像
     */
    private String doctorImg;
    /**
     * 机构默认图片
     */
    private String orgImg;

    /**
     * token
     */
    private String tokenUrl;

    /** 电子健康卡单点登录域名 */
    private String empiUrl;

    /** 微信授权回调域名 */
    private String wxAuthCallbackUrl;

}
