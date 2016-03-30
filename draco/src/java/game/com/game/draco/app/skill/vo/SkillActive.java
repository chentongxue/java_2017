package com.game.draco.app.skill.vo;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.TimeoutConstant;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.AttrFontInfo;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.skill.config.SkillApplyType;
import com.game.draco.app.skill.config.SkillAttackType;
import com.game.draco.app.skill.config.SkillDetail;
import com.game.draco.app.skill.config.SkillPassiveType;
import com.game.draco.app.skill.domain.RoleSkillStat;
import com.game.draco.message.item.AttrFontItem;
import com.game.draco.message.item.SkillApplyItem;
import com.game.draco.message.item.SkillApplyTargetItem;
import com.game.draco.message.item.SkillRoleAttrFontItem;
import com.game.draco.message.push.C0216_WalkTeleportNotifyMessage;
import com.game.draco.message.push.C0320_SkillDashNotifyMessage;
import com.game.draco.message.response.C0301_SkillApplyAttackerMessage;
import com.game.draco.message.response.C0306_SkillApplyOtherMessage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class SkillActive extends SkillAdaptor{

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public SkillActive(short skillId) {
		super(skillId);
		this.setSkillApplyType(SkillApplyType.active);
	}

	
	@Override
	public AttriBuffer getAttriBuffer(AbstractRole role){
		return null ;
	}
	
	@Override
	protected  boolean hasPassiveType(SkillPassiveType skillPassiveType){
		//主动技能没有被动效果
		return false ;
	}
	
	private void sendAttackerMessage(AbstractRole role,SkillContext context,SkillDetail detail,
			Map<Integer, List<AttrFontInfo>> attrFontInfoMap){
		if(null == role || role.getRoleType() == RoleType.NPC){
			return ;
		}
		boolean channelSkill = context.isChannelSkill();
		boolean skillActiveApply = context.isSkillActiveApply();
		//通道技能和客户端主动请求技能都不需要设置actionId和effectId
		SkillApplyItem c301_applyItem = this.createSkillApplyItem(0, role, detail, null,
				skillActiveApply || channelSkill);
		//发送给攻击者
		//如果是客户端主动技能不需要返回动作id,特效id
		C0301_SkillApplyAttackerMessage attackerRespMsg = new C0301_SkillApplyAttackerMessage();
		attackerRespMsg.setSkillApplyItem(c301_applyItem);
		int attrFontRoleKey = role.getMasterRole().getIntRoleId();
		List<SkillRoleAttrFontItem> attackerSeeAttrFontItem = this.createRoleAttriFont(attrFontInfoMap.get(attrFontRoleKey));
		attackerRespMsg.setAttackerAttrFontItem(attackerSeeAttrFontItem);
		if(role.getRoleType() == RoleType.PLAYER){
			//连击数
			RoleInstance player = (RoleInstance)role;
			attackerRespMsg.setHitCombo((short)player.getHitCombo().get());
		}
		role.getBehavior().sendMessage(attackerRespMsg);
	}
	
	@Override
	protected void notifyMessage(AbstractRole role,  Set<Integer> targetIdSet, SkillContext context){
		SkillDetail detail = this.getSkillDetail(context.getSkillLevel());
		Map<Integer, List<AttrFontInfo>> attrFontInfoMap = context.getAttrFontInfoMap();
		
		//发送攻击者
		this.sendAttackerMessage(role, context, detail, attrFontInfoMap);
		
		//特殊效果消息
		Message affixMessage = null ;
		SkillAttackType attackType = getSkillAttackType();
		if(SkillAttackType.Telesport == attackType){
			//闪现通知其他用户坐标
			C0216_WalkTeleportNotifyMessage notifyMessage = new C0216_WalkTeleportNotifyMessage();
			notifyMessage.setRoleId(role.getIntRoleId());
			notifyMessage.setX((short)role.getMapX());
			notifyMessage.setY((short)role.getMapY());
			affixMessage = notifyMessage ;
		}else if(SkillAttackType.Dash == attackType){
			//冲锋
			C0320_SkillDashNotifyMessage message = new C0320_SkillDashNotifyMessage();
			message.setRoleId(role.getIntRoleId());
			affixMessage = message ;
		}
		
		//目标和其他人都要看到被击中者的特殊状态(击飞等)
		List<SkillRoleAttrFontItem> roleStateFontItemList = this.createRoleAttriFont(context.getStateFontInfoList());
		//发送消息给目标
		SkillApplyTargetItem applyItem = (SkillApplyTargetItem)this.createSkillApplyItem(1, role, 
				detail, targetIdSet, context.isChannelSkill());
		if(!Util.isEmpty(targetIdSet)) {
			int attackerId = role.getIntRoleId();
			for(Integer targetId : targetIdSet) {
				if(attackerId == targetId) {
					//目标是自己
					continue;
				}
				List<SkillRoleAttrFontItem> attrFontItemList = Lists.newArrayList();
				//自己属性飘字和状态飘字
				List<AttrFontInfo> fontInfoList = attrFontInfoMap.get(targetId);
				if(Util.isEmpty(fontInfoList)) {
					//不需要给npc发飘字信息
					continue ;
				}
				attrFontItemList.addAll(this.createRoleAttriFont(fontInfoList));
				if(!Util.isEmpty(roleStateFontItemList)) {
					//别人状态飘字,不包含自己
					for(SkillRoleAttrFontItem stateFontItem : roleStateFontItemList) {
						if(stateFontItem.getRoleId() == targetId) {
							continue;
						}
						attrFontItemList.add(stateFontItem);
					}
				}
				C0306_SkillApplyOtherMessage defenderMsg = new C0306_SkillApplyOtherMessage();
				defenderMsg.setSkillApplyItem(applyItem);
				defenderMsg.setStateFontItem(attrFontItemList);
				this.sendSkillApplyMessage(defenderMsg, affixMessage, null, String.valueOf(targetId));
			}
		}
		//发送给其他人
		C0306_SkillApplyOtherMessage otherMsg = new C0306_SkillApplyOtherMessage();
		otherMsg.setSkillApplyItem(applyItem);
		otherMsg.setStateFontItem(roleStateFontItemList);
		try {
			role.getBehavior().notifySkillBuff(otherMsg,affixMessage,
					String.valueOf(applyItem.getTargetRoleId()), targetIdSet);
		} catch (ServiceException e) {
			logger.error("",e);
		}
		
	}
	
	private void sendSkillApplyMessage(Message msg,Message affixMessage,
			String srcRoleId,String destRoleId){
		if(null != msg){
			GameContext.getMessageCenter().sendByRoleId(srcRoleId, destRoleId, msg, 
					TimeoutConstant.Notify_AttrFont_Msg_Timeout);
		}
		if(null != affixMessage){
			GameContext.getMessageCenter().sendByRoleId(srcRoleId, destRoleId, affixMessage, 
					TimeoutConstant.Notify_AttrFont_Msg_Timeout);
		}
	}
	
	private List<SkillRoleAttrFontItem> createRoleAttriFont(List<AttrFontInfo> fontInfoList) {
		if(Util.isEmpty(fontInfoList)) {
			return null;
		}
		
		Map<Integer, List<AttrFontItem>> attrFontItemMap = Maps.newHashMap();
		for(AttrFontInfo attrFont : fontInfoList) {
			int ownerId = attrFont.getRoleId();
			List<AttrFontItem> attrFontItemList = attrFontItemMap.get(ownerId);
			if(null == attrFontItemList) {
				attrFontItemList = Lists.newArrayList();
				attrFontItemMap.put(ownerId, attrFontItemList);
			}
			attrFontItemList.add(this.createAttrFontItem(attrFont));
		}
		
		List<SkillRoleAttrFontItem> items = Lists.newArrayList();
		for(Entry<Integer, List<AttrFontItem>> entry : attrFontItemMap.entrySet()) {
			SkillRoleAttrFontItem item = new SkillRoleAttrFontItem();
			item.setRoleId(entry.getKey());
			item.setAttrFontItem(entry.getValue());
			items.add(item);
		}
		return items;
	}
	
	private AttrFontItem createAttrFontItem(AttrFontInfo attrFont) {
		//属性通知
		AttrFontItem item = new AttrFontItem();
		item.setType(attrFont.getFontType());
		item.setValue(attrFont.getValue());
		return item;
	}
	
	//触发  防御后 技能
	/*private void passiveAfterDefend(SkillContext context){
		AbstractRole defender = context.getDefender();
		//TODO:boss以后会有被动技能 ，，到时候需要修改条件（添加是否为boss   ，，，不是则return）
		if(null == defender 
				|| defender.getRoleType() != RoleType.PLAYER 
				|| context.getDefender() == context.getAttacker()){
			return ;
		}
		for (SkillStat stat : defender.getSkillMap().values()) {
			short skillId = stat.getSkillId();
			Skill skill = GameContext.getSkillApp().getSkill(
					skillId);
			if(!this.isTriggerPassive(skill, SkillPassiveType.afterDefend,defender)){
				continue ;
			}
			SkillContext c = new SkillContext(skill);
			c.setAttacker(defender);
			c.setSkillPassiveType(SkillPassiveType.afterDefend);
			c.setSkillLevel(defender.getSkillLevel(skill.getSkillId()));
			((SkillAdaptor) skill).use(c);
		}
	}*/
	
	private boolean isTriggerPassive(Skill skill,SkillPassiveType passiveType,AbstractRole abstRole){
		if(null == skill 
				|| skill.getSkillId() == this.getSkillId()
				|| null == passiveType){
			return false ;
		}
		if(!skill.hasPassiveType(passiveType)){
			return false ;
		}
		/*if (abstRole instanceof RoleInstance) {
			RoleInstance role = (RoleInstance) abstRole;
			if(!role.isSoulState() || role.getCurrentSoul().getSoulAttriTemplate().isPersonSkill()){
				return skill.getSkillLearnType().isHuman();
			}
			return !skill.getSkillLearnType().isHuman();
		}*/
		return true ;
	}
	
	@Override
	protected void passiveEffect(SkillContext context, SkillPassiveType passiveType) {
		if(context.isSystemTrigger()) {
			//如果是系统触发的技能则不会触发被动技能
			return ;
		}
		if(null == passiveType ){
			return ;
		}
		/*if(passiveType ==SkillPassiveType.afterDefend ){
			this.passiveAfterDefend(context);
			return ;
		}*/
		int skillLevel = context.getSkillLevel();
		Object info = context.getInfo();
		AbstractRole attacker = context.getAttacker();
		AbstractRole defender = context.getDefender();
		try {
			if(!this.isTriggerPassive() || null == passiveType){
				return ;
			}
			AbstractRole role = passiveType.isAttack()?attacker:defender;
			if (this.getSkillApplyType() != SkillApplyType.active
					|| null == role || role.isDeath()) {
				return;
			}
			/*if(!passiveType.isAttack()){
				//被动方的被动技能,攻击方,防御方需要调换
				context.setAttacker(defender);
				context.setDefender(attacker);
			}*/
			for (RoleSkillStat stat : role.getSkillMap().values()) {
				short skillId = stat.getSkillId();
				
				Skill skill = GameContext.getSkillApp().getSkill(
						skillId);
				if(!this.isTriggerPassive(skill, passiveType,role)){
					continue ;
				}
				context.setSkillPassiveType(passiveType);
				context.setSkill(skill);
				context.setSkillLevel(role.getSkillEffectLevel(skill.getSkillId()));
				((SkillAdaptor) skill).use(context);
			}
		} catch (Exception ex) {
			logger.error("",ex);
		}finally{
			context.setAttacker(attacker);
			context.setDefender(defender);
			//主动技能没有被动触发方式
			context.setSkillPassiveType(null);
			//将skill设置回来
			context.setSkill(this);
			//context.setSkillLevel(context.getAttacker().getSkillLevel(skillId));
			//上面语句错误,因为buff中使用的技能从角色身上将无法取到
			context.setSkillLevel(skillLevel);
			context.setInfo(info);
		}
	}
	
	@Override
	protected AbstractRole getSkillOwner(SkillContext context){
		return context.getAttacker() ;
	}
	
	@Override
	protected void hitComboLogic(SkillContext context){
		AbstractRole role = context.getAttacker() ;
		if(null == role){
			return ;
		}
		if(role.getRoleType() != RoleType.PLAYER){
			role.setLastUseSkillTime(System.currentTimeMillis());
			return;
		}
		RoleInstance player = (RoleInstance)role ;
		player.setLastUseSkillTime(System.currentTimeMillis());
	}
}
