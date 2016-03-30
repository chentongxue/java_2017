package com.game.draco.app.dailyplay.action;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.goods.GoodsOperateBean;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.dailyplay.DailyPlayStatus;
import com.game.draco.app.dailyplay.config.DailyPlayReward;
import com.game.draco.app.dailyplay.config.DailyPlayRule;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.item.DailyPlayRuleItem;
import com.game.draco.message.item.GoodsLiteNamedItem;
import com.game.draco.message.request.C1920_DailyPlayPeqMessage;
import com.game.draco.message.response.C1920_DailyPlayRespMessage;
import com.google.common.collect.Lists;

public class DailyPlayListAction extends BaseAction<C1920_DailyPlayPeqMessage> {

	//排序
	private Comparator<DailyPlayRuleItem> comparator = new Comparator<DailyPlayRuleItem>() {
		@Override
		public int compare(DailyPlayRuleItem r1, DailyPlayRuleItem r2) {
			if (r1.getFlag() < r2.getFlag()) {
				return 1;
			}
			if (r1.getFlag() > r2.getFlag()) {
				return -1;
			}
			return 0;
		}
	};
	
		
	@Override
	public Message execute(ActionContext context, C1920_DailyPlayPeqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context) ;
		if(null == role){
			return null ;
		}
		C1920_DailyPlayRespMessage respMsg = new C1920_DailyPlayRespMessage() ;
		
		List<DailyPlayRuleItem> itemList = Lists.newArrayList() ;
		Collection<DailyPlayRule> ruleList = GameContext.getDailyPlayApp().getAllDailyPlayRule() ;
		for (DailyPlayRule rule : ruleList) {
			byte status = GameContext.getDailyPlayApp().getStatus(rule, role);
			if (DailyPlayStatus.canot_show.getType() == status) {
				continue;
			}
			DailyPlayRuleItem item = new DailyPlayRuleItem();
			item.setPlayId((short)rule.getPlayId());
			item.setPlayName(rule.getPlayName());
			item.setPlayDesc(rule.getPlayDesc());
			item.setImageId(rule.getImageId());
			item.setRequireNum(rule.getRequireNum());
			item.setFlag(status);
			short finishNum =GameContext.getDailyPlayApp().getCompleteTimes(
					rule.getPlayId(), role) ;
			item.setFinishNum((short)Math.min(finishNum, rule.getRequireNum()));
			item.setForwardId(rule.getForwardId());
			item.setMinLevel((byte) rule.getRoleLevel());
			//奖励
			this.setReward(item, role, rule.getPlayId());
			itemList.add(item);
		}
		this.sort(itemList);
		respMsg.setDailyPlayItemList(itemList);
		return respMsg ;
	}
	
	private void setReward(DailyPlayRuleItem item,RoleInstance role,int playId){
		DailyPlayReward reward = GameContext.getDailyPlayApp().getDailyPlayReward(playId, role) ;
		if(null == reward){
			return ;
		}
		Map<Byte,Integer> attriMap = reward.getAttriMap() ;
		if(!Util.isEmpty(attriMap)){
			List<AttriTypeValueItem> attriRewardList = Lists.newArrayList() ;
			for(Byte type : attriMap.keySet()){
				AttriTypeValueItem attriItem = new AttriTypeValueItem();
				attriItem.setAttriType(type);
				attriItem.setAttriValue(attriMap.get(type));
				attriRewardList.add(attriItem) ;
			}
			item.setAttriRewardList(attriRewardList);
		}
		List<GoodsOperateBean> goodsList = reward.getGoodsList() ;
		if(!Util.isEmpty(goodsList)){
			List<GoodsLiteNamedItem> goodsRewardList = Lists.newArrayList() ;
			for(GoodsOperateBean bean : goodsList){
				GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(bean.getGoodsId());
				if(null == gb){
					continue ;
				}
				GoodsLiteNamedItem liteItem = gb.getGoodsLiteNamedItem() ;
				liteItem.setNum((short)bean.getGoodsNum());
				liteItem.setBindType(bean.getBindType().getType());
				goodsRewardList.add(liteItem) ;
			}
			item.setGoodsRewardList(goodsRewardList);
		}
	}
	
	private void sort(List<DailyPlayRuleItem> itemList){
		if(Util.isEmpty(itemList) || 0 == itemList.size()){
			return ;
		}
		Collections.sort(itemList, comparator);
	}
}
