package sacred.alliance.magic.action;

import com.game.draco.GameContext;
import com.game.draco.base.ExecutorBean;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C0249_MapTransferToRoleReqMessage;
import com.game.draco.message.request.C0252_MapTransferReqMessage;
import com.game.draco.message.response.C0249_MapTransferToRoleRespMessage;
import sacred.alliance.magic.base.ChangeMapEvent;
import sacred.alliance.magic.base.Result;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.executor.annotation.ExecutorMapping;
import sacred.alliance.magic.vo.Point;
import sacred.alliance.magic.vo.RoleInstance;

@ExecutorMapping(name= ExecutorBean.userOrderedExecutor)
public class MapTransferToRoleAction extends BaseAction<C0249_MapTransferToRoleReqMessage> {

	@Override
	public Message execute(ActionContext context, C0249_MapTransferToRoleReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null;
		}
		C0249_MapTransferToRoleRespMessage respMsg = new C0249_MapTransferToRoleRespMessage();
		String roleId = reqMsg.getTargetRoleId() + "" ;
		if(roleId.equals(role.getRoleId())){
			//目标是自己
			respMsg.setInfo(this.getText(TextId.Role_Targ_Is_Self));
			return respMsg ;
		}
		RoleInstance targetRole = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
		if(null == targetRole){
			//提示目标没在线
			respMsg.setInfo(this.getText(TextId.Role_Targ_Offline));
			return respMsg ;
		}
		Result result = GameContext.getWorldMapApp().transfer(role,
				new Point(targetRole.getMapId(),
						targetRole.getMapX(),
						targetRole.getMapY(),
						ChangeMapEvent.worldmap.getEventType()),
				GameContext.getParasConfig().getWorldMapGoldCost());
		if(null == result || result.isIgnore()){
			return null ;
		}
		if(result.isSuccess()){
			respMsg.setStatus(RespTypeStatus.SUCCESS);
		}
		respMsg.setInfo(result.getInfo());
		return respMsg ;
	}

}
