package com.game.draco.app.buff.effect;

import com.game.draco.app.buff.BuffContext;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.app.npc.domain.NpcInstance;

import sacred.alliance.magic.app.attri.AttriBuffer;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.base.StateType;
import sacred.alliance.magic.vo.AbstractRole;

public class StateEffect implements Effect{

	static class SingletonHolder {
		static StateEffect singleton = new StateEffect();
	}
	
	public static StateEffect getInstance(){
		return SingletonHolder.singleton ;
	}
	
	@Override
	public void resume(BuffContext context) {
		//移除状态
		context.getOwner().removeState(StateType.getType(
				Integer.parseInt(context.getBuffStat().getBuffInfo())));
		//通知状态变化
		context.getOwner().getBehavior().notifyAttribute();
	}

	@Override
	public void store(BuffContext context) {
		AbstractRole owner = context.getOwner();
		//将状态放入buffInfo字段
		context.getBuffStat().setBuffInfo(String.valueOf(context.getStateType().getType()));
		//添加状态
		owner.addState(context.getStateType());
		//如果NPC不能移动，需要发送停止行走的消息
		if(owner.getRoleType() == RoleType.NPC && !context.getStateType().isCanMove() 
				&& context.getOwner().inState(context.getStateType())) {
			owner.getBehavior().stopMove();
		}
		//通知状态变化
		owner.getBehavior().notifyAttribute();
	}

	@Override
	public AttriBuffer getAttriBuffer(BuffStat stat) {
		//修改状态属性
		AbstractRole role = stat.getOwner();
		role.addState(StateType.getType(Integer.parseInt(stat.getBuffInfo())));
		return null;
	}

}
