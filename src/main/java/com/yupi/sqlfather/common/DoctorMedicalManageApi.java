package com.rongda.system.upms.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *
 * @author zhangfen
 * @date 2024-03-07 14:47:10
 */
@ApiModel(value = "doctorMedicalManage")
@Data
@Accessors(chain = true)
public class DoctorMedicalManageApi implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "智能医嘱管理ID")
    private String medicalManageId;

    @ApiModelProperty(value = "医生ID", required = true)
    private String doctorId;

    @ApiModelProperty(value = "西药处方状态（0：未开启；1：开启）", required = false)
    private String westernRecipeStatus;

    @ApiModelProperty(value = "中药处方状态（0：未开启；1：开启）", required = false)
    private String chineseRecipeStatus;

    @ApiModelProperty(value = "西医诊断状态（0：未开启；1：开启）", required = false)
    private String westernDiagnosisStatus;

    @ApiModelProperty(value = "中医诊断状态（0：未开启；1：开启）", required = false)
    private String chineseDiagnosisStatus;

}
