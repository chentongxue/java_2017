package sacred.alliance.magic.app.msgcenter;
import java.util.Collection;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleEntity;


public interface MessageCenter {
		
    void sendMessage(MessageEntry entry) ;
	
	void sendByRoleId(String srcRoleId,String destRoleId,Message message);
	
	void sendByRoleId(String srcRoleId,String destRoleId,Message message, int expireTime);
	
	void sendTrainByRoleId(String destRoleId,Message message);
    
	void send(String srcUserId,String destUserId,Message message);
	
	void sendSysMsg(Collection<RoleEntity> receviers,Message message);
	
	void sendSysMsg(AbstractRole recevier,Message message);
	
	void sendSysMsg(AbstractRole recevier,Message message,int expireTime);
	
	void send(RoleEntity speaker,Collection<RoleEntity> receviers,Message message,boolean sendToSpeaker);
	
	void send(RoleEntity speaker,RoleEntity receiver,Message message,boolean sendToSpeaker) ;
	
	void send(String srcUserId, String destUserId,Message message,int expireTime);
	
	void send(String srcUserId,String[] destUserId,Message message,int expireTime);
	
	void broadcast(String srcUserId,Message message,int expireTime);
	
}
