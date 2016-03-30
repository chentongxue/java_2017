package sacred.alliance.magic.component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;

import sacred.alliance.magic.channel.ChannelEvent;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.channel.ChannelSession;
import sacred.alliance.magic.core.executor.KeyOrderedSupport;

public class ChannelEventSafeSupport implements KeyOrderedSupport<ChannelEvent>{
	private final static String USER_KEY = "userid" ;
	private Set<String> cmdSet = new HashSet<String>();
	private List<String> cmdList = null ;
	private boolean accept = false ;
	private List<String> notNotifyCmdList = null ;
	private Set<String> notNotifyCmdSet = new HashSet<String>();

	public void setCmdList(List<String> cmdList) {
		this.cmdList = cmdList;
	}

	public void setAccept(boolean accept) {
		this.accept = accept;
	}
	
	public void setNotNotifyCmdList(List<String> notNotifyCmdList) {
		this.notNotifyCmdList = notNotifyCmdList;
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

	public void init(){
		this.init(this.cmdList, this.cmdSet);
		this.init(this.notNotifyCmdList, this.notNotifyCmdSet);
	}
	
	@Override
	public String getKey(ChannelEvent entry) {
		String eventKey = entry.getEventKey();
		if(null != eventKey && eventKey.trim().length()>0){
			//说明外部手动设置的eventKey
			return eventKey ;
		}
		//从ChannelSession中获得
		ChannelSession session = entry.getSession();
		if(null == session){
			return null ;
		}
		Object o = session.getAttribute(USER_KEY);
		if(null == o){
			return null ;
		}
		return o.toString();
	}
	
	@Override
	public boolean accept(ChannelEvent event) {
		Message message = this.getMessage(event);
		if(null == message){
			return false ;
		}
		String cmd = String.valueOf(message.getCommandId());
		boolean contains = this.cmdSet.contains(cmd);
		if(accept){
			return contains?true:false ;
		}
		return contains?false:true ;
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
	
	@Override
	public void ignoreNotify(ChannelEvent event) {
		Message message = this.getMessage(event);
		if(null == message){
			return ;
		}
		if(notNotifyCmdList.contains(String.valueOf(message.getCommandId()))){
			return ;
		}
		C0003_TipNotifyMessage msg = new C0003_TipNotifyMessage();
		msg.setMsgContext(GameContext.getI18n().getText(TextId.USER_STATE_ERROR));
		event.getSession().write(msg);
	}

}
