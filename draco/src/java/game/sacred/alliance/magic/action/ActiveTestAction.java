package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0001_ActiveTestReqMessage;
import com.game.draco.message.response.C0001_ActiveTestRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 心跳协议
 * @author wangkun
 *
 */
public class ActiveTestAction extends BaseAction<C0001_ActiveTestReqMessage>{

	@Override
	public Message execute(ActionContext context, C0001_ActiveTestReqMessage reqMessage) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role) {
			return null ;
		}
		//看门狗
		GameContext.getDoorDogApp().heartbeat(role);
		role.setLastHeartNumber(System.currentTimeMillis());
		return new C0001_ActiveTestRespMessage();
		
		/*long lastHeartTime = role.getLastHeartNumber();
		boolean isOpenExitGame = GameContext.getHeartBeatConfig().getSpeedUpExit();
		if(lastHeartTime == 0 || !isOpenExitGame){
			role.setLastHeartNumber(System.currentTimeMillis());
			return new ActiveTestRespMessage();
		}
		
		long now = System.currentTimeMillis();
		int speedUpCount = GameContext.getHeartBeatConfig().getSpeedUpCount();
		int speedUpOffsetTime = GameContext.getHeartBeatConfig().getSpeedUpOffsetTime();
		int heartBeat = GameContext.getHeartBeatConfig().getHeartBeat() * 1000;
		long roleHeartBeat = now - lastHeartTime;
		//如果两次心跳时间小于心跳时间 - 偏移量 则认定为加速
		if(roleHeartBeat < heartBeat - speedUpOffsetTime){
			role.setSpeedUpCount(role.getSpeedUpCount() + 1);
		}else{
			//如果心跳时间正常则判断是否满足正常3次，如果满足把加速状态清零
			if(role.getSpeedUpCount() > 0){
				role.setSpeedUpNotCount(role.getSpeedUpNotCount() + 1);
				if(role.getSpeedUpNotCount() >= speedUpCount){
					role.setSpeedUpCount(0);
					role.setSpeedUpNotCount(0);
				}
			}
		}
		//如果加速3次，踢出游戏
		boolean isSpeedUp = role.getSpeedUpCount() >= speedUpCount ? true : false;
		if(!isSpeedUp){
			role.setLastHeartNumber(System.currentTimeMillis());
			return new ActiveTestRespMessage();
		}
		
		int timeSegregate = GameContext.getHeartBeatConfig().getPermitTime();
		role.setFrozenEndTime(DateUtil.add(new Date(), Calendar.MINUTE, timeSegregate));
		role.setFrozenMemo(RespStatus.SPEED_UP_FROZEN_MEMO);
		ErrorRespMessage exitResp = new ErrorRespMessage();
		exitResp.setInfo(MessageFormat.format(RespStatus.SPEED_UP_FROZEN_MSG, timeSegregate));
		ChannelSession session = context.getSession();
		IoSession ioSession = ((MinaChannelSession) session).getIoSession();
		WriteFuture f = ioSession.write(exitResp);
		f.awaitUninterruptibly(1000);
		
		role.getBehavior().closeNetLink();
		
		Log4jManager.SPEEDUP_LOG.info("speed up role: roleid = "+role.getRoleId()
				+" roleName = "+role.getRoleName()+" userId = "+role.getUserId()
				+" roleHeartBeat = " + roleHeartBeat);
		return null;*/
	}
}
