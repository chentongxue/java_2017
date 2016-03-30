package sacred.alliance.magic.app.quickbuy;

import lombok.Data;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.push.C0008_GoldAndBindChooseNotifyMessage;
import com.game.draco.message.push.C0009_GoldOrBindConfirmNotifyMessage;

public @Data class QuickCost {
	
	private int goldMoney;//消耗金条 <=0表示不支持
	private int bindMoney;//消耗绑金 <=0表示不支持
	private String tips;//提示信息
	private short goldCmdId;
	private String goldParam;
	private short bindCmdId;
	private String bindParam;
	
	/**
	 * 金条绑金消耗确认消息
	 * @param role
	 */
	public Message getMessage(RoleInstance role){
		//支持金条和绑金两种货币，发-8
		if(this.isGoldAndBindChoose()){
			C0008_GoldAndBindChooseNotifyMessage chooseMsg = new C0008_GoldAndBindChooseNotifyMessage();
			if(role.getGoldMoney() >= this.goldMoney){
				chooseMsg.setGoldCmdId(this.goldCmdId);
				chooseMsg.setGoldParam(this.goldParam);
			}else{
				//元宝不足让客户端打开充值界面
				if(GameContext.getChargeApp().isPayOpen()){
					chooseMsg.setGoldCmdId(QuickCostHelper.Charge_CmdId);
				}else{
					C0003_TipNotifyMessage notifyMsg = new C0003_TipNotifyMessage() ;
					notifyMsg.setMsgContext(GameContext.getI18n().getText(TextId.NOT_ENOUGH_GOLD_MONEY));
				}
			}
			/*if(role.getBindingGoldMoney() >= this.bindMoney){
				chooseMsg.setBindCmdId(this.bindCmdId);
				chooseMsg.setBindParam(this.bindParam);
			}*/
			chooseMsg.setTipInfo(this.tips);
			return chooseMsg;
		}
		//支持金条或绑金的一种货币，发-9
		C0009_GoldOrBindConfirmNotifyMessage confirmMsg = new C0009_GoldOrBindConfirmNotifyMessage();
		confirmMsg.setInfo(this.tips);
		//如果是金条
		if(this.onlyGoldMoney()){
			if(role.getGoldMoney() >= this.goldMoney){
				confirmMsg.setCmdId(this.goldCmdId);
				confirmMsg.setParam(this.goldParam);
			}else{
				//元宝不足让客户端打开充值界面
				if(GameContext.getChargeApp().isPayOpen()){
					confirmMsg.setCmdId(QuickCostHelper.Charge_CmdId);
				}else{
					C0003_TipNotifyMessage notifyMsg = new C0003_TipNotifyMessage() ;
					notifyMsg.setMsgContext(GameContext.getI18n().getText(TextId.NOT_ENOUGH_GOLD_MONEY));
					return notifyMsg;
				}
			}
			confirmMsg.setGoldType((byte)1);
			return confirmMsg;
		}
		//如果是绑金
		/*if(role.getBindingGoldMoney() >= this.bindMoney){
			confirmMsg.setCmdId(this.bindCmdId);
			confirmMsg.setParam(this.bindParam);
		}*/
		confirmMsg.setGoldType((byte) 0);
		return confirmMsg;
	}
	
	/** 金条绑金都支持 用户可进行选择 */
	private boolean isGoldAndBindChoose(){
		return this.goldMoney > 0 && this.bindMoney >0;
	}
	
	/** 仅仅支持金条付费 **/
	private boolean onlyGoldMoney(){
		return this.goldMoney > 0 && this.bindMoney <= 0;
	}
	
}
