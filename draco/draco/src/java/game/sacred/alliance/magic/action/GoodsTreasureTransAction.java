package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0515_GoodsTreasureTransReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.ChangeMapEvent;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsTreasure;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public class GoodsTreasureTransAction extends BaseAction<C0515_GoodsTreasureTransReqMessage> {

	@Override
	public Message execute(ActionContext context, C0515_GoodsTreasureTransReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		String[] params = Util.splitString(req.getParam());
		int paramsLength = params.length;
		String goodsInstanceId = params[paramsLength - 1];
		RoleGoods roleGoods = role.getRoleBackpack().getRoleGoodsByInstanceId(goodsInstanceId);
		if(roleGoods == null){
			return null;
		}
		//判断是否正确的点 
		GameContext.getTreasureApp().handleWrongPoint(role, roleGoods);
		
		int goodsId = roleGoods.getGoodsId();
		GoodsTreasure goodTreasure = (GoodsTreasure)(GameContext.getGoodsApp().getGoodsBase(goodsId));
		
		String[] otherParam = Util.splitString(roleGoods.getOtherParm());

		Point tarPoint = new Point(otherParam[0], Integer.parseInt(otherParam[1]),
				Integer.parseInt(otherParam[2]),ChangeMapEvent.worldmap.getEventType());
		if(paramsLength == 1){
			Message message = GameContext.getTreasureApp()
				.triggerCostMessage(role, goodsInstanceId, goodTreasure.getTransGold(), goodTreasure.getTransBindingGold());
			if(null != message){
				return message;
			}
			Result result = GameContext.getTreasureApp().transferTargetPoint(role, null, goodTreasure.getTransGold()
					, goodTreasure.getTransBindingGold(), tarPoint);
			if(!result.isSuccess()){
				return this.buildErrorMsg(result.getInfo());
			}
			return null ;
		}
		 if(paramsLength == 2){
			AttributeType type = null;
			byte attrType = Byte.parseByte(params[0]);
			if(attrType == AttributeType.bindingGoldMoney.getType()){
				type = AttributeType.bindingGoldMoney;
			}else if(attrType == AttributeType.goldMoney.getType()){
				type = AttributeType.goldMoney;
			}
			if(null == type){
				return this.buildErrorMsg(Status.Sys_Error.getTips());
			}
			Result result = GameContext.getTreasureApp().transferTargetPoint(role, type, goodTreasure.getTransGold()
					, goodTreasure.getTransBindingGold(), tarPoint);
			if(!result.isSuccess()){
				return this.buildErrorMsg(result.getInfo());
			}
		}
		return null;
	}
	
	private Message buildErrorMsg(String info){
		C0002_ErrorRespMessage errorMsg = new C0002_ErrorRespMessage();
		errorMsg.setInfo(info);
		return errorMsg;
	}

}
