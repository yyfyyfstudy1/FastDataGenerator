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
@ApiModel(value = "infDoctorDetailApi")
@Data
@Accessors(chain = true)
public class InfDoctorDetailApi implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "")
    private Long id;

    @ApiModelProperty(value = "医生ID")
    private Long doctorId;

    @ApiModelProperty(value = "机构ID")
    private Long orgId;

    @ApiModelProperty(value = "是否开启远程问诊 0否 1是", required = true)
    private Integer isRemote;

    @ApiModelProperty(value = "是否可以在线问诊 0否1是", required = true)
    private Byte isQaOnline;

    @ApiModelProperty(value = "是否便民门诊 0否 1是", required = true)
    private Integer isConvenience;

    @ApiModelProperty(value = "是否开启图文问诊 0否 1是", required = true)
    private Integer isTeletext;

    @ApiModelProperty(value = "是否开启视频问诊 0否 1是", required = true)
    private Integer isVideo;

    /**
     * 是否开启语音问诊 0否 1是
     */
    @ApiModelProperty(value = "是否开启语音文问诊 0否 1是", required = true)
    private Integer isVoice;
    /**
     * 是否开启电话问诊 0否 1是
     */
    @ApiModelProperty(value = "是否开启电话问诊 0否 1是", required = true)
    private Integer isTel;



    @ApiModelProperty(value = "图文问诊挂号费项目ID", required = false)
    private Long teletextRegisterCatId;

    @ApiModelProperty(value = "图文问诊诊疗费项目ID", required = false)
    private Long teletextDiagnosisCatId;

    @ApiModelProperty(value = "视频问诊挂号费项目ID", required = false)
    private Long videoRegisterCatId;

    @ApiModelProperty(value = "视频问诊诊疗费项目ID", required = false)
    private Long videoDiagnosisCatId;

    /**
     * 2024/3/25 新增在线复诊（同之前的便民问诊）费用参数
     */
    @ApiModelProperty(value = "在线复诊问诊挂号费项目ID", required = false)
    private Long followUpRegisterCatId;

    @ApiModelProperty(value = "在线复诊问诊诊疗费项目ID", required = false)
    private Long followUpDiagnosisCatId;


    /**
     * 2024/3/25 新增语音，电话问诊费用参数
     */
    @ApiModelProperty(value = "语音问诊挂号费项目ID", required = false)
    private Long voiceRegisterCatId;

    @ApiModelProperty(value = "语音问诊诊疗费项目ID", required = false)
    private Long voiceDiagnosisCatId;

    @ApiModelProperty(value = "电话问诊挂号费项目ID", required = false)
    private Long telRegisterCatId;

    @ApiModelProperty(value = "电话问诊诊疗费项目ID", required = false)
    private Long telDiagnosisCatId;

}
