package com.game.draco.app.copy.line.vo;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.copy.line.domain.RoleCopyLineReward;
import com.game.draco.app.copy.line.domain.RoleCopyLineScore;

public @Data class CopyLineRoleData {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String roleId;//角色ID
	/** KEY=章节ID,VALUE=领奖记录 */
	private Map<Byte,RoleCopyLineReward> roleRewardMap = new HashMap<Byte,RoleCopyLineReward>();
	/** KEY=章节ID,VALUE=(KEY=副本序列,VALUE=星级记录) */
	private TreeMap<Byte,TreeMap<Byte,RoleCopyLineScore>> roleScoreMap = new TreeMap<Byte,TreeMap<Byte,RoleCopyLineScore>>();
	
	public byte getScoreMaxChapterId(){
		if(Util.isEmpty(this.roleScoreMap)){
			return 0;
		}
		return this.roleScoreMap.lastKey();
	}
	
	public byte getScoreMaxCopyIndex(byte chapterId){
		TreeMap<Byte,RoleCopyLineScore> scoreMap = this.getCopyScoreMap(chapterId);
		if(Util.isEmpty(scoreMap)){
			return 0;
		}
		return scoreMap.lastKey();
	}
	
	public byte getHistoryStar(byte chapterId, byte copyIndex){
		TreeMap<Byte,RoleCopyLineScore> map = this.getCopyScoreMap(chapterId);
		if(Util.isEmpty(map)){
			return 0;
		}
		RoleCopyLineScore score = map.get(copyIndex);
		if(null == score){
			return 0;
		}
		return score.getMaxStar();
	}
	
	/**
	 * 获取章节星级总和
	 * @param chapterId
	 * @return
	 */
	public int getChapterStarSum(byte chapterId){
		TreeMap<Byte,RoleCopyLineScore> scoreMap = this.getCopyScoreMap(chapterId);
		if(Util.isEmpty(scoreMap)){
			return 0;
		}
		int sum = 0;
		for(RoleCopyLineScore score : scoreMap.values()){
			if(null == score){
				continue;
			}
			sum += score.getMaxStar();
		}
		return sum;
	}
	
	public RoleCopyLineReward getRoleCopyLineReward(byte chapterId){
		return this.roleRewardMap.get(chapterId);
	}
	
	private TreeMap<Byte,RoleCopyLineScore> getCopyScoreMap(byte chapterId){
		return this.roleScoreMap.get(chapterId);
	}
	
	/**
	 * 获取已领取星级记录
	 * @param chapterId
	 * @return
	 */
	public int getChapterTakeStarNum(byte chapterId){
		RoleCopyLineReward reward = this.getRoleCopyLineReward(chapterId);
		if(null == reward){
			return 0;
		}
		return reward.getTakeStarNum();
	}
	
	/**
	 * 更新领奖星级记录
	 * @param role
	 * @param chapterId
	 * @param takeStarNum
	 */
	public void updateTakeStarNum(RoleInstance role, byte chapterId, int takeStarNum){
		try {
			RoleCopyLineReward reward = this.getRoleCopyLineReward(chapterId);
			if(null == reward){
				reward = new RoleCopyLineReward();
				reward.setRoleId(this.roleId);
				reward.setChapterId(chapterId);
				reward.setTakeStarNum(takeStarNum);
				this.roleRewardMap.put(chapterId, reward);
				GameContext.getBaseDAO().insert(reward);
			}else{
				reward.setTakeStarNum(takeStarNum);
				GameContext.getBaseDAO().update(reward);
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".updateTakeStarNum error: ", e);
		}
	}
	
	/**
	 * 更新副本评分
	 * @param role
	 * @param chapterId
	 * @param copyIndex
	 * @param starScore
	 */
	public void updateCopyScore(RoleInstance role, byte chapterId, byte copyIndex, byte starScore){
		try {
			TreeMap<Byte,RoleCopyLineScore> scoreMap = this.roleScoreMap.get(chapterId);
			if(Util.isEmpty(scoreMap)){
				scoreMap = new TreeMap<Byte,RoleCopyLineScore>();
				this.roleScoreMap.put(chapterId, scoreMap);
			}
			RoleCopyLineScore record = scoreMap.get(copyIndex);
			if(null == record){
				record = new RoleCopyLineScore();
				record.setRoleId(this.roleId);
				record.setChapterId(chapterId);
				record.setCopyIndex(copyIndex);
				record.setMaxStar(starScore);
				scoreMap.put(copyIndex, record);
				GameContext.getBaseDAO().insert(record);
			}else{
				byte maxStar = record.getMaxStar();
				if(starScore <= maxStar){
					return;
				}
				record.setMaxStar(starScore);
				GameContext.getBaseDAO().update(record);
			}
		} catch (Exception e) {
			this.logger.error(this.getClass().getName() + ".updateCopyScore error: ", e);
		}
	}
	
}
