package sacred.alliance.magic.action;

import com.game.draco.message.request.C3000_ClientNetDelayReqMessage;
import com.game.draco.message.response.C3000_ClientNetDelayRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.AbstractRole;

public class ClientNetDelayAction extends BaseAction<C3000_ClientNetDelayReqMessage>{

	@Override
	public Message execute(ActionContext context, C3000_ClientNetDelayReqMessage req) {
		/** 
		 * 网络延时计算
		 * 1.服务器主推-3001进行取样，此时在角色身上保存当前时间T1（毫秒）
		 * 2.客户端收到-3001时需要发3000，服务器接到命令时，当前时间为T2。计算一次消息交互时间：time=T2-T1，将time封装到-3000中返回给客户端。
		 * 3.客户端收到-3000时，获取time的值，构建3001消息发送给服务器。
		 */
		C3000_ClientNetDelayRespMessage resp = new C3000_ClientNetDelayRespMessage();
		AbstractRole role = this.getCurrentRole(context);
		long netDelay = System.currentTimeMillis() - role.getNetDelayTime();
		resp.setNetdelayTime((int)netDelay);
		return resp;
	}
}
