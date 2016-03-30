package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0252_MapTransferReqMessage;

import sacred.alliance.magic.base.ChangeMapEvent;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public class MapTransferAction extends BaseAction<C0252_MapTransferReqMessage> {

	@Override
	public Message execute(ActionContext context, C0252_MapTransferReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		
		String mapId = reqMsg.getMapId();
		int mapX = reqMsg.getMapX();
		int mapY = reqMsg.getMapY();
		Result result = GameContext.getWorldMapApp().transfer(role, 
				new Point(mapId,mapX,mapY,ChangeMapEvent.worldmap.getEventType()),
				GameContext.getParasConfig().getWorldMapGoldCost());
		if(null == result || result.isSuccess()|| result.isIgnore()){
			return null ;
		}
		return new C0003_TipNotifyMessage(result.getInfo());
	}

}
