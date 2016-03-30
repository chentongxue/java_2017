package sacred.alliance.magic.vo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.AttrFontColorType;
import sacred.alliance.magic.base.AttrFontSizeType;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.StateType;
import sacred.alliance.magic.constant.ScreenConstant;
import sacred.alliance.magic.constant.SkillConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.util.SortedValueNumMap;
import sacred.alliance.magic.util.StringUtil;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.skill.vo.SkillContext;
import com.game.draco.message.item.RoleAttributeChangeItem;
import com.game.draco.message.response.C0400_RoleAttributeChangeRespMessage;

public abstract class AbstractRoleBehavior<T extends AbstractRole> {
	public static Logger log = LoggerFactory.getLogger(AbstractRoleBehavior.class);
	protected T role;
	GameContext context;

	public AbstractRoleBehavior(T t) {
		this.role = t;
		context = GameContext.getGameContext();
	}
	
	public long getLastIoReadTime(){
		return -1 ;
	}
	
	public long getLastIoWriteTime(){
		return -1 ;
	}

	private RoleNotifyAttribute lastStatusAtt;

	public abstract void update() throws ServiceException;

	public abstract void death(AbstractRole attacker);

	public abstract void stopMove();

	public boolean canMove(){
		if(0 == role.getState()){
			return true ;
		}
		for(StateType st : StateType.values()){
			if(role.inState(st) && !st.isCanMove()){
				return false ;
			}
		}
		return true ;
	}
	
	public boolean canUseSkill(){
		if(0 == role.getState()){
			return true ;
		}
		for(StateType st : StateType.values()){
			if(role.inState(st) && !st.isCanUseSkill()){
				return false ;
			}
		}
		return true ;
	}

	public boolean canUseCommonSkill(){
		if(0 == role.getState()){
			return true ;
		}
		for(StateType st : StateType.values()){
			if(role.inState(st) && !st.isCanUseCommonSkill()){
				return false ;
			}
		}
		return true ;
	}


	public void resetHpMp() {
		changeAttribute(AttributeType.curHP, OperatorType.Equal, role
				.getMaxHP());
		notifyAttribute();
	}

	public abstract void enterMap() throws ServiceException;

	public abstract void exitMap() throws ServiceException;

	public abstract void changeMap(Point targetPoint) throws ServiceException;

	public abstract void notifyPosition(Message resp) throws ServiceException;

	public void move(int x, int y, long moveTime) {
		if (x == role.getMapX() && y == role.getMapY()) {
			return;
		}
		/**
		 * role.dir要根据坐标来计算
		 */
		int nowX = role.getMapX();
		int nowY = role.getMapY();
		role.setDir(Direction.getDir(x, y, nowX, nowY));

		role.getMapInstance().move(role, new Point(role.getMapId(), x, y),
				role.getDir());

		/*
		 * WalkSynchRespMessage resp = new WalkSynchRespMessage();
		 * resp.setRoldId(role.getRoleId());
		 * resp.setMapx((short)role.getMapX());
		 * resp.setMapy((short)role.getMapY()); resp.setDir((byte)
		 * role.getDir().getType());
		 * resp.setMomentFlag((byte)role.getMovementFlag
		 * ().getType()(byte)Momentflag.NORMAL.getType());
		 * resp.setMoveOrStop(MoveOrStopConstant.MOVE);
		 * role.getMapInstance().broadcastMap(role, resp);
		 * 
		 * if(role.getRoleType() == RoleType.NPC){ NpcWalkSynchRespMessage resp1
		 * = new NpcWalkSynchRespMessage(); resp1.setNpcId(role.getRoleId());
		 * resp1.setNowX((short)nowX); resp1.setNowY((short)nowY);
		 * resp1.setTargetX((short)role.getMapX());
		 * resp1.setTargetY((short)role.getMapY());
		 * resp1.setSpeed((short)role.getSpeedType().getSpeed(RoleType.NPC));
		 * resp1.setMoveTime(moveTime); role.getMapInstance().broadcastMap(role,
		 * resp1); }
		 */
	}

