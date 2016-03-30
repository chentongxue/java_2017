package com.game.draco.app.hero.arena;

import java.util.List;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.hero.arena.config.HeroArenaBaseConfig;
import com.game.draco.app.hero.arena.domain.RoleHeroArenaRecord;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.message.response.C1270_HeroArenaPanelRespMessage;

public interface HeroArenaApp extends Service {
	
	void login(RoleInstance role);
	
	void logout(RoleInstance role);
	
	C1270_HeroArenaPanelRespMessage getHeroArenaPanelMessage(RoleInstance role);
	
	int getCurrGateId(RoleInstance role);
	
	int getMaxGateId();
	
	/** 匹配对战关卡的角色 **/
	List<String> matchRivalRoles(String roleId);
	
	/** 匹配对战英雄 **/
	List<RoleHero> matchFightRoleHeros(String fightRoleId);
	
	/** 获取对战英雄列表 **/
	List<RoleHero> getFightHeroList(RoleInstance role);
	
	/** 选择自己的对战英雄 **/
	Result selectHeros(RoleInstance role, int[] selectHeros);
	
	Result fighting(RoleInstance role);
	
	HeroArenaBaseConfig getHeroArenaBaseConfig();
	
	RoleHeroArenaRecord getRoleHeroArenaRecord(String roleId);
	
	void fightDeath(RoleInstance role);
	
	void gameOver(RoleInstance role, int gateId, byte[] pkResults, String rivalRoleName);
	
}
