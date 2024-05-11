package com.rongda.system.upms.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rongda.system.catalog.feign.RemoteCfgConfigService;
import com.rongda.system.catalog.model.CfgOrgConfig;
import com.rongda.system.common.core.constant.*;
import com.rongda.system.common.core.constant.enums.LoginTypeEnum;
import com.rongda.system.common.core.constant.enums.PatientSourcesTypeEnum;
import com.rongda.system.common.core.util.R;
import com.rongda.system.common.security.service.MqmcUser;
import com.rongda.system.common.security.util.SecurityUtils;
import com.rongda.system.upms.dto.CurrentUser;
import com.rongda.system.upms.dto.SmsLoginDTO;
import com.rongda.system.upms.model.*;
import com.rongda.system.upms.service.*;
import com.rongda.system.upms.service.Impl.InfOrgServiceImpl;
import com.rongda.system.upms.vo.OrgVO;
import com.rongda.system.upms.vo.ProjectInfo;
import com.rongda.system.upms.vo.StaffInfoVO;
import com.rongda.system.upms.vo.UserInfoVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author jhy
 * @Date 2020/5/5 4:07 下午
 * @Description :当前登录人信息处理摸块
 */
@Slf4j
@AllArgsConstructor
@Component
public class CurrentUserInfoHandler {

    private final CacheManager cacheManager;
    private ProjectInfo projectInfo;
    private CustCustomerService custCustomerService;
    private InfMenuService menuService;
    private InfRoleService roleService;
    private InfDoctorService doctorService;
    private InfOrgServiceImpl orgService;
    private RemoteCfgConfigService remoteCfgConfigService;
    private RedisTemplate redisTemplate;
    private InfUserService infUserService;
    private WxUserInfoService wxUserInfoService;
    private final WxAuthorizeService wxAuthorizeService;

    /**
     * jhy 切换当前登录人的所在诊所
     *
     * @return
     */
    public R switchCurrentOrg(String type, Long orgId, String appId) throws Exception {
        MqmcUser userinfo = SecurityUtils.getUser();
        userinfo.setOrgId(orgId);
        return this.getCurrentUserInfo(type + RedisCacheConstant.USER_ROLE_SEPARATOR + LoginTypeEnum.DOCTOR.getType(), appId, userinfo);
    }

    /**
     * jhy 获取当前登录人信息
     *
     * @return
     * @param: WEB@customer  WEB@doctor  WEB@pharmacyUser WX@customer  WX@doctor
     */
    public R getCurrentUserInfo(String type, String appId) {
        return this.getCurrentUserInfo(type, appId, null);
    }


