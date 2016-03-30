package sacred.alliance.magic.action;

import com.game.draco.message.request.C2377_ActiveAngelChestOpenReadyReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapInstanceEvent;
import sacred.alliance.magic.vo.RoleInstance;

public class ActiveAngelChestOpenReadyAction extends BaseAction<C2377_ActiveAngelChestOpenReadyReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C2377_ActiveAngelChestOpenReadyReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		MapInstance mapInstance = role.getMapInstance();
		if(null == mapInstance || !mapInstance.isNormalLive(role)){
			return null ;
		}
		MapInstanceEvent event = new MapInstanceEvent(MapInstanceEvent.EventType.chestOpenReady,reqMsg.getId());
		mapInstance.doEvent(role,event);
		return null;
	}

}
