//package sacred.alliance.magic.action;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.request.C1714_FactionMapEnterReqMessage;
//import com.game.draco.message.response.C0002_ErrorRespMessage;
//
//import sacred.alliance.magic.app.map.Map;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.Point;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionMapEnterAction extends BaseAction<C1714_FactionMapEnterReqMessage> {
//
//	@Override
//	public Message execute(ActionContext context, C1714_FactionMapEnterReqMessage reqMsg) {
//		try{
//			RoleInstance role = this.getCurrentRole(context);
//			if(!role.hasUnion()){
//				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),this.getText(TextId.Faction_Map_Not_Has));
//			}
//			String mapId = GameContext.getFactionApp().getFactionMapId();
//			Map map = GameContext.getMapApp().getMap(mapId);
//			if(null == map){
//				return null;
//			}
//			Point targetPoint = new Point(mapId, map.getMapConfig().getMaporiginx(), map.getMapConfig().getMaporiginy());
//			role.getBehavior().changeMap(targetPoint);
//			return null;
//		}catch(Exception e){
//			this.logger.error("FactionMapEnterAction", e);
//			return null;
//		}
//	}
//
//}
