package sacred.alliance.magic.action;

import sacred.alliance.magic.app.map.Map;
import sacred.alliance.magic.base.MapLogicType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0109_MapStuckResetReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;


public class MapStuckResetAction extends BaseAction<C0109_MapStuckResetReqMessage> {

	private void sendTips(String msg,String userId){
		C0003_TipNotifyMessage tipNotifyMessage = new C0003_TipNotifyMessage();
		tipNotifyMessage.setMsgContext(msg);
		GameContext.getMessageCenter().send("", userId, tipNotifyMessage);
	}
	
	@Override
	public Message execute(ActionContext context, C0109_MapStuckResetReqMessage reqMsg) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(role.isDeath()){
				this.sendTips(Status.Map_Reborn_Oprate.getTips(), role.getUserId());
				return null ;
			}
			MapInstance mapInstance = role.getMapInstance();
			if(null == mapInstance){
				return null ;
			}
			Map map =role.getMapInstance().getMap();
			//判断当前地图类型是否允许
			MapLogicType logicType = MapLogicType.getMapLogicType(map.getMapConfig().getLogictype());
			if(null == logicType || !logicType.isStuckReset()){
				this.sendTips(this.getText(TextId.Map_canot_stuck_reset), role.getUserId());
				return null ;
			}
			Point point = new Point(map.getMapId(), map.getMapConfig()
					.getMaporiginx(), map.getMapConfig().getMaporiginy());
			GameContext.getUserMapApp().changeMap(role, point);
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),
					this.getText(TextId.SYSTEM_ERROR));
		}
		return null;
	}

}
