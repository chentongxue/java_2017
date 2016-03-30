package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10025_ExitMapReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.base.ChangeMapResult;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

public class ExitMapAction extends ActionSupport<C10025_ExitMapReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10025_ExitMapReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			String info = reqMsg.getInfo();
			RoleInstance role = null;
			if(reqMsg.getInfoType() == 0){
				role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(info);
			}else if(reqMsg.getInfoType() == 1){
				role = GameContext.getOnlineCenter().getRoleInstanceByRoleName(info);
			}
			if(role == null){
				resp.setInfo(GameContext.getI18n().getText(TextId.ROLE_OFFLINE_FAIL));
				return resp;
			}
			String mapId = role.getMapInstance().getMap().getMapId();//地图ID
			Point targetPoint = GameContext.getRoleRebornApp().getRebornPointDetail(mapId,role).createPoint();
			ChangeMapResult changeMapResult = GameContext.getUserMapApp().changeMap(role, targetPoint);
			if(!changeMapResult.isSuccess()){
				resp.setInfo(changeMapResult.getDesc());
				return resp;
			}
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		}catch(Exception e){
			logger.error("ExitMapAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}

}
