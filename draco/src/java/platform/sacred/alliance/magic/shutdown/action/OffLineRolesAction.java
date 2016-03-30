package sacred.alliance.magic.shutdown.action;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10071_ShutdownOffLineRolesReqMessage;
import com.game.draco.debug.message.response.C10071_ShutdownOffLineRolesRespMessage;

 

public class OffLineRolesAction extends ActionSupport<C10071_ShutdownOffLineRolesReqMessage>{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Message execute(ActionContext context, C10071_ShutdownOffLineRolesReqMessage reqMsg) {
		C10071_ShutdownOffLineRolesRespMessage respMsg = new C10071_ShutdownOffLineRolesRespMessage();
		try{
			Collection<RoleInstance> roles = GameContext.getOnlineCenter().getAllOnlineRole();
			for (RoleInstance role : roles) {
				GameContext.getUserGoodsApp().saveRoleAllGoods(role);
			}
			respMsg.setType(Result.SUCCESS);
		}catch(Exception e){
			logger.error("",e);
			respMsg.setType((Result.FAIL));
		}
		return respMsg;
	}

}
