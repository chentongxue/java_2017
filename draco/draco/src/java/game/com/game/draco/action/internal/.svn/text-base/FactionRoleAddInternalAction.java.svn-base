//package com.game.draco.action.internal;
//
//import sacred.alliance.magic.action.BaseAction;
//import sacred.alliance.magic.base.FactionRecordType;
//import sacred.alliance.magic.base.Result;
//import sacred.alliance.magic.constant.Status;
//import sacred.alliance.magic.constant.TextId;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.domain.Faction;
//import sacred.alliance.magic.domain.FactionRecord;
//import sacred.alliance.magic.domain.FactionRole;
//import sacred.alliance.magic.vo.RoleInstance;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.internal.C0072_FactionRoleAddInternalMessage;
//import com.game.draco.message.push.C0003_TipNotifyMessage;
//
//public class FactionRoleAddInternalAction extends BaseAction<C0072_FactionRoleAddInternalMessage> {
//
//	@Override
//	public Message execute(ActionContext context,C0072_FactionRoleAddInternalMessage reqMsg) {
//		Faction faction = reqMsg.getFaction();
//		FactionRole fr = reqMsg.getFactionRole();
//		String roleId = reqMsg.getRoleId();
//		RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
//		String operaRoleId = reqMsg.getOperaRoleId();
//		if(null == role){
//			role = GameContext.getBaseDAO().selectEntity(RoleInstance.class, "roleId", roleId);
//		}
//		if(null == role) {
//			return null;
//		}
//		if(role.hasUnion()){
//			sendMsg(operaRoleId, Status.Faction_Role_Own.getTips());
//			return null;
//		}
//		
//		if(role.getCampId() != faction.getFactionCamp()) {
//			sendMsg(operaRoleId, this.getText(TextId.Faction_Camp_Change));
//			return null;
//		}
//		
//		try{
//			Result result = GameContext.getFactionApp().addFactionRole(role, faction, fr);
//			if(!result.isSuccess()) {
//				sendMsg(operaRoleId, result.getInfo());
//				return null;
//			}
//			
//			//加入公会记录
//			FactionRecord factionRecord = new FactionRecord();
//			factionRecord.setType(FactionRecordType.Faction_Record_Role_Join.getType());
//			factionRecord.setFactionId(faction.getFactionId());
//			factionRecord.setData2(role.getRoleName());
//			GameContext.getFactionFuncApp().createFactionRecord(factionRecord);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	private void sendMsg(String roleId,String msg){
//		//在线则发浮动提示
//		GameContext.getMessageCenter().sendByRoleId(null, roleId, new C0003_TipNotifyMessage(msg));
//	}
//}
