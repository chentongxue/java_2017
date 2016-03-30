package sacred.alliance.magic.vo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.ai.Ai;
import sacred.alliance.magic.app.ai.MessageType;
import sacred.alliance.magic.app.ai.Telegram;
import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.base.ForceRelation;
import sacred.alliance.magic.constant.LoopConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.scheduler.job.LoopCount;
import sacred.alliance.magic.util.Converter;
import sacred.alliance.magic.util.SortedValueNumMap;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.buff.BuffLostType;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.npc.refresh.NpcRefreshRule;
import com.game.draco.app.npc.refresh.NpcRefreshTask;
import com.game.draco.app.npc.type.NpcActionType;
import com.game.draco.app.skill.vo.SkillContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C0401_ForceTypeNotifyMessage;
import com.game.draco.message.response.C0204_MapUserEntryNoticeRespMessage;
import com.game.draco.message.response.C0219_NpcWalkStopRespMessage;

public class NpcInstanceBehavior extends AbstractRoleBehavior<NpcInstance> {
	protected final static Logger logger = LoggerFactory.getLogger(NpcInstanceBehavior.class);
	private NpcInstance role;
	private LoopCount defaultLoopCount = new LoopCount(LoopConstant.NPC_DEFAULT_CYCLE);
	
	
	public NpcInstanceBehavior(NpcInstance t) {
		super(t);
		this.role = t;
	}

	@Override
	public void death(AbstractRole attacker) {
        try{
        	Ai ai = role.getAi();
            if(null != ai){
    			ai.justDied();
    			ai.npcDiedEncouragement();
    		}
            //!!!!! 最后清除仇恨,否则 npcDeathDiversity 中无法获得仇恨
        	//role.getHatredTarget().clearHatredMap();
        	//清除buff
        	role.delAllBuffStat(BuffLostType.dieLost);
        	//重置改变外形状态
        	this.role.setChangeShape(false);
        }catch(Exception ex){
        	logger.error("",ex);
        }
		MapInstance mapInstance = role.getMapInstance();
		if (null != mapInstance) {
			if (mapInstance.npcCanPk()) {
				// 通知NPC AI
				mapInstance.getMessageDispatcher().dispatch(
						new Telegram(role, null, MessageType.JUSTDIE, 0, role
								.getRoleId()));
			}
			mapInstance.npcDeath(role);
			mapInstance.npcDeathDiversity(attacker, role);
			// npc死亡后刷新逻辑
			GameContext.getNpcRefreshApp().npcDeathRefreshPross(role, attacker);
		}
		//最后清除仇恨,否则 npcDeathDiversity 中无法获得仇恨
		//清仇恨列表
    	role.getHatredTarget().clearHatredMap();
	}

    
    
	@Override
	public void update() {
		if(null != role.getAi()){
			role.getAi().updateAI();
		}
		if(!this.defaultLoopCount.isReachCycle()){
			return ;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
		}
		//NPC消失
        long disappearTime = role.getDisappearTime();
        if(disappearTime > 0 && System.currentTimeMillis() > disappearTime){
        	MapInstance mapInstance = role.getMapInstance();
   	     	if(null != mapInstance){
   	     		mapInstance.removeAbstractRole(role);
   	     		mapInstance.notifyNpcDeath(role);
   	     	}
        	return ;
        }
        
		long timeDiff = role.getMapInstance().getWorldTime().getTimeDiff();
		long now = System.currentTimeMillis();
		context.getUserBuffApp().runBuff(role, timeDiff);
		
		//改变怪物所有者
		changeOwner(now);
		
		 //npc刷新设置
		 NpcRefreshTask refreshTask= role.getNpcRefreshTask();
		 if(refreshTask != null){
			 NpcRefreshRule rule = refreshTask.getNpcRefreshRule();
			 if(rule != null) {
				 //到点消失逻辑
				 rule.npcDisappear(role);
				 //循环喊话逻辑
				 rule.loopSpeak(role);
			 }
		 }
		 
		
        // 获得自己视野范围内的敌人
        if(!role.getAi().isActiveAttack()){
            return ;
        }
        
        for(RoleInstance currentRole : role.getMapInstance().getRoleList()){
        	if(currentRole.isDeath()){
        		continue ;
        	}
            if(!role.getAi().isInView(currentRole) ||
                   ForceRelation.enemy != role.getForceRelation(currentRole)){
                continue ;
            }
          role.getMapInstance().getMessageDispatcher().dispatch(new Telegram(currentRole,role,MessageType.INVIEW));
        }
        MapInstance mapInstance = role.getMapInstance();
        
        if(null == mapInstance){
        	return ;
        }
        
        if(!mapInstance.npcCanPk()) {
        	return ;
        }
        //只有配置了NPC需要PK的地图才执行下面逻辑
        for(NpcInstance currentNpc : role.getMapInstance().getNpcList()){
        	if(currentNpc.isDeath()){
        		continue ;
        	}
            if(!role.getAi().isInView(currentNpc) ||
                    ForceRelation.enemy != role.getForceRelation(currentNpc)){
                continue ;
            }
          role.getMapInstance().getMessageDispatcher().dispatch(new Telegram(currentNpc,role,MessageType.INVIEW));
        }
	}

