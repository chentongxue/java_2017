package sacred.alliance.magic.debug.action;

import com.game.draco.GameContext;
import com.game.draco.debug.message.request.C10006_RoleAttributeChangeReqMessage;
import com.game.draco.debug.message.response.C10000_StateRespMessage;

import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.base.OperatorType;
import sacred.alliance.magic.base.OutputConsumeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.vo.RoleInstance;

/**
 * 修改角色属性
 */
public class RoleAttributeChangeAction extends ActionSupport<C10006_RoleAttributeChangeReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10006_RoleAttributeChangeReqMessage reqMsg) {
		C10000_StateRespMessage resp = new C10000_StateRespMessage();
		resp.setType((byte)RespTypeStatus.FAILURE);
		try{
			String roleName = reqMsg.getRoleName();
			int attType = reqMsg.getAttributeType();
			int value = reqMsg.getValue();
			if(0 == value){
				resp.setType((byte)RespTypeStatus.SUCCESS);
				return resp;
			}
			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleName(roleName);
			if(null == role){
				resp.setInfo(GameContext.getI18n().getText(TextId.ROLE_OFFLINE_FAIL));
				return resp;
			}
			resp.setType((byte)RespTypeStatus.SUCCESS);
			AttributeType attributeType = AttributeType.get((byte) attType);
			OperatorType operType = OperatorType.get(reqMsg.getOperType());
			
			if(attributeType.isMoney()){
				OutputConsumeType outputConsumeType = OutputConsumeType.gm_output;
				if(OperatorType.Decrease == operType){
					outputConsumeType = OutputConsumeType.gm_consume;
				}
				GameContext.getUserAttributeApp().changeRoleMoney(role, attributeType, operType, value, outputConsumeType);
			}else{
				role.getBehavior().changeAttribute(attributeType, operType, value);
			}
			role.getBehavior().notifyAttribute();
			if(AttributeType.level == attributeType){
				GameContext.getUserAttributeApp().reCalctAfterChangeLevel(role, operType, value);
				role.roleLevelUp(value);
			}
			return resp;
		}catch(Exception e){
			logger.error("RoleAttributeChangeAction error: ",e);
			resp.setInfo(GameContext.getI18n().getText(TextId.SYSTEM_ERROR));
			return resp;
		}
	}
}
