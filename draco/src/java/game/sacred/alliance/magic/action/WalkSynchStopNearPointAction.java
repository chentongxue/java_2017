package sacred.alliance.magic.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.draco.message.request.C0218_WalkSynchStopNearPointReqMessage;
import com.game.draco.message.response.C0213_WalkSynchStopRespMessage;

import sacred.alliance.magic.base.Direction;
import sacred.alliance.magic.base.StateType;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public class WalkSynchStopNearPointAction extends BaseAction<C0218_WalkSynchStopNearPointReqMessage> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public Message execute(ActionContext context, C0218_WalkSynchStopNearPointReqMessage req) {
		try {
			RoleInstance role = this.getCurrentRole(context);
			if(null == role) {
				return null;
			}
			if(role.getJumpMap().get()){
				return null;
			}
			if(role.inState(StateType.fixed) || role.inState(StateType.coma)
					 || role.inState(StateType.charm) || role.inState(StateType.noMove)){
				return null;
			}
			MapInstance mapInstance = role.getMapInstance();
			if(null == mapInstance){
				//切换地图的时候有可能会为空
				return  null ;
			}
			//行走消息中的地图ID，不是角色当前所在地图
			if(!req.getMapId().equals(mapInstance.getMap().getMapId())){
				return null;
			}
			mapInstance.move(role, new Point(role.getMapId(), req
					.getMapx(), req.getMapy()), req.getDir());
			
			if(null == role.getMapInstance()){
				//切换地图的时候有可能会为空
				return  null ;
			}
			if(role.isDeath()) {
				//死亡以后行走不处理
				return null;
			}
			C0213_WalkSynchStopRespMessage resp = new C0213_WalkSynchStopRespMessage();
			resp.setRoleId(role.getIntRoleId());
			resp.setMapx(req.getMapx());
			resp.setMapy(req.getMapy());
			resp.setDir(req.getDir());
			resp.setSpeed(req.getSpeed());

			role.getBehavior().notifyPosition(resp);
			//mapInstance.broadcastMap(this.getCurrentRole(context), resp, 2000);
			return null;
		} catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}

}
