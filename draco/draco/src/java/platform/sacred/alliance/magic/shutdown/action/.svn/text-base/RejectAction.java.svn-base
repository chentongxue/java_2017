package sacred.alliance.magic.shutdown.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import platform.message.request.C5702_ShutdownRejectReqMessage;
import platform.message.response.C5702_ShutdownRejectRespMessage;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;

/**   
*    
* 项目名称：MagicAndScience   
* 类名称：ShutDownAction   
* 类描述：接受入库消息的Action   
* 创建人：gaojl   
* 创建时间：Sep 8, 2010 11:57:30 AM   
* 修改人：   
* 修改时间：Sep 8, 2010 11:57:30 AM   
* 修改备注：   
* @version    
*    
*/
public class RejectAction extends ActionSupport<C5702_ShutdownRejectReqMessage>{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Message execute(ActionContext context, C5702_ShutdownRejectReqMessage reqMsg) {
		C5702_ShutdownRejectRespMessage respMsg = new C5702_ShutdownRejectRespMessage();
		try{
			if(1 == reqMsg.getType()){
				GameContext.getShutDownApplication().setRefuseRequest();
			}else{
				GameContext.getShutDownApplication().setAcceptRequest();
			}
			respMsg.setType((byte)1);
		}catch(Exception e){
			logger.error("",e);
			respMsg.setType((byte)0);
		}
		return respMsg;
	}

}
