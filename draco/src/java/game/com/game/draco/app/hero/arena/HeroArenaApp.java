package com.game.draco.app.hero.arena;

import java.util.List;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.Service;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.app.AppSupport;
import com.game.draco.app.hero.arena.config.HeroArenaGateConfig;
import com.game.draco.app.hero.arena.domain.RoleHeroArenaRecord;
import com.game.draco.app.hero.arena.vo.HeroRewardResult;
import com.game.draco.app.hero.domain.RoleHero;

public interface HeroArenaApp extends Service, AppSupport {
	
	public Message getHeroArenaPanelMessage(RoleInstance role);
	
	public int getCurrGateId(RoleInstance role);
	
	/** 匹配对战关卡的角色 **/
	public List<String> matchRivalRoles(String roleId);
	
	/** 匹配对战英雄 **/
	public List<RoleHero> matchFightRoleHeros(String fightRoleId);
	
	/** 获取对战英雄列表 **/
	public List<RoleHero> getFightHeroList(RoleInstance role);
	
	/** 选择自己的对战英雄 **/
	public Result selectHeros(RoleInstance role, int[] selectHeros);
	
	public Result fighting(RoleInstance role);
	
	public HeroArenaGateConfig getHeroArenaGateConfig(int gateId);
	
	public RoleHeroArenaRecord getRoleHeroArenaRecord(int roleId);
	
	public void fightDeath(RoleInstance role);
	
	public void gameOver(RoleInstance role, int gateId, HeroFightStatus fightStatus, String rivalRoleName);
	
	public Result heroReborn(RoleInstance role);
	
	public boolean isHeroDead(RoleInstance role, int heroId);
	
	public HeroRewardResult reward(RoleInstance role);
	
	Result resetHeroArena(RoleInstance role);

	boolean isPlay(RoleInstance role);
	
}
