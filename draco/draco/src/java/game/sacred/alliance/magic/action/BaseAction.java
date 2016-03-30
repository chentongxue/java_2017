package sacred.alliance.magic.action;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.SessionUtil;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public abstract class BaseAction<M extends Message> extends ActionSupport<M> {
	protected static final int NOTALLOWEMULATOR = 1 ;
	
	protected RoleInstance getCurrentRole(ActionContext context){
		String userId = this.getUserId(context);
		if(null == userId){
			return null ;
		}
		return GameContext.getOnlineCenter().getRoleInstanceByUserId(userId);
	}
	
	protected String getRoleId(ActionContext context){
		int roleId = SessionUtil.getRoleId(context.getSession()); 
		if(0 == roleId){
			return null ;
		}
		return String.valueOf(roleId);
	}

	protected String getUserId(ActionContext context){
		return SessionUtil.getUserId(context.getSession());
	}
	
	
	protected String getText(String textId){
		return GameContext.getI18n().getText(textId);
	}
	
	protected String messageFormat(String textId,Object... args){
		return GameContext.getI18n().messageFormat(textId,args);
	}
	
}
