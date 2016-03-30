package sacred.alliance.magic.app.attri.action;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.app.attri.config.ExpHookClean;
import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C0007_ConfirmationNotifyMessage;
import com.game.draco.message.request.C0117_RoleHookExpCleanReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

public class RoleHookExpCleanAction extends BaseAction<C0117_RoleHookExpCleanReqMessage>{

	private final short HOOK_EXP_CLEAN_CMDID = new C0117_RoleHookExpCleanReqMessage().getCommandId();
	@Override
	public Message execute(ActionContext context,
			C0117_RoleHookExpCleanReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if (null == role) {
			return null;
		}
		boolean isConfirm = !Util.isEmpty(reqMsg.getInfo());
		int per = (int) (role.get(AttributeType.expHook)
				/ (float) role.get(AttributeType.maxExpHook) * 100);
		per = Math.min(per, 100);

		// 判断是否vip
		int vipLevel = GameContext.getVipApp().getVipLevel(role);
		int minVip = 1;
		ExpHookClean config = GameContext.getAttriApp().getExpHookClean(1);
		if (null != config) {
			minVip = config.getVipLevel();
		}
		if (vipLevel <= config.getVipLevel()) {
			// 1. 未开启vip
			return this.buildPromptMessage(
					GameContext.getI18n().messageFormat(
							TextId.Role_hook_exp_clean_not_vip,
							String.valueOf(minVip), String.valueOf(per)),
					isConfirm);
		}
		if(per <=0){
			return this.buildPromptMessage(GameContext.getI18n().getText(TextId.Role_hook_exp_clean_not_need_do),isConfirm) ;
		}
		config = GameContext.getAttriApp().getExpHookCleanByVip(vipLevel);
		if (null == config) {
			return null;
		}
		RoleCount rc = role.getRoleCount();
		int cleanTimes = rc.getRoleTimesToInt(CountType.TodayHookCleanTimes);//getTodayHookCleanTimes();
		if(cleanTimes >= config.getTimes()){
			//没有剩余次数
			//达到最大vip
			if(config.isMax()){
				return this.buildPromptMessage(GameContext.getI18n().messageFormat(
						TextId.Role_hook_exp_clean_not_times_and_maxvip, String.valueOf(per)), 
						isConfirm) ;
			}
			//未达到最大vip
			return this.buildPromptMessage(GameContext.getI18n().messageFormat(
					TextId.Role_hook_exp_clean_not_times_and_not_maxvip, String.valueOf(per)), 
					isConfirm) ;
		}
		int maxTime = config.getTimes() ;
		config = GameContext.getAttriApp().getExpHookClean(cleanTimes +1);
		//已经开启vip,并且有剩余次数
		if (role.getGoldMoney() < config.getRmbMoney()) {
				// 2.1 钻石不够
				return this.buildPromptMessage(GameContext.getI18n().messageFormat(
												TextId.Role_hook_exp_tips_rmbmoney_not_enough,
												String.valueOf(role.getGoldMoney()),
												String.valueOf(config.getRmbMoney()),
												String.valueOf(per)),
												isConfirm);
		}
		if(!isConfirm){
				//二次确认面板
			return this.buildConfirmMessage(GameContext.getI18n().messageFormat(
						TextId.Role_hook_exp_clean_rmbmoney_enough,
						String.valueOf(config.getRmbMoney()),
						String.valueOf(cleanTimes + 1),
						String.valueOf(maxTime),
						String.valueOf(per)), HOOK_EXP_CLEAN_CMDID, "1");
		}
		// 扣钱,清除疲劳度
		// 扣除游戏币,同时更新疲劳度
		GameContext.getUserAttributeApp().changeRoleMoney(role,
				AttributeType.goldMoney, OperatorType.Decrease,
				config.getRmbMoney(),
				OutputConsumeType.role_fatigue_clean_consume);
		rc.changeTimes(CountType.TodayHookCleanTimes,cleanTimes + 1);
		rc.changeTimes(CountType.TodayHookExp, 0);//setTodayHookExp(0);
		role.getBehavior().notifyAttribute();
		C0003_TipNotifyMessage message = new C0003_TipNotifyMessage();
		message.setMsgContext(GameContext.getI18n().getText(
				TextId.Role_hook_exp_clean_success));
		return message;
		
	}
	
	private Message buildPromptMessage(String tips,boolean isConfirm){
		if(isConfirm){
			return null ;
		}
		C0002_ErrorRespMessage respMsg = new C0002_ErrorRespMessage();
		respMsg.setInfo(tips);
		return respMsg ;
	}
	
	private  Message buildConfirmMessage(String tips,
			short affirmCmdId,String affirmInfo){
		C0007_ConfirmationNotifyMessage message = new C0007_ConfirmationNotifyMessage() ;
		message.setAffirmCmdId(affirmCmdId);
		message.setAffirmParam(affirmInfo);
		message.setInfo(tips);
		return message ;
	}

}
