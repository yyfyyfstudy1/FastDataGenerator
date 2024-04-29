package com.rongda.system.upms.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *
 * @author daijunpeng
 * @date 2024-02-25 14:47:10
 */
@ApiModel(value = "infDoctorMessageDetailApi")
@Data
@Accessors(chain = true)
public class InfDoctorMessageDetailApi implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "是否开启短信通知 0否 1是", required = true)
    private String enableSms;

    @ApiModelProperty(value = "是否开启消息通知 0否1是", required = true)
    private String enableMessage;
}
