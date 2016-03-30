package sacred.alliance.magic.filter;

import com.game.draco.GameContext;
import com.game.draco.message.internal.InternalMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.filter.Filter;
import sacred.alliance.magic.core.filter.FilterChain;

public class RefuseRequestFilter implements Filter{

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ActionContext context, FilterChain chain)
			throws Exception {
		try{
			if(GameContext.getShutDownApp().isRefuseRequest()){
				Message message = context.getMessage();
				if(null == message){
					return ;
				}
				
				if(!(message instanceof InternalMessage)){
					//非内部消息拒绝请求直接返回
					return ;
				}
				//内部消息根据本同消息采取不同策略
				InternalMessage inMsg = (InternalMessage)message;
				if(inMsg.isCanRefuse()){
					return ;
				}
			}
		}catch(Exception ex){
		}
		chain.doFilter(context);
	}

	@Override
	public void init() {
		
	}

}
