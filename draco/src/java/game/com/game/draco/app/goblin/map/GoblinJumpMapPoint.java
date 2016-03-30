package com.game.draco.app.goblin.map;

import sacred.alliance.magic.app.map.MapUtil;
import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.app.map.point.JumpMapPoint;
import sacred.alliance.magic.base.RoleType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;

public class GoblinJumpMapPoint extends JumpMapPoint{

	@Override
	public String isSatisfyCond(AbstractRole role){
		String ret = super.isSatisfyCond(role);
		if(ret.equals(FALSE)){
			return ret ;
		}
		if(role.getRoleType() != RoleType.PLAYER){
			return FALSE ;
		}
		//判断是否有公会
		RoleInstance player = (RoleInstance)role ;
		
		if(!player.hasUnion()){
			MapConfig mapConfig = GameContext.getMapApp().getMapConfig(this.tomapid);
			String text = GameContext.getI18n().messageFormat(TextId.map_havenot_union_canot_enter,
					mapConfig.getMapdisplayname());
			this.notifyPointMessage(text, player);
			return FALSE ;
		}
		// 判断密境是否人数已满
		MapInstance mapInstance = GameContext.getMapApp().getMapInstance(this.getPointKey());
		if (null == mapInstance) {
			return TRUE;
		}
		int maxRoleNum = GameContext.getGoblinApp().getGoblinBaseConfig().getSecretAccomNum();
		if (mapInstance.getRoleCount() > maxRoleNum) {
			this.notifyPointMessage(GameContext.getI18n().getText(TextId.Goblin_Is_Max_Number), player);
			return FALSE;
		}
		return TRUE ;
	}
	
	
	@Override
	public void trigger(AbstractRole role) throws ServiceException {
		// 先调用app的方法让getPointKey()和mapinstance 对应起来
		GameContext.getGoblinApp().setRoleSecretPointKey(role.getRoleId(), this.getPointKey());
		Point point = MapUtil.randomCorrectRoadPoint(tomapid);
		if (null != point) {
			role.getBehavior().changeMap(point);
			return;
		}
		role.getBehavior().changeMap(new Point(tomapid, desX, desY));
	}
	
	public String getPointKey(){
		return "goblin_" + this.mapid + "_" + this.x + "_" + this.y ;
	}
}