	@Override
	public void stopMove() {
		/*if(role.getMovementFlag() != MovementFlagType.MOVEMENTFLAG_STAND){
			role.setMovementFlag(MovementFlagType.MOVEMENTFLAG_STAND);
			WalkSynchRespMessage resp = new WalkSynchRespMessage();
			resp.setRoldId(role.getRoleId());
			resp.setMapx(role.getMapX());
			resp.setMapy(role.getMapY());
			resp.setDir((byte)role.getDir().getType());
			//TODO: 
			resp.setMomentFlag((byte)role.getMovementFlag().getType()(byte)Momentflag.NORMAL.getType());
			resp.setMoveOrStop(MoveOrStopConstant.STOP);
			resp.setSendTime(System.currentTimeMillis());
			role.getMapInstance().broadcastMap(role, resp);
		}*/
		
		C0219_NpcWalkStopRespMessage msg = new C0219_NpcWalkStopRespMessage();
		msg.setNpcId(role.getIntRoleId());
		msg.setX((short)role.getMapX());
		msg.setY((short)role.getMapY());
		role.getMapInstance().broadcastScreenMap(role, msg);
	}

	@Override
	public boolean canMove() {
		if(role.getNpcActionType()==NpcActionType.ROOT){
			return false;
		}
		return super.canMove();
	}
	

	@Override
	public void enterMap() throws ServiceException {
		context.getUserMapApp().enter(role);
		MapInstance instance = role.getMapInstance();
		if(null == instance || !instance.isNormalLive(role)){
			return ;
		}
        for (RoleInstance ri : instance.getRoleList()) {
			C0204_MapUserEntryNoticeRespMessage message = new C0204_MapUserEntryNoticeRespMessage();
			message.setItem(Converter.getNpcBodyItem(role, ri));
			context.getMessageCenter().send("", ri.getUserId(), message);
		}

//		MapUserEntryNoticeRespMessage pushMsg = new MapUserEntryNoticeRespMessage();
//		NpcBodyItem item = new NpcBodyItem();
//		item.setRoleId(role.getRoleId());
//		item.setNpcname(role.getNpcName());
//		item.setMapx(role.getMapX());
//		item.setMapy(role.getMapY());
//		item.setLevel((byte)role.getLevel());
//		//item.setBodyAvatar(Integer.parseInt(role.getAvatarId()));
//		item.setRace(role.getRace().getType());
//		pushMsg.setItem(item);
//		instance.broadcastMap(role, pushMsg);
	}

	@Override
	public void changeMap(Point targetPoint) throws ServiceException {
		//怪物不需要此接口。直接调用enterMapInstance即可
	}

