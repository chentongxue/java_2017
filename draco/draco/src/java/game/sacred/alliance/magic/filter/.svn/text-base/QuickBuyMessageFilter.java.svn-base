package sacred.alliance.magic.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.GameContext;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.filter.Filter;
import sacred.alliance.magic.core.filter.FilterChain;
import sacred.alliance.magic.util.SessionUtil;
import sacred.alliance.magic.vo.RoleInstance;

public class QuickBuyMessageFilter implements Filter {
	
	private static final String USERID = "userid" ;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ActionContext context, FilterChain chain) throws Exception {
		try{
			Message message = context.getMessage();
			short commandId = message.getCommandId();
			if(GameContext.getQuickBuyApp().getCommandIdSet().contains(commandId)){
				RoleInstance role = this.getCurrentRole(context);
				if(null == role){
					return;
				}
				role.setCurrCanQuickBuyReqMessage(message);
			}
		}catch(Exception e){
			this.logger.error("QuickBuyMessageFilter.doFilter error:" + e);
		}finally{
			chain.doFilter(context);
		}
	}

	@Override
	public void init() {
		
	}
	
	private RoleInstance getCurrentRole(ActionContext context){
		String userId = SessionUtil.getUserId(context.getSession());
		if(null == userId){
			return null ;
		}
		return GameContext.getOnlineCenter().getRoleInstanceByUserId(userId);
	}
	
}
