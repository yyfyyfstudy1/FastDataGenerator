package com.rongda.system.tx.manager.netty.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rongda.system.tx.manager.netty.service.IActionService;
import com.rongda.system.tx.manager.config.ConfigReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 心跳包
 * @author jhy
 */
@Service(value = "h")
public class ActionHServiceImpl implements IActionService {


	@Autowired
	private ConfigReader configReader;

	@Override
	public String execute(String channelAddress, String key, JSONObject params) {
		return String.valueOf(configReader.getTransactionNettyDelayTime());
	}
}
