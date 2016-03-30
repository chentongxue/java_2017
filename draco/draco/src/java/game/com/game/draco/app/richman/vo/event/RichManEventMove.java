package com.game.draco.app.richman.vo.event;

import java.util.HashSet;

import sacred.alliance.magic.vo.RoleInstance;
import sacred.alliance.magic.vo.map.MapRichManInstance;

import com.game.draco.GameContext;
import com.game.draco.app.richman.vo.RichManRoleBehavior;
import com.game.draco.app.richman.vo.RichManRoleStat;
import com.game.draco.message.response.C2653_RichManRoleEventNoticeMessage;

public class RichManEventMove extends RichManEventLogic {
	private static RichManEventMove instance = new RichManEventMove();
	private RichManEventMove() {
		
	}
	
	public static RichManEventMove getInstance() {
		return instance;
	}

	@Override
	public void execute(MapRichManInstance mapInstance, RoleInstance role,
			RichManRoleBehavior behavior) {
		int roleId = role.getIntRoleId();
		RichManRoleStat roleStat = mapInstance.getRoleStat(roleId);
		if(null == roleStat) {
			return ;
		}
		byte curGrid = roleStat.getGridId();
		int changeValue = (int)(behavior.getEvent().getEventValue());
		byte newGrid = this.getRoleNewGrid(curGrid, roleStat.getFace(), changeValue);
		//从当前格子上移除角色
		HashSet<Integer> curGridSet = mapInstance.getGridRoleIdSet(curGrid);
		curGridSet.remove(roleId);
		//在目标格子上加如角色
		HashSet<Integer> targetGridSet = mapInstance.getGridRoleIdSet(newGrid);
		targetGridSet.add(roleId);
		//更新格子
		roleStat.setGridId(newGrid);
		//广播消息给客户端玩家的目标格子id;
		int prefix = changeValue >= 0 ? 1 : -1;
		behavior.getEvent().setEventValue(roleStat.getFace() * prefix * newGrid);
		C2653_RichManRoleEventNoticeMessage respMsg = getRoleEventNoticeMessage(behavior);
		mapInstance.broadcastMap(null, respMsg);
	}
	
	private byte getRoleNewGrid(byte curGrid, byte face, int changeValue) {
		byte gridNum = GameContext.getRichManApp().getMapGridNum();
		return (byte)((curGrid + face * changeValue + gridNum) % gridNum);
	}

}
