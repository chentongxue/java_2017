package sacred.alliance.magic.app.msgcenter;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import sacred.alliance.magic.app.onlinecenter.OnlineCenter;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleEntity;
import sacred.alliance.magic.vo.RoleInstance;

public class MessageCenterImpl implements MessageCenter {
	
	private static String DEFAULT_KEY = "0" ;
	private AtomicBoolean started = new AtomicBoolean(false);
	private Map<String, MessageSender> messageSenderMap;
	private OnlineCenter onlineCenter ;
	private MessageSender defaultSender = null ;

	public void setOnlineCenter(OnlineCenter onlineCenter) {
		this.onlineCenter = onlineCenter;
	}
	
	public void setMessageSenderMap(Map<String, MessageSender> messageSenderMap) {
		this.messageSenderMap = messageSenderMap;
	}

	private void init() {
		if (null == this.messageSenderMap) {
			return;
		}
		defaultSender = this.messageSenderMap.get(DEFAULT_KEY);
		for (MessageSender sender : this.messageSenderMap.values()) {
			sender.start();
		}
	
	}

	public void start() {
		if (started.compareAndSet(false, true)) {
			init();
		}
	}

	public void stop() {
		if (started.compareAndSet(true, false)) {
			if (null == this.messageSenderMap) {
				return;
			}
			for (MessageSender sender : this.messageSenderMap.values()) {
				sender.stop();
			}
		}
	}
	
	@Override
	public void sendTrainByRoleId(String destRoleId,Message message){
		if(null == message){
			return ;
		}
		RoleInstance destRole = onlineCenter.getRoleInstanceByRoleId(destRoleId);
		if(null == destRole){
			return;
		}
		destRole.getBehavior().sendMessage(message);
	}
	
	@Override
	public void broadcast(String srcUserId, Message message, int expireTime) {
		Collection<RoleInstance> allRole = onlineCenter.getAllOnlineRole();
		for (RoleInstance role : allRole) {
			if (srcUserId.equals(role.getUserId())) {
				continue;
			}
			this.send(srcUserId, role.getUserId(), message, expireTime);
		}
	}
	
	@Override
	public void send(String srcUserId, String destUserId, Message message) {
		this.send(srcUserId, destUserId, message, 0);
	}
	
	@Override
	public void send(String srcUserId, String destUserId, Message message, int expireTime) {
		MessageEntry entry=new MessageEntry();
		entry.setMessage(message);
		entry.setDestUserId(destUserId);
		//entry.setSrcUserId(srcUserId);
		entry.setExpireTime(expireTime);
		//注意此处
		entry.setCustomerService(false);
		this.sendMessage(entry);
	}
	
	public void sendGm(String srcUserId, String destUserId, Message message,
			int expireTime, boolean isPersist) {
		MessageEntry entry=new MessageEntry();
		entry.setMessage(message);
		entry.setDestUserId(destUserId);
		//entry.setSrcUserId(srcUserId);
		entry.setExpireTime(expireTime);
		//注意此处
		entry.setCustomerService(true);
		this.sendMessage(entry);
	}
	
	@Override
	public void send(String srcUserId, String[] destUserId, Message message, int expireTime) {
		if(null == destUserId){
			return ;
		}
		for(int i=0;i<destUserId.length;i++){
			String destUser = destUserId[i];
			if(srcUserId.equals(destUser)){
				continue ;
			}
			this.send(srcUserId,destUser,message,expireTime);
		}
	}

	
	@Override
	public void sendSysMsg(Collection<RoleEntity> receviers,Message message){
		if(null == receviers){
			return ;
		}
		for(RoleEntity receiver: receviers){
			if(receiver instanceof AbstractRole){
				this.sendSysMsg((AbstractRole)receiver, message);
				continue ;
			}
			//发送给GM
			this.sendGm("-1", receiver.getRoleId(), message, 0, false);
		}
	}
	
	@Override
	public void sendSysMsg(AbstractRole recevier,Message message){
		this.sendSysMsg(recevier, message,0);
	}
	
	@Override
	public void sendSysMsg(AbstractRole recevier, Message message,
			int expireTime) {
		if(null == recevier){
			return ;
		}
		if(recevier.getRoleType() == RoleType.PLAYER) {
			this.send("-1",((RoleInstance)recevier).getUserId(),message,expireTime);
		}
	}
	
	@Override
	public void send(RoleEntity speaker,RoleEntity receiver,
			Message message,boolean sendToSpeaker){
		String speakerRoleId = "" ;
		String speakerUserId = "" ;
		if(null != speaker){
			speakerRoleId = speaker.getRoleId();
			if(speaker instanceof RoleInstance){
				speakerUserId = ((RoleInstance)speaker).getUserId();
			}
		}
		if(!sendToSpeaker && speakerRoleId.equals(receiver.getRoleId())){
			return ;
		}
		if(receiver.getRoleType() == RoleType.PLAYER){
			this.send(speakerUserId,((RoleInstance)receiver).getUserId(),message,0);
			return ;
		}
		if(receiver.getRoleType() == RoleType.GM){
			//发送给GM
			this.sendGm(speaker.getRoleId(), receiver.getRoleId(), message, 0, false);
		}
	}

	@Override
	public void send(RoleEntity speaker, Collection<RoleEntity> receivers,
			Message message,boolean sendToSpeaker) {
		if(null == receivers || null == message){
			return ;
		}
		for(RoleEntity receiver: receivers){
			this.send(speaker,receiver,message,sendToSpeaker);
		}
	}
	
	@Override
	public void sendByRoleId(String srcRoleId, String destRoleId, Message message) {
		RoleInstance role = onlineCenter.getRoleInstanceByRoleId(destRoleId);
		if(null == role){
			return ;
		}
		this.send(srcRoleId, role.getUserId(), message);
		
	}
	
	@Override
	public void sendByRoleId(String srcRoleId, String destRoleId,
			Message message, int expireTime) {
		RoleInstance role = onlineCenter.getRoleInstanceByRoleId(destRoleId);
		if(null == role){
			return ;
		}
		this.send(srcRoleId, role.getUserId(), message, expireTime);
	}

	@Override
	public void sendMessage(MessageEntry entry) {
		MessageSender sender = getMessageSender(entry);
		if (null != sender) {
			sender.sendMessage(entry);
		}
	}

	private MessageSender getMessageSender(MessageEntry entry) {
		if(null == entry.getMessage()){
			return this.defaultSender;
		}
		String commandId = String.valueOf(entry.getMessage().getCommandId());
		MessageSender sender = this.messageSenderMap.get(commandId);
		if (null != sender) {
			return sender;
		}
		return this.defaultSender;
	}


}
