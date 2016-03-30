package sacred.alliance.magic.action;

import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.pk.PkConfig;
import sacred.alliance.magic.app.pk.PkKillConfig;
import sacred.alliance.magic.app.quickbuy.QuickCostHelper;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1526_PkRemoveBuffReqMessage;
import com.game.draco.message.request.C1527_PkRemoveBuffConfirmReqMessage;

public class PkRemoveBuffAction extends BaseAction<C1526_PkRemoveBuffReqMessage> {

	private final short REMOVE_BUFF_EXEC_CMDID = new C1527_PkRemoveBuffConfirmReqMessage().getCommandId() ;
	@Override
	public Message execute(ActionContext context, C1526_PkRemoveBuffReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		PkConfig config = GameContext.getPkApp().getPkConfig();
		if(null == config){
			return new C0003_TipNotifyMessage(this.getText(TextId.ERROR_INPUT));
		}
		short buffId = config.getBuffId();
		BuffStat buffStat = role.getBuffStat(buffId);
		if(null == buffStat){
			return new C0003_TipNotifyMessage(this.getText(TextId.PK_NO_BUFF));
		}
		
		int remainTime = buffStat.getRemainTime();
		if(remainTime <= 1000){
			return new C0003_TipNotifyMessage(this.getText(TextId.PK_BUFF_TOO_SHORT));
		}
		
		PkKillConfig pkKillConfig = GameContext.getPkApp().getPkKillConfig(role.getRoleCount().getRoleTimesToInt(CountType.KillCount));//getKillCount());
		if(null == pkKillConfig){
			return new C0003_TipNotifyMessage(this.getText(TextId.PK_BUFF_TOO_SHORT));
		}
		
		int goldMoney = (int)((double)remainTime/1000/60 * pkKillConfig.getGoldMoney());
		int bindingGoldMoney = (int)((double)remainTime/1000/60 * pkKillConfig.getBindingGoldMoney());
		if(goldMoney < 1){
			goldMoney = 1 ;
		}
		if(bindingGoldMoney < 1){
			bindingGoldMoney = 1 ;
		}
		return QuickCostHelper.getMessage(role, REMOVE_BUFF_EXEC_CMDID,
				String.valueOf("0"), REMOVE_BUFF_EXEC_CMDID, String.valueOf("1"), goldMoney, bindingGoldMoney,
				this.messageFormat(TextId.PK_BUFF_REMOVE_CONFIRM,goldMoney,bindingGoldMoney)) ;
	}
}
