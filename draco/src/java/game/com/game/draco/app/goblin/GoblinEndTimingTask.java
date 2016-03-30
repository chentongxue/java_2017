package com.game.draco.app.goblin;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.game.draco.GameContext;

public class GoblinEndTimingTask implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		if (GameContext.getGoblinApp().isOpen()) {
			GameContext.getGoblinApp().goblinActiveEnd();
		}
	}

}
