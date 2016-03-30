package sacred.alliance.magic.filter;

import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.project.protobuf.codec.ProtoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.util.SessionUtil;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.login.UserInfo;
import com.game.draco.message.internal.InternalMessage;

public class LoggingMessageFilter extends IoFilterAdapter{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ProtoBuffer protoBuffer = null;
    private Set<String> filterCommandSet = new HashSet<String>();

    public void setProtoBuffer(ProtoBuffer protoBuffer) {
        this.protoBuffer = protoBuffer;
    }

    public void setFilterCommandSet(Set<String> filterCommandSet) {
		this.filterCommandSet = filterCommandSet;
	}
    

	private int getRoleId(IoSession session){
    	if(null == session){
    		return 0 ;
    	}
    	Object o = session.getAttribute(SessionUtil.USER_INFO_KEY);
    	if(null == o){
    		return 0 ;
    	}
    	return ((UserInfo)o).getCurrRoleId();
    }

	@Override
    public void messageReceived(NextFilter nextFilter, IoSession session,
            Object message) {
        try {
            if (null != message && logger.isInfoEnabled() && message instanceof Message) {
                Message msg = (Message) message;
                String roleId = String.valueOf(this.getRoleId(session));
                if(!this.isPrintMessageLog(roleId, msg.getCommandId())){
                	return;
                }
                if(msg instanceof InternalMessage ){
                	//内部消息
                	logger.info("Received: " + msg.toString());
                	return ;
                }
                if(null != protoBuffer){
                	 logger.info("Received:(" + this.getRoleId(session) + ") "+ protoBuffer.toString(msg));
                }
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }finally{
        	nextFilter.messageReceived(session, message);
        }
    }

    @Override
    public void messageSent(NextFilter nextFilter, IoSession session,
            WriteRequest writeRequest) {
        try {
            Object message = writeRequest.getMessage();
            if (null != message && logger.isInfoEnabled() && message instanceof Message) {
                Message msg = (Message) message;
                String roleId = String.valueOf(this.getRoleId(session));
                if(!this.isPrintMessageLog(roleId, msg.getCommandId())){
                	return;
                }
                if(msg instanceof InternalMessage ){
                	//内部消息
                	logger.info("Received: " + msg.toString());
                	return ;
                }
                if(null != protoBuffer){
                	logger.info("Sent:(" + roleId + ") "+  protoBuffer.toString(msg));
                }
            }
        } catch (Exception ex) {
            logger.error("", ex);
        }finally{
        	nextFilter.messageSent(session, writeRequest);
        }
        
    }
    
    /**
     * 是否打印消息体的日志
     * @param roleId
     * @param cmdId
     * @return
     */
    private boolean isPrintMessageLog(String roleId, short cmdId){
    	//强制打印日志的角色ID集合
    	Set<String> roleIdSet = GameContext.getParasConfig().getLogMessagePrintRoleIdSet();
    	if(!Util.isEmpty(roleIdSet) && roleIdSet.contains(roleId)){
    		return true;
    	}
    	//正式服务器，不需要打印日志
    	if(GameContext.isOfficialServer()){
    		return false;
    	}
    	return !(null != filterCommandSet && filterCommandSet.contains(String.valueOf(cmdId)));
    }
    
}
