package com.game.draco.app.hero.action;

import java.text.MessageFormat;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.base.GoodsType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsBase;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.hero.config.HeroLuckGoods;
import com.game.draco.app.hero.vo.LuckLotteryResult;
import com.game.draco.message.request.C1265_HeroLuckLotteryReqMessage;
import com.game.draco.message.response.C1265_HeroLuckLotteryRespMessage;

public class HeroLuckLotteryAction extends BaseAction<C1265_HeroLuckLotteryReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1265_HeroLuckLotteryReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		C1265_HeroLuckLotteryRespMessage respMsg = new C1265_HeroLuckLotteryRespMessage();
		LuckLotteryResult result = GameContext.getHeroApp().heroLuckLottery(role, reqMsg.getLuckType());
		if(!result.isSuccess()){
			respMsg.setStatus(RespTypeStatus.FAILURE);
			respMsg.setInfo(result.getInfo());
			return respMsg ;
		}
		GoodsBase gb = result.getGoodsBase() ;
		HeroLuckGoods luckGoods = result.getGoods() ;
		try {
			// 广播
			if (!Util.isEmpty(luckGoods.getBroadcast())) {
				String msg = MessageFormat.format(luckGoods.getBroadcast(),
						role.getRoleName());
				GameContext.getChatApp().sendSysMessage(ChatSysName.System,
						ChannelType.Publicize_Personal, msg, null, null);
			}
		}catch(Exception ex){
			logger.error("",ex);
		}
		if(gb.getGoodsType() == GoodsType.GoodsHero.getType()){
			respMsg.setInfo(GameContext.getI18n().messageFormat(TextId.Hero_luck_success_hero,gb.getName()));
		}else{
			respMsg.setInfo(GameContext.getI18n().messageFormat(TextId.Hero_luck_success_goods,
					gb.getName(),String.valueOf(luckGoods.getGoodsNum())));
		}
		if(null != result.getLuckItem()){
			respMsg.setLuckItem(result.getLuckItem());
		}
		respMsg.setStatus(RespTypeStatus.SUCCESS);
		return respMsg;
	}

}
