package com.game.draco.app.quest.service;

import sacred.alliance.magic.core.Service;

/**
 * 任务模块的支持，必须在所有任务相关App启动之后，方可启动
 */
public interface QuestServiceApp extends Service {
	
	public int getFirstMainQuestId();
	
}
