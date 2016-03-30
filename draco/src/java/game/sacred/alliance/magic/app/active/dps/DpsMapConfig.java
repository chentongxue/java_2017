package sacred.alliance.magic.app.active.dps;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import sacred.alliance.magic.app.active.vo.Active;
import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.ActiveType;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.Cat;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.Point;

import com.game.draco.GameContext;

public @Data class DpsMapConfig {
	
	private short activeId;//活动ID
	private String mapId;//地图ID
	private short mapX;
	private short mapY;
	private String broadcastText ; //活动结束广播内容
	private byte broadcastChannel ; //活动结束广播频道
	private int showSize;//显示排名数
	private short buffId;//buffId，标记是否发过奖
	private String npcIds;//可能出现的BOSS集合
	//可能出现的NPC集合
	private Set<String> npcIdSet = new HashSet<String>();
	
	public Result checkAndInit(){
		Result result = new Result();
		String info = "mapId=" + this.mapId + ",activeId=" + this.activeId + ",";
		try {
			Active active = GameContext.getActiveApp().getActive(this.activeId);
			if(null == active){
				return result.setInfo(info + "active is not exist!");
			}
			if(ActiveType.BossDps.getType() != active.getType()){
				return result.setInfo(info + "this active is not dps type.");
			}
			if(this.showSize <= 0){
				return result.setInfo(info + "showSize is not config!");
			}
			Map map = GameContext.getMapApp().getMap(this.mapId);
			if(null == map){
				return result.setInfo(info + "The map is not exist.");
			}
			if(this.mapX <= 0 || this.mapY <= 0){
				return result.setInfo(info + "The mapX or mapY is error.");
			}
			//将地图逻辑修改为DPS类型
			map.getMapConfig().setLogictype((byte) MapLogicType.dps.getType());
			if(Util.isEmpty(this.npcIds)){
				return result.setInfo(info + "The npcIds is null.");
			}
			String[] ids = this.npcIds.split(Cat.comma);
			for(String id : ids){
				if(Util.isEmpty(id)){
					continue;
				}
				this.npcIdSet.add(id.trim());
			}
			return result.success();
		} catch (RuntimeException e) {
			return result.setInfo(info + e.toString());
		}
	}
	
	public Point getMapPoint(){
		return new Point(this.mapId, this.mapX, this.mapY);
	}
	
}