    public R getCurrentUserInfo(String type, String appId, MqmcUser userinfo) {
        Cache cache;
        String[] strs = type.split(RedisCacheConstant.USER_ROLE_SEPARATOR);
        String loginType = strs[0];
        String infoType = strs[1];



        if (userinfo == null) {
            userinfo = SecurityUtils.getUser();
            if (null == userinfo) {
                String str = JSON.toJSONString(redisTemplate.opsForValue().get(RedisCacheConstant.CA_USER_TOKEN + SecurityUtils.getToken()));
                userinfo = JSONObject.parseObject(str, MqmcUser.class);
            }
        }

        CustCustomer customer = custCustomerService.getById(userinfo.getCustomerId());

        /**
         * 2024/2/14 改造: web端端传来的是WEB@general, 则进行判断该用户是否为药房人员或医生
         */
        if(LoginTypeEnum.GENERAL.getType().equals(infoType)){

            if (null == userinfo.getCustomerId()) {
                return new R().error("参数缺失");
            }
            // 查询登录人员是否为外部药房人员
            if (customer ==null){
                return new R().error("未找到客户");
            }
            // 判断查出的customer的sourceCode来源
            if(customer.getPatientSourcesCode()!=null){
                if(customer.getPatientSourcesCode() == PatientSourcesTypeEnum.SOURCES_PHARMACY_USER.getCode()){
                    // 如果为药房人员
                    infoType = LoginTypeEnum.PHARMACYUSER.getType();
                } else if (customer.getPatientSourcesCode() == PatientSourcesTypeEnum.SOURCES_DOCTOR.getCode()) {
                    // 如果为doctor
                    infoType = LoginTypeEnum.DOCTOR.getType();
                }
            }else {
                // 数据库存在一些null的值，先这样判断
                infoType = LoginTypeEnum.DOCTOR.getType();
            }


        }



        userinfo.setRoleType(roleService.getRoleType(userinfo));
        String username = userinfo.getUsername();
        Long customerId = userinfo.getCustomerId();
        Long orgId = userinfo.getOrgId();
        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(userinfo.getId());
        vo.setProjectInfo(projectInfo);

        // 患者登录
        if (LoginTypeEnum.CUSTOMER.getType().equals(infoType)) {
            StaffInfoVO staffInfoVO = new StaffInfoVO();
            staffInfoVO.setCustId(customerId);
            if (null == customerId) {
                return new R().error("客户ID为空");
            }
            staffInfoVO.setCompId(customer.getCompId());
            WxUserInfo wxUserInfo = wxUserInfoService.getInfoByCustId(customerId);
            staffInfoVO.setOpenId(wxUserInfo.getOpenId());
            return new R().success(staffInfoVO);
        }

        /**
         * 机构参数信息
         */
        if (orgId != null) {
            List<CfgOrgConfig> configList = remoteCfgConfigService.getConfigList(orgId, appId);
            vo.setConfigList(configList);
        }
        cache = cacheManager.getCache(RedisCacheConstant.CUSTOMER_DETAILS);
        if (cache != null && cache.get(customerId) != null) {
            vo.setCustCustomer((CustCustomer) cache.get(customerId).get());
        } else {
            if (customer == null) {
                return new R<>(vo, "普通游客，未完善详细信息！");
            } else {
                vo.setCustCustomer(customer);
                cache.put(customerId, customer);
            }
        }
        String msg = null;

        // 如果登录类型为医生
        if (LoginTypeEnum.DOCTOR.getType().equals(infoType)) {
            InfDoctor doctor = doctorService.getByCustomerId(customerId);
            doctor.setOrgId(orgId);
            if (doctor != null) {
                vo.setDoctor(doctor);
            }
            InfDoctor doctor2 = vo.getDoctor();
            if (doctor2 == null || !CheckStatusConstant.PASS.equals(doctor2.getCheckStatus())) {
                msg = "医生审核未通过";
            }
        } else {
            userinfo.setOrgId(vo.getCustCustomer().getOrgId());
        }

        // 获取org列表
        getCurrentOrgList(userinfo, vo);
        List<InfMenu> menuList = getCurrentMenuList(userinfo);
        List<InfRole> roleList = getCurrentRoleList(userinfo);
        cache = cacheManager.getCache(RedisCacheConstant.TOKEN_ROLES);
        cache.put(SecurityUtils.getToken(), roleList);
        vo.setMenuList(menuList);
        vo.setRoleList(roleList);
        userinfo.setName(vo.getCustCustomer().getName());
        cache = cacheManager.getCache(RedisCacheConstant.USER_DETAILS);
        cache.put(username, userinfo);
        redisTemplate.delete(RedisCacheConstant.CA_USER_TOKEN + SecurityUtils.getToken());
        redisTemplate.opsForValue().set(RedisCacheConstant.CA_USER_TOKEN + SecurityUtils.getToken(), userinfo);
        saveCurrent(userinfo, vo);
        if (CharSequenceUtil.isBlank(msg) && (vo.getCurrentOrg() == null || vo.getCurrentOrg().getCheckedStatus() == null || !CheckStatusConstant.PASS.equals(vo.getCurrentOrg().getCheckedStatus()))) {
            return new R(1, "机构或诊所审核未通过", vo);
        }
        return new R(vo, msg);
    }

    /**
     * 绑定医生信息、openId到wx_user_info
     * @param customerId 客户ID
     * @param openId 微信授权后的openID
     * @param wxAppId 微信AppId
     */
    public R bindWxUserInfo(String customerId, String openId, String wxAppId) {
        WxUserInfo wxUserInfo = wxUserInfoService.getOne(
                new LambdaQueryWrapper<WxUserInfo>().eq(WxUserInfo::getCustId, customerId)
                        .orderByDesc(WxUserInfo::getUpdateTime).last("LIMIT 1"));

        if (null == wxUserInfo) {
            wxUserInfo = new WxUserInfo();
            wxUserInfo.setCustId(Long.valueOf(customerId));
        }
        wxUserInfo.setWxAppId(wxAppId);
        wxUserInfo.setOpenId(openId);
        CustCustomer customer = custCustomerService.getById(customerId);
        wxUserInfo.setNickname(customer.getName());
        wxUserInfoService.saveOrUpdate(wxUserInfo);
        return new R().success(wxUserInfo);
    }

    private List<InfMenu> getCurrentMenuList(MqmcUser userinfo) {
        Long userId = userinfo.getId();
        Long orgId = userinfo.getOrgId();
        if (null == userId || null == orgId) {
            return null;
        }
        return menuService.getCurrentList(userId, orgId);
    }

    private List<InfRole> getCurrentRoleList(MqmcUser userinfo) {
        Long userId = userinfo.getId();
        Long orgId = userinfo.getOrgId();
        if (null == userId || null == orgId) {
            return null;
        }
        return roleService.getCurrentList(userId, orgId);
    }

