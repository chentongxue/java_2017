package sacred.alliance.magic.action;
import com.game.draco.GameContext;
import com.game.draco.message.request.C0502_GoodsTidyPackReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.app.goods.behavior.result.GoodsResult;
import sacred.alliance.magic.base.StorageType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;


public class GoodsTidyPackAction extends BaseAction<C0502_GoodsTidyPackReqMessage> {
	@Override
	public Message execute(ActionContext context, C0502_GoodsTidyPackReqMessage reqMsg) {
		try {	
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			StorageType type = StorageType.get(reqMsg.getContainerType());
			GoodsResult result = GameContext.getUserGoodsApp().reorganization(role, type);
			if(!result.isSuccess()){
				C0002_ErrorRespMessage resp = new C0002_ErrorRespMessage();
				resp.setInfo(result.getInfo());
				return resp ;
			}
			return null;
		} catch (Exception e) {
			logger.error("GoodsTidyPackAction error", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
					this.getText(TextId.SYSTEM_ERROR));
		}
	}
}
