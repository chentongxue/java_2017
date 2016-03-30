package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10060_UserIoSessionKillReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

/**
 * 
 * 无条件删除ioId,此功能只GM工具调用
 *
 */
public class UserIoSessionKillAction extends ActionSupport<C10060_UserIoSessionKillReqMessage>{

	@Override
	public Message execute(ActionContext context, C10060_UserIoSessionKillReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		String userId = reqMsg.getUserId();
		if(null == userId){
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		}
		long ioNum = -1 ;
		Long ioId = GameContext.getOnlineCenter().removeIoId(userId);
		if(null != ioId){
			ioNum = ioId.longValue();
		}
		logger.info("userIoSession kill ,userId=" + userId + " ioId=" + ioNum);
		resp.setType((byte)RespTypeStatus.SUCCESS);
		return resp;
	}

}
