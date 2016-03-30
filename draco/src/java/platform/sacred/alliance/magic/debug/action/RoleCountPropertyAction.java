package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import sacred.alliance.magic.app.count.type.CountType;
import sacred.alliance.magic.app.count.vo.CountRecord;
import sacred.alliance.magic.constant.RespTypeStatus;
import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.RoleCount;
import sacred.alliance.magic.util.DateUtil;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.RoleCountPropertyItem;
import com.game.draco.debug.message.request.C10085_RoleCountDetailReqMessage;
import com.game.draco.debug.message.response.C10085_RoleCountDetailRespMessage;
/**
 * 查看玩家roleCount
 */
public class RoleCountPropertyAction extends ActionSupport<C10085_RoleCountDetailReqMessage>{
	
	@Override
	public Message execute(ActionContext context, C10085_RoleCountDetailReqMessage req) {
		String roleId = req.getRoleId();
		RoleInstance role = null;
		C10085_RoleCountDetailRespMessage resp = new C10085_RoleCountDetailRespMessage();
		try {
			if(Util.isEmpty(roleId)){
				String roleName = req.getRoleName();
				role = GameContext.getUserRoleApp().getRoleByRoleName(roleName);
				if(null != role){
					roleId = role.getRoleId();
				}
			}
			if(Util.isEmpty(roleId)){
				//提示玩家不存在
				resp.setType((byte)RespTypeStatus.FAILURE);
				resp.setInfo("role not exist");
				return resp ;
			}
			role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(roleId);
			RoleCount rc = null;
			if(role != null){
				rc = role.getRoleCount();
			}
			if(rc == null){
				rc = GameContext.getBaseDAO().selectEntity(RoleCount.class, RoleCount.ROLE_ID, roleId);
				if(null == rc){
					//提示记录不存在
					resp.setType((byte)RespTypeStatus.FAILURE);
					resp.setInfo("rolecount not exist");
					return resp ;
				}
				rc.parseDataBase();
			}
			Map<Integer, CountRecord> timesMap = rc.getTimesMap();
			List<RoleCountPropertyItem> propertyItemList = new ArrayList<RoleCountPropertyItem>();
			for (CountRecord cr : timesMap.values()) {
				RoleCountPropertyItem it = new RoleCountPropertyItem();
				it.setDesc(CountType.get(cr.getId()).getDesc());
				it.setEnumName(CountType.get(cr.getId()).toString());
				it.setId(cr.getId());
				switch (CountType.get(cr.getId())) {
				case LuckyBoxLastOpenTime:
				case ChallengeTime:
					it.setValue(DateUtil.date2FormatDate(Long.parseLong(cr.getV()), DateUtil.format3));
					break;
				default:
					it.setValue(cr.getV());
				}
				propertyItemList.add(it);
			}
			resp.setPropertyItemList(propertyItemList);
			resp.setFlowerNum(rc.getFlowerNum());
			resp.setTodayFlowerNum(rc.getTodayFlowerNum());
			resp.setDayTime(rc.getDayTime());
			resp.setRoleId(roleId);
			resp.setRoleName(role.getRoleName());
			resp.setType((byte)RespTypeStatus.SUCCESS);
			return resp;
		} catch (Exception e) {
			this.logger.error("RoleCountPropertyAction err: ", e);
			resp.setType((byte)RespTypeStatus.FAILURE);
			return resp;
		}
	}

}