	@Override
	public void notifyForceType() {
		if(null == role /*|| null == ForceType.getType(role.getForce())*/){
			return ;
		}
		MapInstance  map = role.getMapInstance();
		if(null == map){
			return ;
		}
		//通知地图内用户
		for(RoleInstance current : map.getRoleList()){
			C0401_ForceTypeNotifyMessage pushMsg = new C0401_ForceTypeNotifyMessage();
			pushMsg.setRoleId(role.getIntRoleId());
			//TODO:
			/*pushMsg.setForceRelation((byte)ForceRelationAdaptor.getForceRelation(role.getForce(),
					current.getForce()).getType());*/
			context.getMessageCenter().send("", current.getUserId(), pushMsg);
		}
		/**
		 * 服务器给客户端推这个npc头顶的任务
		 */
		context.getUserQuestApp().notifyQuestNpcHeadSign(role);
	}

	public void changeOwner(long now){
		if (role.getAttackedTime() > 0 && now - role.getAttackedTime() > GameContext.getSkillConfig().getChangeOwerTime()) {
			AbstractRole owner = role.getOwnerInstance();
			if(null != owner) {
				AbstractRole ownerRole = role.getMapInstance().getAbstractRole(owner.getRoleId());
				if(null != ownerRole) {
					role.setAttackedTime(System.currentTimeMillis());
					return ;
				}
			}
			String firstHateNameId = role.getHatredTarget()
					.getFirstHateTarget();
			AbstractRole firstHateRole = null;
			if (null != firstHateNameId) {
				firstHateRole = role.getMapInstance().getAbstractRole(
						firstHateNameId);
			}
			role.setOwnerInstance(firstHateRole);
			role.setAttackedTime(System.currentTimeMillis());
		}
	}

	@Override
	public void exitMap() throws ServiceException {
		
	}

	

	@Override
	public void notifyBattleAttrIncome(NpcInstance dieNpc) {
		
	}


	@Override
	public void notifyPosition(Message resp) throws ServiceException {
		
	}


	@Override
	public void notifyNpcMsg(String content) {
		if(Util.isEmpty(content)){
			return ;
		}
		
		HatredTarget hatredTarget = this.role.getHatredTarget();
		if(null == hatredTarget) {
			return ;
		}
		
		SortedValueNumMap hatredMap = hatredTarget.getHatredMap();
		if(null == hatredMap || hatredMap.size() <= 0) {
			return ;
		}
		for (Object hatredRoleId : hatredMap.keySet()) {
			String roleId = hatredRoleId.toString();
			RoleInstance itemRole = this.role.getMapInstance().getRoleInstance(roleId);
			if (null == itemRole) {
				continue;
			}
			sendNotifyNpcMsg(content, itemRole.getUserId());
		}
	}
	
	public void sendNotifyNpcMsg(String content,String userId) {
		C0003_TipNotifyMessage tipNotifyMessage = new C0003_TipNotifyMessage();
		String msg = role.getNpcname() + ":" + content;
		tipNotifyMessage.setMsgContext(msg);
		GameContext.getMessageCenter().send("", userId, tipNotifyMessage);
	}

	@Override
	public void autoLevelup() {
		
	}
	
	@Override
	public void sendMessage(Message message,int awaitMillis) {
	}

	@Override
	public void closeNetLink() {
		
	}

	@Override
	public void addEvent(Message message) {
		
	}

	@Override
	public void addTargetFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, AbstractRole targetRole) {
		
	}

	@Override
	public void addSelfFont(AttrFontSizeType sizeType, AttrFontColorType colorType, int value) {
		
	}
	
	@Override
	public void notifyAttrFont() {
		
	}

	@Override
	public boolean inSelfEyes(AbstractRole abstractRole) {
		return false;
	}

	@Override
	public void addCumulateEvent(Message message) {
		
	}
	
	@Override
	public void addSelfFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, SkillContext context) {
		
	}
	
	@Override
	public void addTargetFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, AbstractRole targetRole,
			SkillContext context) {
		
	}

	@Override
	public void addSelfFont(AttrFontSizeType sizeType,
			AttrFontColorType colorType, int value, SkillContext context,
			AbstractRole attack) {
		// TODO Auto-generated method stub
		
	}

}
