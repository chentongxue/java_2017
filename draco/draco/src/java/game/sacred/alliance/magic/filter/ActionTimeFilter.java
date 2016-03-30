package sacred.alliance.magic.filter;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.filter.Filter;
import sacred.alliance.magic.core.filter.FilterChain;
import sacred.alliance.magic.util.Log4jManager;

public class ActionTimeFilter implements Filter{

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ActionContext context, FilterChain chain)
			throws Exception {
		long start = System.currentTimeMillis();
		int commandId = context.getMessage().getCommandId();
		chain.doFilter(context);
		long end = System.currentTimeMillis();
		if(Log4jManager.AT_LOG.isInfoEnabled()){
			Log4jManager.AT_LOG.info(commandId + " " + (end-start));
		}
	}

	@Override
	public void init() {
		
	}

}
