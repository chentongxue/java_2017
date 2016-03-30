package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0253_MapTransferConfirmReqMessage;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

public class MapTransferConfirmAction extends BaseAction<C0253_MapTransferConfirmReqMessage> {

	@Override
	public Message execute(ActionContext context, C0253_MapTransferConfirmReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		String param = reqMsg.getParam() ;
		Result result = GameContext.getWorldMapApp().transferConfirm(role,param);
		if(result.isSuccess()||result.isIgnore()){
			return null ;
		}
		//return new ErrorRespMessage(reqMsg.getCommandId(),result.getInfo());
		return new C0003_TipNotifyMessage(result.getInfo());
	}

}
