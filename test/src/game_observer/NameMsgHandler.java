package game_observer;


public class NameMsgHandler {
	
	/**
	 * 改名字
	 * 
	 * @param param
	 */
	@MsgReceiver(CSChangeName.class)
	public void onCSChangeName(MsgParam param) {
		CSChangeName msg = param.getMsg();
		
		NameManager.inst().changeName(param.getHumanObject(), msg.getName());
	}
	
}
