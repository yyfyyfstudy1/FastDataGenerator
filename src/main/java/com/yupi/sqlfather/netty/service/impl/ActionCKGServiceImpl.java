package com.rongda.system.tx.manager.netty.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rongda.system.tx.manager.manager.service.TxManagerService;
import com.rongda.system.tx.manager.netty.service.IActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 检查事务组
 * @author jhy
 */
@Service(value = "ckg")
public class ActionCKGServiceImpl implements IActionService {


	@Autowired
	private TxManagerService txManagerService;

	@Override
	public String execute(String channelAddress, String key, JSONObject params) {
		String res = "";
		String groupId = params.getString("g");
		String taskId = params.getString("t");
		int bs = txManagerService.cleanNotifyTransaction(groupId, taskId);

		res = String.valueOf(bs);
		return res;
	}
}
