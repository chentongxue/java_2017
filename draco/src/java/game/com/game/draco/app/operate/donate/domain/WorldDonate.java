package com.game.draco.app.operate.donate.domain;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;

/**
 * 活动全民捐献值db信息
 * 停服务器时入库
 */
public @Data class WorldDonate {
	private int activeId;
	private int curCount ; //当前总量
	
	private AtomicInteger atomicCurCount;
	private boolean existRecord;
	private boolean changed;
	
	public void init() {
		atomicCurCount = new AtomicInteger(curCount);
	}
	
	/**
	 * 更新计数
	 */
	public int getCurCount() {
		return atomicCurCount.get();
	}
	
	public int addAndGetCount(int count) {
		return atomicCurCount.addAndGet(count);
	}
}
