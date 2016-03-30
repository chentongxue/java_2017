package com.game.draco.app.unionbattle.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

public class IntegralBattleCreateFightJob implements Job {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//创建对战记录
		GameContext.getUnionIntegralBattleApp().createFightList();
	}

}
