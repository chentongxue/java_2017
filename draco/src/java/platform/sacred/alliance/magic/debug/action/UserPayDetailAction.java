package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.UserPayItem;
import com.game.draco.debug.message.request.C10033_UserPayDetailReqMessage;
import com.game.draco.debug.message.response.C10033_UserPayDetailRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.RolePayRecord;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class UserPayDetailAction extends ActionSupport<C10033_UserPayDetailReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10033_UserPayDetailReqMessage reqMsg) {
		C10033_UserPayDetailRespMessage resp = new C10033_UserPayDetailRespMessage();
		try {
			List<RoleInstance> roleList = GameContext.getUserRoleApp().getUserRoles(reqMsg.getUserName(), reqMsg.getUserId(), reqMsg.getChanneluserId());
			if(Util.isEmpty(roleList)){
				return resp;
			}
			RoleInstance instance = roleList.get(0);
			String userId = instance.getUserId();
			RoleInstance onlineRole = GameContext.getOnlineCenter().getRoleInstanceByUserId(userId);
			RolePayRecord record = null;
			String onlineRoleId = null;
			if(null != onlineRole){
				record = onlineRole.getRolePayRecord();
				onlineRoleId = onlineRole.getRoleId();
			}else{
				record = GameContext.getBaseDAO().selectEntity(RolePayRecord.class, "userId", userId);
			}
			resp.setUserId(userId);
			resp.setUserName(instance.getUserName());
			resp.setChannelId(instance.getChannelId());
			resp.setChannelUserId(instance.getChannelUserId());
			resp.setCurrMoney(record.getCurrMoney());
			resp.setTotalMoney(record.getTotalMoney());
			resp.setConsumeMoney(record.getConsumeMoney());
			resp.setPayGold(record.getPayGold());
			List<UserPayItem> userPayList = new ArrayList<UserPayItem>();
			for(RoleInstance role : roleList){
				if(null == role){
					continue;
				}
				UserPayItem item = new UserPayItem();
				String roleId = role.getRoleId();
				item.setCareer(role.getCareer());
				if(null != onlineRoleId && roleId.equals(onlineRoleId)){
					item.setRoleId(roleId);
					item.setRoleName(onlineRole.getRoleName());
					item.setOnline((byte) 1);
					item.setLevel((byte) onlineRole.getLevel());
					item.setExp(onlineRole.getExp());
					//item.setBindMoney(onlineRole.getBindingGoldMoney());
					//item.setConsumeBindMoney(onlineRole.getConsumeBindMoney());
					item.setSilverMoney(onlineRole.getSilverMoney());
				}else{
					item.setRoleId(roleId);
					item.setRoleName(role.getRoleName());
					item.setOnline((byte) 0);
					item.setLevel((byte) role.getLevel());
					item.setExp(role.getExp());
					//item.setBindMoney(role.getBindingGoldMoney());
					//item.setConsumeBindMoney(role.getConsumeBindMoney());
					item.setSilverMoney(role.getSilverMoney());
				}
				userPayList.add(item);
			}
			resp.setUserPayList(userPayList);
			return resp;
		} catch (RuntimeException e) {
			this.logger.error("UserPayDetailAction error: ", e);
			return resp;
		}
	}
}
