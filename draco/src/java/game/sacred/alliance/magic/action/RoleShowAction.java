package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import sacred.alliance.magic.base.AttributeType;
import sacred.alliance.magic.constant.TextId;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.app.asyncpvp.domain.AsyncPvpRoleAttr;
import com.game.draco.message.item.AttriTypeValueItem;
import com.game.draco.message.push.C0003_TipNotifyMessage;
import com.game.draco.message.request.C1102_RoleShowReqMessage;
import com.game.draco.message.response.C0002_ErrorRespMessage;
import com.game.draco.message.response.C1102_RoleShowRespMessage;

public class RoleShowAction extends BaseAction<C1102_RoleShowReqMessage> {
	private List<AttributeType> list = Lists.newArrayList(
			AttributeType.atk, AttributeType.maxHP,
			AttributeType.rit, AttributeType.breakDefense,
			AttributeType.critAtk, AttributeType.critRit,
			AttributeType.dodge, AttributeType.hit);

	@Override
	public Message execute(ActionContext context, C1102_RoleShowReqMessage reqMsg) {
		RoleInstance role = this.getCurrentRole(context);
		if(null == role){
			return null ;
		}
		try {
			// 获得目标角色
			String reqRoleId = String.valueOf(reqMsg.getRoleId());
			/*if (role.getRoleId().equals(reqMsg.getRoleId())) {
				return new C0002_ErrorRespMessage(reqMsg.getCommandId(), Status.Role_Targ_Is_Self.getTips());
			}*/
			RoleInstance target = GameContext.getOnlineCenter().getRoleInstanceByRoleId(reqRoleId);
			if (target == null) {				
				return offlineRoleShow(reqRoleId);
			}
			return onlineRoleShow(target);
		} catch (Exception e) {
			logger.error("", e);
			return new C0002_ErrorRespMessage(reqMsg.getCommandId(), this.getText(TextId.SYSTEM_ERROR));
		}
	}
	
	private C1102_RoleShowRespMessage onlineRoleShow(RoleInstance target) {
		C1102_RoleShowRespMessage respMsg = new C1102_RoleShowRespMessage();
		//角色Id
		respMsg.setRoleId(target.getIntRoleId());
		//角色名称
		respMsg.setRoleName(target.getRoleName());
		//阵营
		respMsg.setCamp(target.getCampId());
		//角色等级
		respMsg.setLevel((byte)target.getLevel());
		//VIP等级
		respMsg.setVipLevel(GameContext.getVipApp().getVipLevel(target));
		//战斗力
		respMsg.setBattleScore(target.getBattleScore());
		//外形
		//RoleShape info = GameContext.getUserRoleApp().getRoleShape(target.getRoleId());
		//respMsg.setEquipResId((short) info.getEquipResId());
		//respMsg.setClothesResId((short) info.getClothesResId());
		//respMsg.setWingResId((short) info.getWingResId());
		respMsg.setHeadId(GameContext.getHeroApp().getRoleHeroHeadId(target.getRoleId()));
		
		//勋章
		respMsg.setMedalList(GameContext.getMedalApp().getMedalList(target.getRoleId()));
		
		//属性
		List<AttriTypeValueItem> showAttrItemes = new ArrayList<AttriTypeValueItem>();
		for (AttributeType attriType : list) {
			AttriTypeValueItem item = new AttriTypeValueItem();
			item.setAttriType(attriType.getType());
			item.setAttriValue(target.get(attriType.getType()));
			showAttrItemes.add(item);
		}
		respMsg.setShowAttrItemes(showAttrItemes);
		return respMsg;
	}
	
	private Message offlineRoleShow(String reqRoleId) {
		AsyncPvpRoleAttr asyRole = GameContext.getAsyncPvpApp().getAsyncPvpRoleAttr(reqRoleId);
		if(null == asyRole){
			//提示当前用户不在线
			return new C0003_TipNotifyMessage(this.getText(TextId.ROLE_NOT_ONLINE));
		}
		C1102_RoleShowRespMessage respMsg = new C1102_RoleShowRespMessage();
		respMsg.setRoleId(Integer.parseInt(asyRole.getRoleId()));
		respMsg.setRoleName(asyRole.getRoleName());
		respMsg.setCamp(asyRole.getCamp());
		respMsg.setLevel((byte)asyRole.getLevel());
		respMsg.setVipLevel(asyRole.getVipLevel());
		respMsg.setBattleScore(asyRole.getBattleScore());
		//RoleShape info = GameContext.getUserRoleApp().getRoleShape(asyRole.getRoleId());
		//respMsg.setEquipResId((short) info.getEquipResId());
		//respMsg.setClothesResId((short) info.getClothesResId());
		//respMsg.setWingResId((short) info.getWingResId());
		respMsg.setHeadId(GameContext.getHeroApp().getHeroHeadId(asyRole.getHeroId()));
		//获得勋章
		respMsg.setMedalList(GameContext.getMedalApp().getMedalList(asyRole.getRoleId()));
		
		List<AttriTypeValueItem> showAttrItemes = new ArrayList<AttriTypeValueItem>();
		for (AttributeType attriType : list) {
			AttriTypeValueItem item = new AttriTypeValueItem();
			item.setAttriType(attriType.getType());
			item.setAttriValue(asyRole.getAttriValue(attriType.getType()));
			showAttrItemes.add(item);
		}
		respMsg.setShowAttrItemes(showAttrItemes);
		return respMsg;
	}
}
