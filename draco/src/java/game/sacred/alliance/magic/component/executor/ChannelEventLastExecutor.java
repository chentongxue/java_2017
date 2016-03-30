package sacred.alliance.magic.component.executor;

import sacred.alliance.magic.channel.ChannelEvent;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.core.executor.KeyOrderedLastExecutor;
import sacred.alliance.magic.util.SessionUtil;

public class ChannelEventLastExecutor extends KeyOrderedLastExecutor<ChannelEvent>{

	protected String __getKey(ChannelEvent entry){
		//从ChannelSession中获得
		ChannelSession session = entry.getSession();
		if(null == session){
			return "" ;
		}
		return SessionUtil.getUserId(session) ;
	}
	
	@Override
	public String getKey(ChannelEvent entry) {
		String eventKey = entry.getEventKey();
		if(null != eventKey && eventKey.trim().length()>0){
			//说明外部手动设置的eventKey
			return eventKey ;
		}
		String key = this.__getKey(entry);
		entry.setEventKey(key);
		return key ;
	}
	
	@Override
	protected void startAction(){
		 this.init();
		 super.startAction();
	}

	private void init(){
	}
	
}
