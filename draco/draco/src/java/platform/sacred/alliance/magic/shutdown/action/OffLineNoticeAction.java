package sacred.alliance.magic.shutdown.action;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;
import com.game.draco.message.response.C0107_UserLogoutRespMessage;

import platform.message.request.C5700_ShutdownOffLineNoticeReqMessage;
import platform.message.response.C5700_ShutdownOffLineNoticeRespMessage;
import sacred.alliance.magic.app.chat.ChannelType;
import sacred.alliance.magic.app.chat.ChatSysName;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.RoleInstance;

/**   
*    
* 项目名称：MagicAndScience   
* 类名称：OffLineNoticeAction   
* 类描述：下线消息通知响应协议   
* 创建人：gaojl   
* 创建时间：Sep 25, 2010 5:17:03 PM   
* 修改人：   
* 修改时间：Sep 25, 2010 5:17:03 PM   
* 修改备注：   
* @version    
*    
*/
public class OffLineNoticeAction extends ActionSupport<C5700_ShutdownOffLineNoticeReqMessage>{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Message execute(ActionContext context, C5700_ShutdownOffLineNoticeReqMessage reqMsg) {
		C5700_ShutdownOffLineNoticeRespMessage respMsg = new C5700_ShutdownOffLineNoticeRespMessage();
		respMsg.setType((byte)0);
		try{
			if (reqMsg.getMinutes() == 1) {
				Collection<RoleInstance> roles = GameContext.getOnlineCenter()
						.getAllOnlineRole();
				for (RoleInstance role : roles) {
					C0107_UserLogoutRespMessage resp = new C0107_UserLogoutRespMessage();
					resp.setType((byte) RespTypeStatus.KICK_ROLE);
					role.getBehavior().sendMessage(resp);
					// GameContext.getOnlineCenter().offline(role);
					role.getBehavior().closeNetLink();
				}
				respMsg.setType((byte) 1);
				return respMsg;
			}
			Result result = GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.World, reqMsg.getContent(), null, null);
			respMsg.setType(result.getResult());
		}catch(Exception e){
			logger.error("",e);
			respMsg.setType((byte)0);
		}
		return respMsg;
	}

}
