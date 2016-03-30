package com.game.draco.app.buff;

import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.message.push.C1523_RoleColorZoomNotifyMessage;

import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class RoleColorZoomBuff extends RoleBuff {

	public RoleColorZoomBuff(short buffId) {
		super(buffId);
	}
	
	@Override
	public void begin(BuffContext context) {
		super.begin(context);
		roleColorZoomNotify(context, true);
	}
	
	@Override
	public void remove(BuffContext context) {
		super.remove(context);
		roleColorZoomNotify(context, false);
	}
	
	@Override
	public void timeOver(BuffContext context) {
		super.timeOver(context);
		roleColorZoomNotify(context, false);
	}
	
	private void roleColorZoomNotify(BuffContext context, boolean isAdd) {
		BuffStat stat = context.getBuffStat();
		if(null == stat) {
			return ;
		}
		AbstractRole owner = stat.getOwner();
		if(null == owner) {
			return ;
		}
		MapInstance instance = owner.getMapInstance();
		if(null == instance) {
			return ;
		}
		
		int color = 0;
		byte zoom = 0;
		if(isAdd) {
			//buff添加
			color = this.getDiscolor();
			zoom = this.getZoom();
		}
		else {
			//移除
			zoom = 10;
			if(owner.getRoleType() == RoleType.PLAYER) {
				color = ((RoleInstance)owner).getColor();
			}
		}
		
		C1523_RoleColorZoomNotifyMessage msg = new C1523_RoleColorZoomNotifyMessage();
		msg.setRoleId(owner.getIntRoleId());
		msg.setZoom(zoom);
		msg.setColor(color);
		//发给自己
		owner.getBehavior().sendMessage(msg);
		//广播
		instance.broadcastMap(owner, msg);
	}

}
