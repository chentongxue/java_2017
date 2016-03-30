package com.game.draco.app.npc;

import java.security.SecureRandom;
import java.util.Date;

import sacred.alliance.magic.app.ai.Ai;
import sacred.alliance.magic.app.map.data.NpcBorn;
import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.component.id.IdFactory;
import sacred.alliance.magic.component.id.IdType;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.NpcInstanceBehavior;
import sacred.alliance.magic.vo.Point;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.app.skill.vo.RoleCopyBehavior;
import com.game.draco.app.skill.vo.Skill;

public class NpcInstanceFactroy {
	
	private static SecureRandom random = new SecureRandom();
	/***
	 * NPC重生
	 * @param npcInstance
	 * @param born
	 * @return
	 */
	public static NpcInstance rebirthNpcInstance(NpcInstance npcInstance,NpcBorn born){
		try {
			npcInstance.setRoleId(IdFactory.getInstance().nextId(IdType.NPCID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		npcInstance.setCreateTime(new Date());
		npcInstance.setDieTime(null);
        //一定要设置,否则重新刷新的怪物死亡后不会通知客户端
//        npcInstance.getHasSendDeathMsg().compareAndSet(false, true);
		npcInstance.getHasSendDeathMsg().compareAndSet(true, false);
		//清除NPC身上技能,buff(!!!)
        npcInstance.getSkillMap().clear();
        npcInstance.delAllBuffStat();
        npcInstance.getHatredTarget().clearHatredMap();
        npcInstance.setOwnerInstance(null);
        npcInstance.setTarget(null);
        
		int bornX = Util.randomInRange(born.getBornmapgxbegin(), born.getBornmapgxend()-born.getBornmapgxbegin());
		int bornY = Util.randomInRange(born.getBornmapgybegin(), born.getBornmapgyend()-born.getBornmapgybegin());

		String mapId = npcInstance.getMapId();
		npcInstance.setRebornPoint(new Point(mapId, bornX, bornY));
		npcInstance.setMapId(mapId);	
		npcInstance.setMapX(bornX);
		npcInstance.setMapY(bornY);
		npcInstance.setDir(getNpcBornDir(born.getBornNpcDir()).getType());
		//npcInstance.setPal(born.getPal());
		//npcInstance.setMmp(born.getMmp());
		//调整NPC属性
		adjustNpcProp(npcInstance);
		
		//构建用户行为
		NpcInstanceFactroy.createNpcBehavior(npcInstance);
		return npcInstance ;
	}
	
	public static NpcInstance createMapBaffle(NpcTemplate npc, String mapId, NpcBorn born){
		NpcInstance npcInstance = new NpcInstance();
		try {
			npcInstance.setRoleId(IdFactory.getInstance().nextId(IdType.NPCID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		npcInstance.setNpc(npc);
		npcInstance.setNpcid(npc.getNpcid());
		//npcInstance.setNpcname(npc.getNpcname());
		npcInstance.setResid(npc.getResid());
		int bornX = Util.randomInRange(born.getBornmapgxbegin(), born.getBornmapgxend()-born.getBornmapgxbegin());
		int bornY = Util.randomInRange(born.getBornmapgybegin(), born.getBornmapgyend()-born.getBornmapgybegin());
		npcInstance.setMapId(mapId);	
		npcInstance.setMapX(bornX);
		npcInstance.setMapY(bornY);
		return npcInstance;
	}
	
	public static NpcInstance createNpcInstance(NpcTemplate npc, String mapId, NpcBorn born){
		NpcInstance npcInstance = new NpcInstance();
		
		try {
			npcInstance.setRoleId(IdFactory.getInstance().nextId(IdType.NPCID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		npcInstance.setNpc(npc);
		
		npcInstance.setNpcid(npc.getNpcid());
		
		//npcInstance.setNpcname(npc.getNpcname());
		
		/**性别*/
		//npcInstance.setSex(GenderType.getType((byte) 0).getType());
		
		/**NPC行为类型*/
		//npcInstance.setNpcActionType(NpcActionType.getType(npc.getActionId()));
		//npcInstance.setForce(ForceType.getType(npc.getForce()).getValue());
		
		//npc不能调用下面语句
		//npcInstance.setRace(RaceType.getType(npc.getRace()).getType());
		//npcInstance.setRace(npc.getRace());
		//npcInstance.setCareer(CareerType.getType(npc.getCareer()).getType());

        npcInstance.setLevel(npc.getLevel());
		
		int bornX = Util.randomInRange(born.getBornmapgxbegin(), born.getBornmapgxend()-born.getBornmapgxbegin());
		int bornY = Util.randomInRange(born.getBornmapgybegin(), born.getBornmapgyend()-born.getBornmapgybegin());

		npcInstance.setRebornPoint(new Point(mapId, bornX, bornY));
		npcInstance.setMapId(mapId);	
		npcInstance.setMapX(bornX);
		npcInstance.setMapY(bornY);
		npcInstance.setDir(getNpcBornDir(born.getBornNpcDir()).getType());
		npcInstance.setLevel(npc.getLevel());
		npcInstance.setCreateTime(new Date());
		//npcInstance.setPal(born.getPal());
		//npcInstance.setMmp(born.getMmp());
		npcInstance.setOwnerInstance(null);
		npcInstance.setTarget(null);
		//npcInstance.setHeadIconResId(npc.getHeadIconResId());
		//调整NPC属性
		adjustNpcProp(npcInstance);
		//构建用户行为
		NpcInstanceFactroy.createNpcBehavior(npcInstance);
		return npcInstance;
	}
	
	public static NpcInstance createNpcInstance(String roleId, NpcTemplate npc, 
			String mapId,int bornX,int bornY){
		NpcInstance npcInstance = new NpcInstance();
		
		try {
			npcInstance.setRoleId(IdFactory.getInstance().nextId(IdType.NPCID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		npcInstance.setNpc(npc);
		
		npcInstance.setNpcid(npc.getNpcid());
		
		//npcInstance.setNpcname(npc.getNpcname());

        npcInstance.setLevel(npc.getLevel());
        
		npcInstance.setRebornPoint(new Point(mapId, bornX, bornY));
		npcInstance.setMapId(mapId);	
		npcInstance.setMapX(bornX);
		npcInstance.setMapY(bornY);
		npcInstance.setDir(getNpcBornDir(0).getType());
		npcInstance.setLevel(npc.getLevel());
		npcInstance.setCreateTime(new Date());
		npcInstance.setOwnerInstance(null);
		npcInstance.setTarget(null);
		npcInstance.setSummonRoleId(roleId);
		//调整NPC属性
		adjustNpcProp(npcInstance);
		//构建用户行为
		NpcInstanceFactroy.createNpcBehavior(npcInstance);
		return npcInstance;
	}
	
	private static Direction getNpcBornDir(int dir){
		//"随机","右","下","左","上"
		//int dir = born.getBornNpcDir();
		if(dir > 4 || dir <=0){
			//随机[1-4]
			dir = random.nextInt(4) + 1 ;
		}
		switch(dir){
		case 1:
			return Direction.RIGHT;
		case 2:
			return Direction.DOWN ;
		case 3:
			return Direction.LEFT ;
		case 4:
			return Direction.UP ;
		}
		return Direction.DOWN ;
	}
	
	private static void adjustNpcProp(NpcInstance npcInstance){
		//根据等级,职业,等级,势力计算基本属性
		GameContext.getUserAttributeApp().reCalct(npcInstance);
        //！！！！ 需要设置否则怪物将是0 hp
		if(0 == npcInstance.getCurHP()){
			npcInstance.setCurHP(npcInstance.getMaxHP());
		}
	}
	
	public  static void createNpcBehavior(NpcInstance npcInstance){
		/**NPC行为*/
		npcInstance.setBehavior(new NpcInstanceBehavior(npcInstance));
		/**NPC AI*/
		Ai ai = GameContext.getAiApp().getAi(npcInstance);
		npcInstance.setAi(ai);
		//初始属性通知状态
		npcInstance.getBehavior().resetCurrentAttributeStatus();
	}
	
	public static NpcInstance createAsyncPvpNpcInstance(AsyncPvpRoleAttr npc, String mapId, int mapX, int mapY){
		NpcInstance npcInstance = new NpcInstance();
		
		try {
			npcInstance.setRoleId(IdFactory.getInstance().nextId(IdType.NPCID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		npcInstance.setNpc(npc);
		
		npcInstance.setNpcid(npc.getNpcid());
		
		//npcInstance.setNpcname(npc.getRoleName());
		
		/**性别*/
		//npcInstance.setSex(GenderType.getType((byte) 0).getType());
		
        npcInstance.setLevel(npc.getLevel());
		
		npcInstance.setRebornPoint(new Point(mapId, mapX, mapY));
		npcInstance.setMapId(mapId);	
		npcInstance.setMapX(mapX);
		npcInstance.setMapY(mapY);
		npcInstance.setDir(Direction.DOWN.getType());
		npcInstance.setLevel(npc.getLevel());
		npcInstance.setSpeed(npc.getSpeed());
		npcInstance.setCreateTime(new Date());
		npcInstance.setOwnerInstance(null);
		npcInstance.setTarget(null);
		//调整NPC属性
		adjustNpcProp(npcInstance);
		//构建用户行为
		NpcInstanceFactroy.createAsyncPvpNpcBehavior(npcInstance, npc);
		return npcInstance;
	}
	
	public static void createAsyncPvpNpcBehavior(NpcInstance npcInstance, AsyncPvpRoleAttr npc){
		/**NPC行为*/
		npcInstance.setBehavior(new NpcInstanceBehavior(npcInstance));
		/**NPC AI*/
		Ai ai = GameContext.getAiApp().getAsyncPvpAi(npcInstance, npc);
		npcInstance.setAi(ai);
		ai.justRespawned();
		//初始属性通知状态
		npcInstance.getBehavior().resetCurrentAttributeStatus();
	}
	
	public static NpcInstance createRoleCopyNpcInstance(NpcTemplate npc, 
			String mapId, int mapX, int mapY, short skillId, int skillLv){
		NpcInstance npcInstance = new NpcInstance(RoleType.COPY);
		
		try {
			npcInstance.setRoleId(IdFactory.getInstance().nextId(IdType.NPCID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		npcInstance.setNpc(npc);
		
		npcInstance.setNpcid(npc.getNpcid());
		
		//npcInstance.setNpcname(npc.getNpcname());
		
        npcInstance.setLevel(npc.getLevel());
		
		npcInstance.setRebornPoint(new Point(mapId, mapX, mapY));
		npcInstance.setMapId(mapId);	
		npcInstance.setMapX(mapX);
		npcInstance.setMapY(mapY);
		npcInstance.setDir(Direction.DOWN.getType());
		npcInstance.setLevel(npc.getLevel());
		npcInstance.setSpeed(npc.getSpeed());
		npcInstance.setCreateTime(new Date());
		npcInstance.setOwnerInstance(null);
		npcInstance.setTarget(null);
		//设置npc消失时间
		int lifeTime = npc.getLifeTime();
		if(lifeTime > 0) {
			npcInstance.setDisappearTime(System.currentTimeMillis() + lifeTime);
		}
		//调整NPC属性
		adjustNpcProp(npcInstance);
		//构建用户行为
		NpcInstanceFactroy.createRoleCopyNpcBehavior(npcInstance, skillId, skillLv);
		return npcInstance;
	}
	
	public static void createRoleCopyNpcBehavior(NpcInstance npcInstance, short skillId, int skillLv){
		/**NPC行为*/
		npcInstance.setBehavior(new RoleCopyBehavior(npcInstance));
		/**NPC AI*/
		Ai ai = GameContext.getAiApp().getAi(npcInstance);
		npcInstance.setAi(ai);
		/** 添加技能 */
		npcInstance.getSkillMap().clear();
		Skill skill = GameContext.getSkillApp().getSkill(skillId);
		if (null != skill) {
			RoleSkillStat stat = new RoleSkillStat();
			stat.setSkillId(skillId);
			stat.setSkillLevel(skillLv);
			npcInstance.getSkillMap().put(skillId, stat);
		}
		//初始属性通知状态
		npcInstance.getBehavior().resetCurrentAttributeStatus();
	}
	
	
}
