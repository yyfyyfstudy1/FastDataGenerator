package com.rongda.system.upms.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@Accessors(chain = true)
public class InfOrgMedicalCopyApi implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "机构ID不能为空")
    private Long orgId;

    /** 病案复印自提地址 */
    @NotNull(message = "病案复印自提地址不能为空")
    private String recordCopyPickupAddress;

    /** 病案复印单价（单位：元） */
    @NotNull(message = "病案复印单价不能为空")
    private Integer recordCopyFee;

    /** 病案复印联系电话 */
    @NotNull(message = "病案复印联系电话不能为空")
    private String recordCopyTel;

}
