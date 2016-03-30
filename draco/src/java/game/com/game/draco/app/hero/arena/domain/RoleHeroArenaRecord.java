package com.game.draco.app.hero.arena.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;

import org.python.google.common.collect.Maps;

import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;

import com.alibaba.fastjson.annotation.JSONField;
import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;

@Data
public class RoleHeroArenaRecord {
	
	private String roleId;//角色ID
	
	private int[] selectHeros = new int[3];//已选上阵英雄列表
	private Set<Integer> dieHero = new HashSet<Integer>();//死亡英雄列表
	private int index;//当前对手索引[从0开始]
	private List<String> rivals = new ArrayList<String>();//对手列表
	private Date updateTime;//更新时间
	private byte state;//状态 1通关 2已领奖
	private short resetNum;//已用重置次数
	private Map<Integer,Short> heroMap = Maps.newHashMap();//自己英雄 
	private Map<Integer,Short> targetHeroMap = Maps.newHashMap();//对方英雄
	
	@JSONField(serialize=false)
	private boolean modified = false;//是否修改过
	@JSONField(serialize=false)
	private List<RoleHero> fightHeros = new ArrayList<RoleHero>();//对战英雄
	
	//重置时间
//	private Date resetTime;
	
	private void reset(){
		Date now = new Date();
		if(DateUtil.sameDay(this.updateTime, now)){
			return;
		}
//		this.dieHero = new HashSet<Integer>();
//		this.index = 0;
		this.resetNum = 0 ;
//		this.rivals = GameContext.getHeroArenaApp().matchRivalRoles(this.roleId);
		this.updateTime = now;
		this.modified = true;
//		heroMap.clear();
//		targetHeroMap.clear();
	}
	
	public void resetHeroArena(){
		dieHero.clear();
		rivals.clear();
		heroMap.clear();
		targetHeroMap.clear();
		this.dieHero = new HashSet<Integer>();
		this.index = 0;
		this.rivals = GameContext.getHeroArenaApp().matchRivalRoles(this.roleId);
		this.modified = true;
	}
	
	@JSONField(serialize=false)
	private int getFightIndex(){
		this.reset();
		return this.index;
	}
	
	@JSONField(serialize=false)
	public int getFightGateId(){
		//关卡ID比index大1
		return this.getFightIndex() + 1;
	}
	
	@JSONField(serialize=false)
	public String getFightRoleId(){
		//对手为空则重新匹配
		if(Util.isEmpty(this.rivals)){
			this.rivals = GameContext.getHeroArenaApp().matchRivalRoles(this.roleId);
			this.modified = true;
		}
		int fightIndex = this.getFightIndex();
		if(fightIndex >= this.rivals.size()){
			return null;
		}
		return this.rivals.get(fightIndex);
	}
	
	public boolean isHeroDead(int heroId){
		return null != this.dieHero && this.dieHero.contains(heroId);
	}
	
	@JSONField(serialize=false)
	public List<RoleHero> getFightHeroList(){
		String fightRoleId = this.getFightRoleId();
		////对手角色Id不存在，清空对手英雄列表
		if(Util.isEmpty(fightRoleId)){
			this.fightHeros.clear();
			return this.fightHeros;
		}
		boolean needMatch = false;
		//容错：判断对手英雄和对手角色ID是否一致，不一致则重新匹配英雄
		if(!Util.isEmpty(this.fightHeros)){
			RoleHero rh = this.fightHeros.get(0);
			if(null == rh || !rh.getRoleId().equals(fightRoleId)){
				this.fightHeros.clear();
				needMatch = true;
			}
		}else{
			needMatch = true;
		}
		if(needMatch){
			List<RoleHero> list = GameContext.getHeroArenaApp().matchFightRoleHeros(fightRoleId);
			if(!Util.isEmpty(list)){
				this.fightHeros = list;
				this.modified = true;
			}
		}
		
		return this.fightHeros;
	}
	
	public void victoryUpdate(){
		this.index ++;//对手索引增加
		this.fightHeros.clear();
		this.updateTime = new Date();
		this.modified = true;
		targetHeroMap.clear();
	}
	
	public boolean hasDieHero(){
		return !Util.isEmpty(this.dieHero);
	}
	
	public void rebornDieHeros(){
		this.dieHero = new HashSet<Integer>();
	}
	
}
