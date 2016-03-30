package com.game.draco.app.asyncpvp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapAsyncPvpContainer;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.asyncpvp.vo.AsyncPvpBattleInfo;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.message.push.C1506_LadderCdMessage;
import com.google.common.collect.Maps;

public class AsyncPvpAppImpl implements AsyncPvpApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Map<String, AsyncPvpBattleInfo> allRoleInfo = Maps.newConcurrentMap();
	private MapAsyncPvpContainer mapContainer = new MapAsyncPvpContainer();
	private AsyncPvpStorage asyncPvpStorage;
	
	public AsyncPvpStorage getAsyncPvpStorage() {
		return asyncPvpStorage;
	}

	public void setAsyncPvpStorage(AsyncPvpStorage asyncPvpStorage) {
		this.asyncPvpStorage = asyncPvpStorage;
	}

	@Override
	public void logout(RoleInstance role) {
		try {
			AsyncPvpRoleAttr roleAttr = new AsyncPvpRoleAttr(role);
			if(Util.isEmpty(roleAttr.getRoleId())) {
				return;
			}
			RoleHorse roleHorse = GameContext.getRoleHorseApp().getOnBattleRoleHorse(role.getIntRoleId());
			if(roleHorse != null){
				roleAttr.setHorseId(roleHorse.getHorseId());
				roleAttr.setHorseLevel(roleHorse.getLevel());
				roleAttr.setHorseQuality(roleHorse.getQuality());
			}
			RoleHero roleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId());
			if(roleHero != null){
				roleAttr.setHeroId(roleHero.getHeroId());
				roleAttr.setHeroLevel(roleHero.getLevel());
				roleAttr.setHeroQuality(roleHero.getQuality());
			}
			roleAttr.setHeroResId(GameContext.getHeroApp().getRoleHeroHeadId(role.getRoleId()));
			roleAttr.setEquipResId((short)role.getEquipResId());
			roleAttr.preToStore(role);
			
			this.asyncPvpStorage.saveAsyncPvpRoleAttr(roleAttr);
			//角色战斗力
			this.asyncPvpStorage.saveRoleBattleScore(role.getRoleId(),
					GameContext.getAttriApp().getEffectBattleScore(role));
			this.removePvpBattleInfo(role.getRoleId());
		} catch (Exception ex) {
			logger.error("AsyncPvpAppImpl.logout error:", ex);
		}
	}

	@Override
	public void addAsyncPvpBattleInfo(AsyncPvpBattleInfo battleInfo) {
		this.allRoleInfo.put(battleInfo.getRoleId(), battleInfo);
	}

	@Override
	public AsyncPvpBattleInfo getAsyncPvpBattleInfo(String roleId) {
		AsyncPvpBattleInfo battleInfo = this.allRoleInfo.get(roleId);
		this.allRoleInfo.remove(roleId);
		return battleInfo;
	}

	@Override
	public AsyncPvpRoleAttr getAsyncPvpRoleAttr(String roleId) {
		RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if(null != targetRole) {
			return new AsyncPvpRoleAttr(targetRole);
		}
		//从ssdb取
		AsyncPvpRoleAttr roleAttr = this.asyncPvpStorage.getAsyncPvpRoleAttr(roleId);
		if(null != roleAttr) {
			roleAttr.postFromStore();
		}
		return roleAttr;
	}
	
	@Override
	public void resetRoleSkill(RoleInstance role) {
		try{
			Map<Short, RoleSkillStat> skillMap = role.getSkillMap();
			if(Util.isEmpty(skillMap)){
				return;
			}
			C1506_LadderCdMessage msg = new C1506_LadderCdMessage();
			List<Short> skillList = new ArrayList<Short>(); 
			for(RoleSkillStat ss : skillMap.values()) {
				short skillId = ss.getSkillId();
				Skill skill = GameContext.getSkillApp().getSkill(skillId);
				if(null == skill) {
					continue;
				}
				if(!skill.isActiveSkill()) {
					continue;
				}
				ss.setLastProcessTime(0);
				skillList.add(skillId);
			}
			
			if(!Util.isEmpty(skillList)) {
				short[] arr = new short[skillList.size()];
				for(int i=0;i<skillList.size();i++) {
					arr[i] = skillList.get(i);
				}
				msg.setSkillIdArr(arr);
			}
			
			role.getBehavior().sendMessage(msg);
		}catch(Exception e){
			logger.error("AsyncPvpApp.resetRoleSkill:",e);
		}
	}

	@Override
	public MapAsyncPvpContainer getMapAsyncPvpContainer() {
		return mapContainer;
	}

	@Override
	public List<AsyncPvpRoleAttr> getAsyncPvpRoleAttrList(List<String> ids) {
		if(Util.isEmpty(ids)) {
			return null;
		}
		return this.asyncPvpStorage.getAsyncPvpRoleAttrList(ids);
	}

	@Override
	public Map<String, String> getRoleBattleScores(String key, String score,
			int limit) {
		return this.asyncPvpStorage.getRoleBattleScores(key, score, limit);
	}
	
	/**
	 * 角色下线时把角色相关的battleInfo移除 
	 */
	private void removePvpBattleInfo(String roleId) {
		this.allRoleInfo.remove(roleId);
	}

	@Override
	public Map<String, String> getRoleBattleScores(String key,
			String startScore, String endScore, int limit) {
		return this.asyncPvpStorage.getRoleBattleScores(key, startScore,endScore, limit);
	}

	@Override
	public long getTotalRanking() {
		return asyncPvpStorage.getTotalRanking();
	}

	@Override
	public int getRoleBattleScoreRanking(String roleId) {
		return asyncPvpStorage.getRoleBattleScoreRanking(roleId);
	}

	@Override
	public void saveRoleAsyncArena(String roleId, int honor) {
		asyncPvpStorage.saveRoleAsyncArena(roleId, honor);
	}

	@Override
	public int getRoleAsyncArenaRanking(String roleId) {
		return asyncPvpStorage.getRoleAsyncArenaRanking(roleId);
	}
	
}
