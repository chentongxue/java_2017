package sacred.alliance.magic.fee.action;

import org.slf4j.Logger;

import com.game.draco.GameContext;
import com.game.draco.message.internal.C0064_RechargehandleInternalMessage;

import platform.message.request.C5901_ChargeNotifyReqMessage;
import platform.message.response.C5901_ChargeNotifyRespMessage;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Log4jManager;

/**
 * 计费中心回调接口
 * 为角色充金条
 */
public class RechargeNotifyAction extends ActionSupport<C5901_ChargeNotifyReqMessage> {
	
	private final Logger logger = Log4jManager.CHARGE_MONEY_LOG;
	
	@Override
	public Message execute(ActionContext context, C5901_ChargeNotifyReqMessage reqMsg){		
		//计费中心返回消息
		C5901_ChargeNotifyRespMessage resp = new C5901_ChargeNotifyRespMessage();
		try{
			//获取订单信息
			String userId = reqMsg.getUserId();
			//接收到充值消息，打印日志
			StringBuffer buffer = new StringBuffer();
			buffer.append("received charge notify $")
				.append(",").append(reqMsg.getAppId())
				.append(",").append(reqMsg.getServerId())
				.append(",").append(userId)
				.append(",").append(reqMsg.getUserName())
				.append(",").append(reqMsg.getRoleId())
				.append(",").append(reqMsg.getRoleName())
				.append(",").append(reqMsg.getMoney())
				.append(",").append(reqMsg.getOrderId())
				.append(",").append(reqMsg.getChannelId())
				.append(",").append(reqMsg.getChannelOrderId())
				.append(",").append(reqMsg.getResult())
				.append(",").append(reqMsg.getStrResult());
			this.logger.info(buffer.toString());
			/*//验证ip
			if(GameContext.isOfficialServer() 
					&& GameContext.getChargeConfig().isPayWhiteIpVerify()){
				//正式服务器需要验证充值通知ip
				ChannelSession session = context.getSession();
				String remoteHost = session.getClientIp();
				if(null == remoteHost){
					remoteHost = "" ;
				}else{
					remoteHost = remoteHost.trim();
				}
				//Client feeClient =  GameContext.getFeeClient();
				Set<String> whiteSet = feeClient.targetHosts();
				if(!whiteSet.contains(remoteHost)){
					//非法ip
					//返回计费中心消息
					this.logger.error("block ip: " + remoteHost + ",userId=" + userId + ",roleId=" + roleId 
							+ ",channelId=" + channelId + ",channelOrderId=" + channelOrderId + ",orderId=" + orderId + ",money=" + money);
					resp.setResult(0);
					return resp;
				}
			}*/
			//具体逻辑到单用户单线程中处理
			C0064_RechargehandleInternalMessage message = new C0064_RechargehandleInternalMessage();
			message.setOriginalMessage(reqMsg);
			message.setOriginalSession(context.getSession());
			GameContext.getUserSocketChannelEventPublisher().publish(userId,message,context.getSession(),true);
			return null ;
		}catch(Exception e){
			this.logger.error("pay exception ：",e);
			resp.setOrderId(reqMsg.getOrderId());
			resp.setChannelId(reqMsg.getChannelId());
			resp.setChannelOrderId(reqMsg.getChannelOrderId());
			resp.setResult(0);
			return resp;
		}
	}
	
	
}
