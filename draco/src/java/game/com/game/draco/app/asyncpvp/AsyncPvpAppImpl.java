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
import com.game.draco.app.pet.domain.RolePet;
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
	public int onLogin(RoleInstance role, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int onLogout(RoleInstance role, Object context) {
		try {
			AsyncPvpRoleAttr roleAttr = new AsyncPvpRoleAttr(role);
			if(Util.isEmpty(roleAttr.getRoleId())) {
				return 1;
			}
			multiRoleAttr(role.getIntRoleId(),roleAttr);
			roleAttr.setHeroHeadId(role.getHeroHeadId());
			roleAttr.setSeriesId(role.getHeroSeriesId());
			roleAttr.setGearId(role.getHeroGearId());
			
			byte vipLevel = GameContext.getVipApp().getVipLevel(role);
			roleAttr.setVipLevel(vipLevel);
			this.asyncPvpStorage.saveAsyncPvpRoleAttr(roleAttr);
			if(null != GameContext.getUserHeroApp().getOnBattleRoleHero(role.getRoleId())){
				//有出战英雄才保存战斗力
				//角色战斗力
				this.asyncPvpStorage.saveRoleBattleScore(role.getRoleId(),
						GameContext.getAttriApp().getEffectBattleScore(role));
			}
			this.removePvpBattleInfo(role.getRoleId());
		} catch (Exception ex) {
			logger.error("AsyncPvpAppImpl.logout error:", ex);
			return 0;
		}
		
		return 1;
	}
	
	private void multiRoleAttr(int roleId,AsyncPvpRoleAttr roleAttr ){
		RoleHorse roleHorse = GameContext.getRoleHorseApp().getOnBattleRoleHorse(roleId);
		if(roleHorse != null){
			roleAttr.setHorseId(roleHorse.getHorseId());
			roleAttr.setHorseQuality(roleHorse.getQuality());
			roleAttr.setHorseStar(roleHorse.getStar());
		}
		RoleHero roleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(String.valueOf(roleId));
		if(roleHero != null){
			roleAttr.setHeroId(roleHero.getHeroId());
			roleAttr.setHeroLevel(roleHero.getLevel());
			roleAttr.setHeroQuality(roleHero.getQuality());
			roleAttr.setHeroStar(roleHero.getStar());
		}
		RolePet rolePet = GameContext.getPetApp().getBattleRolePet(String.valueOf(roleId));
		if (null != rolePet) {
			roleAttr.setPetId(rolePet.getPetId());
			roleAttr.setPetLevel((byte) rolePet.getLevel());
			roleAttr.setPetQuality(rolePet.getQuality());
			roleAttr.setPetStar(rolePet.getStar());
		}
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		return 0;
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
			AsyncPvpRoleAttr attr = new AsyncPvpRoleAttr(targetRole);
			multiRoleAttr(targetRole.getIntRoleId(),attr);
			return attr;
		}
		//从ssdb取
		return this.asyncPvpStorage.getAsyncPvpRoleAttr(roleId);
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
	public Map<String, String> randomRoleBattleScores(String key,
			String startScore, String endScore, int limit) {
		return this.asyncPvpStorage.randomRoleBattleScores(key, startScore,endScore, limit);
	}

	@Override
	public int getRoleBattleScoreRanking(String roleId) {
		return asyncPvpStorage.getRoleBattleScoreRanking(roleId);
	}
	
}
