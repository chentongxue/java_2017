package com.game.draco.app.sign.action;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.sign.config.SignConfig;
import com.game.draco.message.item.GoodsLiteItem;
import com.game.draco.message.item.SignAwardItem;
import com.game.draco.message.request.C2404_SignPanelReqMessage;
import com.game.draco.message.response.C2404_SignPanelRespMessage;
import com.google.common.collect.Lists;

public class SignPanelAction extends BaseAction<C2404_SignPanelReqMessage> {

	@Override
	public Message execute(ActionContext context, C2404_SignPanelReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C2404_SignPanelRespMessage respMsg = new C2404_SignPanelRespMessage();
		Date now = new Date();
		respMsg.setLastMonthTotalDay((byte)DateUtil.getMaxDayLastMonth(now));
		respMsg.setCurMonthTotalDay((byte)DateUtil.getMaxDayMonth(now));
		respMsg.setCurYear((short)DateUtil.getYear(now));
		respMsg.setCurMonth((byte)DateUtil.getMonthIncrOne(now));
		respMsg.setCurDay((byte)DateUtil.getDay(now));
		respMsg.setCurWeek((byte)DateUtil.getWeek(now));
		int signValue = GameContext.getSignApp().getMonthSignValue(role);
		respMsg.setSignInfo(signValue);
		int currSignTimes = GameContext.getSignApp().getCurrSignTimes(role);
		respMsg.setCurrSignTime((byte)currSignTimes);
		//获得奖励列表
		List<SignAwardItem> awardList = Lists.newArrayList();
		Collection<SignConfig> configList = GameContext.getSignApp().getAllSignConfig();
		int awardValue = GameContext.getSignApp().getCurrSignRecv(role);
		for(SignConfig config : configList){
			SignAwardItem item = new SignAwardItem();
			item.setResId((short)config.getResId());
			item.setSignTime((byte)config.getTimes());
			item.setRewardName(config.getRewardName());
			item.setStatus(GameContext.getSignApp().getRecvState(awardValue,
					currSignTimes, config.getTimes()));
			GoodsBase gb = GameContext.getGoodsApp().getGoodsBase(config.getGoodsId());
			if(null != gb){
				GoodsLiteItem goodsItem = gb.getGoodsLiteItem() ;
				goodsItem.setNum((short)config.getGoodsNum());
				goodsItem.setBindType((byte)config.getBindType());
				item.setGoodsItem(goodsItem);
			}
			awardList.add(item);
		}
		respMsg.setAwardList(awardList);
		return respMsg;
	}
}

