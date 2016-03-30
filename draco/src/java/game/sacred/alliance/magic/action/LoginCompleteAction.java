package sacred.alliance.magic.action;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3300_LoginCompleteReqMessage;

public class LoginCompleteAction extends BaseAction<C3300_LoginCompleteReqMessage> {

	@Override
	public Message execute(ActionContext context, C3300_LoginCompleteReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		role.setLoginCompleted(true);
		//判断是否push选择阵营的面板
		//boolean pushSelectCamp = GameContext.getCampBalanceApp().pushToSelectCampMessage(role);
		boolean pushSelectCamp = false ;
		//目标系统提示信息
		GameContext.getTargetApp().pushTargetHintMessage(role);
		if(!pushSelectCamp){
			this.pushFirstUI(role);
		}
		// 通知客户端红点提示UI树
		GameContext.getHintApp().pushHintUITreeMessage(role);
		//hitCombo配置
		GameContext.getHitComboApp().pushHitComboConfig(role);
		// 通知世界等级
		GameContext.getWorldLevelApp().pushRatioChange(role);
		return null ;
	}

	private void pushFirstUI(RoleInstance role) {
		//有登录奖励push领取面板，没有push活跃度面板
		if (GameContext.getAccumulateLoginApp().autoPushUI(role)) {
			return;
		}
		//role.getBehavior().addEvent(new C1920_DailyPlayPeqMessage());
	}
}
