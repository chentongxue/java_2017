package com.game.draco.app.richman.vo.event;

import java.util.List;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.ParasConstant;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.RandomUtil;
import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapRichManInstance;

import com.game.draco.GameContext;
import com.game.draco.app.richman.RichManApp;
import com.game.draco.app.richman.config.RichManCard;
import com.game.draco.app.richman.config.RichManEvent;
import com.game.draco.app.richman.config.RichManState;
import com.game.draco.app.richman.vo.RichManCardHurtType;
import com.game.draco.app.richman.vo.RichManRoleBehavior;
import com.game.draco.app.richman.vo.RichManRoleStat;
import com.game.draco.app.richman.vo.RichManStateType;
import com.game.draco.app.richman.vo.RichManVoucherType;
import com.game.draco.message.item.RichManEventItem;
import com.game.draco.message.item.RichManRoleStatItem;
import com.game.draco.message.response.C2653_RichManRoleEventNoticeMessage;
import com.game.draco.message.response.C2658_RichManRoleStatRespMessage;
import com.game.draco.message.response.C2659_RichManRoleHeadAnimAddRespMessage;
import com.google.common.collect.Lists;

public class RichManEventBeAttacked extends RichManEventLogic {
	private static RichManEventBeAttacked instance = new RichManEventBeAttacked(); 
	
	private RichManEventBeAttacked() {
		
	}
	
	public static RichManEventBeAttacked getInstance() {
		return instance;
	}
		
	@Override
	public void execute(MapRichManInstance mapInstance, RoleInstance role,
			RichManRoleBehavior behavior) {
		RichManEvent event = behavior.getEvent();
		int cardId = (int)event.getEventValue();
		RichManCard card = GameContext.getRichManApp().getRichManCard(cardId);
		if(null == card) {
			return ;
		}
		GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(cardId);
		if(null == gb) {
			return ;
		}
		int rand = RandomUtil.randomInt(RichManApp.TEN_THOUSAND);
		if(rand >= card.getHitRate()) {
			//未打中,提示玩家被谁攻击了
			return ;
		}
		
		//状态
		byte stateId = card.getStateId();
		RichManState state = GameContext.getRichManApp().getRichManState(stateId);
		if(null == state) {
			return ;
		}
		RichManStateType cardType = state.getStateType();
		this.notifyRichManRoleStateStart(mapInstance, role, event, state);
		if(cardType == RichManStateType.Protect) {
			//如果是保护卡只能对自己使用
			C2653_RichManRoleEventNoticeMessage respMsg = getRoleEventNoticeMessage(behavior);
			role.getBehavior().sendMessage(respMsg);
			this.broadcastoleHeadAnimAdd(mapInstance, role.getIntRoleId(), state);
			return ;
		}
		
		//处理点券
		int random = RandomUtil.randomInt(card.getMinValue(), card.getMaxValue());
		int couponChange = 0;
		if(card.getCardVoucherType() == RichManVoucherType.value) {
			//如果值类型
			couponChange = random;
		}
		else {
			couponChange = (int)(role.get(AttributeType.todayCoupon) * random
					/ ParasConstant.PERCENT_BASE_VALUE);
		}
		if(couponChange > 0) {
			//被攻击者减点券
			GameContext.getUserAttributeApp().changeAttribute(role, AttributeType.todayCoupon, 
					OperatorType.Decrease, couponChange, OutputConsumeType.richman_event_consume);
			role.getBehavior().notifyAttribute();
			if(card.getCardHurtType() == RichManCardHurtType.Rob) {
				//如果伤害方式是抢夺,则攻击方获得点券
				RoleInstance attcker = GameContext.getOnlineCenter()
					.getRoleInstanceByRoleId(String.valueOf(behavior.getAttackerId()));
				if(null != attcker) {
					GameContext.getUserAttributeApp().changeAttribute(attcker, AttributeType.todayCoupon, 
							OperatorType.Add, couponChange, OutputConsumeType.richman_event_output);
					attcker.getBehavior().notifyAttribute();
				}
			}
		}
		
		//大富翁事件通知
		String info = GameContext.getI18n().messageFormat(TextId.Richman_be_attacked,
				behavior.getAttackerName(), gb.getName(), couponChange);
		C2653_RichManRoleEventNoticeMessage respMsg = getRoleEventNoticeMessage(behavior, info);
		role.getBehavior().sendMessage(respMsg);
		this.broadcastoleHeadAnimAdd(mapInstance, role.getIntRoleId(), state);
	}
	
	/**
	 * 角色状态时间通知
	 * @param mapInstance
	 * @param role
	 * @param stateId
	 */
	private void notifyRichManRoleStateStart(MapRichManInstance mapInstance, RoleInstance role,
			RichManEvent event, RichManState state) {
		RichManRoleStat roleStat = mapInstance.getRoleStat(role.getIntRoleId());
		if(null != roleStat) {
			roleStat.initStateTime(state.getId(), state.getTime());
			C2658_RichManRoleStatRespMessage stateRespMsg = new C2658_RichManRoleStatRespMessage();
			List<RichManRoleStatItem> statItemList = Lists.newArrayList();
			RichManRoleStatItem statItem = new RichManRoleStatItem();
			statItem.setId(state.getId());
			statItem.setImageId(state.getImageId());
			statItem.setName(state.getName());
			statItem.setStatTime(state.getTime());
			statItemList.add(statItem);
			stateRespMsg.setStatItemList(statItemList);
			role.getBehavior().sendMessage(stateRespMsg);
		}
	}
	
	/**
	 * 玩家头顶动画广播
	 * @param mapInstance
	 * @param roleId
	 * @param event
	 */
	private void broadcastoleHeadAnimAdd(MapRichManInstance mapInstance, int roleId, RichManState state) {
		C2659_RichManRoleHeadAnimAddRespMessage animAddRespMsg = new C2659_RichManRoleHeadAnimAddRespMessage();
		animAddRespMsg.setRoleId(roleId);
		RichManEventItem eventItem = new RichManEventItem();
		eventItem.setId(state.getId());
		eventItem.setAnimId(state.getEffectId());
		animAddRespMsg.setEventItem(eventItem);
		mapInstance.broadcastMap(null, animAddRespMsg);
	}

}
