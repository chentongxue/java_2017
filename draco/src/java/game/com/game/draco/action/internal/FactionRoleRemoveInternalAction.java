//package com.game.draco.action.internal;
//
//import java.util.Date;
//
//import com.game.draco.GameContext;
//import com.game.draco.message.internal.C0073_UnionRoleRemoveInternalMessage;
//
//import sacred.alliance.magic.action.BaseAction;
//import sacred.alliance.magic.core.Message;
//import sacred.alliance.magic.core.action.ActionContext;
//import sacred.alliance.magic.vo.RoleInstance;
//
//public class FactionRoleRemoveInternalAction extends BaseAction<C0073_UnionRoleRemoveInternalMessage> {
//
//	@Override
//	public Message execute(ActionContext context,C0073_UnionRoleRemoveInternalMessage reqMsg) {
//		String roleId = reqMsg.getRoleId();
//		try{
//			RoleInstance role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
//			boolean isOnline = false;
//			if(null == role){
//				role = GameContext.getBaseDAO().selectEntity(RoleInstance.class, "roleId", roleId);
//			}else{
//				isOnline = true;
//			}
//			if(null == role) {
//				return null;
//			}
//			role.setUnionId("");//先不用更新角色数据库
//			//如果角色没有在线则库中role对象上存在公会ID
//			//只更新ROLE对象的公会字段
//			role.setLeaveFactionTime(new Date());
//			if(!isOnline){
//				GameContext.getRoleDAO().updateRoleLeaveFactionTime(role);
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//}
