package sacred.alliance.magic.action;

import sacred.alliance.magic.base.ChangeMapEvent;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.domain.GoodsTreasure;
import sacred.alliance.magic.domain.RoleGoods;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0515_GoodsTreasureTransReqMessage;
/**
 * 虚空漩涡（藏宝图）
 * 
 * */
public class GoodsTreasureTransAction extends BaseAction<C0515_GoodsTreasureTransReqMessage> {

	@Override
	public Message execute(ActionContext context, C0515_GoodsTreasureTransReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		RoleGoods roleGoods = role.getRoleBackpack().getRoleGoodsByInstanceId(req.getParam());
		if(roleGoods == null){
			return null;
		}
		//判断是否正确的点 
		GameContext.getTreasureApp().handleWrongPoint(role, roleGoods);
		
		int goodsId = roleGoods.getGoodsId();
		GoodsTreasure goodTreasure = (GoodsTreasure)(GameContext.getGoodsApp().getGoodsBase(goodsId));
		String[] otherParam = Util.splitString(roleGoods.getOtherParm());
		Point tarPoint = new Point(otherParam[0], Integer.parseInt(otherParam[1]),
				Integer.parseInt(otherParam[2]),ChangeMapEvent.treasure.getEventType());
		
		Result result =GameContext.getWorldMapApp().transfer(role, tarPoint,goodTreasure.getTransGold());
		if(null == result || result.isSuccess() || result.isIgnore()){
			return null ;
		}
		return this.buildErrorMsg(result.getInfo()); 
	}
	
	private Message buildErrorMsg(String info){
		C0003_TipNotifyMessage errorMsg = new C0003_TipNotifyMessage();
		errorMsg.setMsgContext(info);
		return errorMsg;
	}

}
