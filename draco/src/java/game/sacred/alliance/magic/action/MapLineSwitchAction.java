package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.message.request.C0255_MapLineSwitchReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;

import sacred.alliance.magic.app.map.data.MapConfig;
import sacred.alliance.magic.base.ChangeMapResult;
import sacred.alliance.magic.base.SwitchLineType;
import sacred.alliance.magic.constant.Status;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.exception.ServiceException;
import sacred.alliance.magic.vo.LinePoint;
import sacred.alliance.magic.vo.MapInstance;
import sacred.alliance.magic.vo.MapLineContainer;
import sacred.alliance.magic.vo.RoleInstance;

public class MapLineSwitchAction extends BaseAction<C0255_MapLineSwitchReqMessage>{

	@Override
	public Message execute(ActionContext context, C0255_MapLineSwitchReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		int toLine = reqMsg.getLineId();
		if(toLine <=0){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Sys_Param_Error.getTips());
		}
		MapInstance mapInstance = role.getMapInstance();
		if(toLine == role.getLineId() || null == mapInstance){
			return null ;
		}
		if(!this.isChangeLine(role.getMapId(), toLine)){
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Map_Line_Role_Full.getTips());
		}
		role.setLineId(reqMsg.getLineId());
		LinePoint point = new LinePoint(role.getMapId(),role.getMapX(),role.getMapY(),toLine);
		//切线
		try {
			role.setSwitchLineType(SwitchLineType.Manual);
			ChangeMapResult value = GameContext.getUserMapApp().changeMap(role, point);
			if(!value.isSuccess()){
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Map_Change_Line_Fail.getTips());
			}
		} catch (ServiceException e) {
			logger.error("",e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(),Status.Map_Change_Line_Fail.getTips());
		}
		return null;
	}

	//允许切线
	private boolean isChangeLine(String targetMapId,int lineId){
		MapLineContainer container = GameContext.getMapApp().getMapLineContainer(targetMapId);
		if(null == container){
			return false;
		}
		MapInstance targetMapInstance = container.getMapInstance(lineId);
		if(null == targetMapInstance){
			return false;
		}
		MapConfig mc = targetMapInstance.getMap().getMapConfig();
		if(mc.getChangeLine() == 0 && targetMapInstance.isMapRoleFull()){
			return false;
		}
		return true;
	}

}
