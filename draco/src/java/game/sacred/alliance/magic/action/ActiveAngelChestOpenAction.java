package sacred.alliance.magic.action;

import com.game.draco.message.request.C2373_ActiveAngelChestOpenReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapInstanceEvent;
import sacred.alliance.magic.vo.RoleInstance;

public class ActiveAngelChestOpenAction extends BaseAction<C2373_ActiveAngelChestOpenReqMessage>{

	@Override
	public Message execute(ActionContext context,
			C2373_ActiveAngelChestOpenReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		MapInstance mapInstance = role.getMapInstance();
		if(null == mapInstance  || !mapInstance.isNormalLive(role)){
			return null ;
		}
		MapInstanceEvent event = new MapInstanceEvent(MapInstanceEvent.EventType.chestOpen,reqMsg.getId());
		mapInstance.doEvent(role,event);
		return null;
	}

}