	public void changeAttribute(AttributeType attType,
			OperatorType operatorType, int value) {
		if(null == attType){
			return ;
		}
		if (lastStatusAtt == null) {
			lastStatusAtt = context.getUserAttributeApp()
					.getRoleNotifyAttribute(role);
		}
		if (attType.isMoney()) {
			context.getUserAttributeApp().changeRoleMoney(role,
					attType, operatorType, value, OutputConsumeType.default_type);
			//玩家金钱变化日志
			GameContext.getStatLogApp().roleMoneyLog((RoleInstance) role, attType, OutputConsumeType.default_type, value, "");
		} else {
			context.getUserAttributeApp().changeAttribute(role,
					attType, operatorType, value,OutputConsumeType.default_type);
		}

	}

	public void resetCurrentAttributeStatus() {
		lastStatusAtt = context.getUserAttributeApp()
				.getRoleNotifyAttribute(role);
	}


	// 显示人物状态变化，只发送给玩家自己
	/*
	 * public void notifyStateType() { if (null == role) { return; }
	 * if(role.getRoleType()!=RoleType.PLAYER){
	 * role.getMapInstance().getMessageDispatcher().dispatch(new Telegram(null,
	 * (NpcInstance) role, MessageType.DECELERATION, 0, 0)); return; }
	 * 
	 * RoleAttributeChangeRespMessage resp =
	 * context.getUserAttributeApplication().getRoleAttributeChangeMessage(
	 * AttributeType.state, 0, role.getState()); if (null == resp) { return; }
	 * resp.setRoleId(role.getRoleId()); MapInstance map =
	 * role.getMapInstance(); if (null != map) {
	 * role.getMapInstance().broadcastMap(role, resp); }
	 * GameContext.getContext().getMessageCenter().send("",
	 * ((RoleInstance)role).getUserId(), resp); }
	 */

	public abstract void notifyForceType();
	
	
	// 属性变化，通知自己、队友和仇恨值列表中的玩家
	public void notifyAttribute() {
		synchronized (this) {
			if (lastStatusAtt == null) {
				return;
			}
			RoleNotifyAttribute nowStatusAtt = context
					.getUserAttributeApp().getRoleNotifyAttribute(role);
			C0400_RoleAttributeChangeRespMessage respSelf = context
					.getUserAttributeApp()
					.getRoleAttributeChangeMessage(lastStatusAtt, nowStatusAtt);
			if (respSelf == null || null == respSelf.getItems()
					|| 0 == respSelf.getItems().size()) {
				return;
			}
		
			respSelf.setRoleId(role.getIntRoleId());
			
			C0400_RoleAttributeChangeRespMessage respOther = null ;
			List<RoleAttributeChangeItem> filterItem = this.getAttrItemsByFilter(respSelf.getItems());
			if(null != filterItem){
				respOther = new C0400_RoleAttributeChangeRespMessage(); 
				respOther.setRoleId(respSelf.getRoleId());
				respOther.setItems(filterItem);
			}
			if (role.getRoleType() == RoleType.PLAYER
                    || role.getRoleType() == RoleType.PET) {
				// 通知自己
				RoleInstance me = (RoleInstance)role.getMasterRole();
				GameContext.getMessageCenter().send("",
						((RoleInstance) role).getUserId(), respSelf);
				MapInstance mapInstance = role.getMapInstance();
                if(null != mapInstance){
                    mapInstance.notifyRoleAttributeToOther(me,respOther);
                }
			}else {
                // 通知仇恨列表中的人物
                role.getHatredTarget().broadcast(respOther);
            }

			lastStatusAtt = nowStatusAtt;
		}
	}
	
	/**
	 * 通知他人改变后的属性（过滤通知自己的属性列表）
	 * @param items
	 * @return
	 */
	protected List<RoleAttributeChangeItem> getAttrItemsByFilter(List<RoleAttributeChangeItem> items){
		List<RoleAttributeChangeItem> newItems = null ;
		for(RoleAttributeChangeItem item : items){
			if(null == item){
				continue;
			}
			if(item.getAttType() == AttributeType.curHP.getType()
					|| item.getAttType() == AttributeType.maxHP.getType()
					|| item.getAttType() == AttributeType.speed.getType()){
				if(null == newItems){
					newItems = new ArrayList<RoleAttributeChangeItem>();
				}
				newItems.add(item);
				continue;
			}
		}
		return newItems;
	}


	public abstract void notifyBattleAttrIncome(NpcInstance dieNpc);
	

