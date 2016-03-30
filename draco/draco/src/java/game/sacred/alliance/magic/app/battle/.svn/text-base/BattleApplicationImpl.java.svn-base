package sacred.alliance.magic.app.battle;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.goddess.domain.RoleGoddess;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.quest.UserQuestApp;

public class BattleApplicationImpl implements BattleApplication {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private UserQuestApp userQuestApp;
	
	private void changeHpMp(AbstractRole target,float hpChange,float mpChange){
		if(0 != hpChange || 0 != mpChange){
			AttriBuffer buffer = AttriBuffer.createAttriBuffer();
			buffer.append(AttributeType.curHP, hpChange, 0);
			buffer.append(AttributeType.curMP, mpChange, 0);
			GameContext.getUserAttributeApp().changeAttribute(target, buffer);
			target.getBehavior().notifyAttribute();
		}
	}
	
	private boolean samePlayer(AbstractRole r1 ,AbstractRole r2){
		if(null == r1 || null == r2){
			return false ;
		}
		return r1.getRoleId().equals(r2.getRoleId())
			&& r1.getRoleType() == RoleType.PLAYER;
	}
	
	public void attack(AbstractRole attacker, AbstractRole victim, int hp,
			int mp, int hatred) {
		if (null != attacker && attacker.getRoleType() == RoleType.GODDESS) {
			// 处理女神情况
			attacker = ((RoleGoddess) attacker).getRole();
		}
		// 先加入仇恨列表，再改变属性。这样属性变化才会通知给仇恨列表的玩家。
		// 先改变属性，然后才判断怪物死亡否。
		// 最后通知AI，改变怪物的行为
		if (victim.getForceRelation(attacker) == ForceRelation.enemy) {
			victim.getHatredTarget().addHatred(attacker, hatred);
		}
		if (hp > 0) {
			// 治疗比例系数(已经在外面计算)
			this.changeHpMp(victim, hp, mp);
			return;
		}
		if(this.samePlayer(attacker, victim)){
			//!!!!!!! 避免自己伤害自己，直接退出
			return ;
		}
		this.changeHpMp(victim, hp, mp);
		// 攻击者对受害者造成了多少伤害
		MapInstance mapInstance = victim.getMapInstance();
		if (null != mapInstance) {
			mapInstance.damageTaken(attacker, victim, -hp);
		}

		if (victim.isDeath()) {
			try {
				this.killedRole(attacker, victim);
			} catch (ServiceException e) {
				logger.error("", e);
			}
			return;
		}
		if (victim.getRoleType() == RoleType.NPC) {
			victim.getAi().attackStart(attacker, hatred);
		}
	}
	
	
	
	private void killedNpc(AbstractRole attacker, NpcInstance victim) {
		// 给添加经验
		AbstractRole owner = victim.getOwnerInstance();
		if (null == owner
				|| null == owner.getMapInstance()
				|| !owner.getMapInstance().getInstanceId().equals(
						victim.getMapInstance().getInstanceId())) {
			// 所有者已经不在此地图,需要更换所有者

			// killedRole函数里面必须考虑秒杀情况否则
			// 下面这句必须放前面,
			// 不然秒杀怪物的时候,怪物将没有所有者,杀怪物人将无法获得相关奖励(经验等)
			// !!! 此判断一定要有,否则秒杀怪物的时候,将无法获得相关奖励
			owner = attacker;
		}
		if (null == owner) {
			victim.getBehavior().death(owner);
			return;
		}
		// 杀死怪完成任务
		try {
			//必须在死亡方法前调用，因为死亡方法会清除仇恨
			userQuestApp.killMonster(owner, victim);
		} catch (Exception e) {
			this.logger.error("", e);
		}
		
		victim.getBehavior().death(owner);
		if(owner.getRoleType() != RoleType.PLAYER){
			return ;
		}
		RoleInstance ownerInstance = (RoleInstance) owner;
		try {
			ownerInstance.getBehavior().notifyBattleAttrIncome(victim);
			if (!victim.isSummon()) {
				// NPC死亡掉落
				GameContext.getFallApp().fallBox((NpcInstance) victim,
						ownerInstance, OutputConsumeType.monster_fall);
			} else {
				// 如果是召唤的怪死亡 掉落规则不一样
				GameContext.getSummonApp().summonDeath(victim, ownerInstance);
			}
		} catch (Exception ex) {
			logger.error("", ex);
		} 
	}
	
	private void killedPlayer(AbstractRole attacker, RoleInstance victim){
    	victim.getBehavior().death(attacker);
    	//称号广播
    	GameContext.getTitleApp().killTitleBroadcast(attacker,victim);
    
    	if(null != attacker && attacker.getRoleType() == RoleType.PLAYER){
    		//杀死玩家完成任务
        	try{
        		userQuestApp.killRole((RoleInstance)attacker,victim);
        	}catch(Exception e){
        		this.logger.error("", e);
        	}
    	}
    	//死亡触发任务
		try {
			GameContext.getUserQuestApp().death(victim);
		} catch (ServiceException e) {
			logger.error("" + e);
		}
    	//死亡日志
    	GameContext.getStatLogApp().roleDeathLog(victim, attacker);
	}
	
	/**
	 * 击杀
	 * @param attacker
	 *            攻击者
	 * @param victim
	 *            承受者(死亡者)
	 */
	public void killedRole(AbstractRole attacker, AbstractRole victim) throws ServiceException {
		if (victim.getHasSendDeathMsg().get()) {
            return;
        }
        victim.getHasSendDeathMsg().compareAndSet(false, true);
        if (victim.getRoleType() == RoleType.NPC) {
        	this.killedNpc(attacker, (NpcInstance)victim);
        	return ;
        }
        if (victim.getRoleType() == RoleType.PLAYER) {
        	this.killedPlayer(attacker, (RoleInstance)victim);
        }
	}


	public void setUserQuestApp(UserQuestApp userQuestApp) {
		this.userQuestApp = userQuestApp;
	}
	
}
