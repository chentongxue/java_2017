package sacred.alliance.magic.debug.action;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.MapItem;
import com.game.draco.debug.message.request.C10010_GetMapInstanceListReqMessage;
import com.game.draco.debug.message.response.C10010_GetMapInstanceListRespMessage;

import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstance;

public class GetMapInstanceListAction extends ActionSupport<C10010_GetMapInstanceListReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10010_GetMapInstanceListReqMessage req) {
		C10010_GetMapInstanceListRespMessage resp = new C10010_GetMapInstanceListRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			String mapId = req.getMapId();
			if(mapId == null || mapId.length()<=0){
				resp.setInfo(GameContext.getI18n().getText(TextId.GET_MAP_TEMPLATE_NULL));
				return resp;
			}
			Collection<MapInstance> mapInstanceList = GameContext.getMapApp().getAllMapInstance(req.getMapId());
			if(!Util.isEmpty(mapInstanceList)){
				resp.setType((byte)RespTypeStatus.SUCCESS);
				List<MapItem> itemList = new ArrayList<MapItem>(); 
				for(MapInstance mapInstance : mapInstanceList){
					MapItem item = new MapItem();
					item.setMapId(mapId);
					item.setMapInstanceId(mapInstance.getInstanceId());
					item.setMapName(mapInstance.getMap().getMapConfig().getMapdisplayname());
					itemList.add(item);
				}
				resp.setItems(itemList);
			}else{
				resp.setInfo(GameContext.getI18n().messageFormat(TextId.GET_MAP_INSTANCE_FAIL, mapId));
				resp.setItems(null);
			}
			return resp;
		}catch(Exception e){
			logger.error("GetMapInstanceListAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}

}
