package com.rongda.system.upms.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;

/**
 *
 * @author zhangfen
 * @date 2024-01-25 14:47:10
 */
@ApiModel(value = "infOrgDelivery")
@Data
@Accessors(chain = true)
public class InfOrgDeliveryApi implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "机构配送ID")
    private String orgDeliverId;

    @ApiModelProperty(value = "机构ID", required = true)
    private String orgId;

    @ApiModelProperty(value = "医院自提状态（0：未开启；1：开启", required = false)
    private String orgPickupStatus;

    @ApiModelProperty(value = "医院自提地址", required = false)
    private String orgPickupAddress;

    @ApiModelProperty(value = "药店自提状态（0：未开启；1：开启）", required = false)
    private String drugstorePickupStatus;

    @ApiModelProperty(value = "快递状态（0：未开启；1：开启）", required = false)
    private String expressStatus;

    @ApiModelProperty(value = "快递运费（单位：元）", required = false)
    private Integer expressFee;
}
