package com.game.draco.app.npc.action;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.auction.AuctionApp;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.camp.balance.CampBalanceApp;
import com.game.draco.app.exchange.ExchangeConstant;
import com.game.draco.app.npc.domain.NpcInstance;
import com.game.draco.app.quest.QuestHelper;
import com.game.draco.app.quest.poker.QuestPokerApp;
import com.game.draco.app.shopsecret.ShopSecretConstant;
import com.game.draco.app.store.NpcStoreAppImpl;
import com.game.draco.app.union.UnionApp;
import com.game.draco.message.item.NpcFunctionItem;
import com.game.draco.message.request.C0607_NpcFunctionReqMessage;
import com.game.draco.message.request.C0858_AuctionMenuReqMessage;
import com.game.draco.message.request.C1401_ExchangeListReqMessage;
import com.game.draco.message.request.C1530_CampBalanceReqMessage;
import com.game.draco.message.request.C1601_NpcStoreListReqMessage;
import com.game.draco.message.request.C1618_ShopSecretOpenPanelReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C0607_NpcFunctionRespMessage;

public class NpcFunctionAction extends BaseAction<C0607_NpcFunctionReqMessage>{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public Message execute(ActionContext context,
			C0607_NpcFunctionReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		MapInstance mapInstance = role.getMapInstance();
		if (null == mapInstance) {
			return null;
		}
		// 获得NPC
		String npcInstanceId = reqMsg.getParam();
		NpcInstance npcInstance = mapInstance.getNpcInstance(npcInstanceId);
		if ( null == npcInstance ) {
			C0002_ErrorRespMessage erm = new C0002_ErrorRespMessage();
			erm.setReqCmdId(reqMsg.getCommandId());
			erm.setInfo(Status.Npc_Not_Exist.getTips());
			return erm;
		}
		if (1 == npcInstance.getNpc().getShieldFunc()) {
			// NPC开启屏蔽功能
			return null;
		}
		C0607_NpcFunctionRespMessage respMsg = new C0607_NpcFunctionRespMessage();
		// 闲话
		respMsg.setInfo(npcInstance.getNpc().getGossip());

		List<NpcFunctionItem> items = GameContext.getNpcApp().getNpcFunction(
				role, npcInstance);
		respMsg.setItems(items);
		respMsg.setNpcRoleId(npcInstance.getIntRoleId());
		if (null == items || 1 != items.size()) {
			return respMsg;
		}
		// 只有一个功能项并且是任务，直接弹出任务面板
		try {
			NpcFunctionItem item = items.get(0);
			short cmdId = item.getCommandId();
			String param = item.getParam();
			// 任务详情面板
			if (QuestHelper.QuestBeforeOperateReqCmdId == cmdId) {
				return QuestHelper.questBeforeOperateRespMessageBuilder(role,
						param);
			}
			// 扑克任务面板
			if (QuestPokerApp.RmQuestPanelReqCmdId == cmdId) {
				return GameContext.getQuestPokerApp()
						.getQuestPokerPanelMessage(role,false);
			}
			// NPC商店
			if (NpcStoreAppImpl.NPC_STORE_REQ_COMMAND == cmdId) {
				C1601_NpcStoreListReqMessage message = new C1601_NpcStoreListReqMessage();
				message.setParam(param);
				role.getBehavior().addCumulateEvent(message);
				return null;
			}

			// 拍卖行菜单
			if (AuctionApp.AuctionMenuCmdId == cmdId) {
				role.getBehavior().addCumulateEvent(
						new C0858_AuctionMenuReqMessage());
				return null;
			}
			// NPC兑换 1404
			if (ExchangeConstant.EXCHANGE_NPC_ITEM_CMD == cmdId) {
				C1401_ExchangeListReqMessage message = new C1401_ExchangeListReqMessage();
				message.setParam(param);
				role.getBehavior().addCumulateEvent(message);
				return null;
			}
			// 阵营转换
			if (CampBalanceApp.CAMP_BALANCE_CMDID == cmdId) {
				role.getBehavior().addCumulateEvent(
						new C1530_CampBalanceReqMessage());
				return null;
			}
			// 神秘商店
			if (ShopSecretConstant.SHOP_SECRET_ENTER_CMD == cmdId) {
				C1618_ShopSecretOpenPanelReqMessage msg = new C1618_ShopSecretOpenPanelReqMessage();
				msg.setShopId(param);
				role.getBehavior().addCumulateEvent(msg);
				return null;
			}
			
			if (UnionApp.UnionSummonPanelReqCmdId == cmdId) {
				if(!Util.isEmpty(items) && role.hasUnion()){
					boolean flag = role.getUnion().isSummonFlag();
					int num = 0;
					for(NpcFunctionItem it : items){
						if(flag){
							num = 1;
						}
						it.setTitle(it.getTitle() + GameContext.getI18n().messageFormat(TextId.UNION_SUMMON_NUM,num,1));
					}
				}
			}
		} catch (Exception e) {
			this.logger.error(
					"NpcFunctionAction auto open for only one item error: ", e);
		}
		return respMsg;
	}
	
}
