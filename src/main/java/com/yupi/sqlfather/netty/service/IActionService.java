package com.rongda.system.tx.manager.netty.service;


import com.alibaba.fastjson.JSONObject;

/**
 * @author jhy
 */
public interface IActionService {


	String execute(String channelAddress, String key, JSONObject params);

}
