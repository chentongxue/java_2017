package sacred.alliance.magic.action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.message.request.C0200_WalkSynchMoveReqMessage;
import com.game.draco.message.response.C0200_WalkSynchMoveRespMessage;

public class WalkSynchMoveAction extends BaseAction<C0200_WalkSynchMoveReqMessage> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Message execute(ActionContext context, C0200_WalkSynchMoveReqMessage req) {
		try {
			RoleInstance currentRole = this.getCurrentRole(context);
			if(null == currentRole){
				return null ;
			}
			//中断剧情模式
			currentRole.setDramaState(false);
			
			if(currentRole.getJumpMap().get()){
				return null;
			}
			if(!currentRole.getBehavior().canMove()){
				return null ;
			}
			MapInstance mapInstance = currentRole.getMapInstance();
			if(null == mapInstance){
				//切换地图的时候有可能会为空
				return null ;
			}
			
			if(currentRole.isDeath()) {
				//死亡以后行走不处理
				return null;
			}
			
			mapInstance.move(currentRole, new Point(currentRole.getMapId(), req
					.getMapx(), req.getMapy()), req.getDir());
			
			C0200_WalkSynchMoveRespMessage respMsg = new C0200_WalkSynchMoveRespMessage();
			respMsg.setRoleId(currentRole.getIntRoleId());
			respMsg.setMapx(req.getMapx());
			respMsg.setMapy(req.getMapy());
			respMsg.setDir(req.getDir());
			respMsg.setSpeed(req.getSpeed());
			currentRole.getBehavior().notifyPosition(respMsg);
			return null;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

	
}
