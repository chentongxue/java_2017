package sacred.alliance.magic.app.quickbuy;

import com.game.draco.GameContext;
import com.game.draco.message.request.C2801_ChargeMoneyListReqMessage;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.RoleInstance;

public class QuickCostHelper {
	
	/** 打开充值界面的命令字 */
	public static final short Charge_CmdId = new C2801_ChargeMoneyListReqMessage().getCommandId();
	
	/**
	 * 构建消耗金条绑金的二次确认消息
	 * @param role 角色对象
	 * @param goldCmdId 金条命令字
	 * @param goldParam 金条参数
	 * @param bindCmdId 绑金命令字
	 * @param bindParam 绑金参数
	 * @param preInfo 提示信息前缀
	 * @param goldMoney 所需金条
	 * @param bindMoney 所需绑金
	 * @param cmdId 命令字
	 * @return 消耗金条、绑金的二次确认消息（-8或-9）
	 */
	public static Message getMessage(RoleInstance role, short goldCmdId, String goldParam, 
			short bindCmdId, String bindParam,String preInfo, int goldMoney, int bindMoney) {
		if(goldMoney <= 0 && bindMoney <=0 ){
			return null;
		}
		String tipInfo = preInfo + (goldMoney>0?goldMoney+ AttributeType.goldMoney.getName():"");
		if(goldMoney > 0 && bindMoney >0 ){
			tipInfo += "," + GameContext.getI18n().getText(TextId.QUICK_COST_PAY) + ":";
		}
		return QuickCostHelper.getMessage(role, goldCmdId, goldParam, bindCmdId, bindParam, goldMoney, bindMoney, tipInfo);
	}
	
	/**
	 * 构建消耗金条绑金的二次确认消息
	 * @param role 角色对象
	 * @param goldCmdId 金条命令字
	 * @param goldParam 金条参数
	 * @param bindCmdId 绑金命令字
	 * @param bindParam 绑金参数
	 * @param goldMoney 所需金条
	 * @param bindMoney 所需绑金
	 * @param tipInfo 提示信息
	 * @return 消耗金条、绑金的二次确认消息（-8或-9）
	 */
	public static Message getMessage(RoleInstance role, short goldCmdId, String goldParam, 
			short bindCmdId, String bindParam, int goldMoney, int bindMoney, String tipInfo) {
		if(goldMoney <= 0 && bindMoney <=0 ){
			return null;
		}
		QuickCost quickCost = new QuickCost();
		quickCost.setGoldMoney(goldMoney);
		quickCost.setBindMoney(bindMoney);
		quickCost.setTips(tipInfo);
		quickCost.setGoldCmdId(goldCmdId);
		quickCost.setGoldParam(goldParam);
		quickCost.setBindCmdId(bindCmdId);
		quickCost.setBindParam(bindParam);
		return quickCost.getMessage(role);
	}
	
}
