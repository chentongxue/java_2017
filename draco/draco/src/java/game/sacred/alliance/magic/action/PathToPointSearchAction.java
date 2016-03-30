package sacred.alliance.magic.action;

import com.game.draco.message.request.C0215_PathToPointSearchReqMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;

/**
 * 自动寻路
 */
public class PathToPointSearchAction extends BaseAction<C0215_PathToPointSearchReqMessage>{

	//private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public Message execute(ActionContext context, C0215_PathToPointSearchReqMessage reqMsg) {
		/*String mapId = reqMsg.getMapId();
		int x = reqMsg.getMapX();
		int y = reqMsg.getMapY();
		if(Util.isEmpty(mapId) || x <=0 || y <= 0){
			return new ErrorRespMessage(reqMsg.getCommandId(),"参数错误");
		}
		RoleInstance role = this.getCurrentRole(context);
		if(role == null){
			return null;
		}
		try{
			GameContext.getUserMapApp()
				.gotoTargetMapPoint(role, 1, new Point(mapId,x,y));
		}catch(Exception e){
			logger.error("",e);
		}*/
		return null;
	}

}
