package sacred.alliance.magic.app.msgcenter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BalancedMessageSender implements MessageSender{
	private List<MessageSender> senderList = null ;
	private MessageSender[] senders = null ;
	private int senderSize = 0 ;
	private AtomicInteger totalSize = new AtomicInteger(0);
	
	private void init(){
		if(null == senderList 
				|| 0 == this.senderList.size()){
			return ;
		}
		int index = 0 ;
		this.senderSize = this.senderList.size();
		senders = new MessageSender[senderSize];
		for(MessageSender s : this.senderList){
			senders[index++] =s ;
		}
		this.senderList.clear();
		this.senderList = null ;
	}
	
	@Override
	public void messageEntryHandle(MessageEntry entry) {
		//不用处理消息
	}

	@Override
	public void sendMessage(MessageEntry entry) {
		int count = totalSize.incrementAndGet();
		if(count == Integer.MAX_VALUE){
			totalSize.compareAndSet(Integer.MAX_VALUE, 0);
		}
		this.senders[count%this.senderSize].sendMessage(entry);
	}

	@Override
	public void start() {
		this.init();
		if(0 == this.senderSize){
			throw new java.lang.RuntimeException("must set the senderList");
		}
		for(MessageSender s : this.senders){
			s.start();
		}
	}

	@Override
	public void stop() {
		if(0 == this.senderSize){
			return ;
		}
		for(MessageSender s : this.senders){
			s.stop();
		}
	}

	
	public void setSenderList(List<MessageSender> senderList) {
		this.senderList = senderList;
	}

	
	
}
