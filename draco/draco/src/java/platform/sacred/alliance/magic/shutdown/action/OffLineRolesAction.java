package sacred.alliance.magic.shutdown.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import platform.message.request.C5701_ShutDownOffLineRolesReqMessage;
import platform.message.response.C5701_ShutdownOffLineRolesRespMessage;
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
public class OffLineRolesAction extends ActionSupport<C5701_ShutDownOffLineRolesReqMessage>{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public Message execute(ActionContext context, C5701_ShutDownOffLineRolesReqMessage reqMsg) {
		C5701_ShutdownOffLineRolesRespMessage respMsg = new C5701_ShutdownOffLineRolesRespMessage();
		try{
			//GameContext.getUserGoodsApplication().saveAllRole(reqMsg.getType());
			respMsg.setType((byte)1);
		}catch(Exception e){
			logger.error("",e);
			respMsg.setType((byte)0);
		}
		return respMsg;
	}

}
