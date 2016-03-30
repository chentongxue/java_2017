package com.game.draco.app.worldlevel;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

public class CalcWorldLevel implements Job {
	private static final Logger logger = LoggerFactory.getLogger(CalcWorldLevel.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			GameContext.getWorldLevelApp().calcWorldLevel();
		} catch (Exception e) {
			logger.error("CalcWorldLevel.execute error!", e);
		}
	}

}
