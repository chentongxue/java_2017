package sacred.alliance.magic.debug.action;

import java.util.ArrayList;
import java.util.List;

import sacred.alliance.magic.core.Message;
import sacred.alliance.magic.core.action.ActionContext;
import sacred.alliance.magic.core.action.ActionSupport;
import sacred.alliance.magic.util.Util;

import com.game.draco.GameContext;
import com.game.draco.app.mail.vo.MailMoneyRank;
import com.game.draco.debug.message.item.MailMoneyRankItem;
import com.game.draco.debug.message.request.C10038_MailMoneyRankReqMessage;
import com.game.draco.debug.message.response.C10038_MailMoneyRankRespMessage;

public class MailMoneyRankAction extends ActionSupport<C10038_MailMoneyRankReqMessage>{
	
	@Override
	public Message execute(ActionContext arg0, C10038_MailMoneyRankReqMessage reqMsg) {
		C10038_MailMoneyRankRespMessage resp = new C10038_MailMoneyRankRespMessage();
		try{
			List<MailMoneyRank> list = GameContext.getMailDAO().getMoneyRankList(reqMsg.getSendSource(), reqMsg.getMinMoney(), reqMsg.getStart(), reqMsg.getSize());
			if(Util.isEmpty(list)){
				return resp;
			}
			List<MailMoneyRankItem> moneyRankList = new ArrayList<MailMoneyRankItem>();
			for(MailMoneyRank rank : list){
				if(null == rank){
					continue;
				}
				MailMoneyRankItem item = new MailMoneyRankItem();
				item.setRoleId(rank.getRoleId());
				item.setRoleName(rank.getRoleName());
				item.setLevel(rank.getLevel());
				item.setNumber(rank.getNumber());
				item.setTotalMoney(rank.getTotalMoney());
				moneyRankList.add(item);
			}
			resp.setMoneyRankList(moneyRankList);
			return resp;
		}catch(Exception e){
			this.logger.error(this.getClass().getName() + ".execute error: ", e);
			return resp;
		}
	}
	
}
