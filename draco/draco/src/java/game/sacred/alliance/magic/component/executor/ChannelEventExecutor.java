package sacred.alliance.magic.component.executor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import platform.message.request.C5901_ChargeNotifyReqMessage;
import sacred.alliance.magic.channel.ChannelEvent;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.core.executor.KeyOrderedExExecutor;

public class ChannelEventExecutor extends KeyOrderedExExecutor<ChannelEvent>{
	private final static String USER_KEY = "userid" ;
	private Set<String> cumulateSet = new HashSet<String>();
	private List<String> cumulateList = null ;
	private short CHARGE_NOTIFY_CMDID = new C5901_ChargeNotifyReqMessage().getCommandId();
	

	public void setCumulateList(List<String> cumulateList) {
		this.cumulateList = cumulateList;
	}

	@Override
	public boolean acceptCumulate(ChannelEvent event) {
		Message message = this.getMessage(event);
		if(null == message){
			return false ;
		}
		//事件已经指明可以重复
		if(event.isCumulate()){
			return true ;
		}
		String cmd = String.valueOf(message.getCommandId());
		return this.cumulateSet.contains(cmd);
	}
	
	
	protected String __getKey(ChannelEvent entry){
		Message message = this.getMessage(entry);
		if(null != message && message.getCommandId() == CHARGE_NOTIFY_CMDID){
			C5901_ChargeNotifyReqMessage notifyMsg = (C5901_ChargeNotifyReqMessage)message ;
			return notifyMsg.getUserId();
		}
		//从ChannelSession中获得
		ChannelSession session = entry.getSession();
		if(null == session){
			return "" ;
		}
		Object o = session.getAttribute(USER_KEY);
		if(null == o){
			return "" ;
		}
		return o.toString();
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

	private Message getMessage(ChannelEvent event){
		if(null == event){
			return null ;
		}
		Object object = event.getMessage();
		if(null == object){
			return null ;
		}
		if(!(object instanceof Message)){
			return null ;
		}
		return (Message)object ;
	}
	
	private void init(List<String> list,Set<String> set){
		if(null == list || 0 == list.size()){
			return ;
		}
		for(String keyString:list){
			String[] keys = keyString.split(",");
			if(null == keys || 0 == keys.length){
				continue ;
			}
			for(String key : keys){
				if(null == key){continue;}
				set.add(key.trim());
			}
		}
	}

	private void init(){
		this.init(this.cumulateList, this.cumulateSet);
	}
	
}
