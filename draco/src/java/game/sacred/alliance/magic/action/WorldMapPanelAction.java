package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.app.map.worldmap.WorldMapInfo;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.item.WorldMapItem;
import com.game.draco.message.request.C0250_WorldMapPanelReqMessage;
import com.game.draco.message.response.C0250_WorldMapPanelRespMessage;

public class WorldMapPanelAction extends BaseAction<C0250_WorldMapPanelReqMessage> {

	@Override
	public Message execute(ActionContext context, C0250_WorldMapPanelReqMessage req) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		C0250_WorldMapPanelRespMessage respMsg = new C0250_WorldMapPanelRespMessage();
		List<WorldMapInfo> infoList = GameContext.getWorldMapApp().getAllWorldMapInfo();
		if(Util.isEmpty(infoList)){
			return respMsg ;
		}
		List<WorldMapItem> mapItems = new ArrayList<WorldMapItem>();
		for(WorldMapInfo info : infoList){
			WorldMapItem item = new WorldMapItem();
			item.setMapId(info.getMapId());
			Map map = GameContext.getMapApp().getMap(info.getMapId());
			String newMapName = GameContext.getUnionBattleApp().getNewMapNameByMapIndex(info.getMapIndex());
			if(map != null && Util.isEmpty(newMapName)){
				item.setMapName(map.getMapConfig().getMapdisplayname());
			}
			if(!Util.isEmpty(newMapName)){
				item.setMapName(newMapName);
			}
			item.setMapIndex(info.getMapIndex());
			String unionName = GameContext.getUnionBattleApp().getUnionNameByMapIndex(info.getMapIndex());
			item.setUnionName(unionName);
//			item.setMinLevel(info.getMinLevel());
//			item.setMaxLevel(info.getMaxLevel());
//			item.setCanPk(info.worldMapCanPk());
			mapItems.add(item);
		}
		respMsg.setMapItems(mapItems);
		return respMsg;
	}

}
