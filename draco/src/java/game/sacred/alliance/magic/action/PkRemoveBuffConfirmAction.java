package sacred.alliance.magic.action;

import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.pk.PkConfig;
import sacred.alliance.magic.app.pk.PkKillConfig;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1527_PkRemoveBuffConfirmReqMessage;

public class PkRemoveBuffConfirmAction extends BaseAction<C1527_PkRemoveBuffConfirmReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C1527_PkRemoveBuffConfirmReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		String moneyType = reqMsg.getMoneyType();
		if(!Util.isNumeric(moneyType)){
			return new C0003_TipNotifyMessage(this.getText(TextId.ERROR_INPUT));
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
		
		PkKillConfig pkKillConfig = GameContext.getPkApp().getPkKillConfig(role.getRoleCount().getRoleTimesToInt(CountType.KillCount));//.getKillCount());
		if(null == pkKillConfig){
			return new C0003_TipNotifyMessage(this.getText(TextId.PK_BUFF_TOO_SHORT));
		}
		
		AttributeType attri = AttributeType.goldMoney;
		int consumeMoney = (int)((double)remainTime/1000/60 * pkKillConfig.getGoldMoney()) ;
		//【游戏币/潜能/钻石不足弹板】 判断
		Result ar = GameContext.getUserAttributeApp().getEnoughResult(role, attri, consumeMoney);
		if(ar.isIgnore()){//弹板
			return null;
		}
		if(!ar.isSuccess()){//不足
			return new C0003_TipNotifyMessage(this.messageFormat(TextId.NOT_ENOUGH_ATTRIBUTE,attri.getName()));
		}

//		判断钱是否足够
//		if(role.get(attri) < consumeMoney){
//			return new C0003_TipNotifyMessage(this.messageFormat(TextId.NOT_ENOUGH_ATTRIBUTE,attri.getName()));
//		}
		//扣除元宝
		GameContext.getUserAttributeApp().changeRoleMoney(role, attri,
				OperatorType.Decrease, consumeMoney, OutputConsumeType.pk_remove_buff);
		role.getBehavior().notifyAttribute() ;
		
		//清除buff
		GameContext.getUserBuffApp().cleanBuffById(role, buffId, 0);
		role.getRoleCount().changeTimes(CountType.KillCount,0);//setKillCount(0);
		GameContext.getPkApp().changeColorByRemove(role);
		return new C0003_TipNotifyMessage(this.getText(TextId.PK_BUFF_REMOVE_SUCCESS));
	}

}
