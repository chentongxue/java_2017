package sacred.alliance.magic.app.attri.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.attri.config.ExpHookClean;
import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0115_RoleHookReqMessage;
import com.game.draco.message.response.C0115_RoleHookRespMessage;

public class RoleHookAction extends BaseAction<C0115_RoleHookReqMessage>{

	@Override
	public Message execute(ActionContext context, C0115_RoleHookReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		if(1 == role.getHookExpFlag()){
			//不再弹板
			return null ;
		}
		//判断当前地图是否普通地图
		MapInstance mapInstance = role.getMapInstance() ;
		if(null == mapInstance 
				|| mapInstance.mapLogicType() != MapLogicType.defaultLogic.getType()){
			return null ;
		}
		//判断是否到达的疲劳度上限
		if(role.get(AttributeType.expHook) != role.get(AttributeType.maxExpHook)){
			return null ;
		}
		//判断是否vip
		int vipLevel = GameContext.getVipApp().getVipLevel(role);
		ExpHookClean config = GameContext.getAttriApp().getExpHookClean(1);
		if(null == config){
			return null ;
		}
		if(vipLevel <= config.getVipLevel()){
			//1. 未开启vip
			return this.buildRespMessage(GameContext.getI18n().messageFormat(
					TextId.Role_hook_exp_tips_not_vip, String.valueOf(config.getVipLevel())), false);
		}
		config = GameContext.getAttriApp().getExpHookCleanByVip(vipLevel);
		if(null == config){
			return null ;
		}
		RoleCount rc = role.getRoleCount() ;
		int cleanTimes = rc.getRoleTimesToInt(CountType.TodayHookCleanTimes);//getTodayHookCleanTimes();
		if(cleanTimes < config.getTimes()){
			//2. 已经开启vip,并且有剩余次数
			if(role.getGoldMoney() < config.getRmbMoney()){
				//2.1 钻石不够
				return this.buildRespMessage(GameContext.getI18n().messageFormat(
						TextId.Role_hook_exp_tips_rmbmoney_not_enough,
						String.valueOf(role.getGoldMoney()),
						String.valueOf(config.getRmbMoney()),
						String.valueOf(config.getTimes()-cleanTimes)), 
						false);
			}
			//2.2 钻石够
			return this.buildRespMessage(GameContext.getI18n().messageFormat(
					TextId.Role_hook_exp_tips_rmbmoney_enough,
					String.valueOf(config.getRmbMoney()),
					String.valueOf(cleanTimes+1),
					String.valueOf(config.getTimes())), 
					true);
		}
		//3. 已经开启vip,没有剩余次数
		//3.1 未达到最大消除级别
		if(!config.isMax()){
			return this.buildRespMessage(GameContext.getI18n().getText(
					TextId.Role_hook_exp_tips_not_times_and_not_maxvip), 
					false);
		}
		return null;
	}

	
	private Message buildRespMessage(String tips,boolean haveCancelbutton){
		C0115_RoleHookRespMessage respMsg = new C0115_RoleHookRespMessage();
		respMsg.setTips(tips);
		respMsg.setHaveCancelbutton(haveCancelbutton?(byte)1:(byte)0);
		return respMsg ;
	}
}
