package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.npc.domain.NpcTemplate;
import com.game.draco.debug.message.item.NpcInfoItem;
import com.game.draco.debug.message.request.C10026_FindNpcReqMessage;
import com.game.draco.debug.message.response.C10026_FindNpcRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.Point;

public class FindNpcAction extends ActionSupport<C10026_FindNpcReqMessage>{
	
	@Override
	public Message execute(ActionContext conext, C10026_FindNpcReqMessage reqMessage) {
		C10026_FindNpcRespMessage resp = new C10026_FindNpcRespMessage();
		try{
			String npcId = reqMessage.getNpcId();
			List<NpcInfoItem> items = new ArrayList<NpcInfoItem>();
			if(npcId != null || npcId.trim().length()>0){
				List<Point> pointList = GameContext.getMapApp().whereNpcBorn(npcId);
				NpcTemplate temp = GameContext.getNpcApp().getNpcTemplate(npcId);
				if(pointList != null && pointList.size()>0){
					for(Point point:pointList){
						NpcInfoItem item = new NpcInfoItem();
						item.setTempId(npcId);
						item.setInstanceName(temp.getNpcname());
						item.setMapId(point.getMapid());
						item.setMapName(GameContext.getMapApp().getMap(point.getMapid()).getMapConfig().getMapdisplayname());
						item.setMapX(point.getX());
						item.setMapY(point.getY());
						items.add(item);
					}
				}
			}
			resp.setNpcItems(items);
			return resp;
		}catch(Exception e){
			logger.error("FindNpcAction error: ",e);
			resp.setNpcItems(null);
			return resp;
		}
	}

}
