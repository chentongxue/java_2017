package sacred.alliance.magic.action;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0108_ForcedExitMessage;
import com.game.draco.message.request.C0010_SpeedUpDecideReqMessage;
import com.game.draco.message.response.C0010_SpeedUpDecideRespMessage;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Log4jManager;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class SpeedUpDecideAction extends BaseAction<C0010_SpeedUpDecideReqMessage>{

	@Override
	public Message execute(ActionContext context, C0010_SpeedUpDecideReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		Date loginTime = role.getLastLoginTime();
		if(null == loginTime){
			loginTime = new Date();
			role.setLastLoginTime(loginTime);
		}
		int reqDateTime = req.getCurrentDateTime();
		long currentDateTime = System.currentTimeMillis();
		C0010_SpeedUpDecideRespMessage resp = new C0010_SpeedUpDecideRespMessage();
		int giveClientTime = (int)(currentDateTime - loginTime.getTime());
		if(reqDateTime == 0){
			resp.setCurrentDateTime(giveClientTime);
			return resp;
		}
		
		//加速判定间隔时间（阀值 单位：毫秒）
		int speedUpOffsetTime = GameContext.getHeartBeatConfig().getSpeedUpOffsetTime() ;
		
		// 客户端加速，reqDateTime将大于系统当前时间
		// 此时reqDateTime - 当前时间，如果大于加速判定间隔时间，则视为加速
		long intervalTime = reqDateTime + loginTime.getTime() - currentDateTime;
		boolean isSpeedUp = intervalTime > speedUpOffsetTime;
		if(isSpeedUp){
			Log4jManager.SPEEDUP_LOG.info("speed up role: roleId="+role.getRoleId()
				+" roleName="+role.getRoleName()+" userId="+role.getUserId()
				+" reqDateTime="+reqDateTime+" currentDateTime="+currentDateTime
				+" intervalTime="+ intervalTime + " loginTime=" + loginTime.getTime());
			//加速次数+1
			role.setSpeedUpCount(Util.safeIntAdd(role.getSpeedUpCount(), 1));
		} else if (role.getSpeedUpCount() > 0) {
			// 如果心跳时间正常则判断是否满足正常X次，如果满足把加速状态清零
			role.setSpeedUpNotCount(role.getSpeedUpNotCount() + 1);
			if (role.getSpeedUpNotCount() >= GameContext.getHeartBeatConfig()
					.getSpeedNotUpMinCount()) {
				role.setSpeedUpCount(0);
				role.setSpeedUpNotCount(0);
			}
		}
		
		boolean isOpenExitGame = GameContext.getHeartBeatConfig().getSpeedUpExit();
		if(isOpenExitGame && 
				role.getSpeedUpCount() >=  GameContext.getHeartBeatConfig().getSpeedUpMaxCount() ){
			//加速到底最大次数 && 开启加速踢下线
			int timeSegregate = GameContext.getHeartBeatConfig().getPermitTime();
			role.setFrozenEndTime(DateUtil.add(new Date(), Calendar.MINUTE, timeSegregate));
			role.setFrozenMemo(this.getText(TextId.SPEED_UP_FROZEN_MEMO));
			C0108_ForcedExitMessage exitResp = new C0108_ForcedExitMessage();
			exitResp.setInfo(this.messageFormat(TextId.SPEED_UP_FROZEN_MSG, timeSegregate));
			//ChannelSession session = context.getSession();
			//IoSession ioSession = ((MinaChannelSession) session).getIoSession();
			//WriteFuture f = ioSession.write(exitResp);
			//f.awaitUninterruptibly(1000);
			role.getBehavior().sendMessage(exitResp,1000);
			role.getBehavior().closeNetLink();
			return null ;
			
		}
		resp.setCurrentDateTime(giveClientTime);
		return resp;
	}

	
}
