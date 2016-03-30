package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.RoleMoneyChartsItem;
import com.game.draco.debug.message.request.C10045_RoleMoneyChartsReqMessage;
import com.game.draco.debug.message.response.C10045_RoleMoneyChartsRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;
import sacred.alliance.magic.vo.RoleInstance;

public class RoleMoneyChartsAction extends ActionSupport<C10045_RoleMoneyChartsReqMessage>{

	@Override
	public Message execute(ActionContext arg0, C10045_RoleMoneyChartsReqMessage req) {
		C10045_RoleMoneyChartsRespMessage resp = new C10045_RoleMoneyChartsRespMessage();
		int start = req.getStart() < 0 ? 0 : req.getStart();
		int end = req.getEnd() <= 0 ? 50 : req.getEnd();
		int orderby = req.getOrderby() <= 0 ? 1 : req.getOrderby();
		List<RoleInstance> list = GameContext.getUserRoleApp().getRoleMoneyCharts(orderby, start, end);
		if(Util.isEmpty(list)){
			return resp;
		}
		List<RoleMoneyChartsItem> items = new ArrayList<RoleMoneyChartsItem>();
		for(RoleInstance role : list){
			RoleMoneyChartsItem item = new RoleMoneyChartsItem();
			item.setUserId(role.getUserId());
			item.setUserName(role.getUserName());
			item.setRoleId(role.getIntRoleId());
			item.setRoleName(role.getRoleName());
			item.setResidueGoldMoney(role.getRoleConsumeGold());
			//item.setBindingGoldMoney(role.getBindingGoldMoney());
			//item.setResidueBindingMoney(role.getConsumeBindMoney());
			item.setSilverMoney(role.getSilverMoney());
			item.setFrozenEndTime(role.getFrozenEndTime());
			items.add(item);
		}
		resp.setItems(items);
		return resp;
	}
	
}
