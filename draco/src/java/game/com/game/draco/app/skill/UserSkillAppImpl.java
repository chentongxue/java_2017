package com.game.draco.app.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.SkillApplyResult;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.dao.BaseDAO;
import sacred.alliance.magic.domain.RoleLevelup;
import sacred.alliance.magic.module.cache.Cache;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.domain.RoleHero;
import com.game.draco.app.horse.domain.RoleHorse;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillSourceType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.func.PetSkillLearnFunc;
import com.game.draco.app.skill.func.HeroSkillLearnFunc;
import com.game.draco.app.skill.func.HorseSkillLearnFunc;
import com.game.draco.app.skill.func.RoleSkillLearnFunc;
import com.game.draco.app.skill.func.SkillLearnFunc;
import com.game.draco.app.skill.vo.Skill;
import com.game.draco.app.skill.vo.SkillContext;
import com.game.draco.message.item.RoleSkillItem;

public class UserSkillAppImpl implements UserSkillApp {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private SkillApp skillApp;
	private BaseDAO baseDAO;
	private Cache<String, Long> skillProcessCache;//KEY=roleId_skillId,VALUE=lastProcessTime
	private Map<SkillSourceType,SkillLearnFunc> learnFuncMap = new HashMap<SkillSourceType,SkillLearnFunc>();
	
	public UserSkillAppImpl(){
		this.register(new RoleSkillLearnFunc());
		this.register(new HeroSkillLearnFunc());
		this.register(new PetSkillLearnFunc());
		this.register(new HorseSkillLearnFunc());
	}
	
	private void register(SkillLearnFunc skillLearn){
		this.learnFuncMap.put(skillLearn.getSkillSourceType(), skillLearn);
	}
	
	public void setBaseDAO(BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}
	
	@Override
	public List<RoleSkillItem> getRoleSkillItemList(RoleInstance role){
		List<RoleSkillItem> skillItemes = new ArrayList<RoleSkillItem>();
		Map<Short,RoleSkillStat> skillMap = role.getSkillMap() ;
		for (RoleSkillStat roleSkill : skillMap.values()) {
			if (null == roleSkill) {
				continue;
			}
			Skill skill = GameContext.getSkillApp().getSkill(roleSkill.getSkillId());
			if (null == skill
					|| skill.getSkillApplyType() != SkillApplyType.active) {
				continue;
			}
			try {
				long lastProcessTime = roleSkill.getLastProcessTime();
				if(0 != lastProcessTime){
					long now = System.currentTimeMillis();
					if (roleSkill.getLastProcessTime() > now) {
						roleSkill.setLastProcessTime(now);
						lastProcessTime = now ;
					}
				}
				RoleSkillItem item = Converter.getRoleSkillItem(role, skill,
						roleSkill.getSkillLevel(), lastProcessTime);
				skillItemes.add(item);
			} catch (Exception e) {
				logger.error("role enter action,skillid="
						+ skill.getSkillId(), e);
			}
		}
		return skillItemes;
	}
	
	public RoleSkillStat getSkillStat(AbstractRole role, short skillId) {
		return role.getSkillStat(skillId);
	}

	public SkillApplyResult useSkill(AbstractRole role, short skillId) {
		return this.useSkill(role, skillId, 0);
	}
	
	public SkillApplyResult deathUseSkill(AbstractRole role, short skillId){
		Skill skill = skillApp.getSkill(skillId);
		SkillContext context = new SkillContext(skill);
		context.setAttacker(role);
		context.setSkillLevel(role.getSkillLevel(skillId));
		context.setClientDelay(0);
		context.setDefender(role.getTarget());
		//必须设置
		context.setMustLive(false);
		return skill.use(context);
	}
	
	public SkillApplyResult useSkill(AbstractRole role, short skillId,int clientDelay){
		Skill skill = this.skillApp.getSkill(skillId);
		return skill.use(role,clientDelay);
	}
	
