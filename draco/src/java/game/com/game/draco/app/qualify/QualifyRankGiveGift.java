package com.game.draco.app.qualify;

import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.qualify.domain.QualifyRank;

public class QualifyRankGiveGift implements Job {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Map<Short, QualifyRank> qualifyMap = GameContext.getQualifyApp().getQualifyRankMap();
		if (Util.isEmpty(qualifyMap)) {
			return;
		}
		for (QualifyRank roleQualify : qualifyMap.values()) {
			try {
				if (null == roleQualify) {
					continue;
				}
				GameContext.getQualifyApp().giveQualifyRankGift(roleQualify);
			} catch (Exception ex) {
				logger.error("QualifyAppImpl.execute error!", ex);
			}
		}
	}

}
