package sacred.alliance.magic.app.simulator;

import java.util.HashSet;
import java.util.Set;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.project.protobuf.codec.ProtoBuffer;

import sacred.alliance.magic.core.Message;

public class PrintMessageFilter extends IoFilterAdapter {
	private Set<String> filterCommandSet = new HashSet<String>();
	private ProtoBuffer protoBuffer ;
	private void printMessage(String prefix,Object message){
		if(null == message || !(message instanceof Message)){
			return ;
		}
		Message msg = (Message)message ;
		if(filterCommandSet.contains(String.valueOf(msg.getCommandId()))){
			return ;
		}
		System.out.println(prefix + " " + /*msg.toString()*/ protoBuffer.toString(msg));
	}
	
	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) {
		try {
			this.printMessage("Received:", message);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			nextFilter.messageReceived(session, message);
		}

	}

	@Override
	public void messageSent(NextFilter nextFilter, IoSession session,
			WriteRequest writeRequest) {
		try {
			this.printMessage("Sent:", writeRequest.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			nextFilter.messageSent(session, writeRequest);
		}
	}

	public Set<String> getFilterCommandSet() {
		return filterCommandSet;
	}

	

	public ProtoBuffer getProtoBuffer() {
		return protoBuffer;
	}

	public void setProtoBuffer(ProtoBuffer protoBuffer) {
		this.protoBuffer = protoBuffer;
	}

	public void setFilterCommandSet(Set<String> filterCommandSet) {
		this.filterCommandSet = filterCommandSet;
	}
	
	
}
