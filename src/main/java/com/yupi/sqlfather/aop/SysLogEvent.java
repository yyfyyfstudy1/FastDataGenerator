package com.rongda.ih.common.log.event;

import com.rongda.system.upms.model.SysLog;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author jhy
 * 系统日志事件
 */
@Getter
@AllArgsConstructor
public class SysLogEvent {
	private final SysLog sysLog;
}
