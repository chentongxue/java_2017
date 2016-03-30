package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import com.game.draco.GameContext;
import com.game.draco.debug.message.item.UserMoneyRankItem;
import com.game.draco.debug.message.request.C10056_UserMoneyRankReqMessage;
import com.game.draco.debug.message.response.C10056_UserMoneyRankRespMessage;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.domain.RolePayRecord;
import sacred.alliance.magic.util.Util;

public class UserMoneyRankAction extends ActionSupport<C10056_UserMoneyRankReqMessage>{

	@Override
	public Message execute(ActionContext arg0, C10056_UserMoneyRankReqMessage req) {
		C10056_UserMoneyRankRespMessage resp = new C10056_UserMoneyRankRespMessage();
		int start = req.getStart() < 0 ? 0 : req.getStart();
		int end = req.getEnd() <= 0 ? 50 : req.getEnd();
		int orderby = req.getOrderby() <= 0 ? 1 : req.getOrderby();
		List<RolePayRecord> list = GameContext.getUserRoleApp().getUserMoneyRankList(orderby, start, end);
		if(Util.isEmpty(list)){
			return resp;
		}
		List<UserMoneyRankItem> userMoneyRankList = new ArrayList<UserMoneyRankItem>();
		for(RolePayRecord record : list){
			UserMoneyRankItem item = new UserMoneyRankItem();
			item.setRoleId(record.getRoleId());
			item.setCurrMoney(record.getCurrMoney());
			item.setTotalMoney(record.getTotalMoney());
			item.setConsumeMoney(record.getConsumeMoney());
			item.setPayGold(record.getPayGold());
			userMoneyRankList.add(item);
		}
		resp.setUserMoneyRankList(userMoneyRankList);
		return resp;
	}
	
}
