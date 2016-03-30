package sacred.alliance.magic.app.attri.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.attri.config.ExpHookClean;
import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0116_RoleHookExecReqMessage;

public class RoleHookExecAction extends BaseAction<C0116_RoleHookExecReqMessage>{

	@Override
	public Message execute(ActionContext context, C0116_RoleHookExecReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		if(1 == reqMsg.getNotPrompt()){
			//不再弹板
			role.setHookExpFlag((byte)1);
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
		ExpHookClean config = GameContext.getAttriApp().getExpHookCleanByVip(vipLevel);
		if(null == config){
			return null ;
		}
		RoleCount rc = role.getRoleCount() ;
		int cleanTimes = rc.getRoleTimesToInt(CountType.TodayHookCleanTimes);//getTodayHookCleanTimes();

		ExpHookClean consumeConfig = GameContext.getAttriApp().getExpHookClean(cleanTimes+1);
		if(null == consumeConfig){
			return null ;
		}
		if(cleanTimes < config.getTimes() &&
				role.getGoldMoney() >= consumeConfig.getRmbMoney()){
			//扣除游戏币,同时更新疲劳度
			GameContext.getUserAttributeApp().changeRoleMoney(role, AttributeType.goldMoney,
					OperatorType.Decrease, consumeConfig.getRmbMoney(), OutputConsumeType.role_fatigue_clean_consume);
			rc.changeTimes(CountType.TodayHookCleanTimes, cleanTimes+1);
			rc.changeTimes(CountType.TodayHookExp, 0);
			role.getBehavior().notifyAttribute();
			C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
			message.setMsgContext(GameContext.getI18n().getText(TextId.Role_hook_exp_clean_success));
			return message ;
		}
		return null;
	}
	
}
