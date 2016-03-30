package sacred.alliance.magic.action;

import com.game.draco.message.request.C0205_MapSwitchReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.RoleInstance;

public class MapSwitchAction extends BaseAction<C0205_MapSwitchReqMessage>{

	@Override
	public Message execute(ActionContext context, C0205_MapSwitchReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(null == role){
				return null ;
			}
			String mapId = role.getMapId();
			if(Util.isEmpty(mapId) || !mapId.equals(reqMsg.getMapId())){
				return null;
			}
			role.setMapX(reqMsg.getMapX());
			role.setMapY(reqMsg.getMapY());
			MapInstance mapInstance = role.getMapInstance();
			if(null != mapInstance) {
				mapInstance.footOnPoint(role);
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		return null;
	}
	
}
