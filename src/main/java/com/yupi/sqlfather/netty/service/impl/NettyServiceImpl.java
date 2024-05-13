package com.rongda.system.tx.manager.netty.service.impl;

import com.rongda.system.tx.manager.netty.service.IActionService;
import com.rongda.system.tx.manager.netty.service.NettyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author jhy
 */
@Service
public class NettyServiceImpl implements NettyService {

	@Autowired
	private ApplicationContext spring;

	@Override
	public IActionService getActionService(String action) {
		return spring.getBean(action, IActionService.class);
	}
}
