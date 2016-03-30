package sacred.alliance.magic.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.app.buff.Buff;
import com.game.draco.app.buff.stat.BuffStat;
import com.game.draco.message.item.RoleBuffShowItem;
import com.game.draco.message.request.C1106_RoleBuffListReqMessage;
import com.game.draco.message.response.C1106_RoleBuffListRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.vo.AbstractRole;
import sacred.alliance.magic.vo.RoleInstance;

public class RoleBuffListAction extends BaseAction<C1106_RoleBuffListReqMessage> {

	@Override
	public Message execute(ActionContext context, C1106_RoleBuffListReqMessage reqMsg) {
		int roleId = reqMsg.getRoleId();
		AbstractRole role = null;
		if(roleId > 0){
			role = GameContext.getOnlineCenter().getRoleInstanceByRoleId(String.valueOf(roleId));
		} else {
			RoleInstance currRole = this.getCurrentRole(context);
			role = currRole.getMapInstance().getNpcInstance(String.valueOf(roleId));
		}
		C1106_RoleBuffListRespMessage respMsg = new C1106_RoleBuffListRespMessage();
		if(null == role){
			return respMsg;
		}
		List<RoleBuffShowItem> buffList = new ArrayList<RoleBuffShowItem>();
		for(BuffStat stat : role.getReceiveBuffCopy()){
			if(null == stat){
				continue;
			}
			short buffId = stat.getBuffId();
			Buff buff = GameContext.getBuffApp().getBuff(buffId);
			if(null == buff){
				continue;
			}
			RoleBuffShowItem item = new RoleBuffShowItem();
			item.setBuffLayer(stat.getLayer());
			item.setBuffName(buff.getBuffName());
			item.setIconId(buff.getIconId());
			item.setRemainTime(stat.getRemainTime());
			item.setDesc(buff.getBuffDesc(stat.getBuffLevel()));
			buffList.add(item);
		}
		respMsg.setBuffList(buffList);
		return respMsg;
	}
	
}