	@Override
	public SkillApplyResult useSkillCondition(AbstractRole role, short skillId,boolean systemTrigger,boolean judgeUseCond){
		Skill skill = this.skillApp.getSkill(skillId);
		return skill.use(role,0,systemTrigger, judgeUseCond);
	}
	
	@Override
	public SkillApplyResult useSkill(AbstractRole role, short skillId,int clientDelay,
			boolean skillActiveApply){
		Skill skill = skillApp.getSkill(skillId);
		SkillContext context = new SkillContext(skill);
		context.setAttacker(role);
		context.setSkillLevel(role.getSkillEffectLevel(skillId));
		context.setClientDelay(clientDelay);
		context.setDefender(role.getTarget());
		context.setMustLive(false);
		context.setSkillActiveApply(skillActiveApply);
		return skill.use(context);
	}
	
	@Override
	public List<RoleSkillStat> selectRoleSkillList(String roleId) {
		return this.baseDAO.selectList(RoleSkillStat.class, RoleSkillStat.ROLEID, roleId);
	}
	

	public void setSkillApp(SkillApp skillApp) {
		this.skillApp = skillApp;
	}

    @Override
    public void addNpcSkill(NpcInstance npcInstance, short skillId, int skillLevel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	@Override
	public AttriBuffer getAttriBuffer(AbstractRole role) {
		AttriBuffer buffer = AttriBuffer.createAttriBuffer();
		for(RoleSkillStat stat : role.getCurrentSkillMap().values()){
			if(stat.getSkillLevel() <=0){
				continue ;
			}
			short skillId = stat.getSkillId();
			Skill skill = skillApp.getSkill(skillId);
			if(null == skill){
				continue ;
			}
			buffer.append(skill.getAttriBuffer(role));
		}
		return buffer;
	}

	@Override
	public SkillApplyResult bossUseSkill(AbstractRole role, short skillId) {
		return null;
	}
	
	
	/**
	 * 因为角色级别的提升,主动技能的消耗(HP,MP)会发生变化
	 * 需要及时通知客户端
	 * @param role
	 */
	private void notifyActiveSkill(RoleInstance role){
		try {
			int lv = role.getLevel();
			RoleLevelup levelup = GameContext.getAttriApp().getLevelup(lv);
			if(null == levelup){
				//当前级别相对于前一级的消耗没有发生变化
				return ;
			}
			Map<Short, RoleSkillStat> skillMap = role.getSkillMap();
			if (null == skillMap || 0 == skillMap.size()) {
				return;
			}
			// 通知客户端主动技能的耗魔
			Skill.sendSkillUpdateMessage(role, skillMap.keySet().toArray());
		}catch(Exception ex){
			logger.error("notifyActiveSkill error,role level=" + role.getLevel() ,ex);
		}
	}
	
	

	@Override
	public void roleLevelup(AbstractRole role) {
		if(null == role || role.getRoleType() != RoleType.PLAYER){
			return ;
		}
		RoleInstance player = (RoleInstance)role ;
		this.notifyActiveSkill(player);
	}

	@Override
	public int onLogin(RoleInstance role, Object context) {
		try{
			String roleId = role.getRoleId();
			Map<Short,RoleSkillStat> skillMap = role.getSkillMap();
			
			/*List<RoleSkillStat> skillList = this.selectRoleSkillList(role.getRoleId());
			if (!Util.isEmpty(skillList)) {
				long now = System.currentTimeMillis();
				for (RoleSkillStat stat : skillList) {
					if(null == stat){
						continue;
					}
					Skill skill = this.skillApp.getSkill(stat.getSkillId());
					if(null == skill){
						continue ;
					}
					short skillId = skill.getSkillId();
					long lastProcessTime = this.getLastProcessTimeFromCache(roleId, skillId);
					if(lastProcessTime > 0 && lastProcessTime < now){
						stat.setLastProcessTime(lastProcessTime);
					}
					skillMap.put(stat.getSkillId(), stat);
				}
			}*/
			RoleHero onBattleHero = GameContext.getUserHeroApp().getOnBattleRoleHero(roleId);
			if(null != onBattleHero) {
				skillMap.putAll(onBattleHero.getSkillMap());
			}
			RoleHorse roleHorse = GameContext.getRoleHorseApp().getOnBattleRoleHorse(role.getIntRoleId());
			if(roleHorse != null){
				skillMap.putAll(GameContext.getRoleHorseApp().packRoleSkillStat(role.getIntRoleId(),roleHorse.getSkillList()));
			}
			
		}catch(Exception e){
			this.logger.error("skill init by login error:" + e);
			return 0;
		}
		return 1;
	}
	
	@Override
	public int onLogout(RoleInstance role, Object context){
		try {
			Map<Short,RoleSkillStat> skillMap = role.getSkillMap();
			long now = System.currentTimeMillis() ;
			for(RoleSkillStat stat : skillMap.values()){
				if(null == stat){
					continue;
				}
				Skill skill = this.skillApp.getSkill(stat.getSkillId());
				if(null == skill){
					continue ;
				}
				long lastProcessTime = stat.getLastProcessTime();
				long time = now - lastProcessTime;
				if(time > 0 && time < skill.getCd(role)){
					int cdTime = skill.getCd(role);
					if(time < cdTime){
						long liveTime = cdTime - time;
						this.addLastProcessTimeToCache(stat.getRoleId(), skill.getSkillId(), lastProcessTime, liveTime);
					}
				}
			}
		} catch (Exception ex) {
			Log4jManager.OFFLINE_ERROR_LOG.error(
					"save skill to db error: roleId=" + role.getRoleId() + ",userId="
							+ role.getUserId(), ex);
			return 0;
		}
		
		return 1;
	}
	
	@Override
	public int onCleanup(String roleId, Object context) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public long getLastProcessTimeFromCache(String roleId, short skillId){
		String key = this.buildSkillProcessCacheKey(roleId, skillId);
		Long value = this.skillProcessCache.get(key);
		if(null == value){
			return 0;
		}
		return value;
	}
	
	private void addLastProcessTimeToCache(String roleId, short skillId, long lastProcessTime, long liveTime){
		String key = this.buildSkillProcessCacheKey(roleId, skillId);
		this.skillProcessCache.put(key, lastProcessTime, liveTime, TimeUnit.MILLISECONDS);
	}
	
	private String buildSkillProcessCacheKey(String roleId, short skillId){
		return roleId + Cat.underline + skillId;
	}
	
	@Override
	public SkillLearnFunc getSkillLearnFunc(SkillSourceType skillSourceType) {
		return this.learnFuncMap.get(skillSourceType);
	}
	
	public void setSkillProcessCache(Cache<String, Long> skillProcessCache) {
		this.skillProcessCache = skillProcessCache;
	}
	
	
	/**
	 * 包含主角和英雄的技能
	 * @param role
	 * @param level
	 * @return
	 */
	public int getSkillLevelNum(RoleInstance role,int level){
		return this.getPlayerSkillLevel(role, level) 
				+ GameContext.getHeroApp().getSkillLevelNum(role.getRoleId(), level) ;
	}
	
	private int getPlayerSkillLevel(RoleInstance role, int level) {
		Map<Short, RoleSkillStat> map = role.getSkillMap();
		if (Util.isEmpty(map)) {
			return 0;
		}
		int num = 0;
		for (RoleSkillStat stat : map.values()) {
			if (stat.getSkillLevel() < level) {
				continue;
			}
			Skill skill = GameContext.getSkillApp().getSkill(stat.getSkillId());
			if (null == skill) {
				continue;
			}
			if (skill.getSkillSourceType() != SkillSourceType.Role) {
				continue;
			}
			num++;
		}
		return num;
	}
}