	public boolean hasInBattle() {
		if (role.getHatredTarget().getHatredMap().size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/** 自己是否在目标的视野之内 */
	public boolean inTargetEyes(RoleInstance instance) {
		int screenWidth = instance.getScreenWidth() + ScreenConstant.OFFSET;
		int screenHeight = instance.getScreenHeight() + ScreenConstant.OFFSET;
		// 如果是队友，需要发送协议
		/*if (GameContext.getTeamApplication().isInSameTeam(role.getRoleId(),
				instance.getRoleId())) {
			return true;
		}*/
		// 判断是否在同一屏幕内，如果不在同一屏幕内不发消息
		int originX = instance.getMapX() - screenWidth / 2;
		int originY = instance.getMapY() - screenHeight / 2;
		if (Util.inRectangle(originX, originY, role.getMapX(), role
				.getMapY(), screenWidth, screenHeight)) {
			return true;
		} else {
			return false;
		}
	}
	
	/** 目标是否在自己的视野之内 */
	public abstract boolean inSelfEyes(AbstractRole abstractRole);
	
	protected void sendSkillApplyMessage(Message msg,Message affixMessage,
			String srcUserId,String destUserId){
		if(null != msg){
			context.getMessageCenter().send(srcUserId, destUserId, msg, 2000);
		}
		if(null != affixMessage){
			context.getMessageCenter().send(srcUserId, destUserId, affixMessage, 2000);
		}
	}
	
	/**
	 * 必须转发消息的情况
	 * @return
	 */
	private boolean isMustTransMsg(RoleInstance instance){
		if (isSameTeam(instance)){
			return true;
		}
		return false;
	}

	
	private boolean inHatredMap(RoleInstance instance){
		return instance.getHatredTarget().inHatredMap(role.getRoleId());
	}

	public void notifySkillBuff(Message resp,Message affixMessage,
			String mustRoleId, Set<Integer> excludeRoleIds)
			throws ServiceException {
		MapInstance mapInstance = this.role.getMasterRole().getMapInstance();
		if (mapInstance == null){
			return;
		}
		List<RoleInstance> roleList = new ArrayList<RoleInstance>();
		for (RoleInstance currentRole : mapInstance.getRoleList()) {
			if(role.getMasterRole().getRoleId().equals(currentRole.getRoleId())){
				//自己
				continue ;
			}
//			if(null != excludeRoleIds && excludeRoleIds.contains(currentRole.getIntRoleId())) {
//				continue ;
//			}
			if (null != mustRoleId && currentRole.getRoleId().equals(mustRoleId)) {
				this.sendSkillApplyMessage(resp, affixMessage, null, currentRole.getUserId());
				continue;
			}
			if (isMustTransMsg(currentRole)) {
				this.sendSkillApplyMessage(resp, affixMessage, null, currentRole.getUserId());
				continue;
			}
			if (this.inHatredMap(currentRole) || inTargetEyes(currentRole)) {
				roleList.add(currentRole);
			}
		}
		if (roleList.size() <= 0) {
			return;
		}
		int count = SkillConstant.sendMsgCount;
		if (roleList.size() < count) {
			count = roleList.size();
		}
		for (int i = 0; i < count; i++) {
			RoleInstance instance = roleList.get(role.getSkillMsgIndex()
					% roleList.size());
			this.sendSkillApplyMessage(resp, affixMessage, null, instance.getUserId());
			role.setSkillMsgIndex(role.getSkillMsgIndex() + 1);
		}
		roleList.clear();
		roleList = null ;
	}
	
	protected boolean isSameTeam(RoleInstance instance){
		return GameContext.getTeamApp().isInSameTeam(
				role, instance);
	}
	
	/*public boolean isChangeTarget() {
		boolean isChange = role.getChangeTarget().get();
		role.getChangeTarget().compareAndSet(true, false);
		return isChange;
	}*/


	public void changeDisTarget() {
		if (role.getHatredTarget().getHatredMap() == null
				|| role.getHatredTarget().getHatredMap().size() == 0) {
			return;
		}
		int dis = 0;
		int maxDis = 0;
		Point p1 = new Point("", role.getMapX(), role.getMapY());
		SortedValueNumMap map = role.getHatredTarget().getHatredMap();
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String targetId = entry.getKey().toString();
			AbstractRole targetRole = role.getMapInstance().getAbstractRole(
					targetId);
			Point p2 = new Point("", targetRole.getMapX(), targetRole.getMapY());
			dis = Point.math_DistPointPoint(p1.x - p2.x, p1.y - p2.y);
			if (dis >= maxDis) {
				maxDis = dis;
				role.setTarget(targetRole);
			}
		}
	}

	public void changeHatredTarget(int hatredSort) {
		SortedValueNumMap map = role.getHatredTarget().getHatredMap();
		if (map == null || map.size() == 0) {
			return;
		}
		AbstractRole targetRole = null;
		String targetId = null;
		if (role.getHatredTarget().getHatredMap().size() == 1
				|| hatredSort == 1) {
			targetId = role.getHatredTarget().getFirstHateTarget();
		} else {
			Iterator it = map.entrySet().iterator();
			int i = 0;
			while (it.hasNext()) {
				i++;
				if (i == hatredSort - 1) {
					Map.Entry entry = (Map.Entry) it.next();
					targetId = entry.getKey().toString();
					if (targetId == null) {
						return;
					}
					break;
				}
			}
		}
		targetRole = role.getMapInstance().getAbstractRole(targetId);
		if (targetRole == null) {
			return;
		}
		role.setTarget(targetRole);
	}

	public void changeRandomHatredTarget() {
		SortedValueNumMap map = role.getHatredTarget().getHatredMap();
		if (map == null || map.size() == 0) {
			return;
		}
		int hatredSort = StringUtil.randomInt(1, map.size());
		AbstractRole targetRole = null;
		String targetId = null;
		if (role.getHatredTarget().getHatredMap().size() == 1
				|| hatredSort == 1) {
			targetId = role.getHatredTarget().getFirstHateTarget();
		} else {
			Iterator it = map.entrySet().iterator();
			int i = 0;
			while (it.hasNext()) {
				i++;
				if (i == hatredSort - 1) {
					Map.Entry entry = (Map.Entry) it.next();
					targetId = entry.getKey().toString();
					if (targetId == null) {
						return;
					}
					break;
				}
			}
		}
		targetRole = role.getMapInstance().getAbstractRole(targetId);
		if (targetRole == null) {
			return;
		}
		role.setTarget(targetRole);
	}
	
	public abstract void autoLevelup() ;
	
	
	//NPC喊话
	public abstract void notifyNpcMsg(String content);
	//玩家死亡后通知经验变化
//	public abstract void notifyRoleExp(RoleInstance dieRole);
	//玩家死亡
	//public abstract void death(RoleInstance attacker);
	
	public  void sendMessage(Message message){
		this.sendMessage(message,0);
	}
	public abstract void sendMessage(Message message,int awaitMillis) ;
	/**断开网络链接*/
	public abstract void closeNetLink();
	
	public abstract void addEvent(Message message);
	
	public abstract void addCumulateEvent(Message message);

	public RoleNotifyAttribute getLastStatusAtt() {
		return lastStatusAtt;
	}

	public void setLastStatusAtt(RoleNotifyAttribute lastStatusAtt) {
		this.lastStatusAtt = lastStatusAtt;
	}
	
	/**
	 * 自身的飘字效果
	 * @param sizeType
	 * @param colorType
	 * @param value
	 */
	public abstract void addSelfFont(AttrFontSizeType sizeType, AttrFontColorType colorType, int value);
	
	
	/**
	 * 攻击目标时，对目标所造成的飘字效果
	 * @param sizeType
	 * @param colorType
	 * @param value
	 * @param targetRole
	 */
	public abstract void addTargetFont(AttrFontSizeType sizeType, AttrFontColorType colorType, int value, AbstractRole targetRole);
	
	/**
	 * 发送飘字通知
	 */
	public abstract void notifyAttrFont();
	
	/**
	 * 处理技能使用流程中自身的飘字效果
	 * @param sizeType
	 * @param colorType
	 * @param value
	 * @param attacker
	 */
	public abstract void addSelfFont(AttrFontSizeType sizeType, AttrFontColorType colorType,
			int value, SkillContext context);
	
	/**
	 * 处理技能使用流程中自身的飘字效果
	 * @param sizeType
	 * @param colorType
	 * @param value
	 * @param attacker
	 */
	public abstract void addSelfFont(AttrFontSizeType sizeType, AttrFontColorType colorType,
			int value, SkillContext context,AbstractRole attack);
	
	/**
	 * 处理技能使用流程中攻击目标时，对目标所造成的飘字效果
	 * @param sizeType
	 * @param colorType
	 * @param value
	 * @param targetRole
	 */
	public abstract void addTargetFont(AttrFontSizeType sizeType, AttrFontColorType colorType,
			int value, AbstractRole targetRole, SkillContext context);
	
	public void switchBattleState() {
		
	}
}
