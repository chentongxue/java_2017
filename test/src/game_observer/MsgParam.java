package game_observer;

import com.google.protobuf.GeneratedMessage;

/**
 * 游戏中的客户端消息都通过这个类封装
 */
public class MsgParam extends MsgParamBase {
	private HumanObject humanObj;		//发送消息的玩家
	
	public MsgParam(GeneratedMessage msg) {
		super(msg);
	}

	public HumanObject getHumanObject() {
		return humanObj;
	}
	
	public void setHumanObject(HumanObject humanObj) {
		this.humanObj = humanObj;
	}
}