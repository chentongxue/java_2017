package sacred.alliance.magic.action;

import sacred.alliance.magic.app.token.AccountToken;
import sacred.alliance.magic.app.token.TokenResult;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.executor.annotation.ExecutorMapping;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.base.ExecutorBean;
import com.game.draco.message.internal.C0056_UserLoginInternalMessage;
import com.game.draco.message.request.C4999_UserLoginSafeReqMessage;
import com.game.draco.message.response.C4999_UserLoginSafeRespMessage;

@ExecutorMapping(name=ExecutorBean.loginExecutor)
public class UserLoginSafeAction extends BaseAction<C4999_UserLoginSafeReqMessage>{

	private final byte TOKEN_ERROR = 2;
	
	private String getProtoLowTips(int channelId){
		return this.getText(TextId.Proto_Version_Too_Low_Prefix + String.valueOf(channelId));
	}
	
	@Override
	public Message execute(ActionContext context,
			C4999_UserLoginSafeReqMessage reqMsg) {
    	C4999_UserLoginSafeRespMessage respMsg = new C4999_UserLoginSafeRespMessage();
		//判断协议版本
        if(reqMsg.getProtoVersion()<GameContext.PROTO_VERSION){
        	respMsg.setType(Status.Role_FAILURE.getInnerCode());
        	String tip = this.getProtoLowTips(reqMsg.getChannelId());
        	if(Util.isEmpty(tip)){
        		//如果未配置相应的渠道则返回默认渠道信息
        		tip = this.getProtoLowTips(-1);
        	}
        	if(Util.isEmpty(tip)){
        		tip = Status.Role_Login_Proto_Low.getTips() ;
        	}
        	respMsg.setInfo(tip);
        	return respMsg;
        }
        TokenResult result = GameContext.getTokenApp().loginCheck(reqMsg);
    	if(!result.isSuccess()){
        	respMsg.setType(TOKEN_ERROR);
    		respMsg.setInfo(result.getInfo());
    		return respMsg;
    	}
    	//判断渠道
        int loginChannelId = reqMsg.getChannelId();
        int userChannelId = result.getAccountToken().getChannelId();
        if(loginChannelId != userChannelId){
        	respMsg.setType(Status.Role_FAILURE.getInnerCode());
        	respMsg.setInfo(this.getText(TextId.Role_Login_OsType_Not_Allow));
        	return respMsg;
        }
        //判断serverId是否是数字
        if(!Util.isNumber(reqMsg.getServerId())){
        	respMsg.setType(Status.Role_FAILURE.getInnerCode());
        	respMsg.setInfo(this.getText(TextId.ERROR_INPUT));
        	return respMsg;
        }
    	this.login(context, result, reqMsg);
		return null ;
	
	}
	
	protected void login(ActionContext context, TokenResult result, C4999_UserLoginSafeReqMessage reqMsg){
		AccountToken accountToken = result.getAccountToken();
		//避免麻烦的多线程问题
		//将逻辑放入单用户单线程中执行器中处理
		String userId = String.valueOf(accountToken.getUserId());
		C0056_UserLoginInternalMessage internalReqMsg = new C0056_UserLoginInternalMessage();
		internalReqMsg.setUserId(userId);
		internalReqMsg.setAccountToken(accountToken);
		internalReqMsg.setUserReqMsg(reqMsg);
		
		GameContext.getUserSocketChannelEventPublisher().publish(userId, 
				internalReqMsg, context.getSession());
	}
	
}
