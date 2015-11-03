package game_observer;

import function.GofFunction1;

@GofGenFile
public final class MsgReceiverInit{
	public static <K,P> void init(ObServer<K, P> ob){
		ob.reg("class org.gof.demo.worldsrv.msg.Msg$CSChangeName", (GofFunction1<MsgParam>)(ob.getTargetBean(NameMsgHandler.class))::onCSChangeName, 1);  
	}
}

