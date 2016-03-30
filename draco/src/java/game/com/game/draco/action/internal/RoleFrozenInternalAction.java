package com.game.draco.action.internal;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import com.game.draco.GameContext;
import com.game.draco.app.chat.ChannelType;
import com.game.draco.app.chat.ChatSysName;
import com.game.draco.debug.message.request.C10015_FrozenRoleReqMessage;
import com.game.draco.message.internal.C0057_RoleFrozenInternalMessage;

import sacred.alliance.magic.action.BaseAction;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 
 * GM平台管理封用户
 *
 */
public class RoleFrozenInternalAction extends BaseAction<C0057_RoleFrozenInternalMessage>{

	@Override
	public Message execute(ActionContext context,
			C0057_RoleFrozenInternalMessage reqMsg) {
		RoleInstance role  = reqMsg.getRole();
		if(null == role){
			return null;
		}
		C10015_FrozenRoleReqMessage adminReq = reqMsg.getAdminReqMsg();
		Date frozenBeginTime = new Date();
		Date frozenEndTime = adminReq.getFrozenEndTime();
		if(frozenEndTime == null){
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, 1);
			frozenEndTime = calendar.getTime();
		}
		role.setFrozenBeginTime(frozenBeginTime);
		role.setFrozenEndTime(frozenEndTime);
		role.setFrozenMemo(adminReq.getMemo());
		//拍卖行物品下架
		GameContext.getAuctionApp().frozenRoleDownShelf(role.getRoleId());
		// 判断角色是否在线
		boolean isOnline = GameContext.getOnlineCenter().isOnlineByRoleId(role.getRoleId());
		if(isOnline){
			//角色在线,先踢下线再隔离
			role.getBehavior().closeNetLink();
			this.notify(role,frozenEndTime,frozenBeginTime,adminReq.getMemo());
			return null;
		}
		//用户不在线只有入数据库
		GameContext.getBaseDAO().update(role);
		//判断发送广播信息（0=广播，1=不广播）
		if(1 != adminReq.getBroadcastType()){
			this.notify(role,frozenEndTime,frozenBeginTime,adminReq.getMemo());
		}
		//处理玩家不在线排行榜除名，在线的意见在下线日志里面处理了
		GameContext.getRankApp().offlineRoleOffRank(role.getRoleId());
		return null;
	}
	
	private void notify(RoleInstance role,Date frozenEndTime,Date frozenBeginTime,String memo){
		String tips = "" ;
		int hour = Math.abs((int)DateUtil.dateDiffHour(frozenEndTime, frozenBeginTime));
		if(hour > 500) {
			tips = this.messageFormat(TextId.ROLE_FROZEN_FOREVER_TIPS,role.getRoleName(),memo);
		}else{
			tips = this.messageFormat(TextId.ROLE_FROZEN_HOUR_TIPS,role.getRoleName(),memo,hour);
		}
		GameContext.getChatApp().sendSysMessage(ChatSysName.System, ChannelType.World,tips, null, null);
	}

}
