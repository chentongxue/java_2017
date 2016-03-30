package com.game.draco.app.unionbattle.action;

import java.util.Date;
import java.util.List;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.goods.Util;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.union.domain.Union;
import com.game.draco.app.unionbattle.config.UnionIntegral;
import com.game.draco.app.unionbattle.domain.UnionIntegralState;
import com.game.draco.app.unionbattle.type.IntegralBattleRoundType;
import com.game.draco.app.unionbattle.type.IntegralBattleWeekType;
import com.game.draco.message.item.UnionIntegralBattleFightItem;
import com.game.draco.message.request.C2541_UnionIntegralFightListReqMessage;
import com.game.draco.message.response.C2541_UnionIntegralFightListRespMessage;
import com.google.common.collect.Lists;

/**
 * 查看公会积分战对战列表
 */
public class UnionIntegralFightListAction extends BaseAction<C2541_UnionIntegralFightListReqMessage> {

	@Override
	public Message execute(ActionContext context, C2541_UnionIntegralFightListReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		C2541_UnionIntegralFightListRespMessage respMsg = new C2541_UnionIntegralFightListRespMessage();
		
		UnionIntegral integral = GameContext.getUnionIntegralBattleDataApp().getIntegral();
		
		String openTime = integral.getOpenTime();
		
		byte reqRound = req.getRound();
		if(reqRound == -1){
			reqRound = (byte)GameContext.getUnionIntegralBattleApp().getRound();
		}
		
		List<Date> startDateList = integral.getStartDateList();
		
		if(reqRound >= 0){
			Date date = startDateList.get(reqRound);
			int minutes = DateUtil.getMinutes(date);
			String minu = String.valueOf(minutes);
			if(DateUtil.getMinutes(date) < 10){
				minu = "0" + minutes;
			}
			openTime = DateUtil.getHour(date) + Cat.colon + minu;
		}
		
		Active active = GameContext.getActiveApp().getActive(integral.getActiveId());
		
		String [] weekArr = active.getWeekTerm().split(",");
		
		boolean flag = false;
		
		int sysWeek = DateUtil.getWeek();
		
		for(String w : weekArr){
			if(Util.isEmpty(w)){
				continue;
			}
			if(sysWeek == Integer.parseInt(w)){
				flag = true;
				break;
			}
		}
		String info = GameContext.getI18n().messageFormat(TextId.UNION_INTEGRAL_FIGHT_INFO_NO_DATA);
		
		if(flag){
			String time = GameContext.getI18n().getText(IntegralBattleWeekType.getWeek(sysWeek).getStrWeek());
			
			String round = GameContext.getI18n().getText(IntegralBattleRoundType.get(reqRound).getInfo());
			
			info = GameContext.getI18n().messageFormat(TextId.UNION_INTEGRAL_FIGHT_INFO, time,round,openTime);
		}
		
		respMsg.setRound(reqRound);
		
		List<UnionIntegralState> list = GameContext.getUnionIntegralBattleApp().getUnionIntegralStateRecordList(reqRound);
		if(!flag && !Util.isEmpty(list)){
			info = GameContext.getI18n().messageFormat(TextId.UNION_INTEGRAL_FIGHT_INFO_DATA);
		}
		respMsg.setInfo(info);
		if(Util.isEmpty(list)){
			return respMsg;
		}
		
		List<UnionIntegralBattleFightItem> itemList = Lists.newArrayList();
		for(UnionIntegralState record : list){
			UnionIntegralBattleFightItem item = new UnionIntegralBattleFightItem();
			String unionName = GameContext.getI18n().getText(TextId.UNION_INTEGRAL_FIGHT_BYE);
			if(!record.getUnionId().equals(GameContext.getI18n().getText(TextId.UNION_INTEGRAL_FIGHT_BYE))){
				Union union = GameContext.getUnionApp().getUnion(record.getUnionId());
				if(union != null){
					unionName = union.getUnionName();
				}
			}
			item.setUnionName(unionName);
			item.setGrid((byte)record.getGrid());
			item.setGroupId((byte)record.getGroupId());
			item.setState(record.getState());
			itemList.add(item);
		}
		
		respMsg.setState(active.isTimeOpen());
		respMsg.setList(itemList);
		return respMsg;
	}
	
}
