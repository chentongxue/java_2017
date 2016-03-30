package sacred.alliance.magic.action;

import java.util.HashSet;
import java.util.Set;

import com.game.draco.GameContext;
import com.game.draco.message.request.C3904_TradingLockReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class TradingLockAction extends BaseAction<C3904_TradingLockReqMessage>{

	@Override
	public Message execute(ActionContext context, C3904_TradingLockReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		if(0 > reqMsg.getMoneyNum()){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.SYS_Money_Input_Error.getTips());
		}
		String[] goodsList = reqMsg.getGoods();
		//!!!! 判断传入参数是否合法
		if(null != goodsList){
			Set<String> sets = new HashSet<String>();
			for(String id : goodsList){
				if(null == id || sets.contains(id)){
					sets.clear();
					sets = null ;
					return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Sys_Param_Error.getTips());
				}
				sets.add(id);
			}
			sets.clear();
			sets = null ;
		}
		Result result = GameContext.getTradingApp().lock(role, reqMsg.getMoneyNum(), goodsList);
		if(result.isSuccess()){
			return null ;
		}
		return new C0002_ErrorRespMessage(reqMsg.getCommandId(),result.getInfo());
	}

}
