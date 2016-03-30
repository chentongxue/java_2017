package sacred.alliance.magic.app.simulator;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import sacred.alliance.magic.core.Message;

import com.game.draco.message.response.C4999_UserLoginSafeRespMessage;

public class SimulatorIoHandler extends IoHandlerAdapter{
	

	public void messageReceived(IoSession io, Object message) throws Exception {
		if(null != message && message instanceof Message){
			Message msg = (Message)message ;
			//登录返回
			if(msg.getCommandId() == -4999){
				C4999_UserLoginSafeRespMessage loginRespMsg = (C4999_UserLoginSafeRespMessage)msg ;
				if(1 == loginRespMsg.getType()){
					System.out.println("=======================");
					System.out.println("===== the random key=" + loginRespMsg.getRandomKey()+" ====");
					System.out.println("=======================");
					SimulatorContext.getInstance().getMinaSecurity().setRandomKey(
							io, loginRespMsg.getRandomKey());
				}
				return ;
			}
			
			//测试加密解密代码
			/*if(-104 == msg.getCommandId()){
				RoleEnterRespMessage enterRespMsg = (RoleEnterRespMessage)msg ;
				if(1 == enterRespMsg.getType()){
					final IoSession session = io ;
					System.out.println("=====================登录成功");
					new Thread(new Runnable(){
						@Override
						public void run() {
							for(int i=0 ;i<256*256;i++){
								ActiveTestReqMessage reqMsg = new ActiveTestReqMessage();
								session.write(reqMsg);
								try {
									Thread.sleep(5);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							System.out.println("================发送完成");
						}
					}).start();
				}
				return ;
			}*/
			
		}
	}
	
	
}