    private void getCurrentOrgList(MqmcUser userinfo, UserInfoVO vo) {
        Long customerId = userinfo.getCustomerId();
        Long orgId = userinfo.getOrgId();
        List<OrgVO> orgList = new ArrayList<>();
        Cache cache = cacheManager.getCache(RedisCacheConstant.ORG_DETAILS);
        if (cache == null || orgId == null) {
            orgList = orgService.selectEnableListByCustId(customerId);
        } else {
            if (cache.get(orgId) != null) {
                OrgVO org = (OrgVO) cache.get(orgId).get();
                orgList.add(org);
            } else {
                OrgVO org = orgService.getCurrentByInfo(orgId, customerId);
                if (org != null) {
                    cache.put(org.getId(), org);
                    orgList.add(org);
                }
            }
        }
        // 如果只有一个诊所，则默认选中
        if (CollectionUtil.isNotEmpty(orgList) && orgList.size() == 1) {
            if (cache != null) {
                cache.put(orgList.get(0).getId(), orgList.get(0));
                cache = cacheManager.getCache(RedisCacheConstant.TOKEN_ORGS);
                cache.put(SecurityUtils.getToken(), orgList.get(0).getId());
            } else {
                cache = cacheManager.getCache(RedisCacheConstant.TOKEN_ORGS);
                cache.evict(SecurityUtils.getToken());
            }
            userinfo.setOrgId(orgList.get(0).getId());
            vo.setCurrentOrg(orgList.get(0));
        }
        vo.setEnableOrgList(orgList);
    }

    public void saveCurrent(MqmcUser user, UserInfoVO vo) {
        Cache cache = cacheManager.getCache(RedisCacheConstant.TOKEN_CURRENT);
        if (null != cache) {
            CurrentUser current = new CurrentUser();
            current.setId(user.getId());
            current.setCustomerId(user.getCustomerId());
            current.setOrgId(user.getOrgId());
            current.setName(user.getName());
            StringBuilder adminStrSB = new StringBuilder();
            if (vo.getCurrentOrg() != null) {
                current.setParentOrgId(vo.getCurrentOrg().getParentId());
                current.setOrgId(vo.getCurrentOrg().getId());
                current.setOrgCode(vo.getCurrentOrg().getCode());
                current.setOrgName(vo.getCurrentOrg().getName());
                current.setOrgLevel(vo.getCurrentOrg().getLevel());
                current.setOrgType(current.getOrgLevel());
                current.setDepartCode(vo.getCurrentOrg().getDepartCode());
                current.setDepartId(vo.getCurrentOrg().getDepartId());
                current.setDepartName(vo.getCurrentOrg().getDepartName());
                current.setWxAppId(vo.getCurrentOrg().getWxAppId());
                current.setWxCode(vo.getCurrentOrg().getWxCode());
                if (user.getId().equals(vo.getCurrentOrg().getAdminId())) {
                    if (OrgLevelConstant.PLATFORM_LEVEL.equals(current.getOrgLevel())) {
                        adminStrSB.append(AdminStrConstant.MQMC_ADMIN);
                        adminStrSB.append(StrUtil.COLON);
                    }
                    if (OrgLevelConstant.OPERATE_CENTER_LEVEL.equals(current.getOrgLevel())) {
                        adminStrSB.append(AdminStrConstant.OP_ADMIN);
                        adminStrSB.append(StrUtil.COLON);
                    }
                    adminStrSB.append(AdminStrConstant.CL_ADMIN);
                    adminStrSB.append(StrUtil.COLON);
                }
                if (user.getId().equals(vo.getCurrentOrg().getDepartAdminId())) {
                    adminStrSB.append(AdminStrConstant.DEPART_ADMIN);
                    adminStrSB.append(StrUtil.COLON);
                }
            }
            if (vo.getDoctor() != null) {
                current.setDoctorId(vo.getDoctor().getId());
            }
            current.setAdminStr(adminStrSB.toString());
            cache.put(SecurityUtils.getToken(), current);
        }
    }

    /**
     * 互联网医院患者端验证码登录
     *
     * @param phone
     * @param smsCode
     * @return
     */
    public R getSmsLogin(String phone, String smsCode) {
        // 验证码
        String smsKey = MessageConstant.MESSAGE_CODE_KEY + phone;
        String codeObj = String.valueOf(redisTemplate.opsForValue().get(smsKey));
        if (!codeObj.equals(smsCode)) {
            return new R().error("验证码错误，请核实！");
        }
        redisTemplate.delete(smsKey);
        SmsLoginDTO smsLoginDTO = new SmsLoginDTO();
        smsLoginDTO.setPhone(phone);
        smsLoginDTO.setPassWord(Constant.DEFAULT_PASSWORD);
//        Oath2UserTokenDTO userToken = wxAuthorizeService.loginOath2(phone);
//        return new R().success(userToken);
        return new R().success(smsLoginDTO);
    }

    public R getAppInfoByPhone(String phone) {
        InfUser infUser = infUserService.getUserByPhone(phone);
        if (null == infUser) {
            return new R().error("无此数据");
        }
        SmsLoginDTO dto = new SmsLoginDTO();
        dto.setPhone(phone);
        dto.setPassWord(Constant.DEFAULT_PASSWORD);
        return new R().success(dto);
    }

}
