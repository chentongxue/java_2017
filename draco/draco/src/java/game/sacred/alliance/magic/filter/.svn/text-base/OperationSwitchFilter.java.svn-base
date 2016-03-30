package sacred.alliance.magic.filter;

import com.game.draco.GameContext;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.filter.Filter;
import sacred.alliance.magic.core.filter.FilterChain;

public class OperationSwitchFilter implements Filter{

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ActionContext context, FilterChain chain)
			throws Exception {
		try{
			int commandId = context.getMessage().getCommandId();
			String strCmd = "," + GameContext.getParasConfig().getForbidCommandId() + "," ;
			if(strCmd.indexOf("," + commandId + ",")>=0){
				C0002_ErrorRespMessage msg = new C0002_ErrorRespMessage();
				msg.setType((byte)0);
				msg.setInfo(GameContext.getI18n().getText(TextId.FORBID_MESSAGE));
				context.getSession().write(msg);
				return ;
			}
		}catch(Exception ex){
			
		}
		chain.doFilter(context);
	}

	@Override
	public void init() {
		
	}

}
