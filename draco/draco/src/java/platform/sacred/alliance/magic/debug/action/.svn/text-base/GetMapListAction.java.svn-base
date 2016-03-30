package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.MapItem;
import com.game.draco.debug.message.request.C10009_GetMapListReqMessage;
import com.game.draco.debug.message.response.C10009_GetMapListRespMessage;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstance;

public class GetMapListAction extends ActionSupport<C10009_GetMapListReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10009_GetMapListReqMessage req) {
		C10009_GetMapListRespMessage resp = new C10009_GetMapListRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			Collection<MapInstance> mapInstanceList = GameContext.getMapApp().getAllMapInstance();
			if(!Util.isEmpty(mapInstanceList)){
				resp.setType((byte)RespTypeStatus.SUCCESS);
				List<MapItem> itemList = new ArrayList<MapItem>(); 
				for(MapInstance mapInstance : mapInstanceList){
					MapItem item = new MapItem();
					String mapId = mapInstance.getMap().getMapId();
					item.setMapId(mapId);
					item.setMapInstanceId(mapInstance.getInstanceId());
					item.setMapName(mapInstance.getMap().getMapConfig().getMapdisplayname());
					itemList.add(item);
				}
				resp.setItems(itemList);
			}else{
				resp.setInfo(GameContext.getI18n().getText(TextId.GET_MAP_LIST_FAIL));
				resp.setItems(null);
			}
			return resp;
		}catch(Exception e){
			logger.error("GetMapListAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}

}
