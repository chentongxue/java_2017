package com.game.draco.app.asyncarena.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncarena.config.AsyncBase;
import com.game.draco.app.asyncarena.config.AsyncBuy;
import com.game.draco.app.asyncarena.config.AsyncGroupReward;
import com.game.draco.app.asyncarena.config.AsyncRankReward;
import com.game.draco.app.asyncarena.config.AsyncRefresh;
import com.game.draco.app.asyncarena.domain.AsyncArenaRole;
import com.game.draco.app.asyncarena.domain.AsyncBattleInfo;
import com.game.draco.message.item.AsyncArenaTargetItem;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C2620_RoleAsyncArenaListReqMessage;
import com.game.draco.message.response.C2620_RoleAsyncArenaListRespMessage;

public class RoleAsyncArenaBattleInfoListAction extends BaseAction<C2620_RoleAsyncArenaListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2620_RoleAsyncArenaListReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null;
		}
		//检查对战人数
		GameContext.getRoleAsyncArenaApp().validRoleBattltNum(role);
		//对战数据
		Map<Integer,AsyncBattleInfo> asyncBattleInfoMap = GameContext.getRoleAsyncArenaApp().getRoleAsyncBattleInfo(role);
		//角色数据
		AsyncArenaRole asyncArenaRole = GameContext.getRoleAsyncArenaApp().getRoleAsyncArenaInfo(role);
		//购买次数数据
		//暂时没有VIP系统先填-1
		byte freeNum = GameContext.getAsyncArenaApp().freeNum();
		
		if(asyncBattleInfoMap  == null || asyncBattleInfoMap .isEmpty()) {
			return new C0003_TipNotifyMessage(this.getText(TextId.ASYNC_ARENA_ROLE_NO_BATTLE_INFO));
		}
		
		AsyncBuy asyncBuy = GameContext.getAsyncArenaApp().getAsyncBuy((byte)-1,(byte)(asyncArenaRole.getMoneyNum()+freeNum+1));
		AsyncBase base = GameContext.getAsyncArenaApp().getAsyncBase();
		C2620_RoleAsyncArenaListRespMessage respMsg = new C2620_RoleAsyncArenaListRespMessage();
		respMsg.setRefNum(asyncArenaRole.getRefNum());
		respMsg.setSuccessNum(asyncArenaRole.getSuccessNum());
		respMsg.setChallengeNum(asyncArenaRole.getChallengeNum());
		respMsg.setHistoryHonor(asyncArenaRole.getHistoryHonor());
		respMsg.setNowHonor(asyncArenaRole.getNowHonor());
		respMsg.setRankId(base.getRankId());
		respMsg.setShopId(base.getShopId());
		
		AsyncRefresh refresh = GameContext.getAsyncArenaApp().getAsyncRefresh((byte)-1, (byte)(asyncArenaRole.getRefNum()+1));
		if(refresh != null){
			respMsg.setPrice(refresh.getPrice());
		}
		//购买次数花费
		if(asyncBuy != null){
			respMsg.setExpenditure(asyncBuy.getPrice());
		}
		
		//剩余时间
//		Date date = DateUtil.getDateEndTime(new Date(System.currentTimeMillis()));
//		int min = DateUtil.dateDiffSecond(new Date(System.currentTimeMillis()),date);
		respMsg.setTime(this.getText(TextId.ASYNC_ARENA_ROLE_RESET_INFO));
		
		//获得角色排名
		int rank = GameContext.getRoleAsyncArenaStorage().getRoleAsyncArenaRanking(role.getRoleId());
		respMsg.setRank(rank);
		
		if(asyncArenaRole.getIsReward() == 0){
			if(asyncArenaRole.getHistoryRanking() != 0){
				rank = asyncArenaRole.getHistoryRanking();
			}
		}
	
		respMsg.setHisroryRanking(rank);

		AsyncRankReward rankReward = GameContext.getRoleAsyncArenaApp().getAsyncRankReward(rank);
		
		if(rankReward != null){
			List<AsyncGroupReward> groupRewardList = GameContext.getAsyncArenaApp().getAsyncGroupRewardList(rankReward.getGroupId());
			if(!Util.isEmpty(groupRewardList)){
				List<AttriTypeValueItem> attriItemList = new ArrayList<AttriTypeValueItem>();
				
				for(AsyncGroupReward group : groupRewardList){
					AttriTypeValueItem attrItem = new AttriTypeValueItem();
					attrItem.setAttriType(group.getAttrType());
					attrItem.setAttriValue(group.getAttrValue());
					attriItemList.add(attrItem);
				}
				respMsg.setAttriItemList(attriItemList);
			}
			
			if(!Util.isEmpty(rankReward.getGoodsId())){
				String [] goodsId = rankReward.getGoodsId().split(",");
				String [] goodsNum = rankReward.getGoodsNum().split(",");
				String [] goodsBind = rankReward.getBindType().split(",");
				if(goodsId != null && goodsId.length > 0){
					GoodsLiteNamedItem goodsLiteItem = null;
					List<GoodsLiteItem> goodsLiteItemList = new ArrayList<GoodsLiteItem>();
					int i=0;
					for(;i<goodsId.length;i++){
						goodsLiteItem = new GoodsLiteNamedItem();
						GoodsBase goodsBase = GameContext.getGoodsApp().getGoodsBase(Integer.parseInt(goodsId[i]));
						goodsLiteItem.setGoodsId(goodsBase.getId());
						goodsLiteItem.setGoodsName(goodsBase.getName());
						goodsLiteItem.setBindType(Byte.parseByte(goodsBind[i]));
						goodsLiteItem.setNum(Short.parseShort(goodsNum[i]));
						goodsLiteItem.setQualityType(goodsBase.getQualityType());
						goodsLiteItem.setGoodsLevel((byte)goodsBase.getLevel());
						goodsLiteItemList.add(goodsLiteItem);
					}
					respMsg.setGoodsItem(goodsLiteItemList);
				}
			}
		}
		
		List<AsyncArenaTargetItem> targetList = GameContext.getRoleAsyncArenaApp().getAsyncArenaTargetItemList(role);
		if(targetList == null){
			new ArrayList<AsyncArenaTargetItem>();
		}
		
		if(!Util.isEmpty(targetList)){
			//按战力排序
			Collections.sort(targetList);
		}
		respMsg.setTargetItem(targetList);
		
		return respMsg;
	}
	

}
